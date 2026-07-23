# NLB 保持 UDP/TCP 四层语义，承载 Syslog/Trap；LB 承载 Web/HTTPS 七层路由与 TLS。
resource "oci_network_load_balancer_network_load_balancer" "protocol" {
  count                          = var.enabled ? 1 : 0
  compartment_id                 = var.compartment_id
  display_name                   = "${var.name}-protocol-nlb"
  subnet_id                      = var.public_subnet_id
  is_private                     = false
  is_preserve_source_destination = false
}
resource "oci_load_balancer_load_balancer" "web" {
  count          = var.enabled ? 1 : 0
  compartment_id = var.compartment_id
  display_name   = "${var.name}-web-lb"
  shape          = "flexible"
  subnet_ids     = [var.public_subnet_id]
  shape_details {
    minimum_bandwidth_in_mbps = 10
    maximum_bandwidth_in_mbps = 100
  }
}
