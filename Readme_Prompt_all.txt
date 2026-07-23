你现在是一名资深云架构师、Java技术负责人、网络监控系统工程师和DevOps工程师。

请在当前Git仓库中，设计并实现一个可以本地运行、可以部署到Kubernetes、并包含AWS与OCI基础设施示例的完整学习项目。

项目名称：

hybrid-cloud-bms-monitoring-platform

项目定位：

这是一个模拟大型通信运营商BMS网络监视系统的新系统重构项目。

系统运行于AWS＋OCI混合云环境中，主要负责监控客户侧路由器、VPN线路、网络设备和通信服务状态。

系统需要采集和处理：

- Syslog
- SNMP GET
- SNMP Trap
- TCP Ping
- 网络设备状态
- 设备告警
- 通信故障
- 恢复事件
- 历史趋势数据

新系统的主要运行环境是OCI。

AWS Lambda负责执行SNMP GET和TCP Ping等主动监控任务。

OCI侧负责：

- 数据接收
- 数据转换
- 障害判定
- 数据保存
- Web管理画面
- 告警检查
- 邮件通知
- 外部系统联动

本项目的核心目标不是制作简单Demo，而是制作一个：

- 架构完整
- 可以实际运行
- 代码结构清晰
- 代码有详细注释
- 文档非常详细
- 可以用于学习和面试说明
- 可以在Windows和macOS上运行
- 可以逐步部署到Docker、Kubernetes和OCI的项目

==================================================
一、必须使用的技术栈
==================================================

1. Java后端

必须使用：

- Java 21
- Spring Boot 3.x
- Maven
- Maven Wrapper
- Spring MVC
- Spring Validation
- Spring Security
- Spring Data JPA
- Spring JDBC或JdbcTemplate
- Spring Scheduler
- Spring Boot Actuator
- Thymeleaf
- SLF4J
- Logback
- JUnit 5
- Mockito
- MockMvc
- Testcontainers

禁止使用：

- React
- Vue
- Angular
- Next.js
- 独立前端工程
- Node.js前端构建项目

本项目不是前后端分离架构。

必须采用：

Spring Boot＋Spring MVC＋Thymeleaf服务端渲染

运行流程必须是：

浏览器
→ Spring MVC Controller
→ Service
→ Repository
→ 数据库
→ Model
→ Thymeleaf模板
→ HTML响应

允许使用少量原生JavaScript增强页面功能，例如：

- Fetch API
- Ajax
- 定时刷新
- 表格筛选
- Modal
- 动态表单
- 图表展示

可以使用：

- Bootstrap
- Chart.js
- 原生HTML
- 原生CSS
- 原生JavaScript

2. 网络监控协议

必须实现或模拟以下功能。

Syslog：

- UDP Syslog接收
- TCP Syslog接收
- 默认本地端口使用5514，避免普通用户无法绑定514
- 支持通过配置切换为514
- RFC 3164基础格式
- RFC 5424基础格式
- Facility解析
- Severity解析
- Timestamp解析
- Hostname解析
- Message解析
- 原始消息保存
- 格式错误处理
- 重复消息处理
- Syslog发送模拟器

SNMP：

- SNMP GET
- SNMP Trap
- SNMP v2c
- 预留SNMP v3扩展接口
- OID
- MIB概念
- Community配置
- Timeout
- Retry
- Trap解析
- GET结果解析
- SNMP事件保存
- SNMP Trap模拟发送器

Java SNMP实现优先使用：

- SNMP4J

如果需要系统工具，可以辅助使用：

- Net-SNMP
- snmptrapd
- snmpget
- snmpwalk
- snmptrap

本地Trap端口默认使用1162，避免普通用户无法绑定162。

TCP Ping：

这里的TCP Ping不是普通ICMP Ping。

必须通过Java Socket尝试连接指定的：

- IP地址
- TCP端口

需要记录：

- 成功或失败
- 响应时间
- Connection refused
- Timeout
- DNS错误
- 检查时间
- 重试次数

必须提供一个可以配置监控目标的页面。

