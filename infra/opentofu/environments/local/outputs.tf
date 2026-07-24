# 输出本地验证边界，明确该环境不会创建 AWS 或 OCI 资源。
# 该提示防止语法校验通过被误解为云端部署已经验证。
output "validation_message" {
  value = "${local.project}:${local.environment}:no-cloud-resources"
}
