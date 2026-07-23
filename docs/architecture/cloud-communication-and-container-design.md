# 云端通信与容器结构设计书

本文重点解释三件事：

1. 网络设备、AWS、OCI 与浏览器之间如何通信。
2. 流量进入 OCI 后如何到达 OKE 中的不同容器。
3. 每种协议使用什么端口、负载均衡器和安全边界，以及失败时从哪里排查。

本文中的图分为三个实现级别：

| 标记 | 含义 |
|---|---|
| 实线 | 当前代码、Docker Compose 或 Kubernetes YAML 已有的路径 |
| 虚线 | OpenTofu 默认关闭的云资源样例，启用前不会创建或收费 |
| “生产补充” | 设计目标，但当前 IaC 尚未完整生成 listener/backend/certificate/VPN 等资源 |

## 1. 系统通信总览

被动监控由设备主动把 Syslog/Trap 发到 OCI；主动监控由 AWS Lambda 定时访问设备，再把结果通过 HTTPS 交给 OCI。浏览器只访问 Web/HTTPS，不直接访问接收器或数据库。

```mermaid
flowchart LR
  subgraph CUSTOMER["客户网络"]
    ROUTER["路由器 / VPN / 交换机"]
    SERVICE["被监视 TCP 服务"]
  end

  subgraph AWS["AWS 主动监控平面"]
    EVENTBRIDGE["EventBridge"]
    GETLAMBDA["SNMP GET Lambda"]
    TCPLAMBDA["TCP Ping Lambda"]
  end

  subgraph OCI["OCI 主运行平面"]
    NLB["OCI NLB\n四层 UDP/TCP"]
    WEBLB["OCI LB / Ingress\n七层 HTTPS"]
    OKE["OKE 工作负载"]
    DB[("PostgreSQL / ADB")]
    NOTIFY["通知 / Functions"]
  end

  BROWSER["运维浏览器"]

  ROUTER -->|"Syslog UDP/TCP 514"| NLB
  ROUTER -->|"SNMP Trap UDP 162"| NLB
  EVENTBRIDGE --> GETLAMBDA
  EVENTBRIDGE --> TCPLAMBDA
  GETLAMBDA -->|"SNMP GET UDP 161"| ROUTER
  TCPLAMBDA -->|"TCP connect 目标端口"| SERVICE
  GETLAMBDA -->|"HTTPS JSON 443"| WEBLB
  TCPLAMBDA -->|"HTTPS JSON 443"| WEBLB
  BROWSER -->|"HTTPS 443"| WEBLB
  NLB --> OKE
  WEBLB --> OKE
  OKE -->|"JDBC 1522 或 5432"| DB
  OKE --> NOTIFY
```

## 2. 云与网络区域划分

公网子网只放负载均衡入口。OKE 节点、ADB、内部服务放私有子网。Bastion 提供短时运维入口，不给节点长期开放公网 SSH。

```mermaid
flowchart TB
  INTERNET["Internet / 客户 WAN"]

  subgraph AWSVPC["AWS VPC 10.10.0.0/16"]
    subgraph AWSPRIV["AWS 私有子网"]
      LAMBDA["监控 Lambda"]
    end
    AWSNAT["NAT Gateway\n公网 HTTPS 备用"]
    AWSSG["Lambda Security Group\n以 Egress 为主"]
  end

  subgraph OCIVCN["OCI VCN 10.20.0.0/16"]
    subgraph OCIPUB["OCI 公网子网"]
      NLB2["Protocol NLB"]
      LB2["Web HTTPS LB"]
    end
    subgraph OCIPRIV["OCI 私有子网"]
      NODES["OKE Node Pool"]
      ADB["ADB Private Endpoint"]
      BASTION["OCI Bastion"]
    end
    IGW["Internet Gateway"]
    NAT["NAT Gateway"]
    DRG["DRG / VPN Attachment\n生产补充"]
  end

  INTERNET --> IGW
  IGW --> NLB2
  IGW --> LB2
  LAMBDA -. "Site-to-Site VPN" .-> DRG
  LAMBDA --> AWSNAT -->|"HTTPS 443 备用路径"| LB2
  NLB2 --> NODES
  LB2 --> NODES
  NODES --> ADB
  NODES --> NAT --> INTERNET
  BASTION -. "临时受控会话" .-> NODES
  AWSSG --> LAMBDA
```