3. Spring Boot核心业务

必须实现以下业务模块：

- 设备管理
- 监视对象管理
- Syslog事件接收
- SNMP Trap事件接收
- SNMP GET结果接收
- TCP Ping结果接收
- 数据标准化
- 障害判定
- 恢复判定
- 重复告警抑制
- 告警级别判定
- 告警确认
- 告警关闭
- 告警历史
- 监控条件设置
- 阈值设置
- 通知对象设置
- 告警检查定时任务
- 邮件发送模拟
- 审计日志
- 系统运行状态页面
- 历史趋势报表

障害状态至少包括：

- NORMAL
- WARNING
- CRITICAL
- RECOVERED
- ACKNOWLEDGED
- CLOSED

障害判定流程需要清楚实现：

原始监控数据
→ 格式解析
→ 数据标准化
→ 查找设备主数据
→ 查找监视规则
→ 阈值判断
→ 障害或恢复判断
→ 重复事件判断
→ 保存事件
→ 创建或更新告警
→ 触发通知

必须设计清楚以下概念：

- Event
- Alert
- Device
- MonitoringTarget
- MonitoringRule
- NotificationTarget
- AlertHistory
- AuditLog

Event和Alert不能混为一谈。

Event表示单次监控事件。

Alert表示由一个或多个Event形成的持续故障状态。

4. Web管理画面

页面文字使用日语。

至少实现以下页面：

1. ログイン画面
2. ダッシュボード
3. 監視対象機器一覧
4. 監視対象機器詳細
5. 監視対象機器登録・編集
6. 監視ルール設定
7. イベント一覧
8. イベント詳細
9. アラート一覧
10. アラート詳細
11. アラート確認画面
12. Syslog受信履歴
13. SNMP Trap受信履歴
14. SNMP GET結果一覧
15. TCP Ping結果一覧
16. 通知先設定
17. レポート画面
18. システム稼働状況
19. 監査ログ
20. ユーザー管理

Dashboard至少显示：

- 监控设备总数
- 正常设备数
- Warning数量
- Critical数量
- 当前未恢复告警
- 最近事件
- 最近告警
- Syslog接收数量
- SNMP Trap接收数量
- TCP Ping成功率
- 最近24小时趋势图

页面必须使用：

- Thymeleaf Fragment
- 公共Header
- 公共Sidebar
- 公共Footer
- 统一Layout
- 表单Validation
- 错误信息显示
- 分页
- 排序
- 搜索
- 状态颜色区分
- 日期格式化

5. 数据库

本地开发默认使用：

- PostgreSQL

必须提供：

- Docker Compose PostgreSQL
- Flyway数据库迁移
- 初始化测试数据
- 数据库Schema说明
- ER图
- 索引设计
- 外键设计
- 审计字段

同时提供Oracle ADB连接Profile。

需要包含：

- application-local.yml
- application-postgresql.yml
- application-oracle.yml
- Oracle JDBC驱动配置
- Oracle ADB Wallet连接说明
- HikariCP配置
- Secret管理说明

注意：

项目默认不能强制要求真实OCI账号。

本地运行必须只依赖：

- Java
- Docker
- Maven
- 浏览器

Oracle ADB配置只能作为可选Profile。

6. AWS部分

必须创建AWS Lambda示例。

至少包含两个Lambda：

- snmp-get-lambda
- tcp-ping-lambda

功能：

snmp-get-lambda：

- 根据配置执行SNMP GET
- 返回OID和监控值
- 将结果通过HTTPS发送到OCI侧或本地Spring Boot API
- 支持Timeout和Retry
- 支持错误处理
- 支持CloudWatch日志

tcp-ping-lambda：

- 根据目标IP和端口执行TCP连接检查
- 记录响应时间
- 将结果通过HTTPS发送给Spring Boot API
- 支持多个监控目标
- 支持Timeout和Retry
- 支持CloudWatch日志

必须提供：

- Lambda源码
- Lambda单元测试
- IAM最小权限示例
- CloudWatch Logs配置
- EventBridge定时执行示例
- AWS网络构成说明
- Lambda访问私有网络设备时的VPC配置说明
- Security Group说明
- NAT或VPN通信说明

