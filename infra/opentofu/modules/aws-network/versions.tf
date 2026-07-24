# 固定 AWS 网络模块所需的 OpenTofu 与 AWS Provider 版本，避免资源模式升级造成不可预期差异。
# Provider 区域与认证由调用环境注入，模块不包含访问密钥。
terraform {
  required_version = ">= 1.8.0"
  required_providers {
    aws = {
      source = "hashicorp/aws", version = "~> 6.0"
    }
  }
}
