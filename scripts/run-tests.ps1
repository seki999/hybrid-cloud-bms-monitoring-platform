# 执行整个 Maven 多模块项目的 clean verify 生命周期，统一 Windows 开发者与 CI 的验证入口。
# ErrorActionPreference 与显式退出码检查共同确保子进程失败不会被误报为成功。
$ErrorActionPreference = 'Stop'
& "$PSScriptRoot\..\mvnw.cmd" clean verify
