# VPN 与路由
## 技术是什么
Site-to-Site VPN 在 AWS VPC 与 OCI VCN 间建立加密隧道，路由决定流量去向与回程。
## 为什么使用
Lambda 可能需访问客户私网设备，并把结果可靠传到 OCI；公网 HTTPS 是简化备选。
## 项目位置
架构图、AWS/OCI network module README；真实 VPN 资源因费用和外部参数不默认创建。
## 主要概念
Customer/Virtual Gateway、OCI DRG、IPsec、BGP/static route、NAT、asymmetric routing、MTU。
## 主要配置
AWS `10.10.0.0/16`、OCI `10.20.0.0/16` 不重叠；两端路由、SG/NSG 与防火墙都需允许 HTTPS 443 或监控协议。
## 示例代码
Lambda 经 VPN 调 OCI private LB；无 VPN 时经 NAT 调 public HTTPS，并用 TLS/API Key/mTLS 加固。
## 常用命令
使用各云 route table/flow log 只读检查；`traceroute` 仅作为辅助，最终以端口连接验证。
## 常见错误
只有一端路由、CIDR 重叠、BGP 前缀未发布、UDP NAT 超时、MTU 黑洞、把 SSH 22 暴露公网。
