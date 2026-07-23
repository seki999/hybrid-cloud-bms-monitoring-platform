# Docker 与 Compose
## 技术是什么
Docker 构建不可变 OCI 镜像，Compose 编排本地多容器依赖。
## 为什么使用
在 Windows/macOS 用相同 PostgreSQL、MailHog、SNMP agent、Spring app 和 Syslog simulator 复现环境。
## 项目位置
`infra/docker/bms-app.Dockerfile`、`docker-compose.yml`、`scripts/dev-up/down.*`。
## 主要概念
多阶段构建、layer cache、非 root、healthcheck、volume、network、depends_on health。
## 主要配置
构建阶段 Maven/Java21，运行阶段 JRE21+curl，用户 10001，Actuator readiness 健康检查。
## 示例代码
`docker compose config --quiet` 先做插值/结构验证，`docker compose up -d --build` 再启动。
## 常用命令
`docker compose ps`、`docker compose logs -f bms-app`、`docker compose down`（保留数据）。
## 常见错误
Docker Desktop engine 未启动、端口冲突、镜像内 localhost 指错服务、用 `down -v` 意外删除数据库。