### 设计约束

- AWS `10.10.0.0/16` 与 OCI `10.20.0.0/16` 不得重叠。
- 私有子网默认不能直接接收入站公网连接。
- VPN 路径必须同时配置去程路由、回程路由、AWS SG/NACL、OCI NSG/Security List。
- 没有 VPN 时，只允许 Lambda 经 NAT 调用 OCI 的 HTTPS API；SNMP v2c 不穿越公网。

## 3. OKE 容器与 Service 拓扑

Kubernetes 中使用同一个不可变镜像创建五个逻辑 Deployment。Web 有 ClusterIP Service；Syslog 与 Trap 各有四层 LoadBalancer Service；worker 没有业务入口。

```mermaid
flowchart TB
  HTTPS["Ingress / HTTPS LB"]
  PROTONLB["OCI NLB"]

  subgraph NS["Namespace: bms-monitoring"]
    WEBSVC["Service bms-web-app\nTCP 80 -> 8080"]
    SYSLOGSVC["Service syslog-receiver\nUDP/TCP 5514"]
    TRAPSVC["Service snmp-trap-receiver\nUDP 1162"]

    subgraph WEBDEPLOY["Deployment bms-web-app"]
      WEB1["Web Pod 1\nSpring MVC/API"]
      WEB2["Web Pod 2\nSpring MVC/API"]
    end

    SYSLOGPOD["Syslog Receiver Pod\nUDP/TCP socket"]
    TRAPPOD["SNMP Trap Receiver Pod\nSNMP4J UDP socket"]
    MONITORPOD["Monitoring Worker Pod\n主动检查逻辑"]
    ALERTPOD["Alert Check Worker Pod\nScheduler 启用"]
    PG[("PostgreSQL Service\nTCP 5432")]
    MAIL["MailHog Service\nSMTP 1025"]
  end

  HTTPS --> WEBSVC
  WEBSVC --> WEB1
  WEBSVC --> WEB2
  PROTONLB --> SYSLOGSVC --> SYSLOGPOD
  PROTONLB --> TRAPSVC --> TRAPPOD
  WEB1 --> PG
  WEB2 --> PG
  SYSLOGPOD --> PG
  TRAPPOD --> PG
  MONITORPOD --> PG
  ALERTPOD --> PG
  ALERTPOD --> MAIL
```

## 4. 一个镜像如何承担五种容器角色

当前设计不是五套不同 JAR，而是同一镜像通过环境变量启停协议接收器和告警调度器。这样先实现独立扩缩和故障隔离，未来再按需要拆成独立制品。

```mermaid
flowchart LR
  IMAGE["同一镜像\nhybrid-cloud-bms-monitoring-platform"]

  IMAGE --> WEB["bms-web-app\nBMS_COMPONENT=web\nReceiver=false\nScheduler=false"]
  IMAGE --> SYS["syslog-receiver\nSyslog=true\nTrap=false\nScheduler=false"]
  IMAGE --> TRAP["snmp-trap-receiver\nSyslog=false\nTrap=true\nScheduler=false"]
  IMAGE --> MON["monitoring-worker\nReceiver=false\nScheduler=false"]
  IMAGE --> ALERT["alert-check-worker\nReceiver=false\nScheduler=true"]

  WEB -->|"由 Service/Ingress 暴露"| USERS["浏览器 / HTTPS API"]
  SYS -->|"由 LoadBalancer Service 暴露"| SYSDEV["Syslog 发送设备"]
  TRAP -->|"由 LoadBalancer Service 暴露"| TRAPDEV["SNMP Trap 设备"]
  MON -->|"不对外暴露"| INTERNAL1["内部任务"]
  ALERT -->|"不对外暴露"| INTERNAL2["告警检查"]
```

