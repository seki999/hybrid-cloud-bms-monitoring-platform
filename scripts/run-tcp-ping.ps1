param([string]$HostName='localhost', [int]$Port=8080)
$ErrorActionPreference = 'Stop'
$apiKey = if ($env:BMS_INGEST_API_KEY) { $env:BMS_INGEST_API_KEY } else { 'change-me-local-ingest-key' }
$body = @{host=$HostName;port=$Port;timeoutMillis=1500;retries=1} | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/v1/monitoring/tcp-ping' -Headers @{'X-BMS-API-Key'=$apiKey} -ContentType 'application/json' -Body $body | ConvertTo-Json
