output "bastion_id" {
  value = try(oci_bastion_bastion.this[0].id, null)
}
