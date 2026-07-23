# 安全策略

## 报告方式

请勿在公开 Issue 中粘贴密码、SNMP Community、OCID、Wallet、云密钥或客户网络信息。请通过仓库维护者提供的私有渠道报告，并附上不含敏感值的复现步骤。

## 本地账号

`admin / Admin123!` 仅用于隔离的本地学习环境。生产部署必须禁用该默认值，使用企业 IdP 或外部 Secret 注入随机强密码，并执行轮换。

## 基线

- API 密钥、数据库密码、SNMP Community 只从环境变量或 Secret Store 获取。
- 日志过滤认证头、Cookie、密码与完整 Secret。
- 云端采用最小权限 IAM、私有子网、TLS、NetworkPolicy 与审计日志。
- 物理警报器控制在本项目中始终为安全 Mock；不得直接连接生产设备。

