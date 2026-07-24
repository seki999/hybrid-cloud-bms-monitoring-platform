#!/usr/bin/env sh
# 停止并移除本项目的 Compose 容器与网络，但刻意保留数据库卷以保护本地调试数据。
# 数据删除必须由操作者显式追加 -v，避免普通停机动作造成不可恢复的数据丢失。
set -eu
docker compose down
echo '本地容器已停止；数据库 volume 被保留。需要删除数据时请显式执行 docker compose down -v。'
