# 定义 Autonomous Database 的容量、版本、网络和管理员秘密引用等输入契约。
# 敏感值标记可阻止常规输出泄露，但调用方仍需使用安全的状态存储。
variable "enabled" {
  type    = bool
  default = false
}
variable "compartment_id" {
  type      = string
  default   = ""
  sensitive = true
}
variable "db_name" {
  type    = string
  default = "BMSDB"
}
variable "display_name" {
  type    = string
  default = "bms-adb"
}
variable "admin_password" {
  type      = string
  default   = ""
  sensitive = true
}
variable "subnet_id" {
  type    = string
  default = ""
}
variable "nsg_ids" {
  type    = list(string)
  default = []
}
