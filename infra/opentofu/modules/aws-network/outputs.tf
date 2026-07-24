# 导出 VPC、子网和 Lambda 安全组标识，供函数与混合云路由层建立显式引用。
# 模块禁用时输出为空，避免条件资源的索引错误。
output "vpc_id" {
  value = try(aws_vpc.this[0].id, null)
}
output "public_subnet_ids" {
  value = values(aws_subnet.public)[*].id
}
output "private_subnet_ids" {
  value = values(aws_subnet.private)[*].id
}
output "lambda_security_group_id" {
  value = try(aws_security_group.lambda[0].id, null)
}