### 当前实现边界

- 五个 Deployment 已独立，接收器和 scheduler 开关已配置。
- 所有角色仍加载同一 Spring Boot 应用上下文；这属于“模块化单体的运行角色隔离”，不是完全独立微服务。
- `monitoring-worker` 当前没有独立对外 Service；后续加入分布式调度锁后再扩多个副本。

## 5. Syslog UDP 通信路径

UDP 没有连接和应用层确认，设备看到“发送成功”不代表平台已入库。因此需要结合 NLB 指标、Pod 接收日志和数据库事件三层确认。

```mermaid
sequenceDiagram
  participant D as Network Device
  participant N as OCI NLB
  participant S as K8s Service
  participant P as Syslog Receiver Pod
  participant X as SyslogParser
  participant E as EventProcessingService
  participant DB as Database

  D->>N: UDP datagram / 514
  Note over D,N: 无 TCP handshake，无应用层 ACK
  N->>S: UDP backend forwarding
  S->>P: targetPort 5514/UDP
  P->>X: raw datagram
  X-->>P: facility severity timestamp host message
  P->>E: IngestRequest
  E->>DB: INSERT monitoring_events
  E->>DB: INSERT/UPDATE alerts when required
```

## 6. Syslog TCP 通信路径

TCP 能确认传输连接，但仍不代表业务入库成功。接收器按行分帧；设备必须发送换行或按双方约定关闭连接。

```mermaid
sequenceDiagram
  participant D as Network Device
  participant N as OCI NLB
  participant P as Syslog Receiver Pod
  participant DB as Database

  D->>N: TCP SYN / 514
  N->>P: TCP SYN / 5514
  P-->>D: Connection established
  D->>P: RFC3164 or RFC5424 line
  P->>P: 按行分帧与解析
  P->>DB: 保存 raw + normalized event
  alt 格式错误
    P->>DB: 保存/记录受控错误
  else 重复事件
    P->>DB: duplicate=true 并执行抑制
  end
```

## 7. SNMP Trap 通信路径

Trap 是设备主动发送的 UDP 通知。生产端口是 162，本地/容器端口是 1162。接收器监听通配地址，以便 localhost、Docker、Pod 网卡和 NLB 转发都能到达。

```mermaid
sequenceDiagram
  participant D as SNMP Device
  participant N as OCI NLB UDP 162
  participant S as K8s Service UDP 1162
  participant R as Trap Receiver Pod
  participant P as SnmpTrapParser
  participant E as Event Processing
  participant DB as Database

  D->>N: SNMPv2c Trap varbinds
  N->>S: UDP forwarding
  S->>R: 0.0.0.0:1162
  R->>P: PDU + peer address
  P-->>R: trapOID eventKey severity status
  R->>E: normalized request
  E->>DB: Event
  alt linkDown
    E->>DB: Alert = CRITICAL
  else linkUp
    E->>DB: Alert = RECOVERED
  end
```

## 8. AWS 主动监控与跨云 HTTPS

EventBridge 触发 Lambda。Lambda 从客户网络视角执行检查，然后只把标准 JSON 结果发到 OCI。OCI 不需要允许 AWS 直接连接数据库。

```mermaid
sequenceDiagram
  participant EB as EventBridge
  participant L as AWS Lambda
  participant D as Customer Device
  participant LB as OCI HTTPS LB
  participant API as BMS Web/API Pod
  participant DB as ADB/PostgreSQL

  EB->>L: 定时 payload
  alt SNMP GET
    L->>D: UDP 161 OID/community/timeout/retry
    D-->>L: value or timeout
  else TCP Ping
    L->>D: TCP connect target port
    D-->>L: success/refused/timeout
  end
  L->>LB: POST /api/v1/ingest/events HTTPS 443
  Note over L,LB: API Key 示例；生产建议 mTLS/OIDC
  LB->>API: HTTP 8080
  API->>DB: Event + Alert transaction
  API-->>L: accepted/error response
```

