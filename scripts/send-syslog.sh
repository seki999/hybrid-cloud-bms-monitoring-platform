#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
transport="${1:-udp}"; format="${2:-rfc5424}"; message="${3:-BMS manual test: interface down}"
./mvnw -q -pl apps/syslog-simulator exec:java "-Dexec.args=localhost 5514 $transport $format $message"
