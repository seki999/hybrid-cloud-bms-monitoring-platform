output "vcn_id" {
  value = try(oci_core_vcn.this[0].id, null)
}
output "public_subnet_id" {
  value = try(oci_core_subnet.public[0].id, null)
}
output "private_subnet_id" {
  value = try(oci_core_subnet.private[0].id, null)
}
output "network_security_group_id" {
  value = try(oci_core_network_security_group.bms[0].id, null)
}
