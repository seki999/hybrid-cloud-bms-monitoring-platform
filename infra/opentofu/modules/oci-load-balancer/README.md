# OCI Load Balancer module

NLB 面向 Syslog UDP/TCP 与 SNMP Trap UDP，避免七层代理改写协议；Flexible Load Balancer 面向 HTTPS Web/API、TLS 终止与 Ingress。监听器、后端集和证书应由环境层按实际 OKE 服务地址配置。两类资源都计费，必须显式 `enabled=true`；销毁前确认 DNS 已切走。
