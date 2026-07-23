# AWS Lambda
## 技术是什么
按调用计费的函数运行环境，本项目使用 Java 21 handler 执行主动监控。
## 为什么使用
EventBridge 可按计划从 AWS 网络位置执行 SNMP GET/TCP connect，并把结果传到 OCI。
## 项目位置
`apps/snmp-get-lambda`、`apps/tcp-ping-lambda`、payload 与 `modules/aws-lambda`。
## 主要概念
handler、冷启动、timeout、VPC ENI、执行角色、EventBridge、CloudWatch Logs。
## 主要配置
API URL/Key 由 Secret 注入；VPC 子网需有目标与 OCI HTTPS 回程，角色只保留日志权限。
## 示例代码
输入 `{host,port,timeoutMillis,retries}`，输出 success/latency/errorType，再 HTTPS POST ingestion API。
## 常用命令
`./mvnw -pl apps/tcp-ping-lambda test package`；云创建默认关闭且需审批。
## 常见错误
NAT/路由缺失、超时小于重试总时长、把 Secret 记录到日志、Lambda ENI 导致 destroy 等待。
