# 停止本项目的 Compose 服务并保留数据库卷，适合作为日常开发环境的安全关停入口。
# 卷删除不包含在脚本中，防止一次普通停机误删本地持久化数据。
$ErrorActionPreference = 'Stop'
docker compose down
Write-Host '本地容器已停止；数据库 volume 被保留。需要删除数据时请显式执行 docker compose down -v。'
