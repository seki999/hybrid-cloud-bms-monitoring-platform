# Terraform 与 OpenTofu 比较
## 技术是什么
两者共享 Terraform 1.5.x 之前的 HCL/工作流基础，之后由不同社区、许可和发布路线维护。
## 为什么使用
本项目以 OpenTofu 为主，同时让熟悉 Terraform 的工程师理解迁移边界。
## 项目位置
所有 `.tf` 以 OpenTofu 验证；README 不声称在 Terraform 上已验证。
## 主要概念
语言兼容、Provider protocol、state format、registry、backend、license 与 feature divergence。
## 主要配置
使用标准 HCL 与公开 Provider source，锁定兼容范围和 lockfile，避免未经验证的单方特性。
## 示例代码
同一 `required_providers` 通常可用，但迁移必须复制/备份 state 并在隔离环境重新 plan。
## 常用命令
OpenTofu: `tofu validate`; Terraform: `terraform validate`，结果必须分别记录。
## 常见错误
假设永远双向兼容、两种 CLI 交替写同一 state、遗漏 backend/plugin 版本差异、许可证评估不足。