如果本地无法真正运行Lambda，则必须提供：

- Lambda本地模拟脚本
- 示例Payload
- Spring Boot接收API
- 完整运行说明

7. OCI部分

必须详细说明并提供OpenTofu基础设施示例。

需要覆盖：

- OCI Tenancy
- Compartment
- IAM User
- IAM Group
- IAM Policy
- Dynamic Group
- VCN
- Public Subnet
- Private Subnet
- Route Table
- Internet Gateway
- NAT Gateway
- Service Gateway
- Network Security Group
- Security List
- OCI Network Load Balancer
- OCI Load Balancer
- OKE
- Node Pool
- Oracle Autonomous Database
- OCI Functions
- OCI Logging
- OCI Monitoring
- OCI Notifications
- OCI Vault
- OCI Bastion
- Smart Jumper概念
- 跨云连接
- VPN
- 路由
- AWS与OCI之间的HTTPS通信

需要解释：

为什么Syslog和SNMP Trap使用NLB。

为什么Web和HTTPS API使用Load Balancer或Ingress。

必须说明：

- Syslog UDP/TCP 514
- SNMP GET UDP 161
- SNMP Trap UDP 162
- HTTPS TCP 443
- SSH TCP 22

不要默认真正创建收费资源。

所有OCI资源必须：

- 默认关闭
- 通过变量显式启用
- 在README中明确费用风险
- 提供destroy步骤
- 不写死OCID
- 不写死Compartment ID
- 不写死密钥路径
- 不提交任何Secret

8. OpenTofu和Terraform

IaC主要使用OpenTofu。

必须提供：

- tofu init
- tofu fmt
- tofu validate
- tofu plan
- tofu apply
- tofu destroy

并详细解释：

- Provider
- Resource
- Data Source
- Variable
- Local
- Output
- Module
- State
- Remote Backend
- State Lock
- Dependency
- Lifecycle
- Import
- Taint替代方式
- moved block
- for_each
- count
- dynamic block
- workspace或环境分离
- tfvars
- sensitive变量
- Provider version pinning

必须说明OpenTofu与Terraform的关系和常见差异。

IaC目录至少包含：

infra/
├── opentofu/
│   ├── modules/
│   │   ├── oci-network/
│   │   ├── oci-oke/
│   │   ├── oci-load-balancer/
│   │   ├── oci-adb/
│   │   ├── oci-functions/
│   │   ├── oci-bastion/
│   │   ├── aws-lambda/
│   │   └── aws-network/
│   ├── environments/
│   │   ├── local/
│   │   ├── dev/
│   │   ├── staging/
│   │   └── production/
│   └── README.md

所有模块必须包含：

- main.tf
- variables.tf
- outputs.tf
- versions.tf
- README.md
- example.tfvars

9. Docker

必须提供：

- Spring Boot Dockerfile
- 多阶段构建
- 非root用户
- 健康检查
- PostgreSQL容器
- MailHog或邮件模拟容器
- SNMP模拟容器
- Syslog模拟容器
- Docker Compose

必须可以执行：

docker compose up -d

并启动完整本地环境。

Docker Compose至少包含：

- bms-app
- postgresql
- mailhog
- snmp-agent
- syslog-simulator

10. Kubernetes与OKE

必须提供Kubernetes YAML。

至少包括：

- Namespace
- Deployment
- Service
- ConfigMap
- Secret示例
- ServiceAccount
- Role
- RoleBinding
- PersistentVolumeClaim
- Ingress
- NetworkPolicy
- HorizontalPodAutoscaler
- PodDisruptionBudget
- Liveness Probe
- Readiness Probe
- Startup Probe
- Resource Requests
- Resource Limits

必须至少将以下组件逻辑分离：

- bms-web-app
- syslog-receiver
- snmp-trap-receiver
- monitoring-worker
- alert-check-worker

需要说明：

