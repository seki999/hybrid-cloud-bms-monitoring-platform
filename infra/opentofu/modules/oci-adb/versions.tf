# 固定 Autonomous Database 模块的 OpenTofu 与 OCI Provider 兼容范围，降低数据库参数漂移风险。
# 认证配置从环境继承，不允许将云凭据写入模块代码。
terraform {
  required_version = ">= 1.8.0"
  required_providers {
    oci = {
      source = "oracle/oci", version = "~> 7.0"
    }
  }
}
