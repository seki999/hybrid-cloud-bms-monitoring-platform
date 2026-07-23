# 实施计划

本计划把 1,412 行需求转换为可独立验证的八个阶段。所有云资源默认关闭；只有显式变量启用后才会进入 `plan/apply`。

## 前提与边界

- 项目目录与可见名称固定为 `hybrid-cloud-bms-monitoring-platform`。
- Java 代码注释和技术文档使用中文，管理画面使用日语，代码标识符使用英文。
- 本地主要路径为 Java 21 + Maven Wrapper + Docker Compose PostgreSQL。
- `capture` Profile 只用于 Docker 不可用时的真实画面自动化验证，正式本地 Profile 仍使用 PostgreSQL。
- AWS/OCI 代码是可验证的基础设施模板；收费资源必须通过 `enable_*` 变量显式开启。
- Node.js 只作为 Playwright 截图工具，不构成独立前端工程。

## 阶段与验收条件

1. [x] 仓库分析、目录骨架、基础 README 与任务清单
   - 验证：Git 根目录存在，Maven Monorepo 模块可枚举。
2. [x] Spring Boot、Flyway、Entity、Repository、Service、Controller、Thymeleaf
   - 验证：`./mvnw test`；20 个日语页面的映射可访问。
3. [x] Syslog、SNMP、TCP Ping、Event、Alert Engine、通知
   - 验证：解析器、Socket、故障/恢复/重复抑制测试通过；模拟发送产生数据库记录。
4. [ ] Docker、PostgreSQL、MailHog、模拟器
   - 验证：`docker compose config --quiet`、镜像构建、容器健康检查、数据库演示数据计数。
   - 当前：Compose 配置解析通过；Docker Desktop 引擎未运行，镜像和容器健康检查待验证。
5. [ ] Kubernetes、kind、OKE 说明
   - 验证：`kubectl kustomize infra/kubernetes/overlays/kind` 与 server-side/dry-run 可用项。
   - 当前：Kustomize 离线渲染通过；本机缺少 kind，集群部署待验证。
6. [ ] AWS Lambda、OCI Functions、OpenTofu
   - 验证：Lambda/Function 单元测试，`tofu fmt -check`、`tofu init -backend=false`、`tofu validate`。
   - 当前：函数测试通过，Terraform 兼容验证通过；本机缺少 OpenTofu，不能标记完成。
7. [x] 文档、Mermaid、演示数据、实际启动、12 张真实截图
   - 验证：Playwright 固定 viewport 自动登录并逐路由截图；PNG 非空且 README 相对路径有效。
8. [ ] 全量验证与修复
   - 验证：`docs/validation-report.md` 逐项记录命令、时间、结果、失败原因和未验证边界。
   - 当前：验证报告已建立；Docker、kind、OpenTofu 的环境受限项解除后再完成最终验收。

## 任务清单

- [x] 领域模型明确区分 Event（一次观测）与 Alert（持续故障）。
- [x] RBAC 覆盖 ADMIN / OPERATOR / VIEWER，API 使用外部化密钥。
- [x] 设备、规则、事件、告警、通知、报表、状态、审计、用户页面完成。
- [x] RFC 3164 / RFC 5424、SNMP v2c Trap/GET、TCP Socket 检查完成。
- [x] 10 台设备与每类至少 20/30 条协议数据可一键生成。
- [x] 五个 Kubernetes 逻辑组件可独立部署和扩缩容。
- [x] 八个 OpenTofu 模块及四套环境目录齐备。
- [x] Windows 与 macOS/Linux 脚本成对提供。
- [x] README 40 章、独立技术文档、架构图、ER 图、运行截图齐备。
- [x] `.gitignore` 排除 Secret、Wallet、tfstate、构建缓存和浏览器依赖目录。
