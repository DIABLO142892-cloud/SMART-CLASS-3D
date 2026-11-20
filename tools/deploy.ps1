param(
    [Parameter(Mandatory = $true)]
    [string]$TomcatHome,
    [string]$DatabaseUrl = 'jdbc:mysql://127.0.0.1:3306/smart_classroom_finder?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata',
    [string]$DatabaseUser = 'root',
    [string]$DatabasePassword = ''
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$war = Join-Path $projectRoot 'target\smart-classroom-finder-1.0.0-SNAPSHOT.war'
$webapps = Join-Path $TomcatHome 'webapps'

if (-not (Test-Path -LiteralPath (Join-Path $TomcatHome 'bin\startup.bat'))) {
    throw "Tomcat was not found at $TomcatHome"
}

Push-Location $projectRoot
try {
    & mvn.cmd clean package
    if ($LASTEXITCODE -ne 0) { throw 'Maven build failed.' }
} finally {
    Pop-Location
}

Copy-Item -LiteralPath $war -Destination (Join-Path $webapps 'smartclass3d.war') -Force
$env:DB_URL = $DatabaseUrl
$env:DB_USER = $DatabaseUser
$env:DB_PASSWORD = $DatabasePassword
$env:APP_TIMEZONE = 'Asia/Kolkata'
& (Join-Path $TomcatHome 'bin\startup.bat')
Write-Host 'Deployed. Open http://localhost:8080/smartclass3d/login' -ForegroundColor Green
