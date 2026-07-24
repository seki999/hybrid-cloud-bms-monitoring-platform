#!/usr/bin/env sh
# 向本地 Syslog 接收端发送可重复的手工测试消息，用于验证 UDP/TCP 与 RFC 格式解析链路。
# 参数均提供安全的本地默认值，调用者仍可覆盖传输协议、消息格式和正文。
set -eu
cd "$(dirname "$0")/.."
transport="${1:-udp}"; format="${2:-rfc5424}"; message="${3:-BMS manual test: interface down}"
./mvnw -q -pl apps/syslog-simulator exec:java "-Dexec.args=localhost 5514 $transport $format $message"