- 一个Pod多个Container和多个Deployment的区别
- 为什么接收服务应该独立扩缩容
- 如何在Kubernetes中暴露UDP服务
- NLB如何连接UDP/TCP Service
- ALB或Ingress如何连接HTTP Service
- ConfigMap和Secret的区别
- Rolling Update
- Pod故障恢复
- 日志查看
- 故障排查方法

需要提供常用命令说明：

- kubectl get
- kubectl describe
- kubectl logs
- kubectl exec
- kubectl port-forward
- kubectl rollout
- kubectl top
- kubectl events

本地Kubernetes必须支持：

- kind

或者：

- minikube

优先选择kind，并提供Windows和macOS运行步骤。

11. OCI Functions和告警

必须提供一个OCI Functions风格的告警通知示例。

功能：

- 接收告警Payload
- 根据告警级别决定通知方式
- 发送邮件到MailHog或模拟邮件服务
- 保存发送结果
- 失败重试
- 防止重复发送
- 记录审计日志

必须说明生产环境中如何替换为：

- OCI Email Delivery
- OCI Notifications
- 外部邮件网关
- 警报器
- TCP或SSH控制外部设备

不能在本地代码中真正控制任何物理警报器。

只需要实现安全的Mock。

12. VPN和网络设计

必须提供详细网络文档。

需要说明：

- AWS VPC
- OCI VCN
- Site-to-Site VPN
- Route Table
- Security Group
- NSG
- TCP
- UDP
- NAT
- DNS
- MTU
- MSS
- Timeout
- Retry
- 防火墙
- 跨云HTTPS
- Lambda访问客户网络
- 网络设备访问控制
- SNMP Community安全风险
- SNMP v3优势

必须使用Mermaid绘制：

- 系统全体构成图
- AWS构成图
- OCI构成图
- VCN构成图
- Kubernetes构成图
- 监视数据流图
- 告警处理时序图
- Spring Boot处理流程图
- 数据库ER图
- Syslog接收时序图
- SNMP Trap接收时序图
- SNMP GET处理时序图
- TCP Ping处理时序图

==================================================
二、项目目录要求
==================================================

请创建清晰的Monorepo结构。

建议目录：

hybrid-cloud-bms-monitoring-platform/
├── app/
│   └── bms-app/
├── apps/
│   ├── syslog-simulator/
│   ├── snmp-simulator/
│   ├── tcp-ping-simulator/
│   ├── snmp-get-lambda/
│   ├── tcp-ping-lambda/
│   └── alert-function/
├── infra/
│   ├── opentofu/
│   ├── kubernetes/
│   └── docker/
├── mock/
│   ├── syslog/
│   ├── snmp/
│   ├── lambda/
│   └── external-system/
├── input/
├── out/
├── scripts/
├── docs/
│   ├── architecture/
│   ├── technologies/
│   ├── database/
│   ├── network/
│   ├── operations/
│   ├── troubleshooting/
│   └── screenshots/
├── docker-compose.yml
├── README.md
├── CONTRIBUTING.md
├── SECURITY.md
├── CHANGELOG.md
└── LICENSE

==================================================
三、代码注释要求
==================================================

所有代码必须包含详细注释。

但不能简单地为每一行写无意义注释。

注释重点必须放在：

- 类的职责
- 方法的职责
- 参数意义
- 返回值意义
- 异常处理原因
- 网络协议处理逻辑
- 数据转换规则
- 障害判定规则
- 重复告警判断
- 恢复判断
- Timeout和Retry设计
- 安全设计
- 事务边界
- 并发处理
- 数据库索引考虑
- Kubernetes配置理由
- OpenTofu资源设计理由

Java代码必须包含：

- 类级Javadoc
- 公共方法Javadoc
- 复杂业务逻辑行内注释
- 枚举说明
- DTO字段说明
- Entity关系说明

代码注释和Javadoc使用中文。

UI页面使用日语。

变量名、类名、方法名使用清楚的英文。

禁止：

- 使用含义不明的变量名
- 大量复制代码
- 超大Controller
- 超大Service
- 将所有逻辑写在一个类中
- 空实现
- TODO代替实现
- 伪代码代替真正代码

