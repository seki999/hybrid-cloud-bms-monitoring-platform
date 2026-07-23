# 故障排查
## 技术是什么
基于分层证据定位应用、协议、数据库、容器、Kubernetes 与云网络问题的方法。
## 为什么使用
混合云监控故障常跨多个边界，猜测式重启会丢失原因和扩大影响。
## 项目位置
Actuator、结构化日志、request/correlation ID、系统状态页、runbook 与验证报告。
## 主要概念
复现、时间线、影响范围、健康/就绪差异、五元组、依赖图、回滚与事后分析。
## 主要配置
日志不含 Secret；readiness 控制流量，liveness 只判断不可恢复卡死；保留原始协议数据。
## 示例代码
先 `docker compose ps`，再 health endpoint，再服务日志，再数据库/端口探测，每步记录时间与结果。
## 常用命令
`curl /actuator/health/readiness`、`docker compose logs`、`kubectl describe pod`、`kubectl logs --previous`。
## 常见错误
先删容器/volume、把 UDP 无响应当应用 bug、忽略 DNS/回程路由、只看最新日志、将未执行检查报告为通过。
