# 安全模型

保护对象包括设备地址、SNMP community、API Key、ADB Wallet、告警/审计数据和云凭据。入口包括 Web 登录、HTTPS ingestion、Syslog/Trap UDP/TCP、邮件和运维通道。

- 身份：ADMIN/OPERATOR/VIEWER 最小权限；生产以 OIDC/企业 IdP 替换本地账号。
- Web：Spring Security、BCrypt、CSRF、CSP、拒绝 framing、安全响应头；变更操作有方法/URL 级 RBAC。
- API：常量时间 API Key 比较；生产由 Vault/Secrets Manager 注入并轮换，边界 LB 负责 TLS、限流与 WAF。
- 协议：v2c 只用于隔离学习网络；生产优先 SNMPv3 authPriv。Syslog/Trap 来源通过私网、NLB NSG 和设备 allowlist 限制。
- 平台：容器非 root、只读根文件系统、drop ALL capabilities、restricted Pod Security、默认拒绝 NetworkPolicy、ServiceAccount 不自动挂 token。
- 数据：参数绑定、防 SQL 注入、日志脱敏、审计追加写；备份、state、Wallet 均加密。

残余风险：UDP 可伪造、v2c community 明文、内存用户不适合生产、示例 API Key 无租户/签名。上线前需加入 mTLS/OIDC、来源认证、速率限制、Secret 轮换、SAST/依赖/镜像/IaC 扫描和渗透测试。
