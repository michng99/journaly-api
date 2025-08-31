# Manual GCP Setup via Web Console

If you prefer not to install the gcloud CLI, you can set up the service account manually through the Google Cloud Console.

## Step 1: Enable APIs

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select or create project: `journaly-api-production`
3. Go to **APIs & Services > Library**
4. Enable these APIs:
   - Cloud Build API
   - Container Registry API
   - Cloud Run API
   - Artifact Registry API
   - Cloud Resource Manager API

## Step 2: Create Service Account

1. Go to **IAM & Admin > Service Accounts**
2. Click **"CREATE SERVICE ACCOUNT"**
3. Fill in details:
   - **Service account name**: `github-actions-sa`
   - **Service account ID**: `github-actions-sa`
   - **Description**: `Service account for GitHub Actions CI/CD pipeline`
4. Click **"CREATE AND CONTINUE"**

## Step 3: Grant Permissions

Add these roles to the service account:

1. **Cloud Build Service Account** (`roles/cloudbuild.builds.builder`)
2. **Storage Admin** (`roles/storage.admin`)
3. **Cloud Run Admin** (`roles/run.admin`)
4. **Service Account User** (`roles/iam.serviceAccountUser`)
5. **Cloud Build Editor** (`roles/cloudbuild.builds.editor`)

### How to add roles:
1. Select the service account you just created
2. Click **"GRANT ACCESS"**
3. Add each role one by one
4. Click **"SAVE"**

## Step 4: Create Service Account Key

1. Go to the **"KEYS"** tab of your service account
2. Click **"ADD KEY" > "Create new key"**
3. Select **"JSON"** format
4. Click **"CREATE"**
5. The JSON file will download automatically

## Step 5: Add Key to GitHub Secrets

1. Open the downloaded JSON file in a text editor
2. Copy the **entire content** of the file
3. Go to your GitHub repository
4. Navigate to **Settings > Secrets and variables > Actions**
5. Click **"New repository secret"**
6. Name: `GCP_SA_KEY`
7. Value: Paste the entire JSON content
8. Click **"Add secret"**

## Step 6: Security Cleanup

1. **Delete the downloaded JSON file** from your computer
2. **Never commit the JSON file** to your repository

## Step 7: Test the Setup

1. Commit and push the changes we made to your repository:
   ```bash
   git add .
   git commit -m "Add GCP integration setup"
   git push origin main
   ```

2. Go to **GitHub > Actions** tab to watch the deployment

3. Check **Google Cloud Console > Cloud Build > History** for build progress

4. Verify deployment at **Cloud Run > Services**

## Verification Commands (if you install gcloud later)

```bash
# Check service account exists
gcloud iam service-accounts list --filter="email:github-actions-sa@journaly-api-production.iam.gserviceaccount.com"

# Check permissions
gcloud projects get-iam-policy journaly-api-production --flatten="bindings[].members" --filter="bindings.members:github-actions-sa@journaly-api-production.iam.gserviceaccount.com"

# Check recent builds
gcloud builds list --limit=5
```

---

## 🔐 Security Reminders

- ✅ Service account has minimal required permissions
- ✅ JSON key stored securely in GitHub Secrets
- ✅ Local JSON file deleted
- ✅ Never commit keys to repository

## 🆘 Troubleshooting

If you encounter issues:
1. Verify all APIs are enabled
2. Check service account permissions
3. Ensure JSON format is correct in GitHub secret
4. Review the full setup guide in `docs/GCP-GITHUB-SETUP.md`
