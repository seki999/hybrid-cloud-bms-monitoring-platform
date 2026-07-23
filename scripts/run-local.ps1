$ErrorActionPreference = 'Stop'
docker compose up -d postgresql mailhog snmp-agent
& "$PSScriptRoot\..\mvnw.cmd" -pl app/bms-app spring-boot:run
