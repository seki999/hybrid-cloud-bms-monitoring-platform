# 调用本地 TCP Ping API，并由 PowerShell 负责生成合法 JSON，避免手工转义导致请求格式错误。
# API 密钥优先来自环境变量；固定超时与重试次数让联调结果具有可比性。
param([string]$HostName='localhost', [int]$Port=8080)
$ErrorActionPreference = 'Stop'
$apiKey = if ($env:BMS_INGEST_API_KEY) { $env:BMS_INGEST_API_KEY } else { 'change-me-local-ingest-key' }
$body = @{host=$HostName;port=$Port;timeoutMillis=1500;retries=1} | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/v1/monitoring/tcp-ping' -Headers @{'X-BMS-API-Key'=$apiKey} -ContentType 'application/json' -Body $body | ConvertTo-Json
