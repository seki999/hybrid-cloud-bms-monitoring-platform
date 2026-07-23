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
