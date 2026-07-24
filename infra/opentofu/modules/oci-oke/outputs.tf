# 导出后续 Ingress、运维自动化和环境汇总需要的 OKE 标识；模块关闭时返回空值。
# 调用方应把输出视为资源引用，不应从名称推导云端 ID。
output "cluster_id" {
  value = try(oci_containerengine_cluster.this[0].id, null)
}
output "node_pool_id" {
  value = try(oci_containerengine_node_pool.this[0].id, null)
}
