$ErrorActionPreference = 'Stop'
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) { throw 'docker 命令未安装或不在 PATH。' }
docker compose config --quiet
docker compose up -d --build
docker compose ps
