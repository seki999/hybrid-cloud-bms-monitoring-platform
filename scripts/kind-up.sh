#!/usr/bin/env sh
# 创建本地 kind 集群、构建应用镜像并部署 Kustomize kind 覆盖层，用于复现 Kubernetes 运行形态。
# 在修改集群状态前先检查必需命令，避免执行到中途才留下不完整环境。
set -eu
root="$(cd "$(dirname "$0")/.." && pwd)"
command -v kind >/dev/null 2>&1 || { echo 'kind 未安装。请先安装 kind。' >&2; exit 1; }
kind create cluster --config "$root/infra/kubernetes/kind-config.yaml"
docker build -f "$root/infra/docker/bms-app.Dockerfile" -t hybrid-cloud-bms-monitoring-platform:local "$root"
kind load docker-image hybrid-cloud-bms-monitoring-platform:local --name bms-monitoring
kubectl apply -k "$root/infra/kubernetes/overlays/kind"
kubectl -n bms-monitoring rollout status deployment/bms-web-app --timeout=5m
