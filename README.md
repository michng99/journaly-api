# 📓 Journaly API

A robust Spring Boot application for journaling with AI-powered sentiment analysis, built for production scalability.

## 🚀 Features

- **Journal Management**: Create, read, update journal entries with pagination
- **AI-Powered Sentiment Analysis**: Azure AI Text Analytics integration with fallback
- **Tag Management**: Flexible tagging system with many-to-many relationships
- **Caching**: Redis-based caching for optimal performance
- **Security**: Production-ready security configuration with CORS support
- **Monitoring**: Prometheus metrics and health checks
- **Testing**: Comprehensive unit and integration tests with Testcontainers
- **Containerization**: Multi-stage Docker builds with health checks
- **CI/CD**: GitHub Actions pipeline with automated testing and deployment

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.5.5
- **Java**: 21
- **Database**: PostgreSQL (production), H2 (development)
- **Cache**: Redis
- **AI Service**: Azure Text Analytics
- **Monitoring**: Prometheus + Grafana
- **Testing**: JUnit 5, Testcontainers, REST Assured
- **Containerization**: Docker + Docker Compose

## 🏗️ Architecture

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │────│  API Layer  │────│ Service Layer│
└─────────────┘    └─────────────┘    └─────────────┘
                           │                    │
                   ┌─────────────┐    ┌─────────────┐
                   │Cache (Redis)│    │Database(PG) │
                   └─────────────┘    └─────────────┘
                           │
                   ┌─────────────┐
                   │ Azure AI    │
                   └─────────────┘
```

## 🚀 Quick Start

### Using Docker Compose (Recommended)

```bash
# Clone the repository
git clone <your-repo-url>
cd journaly-api

# Start all services
docker-compose up -d

# With monitoring (Prometheus + Grafana)
docker-compose --profile monitoring up -d

# Access the application
curl http://localhost:8080/api/entries/health
```

### Local Development

#### Prerequisites
- Java 21
- Maven 3.6+
- PostgreSQL 15+ (optional)
- Redis 7+ (optional)

```bash
# Install dependencies
./mvnw dependency:resolve

# Run tests
./mvnw test

# Run the application
./mvnw spring-boot:run

# With specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📊 API Endpoints

### Journal Entries
```bash
# Create entry
POST /api/entries/create
Content-Type: application/json
{
  "content": "Today was amazing! I feel very grateful."
}

# Get all entries (paginated)
GET /api/entries?page=0&size=10

# Get entry by ID
GET /api/entries/{entryId}

# Update tags
PUT /api/entries/{entryId}/tags
Content-Type: application/json
{
  "tagNames": ["#happy", "#grateful", "#productive"]
}
```

### Health & Monitoring
```bash
# Health check
GET /api/entries/health

# Detailed health with dependencies
GET /api/health

# Metrics (Prometheus format)
GET /actuator/prometheus

# Application info
GET /actuator/info
```

## 🔧 Configuration

### Environment Variables

#### Required for Production
```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/journaly
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# Azure AI (Optional)
AZURE_AI_KEY=your_azure_key
AZURE_AI_ENDPOINT=https://your-endpoint.cognitiveservices.azure.com/
```

## 🧪 Testing

```bash
# Unit tests
./mvnw test

# Integration tests with Testcontainers
./mvnw verify -P integration-tests

# Load testing (if configured)
./mvnw test -P load-tests

# Security scanning
./mvnw dependency-check:check
```

## 🐳 Docker Deployment

### Build Image
```bash
# Build optimized production image
docker build -t journaly-api:latest .
```

### Run Container
```bash
# Run with environment variables
docker run -d \
  --name journaly-api \
  -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/journaly \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=password \
  journaly-api:latest
```

## ☁️ Cloud Deployment

### AWS ECS
```bash
# Build and push to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account>.dkr.ecr.us-east-1.amazonaws.com
docker tag journaly-api:latest <account>.dkr.ecr.us-east-1.amazonaws.com/journaly-api:latest
docker push <account>.dkr.ecr.us-east-1.amazonaws.com/journaly-api:latest
```

### Azure Container Apps
```bash
# Build and push to ACR
az acr build --registry <registry-name> --image journaly-api:latest .

# Deploy to Container Apps
az containerapp create \
  --name journaly-api \
  --resource-group <resource-group> \
  --environment <environment-name> \
  --image <registry>.azurecr.io/journaly-api:latest
```

### Google Cloud Run
```bash
# Build and push to GCR
docker tag journaly-api:latest gcr.io/<project-id>/journaly-api:latest
docker push gcr.io/<project-id>/journaly-api:latest

# Deploy to Cloud Run
gcloud run deploy journaly-api \
  --image gcr.io/<project-id>/journaly-api:latest \
  --platform managed \
  --region us-central1
```

## 📈 Monitoring & Observability

### Prometheus Metrics
- Application metrics: `/actuator/prometheus`
- Custom business metrics
- JVM metrics, database connection pool metrics

### Grafana Dashboards
Access Grafana at `http://localhost:3000` (admin/admin)
- Application Overview Dashboard
- Database Performance Dashboard
- Redis Cache Dashboard

## 🔒 Security

### Production Security Checklist
- ✅ Input validation with Bean Validation
- ✅ HTTPS enforcement (configure reverse proxy)
- ✅ CORS configuration
- ✅ Security headers
- ✅ Dependency vulnerability scanning
- ✅ No sensitive data in logs
- ✅ Database connection encryption

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow
1. **Test Stage**: Unit tests, integration tests
2. **Security**: OWASP dependency check
3. **Build**: Multi-arch Docker images
4. **Deploy**: Staging → Production

## 📊 Performance

### Optimization Features
- ✅ Connection pooling (HikariCP)
- ✅ Redis caching
- ✅ Lazy loading
- ✅ Batch processing
- ✅ Async AI processing
- ✅ Query optimization

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Run tests (`./mvnw test`)
4. Commit changes (`git commit -m 'Add amazing feature'`)
5. Push to branch (`git push origin feature/amazing-feature`)
6. Open Pull Request

---

**⭐ If this project helped you, please give it a star!**