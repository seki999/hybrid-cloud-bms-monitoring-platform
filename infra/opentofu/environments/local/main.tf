# 本地环境只验证 OpenTofu 工具链，不创建云资源，也不需要任何凭据。
locals {
  project     = "hybrid-cloud-bms-monitoring-platform"
  environment = "local"
}
