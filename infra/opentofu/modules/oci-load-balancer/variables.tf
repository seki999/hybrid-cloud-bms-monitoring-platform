# 定义网络负载均衡器的子网、监听端口、后端目标和健康检查参数。
# 输入保持协议层可配置，以同时承载 Syslog、SNMP 等不同接入流量。
variable "enabled" {
  type    = bool
  default = false
}
variable "compartment_id" {
  type      = string
  default   = ""
  sensitive = true
}
variable "name" {
  type    = string
  default = "bms"
}
variable "public_subnet_id" {
  type    = string
  default = ""
}
variable "private_subnet_id" {
  type    = string
  default = ""
}
