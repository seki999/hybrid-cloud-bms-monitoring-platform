variable "enabled" {
  type    = bool
  default = false
}
variable "name" {
  type    = string
  default = "bms-tcp-ping"
}
variable "jar_path" {
  type    = string
  default = ""
}
variable "handler" {
  type    = string
  default = "com.example.bms.lambda.tcp.TcpPingLambdaHandler::handleRequest"
}
variable "subnet_ids" {
  type    = list(string)
  default = []
}
variable "security_group_ids" {
  type    = list(string)
  default = []
}
variable "spring_api_url" {
  type    = string
  default = ""
}
variable "spring_api_key" {
  type      = string
  default   = ""
  sensitive = true
}
