# 开发环境统一约束 OpenTofu、AWS 与 OCI Provider 版本，保证混合云模块在同一依赖集合下解析。
# Provider 认证通过外部环境注入，仓库不存储长期云密钥。
terraform {
  required_version = ">= 1.8.0"
  required_providers {
    oci = {
      source = "oracle/oci", version = "~> 7.0"
    }
    aws = {
      source = "hashicorp/aws", version = "~> 6.0"
    }

  }
}
provider "oci" {
  region = var.oci_region
}
provider "aws" {
  region = var.aws_region
}
