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