## 9. Web 与 API 的七层入口

Web 和 JSON API 都经过 HTTPS。TLS、证书、Host/Path 路由在 OCI LB 或 Ingress 层处理；Pod 只暴露集群内 HTTP 8080。

```mermaid
flowchart LR
  CLIENT["Browser / AWS Lambda"]
  DNS["DNS"]
  WAF["WAF 可选"]
  LB["OCI Load Balancer\nTLS 443"]
  INGRESS["Ingress Controller\nHost/Path"]
  SVC["ClusterIP Service\n80"]
  POD["bms-web-app Pod\n8080"]
  SECURITY["Spring Security\nSession / RBAC / API Key"]
  APP["MVC Controller / API Controller"]

  CLIENT --> DNS --> WAF --> LB --> INGRESS --> SVC --> POD --> SECURITY --> APP
```

## 10. 为什么协议入口使用 NLB，而 Web 使用 LB

```mermaid
flowchart TB
  INPUT{"进入的是什么流量？"}
  INPUT -->|"HTTP / HTTPS"| L7["七层 LB / Ingress"]
  INPUT -->|"原始 UDP / TCP"| L4["四层 NLB"]

  L7 --> TLS["TLS 终止"]
  L7 --> ROUTE["Host / Path 路由"]
  L7 --> HEADER["HTTP Header / Session"]

  L4 --> SYSLOG["Syslog UDP/TCP"]
  L4 --> TRAP["SNMP Trap UDP"]
  L4 --> SOURCE["尽量保留源地址语义"]

  BAD["错误做法"] -->|"把 UDP 交给仅 HTTP Ingress"| DROP["无法建立正确协议路径"]
```

## 11. 端口转换与责任矩阵

| 用途 | 外部标准端口 | 本地/Pod 端口 | 协议 | 入口 | 终点 |
|---|---:|---:|---|---|---|
| Web / API | 443 | 8080 | TCP/HTTPS→HTTP | OCI LB / Ingress | `bms-web-app` |
| Syslog | 514 | 5514 | UDP | OCI NLB | `syslog-receiver` |
| Syslog | 514 | 5514 | TCP | OCI NLB | `syslog-receiver` |
| SNMP Trap | 162 | 1162 | UDP | OCI NLB | `snmp-trap-receiver` |
| SNMP GET | 161 | 161/1161 | UDP | Lambda 出站 | 网络设备 / 本地 agent |
| PostgreSQL | 不公开 | 5432 | TCP | ClusterIP | PostgreSQL Pod |
| Oracle ADB | 不公开 | 1522 等 | TCP/TLS | Private Endpoint | ADB |
| SMTP 模拟 | 不公开 | 1025 | TCP | ClusterIP | MailHog |
| SSH | 不公开 | 22 | TCP | Bastion 临时会话 | 受控目标 |

> 当前 Kubernetes YAML 的 LoadBalancer Service 直接公开 5514/1162。生产的 514→5514、162→1162 listener/backend 映射属于云环境层配置。当前 `oci-load-balancer` 模块只创建 NLB/LB 外壳，尚未创建完整 listener、backend set、证书和 DNS；上线前必须补齐，不能把本图当作已部署事实。

```mermaid
flowchart LR
  EXT514["外部 514 UDP/TCP"] --> NLB514["NLB Listener 514\n生产补充"] --> SVC5514["Service 5514"] --> POD5514["Pod 5514"]
  EXT162["外部 162 UDP"] --> NLB162["NLB Listener 162\n生产补充"] --> SVC1162["Service 1162"] --> POD1162["Pod 1162"]
  EXT443["外部 443 HTTPS"] --> LB443["LB Listener 443\n生产补充"] --> SVC80["Service 80"] --> POD8080["Pod 8080"]
```

## 12. NetworkPolicy 允许的容器通信

