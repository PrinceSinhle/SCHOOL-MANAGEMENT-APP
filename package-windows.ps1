$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$stageDirectory = Join-Path $projectRoot "target\jpackage-input"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$destination = Join-Path $projectRoot "dist\windows-$timestamp"

Push-Location $projectRoot
try {
    & mvn -DskipTests clean package
    if ($LASTEXITCODE -ne 0) { throw "Maven packaging failed." }

    New-Item -ItemType Directory -Path $stageDirectory -Force | Out-Null
    Copy-Item -LiteralPath (Join-Path $projectRoot "target\school-management-app-1.0.0.jar") `
        -Destination (Join-Path $stageDirectory "school-management-app.jar") -Force

    & jpackage --type app-image --name SabathaSchool --app-version 1.0.0 `
        --input "$stageDirectory" --main-jar school-management-app.jar `
        --main-class schoolmanagement.za.App --dest "$destination"
    if ($LASTEXITCODE -ne 0) { throw "Windows app packaging failed." }

    Write-Host "App package created at $destination\SabathaSchool"
} finally {
    Pop-Location
}
