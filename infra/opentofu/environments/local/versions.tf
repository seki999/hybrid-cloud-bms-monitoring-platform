# 本地环境仅约束 OpenTofu 版本，不配置任何云 Provider，确保离线即可执行格式和语法验证。
# 该环境是安全的学习与结构检查入口，不代表真实云资源计划。
terraform {
  required_version = ">= 1.8.0"
}
