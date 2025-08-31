# Install Google Cloud CLI on Windows

## Quick Installation Steps

### Option 1: Using PowerShell (Recommended)
```powershell
# Download and run the installer
(New-Object Net.WebClient).DownloadFile("https://dl.google.com/dl/cloudsdk/channels/rapid/GoogleCloudSDKInstaller.exe", "$env:Temp\GoogleCloudSDKInstaller.exe")
& $env:Temp\GoogleCloudSDKInstaller.exe
```

### Option 2: Manual Download
1. Go to: https://cloud.google.com/sdk/docs/install-sdk#windows
2. Download the Google Cloud CLI installer
3. Run the installer and follow the prompts
4. **Important**: Check "Start Google Cloud SDK Shell" during installation

### Option 3: Using Chocolatey (if you have it)
```powershell
choco install gcloudsdk
```

## After Installation

1. **Restart your terminal/PowerShell**
2. **Verify installation**:
   ```bash
   gcloud --version
   ```

3. **Initialize gcloud**:
   ```bash
   gcloud init
   ```

4. **Authenticate**:
   ```bash
   gcloud auth login
   ```

5. **Set your project**:
   ```bash
   gcloud config set project journaly-api-production
   ```

## Next Steps After Installation

Once gcloud is installed and configured, come back and we'll continue with:
1. Running the service account setup script
2. Creating the GitHub secret
3. Testing the deployment

---

**⚠️ Note**: The Google Cloud CLI installation requires a restart of your terminal. Please complete the installation and then let me know when you're ready to continue!
