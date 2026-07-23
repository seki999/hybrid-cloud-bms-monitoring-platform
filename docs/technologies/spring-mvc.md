# Spring MVC
## 技术是什么
基于 Servlet 的请求路由、参数绑定、验证、Model 与视图解析框架。
## 为什么使用
本项目要求服务端渲染，MVC 能让权限、验证和页面导航保持在一个 Java 应用内。
## 项目位置
`web/*Controller`、`GlobalExceptionHandler`、`DeviceForm` 与 MockMvc 测试。
## 主要概念
Controller、RequestMapping、DTO、Bean Validation、PRG、分页、ControllerAdvice。
## 主要配置
写操作由 CSRF 保护，`/api/**` 用 API Key 并排除 CSRF；统一错误页不暴露堆栈。
## 示例代码
`@PostMapping @PreAuthorize ...` 接收 `@Valid DeviceForm`，成功后 redirect 并写 flash message。
## 常用命令
`./mvnw -pl app/bms-app -Dtest=*ControllerTest test` 运行 Web 切片测试。
## 常见错误
直接绑定 Entity、缺失 CSRF token、懒加载对象离开事务、把验证异常误报成 500。
