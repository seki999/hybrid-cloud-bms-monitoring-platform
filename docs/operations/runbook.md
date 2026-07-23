# 运维 Runbook

## 快速诊断

1. `curl http://localhost:8080/actuator/health/readiness` 区分应用、数据库和协议接收器健康。
2. `docker compose ps` 与 `docker compose logs --tail=200 bms-app` 检查容器状态和带 request/correlation ID 的日志。
3. UI 的「システム稼働状況」「監査ログ」「通知履歴」确认影响范围；不要在工单粘贴 Secret。
4. Syslog 使用 `scripts/send-syslog.*`，Trap 使用 `scripts/send-snmp-trap.*`，TCP 使用 `scripts/run-tcp-ping.*` 做受控探测。

## 常见故障

- UDP 无数据：确认主机/容器/Kubernetes 三层端口均为 5514/1162，Windows 防火墙与 NLB listener 允许 UDP。
- TCP refused：区分目标主动拒绝与 DNS/timeout，查看 `tcp_ping_results.error_type` 和重试次数。
- 数据库不可用：停止写入型维护，验证连接池与磁盘；恢复后 Flyway 版本必须与应用一致。
- 告警风暴：先确认重复窗口/规则，不直接禁用全部通知；可临时停告警 worker 并保留事件采集。

## 恢复与回滚

镜像按不可变版本回滚，数据库变更采用向前兼容迁移；不得自动回滚已写入数据的 Flyway 脚本。云资源销毁和数据库 volume 删除均需要独立审批与备份确认。本地 `docker compose down` 保留 volume，只有明确接受数据丢失时才执行 `down -v`。
