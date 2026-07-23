param([ValidateSet('udp','tcp')][string]$Transport='udp', [ValidateSet('rfc3164','rfc5424')][string]$Format='rfc5424', [string]$Message='BMS 手动测试: interface down')
$ErrorActionPreference = 'Stop'
& "$PSScriptRoot\..\mvnw.cmd" -q -pl apps/syslog-simulator exec:java "-Dexec.args=localhost 5514 $Transport $Format $Message"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
