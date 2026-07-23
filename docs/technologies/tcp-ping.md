# TCP Ping
## 技术是什么
通过 Java Socket 在期限内连接目标 IP/主机与 TCP 端口，不是 ICMP ping。
## 为什么使用
能从业务路径验证 HTTPS、SSH 或专用服务端口是否真正接受连接。
## 项目位置
`TcpPingService`、结果实体/页面、调度器、API、AWS Lambda 与本地脚本。
## 主要概念
DNS 解析、connect timeout、connection refused、响应时间、重试和检查时间。
## 主要配置
目标 host/port、100-30000ms timeout、0-5 retry；结果和错误类型都持久化。
## 示例代码
`new Socket().connect(new InetSocketAddress(host, port), timeoutMillis)`，finally 关闭 socket。
## 常用命令
`scripts/run-tcp-ping.ps1 localhost 8080` 或对应 `.sh`。
## 常见错误
把 refused 当 timeout、无限重试、检查进程与用户路径不同、误认为成功握手代表应用层健康。
