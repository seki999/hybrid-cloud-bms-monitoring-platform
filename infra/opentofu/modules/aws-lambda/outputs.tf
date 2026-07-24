# 导出函数 ARN 与名称，供事件源、权限策略和监控告警引用。
# 输出不包含环境变量中的秘密，避免状态消费者获得不必要的敏感信息。
output "function_arn" {
  value = try(aws_lambda_function.this[0].arn, null)
}
output "role_arn" {
  value = try(aws_iam_role.this[0].arn, null)
}
