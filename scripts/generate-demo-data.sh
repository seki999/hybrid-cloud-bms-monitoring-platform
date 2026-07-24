#!/usr/bin/env sh
# 调用受 API Key 保护的演示数据端点，为页面检查和本地联调生成可重复的数据集。
# 密钥优先取自环境变量；默认值只与本地 application 配置配套使用。
set -eu
curl --fail --silent --show-error -X POST -H "X-BMS-API-Key: ${BMS_INGEST_API_KEY:-change-me-local-ingest-key}" http://localhost:8080/api/v1/demo-data/generate
printf '\n'
