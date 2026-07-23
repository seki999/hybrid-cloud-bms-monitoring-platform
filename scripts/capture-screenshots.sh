#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
command -v npm >/dev/null 2>&1 || { echo '截图工具需要 Node.js/npm；它不参与应用前端构建。' >&2; exit 1; }
npm ci
npx playwright install chromium
npm run screenshots
