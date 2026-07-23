#!/usr/bin/env sh
set -eu
docker compose down
echo '本地容器已停止；数据库 volume 被保留。需要删除数据时请显式执行 docker compose down -v。'
