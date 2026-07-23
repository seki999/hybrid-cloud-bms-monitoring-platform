# OpenTofu
## 技术是什么
开源声明式 IaC 工具，读取 HCL 计算计划并通过 Provider 管理资源与 state。
## 为什么使用
把 OCI 主环境和 AWS 主动监控基础设施变成可评审、可复用、默认关闭的代码。
## 项目位置
`infra/opentofu` 八个 modules 与四个 environments。
## 主要概念
Provider/Resource/Data、Variable/Local/Output、Module、State/Lock、Dependency、Lifecycle、Import、moved、count/for_each/dynamic。
## 主要配置
Provider version pin，环境独立 state，Secret input sensitive；所有云 module `enabled=false`。
## 示例代码
`count = var.enabled ? 1 : 0` 是费用安全门；重建用 `tofu apply -replace=address` 而非旧 taint。
## 常用命令
依次 `tofu init`、`fmt -check`、`validate`、`plan -out`、经审批 `apply`；`destroy` 需二次审批。
## 常见错误
在错误 state/账号 apply、把 sensitive 当加密、提交 tfstate/tfvars、忽略 plan 中 replacement 与持续费用。
