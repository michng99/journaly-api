# Journaly API

A Spring Boot REST API for a journaling application that provides secure user authentication and AI-powered journal entry management.

## Features

- 🔐 **Secure Authentication**: User registration and login with Spring Security
- 📝 **Journal Management**: Create, read, update, and delete journal entries
- 🏷️ **Tagging System**: Organize entries with customizable tags
- 🤖 **AI Integration**: Azure Text Analytics for enhanced entry processing
- 🗄️ **Database Support**: PostgreSQL for production, H2 for development/testing

## Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 21
- **Database**: PostgreSQL (production), H2 (development)
- **Security**: Spring Security
- **ORM**: Spring Data JPA
- **AI Service**: Azure Text Analytics
- **Build Tool**: Maven
- **Testing**: Spring Boot Test

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- PostgreSQL (for production)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/journaly-api.git
   cd journaly-api
   ```

2. Configure the database in `src/main/resources/application.properties`

3. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - User login

### Journal Entries
- `GET /api/journals` - Get all journal entries
- `POST /api/journals` - Create a new journal entry
- `GET /api/journals/{id}` - Get a specific journal entry
- `PUT /api/journals/{id}` - Update a journal entry
- `DELETE /api/journals/{id}` - Delete a journal entry
- `POST /api/journals/{id}/tags` - Update tags for an entry

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/journaly/api/
│   │       ├── config/          # Security configuration
│   │       ├── controller/      # REST controllers
│   │       ├── dto/            # Data Transfer Objects
│   │       ├── entity/         # JPA entities
│   │       ├── repository/     # Data repositories
│   │       └── service/        # Business logic
│   └── resources/
│       └── application.properties
└── test/
    └── java/                   # Unit and integration tests
```

## Configuration

The application uses Spring profiles for different environments:

- **Development**: Uses H2 in-memory database
- **Production**: Uses PostgreSQL

Configure your database connection in `application.properties`:

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/journaly
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

Your Name - your.email@example.com

Project Link: [https://github.com/yourusername/journaly-api](https://github.com/yourusername/journaly-api)
