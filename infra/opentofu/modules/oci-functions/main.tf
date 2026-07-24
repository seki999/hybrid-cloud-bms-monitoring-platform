# 在指定私有子网中创建 Functions 应用并部署容器镜像函数，使协议任务可按需无服务器执行。
# 资源关系使用显式输入连接网络层，模块本身不隐式创建共享基础设施。
resource "oci_functions_application" "this" {
  count          = var.enabled ? 1 : 0
  compartment_id = var.compartment_id
  display_name   = "${var.name}-app"
  subnet_ids     = var.subnet_ids
}
resource "oci_functions_function" "this" {
  count          = var.enabled ? 1 : 0
  application_id = oci_functions_application.this[0].id
  display_name   = var.name
  image          = var.image
  memory_in_mbs  = var.memory_in_mbs
  config = {
    NOTIFICATION_TOPIC_ID = var.notification_topic_id

  }
}
