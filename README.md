# MyCityGov

Simple Spring Boot web app for managing citizen requests and appointments.

## Prerequisites
- Java 21
- Maven (or use the included Maven Wrapper)

## External service (required for some features)
This project uses the NOC external service for:
- Phone number validation during registration
- Gov login (AFM + PIN)

Start the NOC service first:
- Repo: https://github.com/AnastasisDimou/NOC-ExternalService
- Default URL expected by this app: http://localhost:8081

If you run it on another host/port, update:
- `src/main/resources/application.yml` -> `external.service.base-url`

## Run the app
From the project root:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

App will start at:
- http://localhost:8080

## Default users (seeded on startup)
Password for all accounts: `password`
- citizen@example.com (CITIZEN)
- employee@example.com (EMPLOYEE)
- tech.employee@example.com (EMPLOYEE)
- clean.employee@example.com (EMPLOYEE)
- admin@example.com (ADMIN)

## Useful URLs
- Home: http://localhost:8080/
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- H2 Console: http://localhost:8080/h2-console

## Notes
- Database is H2 (file-based) and the schema is recreated on each run.
- If the NOC service is not running, registration and gov login will fail.
