# 创建名为 bms-monitoring 的 kind 集群，并将本地镜像装载后部署 Kustomize 清单。
# 所有路径都基于脚本目录解析，使脚本可以从任意当前目录可靠执行。
$ErrorActionPreference = 'Stop'
if (-not (Get-Command kind -ErrorAction SilentlyContinue)) { throw 'kind 未安装。请先安装 kind。' }
kind create cluster --config "$PSScriptRoot\..\infra\kubernetes\kind-config.yaml"
docker build -f "$PSScriptRoot\..\infra\docker\bms-app.Dockerfile" -t hybrid-cloud-bms-monitoring-platform:local "$PSScriptRoot\.."
kind load docker-image hybrid-cloud-bms-monitoring-platform:local --name bms-monitoring
kubectl apply -k "$PSScriptRoot\..\infra\kubernetes\overlays\kind"
kubectl -n bms-monitoring rollout status deployment/bms-web-app --timeout=5m
