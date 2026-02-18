# KCB P11 Masking Spring Boot Starter + Books API Demo

A spring boot starter and demo showing sensitive data masking in logs/JSON.

- Spring Boot: 4.x (Jackson 3 `tools.jackson.*`)
- Java: 21 (LTS)
- Tests: JUnit 5 with Hamcrest
- Integration: Testcontainers (Oracle Free) + Rest-Assured

## Modules

- `p11-masking-spring-boot-starter`: Auto-configured Jackson module masking configured string fields.
- `books-api-demo`: Minimal CRUD API demonstrating the starter.

## Quick start

Requirements:
- Java 21
- Maven 3.9+
- Docker (for Testcontainers)

Run all tests (includes Testcontainers-based API tests):

```bash
mvn -q -DskipITs=false test
```

Run the demo API only:

```bash
mvn -q -pl books-api-demo spring-boot:run
```

## Config (demo)

```yaml
p11:
  masking:
    enabled: true
    mask-style: PARTIAL  # FULL | PARTIAL | LAST4
    mask-character: "*"
    fields:
      - email
      - phoneNumber
```

## Highlights

- Testcontainers: Oracle FreeDB container wired via `@ServiceConnection` for repeatable integration tests.
- Spring Boot 4 + Jackson 3: Contextual `String` serializer applies masking only to configured fields.
- Java 21: Records for DTOs and modern toolchain.
