# PostgreSQL
## 技术是什么
开源关系数据库，支持事务、索引、约束与丰富 SQL。
## 为什么使用
本地零云费用、Docker/Testcontainers 易复现，并与 JPA/Flyway 形成真实集成测试。
## 项目位置
Compose `postgresql`、`application-postgresql.yml`、Flyway migrations、Repository tests。
## 主要概念
ACID、schema、sequence/identity、index、transaction isolation、connection pool、backup。
## 主要配置
数据库名/用户/密码来自环境，Hikari 连接；JPA validate，Flyway 管结构。
## 示例代码
索引 `(source, occurred_at)` 支持按协议历史倒序查询，活动告警按 status/lastOccurredAt 查询。
## 常用命令
`docker compose exec postgresql psql -U bms_user -d bms_monitoring`；测试由 Testcontainers 临时创建。
## 常见错误
把容器端口与主机端口混淆、时区不统一、没有索引的趋势查询、在生产用默认密码、直接改已应用 migration。
