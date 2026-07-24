# 汇总各模块关键资源 ID，便于部署后接入应用配置和运维检查。
# 未启用的云平台返回空值，调用自动化必须显式处理这一状态。
output "oci_vcn_id" {
  value = module.oci_network.vcn_id
}
output "aws_vpc_id" {
  value = module.aws_network.vpc_id
}
