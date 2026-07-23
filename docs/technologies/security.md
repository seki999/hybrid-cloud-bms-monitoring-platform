# 安全
## 技术是什么
身份、授权、输入、Secret、网络、容器、审计和供应链的纵深防御。
## 为什么使用
监控平台持有设备地址与凭据，并暴露多协议入口，攻击面跨浏览器、网络设备和云 API。
## 项目位置
`SecurityConfig`、API Key/filter、审计实体、Docker/Kubernetes/OpenTofu 安全配置与 threat model。
## 主要概念
RBAC、CSRF/XSS/CSP、BCrypt、least privilege、Secret rotation、mTLS、non-root、deny-by-default。
## 主要配置
ADMIN/OPERATOR/VIEWER；生产密码/Key/community/Wallet 从 Vault 注入，日志只带 request/correlation ID。
## 示例代码
`MessageDigest.isEqual` 常量时间比较 API Key；写操作保持 CSRF；Kubernetes drop ALL capabilities。
## 常用命令
`rg` 检查疑似 Secret，依赖/镜像/IaC 扫描在 CI 执行；发现泄漏先轮换再清理历史。
## 常见错误
默认密码上线、v2c 跨公网、permitAll API 无二次认证、把 sensitive output 当不会进 state、日志输出 Header。
