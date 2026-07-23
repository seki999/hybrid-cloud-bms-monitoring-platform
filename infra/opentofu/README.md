# OpenTofu 基础设施

本目录提供八个可复用模块和 local/dev/staging/production 四级环境。所有 OCI/AWS 资源均以 `enabled=false` 为默认安全门；当前仓库不执行真实 `plan/apply/destroy`，因为这需要账户凭据且可能创建收费资源。

## 无云验证

```bash
tofu fmt -recursive -check
tofu -chdir=environments/local init -backend=false
tofu -chdir=environments/local validate
```

## 经审批的云执行

```bash
cd infra/opentofu/environments/dev
cp dev.tfvars.example dev.auto.tfvars   # 该文件被 Git 忽略
tofu init
tofu validate
tofu plan -var-file=dev.auto.tfvars -out=dev.tfplan
tofu apply dev.tfplan                    # 只有审批后才执行
tofu destroy -var-file=dev.auto.tfvars   # 再次审批并确认备份
```

## 核心概念

- Provider 连接 OCI/AWS API；Resource 声明资源；Data Source 读取已有对象。
- Variable 是输入，Local 是模块内计算值，Output 是稳定接口；敏感输入加 `sensitive=true` 但仍会进入 state。
- Module 封装重复设计；`count` 用于总开关，`for_each` 用于子网集合，`dynamic` 用于可选 VPC 配置。
- State 是基础设施映射，不是普通缓存；团队环境应放在加密远端 Backend，并使用服务端 State Lock。
- Dependency 由资源引用自动构建；`lifecycle` 可控制替换顺序，但不能用来掩盖错误依赖。
- 既有资源用 `import` 纳管；替代旧 `taint` 的方式是 `tofu apply -replace=address`。
- 重命名地址应使用 `moved` block，避免无意义 destroy/create。
- 环境以目录与独立 state 分离；tfvars 只放非敏感差异，Secret 由 Vault/CI 注入。
- Provider 使用兼容范围锁定并提交 `.terraform.lock.hcl`（在实际 init 的环境中生成）。

OpenTofu 源自 Terraform 1.5.x 后的开源分支，HCL 与常用工作流高度兼容，但许可证、发布节奏、部分新功能和 Provider 支持边界不同。迁移前应在隔离 state 副本上验证，不能假设双向完全兼容。

## 网络与费用

Syslog UDP/TCP 514 和 SNMP Trap UDP 162 需要四层 OCI NLB；Web/API HTTPS 443 使用 OCI Load Balancer 或 Kubernetes Ingress。SNMP GET UDP 161 从 AWS Lambda 主动发起。SSH TCP 22 不公网开放，只经 OCI Bastion/Smart Jumper。NAT Gateway、VPN、OKE 节点、Load Balancer、ADB、Functions 和日志均可能计费；README 中的销毁命令也属于破坏操作，必须确认目标 state 与数据备份。