Namespace 先 default deny，再按需要放行。业务 Pod 可以访问数据库、MailHog、DNS 和 HTTPS；数据库与 MailHog 只接受 BMS Pod。

```mermaid
flowchart LR
  subgraph BMSPODS["BMS Pods"]
    WEB["Web"]
    SYS["Syslog"]
    TRAP["Trap"]
    MON["Monitoring"]
    ALERT["Alert"]
  end

  DNS["DNS\nUDP/TCP 53"]
  HTTPS["外部 HTTPS\nTCP 443"]
  PG["PostgreSQL\nTCP 5432"]
  MAIL["MailHog\nTCP 1025"]
  DENY["其他 Ingress/Egress\n默认拒绝"]

  WEB --> PG
  SYS --> PG
  TRAP --> PG
  MON --> PG
  ALERT --> PG
  ALERT --> MAIL
  WEB --> DNS
  SYS --> DNS
  TRAP --> DNS
  MON --> HTTPS
  ALERT --> HTTPS
  DENY -. "没有显式规则" .-> BMSPODS
```

### 注意

当前 `allow-bms-required-traffic` 的入站规则按端口放行，没有限制来源 Pod/namespace/IPBlock。生产应分别为 Web、Syslog、Trap 建立更窄的来源规则，并结合 OCI NSG 限制设备网段。

## 13. Event 与 Alert 的事务关系

Event 是一次观测事实；Alert 是多个 Event 聚合出的持续状态。两者不能合并成同一张表或同一概念。

```mermaid
flowchart LR
  RAW["原始数据"]
  EVENT1["Event #101\nlinkDown"]
  EVENT2["Event #102\n重复 linkDown"]
  EVENT3["Event #103\nlinkUp"]
  ALERT["Alert A-10\n同一设备+eventKey"]
  HISTORY["AlertHistory"]

  RAW --> EVENT1 -->|"创建"| ALERT
  RAW --> EVENT2 -->|"eventCount + 1\n重复抑制"| ALERT
  RAW --> EVENT3 -->|"状态变更"| ALERT
  ALERT -->|"CRITICAL"| HISTORY
  ALERT -->|"RECOVERED"| HISTORY
```

```mermaid
sequenceDiagram
  participant R as Receiver/API
  participant E as EventProcessingService
  participant D as Device/Rule Repository
  participant A as AlertService
  participant DB as Database
  participant N as NotificationService

  R->>E: IngestRequest
  E->>D: 设备与规则匹配
  D-->>E: device rule threshold
  E->>DB: INSERT Event
  E->>A: event + normalized status
  A->>DB: find active Alert by key
  A->>DB: create/update Alert + AlertHistory
  A->>N: notify only when policy requires
  N->>DB: NotificationDelivery + AuditLog
```

## 14. 告警通知容器路径

本地使用 MailHog。生产可以替换为 OCI Email Delivery、Notifications 或外部邮件网关。物理警报器不由本地代码直接控制，只保留安全 Mock/外部系统接口。

```mermaid
flowchart LR
  ALERT["Alert 状态变化"]
  POLICY["级别 / 通知规则"]
  IDEMP["幂等键检查"]
  WORKER["alert-check-worker"]
  LOCAL["MailHog SMTP 1025"]
  OCIEMAIL["OCI Email Delivery\n生产替换"]
  OCINOTIFY["OCI Notifications\n生产替换"]
  EXT["外部网关 / 工单系统\n生产替换"]
  AUDIT[("notification_deliveries\naudit_logs")]

  ALERT --> POLICY --> IDEMP --> WORKER
  WORKER --> LOCAL
  WORKER -.-> OCIEMAIL
  WORKER -.-> OCINOTIFY
  WORKER -.-> EXT
  LOCAL --> AUDIT
  OCIEMAIL -.-> AUDIT
  OCINOTIFY -.-> AUDIT
  EXT -.-> AUDIT
```

## 15. 扩容、高可用与故障隔离