==================================================
四、README要求
==================================================

README.md必须是非常详细的中文技术说明书。

README至少包含以下章节：

1. 项目介绍
2. 项目背景
3. 业务目标
4. 系统全体架构
5. AWS与OCI职责划分
6. 数据流说明
7. Syslog说明
8. SNMP GET说明
9. SNMP Trap说明
10. TCP Ping说明
11. Spring Boot服务端渲染说明
12. Thymeleaf说明
13. Java模块说明
14. 数据转换说明
15. 障害判定说明
16. 告警生命周期说明
17. PostgreSQL说明
18. Oracle ADB说明
19. Docker说明
20. Kubernetes说明
21. OKE说明
22. OCI NLB和Load Balancer说明
23. AWS Lambda说明
24. OCI Functions说明
25. OpenTofu说明
26. Terraform与OpenTofu比较
27. VPN和跨云网络说明
28. 安全设计
29. 项目目录说明
30. 本地运行方法
31. Windows运行方法
32. macOS运行方法
33. Docker运行方法
34. Kubernetes运行方法
35. OpenTofu运行方法
36. 测试方法
37. 截图说明
38. 常见故障排查
39. 已知限制
40. 未来扩展方向

README中必须包含完整命令。

例如：

git clone
cd
docker compose up
mvn test
mvn spring-boot:run
kubectl apply
tofu init
tofu plan
tofu destroy

README不能只列命令，必须解释每条命令的作用。

==================================================
五、各技术独立说明文档
==================================================

请在docs/technologies中创建独立文档。

至少包括：

- java-spring-boot.md
- spring-mvc.md
- thymeleaf.md
- html-css-javascript.md
- syslog.md
- snmp.md
- tcp-ping.md
- aws-lambda.md
- aws-network.md
- oci-overview.md
- oci-vcn.md
- oci-oke.md
- oci-load-balancer.md
- oci-adb.md
- oci-functions.md
- kubernetes.md
- docker.md
- opentofu.md
- terraform-comparison.md
- postgresql.md
- oracle-database.md
- vpn-and-routing.md
- monitoring-and-alerting.md
- security.md
- troubleshooting.md

每个文档必须包含：

- 技术是什么
- 为什么本项目使用它
- 在本项目中的位置
- 主要概念
- 主要配置
- 示例代码
- 常用命令
- 常见错误
- 排查方法
- 安全注意事项
- 本项目对应代码目录
- 面试中可以如何说明

==================================================
六、测试要求
==================================================

必须实现：

- Java单元测试
- Service测试
- Controller测试
- Repository测试
- Validation测试
- Syslog解析测试
- SNMP解析测试
- TCP Ping测试
- 障害判定测试
- 恢复判定测试
- 重复告警抑制测试
- 定时任务测试
- 权限测试
- API集成测试
- Testcontainers PostgreSQL测试

测试覆盖重点业务。

不要只测试Getter和Setter。

必须提供：

- 测试数据
- 测试运行命令
- 测试结果示例
- 覆盖率报告

可以使用JaCoCo。

目标：

- 业务核心代码覆盖率不低于80%
- 全项目总体覆盖率不低于70%

==================================================
七、演示数据要求
==================================================

必须提供可以一键生成的演示数据。

至少包括：

- 10台网络设备
- 3台Juniper风格路由器
- 2台Linux服务器
- 2台Windows服务器
- 3个虚拟网络服务
- 30条Syslog
- 20条SNMP Trap
- 20条SNMP GET结果
- 20条TCP Ping结果
- Normal事件
- Warning事件
- Critical事件
- Recovery事件
- 已确认告警
- 未确认告警
- 已关闭告警

提供脚本：

scripts/generate-demo-data.sh
scripts/generate-demo-data.ps1

==================================================
八、运行结果截图要求
==================================================

这是强制要求。

不能使用占位图。

不能制作假的截图。

必须实际启动项目，并使用Playwright自动访问页面并截取真实运行画面。

截图至少10张，建议12张。

必须包含：

