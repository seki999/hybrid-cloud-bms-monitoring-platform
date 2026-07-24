# 定义 VPC、可用区和公私子网地址段，作为 AWS 网络拓扑的明确输入契约。
# enabled 允许在只验证 OCI 或本地环境时跳过 AWS 资源创建。
variable "enabled" {
  type    = bool
  default = false
}
variable "name" {
  type    = string
  default = "bms"
}
variable "vpc_cidr" {
  type    = string
  default = "10.10.0.0/16"
}
variable "availability_zones" {
  type    = list(string)
  default = []
}
variable "private_subnet_cidrs" {
  type    = list(string)
  default = []
}
variable "public_subnet_cidrs" {
  type    = list(string)
  default = []
}
