#!/usr/bin/env sh
set -eu
curl --fail --silent --show-error -X POST -H "X-BMS-API-Key: ${BMS_INGEST_API_KEY:-change-me-local-ingest-key}" http://localhost:8080/api/v1/demo-data/generate
printf '\n'
