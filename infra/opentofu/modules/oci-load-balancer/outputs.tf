# 导出 NLB 标识和分配地址，供 DNS、监控和环境级文档引用。
# 模块关闭时返回空结果，避免未创建资源仍产生无效索引访问。
output "nlb_id" {
  value = try(oci_network_load_balancer_network_load_balancer.protocol[0].id, null)
}
output "web_lb_id" {
  value = try(oci_load_balancer_load_balancer.web[0].id, null)
}
