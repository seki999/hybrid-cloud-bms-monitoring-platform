# Java 21 与 Spring Boot
## 技术是什么
Java 21 LTS 与 Spring Boot 3.x 提供 JVM、依赖自动配置、嵌入式服务器和可观测端点。
## 为什么使用
适合长期企业系统，也能以一个可执行 JAR 统一本地、容器和 OKE 运行方式。
## 项目位置
根 `pom.xml` 管理六个 Maven module；入口是 `BmsApplication`。
## 主要概念
依赖注入、Bean 生命周期、Profile、ConfigurationProperties、事务边界和 Actuator。
## 主要配置
`application.yml` 定义共通项，`local/postgresql/oracle/capture` profile 隔离环境差异。
## 示例代码
`@Service @Transactional public Device create(...)` 将写入和审计置于同一事务。
## 常用命令
`./mvnw clean verify` 完整验证；`./mvnw -pl app/bms-app spring-boot:run` 启动应用。
## 常见错误
JDK 不是 21、激活错误 profile、把环境 Secret 写入 YAML、在 Controller 中绕过事务服务。
