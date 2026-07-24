# 固定 OCI Functions 模块的 OpenTofu 与 Provider 兼容范围，确保函数资源模式在 CI 与本地一致。
# 版本约束不包含认证信息，凭据必须由环境变量或外部配置提供。
terraform {
  required_version = ">= 1.8.0"
  required_providers {
    oci = {
      source = "oracle/oci", version = "~> 7.0"
    }
  }
}
