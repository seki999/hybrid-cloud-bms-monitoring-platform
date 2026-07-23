# SNMP
## 技术是什么
通过 OID 读取 MIB 对象或接收设备主动 Trap 的网络管理协议。
## 为什么使用
GET 提供主动状态/指标，Trap 提供低延迟故障通知，两者互补。
## 项目位置
`protocol/snmp`、SNMP4J、`snmp-simulator`、GET Lambda 与 Trap/GET 历史页。
## 主要概念
OID、MIB、v2c community、v3 user/auth/privacy、UDP 161/162、timeout/retry、varbind。
## 主要配置
本地 agent 1161、Trap 1162；community 从环境变量读取，代码保留 v3 扩展接口。
## 示例代码
GET `1.3.6.1.2.1.1.3.0` 获取 sysUpTime；linkDown Trap OID 为 `1.3.6.1.6.3.1.1.5.3`。
## 常用命令
`scripts/send-snmp-trap.ps1 linkDown`；API `/api/v1/monitoring/snmp-get` 执行受控查询。
## 常见错误
community/版本不一致、ACL 拒绝、UDP 回包路由错误、OID 实例缺 `.0`、生产继续使用明文 v2c。
