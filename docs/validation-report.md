# 验证报告

验证时间：2026-07-23（Asia/Tokyo）  
项目：`hybrid-cloud-bms-monitoring-platform`

## 结论

Java 构建、可运行应用、20 个日语页面、Syslog、SNMP Trap、TCP Ping、截图、Docker Compose 配置、Kubernetes 离线渲染和 Terraform 兼容语法验证均已通过。

当前不能把整个项目标记为“全部环境验证完成”：Docker Desktop 引擎未运行，且本机未安装 OpenTofu 与 kind。因此 PostgreSQL Testcontainers、Compose 容器健康、kind 集群和 OpenTofu CLI 本身仍属于未验证边界。未执行任何云端 `plan`、`apply` 或 `destroy`。

## 验证环境

| 项目 | 结果 |
|---|---|
| OS | Windows / PowerShell |
| Java | Eclipse Temurin 21.0.11 |
| Maven | Maven Wrapper，正常 |
| Docker CLI | 已安装 |
| Docker Engine | 未运行，无法连接 Docker Desktop Linux Engine |
| kubectl | v1.36.1，Kustomize v5.8.1 |
| kind | 未安装 |
| OpenTofu | 未安装 |
| Terraform | 已安装，仅用于 HCL/provider 兼容验证 |
| Node.js / Playwright | 已安装；仅用于截图证据，不是前端构建 |

## Java 构建与自动测试

命令：

```powershell
.\mvnw.cmd verify
```

结果：`BUILD SUCCESS`，7 个 Reactor 模块全部成功。

- `bms-app`：21 个测试，20 个通过，1 个跳过。
- 跳过项：`PostgresqlContainerTest`，原因是 Docker 引擎未运行。
- `snmp-get-lambda`：1 个通过。
- `tcp-ping-lambda`：1 个通过。
- `alert-function`：1 个通过。
- 总计：24 个测试，23 个通过，1 个因环境条件跳过，0 失败，0 错误。
- 新增 `SnmpTrapReceiverTest` 验证 IPv4 loopback Trap 可被通配地址接收器处理。

JaCoCo 报告生成于：

```text
app/bms-app/target/site/jacoco/index.html
```

## 应用启动与页面

验证用启动命令：

```powershell
java -jar app\bms-app\target\bms-app-1.0.0-SNAPSHOT.jar --spring.profiles.active=capture
```

`capture` Profile 使用临时 H2 PostgreSQL 兼容模式，只用于 Docker 引擎不可用时的页面和协议验证；正式本地路径仍是 Docker Compose PostgreSQL。

应用启动成功，Tomcat 监听 `8080`，Syslog 监听 UDP/TCP `5514`，SNMP Trap 监听通配 UDP `1162`。浏览器实际登录后逐条访问以下 20 个页面，均显示预期标题/主内容且未进入错误页：

1. 登录
2. ダッシュボード
3. 監視対象機器一覧
4. 監視対象機器詳細
5. 監視対象機器登録・編集
6. 監視ルール設定
7. イベント一覧
8. イベント詳細
9. アラート一覧
10. アラート詳細
11. アラート確認
12. Syslog受信履歴
13. SNMP Trap受信履歴
14. SNMP GET結果一覧
15. TCP Ping結果一覧
16. 通知先設定
17. 履歴トレンドレポート
18. システム稼働状況
19. 監査ログ
20. ユーザー管理

## 协议端到端验证

执行：

```powershell
.\scripts\send-syslog.ps1 -Transport udp -Format rfc3164 -Message 'continuation-validation-rfc3164'
.\scripts\send-syslog.ps1 -Transport tcp -Format rfc5424 -Message 'continuation-validation-rfc5424'
.\scripts\send-snmp-trap.ps1 -State linkDown
.\scripts\send-snmp-trap.ps1 -State linkUp
.\scripts\run-tcp-ping.ps1 -HostName localhost -Port 8080
```

结果：

