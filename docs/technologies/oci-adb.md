# OCI Autonomous Database
## 技术是什么
Oracle 托管自治数据库，自动化补丁、备份和部分伸缩运维。
## 为什么使用
作为 OCI 生产持久层，承载事件、告警、审计与趋势数据。
## 项目位置
`modules/oci-adb`、Oracle JDBC/Flyway 依赖和 `application-oracle.yml`。
## 主要概念
ATP workload、private endpoint、mTLS、Wallet、service name、CPU/storage、auto scaling。
## 主要配置
私有 subnet+NSG、`is_mtls_connection_required=true`；Wallet 只读挂载并用 `TNS_ADMIN` 指向。
## 示例代码
JDBC URL `jdbc:oracle:thin:@service_high?TNS_ADMIN=/run/secrets/wallet` 从环境传入。
## 常用命令
应用启动前执行 Flyway validate/migrate；实际 ADB 创建/销毁必须审批和备份确认。
## 常见错误
提交 Wallet、service name 错、容器无文件权限、H2/PostgreSQL 方言 SQL 未在 Oracle staging 验证、误 destroy 数据。
