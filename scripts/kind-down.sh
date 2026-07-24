#!/usr/bin/env sh
# 仅删除本项目约定的 bms-monitoring kind 集群，避免影响机器上的其他本地集群。
# 集群名称必须与 kind-config.yaml 保持一致，否则清理命令不会命中目标。
set -eu
kind delete cluster --name bms-monitoring
