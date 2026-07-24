#!/usr/bin/env sh
# 执行整个 Maven 多模块项目的清理、编译和测试验证，避免旧构建产物掩盖依赖或测试问题。
# 任一命令失败都会立即终止，从而让本地与 CI 获得一致的非零退出状态。
set -eu
cd "$(dirname "$0")/.."
./mvnw clean verify
