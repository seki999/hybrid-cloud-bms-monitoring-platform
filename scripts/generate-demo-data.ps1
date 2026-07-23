$ErrorActionPreference = 'Stop'
$apiKey = if ($env:BMS_INGEST_API_KEY) { $env:BMS_INGEST_API_KEY } else { 'change-me-local-ingest-key' }
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/v1/demo-data/generate' -Headers @{'X-BMS-API-Key'=$apiKey} | ConvertTo-Json
