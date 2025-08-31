# Security Checklist for GCP-GitHub Integration

## 🔐 Service Account Security

### ✅ Service Account Setup
- [ ] Created dedicated service account for GitHub Actions
- [ ] Used descriptive name: `github-actions-sa`
- [ ] Applied principle of least privilege
- [ ] Documented all assigned roles and permissions

### ✅ IAM Permissions (Minimal Required)
- [ ] `roles/cloudbuild.builds.builder` - Build container images
- [ ] `roles/storage.admin` - Access Container Registry
- [ ] `roles/run.admin` - Deploy to Cloud Run
- [ ] `roles/iam.serviceAccountUser` - Use service accounts
- [ ] `roles/cloudbuild.builds.editor` - Manage builds

### ❌ Avoid These Permissions
- [ ] **NEVER** grant `roles/owner`
- [ ] **NEVER** grant `roles/editor`
- [ ] **NEVER** grant `roles/iam.securityAdmin`
- [ ] **NEVER** grant broad compute permissions

## 🔑 Key Management

### ✅ Service Account Keys
- [ ] Generated JSON key using `gcloud` CLI
- [ ] Stored key content in GitHub Secrets (not file)
- [ ] Named secret: `GCP_SA_KEY`
- [ ] Deleted local JSON file immediately after upload
- [ ] Set key rotation schedule (quarterly)

### ✅ Key Security Practices
- [ ] Never commit keys to repository
- [ ] Never share keys via email/chat
- [ ] Never store keys in plain text files
- [ ] Use environment-specific service accounts

### 🗓️ Key Rotation Schedule
- [ ] **Quarterly rotation** of service account keys
- [ ] **Immediate rotation** if compromise suspected
- [ ] **Annual review** of service account permissions
- [ ] **Monthly audit** of key usage in Cloud Console

## 🛡️ GitHub Repository Security

### ✅ Repository Settings
- [ ] Enable branch protection for `main` branch
- [ ] Require pull request reviews
- [ ] Require status checks to pass
- [ ] Restrict force pushes
- [ ] Enable security alerts

### ✅ Secrets Management
- [ ] Use repository secrets (not organization secrets for sensitive data)
- [ ] Regular audit of secrets access
- [ ] Document all secrets and their purpose
- [ ] Remove unused secrets immediately

### ✅ Environment Protection
- [ ] Create `production` environment in GitHub
- [ ] Require manual approval for production deployments
- [ ] Limit environment access to authorized users
- [ ] Configure deployment timeout limits

## 🚨 Security Monitoring

### ✅ Cloud Audit Logs
- [ ] Enable Cloud Audit Logs for all services
- [ ] Monitor service account usage
- [ ] Set up alerts for unusual activity
- [ ] Regular review of access patterns

### ✅ GitHub Security Features
- [ ] Enable Dependabot alerts
- [ ] Configure CodeQL analysis
- [ ] Enable secret scanning
- [ ] Review security advisories regularly

### ✅ Monitoring Alerts
- [ ] Set up billing alerts for unexpected costs
- [ ] Monitor Cloud Build usage patterns
- [ ] Alert on failed authentication attempts
- [ ] Track resource usage anomalies

## 🔍 Security Scanning

### ✅ Container Security
- [ ] Trivy vulnerability scanning in pipeline
- [ ] Regular base image updates
- [ ] Non-root user in container
- [ ] Minimal container image size

### ✅ Code Security
- [ ] OWASP dependency scanning
- [ ] Static code analysis (SonarQube/CodeQL)
- [ ] Regular dependency updates
- [ ] Security linting rules

### ✅ Infrastructure Security
- [ ] Cloud Security Command Center enabled
- [ ] Regular security health checks
- [ ] Network security policies
- [ ] Encryption at rest and in transit

## 🚫 Security Anti-Patterns

### ❌ NEVER Do These Things:

1. **Key Management:**
   - Store keys in code repositories
   - Share keys via insecure channels
   - Use long-lived keys without rotation
   - Grant excessive permissions "just in case"

2. **GitHub Actions:**
   - Use `pull_request_target` with untrusted code
   - Output sensitive data in logs
   - Use third-party actions without verification
   - Store secrets in workflow files

3. **GCP Configuration:**
   - Use default service accounts
   - Grant project-level permissions for specific resources
   - Disable audit logging
   - Use weak authentication methods

## 🔄 Incident Response

### 🚨 If Keys Are Compromised:

1. **Immediate Actions (within 1 hour):**
   ```bash
   # Disable the service account
   gcloud iam service-accounts disable github-actions-sa@journaly-api-production.iam.gserviceaccount.com
   
   # List all keys for the service account
   gcloud iam service-accounts keys list --iam-account=github-actions-sa@journaly-api-production.iam.gserviceaccount.com
   
   # Delete all keys
   gcloud iam service-accounts keys delete [KEY_ID] --iam-account=github-actions-sa@journaly-api-production.iam.gserviceaccount.com
   ```

2. **Investigation (within 24 hours):**
   - Review Cloud Audit Logs for unauthorized access
   - Check GitHub Actions logs for suspicious activity
   - Analyze Cloud Build history for unexpected builds
   - Review Cloud Run deployments

3. **Recovery (within 48 hours):**
   - Create new service account with fresh keys
   - Update GitHub Secrets with new credentials
   - Test deployment pipeline
   - Document incident and improve security measures

### 📞 Emergency Contacts
- **Security Team:** [your-security-team@company.com]
- **DevOps Lead:** [devops-lead@company.com]
- **GCP Support:** [GCP Support Case]

## ✅ Security Validation Tests

### Regular Security Tests:

```bash
# Test 1: Verify minimal permissions
gcloud projects get-iam-policy journaly-api-production \
  --flatten="bindings[].members" \
  --format="table(bindings.role)" \
  --filter="bindings.members:github-actions-sa@journaly-api-production.iam.gserviceaccount.com"

# Test 2: Check for unused service accounts
gcloud iam service-accounts list --filter="disabled:true"

# Test 3: Verify key age
gcloud iam service-accounts keys list \
  --iam-account=github-actions-sa@journaly-api-production.iam.gserviceaccount.com \
  --format="table(name,validAfterTime,validBeforeTime)"

# Test 4: Check recent activity
gcloud logging read 'protoPayload.authenticationInfo.principalEmail="github-actions-sa@journaly-api-production.iam.gserviceaccount.com"' \
  --limit=50 \
  --format="table(timestamp,protoPayload.methodName,protoPayload.resourceName)"
```

## 📋 Monthly Security Review

### Review Checklist:
- [ ] Audit service account permissions
- [ ] Check key rotation dates
- [ ] Review failed authentication attempts
- [ ] Validate GitHub repository security settings
- [ ] Update security documentation
- [ ] Test incident response procedures
- [ ] Review and update this checklist

---

## 🏆 Security Score

Track your security posture:

| Category | Weight | Score | Max |
|----------|--------|-------|-----|
| Service Account Setup | 25% | ___/25 | 25 |
| Key Management | 30% | ___/30 | 30 |
| Repository Security | 20% | ___/20 | 20 |
| Monitoring & Alerts | 15% | ___/15 | 15 |
| Documentation | 10% | ___/10 | 10 |
| **Total Security Score** | 100% | **___/100** | **100** |

**Target:** Maintain 95+ security score at all times.

**Remember:** Security is not a destination, it's a journey. Regular reviews and updates are essential for maintaining a secure deployment pipeline.