1. 登录画面
2. Dashboard
3. 设备列表
4. 设备详细
5. 设备编辑
6. 事件列表
7. Syslog接收履历
8. SNMP Trap接收履历
9. TCP Ping结果
10. 告警列表
11. 告警详细
12. 报表或系统状态页面

截图保存到：

docs/screenshots/

文件名使用：

01-login.png
02-dashboard.png
03-devices.png
04-device-detail.png
05-device-edit.png
06-events.png
07-syslog.png
08-snmp-trap.png
09-tcp-ping.png
10-alerts.png
11-alert-detail.png
12-report.png

必须创建：

scripts/capture-screenshots.sh
scripts/capture-screenshots.ps1

截图脚本必须：

- 检查应用是否启动
- 自动登录
- 自动访问各页面
- 等待数据加载完成
- 使用固定浏览器尺寸
- 截取完整页面
- 发生错误时输出原因
- 不静默失败

优先使用：

- Playwright
- Chromium

如果环境中未安装浏览器，需要安装Playwright Chromium。

README中必须使用相对路径嵌入所有截图，例如：

![Dashboard](docs/screenshots/02-dashboard.png)

README中至少实际展示10张运行截图。

每张截图下方必须说明：

- 页面用途
- 主要功能
- 对应Controller
- 对应Service
- 对应数据库表
- 对应URL

==================================================
九、脚本要求
==================================================

必须同时支持Windows和macOS/Linux。

至少创建：

scripts/
├── dev-up.sh
├── dev-up.ps1
├── dev-down.sh
├── dev-down.ps1
├── run-local.sh
├── run-local.ps1
├── run-tests.sh
├── run-tests.ps1
├── generate-demo-data.sh
├── generate-demo-data.ps1
├── send-syslog.sh
├── send-syslog.ps1
├── send-snmp-trap.sh
├── send-snmp-trap.ps1
├── run-tcp-ping.sh
├── run-tcp-ping.ps1
├── capture-screenshots.sh
├── capture-screenshots.ps1
├── kind-up.sh
├── kind-up.ps1
├── kind-down.sh
└── kind-down.ps1

所有脚本必须：

- 有详细注释
- 失败立即停止
- 检查依赖
- 输出当前步骤
- 输出错误原因
- 不使用硬编码绝对路径
- 不要求管理员权限

==================================================
十、安全要求
==================================================

必须实现和说明：

- Spring Security
- 登录认证
- RBAC
- ADMIN
- OPERATOR
- VIEWER
- CSRF
- XSS防护
- 输入验证
- SQL注入防护
- Secret外部化
- 环境变量
- .env.example
- Kubernetes Secret示例
- OCI Vault说明
- AWS Secrets Manager说明
- 最小权限原则
- SNMP Community不能硬编码
- 日志中不能打印密码
- 日志中不能打印完整Secret
- Git中不能提交密钥
- 默认账号只用于本地演示

本地演示账号可以是：

admin / Admin123!

但README必须明确说明：

该账号只允许用于本地学习环境，生产环境必须修改。

==================================================
十一、代码质量要求
==================================================

必须遵循：

- 分层架构
- 清晰的Package划分
- SOLID
- 单一职责
- DTO与Entity分离
- Controller不包含业务逻辑
- Repository不包含业务判定
- Service负责业务处理
- Protocol Adapter负责协议解析
- Alert Engine负责障害判定
- Notification Adapter负责通知
- Infrastructure代码和业务代码分离

推荐Package：

com.example.bms
├── common
├── config
├── security
├── device
├── monitoring
├── event
├── alert
├── notification
├── report
├── audit
├── protocol
│   ├── syslog
│   ├── snmp
│   └── tcpping
├── infrastructure
└── web

必须实现统一异常处理：

- @ControllerAdvice
- 业务异常
- 参数异常
- 数据不存在
- 权限不足
- 系统异常

必须实现：

- 统一错误页面
- 404页面
- 403页面
- 500页面

==================================================
十二、日志与可观测性
==================================================

必须提供：

