# Google Cloud Platform & GitHub Integration Setup Guide

This guide will help you connect your GitHub repository to Google Cloud Platform for automated deployment of the Journaly API.

## Prerequisites

- Google Cloud Platform account with billing enabled
- GitHub repository with admin access
- Google Cloud CLI (`gcloud`) installed locally
- Project ID: `journaly-api-production`

## Step 1: Create and Configure Service Account

### Option A: Automated Setup (Recommended)

1. **Run the setup script:**
   ```bash
   chmod +x scripts/setup-gcp-service-account.sh
   ./scripts/setup-gcp-service-account.sh
   ```

2. **Follow the script output instructions** to upload the key to GitHub Secrets.

### Option B: Manual Setup

1. **Enable required APIs:**
   ```bash
   gcloud services enable cloudbuild.googleapis.com
   gcloud services enable containerregistry.googleapis.com
   gcloud services enable run.googleapis.com
   gcloud services enable artifactregistry.googleapis.com
   ```

2. **Create service account:**
   ```bash
   gcloud iam service-accounts create github-actions-sa \
     --display-name="GitHub Actions Service Account" \
     --project=journaly-api-production
   ```

3. **Grant minimal required permissions:**
   ```bash
   # Cloud Build permissions
   gcloud projects add-iam-policy-binding journaly-api-production \
     --member="serviceAccount:github-actions-sa@journaly-api-production.iam.gserviceaccount.com" \
     --role="roles/cloudbuild.builds.builder"

   # Container Registry permissions
   gcloud projects add-iam-policy-binding journaly-api-production \
     --member="serviceAccount:github-actions-sa@journaly-api-production.iam.gserviceaccount.com" \
     --role="roles/storage.admin"

   # Cloud Run permissions
   gcloud projects add-iam-policy-binding journaly-api-production \
     --member="serviceAccount:github-actions-sa@journaly-api-production.iam.gserviceaccount.com" \
     --role="roles/run.admin"

   # Service Account User
   gcloud projects add-iam-policy-binding journaly-api-production \
     --member="serviceAccount:github-actions-sa@journaly-api-production.iam.gserviceaccount.com" \
     --role="roles/iam.serviceAccountUser"

   # Cloud Build Editor
   gcloud projects add-iam-policy-binding journaly-api-production \
     --member="serviceAccount:github-actions-sa@journaly-api-production.iam.gserviceaccount.com" \
     --role="roles/cloudbuild.builds.editor"
   ```

4. **Create service account key:**
   ```bash
   gcloud iam service-accounts keys create gcp-service-account-key.json \
     --iam-account=github-actions-sa@journaly-api-production.iam.gserviceaccount.com
   ```

## Step 2: Configure GitHub Secrets

1. **Navigate to your GitHub repository**
2. **Go to Settings > Secrets and variables > Actions**
3. **Create a new repository secret:**
   - Name: `GCP_SA_KEY`
   - Value: Copy and paste the **entire contents** of the `gcp-service-account-key.json` file

4. **Verify the secret format:**
   The secret should contain valid JSON starting with:
   ```json
   {
     "type": "service_account",
     "project_id": "journaly-api-production",
     ...
   }
   ```

## Step 3: Set Up Cloud Build Trigger (Optional)

For automatic triggering from GitHub:

1. **Go to Cloud Build > Triggers in GCP Console**
2. **Create Trigger:**
   - Name: `journaly-api-github-trigger`
   - Event: Push to a branch
   - Source: Connect your GitHub repository
   - Branch: `^main$`
   - Configuration: Cloud Build configuration file
   - Location: `cloudbuild.yaml`

## Step 4: Test the Integration

### Using GitHub Actions

1. **Push to main branch** or manually trigger the workflow
2. **Monitor the deployment:**
   - GitHub Actions: Repository > Actions tab
   - Cloud Build: GCP Console > Cloud Build > History
   - Cloud Run: GCP Console > Cloud Run

### Manual Testing

```bash
# Test local build
./mvnw clean package -DskipTests

# Test Cloud Build manually
gcloud builds submit --config=cloudbuild.yaml --substitutions=COMMIT_SHA=$(git rev-parse HEAD)
```

## Step 5: Environment Configuration

### Staging Environment
- Automatic deployment on push to `main` branch
- Service name: `journaly-api-staging`
- Lower resource allocation

### Production Environment
- Manual approval required (GitHub Environment protection)
- Service name: `journaly-api`
- Full resource allocation
- Database: Cloud SQL PostgreSQL

## Security Best Practices

### ✅ DO:
- **Use the provided service account** with minimal permissions
- **Store keys only in GitHub Secrets**, never in code
- **Enable GitHub Environment protection** for production
- **Regularly rotate service account keys** (quarterly)
- **Monitor Cloud Build logs** for unauthorized access
- **Use branch protection rules** for main branch

### ❌ DON'T:
- **Never commit JSON key files** to the repository
- **Don't grant broad permissions** like `Owner` or `Editor`
- **Don't share service account keys** outside the team
- **Don't use personal Google accounts** for service accounts
- **Don't disable security scanning** in the pipeline

## Troubleshooting

### Common Issues

1. **"Permission denied" errors:**
   - Verify service account permissions
   - Check if APIs are enabled
   - Ensure service account has Cloud Build permissions

2. **"Image not found" errors:**
   - Check Container Registry permissions
   - Verify project ID in cloudbuild.yaml
   - Ensure Docker authentication is working

3. **Cloud Run deployment failures:**
   - Check service account has Cloud Run Admin role
   - Verify region is correct (us-central1)
   - Check application.properties for production profile

4. **GitHub Actions failures:**
   - Verify GCP_SA_KEY secret is properly formatted JSON
   - Check service account key hasn't expired
   - Ensure all required APIs are enabled

### Debug Commands

```bash
# Check service account permissions
gcloud projects get-iam-policy journaly-api-production

# List Cloud Build history
gcloud builds list --limit=10

# Check Cloud Run services
gcloud run services list --region=us-central1

# View Cloud Build logs
gcloud builds log [BUILD_ID]
```

## Monitoring and Maintenance

### Regular Tasks
- **Weekly:** Review Cloud Build logs and costs
- **Monthly:** Check for security vulnerabilities
- **Quarterly:** Rotate service account keys
- **Annually:** Review and update IAM permissions

### Monitoring Setup
- Set up billing alerts in GCP
- Configure Cloud Monitoring for Cloud Run
- Enable Cloud Build notifications
- Set up GitHub Actions workflow notifications

## Cost Optimization

- **Use Cloud Build only for main branch** deployments
- **Set appropriate resource limits** in Cloud Run
- **Monitor Container Registry storage** usage
- **Clean up old container images** regularly

## Additional Resources

- [Google Cloud Build Documentation](https://cloud.google.com/build/docs)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Cloud Run Documentation](https://cloud.google.com/run/docs)
- [Service Account Best Practices](https://cloud.google.com/iam/docs/best-practices-for-using-service-accounts)

---

## Quick Start Checklist

- [ ] Run `scripts/setup-gcp-service-account.sh`
- [ ] Upload service account key to GitHub secret `GCP_SA_KEY`
- [ ] Delete local key file: `rm gcp-service-account-key.json`
- [ ] Push to main branch to trigger deployment
- [ ] Verify deployment at Cloud Run URL
- [ ] Set up production environment protection in GitHub
- [ ] Configure monitoring and alerts

**🎉 Your GitHub repository is now connected to Google Cloud Platform!**
