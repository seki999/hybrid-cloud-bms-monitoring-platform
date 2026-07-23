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
