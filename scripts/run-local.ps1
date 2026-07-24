# 启动本地依赖容器后以前台方式运行 Spring Boot，适合 Windows/IDE 联合调试。
# 前台 Maven 进程保留完整日志与断点能力，依赖服务则由 Compose 维持一致版本。
$ErrorActionPreference = 'Stop'
docker compose up -d postgresql mailhog snmp-agent
& "$PSScriptRoot\..\mvnw.cmd" -pl app/bms-app spring-boot:run
