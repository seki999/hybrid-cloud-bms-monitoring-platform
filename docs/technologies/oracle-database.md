# Oracle Database / ADB
## 技术是什么
Oracle 关系数据库；ADB 是 OCI 托管形态，支持服务名、Wallet/mTLS 与自治运维。
## 为什么使用
目标生产环境位于 OCI，需提供从 PostgreSQL 学习环境切换到 ADB 的清晰边界。
## 项目位置
Oracle JDBC/Flyway 依赖、`application-oracle.yml`、`modules/oci-adb` 与数据库文档。
## 主要概念
service name、schema/user、sequence/identity、NUMBER/VARCHAR2/TIMESTAMP、Wallet、TNS_ADMIN。
## 主要配置
JDBC URL/用户/密码/Wallet 全部外部注入；private endpoint、NSG 和 mTLS 必须开启。
## 示例代码
Profile 仅切换 DataSource/dialect，业务 Repository 与 Controller 不包含 Oracle 分支。
## 常用命令
在 staging 运行 `./mvnw ...` 启动前 Flyway 自动 validate/migrate；Wallet 权限只读。
## 常见错误
把 PostgreSQL 特有 SQL 带到 Oracle、对象名大小写/长度差异、Wallet 过期、连接服务别名错、将密码置于命令历史。
