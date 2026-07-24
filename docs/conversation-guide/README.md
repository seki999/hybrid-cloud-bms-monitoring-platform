# 双人对话式项目技术讲解

## 文档目的

本指南基于当前仓库的源码、配置、迁移、测试、容器、Kubernetes、OpenTofu 和 CI 文件生成。它用 The Curious Host 与 The Witty Expert 的双人对话，从零基础视角逐步进入架构、调用链和生产取舍。

文档不把项目名称当作功能证据，不把静态配置当作上线证明，也不会输出真实密码、密钥、Token 或连接字符串。

01–09 每章都包含与附近对话直接对应的 Mermaid 图和图表说明，共 45 张。图中节点使用当前仓库的真实类、方法、表、Compose 服务、Kubernetes 资源和 CI Job；生产建议统一标记为“当前仓库尚未完全实现”。这些图是 Markdown 代码块，仓库没有为本指南生成 PNG、JPG 或 SVG 文件。

## 推荐阅读顺序

1. [项目代码分析报告](00-project-analysis.md)
2. [项目全景对话](01-project-overview-dialogue.md)
3. [架构拆解对话](02-architecture-dialogue.md)
4. [请求调用链对话](03-request-flow-dialogue.md)
5. [数据与集成对话](04-data-and-integration-dialogue.md)
6. [基础设施对话](05-infrastructure-dialogue.md)
7. [安全与可观测性对话](06-security-and-observability-dialogue.md)
8. [测试与 CI/CD 对话](07-testing-and-cicd-dialogue.md)
9. [生产就绪对话](08-production-readiness-dialogue.md)
10. [面试表达对话](09-interview-explanation-dialogue.md)

## 每章简介

| 章节 | 内容 |
|---|---|
| `00-project-analysis.md` | 技术栈、目录、入口、调用链、已实现能力、缺口和风险证据清单 |
| `01-project-overview-dialogue.md` | 业务问题、用户、六个 Maven 模块和本地运行心智模型 |
| `02-architecture-dialogue.md` | 模块化单体、五角色镜像、协议 Adapter 和混合云边界 |
| `03-request-flow-dialogue.md` | API、设备表单、被动协议、事务和页面渲染的完整路径 |
| `04-data-and-integration-dialogue.md` | 11 张表、Flyway、JPA/JDBC、去重与四类监视输入 |
| `05-infrastructure-dialogue.md` | Docker、Compose、Kubernetes、Kustomize、kind 和 OpenTofu |
| `06-security-and-observability-dialogue.md` | 登录、RBAC、API Key、容器安全、日志、指标和健康 |
| `07-testing-and-cicd-dialogue.md` | 分层测试、loopback 回归、Testcontainers 与 CI 静态边界 |
| `08-production-readiness-dialogue.md` | SLO、容量、备份、故障、安全和上线缺口 |
| `09-interview-explanation-dialogue.md` | 3 分钟、10 分钟介绍和高频追问 |

## 适合的读者

- 第一次接触 Spring Boot、网络监视或服务端渲染的学习者。
- 需要定位 Controller、Service、Repository、迁移和模板关系的开发者。
- 需要审查容器、Kubernetes、OpenTofu、安全和运维边界的工程师。
- 准备以该项目进行技术面试说明的项目作者。

## 如何配合代码学习

1. 先打开本章“关键文件”表列出的入口。
2. 沿对话中的类名和方法名使用代码搜索定位。
3. 对照测试确认正常与失败分支。
4. 对照 `application.yml`、Compose 和 Kubernetes 环境变量，理解同一代码在不同角色下如何运行。
5. 只把亲自运行过的结果标为已验证；Docker、kind 或云环境不可用时保留“待验证”。

## 本地学习建议

Windows 可优先使用仓库中的 `.ps1` 脚本，Unix 环境使用对应 `.sh`。基础顺序是：

```powershell
.\mvnw.cmd clean verify
.\scripts\dev-up.ps1
.\scripts\run-local.ps1
```

具体参数、依赖与停止方式以根 `README.md` 和脚本内容为准。不要在不了解数据卷影响时执行破坏性清理，也不要对真实云环境运行 OpenTofu `apply/destroy`。

## 如何切换到英语练习模式

当前文件是中文模式。英语练习时，保留文件路径、类名、方法名、API 路径和资源名原样，将 Speaker 对话复述为英文；每次复述仍要区分：

- Confirmed by code
- Inferred from call relationships
- Not found in the repository
- Production recommendation, not current implementation

## 项目当前状态说明

- 可以从代码确认：主 Spring Boot 应用、Thymeleaf 页面、协议接收/检查、事件与告警领域、Flyway、测试、本地容器和部署配置均存在。
- 可以从配置确认：Kubernetes 采用五个逻辑角色，OpenTofu 提供 AWS/OCI 模块且云资源开关默认关闭。
- 不能从仓库确认：真实 AWS/OCI 资源已创建、生产集群已上线、性能目标已达成或容灾演练已完成。
- 本次 GitHub 检查没有发现开放的当前用户 PR 或近期 Issue，因此没有具体远端失败检查可供调试。
