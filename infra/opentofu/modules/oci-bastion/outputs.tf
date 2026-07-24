# 导出堡垒服务 ID，供受控会话创建流程使用；不输出任何短期会话凭据。
# 禁用模块时返回空值，使上层环境可以安全地条件引用。
output "bastion_id" {
  value = try(oci_bastion_bastion.this[0].id, null)
}
