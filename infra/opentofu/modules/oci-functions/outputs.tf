# 导出函数应用与函数本体标识，供 API 网关、监控或部署流水线建立显式依赖。
# 模块禁用时输出为空，调用方必须先依据启用状态判断是否可使用。
output "application_id" {
  value = try(oci_functions_application.this[0].id, null)
}
output "function_id" {
  value = try(oci_functions_function.this[0].id, null)
}
