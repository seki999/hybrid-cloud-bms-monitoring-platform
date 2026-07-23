resource "oci_bastion_bastion" "this" {
  count                        = var.enabled ? 1 : 0
  bastion_type                 = "STANDARD"
  compartment_id               = var.compartment_id
  name                         = var.name
  target_subnet_id             = var.target_subnet_id
  client_cidr_block_allow_list = var.client_cidr_block_allow_list
  max_session_ttl_in_seconds   = var.max_session_ttl_in_seconds
}
