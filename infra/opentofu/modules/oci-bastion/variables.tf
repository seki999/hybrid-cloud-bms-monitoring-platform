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
