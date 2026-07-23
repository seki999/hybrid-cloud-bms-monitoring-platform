#!/usr/bin/env sh
set -eu
root="$(cd "$(dirname "$0")/.." && pwd)"
command -v kind >/dev/null 2>&1 || { echo 'kind 未安装。请先安装 kind。' >&2; exit 1; }
kind create cluster --config "$root/infra/kubernetes/kind-config.yaml"
docker build -f "$root/infra/docker/bms-app.Dockerfile" -t hybrid-cloud-bms-monitoring-platform:local "$root"
kind load docker-image hybrid-cloud-bms-monitoring-platform:local --name bms-monitoring
kubectl apply -k "$root/infra/kubernetes/overlays/kind"
kubectl -n bms-monitoring rollout status deployment/bms-web-app --timeout=5m
