# 通过项目内置模拟器发送 linkDown 或 linkUp Trap，用于验证 Windows 下的 SNMP 接收链路。
# 团体字可由环境变量覆盖；ValidateSet 则阻止脚本传入模拟器不认识的事件状态。
param([ValidateSet('linkDown','linkUp')][string]$State='linkDown')
$ErrorActionPreference = 'Stop'
$community = if ($env:SNMP_COMMUNITY) { $env:SNMP_COMMUNITY } else { 'change-me-local-community' }
& "$PSScriptRoot\..\mvnw.cmd" -q -pl apps/snmp-simulator exec:java "-Dexec.args=localhost 1162 $community $State"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
