# Parse arguments from flags
param (
    [string]$c
)

if (-not $c) {
    Write-Host "Error: No configuration specified. Use -c to specify the configuration." -ForegroundColor Red
    exit 1
}

# Retrieve absolute path to this script
$script = $MyInvocation.MyCommand.Path
$scriptPath = Split-Path -Parent $script

Write-Host "Installing configuration $c"

# Push hardware map configuration file
Write-Host "--> Pushing hardware map configuration file"
$confHwMapFilePath = Join-Path -Path $scriptPath -ChildPath "..\conf\hwmap\$c.xml"
$remotePath = "/sdcard/FIRST/$c.xml"

adb push $confHwMapFilePath $remotePath

if ($LASTEXITCODE -eq 0) {
    Write-Host "Configuration file pushed successfully." -ForegroundColor Green
} else {
    Write-Host "Failed to push configuration file." -ForegroundColor Red
}

# Push robot configuration file
Write-Host "--> Pushing robot configuration file"
$confRobotFilePath = Join-Path -Path $scriptPath -ChildPath "..\conf\robot\$c.json"
$remotePath = "/sdcard/FIRST/$c.json"

adb push $confRobotFilePath $remotePath

if ($LASTEXITCODE -eq 0) {
    Write-Host "Configuration file pushed successfully." -ForegroundColor Green
} else {
    Write-Host "Failed to push configuration file." -ForegroundColor Red
}
