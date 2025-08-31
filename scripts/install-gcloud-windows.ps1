# Google Cloud CLI Installation Script for Windows
# Run this script in PowerShell as Administrator

param(
    [switch]$Force = $false
)

Write-Host "🚀 Google Cloud CLI Installation Script" -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green
Write-Host ""

# Check if running as administrator
$currentPrincipal = New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())
$isAdmin = $currentPrincipal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "⚠️  Warning: Not running as Administrator" -ForegroundColor Yellow
    Write-Host "   Some features may not work properly" -ForegroundColor Yellow
    Write-Host ""
}

# Check if gcloud is already installed
try {
    $gcloudVersion = & gcloud version 2>$null
    if ($LASTEXITCODE -eq 0 -and -not $Force) {
        Write-Host "✅ Google Cloud CLI is already installed!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Current version:" -ForegroundColor Cyan
        & gcloud version
        Write-Host ""
        Write-Host "To reinstall, run with -Force parameter" -ForegroundColor Yellow
        exit 0
    }
} catch {
    Write-Host "📦 Google Cloud CLI not found. Installing..." -ForegroundColor Blue
}

# Create temp directory
$tempDir = "$env:TEMP\gcloud-install"
if (Test-Path $tempDir) {
    Remove-Item -Recurse -Force $tempDir
}
New-Item -ItemType Directory -Path $tempDir | Out-Null

try {
    # Download the installer
    $installerUrl = "https://dl.google.com/dl/cloudsdk/channels/rapid/GoogleCloudSDKInstaller.exe"
    $installerPath = "$tempDir\GoogleCloudSDKInstaller.exe"
    
    Write-Host "📥 Downloading Google Cloud CLI installer..." -ForegroundColor Blue
    Write-Host "   URL: $installerUrl" -ForegroundColor Gray
    
    # Use WebClient for better progress indication
    $webClient = New-Object System.Net.WebClient
    $webClient.DownloadFile($installerUrl, $installerPath)
    
    Write-Host "✅ Download completed!" -ForegroundColor Green
    
    # Run the installer
    Write-Host "🔧 Starting installation..." -ForegroundColor Blue
    Write-Host "   Please follow the installer prompts" -ForegroundColor Yellow
    Write-Host "   ✅ Make sure to check 'Start Google Cloud SDK Shell'" -ForegroundColor Yellow
    Write-Host ""
    
    Start-Process -FilePath $installerPath -Wait
    
    Write-Host "🎉 Installation completed!" -ForegroundColor Green
    Write-Host ""
    
    # Check if installation was successful
    Write-Host "🔍 Verifying installation..." -ForegroundColor Blue
    
    # Refresh environment variables
    $env:PATH = [System.Environment]::GetEnvironmentVariable("PATH","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("PATH","User")
    
    try {
        & gcloud version | Out-Null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Installation verified successfully!" -ForegroundColor Green
            Write-Host ""
            Write-Host "📋 Next Steps:" -ForegroundColor Cyan
            Write-Host "   1. Restart your PowerShell/Terminal" -ForegroundColor White
            Write-Host "   2. Run: gcloud init" -ForegroundColor White
            Write-Host "   3. Run: gcloud auth login" -ForegroundColor White
            Write-Host "   4. Run: gcloud config set project journaly-api-production" -ForegroundColor White
            Write-Host "   5. Return to the setup process" -ForegroundColor White
        } else {
            throw "gcloud command not working"
        }
    } catch {
        Write-Host "⚠️  Installation completed but gcloud not immediately available" -ForegroundColor Yellow
        Write-Host "   Please restart your terminal and verify with: gcloud --version" -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "❌ Installation failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "🔄 Alternative installation methods:" -ForegroundColor Cyan
    Write-Host "   1. Manual download: https://cloud.google.com/sdk/docs/install-sdk#windows" -ForegroundColor White
    Write-Host "   2. Use Chocolatey: choco install gcloudsdk" -ForegroundColor White
    Write-Host "   3. Use the manual GCP Console setup (see docs/MANUAL-GCP-SETUP.md)" -ForegroundColor White
    exit 1
} finally {
    # Cleanup
    if (Test-Path $tempDir) {
        Remove-Item -Recurse -Force $tempDir
    }
}

Write-Host ""
Write-Host "🚀 Ready to continue with GCP setup!" -ForegroundColor Green
Write-Host "   Run the service account setup script next:" -ForegroundColor White
Write-Host "   ./scripts/setup-gcp-service-account.sh" -ForegroundColor Cyan
