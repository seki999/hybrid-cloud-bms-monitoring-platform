# 约束 OKE 模块使用的 OpenTofu 与 OCI Provider 版本，避免不同环境解析出不兼容的资源参数。
# 这里只声明能力边界，不配置凭据；认证信息由调用环境安全注入。
terraform {
  required_version = ">= 1.8.0"
  required_providers {
    oci = {
      source = "oracle/oci", version = "~> 7.0"
    }
  }
}
