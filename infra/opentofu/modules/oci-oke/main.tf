resource "oci_containerengine_cluster" "this" {
  count              = var.enabled ? 1 : 0
  compartment_id     = var.compartment_id
  kubernetes_version = var.kubernetes_version
  name               = var.name
  vcn_id             = var.vcn_id
  endpoint_config {
    is_public_ip_enabled = false
    subnet_id            = var.cluster_subnet_id
  }
  options {
    service_lb_subnet_ids = [var.cluster_subnet_id]
  }
}
resource "oci_containerengine_node_pool" "this" {
  count              = var.enabled ? 1 : 0
  cluster_id         = oci_containerengine_cluster.this[0].id
  compartment_id     = var.compartment_id
  kubernetes_version = var.kubernetes_version
  name               = "${var.name}-pool"
  node_shape         = var.node_shape
  node_shape_config {
    memory_in_gbs = 16
    ocpus         = 2
  }
  node_source_details {
    image_id    = var.node_image_id
    source_type = "IMAGE"
  }
  node_config_details {
    size = var.node_count
    placement_configs {
      availability_domain = var.availability_domain
      subnet_id           = var.node_subnet_id
    }

  }
}
