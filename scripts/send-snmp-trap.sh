#!/usr/bin/env sh
# 通过项目内置模拟器向本地 Trap 端口发送 SNMP v2c 链路状态事件，供接收与解析流程联调。
# 团体字优先读取环境变量；默认值只面向隔离的本地开发环境，不能用于共享或生产环境。
set -eu
cd "$(dirname "$0")/.."
./mvnw -q -pl apps/snmp-simulator exec:java "-Dexec.args=localhost 1162 ${SNMP_COMMUNITY:-change-me-local-community} ${1:-linkDown}"