Web 可以水平扩容；接收器应按协议流量独立扩容，但 UDP 扩容前必须确认 NLB 分流、去重和源地址策略。scheduler 多副本前必须加入分布式锁。

```mermaid
flowchart TB
  TRAFFIC["HTTPS 流量增加"] --> HPA["Web HPA\n2 -> 6 Pods"]
  HPA --> PDB["PDB minAvailable=1"]
  PDB --> ROLL["Rolling Update"]

  SYSLOAD["Syslog 流量增加"] --> SYSSCALE["独立扩容 Syslog Receiver"]
  TRAPLOAD["Trap 流量增加"] --> TRAPSCALE["独立扩容 Trap Receiver"]
  SYSSCALE --> DEDUPE["数据库唯一键 / 重复抑制"]
  TRAPSCALE --> DEDUPE

  SCHED["Alert Scheduler"]
  SCHED -->|"当前 1 replica"| SAFE["避免重复执行"]
  SCHED -. "扩容前必须增加" .-> LOCK["DB/Redis 分布式锁"]
```

### 组件故障影响

| 故障组件 | 直接影响 | 不应受影响 |
|---|---|---|
| Web Pod | 部分页面/API 请求 | 协议接收、其他 Web Pod |
| Syslog receiver | Syslog 接收 | Trap、Web、已有数据查询 |
| Trap receiver | Trap 接收 | Syslog、Web、已有数据查询 |
| monitoring worker | 主动检查延迟 | 被动接收、页面查询 |
| alert worker | 告警检查/通知延迟 | Event 原始数据接收 |
| Database | 全部持久化路径 | 网络入口可能仍接包，但不能宣称业务成功 |

## 16. 本地 Docker Compose 与云端 OKE 的对应关系

本地 Compose 为单个 `bms-app` 容器同时启用全部角色；OKE 把同一镜像拆成五个 Deployment。两者业务代码相同，但伸缩和故障边界不同。

```mermaid
flowchart LR
  subgraph LOCAL["Docker Compose"]
    APP["bms-app\nWeb + Syslog + Trap + Scheduler"]
    LPG[("PostgreSQL")]
    LM["MailHog"]
    AGENT["SNMP Agent"]
    SIM["Syslog Simulator"]
    APP --> LPG
    APP --> LM
    APP --> AGENT
    SIM --> APP
  end

  subgraph CLOUD["OKE"]
    W["Web Deployment"]
    S["Syslog Deployment"]
    T["Trap Deployment"]
    M["Monitoring Deployment"]
    A["Alert Deployment"]
    CDB[("ADB / PostgreSQL")]
    W --> CDB
    S --> CDB
    T --> CDB
    M --> CDB
    A --> CDB
  end

  APP -. "同一镜像按角色拆分" .-> W
  APP -.-> S
  APP -.-> T
  APP -.-> M
  APP -.-> A
```

## 17. 从本地到生产的部署演进

```mermaid
flowchart LR
  JAR["阶段 1\n本地 JAR + PostgreSQL"]
  COMPOSE["阶段 2\nDocker Compose"]
  KIND["阶段 3\nkind + 5 Deployments"]
  OKEDEV["阶段 4\nOKE Dev + PostgreSQL/ADB"]
  OKEPROD["阶段 5\nOKE Production + ADB + LB/NLB"]
  HYBRID["阶段 6\nAWS Lambda + VPN + OCI"]

  JAR --> COMPOSE --> KIND --> OKEDEV --> OKEPROD --> HYBRID
```

每个阶段必须先验证当前层，再进入下一层：

- JAR：构建、测试、登录、协议模拟。
- Compose：五个服务健康、数据库数据、MailHog。
- kind：五个 Deployment、Service、NetworkPolicy、rollout。
- OKE Dev：私有节点、LB/NLB backend health、日志与成本。
- OKE Production：证书、DNS、Vault、ADB、备份、HPA/PDB。
- Hybrid：双隧道 VPN、路由、Lambda VPC、端到端延迟与重试。

## 18. 通信故障排查图

