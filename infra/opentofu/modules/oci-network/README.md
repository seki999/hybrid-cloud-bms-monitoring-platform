# OCI Network module

创建 VCN、公私有子网、Internet/NAT Gateway、路由表和 NSG。`enabled=false` 是费用安全门；示例 OCID 仅为占位符。生产设计还应为 Object Storage/ADB 增加 Service Gateway，并用 NSG/Security List 明确开放 UDP/TCP 514、UDP 161/162、TCP 443，SSH 22 只能来自 Bastion。执行前复制 `example.tfvars` 到 Git 忽略的文件；销毁使用 `tofu destroy -var-file=...`。
