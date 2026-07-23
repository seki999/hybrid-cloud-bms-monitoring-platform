output "cluster_id" {
  value = try(oci_containerengine_cluster.this[0].id, null)
}
output "node_pool_id" {
  value = try(oci_containerengine_node_pool.this[0].id, null)
}
