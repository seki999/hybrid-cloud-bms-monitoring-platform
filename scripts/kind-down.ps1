# 仅销毁本项目使用的 bms-monitoring kind 集群，不清理 Docker 中的其他镜像或容器。
# 显式集群名将删除范围限制在开发环境，降低误操作风险。
$ErrorActionPreference = 'Stop'
kind delete cluster --name bms-monitoring
