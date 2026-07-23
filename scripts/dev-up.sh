#!/usr/bin/env sh
set -eu
command -v docker >/dev/null 2>&1 || { echo 'docker 命令未安装或不在 PATH。' >&2; exit 1; }
docker compose config --quiet
docker compose up -d --build
docker compose ps
