# Thymeleaf
## 技术是什么
服务器端 HTML 模板引擎，使用自然模板属性组合 Model 数据。
## 为什么使用
无需独立前端工程即可交付日文运维页面，并复用 Spring Security/Validation。
## 项目位置
`app/bms-app/src/main/resources/templates` 的 20 个页面及 `fragments/layout.html`。
## 主要概念
Fragment、表达式、`th:each`、`th:if`、URL 表达式、表单绑定与错误提示。
## 主要配置
开发期关闭 cache；所有动态文本默认 HTML 转义，CSS/JS 由同源静态目录提供。
## 示例代码
`th:text="${alert.title}"` 转义标题，`th:href="@{/alerts/{id}(id=${alert.id})}"` 生成链接。
## 常用命令
启动后访问 `/dashboard`；截图脚本逐页验证模板实际渲染。
## 常见错误
表达式拼接语法错误、片段参数不一致、在模板触发懒加载、用 `th:utext` 引入 XSS。
