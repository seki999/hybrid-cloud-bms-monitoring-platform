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
