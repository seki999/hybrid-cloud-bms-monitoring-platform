# 监控与告警
## 技术是什么
把原始信号解析、标准化、规则判定、去重、生命周期与通知组成可审计闭环。
## 为什么使用
通信运维关注故障影响与恢复，不应把每条设备日志直接等同一个告警。
## 项目位置
`EventProcessingService`、`AlertService`、scheduler、notification、Dashboard/report 页面。
## 主要概念
Event vs Alert、threshold、severity、dedup window、recovery、acknowledge/close、idempotency、SLO。
## 主要配置
规则/阈值来自数据库；事件保存原文，告警保存当前聚合状态，历史保存每次迁移。
## 示例代码
相同 device/source/eventKey 在窗口内更新 occurrence count；恢复事件迁移为 RECOVERED 而非删除。
## 常用命令
生成演示数据后查看 `/events`、`/alerts`、`/reports/trends` 与 MailHog 8025。
## 常见错误
Event/Alert 混表、恢复即删除、重复通知无幂等键、定时任务多副本重复执行、只看技术指标不看影响。
