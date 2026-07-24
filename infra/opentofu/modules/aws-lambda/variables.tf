# 定义 Lambda 包路径、处理器、运行时、网络和环境变量输入，集中表达函数部署契约。
# enabled 支持在未准备构建产物或 AWS 凭据时仅执行静态配置验证。
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
