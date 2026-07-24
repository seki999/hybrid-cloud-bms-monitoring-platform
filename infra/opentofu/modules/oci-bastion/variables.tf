# 定义堡垒服务的目标子网、允许来源和会话时长，集中控制受管运维入口。
# enabled 支持按环境关闭运维入口，避免开发配置强制创建公网管理面。
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
  default = "bms-bastion"
}
variable "target_subnet_id" {
  type    = string
  default = ""
}
variable "client_cidr_block_allow_list" {
  type    = list(string)
  default = []
}
variable "max_session_ttl_in_seconds" {
  type    = number
  default = 10800
}
