# 约束 OCI 网络模块依赖版本，避免 Provider 升级改变 VCN、安全列表或路由资源行为。
# 调用方负责 Provider 配置和认证，本模块只声明资源所需的最低能力。
terraform {
  required_version = ">= 1.8.0"
  required_providers {
    oci = {
      source = "oracle/oci", version = "~> 7.0"
    }
  }
}
