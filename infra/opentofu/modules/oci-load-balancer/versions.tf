# 固定 OCI Network Load Balancer 模块的工具与 Provider 版本，保证监听器和后端集资源语义稳定。
# Provider 的区域与认证由环境层统一配置，模块不持有任何密钥。
terraform {
  required_version = ">= 1.8.0"
  required_providers {
    oci = {
      source = "oracle/oci", version = "~> 7.0"
    }
  }
}
