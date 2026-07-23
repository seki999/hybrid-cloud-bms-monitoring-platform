# Syslog
## 技术是什么
网络设备通过 UDP/TCP 发送事件的日志协议；本项目解析 RFC3164/5424 基础格式。
## 为什么使用
路由器、VPN 与通信设备普遍输出 Syslog，适合被动采集链路/系统事件。
## 项目位置
`protocol/syslog` 接收器和解析器、`apps/syslog-simulator`、历史页面。
## 主要概念
PRI=`facility*8+severity`、timestamp、hostname、app/tag、原文、UDP 无连接与 TCP 行分隔。
## 主要配置
本地默认 UDP/TCP 5514；生产可改 514，并通过 NLB/NSG 限制来源。
## 示例代码
`<132>1 2026-07-23T10:00:00+09:00 edge-01 bms 100 LINK - interface down`。
## 常用命令
`scripts/send-syslog.ps1 udp rfc5424`；Docker 查看 `bms-app` 日志确认接收。
## 常见错误
端口被占用、防火墙丢 UDP、错误 PRI、旧 RFC3164 缺年份/时区、把重复消息创建为多个活动告警。
