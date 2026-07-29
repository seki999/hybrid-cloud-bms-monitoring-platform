# 变更日志

## 1.0.0 - 2026-07-23

- 创建 Java 25 / Spring Boot 3.5 混合云 BMS 监视平台。
- 实现协议接收、事件标准化、告警生命周期、日语 SSR 管理画面与安全控制。
- 加入 Docker Compose、Kubernetes/kind、AWS Lambda、OCI Functions 与 OpenTofu 学习资产。
- 加入自动测试、演示数据、Playwright 真实截图和逐项验证报告。
- 修复 SNMP Trap 接收器绑定到单一网卡、导致 localhost 模拟数据无法到达的问题，并增加回环接收回归测试。
- 增加站点图标，避免正常浏览页面时把缺失 `/favicon.ico` 记录为未处理异常。
- 新增云端通信与容器结构设计书，通过 19 张 Mermaid 图说明 AWS/OCI 通信、NLB/LB、五类 OKE Pod、端口转换、NetworkPolicy、高可用和故障排查。
