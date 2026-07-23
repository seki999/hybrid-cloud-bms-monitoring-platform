# OCI 总览
## 技术是什么
Oracle Cloud Infrastructure 提供 IAM、VCN、OKE、LB/NLB、ADB、Functions、Logging、Monitoring、Notifications、Vault 与 Bastion。
## 为什么使用
本系统主运行环境是 OCI，并利用 ADB/OKE 与四层网络服务承接监控数据。
## 项目位置
六个 OCI OpenTofu module、Kubernetes 清单和 `oracle` profile。
## 主要概念
Tenancy、Compartment、Region/AD/FD、IAM Group/Policy、Dynamic Group、OCID。
## 主要配置
所有 OCID/密钥路径外部输入，所有收费资源默认 `enabled=false`。
## 示例代码
Policy 可让 OKE dynamic group 在指定 Compartment 使用网络和负载均衡器，范围不可写成 tenancy-wide 通配。
## 常用命令
`oci iam compartment list` 只读确认范围；IaC 用 `tofu plan` 审核后才 apply。
## 常见错误
在错误 Compartment/Region 创建资源、宽泛 IAM Policy、提交 API key/PEM、忽略 service limit 与费用。
