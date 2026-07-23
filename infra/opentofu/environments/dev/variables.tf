variable "enable_oci" {
  type    = bool
  default = false
}
variable "enable_aws" {
  type    = bool
  default = false
}
variable "oci_region" {
  type    = string
  default = "ap-tokyo-1"
}
variable "aws_region" {
  type    = string
  default = "ap-northeast-1"
}
variable "oci_compartment_id" {
  type      = string
  default   = ""
  sensitive = true
}
variable "aws_availability_zones" {
  type    = list(string)
  default = ["ap-northeast-1a", "ap-northeast-1c"]
}
