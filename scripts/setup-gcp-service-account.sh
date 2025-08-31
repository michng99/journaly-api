#!/bin/bash

# GCP Service Account Setup Script for Journaly API
# This script creates a service account with minimal required permissions

set -e

PROJECT_ID="journaly-api-production"
SERVICE_ACCOUNT_NAME="github-actions-sa"
SERVICE_ACCOUNT_EMAIL="${SERVICE_ACCOUNT_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
KEY_FILE="gcp-service-account-key.json"

echo "🚀 Setting up GCP Service Account for GitHub Actions..."
echo "Project ID: ${PROJECT_ID}"
echo "Service Account: ${SERVICE_ACCOUNT_EMAIL}"
echo ""

# Check if gcloud is installed and authenticated
if ! command -v gcloud &> /dev/null; then
    echo "❌ gcloud CLI is not installed. Please install it first:"
    echo "https://cloud.google.com/sdk/docs/install"
    exit 1
fi

# Set the project
echo "📋 Setting GCP project..."
gcloud config set project ${PROJECT_ID}

# Enable required APIs
echo "🔧 Enabling required APIs..."
gcloud services enable cloudbuild.googleapis.com
gcloud services enable containerregistry.googleapis.com
gcloud services enable run.googleapis.com
gcloud services enable artifactregistry.googleapis.com
gcloud services enable cloudresourcemanager.googleapis.com

# Create service account
echo "👤 Creating service account..."
gcloud iam service-accounts create ${SERVICE_ACCOUNT_NAME} \
    --display-name="GitHub Actions Service Account" \
    --description="Service account for GitHub Actions CI/CD pipeline"

# Wait a moment for service account to be created
sleep 5

# Grant minimal required permissions
echo "🔐 Granting IAM roles with minimal permissions..."

# Cloud Build permissions
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
    --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
    --role="roles/cloudbuild.builds.builder"

# Container Registry permissions
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
    --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
    --role="roles/storage.admin"

# Cloud Run permissions
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
    --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
    --role="roles/run.admin"

# Service Account User (required for Cloud Run deployment)
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
    --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
    --role="roles/iam.serviceAccountUser"

# Cloud Build Service Account (for triggering builds)
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
    --member="serviceAccount:${SERVICE_ACCOUNT_EMAIL}" \
    --role="roles/cloudbuild.builds.editor"

# Create and download service account key
echo "🗝️  Creating service account key..."
gcloud iam service-accounts keys create ${KEY_FILE} \
    --iam-account=${SERVICE_ACCOUNT_EMAIL}

echo ""
echo "✅ Service Account Setup Complete!"
echo ""
echo "📋 Summary:"
echo "   • Service Account: ${SERVICE_ACCOUNT_EMAIL}"
echo "   • Key File: ${KEY_FILE}"
echo "   • Project ID: ${PROJECT_ID}"
echo ""
echo "🔒 Security Notes:"
echo "   • The JSON key file contains sensitive credentials"
echo "   • Upload the contents to GitHub Secrets (not the file itself)"
echo "   • Delete the local key file after uploading to GitHub"
echo "   • Never commit key files to your repository"
echo ""
echo "📤 Next Steps:"
echo "   1. Copy the contents of ${KEY_FILE}"
echo "   2. Go to your GitHub repository Settings > Secrets and variables > Actions"
echo "   3. Create a new secret named 'GCP_SA_KEY'"
echo "   4. Paste the JSON content into the secret value"
echo "   5. Delete the local ${KEY_FILE} file"
echo ""
echo "🗑️  To delete the local key file, run:"
echo "   rm ${KEY_FILE}"
