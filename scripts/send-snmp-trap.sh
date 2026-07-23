#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
./mvnw -q -pl apps/snmp-simulator exec:java "-Dexec.args=localhost 1162 ${SNMP_COMMUNITY:-change-me-local-community} ${1:-linkDown}"
