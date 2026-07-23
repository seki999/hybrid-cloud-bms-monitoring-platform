# OCI VCN
## 技术是什么
OCI 的隔离虚拟网络，含子网、路由、Gateway、NSG 和 Security List。
## 为什么使用
把公网协议入口、私有 OKE/ADB、Bastion 和跨云 VPN 分层。
## 项目位置
`modules/oci-network`；输出被 OKE、LB、ADB、Functions 和 Bastion 使用。
## 主要概念
VCN CIDR、Regional Subnet、IGW/NAT/Service Gateway、DRG、NSG、Security List。
## 主要配置
公网子网放 LB/NLB，私网放 Node/ADB；默认路由分别指 IGW/NAT，生产补 Service Gateway。
## 示例代码
`route_rules` 的目标 `0.0.0.0/0` 指向 NAT，OCI services CIDR 指向 Service Gateway。
## 常用命令
`tofu validate` 检查引用；OCI Console Flow Logs 验证 5-tuple 丢包位置。
## 常见错误
缺回程路由、误给私网 VNIC 公网 IP、同时依赖 NSG/Security List 却漏规则、CIDR 与 AWS 重叠。
