output "application_id" {
  value = try(oci_functions_application.this[0].id, null)
}
output "function_id" {
  value = try(oci_functions_function.this[0].id, null)
}
