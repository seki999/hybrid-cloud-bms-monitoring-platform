# Kubernetes / kind

`base` 包含 OCI OKE 与本地 kind 共用的声明，`overlays/kind` 只缩减 Web 副本数并关闭本地域名的 HTTPS 强制跳转。五个 Java 工作负载使用同一镜像但设置不同组件角色，分别承担 Web、Syslog、Trap、主动监控和告警检查。

## 本地执行

1. 构建镜像：`docker build -f infra/docker/bms-app.Dockerfile -t hybrid-cloud-bms-monitoring-platform:local .`
2. 创建集群：`scripts/kind-up.ps1` 或 `scripts/kind-up.sh`
3. 端口转发：`kubectl -n bms-monitoring port-forward svc/bms-web-app 8088:80`
4. 打开 `http://localhost:8088`。

示例 Secret 只包含占位值。实际 OKE 环境应通过 OCI Vault 与 External Secrets 注入，禁止提交真实密码、API Key、SNMP community 或 Autonomous Database Wallet。

## 生产边界

- OKE 中 PostgreSQL 仅作学习路径；生产应切换到 Autonomous Database profile。
- LoadBalancer/Ingress 类型需要 OKE 控制器和对应子网权限；kind 主要使用 port-forward。
- HPA 依赖 metrics-server；PDB 在单节点 kind 中仅用于验证清单结构。
