$ErrorActionPreference = 'Stop'
if (-not (Get-Command npm -ErrorAction SilentlyContinue)) { throw '截图工具需要 Node.js/npm；它不参与应用前端构建。' }
npm ci
npx playwright install chromium
npm run screenshots
