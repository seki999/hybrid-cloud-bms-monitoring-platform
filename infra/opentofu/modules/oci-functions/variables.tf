# 定义 OCI Functions 应用所需网络、镜像与命名输入，并通过类型约束尽早发现环境配置错误。
# enabled 用于在尚未启用函数服务的环境中保留模块接口而跳过资源创建。
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
  default = "bms-alert-function"
}
variable "subnet_ids" {
  type    = list(string)
  default = []
}
variable "image" {
  type    = string
  default = ""
}
variable "memory_in_mbs" {
  type    = number
  default = 512
}
variable "notification_topic_id" {
  type      = string
  default   = ""
  sensitive = true
}
