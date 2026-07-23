output "database_id" {
  value     = try(oci_database_autonomous_database.this[0].id, null)
  sensitive = true
}
output "connection_strings" {
  value     = try(oci_database_autonomous_database.this[0].connection_strings, null)
  sensitive = true
}
