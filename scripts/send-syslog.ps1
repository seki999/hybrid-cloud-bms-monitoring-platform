# 向本地 Syslog 接收端发送手工测试消息，并限制协议与格式参数，避免生成接收端不支持的组合。
# Maven 子模块负责实际报文构造，本脚本只统一 Windows 环境下的调用入口和退出码传播。
param([ValidateSet('udp','tcp')][string]$Transport='udp', [ValidateSet('rfc3164','rfc5424')][string]$Format='rfc5424', [string]$Message='BMS 手动测试: interface down')
$ErrorActionPreference = 'Stop'
& "$PSScriptRoot\..\mvnw.cmd" -q -pl apps/syslog-simulator exec:java "-Dexec.args=localhost 5514 $Transport $Format $Message"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
