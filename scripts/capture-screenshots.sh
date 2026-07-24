#!/usr/bin/env sh
# 安装锁文件指定的 Playwright 依赖并执行固定视口截图流程，生成 README 使用的真实页面证据。
# Node.js 只服务于截图自动化，不参与 Spring Boot 应用的前端资源构建。
set -eu
cd "$(dirname "$0")/.."
command -v npm >/dev/null 2>&1 || { echo '截图工具需要 Node.js/npm；它不参与应用前端构建。' >&2; exit 1; }
npm ci
npx playwright install chromium
npm run screenshots
