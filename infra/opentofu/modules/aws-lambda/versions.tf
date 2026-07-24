# 约束 Lambda 模块使用的 AWS Provider 版本，保证运行时、IAM 和 VPC 配置字段稳定。
# 云端认证与区域由环境层配置，不在模块内保存凭据。
terraform {
  required_version = ">= 1.8.0"
  required_providers {
    aws = {
      source = "hashicorp/aws", version = "~> 6.0"
    }
  }
}
