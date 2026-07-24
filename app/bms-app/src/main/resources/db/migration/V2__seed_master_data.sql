-- 写入可重复的本地主数据与监控样例，为首次启动、页面展示和集成测试提供一致基线。
-- 固定标识和幂等约束应与表结构保持一致，生产环境可通过独立流程替换这些演示记录。
INSERT INTO devices (name, hostname, location, vendor, device_type, status, description, created_at, updated_at) VALUES
('東京エッジルーター01', '10.20.1.11', '東京DC / Zone-A', 'Juniper style', 'ROUTER', 'NORMAL', '顧客VPNの主系ルーター', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('東京エッジルーター02', '10.20.1.12', '東京DC / Zone-B', 'Juniper style', 'ROUTER', 'WARNING', '顧客VPNの副系ルーター', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('大阪エッジルーター01', '10.30.1.11', '大阪DC / Zone-A', 'Juniper style', 'ROUTER', 'CRITICAL', '西日本集約ルーター', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('監視Linux01', '10.20.2.21', '東京DC / Apps', 'Linux', 'LINUX_SERVER', 'NORMAL', 'Syslog中継サーバー', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('監視Linux02', '10.30.2.21', '大阪DC / Apps', 'Linux', 'LINUX_SERVER', 'NORMAL', 'SNMPポーリングサーバー', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('業務Windows01', '10.20.3.31', '東京オフィス', 'Windows', 'WINDOWS_SERVER', 'NORMAL', '業務サービス監視対象', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('業務Windows02', '10.30.3.31', '大阪オフィス', 'Windows', 'WINDOWS_SERVER', 'WARNING', 'バックアップサービス監視対象', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VPNゲートウェイ', 'vpn.service.local', 'AWS / OCI', 'Virtual', 'VIRTUAL_SERVICE', 'NORMAL', 'クラウド間VPNサービス', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('公開HTTPS API', 'api.service.local', 'OCI OKE', 'Virtual', 'VIRTUAL_SERVICE', 'NORMAL', 'Lambda受信API', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('メール通知サービス', 'mail.service.local', 'OCI Private', 'Virtual', 'VIRTUAL_SERVICE', 'NORMAL', '通知アダプター', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO monitoring_targets (device_id, name, target_type, host, port, oid, enabled, interval_seconds, created_at, updated_at)
SELECT id, 'TCP 443 接続監視', 'TCP_PING', hostname, 443, NULL, TRUE, 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM devices;

INSERT INTO monitoring_targets (device_id, name, target_type, host, port, oid, enabled, interval_seconds, created_at, updated_at)
SELECT id, 'インターフェース状態 GET', 'SNMP_GET', hostname, 161, '1.3.6.1.2.1.2.2.1.8.1', TRUE, 300, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM devices WHERE device_type = 'ROUTER';

INSERT INTO monitoring_rules (target_id, name, metric_name, warning_threshold, critical_threshold, suppression_seconds, enabled, created_at, updated_at)
SELECT id, name || ' 応答時間しきい値', 'responseMillis', 300, 1000, 300, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM monitoring_targets WHERE target_type = 'TCP_PING';

INSERT INTO notification_targets (name, channel, address, minimum_severity, enabled, created_at, updated_at) VALUES
('一次運用チーム', 'EMAIL', 'operator@local.invalid', 'WARNING', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('重大障害エスカレーション', 'EMAIL', 'critical@local.invalid', 'CRITICAL', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('外部連携安全Mock', 'MOCK_WEBHOOK', 'mock://external-system/alerts', 'CRITICAL', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO app_users (username, display_name, role, enabled, created_at, updated_at) VALUES
('admin', 'ローカル管理者', 'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('operator', '運用担当者', 'OPERATOR', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('viewer', '参照担当者', 'VIEWER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO audit_logs (actor, action, resource_type, resource_id, detail, created_at, updated_at) VALUES
('system', 'DATABASE_INITIALIZED', 'SYSTEM', '1', 'Flywayで設備・監視対象・通知先の初期データを登録', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
