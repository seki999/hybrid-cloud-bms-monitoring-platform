# HTML、CSS 与原生 JavaScript
## 技术是什么
语义 HTML、响应式 CSS 和少量无构建原生 JavaScript。
## 为什么使用
保持 SSR 架构与低运维复杂度，同时提供筛选、图表和状态增强。
## 项目位置
`static/css/app.css`、`static/js/app.js` 与 Thymeleaf templates。
## 主要概念
可访问标签、CSS Grid/Flex、设计 token、渐进增强、Canvas 与 DOMContentLoaded。
## 主要配置
CSP 只允许同源脚本/样式，不依赖 CDN；1440x960 为文档截图基准。
## 示例代码
`data-chart-values` 将服务器数据交给原生 Canvas 绘图，页面无 JS 时表格仍可用。
## 常用命令
`npm run screenshots` 仅运行 Playwright 证据采集，不执行前端构建。
## 常见错误
把 Node 工具误当应用依赖、缺少 label/焦点样式、内联脚本被 CSP 拦截、仅用颜色表达状态。
