# AWS Lambda module

创建 Java 21 Lambda、最小执行角色与 EventBridge 定时触发。函数可在 VPC 中执行 SNMP GET/TCP Socket 检查，再通过跨云 VPN/NAT 或 HTTPS 443 调用 OCI Spring API。API Key 应来自 Secrets Manager/加密环境变量，日志不得输出 community 或 Key。Lambda、NAT Gateway 与日志可能计费，默认关闭；销毁使用 `tofu destroy`。
