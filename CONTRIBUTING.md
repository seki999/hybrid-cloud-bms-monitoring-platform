# 贡献指南

1. 从 `main` 创建短生命周期分支，并把一次变更限制在一个可验证目标内。
2. Java 21 环境执行 `./mvnw clean verify`（Windows 使用 `mvnw.cmd`）。
3. 修改数据库时只追加 Flyway 迁移，不修改已经发布的迁移。
4. 修改页面后执行截图脚本并检查日语文案、键盘操作、错误提示与响应式布局。
5. 修改 IaC 时执行 `tofu fmt -recursive -check` 和各环境的 `tofu validate`，不得提交 state 或 tfvars。
6. Pull Request 必须列出已运行和未运行的验证，禁止把未执行项写成成功。

