#!/usr/bin/env sh
# 调用应用的 TCP Ping API，快速验证鉴权、请求校验、探测执行和结果持久化的完整链路。
# 主机与端口可由位置参数覆盖；API 密钥默认值仅用于本地配置，不应复用于真实环境。
set -eu
curl --fail --silent --show-error -X POST -H 'Content-Type: application/json' -H "X-BMS-API-Key: ${BMS_INGEST_API_KEY:-change-me-local-ingest-key}" --data "{\"host\":\"${1:-localhost}\",\"port\":${2:-8080},\"timeoutMillis\":1500,\"retries\":1}" http://localhost:8080/api/v1/monitoring/tcp-ping
printf '\n'
