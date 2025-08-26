# Journaly API - Usage Examples

This document provides detailed examples of how to use the Journaly API endpoints.

## Base URL
```
http://localhost:8080
```

## Authentication

The API uses basic authentication. Default credentials (for development):
- **Username**: `admin`
- **Password**: `password`

## API Endpoints

### 1. Journal Entries

#### Create a Journal Entry
```bash
POST /api/journals
Content-Type: application/json
Authorization: Basic YWRtaW46cGFzc3dvcmQ=

{
    "content": "Today was a wonderful day! I learned a lot about Spring Boot and completed my first API project.",
    "tags": ["learning", "spring-boot", "achievement"]
}
```

**Response:**
```json
{
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "content": "Today was a wonderful day! I learned a lot about Spring Boot and completed my first API project.",
    "createdAt": "2025-08-26T08:17:56.342Z",
    "tags": [
        {
            "id": 1,
            "name": "learning"
        },
        {
            "id": 2,
            "name": "spring-boot"
        },
        {
            "id": 3,
            "name": "achievement"
        }
    ],
    "sentimentAnalysis": {
        "positive": 0.89,
        "negative": 0.05,
        "neutral": 0.06,
        "label": "POSITIVE"
    }
}
```

#### Get All Journal Entries
```bash
GET /api/journals
Authorization: Basic YWRtaW46cGFzc3dvcmQ=
```

#### Get Specific Journal Entry
```bash
GET /api/journals/{id}
Authorization: Basic YWRtaW46cGFzc3dvcmQ=
```

#### Update Journal Entry
```bash
PUT /api/journals/{id}
Content-Type: application/json
Authorization: Basic YWRtaW46cGFzc3dvcmQ=

{
    "content": "Updated content for my journal entry",
    "tags": ["updated", "reflection"]
}
```

#### Delete Journal Entry
```bash
DELETE /api/journals/{id}
Authorization: Basic YWRtaW46cGFzc3dvcmQ=
```

#### Update Tags for Entry
```bash
POST /api/journals/{id}/tags
Content-Type: application/json
Authorization: Basic YWRtaW46cGFzc3dvcmQ=

{
    "tags": ["new-tag", "another-tag"]
}
```

## Development Tools

### H2 Database Console
When running in development mode, you can access the H2 database console at:
```
http://localhost:8080/h2-console
```

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: (leave empty)

### Health Check
```bash
GET /actuator/health
```

## Testing with curl

### Create a new journal entry:
```bash
curl -X POST http://localhost:8080/api/journals \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46cGFzc3dvcmQ=" \
  -d '{
    "content": "My first journal entry via API!",
    "tags": ["api", "test", "first-entry"]
  }'
```

### Get all entries:
```bash
curl -X GET http://localhost:8080/api/journals \
  -H "Authorization: Basic YWRtaW46cGFzc3dvcmQ="
```

## Error Responses

### 400 Bad Request
```json
{
    "timestamp": "2025-08-26T08:17:56.342Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/api/journals"
}
```

### 401 Unauthorized
```json
{
    "timestamp": "2025-08-26T08:17:56.342Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Authentication required",
    "path": "/api/journals"
}
```

### 404 Not Found
```json
{
    "timestamp": "2025-08-26T08:17:56.342Z",
    "status": 404,
    "error": "Not Found",
    "message": "Journal entry not found",
    "path": "/api/journals/123"
}
```

## Environment Variables for Production

Set these environment variables for production deployment:

```bash
# Database Configuration
export DATABASE_URL="jdbc:postgresql://your-host:5432/your-database"
export DATABASE_USERNAME="your-username"
export DATABASE_PASSWORD="your-password"
export DATABASE_DRIVER="org.postgresql.Driver"

# Security
export SECURITY_USER_NAME="your-admin-username"
export SECURITY_USER_PASSWORD="your-secure-password"

# Azure AI Service
export AZURE_AI_KEY="your-azure-key"
export AZURE_AI_ENDPOINT="https://your-service.cognitiveservices.azure.com/"
```

## Next Steps

1. **Test the API** using the examples above
2. **Set up production environment** with PostgreSQL
3. **Configure Azure AI Service** for sentiment analysis
4. **Add more sophisticated authentication** (JWT tokens)
5. **Implement user registration and management**
6. **Add API rate limiting and monitoring**
