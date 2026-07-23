resource "oci_functions_application" "this" {
  count          = var.enabled ? 1 : 0
  compartment_id = var.compartment_id
  display_name   = "${var.name}-app"
  subnet_ids     = var.subnet_ids
}
resource "oci_functions_function" "this" {
  count          = var.enabled ? 1 : 0
  application_id = oci_functions_application.this[0].id
  display_name   = var.name
  image          = var.image
  memory_in_mbs  = var.memory_in_mbs
  config = {
    NOTIFICATION_TOPIC_ID = var.notification_topic_id

  }
}
