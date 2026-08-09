# Secret Santa Spring Boot API

Spring Boot 3 API for the Secret Santa theme, with a Java-only backend scope and an OpenAPI contract that defines the available endpoints.

The service exposes the same core endpoints from `openapi.yaml`, uses JWT bearer authentication for protected routes, and keeps persistence behind an in-memory repository so the example runs without external infrastructure. The supplied PostgreSQL-style schema is included as `src/main/resources/schema.sql` for a future database-backed implementation.

## Run

Requirements: Java 17+ and Maven 3.9+.

```bash
cd /Users/alexkayser/git/secret-santa-spring-boot
mvn clean package
java -jar target/secret-santa-spring-boot-1.0.0-SNAPSHOT.jar
```

The API listens on `http://localhost:8080`. Management endpoints use port `8081`.

## Included API surface

- `POST /v1/user`
- `POST /v1/user/signin`
- `POST /v1/user/refresh`
- `GET /v1/user/{id}`
- `POST /v1/group`
- `GET /v1/group/{id}`
- `POST /v1/group/{id}/participant`
- `GET /v1/group/{id}/participants`
- `POST /v1/group/{id}/draw`
- `GET /v1/group/{id}/friend`

`openapi.yaml` is the source of truth for the API contract used by this project.
