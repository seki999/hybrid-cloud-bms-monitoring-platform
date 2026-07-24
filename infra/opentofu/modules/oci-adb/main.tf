# 创建私有 Autonomous Database，并通过传入子网与访问控制限制数据库暴露面。
# 生命周期参数由环境配置决定，模块不自动放宽网络或备份保护策略。
resource "oci_database_autonomous_database" "this" {
  count                       = var.enabled ? 1 : 0
  compartment_id              = var.compartment_id
  db_name                     = var.db_name
  display_name                = var.display_name
  admin_password              = var.admin_password
  db_workload                 = "OLTP"
  is_auto_scaling_enabled     = false
  is_free_tier                = false
  data_storage_size_in_tbs    = 1
  cpu_core_count              = 1
  subnet_id                   = var.subnet_id
  nsg_ids                     = var.nsg_ids
  is_mtls_connection_required = true
}
