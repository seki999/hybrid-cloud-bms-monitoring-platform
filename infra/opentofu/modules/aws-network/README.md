# AWS Network module

创建 VPC、公私有子网、Internet Gateway 与 Lambda egress 安全组。跨云生产路径可增加 Virtual Private Gateway/Customer Gateway、Site-to-Site VPN 和双路由；学习路径可经 NAT 通过 OCI HTTPS 443 API。NAT/VPN 会持续计费，故未在默认模块中创建。所有资源默认关闭；变更路由前须验证回程路径，销毁时先解除 Lambda ENI。
