# OCI Functions
## 技术是什么
基于 Fn Project 的 OCI 托管函数服务，镜像存储于 OCIR。
## 为什么使用
提供告警外部联动样例，使通知处理可按事件扩展而不耦合 Web 请求。
## 项目位置
`apps/alert-function` 与 `modules/oci-functions`。
## 主要概念
Application、Function、Invoke endpoint、OCIR、Dynamic Group、Policy、Logging、Notifications。
## 主要配置
私网 subnet、512MB、topic OCID 外部注入；Dynamic Group 只获使用指定 topic/Vault secret 的权限。
## 示例代码
函数接受 alert JSON，验证幂等 key 后构造通知，不记录敏感字段。
## 常用命令
`./mvnw -pl apps/alert-function test package`；Fn/OCI 部署仅在显式启用后执行。
## 常见错误
OCIR 区域/认证错误、应用无出网、Dynamic Group 规则不匹配、重复事件发送多封通知。
