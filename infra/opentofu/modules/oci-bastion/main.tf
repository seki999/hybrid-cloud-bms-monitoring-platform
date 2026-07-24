# 在指定私有子网上创建 OCI Bastion，提供无需公开后端地址的受审计运维通道。
# 来源网段与会话时长由环境显式传入，避免模块隐藏安全策略。
resource "oci_bastion_bastion" "this" {
  count                        = var.enabled ? 1 : 0
  bastion_type                 = "STANDARD"
  compartment_id               = var.compartment_id
  name                         = var.name
  target_subnet_id             = var.target_subnet_id
  client_cidr_block_allow_list = var.client_cidr_block_allow_list
  max_session_ttl_in_seconds   = var.max_session_ttl_in_seconds
}
