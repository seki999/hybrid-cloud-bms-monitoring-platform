$ErrorActionPreference = 'Stop'
docker compose down
Write-Host '本地容器已停止；数据库 volume 被保留。需要删除数据时请显式执行 docker compose down -v。'
