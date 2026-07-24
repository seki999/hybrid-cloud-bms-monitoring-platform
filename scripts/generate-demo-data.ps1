# 调用演示数据端点并将响应序列化为 JSON，便于 Windows 终端确认实际生成结果。
# API Key 通过请求头传递，环境变量可覆盖仅用于本地开发的默认值。
$ErrorActionPreference = 'Stop'
$apiKey = if ($env:BMS_INGEST_API_KEY) { $env:BMS_INGEST_API_KEY } else { 'change-me-local-ingest-key' }
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/v1/demo-data/generate' -Headers @{'X-BMS-API-Key'=$apiKey} | ConvertTo-Json
