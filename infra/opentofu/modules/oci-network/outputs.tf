# 导出 VCN 及公私子网 ID，供 OKE、Functions、ADB 和负载均衡模块复用同一网络边界。
# 输出保持资源引用关系，避免下游通过标签或名称进行脆弱的运行时查找。
output "vcn_id" {
  value = try(oci_core_vcn.this[0].id, null)
}
output "public_subnet_id" {
  value = try(oci_core_subnet.public[0].id, null)
}
output "private_subnet_id" {
  value = try(oci_core_subnet.private[0].id, null)
}
output "network_security_group_id" {
  value = try(oci_core_network_security_group.bms[0].id, null)
}
