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
  default = "bms-alert-function"
}
variable "subnet_ids" {
  type    = list(string)
  default = []
}
variable "image" {
  type    = string
  default = ""
}
variable "memory_in_mbs" {
  type    = number
  default = 512
}
variable "notification_topic_id" {
  type      = string
  default   = ""
  sensitive = true
}
