#!/usr/bin/env sh
# 校验 Compose 配置后构建并启动完整本地开发栈，最后输出服务状态便于快速发现启动失败。
# 预先检查 Docker 命令和配置语法，可在产生容器变更前暴露常见环境问题。
set -eu
command -v docker >/dev/null 2>&1 || { echo 'docker 命令未安装或不在 PATH。' >&2; exit 1; }
docker compose config --quiet
docker compose up -d --build
docker compose ps
