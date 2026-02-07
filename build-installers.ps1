Param(
  [string]$Version = "1.0.0",
  [ValidateSet("msi","exe")][string]$Type = "msi"
)

$ErrorActionPreference = "Stop"

function Require-Command([string]$cmd) {
  if (-not (Get-Command $cmd -ErrorAction SilentlyContinue)) {
    throw "Missing required command: $cmd"
  }
}

Require-Command "mvn"
Require-Command "jpackage"

Write-Host "Building Maven modules..." -ForegroundColor Green
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
  throw "Maven build failed"
}

$dist = Join-Path $PSScriptRoot "dist"
New-Item -ItemType Directory -Force -Path $dist | Out-Null

# Use a unique output directory per run to avoid failures when previous installer files are locked
$destDir = Join-Path $dist ("run-{0}" -f (Get-Date -Format "yyyyMMdd-HHmmss"))
New-Item -ItemType Directory -Force -Path $destDir | Out-Null

$serverTarget = Join-Path $PSScriptRoot "server\target"
$clientTarget = Join-Path $PSScriptRoot "client\target"
$sharedTarget = Join-Path $PSScriptRoot "shared\target"

# Stage app + runtime dependencies for jpackage (jpackage does NOT resolve Maven deps automatically)
$stageRoot = Join-Path $dist "_jpackage"
if (Test-Path $stageRoot) {
  Remove-Item -Recurse -Force $stageRoot
}

$serverInput = Join-Path $stageRoot "server"
$clientInput = Join-Path $stageRoot "client"
New-Item -ItemType Directory -Force -Path $serverInput | Out-Null
New-Item -ItemType Directory -Force -Path $clientInput | Out-Null

$serverJar = "todolist-server-$Version.jar"
$clientJar = "todolist-client-$Version.jar"
$sharedJar = "todolist-shared-$Version.jar"

$serverJarPath = Join-Path $serverTarget $serverJar
$clientJarPath = Join-Path $clientTarget $clientJar
$sharedJarPath = Join-Path $sharedTarget $sharedJar

if (-not (Test-Path $serverJarPath)) { throw "Missing server jar: $serverJarPath" }
if (-not (Test-Path $clientJarPath)) { throw "Missing client jar: $clientJarPath" }
if (-not (Test-Path $sharedJarPath)) { throw "Missing shared jar: $sharedJarPath" }

Copy-Item -Force $serverJarPath (Join-Path $serverInput $serverJar)
Copy-Item -Force $clientJarPath (Join-Path $clientInput $clientJar)

# Ensure shared module is on the runtime classpath for both apps
Copy-Item -Force $sharedJarPath (Join-Path $serverInput $sharedJar)
Copy-Item -Force $sharedJarPath (Join-Path $clientInput $sharedJar)

Write-Host "Staging runtime dependencies for server..." -ForegroundColor Green
mvn -q -pl server -DskipTests dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory="$serverInput"

Write-Host "Staging runtime dependencies for client..." -ForegroundColor Green
mvn -q -pl client -DskipTests dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory="$clientInput"

Write-Host "Packaging Server UI ($Type)..." -ForegroundColor Green
jpackage `
  --dest $destDir `
  --input $serverInput `
  --name "TodoList Server" `
  --main-jar $serverJar `
  --main-class dk.dtu.ServerApp `
  --type $Type `
  --app-version $Version `
  --vendor "Patrick" `
  --description "TodoList Management Server" `
  --win-menu `
  --win-shortcut `
  --java-options "-Dtodolist.data.dir=%APPDATA%\\TodoList" `
  --java-options "-Dtodolist.port=9001"

Write-Host "Packaging Client UI ($Type)..." -ForegroundColor Green
jpackage `
  --dest $destDir `
  --input $clientInput `
  --name "TodoList Client" `
  --main-jar $clientJar `
  --main-class dk.dtu.ClientApp `
  --type $Type `
  --app-version $Version `
  --vendor "Patrick" `
  --description "TodoList Management Client" `
  --win-menu `
  --win-shortcut `
  --java-options "-Dtodolist.server.ip=127.0.0.1" `
  --java-options "-Dtodolist.port=9001"

Write-Host "Done. Output is in: $destDir" -ForegroundColor Yellow
Write-Host "NOTE: If you choose MSI, Windows may require WiX Toolset installed." -ForegroundColor Yellow
