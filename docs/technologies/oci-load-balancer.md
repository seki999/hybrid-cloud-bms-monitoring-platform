# OCI Load Balancer 与 NLB
## 技术是什么
NLB 处理四层 UDP/TCP，Load Balancer 处理 HTTP/HTTPS 七层路由和 TLS。
## 为什么使用
Syslog/Trap 不能经过仅 HTTP 的 Ingress，而 Web/API 需要 TLS、Host/Path 路由与健康检查。
## 项目位置
`modules/oci-load-balancer`、Kubernetes protocol Services 与 Web Ingress。
## 主要概念
Listener、backend set、health check、source IP、TLS certificate、public/private frontend。
## 主要配置
NLB 开 UDP/TCP 514 与 UDP 162（本地映射 5514/1162）；LB 开 HTTPS 443。
## 示例代码
`oci_network_load_balancer_network_load_balancer` 与 `oci_load_balancer_load_balancer` 分开创建。
## 常用命令
`kubectl get svc,ingress -n bms-monitoring`；云端检查 backend health。
## 常见错误
用 HTTP LB 接 UDP、health port 不通、源地址保存误配、证书链不完整、创建后忘记费用。
