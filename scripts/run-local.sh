#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
docker compose up -d postgresql mailhog snmp-agent
./mvnw -pl app/bms-app spring-boot:run
