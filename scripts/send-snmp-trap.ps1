param([ValidateSet('linkDown','linkUp')][string]$State='linkDown')
$ErrorActionPreference = 'Stop'
$community = if ($env:SNMP_COMMUNITY) { $env:SNMP_COMMUNITY } else { 'change-me-local-community' }
& "$PSScriptRoot\..\mvnw.cmd" -q -pl apps/snmp-simulator exec:java "-Dexec.args=localhost 1162 $community $State"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
