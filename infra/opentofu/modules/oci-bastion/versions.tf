# 约束 OCI Bastion 模块的 Provider 版本，确保会话 TTL 与目标子网参数保持兼容。
# 凭据和区域配置属于环境层，本文件不保存访问秘密。
terraform {
  required_version = ">= 1.8.0"
  required_providers {
    oci = {
      source = "oracle/oci", version = "~> 7.0"
    }
  }
}
