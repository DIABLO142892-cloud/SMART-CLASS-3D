param(
    [Parameter(Mandatory = $true)]
    [string]$TomcatHome
)

$shutdown = Join-Path $TomcatHome 'bin\shutdown.bat'
if (-not (Test-Path -LiteralPath $shutdown)) { throw "Tomcat was not found at $TomcatHome" }
& $shutdown
