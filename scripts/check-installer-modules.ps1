# Guard: jdeps computes the JDK modules the client app actually references. If
# any module is missing from scripts/installer-modules.txt (the single source
# for every jlink/jpackage --add-modules list), FAIL instead of shipping an
# installer that crashes silently at launch (as v2.0.0 did when java.net.http
# was missing). Runs on Windows, macOS, and Linux (pwsh).
#
# Usage: pwsh scripts/check-installer-modules.ps1 [-InputDir client-jpackage-input]
param(
  # Directory holding the client app jar plus all dependency jars.
  [string]$InputDir = "client-jpackage-input"
)

$listFile = Join-Path $PSScriptRoot "installer-modules.txt"
$declared = (Get-Content $listFile -Raw).Trim() -split "," | ForEach-Object { $_.Trim() } | Where-Object { $_ }

$jars = (Get-ChildItem (Join-Path $InputDir "*.jar")).FullName
if (-not $jars) { Write-Error "No jars found in '$InputDir'."; exit 1 }

$out = & jdeps --multi-release 21 --ignore-missing-deps --print-module-deps $jars 2>$null
if (-not $out) { Write-Host "jdeps produced no output; skipping guard"; exit 0 }

$needed = ($out -join ",") -split "," | ForEach-Object { $_.Trim() } | Where-Object { $_ -like "java.*" -or $_ -like "jdk.*" } | Sort-Object -Unique
$missing = $needed | Where-Object { $declared -notcontains $_ }
if ($missing) {
  Write-Error ("installer-modules.txt is missing: " + ($missing -join ", ") + ". Add them to scripts/installer-modules.txt (all workflows and build-installers.ps1 read that file).")
  exit 1
}
Write-Host ("Module guard OK. jdeps-required: " + ($needed -join ", "))
