# 定义 VCN、子网、可用域与命名输入，集中描述 OCI 网络地址规划的外部契约。
# enabled 让本地和单云环境能够解析配置而不触发 OCI 资源创建。
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
variable "vcn_cidr" {
  type    = string
  default = "10.20.0.0/16"
}
variable "public_subnet_cidr" {
  type    = string
  default = "10.20.10.0/24"
}
variable "private_subnet_cidr" {
  type    = string
  default = "10.20.20.0/24"
}
