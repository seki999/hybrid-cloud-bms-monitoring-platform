# 定义 OKE 集群模块的输入契约，包括网络归属、Kubernetes 版本和节点池容量。
# enabled 开关允许在无 OCI 凭据的本地校验中保持资源图可解析而不创建云资源。
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
  default = "bms-oke"
}
variable "vcn_id" {
  type    = string
  default = ""
}
variable "cluster_subnet_id" {
  type    = string
  default = ""
}
variable "node_subnet_id" {
  type    = string
  default = ""
}
variable "kubernetes_version" {
  type    = string
  default = "v1.32.1"
}
variable "node_shape" {
  type    = string
  default = "VM.Standard.E4.Flex"
}
variable "node_image_id" {
  type    = string
  default = ""
}
variable "node_count" {
  type    = number
  default = 1
}
variable "availability_domain" {
  type    = string
  default = ""
}
