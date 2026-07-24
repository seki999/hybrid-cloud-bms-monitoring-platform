# 导出数据库与连接端点标识，供应用配置和运维集成建立依赖。
# 管理员密码等秘密不会通过输出传播，避免进入日志或普通流水线变量。
output "database_id" {
  value     = try(oci_database_autonomous_database.this[0].id, null)
  sensitive = true
}
output "connection_strings" {
  value     = try(oci_database_autonomous_database.this[0].connection_strings, null)
  sensitive = true
}