- Logback配置
- JSON日志Profile
- 普通文本日志Profile
- Correlation ID
- Request ID
- Actuator Health
- Actuator Metrics
- 自定义Health Indicator
- 数据库Health
- Syslog Receiver Health
- SNMP Receiver Health
- Alert Worker Health

必须解释生产环境中如何连接：

- OCI Logging
- OCI Monitoring
- CloudWatch Logs
- Prometheus
- Grafana

Prometheus和Grafana可以作为可选扩展，不需要成为默认运行条件。

==================================================
十三、实际执行要求
==================================================

不要只生成文件。

必须实际执行并验证以下内容：

1. Maven编译
2. Java单元测试
3. 集成测试
4. Docker镜像构建
5. Docker Compose启动
6. PostgreSQL初始化
7. Spring Boot应用启动
8. Demo数据生成
9. 页面访问
10. Syslog模拟发送
11. SNMP Trap模拟发送
12. TCP Ping执行
13. Playwright截图
14. README截图链接检查
15. Kubernetes YAML语法检查
16. OpenTofu fmt
17. OpenTofu validate

如果当前环境无法执行某一步：

- 明确记录原因
- 不要假装执行成功
- 给出可以在本地执行的准确命令
- 将未验证内容写入docs/validation-report.md

必须创建：

docs/validation-report.md

记录：

- 已执行项目
- 执行命令
- 执行时间
- 执行结果
- 失败原因
- 修正内容
- 尚未验证内容

==================================================
十四、实施顺序
==================================================

请按以下顺序工作。

第一阶段：

- 分析当前仓库
- 保留已有有效文件
- 创建IMPLEMENTATION_PLAN.md
- 写出任务清单
- 创建目录结构
- 创建基础README

第二阶段：

- 创建Spring Boot应用
- 创建数据库Schema
- 创建Entity
- 创建Repository
- 创建Service
- 创建Controller
- 创建Thymeleaf页面

第三阶段：

- 实现Syslog
- 实现SNMP
- 实现TCP Ping
- 实现Event
- 实现Alert Engine
- 实现通知功能

第四阶段：

- 添加Docker
- 添加PostgreSQL
- 添加MailHog
- 添加模拟器
- 添加Docker Compose

第五阶段：

- 添加Kubernetes
- 添加kind
- 添加OKE部署说明

第六阶段：

- 添加AWS Lambda
- 添加OCI Functions
- 添加OpenTofu

第七阶段：

- 完成技术文档
- 完成架构图
- 完成测试
- 生成Demo数据
- 启动应用
- 生成真实截图
- 将截图嵌入README

第八阶段：

- 执行完整验证
- 修复错误
- 更新validation-report.md
- 更新README
- 给出最终完成报告

==================================================
十五、最终完成标准
==================================================

只有同时满足以下条件，才算完成：

- 项目可以编译
- 单元测试可以运行
- Docker Compose可以启动
- Spring Boot页面可以访问
- PostgreSQL中有演示数据
- Syslog模拟可以产生事件
- SNMP Trap模拟可以产生事件
- TCP Ping可以产生结果
- 障害判定可以创建Alert
- 恢复事件可以关闭或恢复Alert
- 登录和权限可以工作
- README足够详细
- 各技术有独立说明文档
- 至少有10张真实运行截图
- README中实际显示这些截图
- OpenTofu代码完成fmt和validate
- Kubernetes YAML完整
- Windows和macOS运行方法完整
- 代码有详细且有意义的注释
- 不存在硬编码Secret
- 不存在空实现
- 不存在假截图
- 不存在声称成功但实际未验证的内容

==================================================
十六、最终回复格式
==================================================

工作完成后，请用中文报告：

1. 已完成内容
2. 项目目录
3. 核心架构
4. 主要页面
5. 主要技术栈
6. 测试结果
7. Docker运行结果
8. 截图生成结果
9. OpenTofu验证结果
10. Kubernetes配置结果
11. 已知限制
12. 运行命令
13. 重要文件位置
14. 后续建议

不要只告诉我代码已经生成。

请实际完成、运行、检查、修复并提供最终结果。