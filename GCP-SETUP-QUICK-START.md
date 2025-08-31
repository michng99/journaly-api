# 🚀 Quick Start: Connect GitHub to GCP

## ⚡ 5-Minute Setup

### 1. Run Setup Script
```bash
chmod +x scripts/setup-gcp-service-account.sh
./scripts/setup-gcp-service-account.sh
```

### 2. Add GitHub Secret
1. Copy the JSON content from `gcp-service-account-key.json`
2. Go to: **GitHub Repository > Settings > Secrets and variables > Actions**
3. Click **"New repository secret"**
4. Name: `GCP_SA_KEY`
5. Value: Paste the JSON content
6. Click **"Add secret"**

### 3. Clean Up
```bash
rm gcp-service-account-key.json
```

### 4. Test Deployment
```bash
git add .
git commit -m "Add GCP integration"
git push origin main
```

## ✅ Verification

1. **GitHub Actions**: Check the "Deploy to Google Cloud Platform" workflow
2. **Cloud Build**: Monitor build progress in GCP Console
3. **Cloud Run**: Verify service deployment

## 🆘 Need Help?

- **Full Guide**: See `docs/GCP-GITHUB-SETUP.md`
- **Security**: Check `docs/SECURITY-CHECKLIST.md`
- **Troubleshooting**: Common issues documented in the full guide

## 📋 Commands Cheat Sheet

```bash
# Check service account
gcloud iam service-accounts list

# View recent builds
gcloud builds list --limit=5

# Check Cloud Run services  
gcloud run services list --region=us-central1

# View service logs
gcloud logs tail --follow --resource-type=cloud_run_revision

# Test health endpoint
curl https://journaly-api-[random-hash]-uc.a.run.app/api/entries/health
```

---
**🎯 Goal**: Your API should be automatically deployed to GCP Cloud Run when you push to the `main` branch!