- RFC 3164 UDP 消息在事件页面可检索。
- RFC 5424 TCP 消息在事件页面可检索。
- TCP Ping API 返回 `success=true`、`responseMillis=6`，事件页面出现 `TCP接続成功`。
- SNMP Trap 修复前模拟器可发送但接收计数不增加；定位为接收器绑定到单一网卡地址。
- 改为 `0.0.0.0/1162` 通配监听并重启后，发送 `linkDown` 与 `linkUp`，仪表盘 SNMP Trap 计数从 20 增加到 22。
- 故障、恢复和重复抑制还由 `AlertServiceTest`、`SnmpTrapParserTest` 与演示数据页面共同覆盖。

## Docker

命令：

```powershell
docker compose config --quiet
```

结果：通过。Compose 服务、变量插值、端口、依赖和健康检查语法有效。

未运行：

```powershell
docker compose build
docker compose up -d
```

原因：Docker Desktop Linux Engine 未运行。因此不能确认本次环境中的镜像构建、PostgreSQL 容器、MailHog、SNMP agent、Syslog simulator 和容器健康状态。`docker compose config` 不等同于容器运行成功。

## Kubernetes

命令：

```powershell
kubectl kustomize infra/kubernetes/overlays/kind
```

结果：通过，离线渲染 879 行 YAML。渲染内容包含 Namespace、五个业务 Deployment、Service、ConfigMap、Secret 示例、ServiceAccount/RBAC、PVC、Ingress、NetworkPolicy、HPA、PDB、三类探针和资源 requests/limits。

未运行 kind 集群创建、镜像 load、`kubectl apply` 和 rollout：本机未安装 kind，Docker 引擎也未运行。

## OpenTofu / Terraform 兼容验证

OpenTofu CLI 未安装，所以未声称 `tofu fmt/init/validate` 已通过。

本次使用 Terraform 做了兼容性检查：

```powershell
terraform fmt -check -recursive infra\opentofu
terraform -chdir=infra/opentofu/environments/local init -backend=false -input=false
terraform -chdir=infra/opentofu/environments/local validate
terraform -chdir=infra/opentofu/environments/dev init -backend=false -input=false
terraform -chdir=infra/opentofu/environments/dev validate
```

结果：格式检查、local 和 dev 的初始化/验证全部通过。8 个模块均具备 `main.tf`、`variables.tf`、`outputs.tf`、`versions.tf`、`README.md` 和 `example.tfvars`；四套环境目录存在。

边界：

- Terraform 验证是 HCL/provider 兼容证据，不等同于 OpenTofu CLI 验证。
- 未执行 `plan`、`apply` 或 `destroy`。
- 未连接 AWS/OCI 账号，未创建收费资源。

## 截图与 README

- `docs/screenshots` 中有 12 张 PNG。
- 12 张均为 1440 像素宽，文件非空；高度为 960 至 1558 像素。
- README 有 12 个截图引用，所有相对路径存在。
- 已人工检查 Dashboard 截图，内容为真实日语应用页面与演示数据，不是占位图。

## 安全与仓库卫生

- 未发现 AWS Access Key、私钥或真实 OCI OCID。
- 搜索命中的口令均为环境变量引用、本地学习默认值或明确的 `replace-me` 示例。
- `.gitignore` 排除 `.env`、Wallet、私钥、tfstate、`.terraform`、`target`、`node_modules` 和浏览器测试输出。
- 云资源默认由 `enable_*` 变量关闭。

## 待完成的环境验证

1. 启动 Docker Desktop 后执行 `.\mvnw.cmd verify`，确认 Testcontainers PostgreSQL 测试不再跳过。
2. 执行 `docker compose build`、`docker compose up -d`，检查五个服务、健康状态、PostgreSQL 演示数据和 MailHog。
3. 安装 kind 后执行 `scripts/kind-up.ps1`，检查 Deployment rollout、Ingress/port-forward 与 UDP Service。
4. 安装 OpenTofu 后执行 `tofu fmt -check`、`tofu init -backend=false`、`tofu validate`。
5. 只有在明确授权并接受费用风险后，才可在隔离账号中执行云端 `plan/apply/destroy`。
