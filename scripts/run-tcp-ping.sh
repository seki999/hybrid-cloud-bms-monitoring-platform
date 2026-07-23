#!/usr/bin/env sh
set -eu
curl --fail --silent --show-error -X POST -H 'Content-Type: application/json' -H "X-BMS-API-Key: ${BMS_INGEST_API_KEY:-change-me-local-ingest-key}" --data "{\"host\":\"${1:-localhost}\",\"port\":${2:-8080},\"timeoutMillis\":1500,\"retries\":1}" http://localhost:8080/api/v1/monitoring/tcp-ping
printf '\n'
