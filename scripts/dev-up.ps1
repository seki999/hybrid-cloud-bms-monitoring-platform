# 校验 Compose 配置后构建并启动完整开发栈，再打印容器状态供操作者确认。
# Docker 可用性和配置语法会在创建容器前检查，减少半完成环境。
$ErrorActionPreference = 'Stop'
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) { throw 'docker 命令未安装或不在 PATH。' }
docker compose config --quiet
docker compose up -d --build
docker compose ps
