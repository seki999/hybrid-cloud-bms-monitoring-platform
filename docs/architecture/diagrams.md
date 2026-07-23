# 架构图集

以下 12 张 Mermaid 图与当前代码、容器、Kubernetes 和 IaC 目录一一对应；虚线表示只提供部署样例、默认关闭的云资源。

需要深入了解跨云通信、NLB/LB、端口转换、五类 Pod、NetworkPolicy、故障隔离和逐层排查时，请继续阅读包含 20 个专题、19 张图的[云端通信与容器结构设计书](cloud-communication-and-container-design.md)。

## 1. 整体系统

```mermaid
flowchart LR
  Device["客户路由器 / VPN / 网络设备"] -->|"Syslog / Trap"| Receiver["OCI 接收层"]
  Lambda["AWS Lambda 主动监控"] -->|"HTTPS JSON"| API["Spring Boot API"]
  Receiver --> Normalize["解析与标准化"] --> Judge["障害 / 恢复判定"] --> DB[("PostgreSQL / ADB")]
  Judge --> Alert["告警与通知"]
  DB --> MVC["Spring MVC + Thymeleaf"] --> Browser["运维浏览器"]
```

## 2. AWS 与 OCI 混合云网络

```mermaid
flowchart LR
  subgraph AWS
    EB["EventBridge"] --> L["SNMP GET / TCP Ping Lambda"]
    L --> AVPC["私有子网"]
  end
  AVPC -->|"VPN 路由或 NAT + HTTPS 443"| OLB["OCI LB / Ingress"]
  subgraph OCI
    OLB --> OKE["OKE 私有服务"] --> ADB[("Autonomous Database")]
    NLB["OCI NLB UDP/TCP"] --> OKE
  end
```

## 3. AWS Lambda 主动监控

```mermaid
sequenceDiagram
  participant E as EventBridge
  participant L as Java 21 Lambda
  participant D as Network Device
  participant B as BMS API
  E->>L: schedule + target payload
  L->>D: SNMP GET UDP 161 / TCP connect
  D-->>L: value or timeout/error
  L->>B: HTTPS POST + API key
  B-->>L: 202 eventId
```

## 4. OCI 运行拓扑

```mermaid
flowchart TB
  Internet --> NLB["Public NLB"] --> R1["Syslog Receiver"]
  NLB --> R2["SNMP Trap Receiver"]
  Internet --> LB["HTTPS Load Balancer"] --> W["Web/API Pods"]
  W --> MW["Monitoring Worker"]
  W --> AW["Alert Check Worker"]
  R1 --> ADB[("ADB Private Endpoint")]
  R2 --> ADB
  MW --> ADB
  AW --> ADB
  Bastion["OCI Bastion"] -. "临时运维" .-> OKE["Private OKE API"]
```

## 5. Syslog 接收链路

```mermaid
sequenceDiagram
  participant D as Device
  participant U as UDP/TCP 5514
  participant P as SyslogParser
  participant E as EventProcessingService
  participant R as Repository
  D->>U: RFC3164 or RFC5424 raw message
  U->>P: UTF-8 line/datagram
  P-->>U: facility severity timestamp host message
  U->>E: normalized IngestRequest
  E->>R: raw + normalized + duplicate flag
```

## 6. SNMP GET 与 Trap

```mermaid
flowchart LR
  Rule["OID / community / timeout / retry"] --> GET["SNMP4J v2c GET"] --> Device
  Device -->|"value / timeout"| GET --> API["EventProcessingService"]
  Device -->|"v2c Trap UDP 1162"| Trap["SnmpTrapReceiver"] --> Parser["TrapParser"] --> API
  V3["SnmpV3Extension 接口"] -. "后续认证与加密" .-> GET
```

## 7. TCP Ping

```mermaid
stateDiagram-v2
  [*] --> ResolveDNS
  ResolveDNS --> Connect: address resolved
  ResolveDNS --> DNS_ERROR: unknown host
  Connect --> SUCCESS: socket connected
  Connect --> REFUSED: connection refused
  Connect --> TIMEOUT: deadline exceeded
  REFUSED --> Retry
  TIMEOUT --> Retry
  Retry --> Connect: retry remaining
  Retry --> [*]: exhausted
  SUCCESS --> [*]
```

## 8. 事件处理

```mermaid
flowchart LR
  Raw["原始监控数据"] --> Parse["格式解析"] --> Norm["标准化"] --> Device["设备主数据匹配"] --> Rule["规则匹配"] --> Threshold["阈值判断"] --> Dedupe["重复抑制"] --> Save["Event 保存"] --> Alert["Alert 新建/更新"] --> Notify["幂等通知"]
```

## 9. 告警生命周期

```mermaid
stateDiagram-v2
  [*] --> NORMAL
  NORMAL --> WARNING
  WARNING --> CRITICAL
  WARNING --> RECOVERED
  CRITICAL --> RECOVERED
  WARNING --> ACKNOWLEDGED: operator confirms
  CRITICAL --> ACKNOWLEDGED: operator confirms
  ACKNOWLEDGED --> RECOVERED: recovery event
  RECOVERED --> CLOSED: automatic/manual close
  ACKNOWLEDGED --> CLOSED: manual close
```

## 10. Spring MVC SSR

```mermaid
sequenceDiagram
  participant B as Browser
  participant S as Spring Security
  participant C as MVC Controller
  participant V as Service
  participant R as JPA Repository
  participant T as Thymeleaf
  B->>S: GET /alerts
  S->>C: authenticated request + role
  C->>V: search filters + page
  V->>R: query
  R-->>V: Page entities
  V-->>C: view model
  C->>T: template + model
  T-->>B: Japanese HTML response
```

## 11. Kubernetes 工作负载

```mermaid
flowchart TB
  Ingress --> Web["bms-web-app x2 + HPA/PDB"]
  NLB --> Syslog["syslog-receiver"]
  NLB --> Trap["snmp-trap-receiver"]
  Monitor["monitoring-worker"] --> DB[("PostgreSQL / ADB")]
  Alert["alert-check-worker"] --> DB
  Web --> DB
  Syslog --> DB
  Trap --> DB
  Config["ConfigMap + Secret/Vault"] --> Web
  Probe["Actuator probes"] --> Web
```

## 12. OpenTofu 模块依赖

```mermaid
flowchart LR
  Env["environment: dev/staging/production"] --> ON["oci-network"]
  Env --> AN["aws-network"]
  ON --> OKE["oci-oke"]
  ON --> LB["oci-load-balancer"]
  ON --> ADB["oci-adb"]
  ON --> FN["oci-functions"]
  ON --> Bastion["oci-bastion"]
  AN --> Lambda["aws-lambda"]
  OKE --> LB
  OKE --> ADB
```
