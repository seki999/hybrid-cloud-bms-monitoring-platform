#!/usr/bin/env sh
# 先启动应用依赖的 PostgreSQL、邮件捕获器和 SNMP Agent，再以前台方式运行 Spring Boot。
# 应用不放入容器，便于开发期间使用 IDE 调试，同时依赖服务仍保持可重复部署。
set -eu
cd "$(dirname "$0")/.."
docker compose up -d postgresql mailhog snmp-agent
./mvnw -pl app/bms-app spring-boot:run