```mermaid
flowchart TB
  START["监控数据没有出现在页面"]
  SEND{"发送端是否真的发出？"}
  PORT{"目标 IP / 协议 / 端口正确？"}
  CLOUD{"NLB/LB backend healthy？"}
  K8S{"Service endpoint 有 Pod IP？"}
  POD{"Pod 端口监听且 Ready？"}
  POLICY{"NSG / Security List / NetworkPolicy 允许？"}
  PARSE{"接收日志有解析错误？"}
  DB{"数据库写入成功？"}
  UI{"页面筛选/时间范围正确？"}
  DONE["定位完成"]

  START --> SEND
  SEND -->|"否"| DONE
  SEND -->|"是"| PORT
  PORT -->|"否"| DONE
  PORT -->|"是"| CLOUD
  CLOUD -->|"否"| DONE
  CLOUD -->|"是"| K8S
  K8S -->|"否"| DONE
  K8S -->|"是"| POD
  POD -->|"否"| DONE
  POD -->|"是"| POLICY
  POLICY -->|"否"| DONE
  POLICY -->|"是"| PARSE
  PARSE -->|"是"| DONE
  PARSE -->|"否"| DB
  DB -->|"失败"| DONE
  DB -->|"成功"| UI
  UI --> DONE
```

### 按协议检查

| 现象 | 第一检查点 | 第二检查点 | 最终证据 |
|---|---|---|---|
| UDP Syslog 丢失 | 设备目标 IP/514、NLB UDP listener | Service endpoint、Pod 5514/UDP | `monitoring_events.raw_message` |
| TCP Syslog 失败 | TCP handshake、换行分帧 | NLB TCP backend、Pod 5514/TCP | Event 与 receiver 日志 |
| Trap 丢失 | UDP 162、community/来源网段 | Pod 通配监听 1162、NSG/Policy | Trap history 计数增加 |
| Lambda 上报失败 | Lambda CloudWatch、DNS/NAT/VPN | LB 443、API Key/mTLS | API 响应与 Event ID |
| 页面打不开 | DNS/TLS/LB/Ingress | Web Service endpoint、readiness | HTTP 状态与 request ID |
| 通知没发送 | Alert 状态、幂等键 | SMTP/Notifications 可达性 | NotificationDelivery/AuditLog |

## 19. 当前实现与生产补充项

| 领域 | 当前仓库已有 | 生产仍需补充 |
|---|---|---|
| OCI 网络 | VCN、子网、IGW、NAT、NSG 资源样例 | Service Gateway、DRG/VPN、完整 NSG/Security List |
| OCI 入口 | NLB/LB 资源外壳 | listener、backend set、health check、证书、DNS |
| OKE | Cluster/Node Pool 模块与 Kubernetes YAML | OCIR、Ingress Controller、metrics-server、日志采集 |
| 数据库 | PostgreSQL、ADB 模块/Profile | Wallet/Vault 注入、备份、DR、容量测试 |
| 跨云 | HTTPS/VPN 设计说明 | 双隧道、BGP/静态路由、真实设备网段 |
| 认证 | 本地 RBAC、API Key | IdP/OIDC、mTLS、WAF、密钥轮换 |
| 调度 | 单副本 scheduler | 分布式锁、任务租约、失败队列 |

## 20. 相关实现位置

- Kubernetes 五角色：`infra/kubernetes/base/bms-components.yaml`
- Service 与端口：`infra/kubernetes/base/services.yaml`
- 网络策略：`infra/kubernetes/base/network-policy.yaml`
- Compose 本地结构：`docker-compose.yml`
- OCI VCN：`infra/opentofu/modules/oci-network`
- OCI NLB/LB：`infra/opentofu/modules/oci-load-balancer`
- AWS VPC：`infra/opentofu/modules/aws-network`
- Lambda：`apps/snmp-get-lambda`、`apps/tcp-ping-lambda`
- 协议接收器：`app/bms-app/src/main/java/com/example/bms/protocol`
- 运维排查：`docs/operations/runbook.md`

