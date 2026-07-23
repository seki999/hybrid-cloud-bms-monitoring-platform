output "nlb_id" {
  value = try(oci_network_load_balancer_network_load_balancer.protocol[0].id, null)
}
output "web_lb_id" {
  value = try(oci_load_balancer_load_balancer.web[0].id, null)
}
