# 安装锁定版本的 Playwright 与 Chromium，然后按预定义页面顺序采集文档截图。
# 截图工具与应用构建相互独立，因此缺少 npm 时给出明确错误而不影响 Java 编译。
$ErrorActionPreference = 'Stop'
if (-not (Get-Command npm -ErrorAction SilentlyContinue)) { throw '截图工具需要 Node.js/npm；它不参与应用前端构建。' }
npm ci
npx playwright install chromium
npm run screenshots
