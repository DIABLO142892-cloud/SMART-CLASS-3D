param(
    [string]$MySqlExe = 'C:\xampp\mysql\bin\mysql.exe',
    [string]$DatabaseHost = '127.0.0.1',
    [int]$Port = 3306,
    [string]$User = 'root',
    [string]$Password = ''
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$schema = (Join-Path $projectRoot 'database\schema.sql').Replace('\', '/')
$seed = (Join-Path $projectRoot 'database\seed.sql').Replace('\', '/')

if (-not (Test-Path -LiteralPath $MySqlExe)) {
    throw "MySQL client not found at $MySqlExe"
}

$arguments = @('--protocol=tcp', '-h', $DatabaseHost, '-P', $Port, '-u', $User)
if ($Password) { $arguments += "-p$Password" }

Write-Host 'Recreating SmartClass 3D database...'
& $MySqlExe @arguments --execute="SOURCE $schema; SOURCE $seed;"
if ($LASTEXITCODE -ne 0) { throw 'Database import failed.' }

& $MySqlExe @arguments --database=smart_classroom_finder --execute='SELECT COUNT(*) AS classrooms FROM classrooms; SELECT COUNT(*) AS users FROM users;'
if ($LASTEXITCODE -ne 0) { throw 'Database verification failed.' }
Write-Host 'Database setup complete.' -ForegroundColor Green
