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
