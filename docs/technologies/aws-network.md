# AWS Network
## 技术是什么
VPC、子网、路由、安全组、NAT 与 VPN 组成 AWS 私有网络边界。
## 为什么使用
Lambda 需靠近 AWS/客户网设备，并安全到达 OCI HTTPS API。
## 项目位置
`infra/opentofu/modules/aws-network` 与混合云架构图。
## 主要概念
CIDR、公私有子网、AZ、IGW、NAT、SG stateful、Site-to-Site VPN、回程路由。
## 主要配置
示例 VPC `10.10.0.0/16`；公网 IP 默认不自动分配，Lambda SG 仅 egress。
## 示例代码
模块用 `for_each` 为 CIDR 列表创建子网，并输出给 Lambda VPC config。
## 常用命令
`tofu -chdir=infra/opentofu/environments/dev validate`；真实 plan/apply 必须审批。
## 常见错误
AWS/OCI CIDR 重叠、只有去程无回程、私有 Lambda 没 NAT/DNS、SG 与 NACL 混淆。
