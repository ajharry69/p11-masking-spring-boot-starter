# P11 Masking Spring Boot Starter + Books API Demo

![Maven Central Version](https://img.shields.io/maven-central/v/ke.co.xently/p11-masking-spring-boot-starter)
[![Java CI with Maven](https://github.com/xently/p11-masking-spring-boot-starter/actions/workflows/maven.yml/badge.svg)](https://github.com/xently/p11-masking-spring-boot-starter/actions/workflows/maven.yml)
[![Maven Central Publish](https://github.com/xently/p11-masking-spring-boot-starter/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/xently/p11-masking-spring-boot-starter/actions/workflows/maven-publish.yml)

A spring boot starter and demo showing sensitive data masking in logs.

- Spring Boot: 4.x (Jackson 3 `tools.jackson.*`)
- Java: 21 (LTS)
- Tests: JUnit 5 with Hamcrest
- Integration: H2 + Rest-Assured

## Modules

- `p11-masking-spring-boot-starter`: Log masking for sensitive data.
- `demo`: Minimal CRUD API demonstrating the starter.

## Quick start

Requirements:
- Java 21
- Maven 3.9+

Run all tests:

```bash
mvn -q test
```

Run the demo API:

```bash
mvn -pl demo -am spring-boot:run
```

The APIs can be tested through - http://localhost:8080/scalar

## Config (demo)

```yaml
log:
  forging:
    replacement: " " # optional, default (space)
    replace-continuous-at-once: true # optional, default true
  p11:
    masking:
      enabled: true
      mask-style: PARTIAL  # FULL | PARTIAL
      mask-character: "*"
      default-mask-length: 8
      partial-exemption:
        from-start: 1
        from-end: 0
        min-unmasked-length: 3
        mask-if-short: true
      fields:
        - email
        - phoneNumber
```

## Masking styles

The starter supports two masking styles:

### FULL masking
The entire value is replaced with a mask of a fixed length (configured via `default-mask-length`).

### PARTIAL masking
Only parts of the value are masked, leaving some characters visible (e.g., for verification purposes).

Configuration options for `PARTIAL` masking:
- `from-start`: Number of characters to leave unmasked at the beginning.
- `from-end`: Number of characters to leave unmasked at the end.
- `min-unmasked-length`: Minimum length the string must have for partial masking to be applied.
- `mask-if-short`: If the string is shorter than `min-unmasked-length` (after accounting for exemptions), should it be fully masked (true) or left unmasked (false).

Email addresses are handled specially in `PARTIAL` mode: only the username part is partially masked, while the domain remains fully visible.

### Pattern-based masking

You can define custom regular expressions to find and mask sensitive data in log messages using `log.p11.masking.patterns`.

- **With capturing groups**: If the regex contains capturing groups, only the content of those groups will be masked. This is ideal for masking values within a specific format (e.g., `key=value` or JSON).
- **Without capturing groups**: The entire text matched by the regex will be masked.

Example configuration:
```yaml
log:
  p11:
    masking:
      patterns:
        # Mask only the value part of a token parameter
        - '(?:[a-z]+_)*token(?:_[a-z]+)*\s*=([^"]+)[])]'
        # Mask only the value in a JSON "token" field
        - '"(?:[a-z]+_)*token(?:_[a-z]+)*"\s*:\s*"([^"]+)"'
```

## Log masking (no code changes)

The starter installs a Logback `%msg` converter that masks sensitive values in log output.
It works for structured messages (e.g., `email=...`, JSON snippets, Lombok `toString`) and
common patterns like emails, phone numbers, and card numbers. Default field names are used
if `log.p11.masking.fields` is not provided, and any configured fields override the defaults.

Default fields:
`password`, `passcode`, `secret`, `token`, `accessToken`, `refreshToken`, `ssn`,
`creditCard`, `cardNumber`, `email`, `phone`, `phoneNumber`, `accountNumber`, `pin`.

## Annotation-based masking

Use `@Mask` to force masking on a specific field even if it is not listed in `p11.masking.fields`.
Annotation settings override the global properties.

```java
import co.ke.xently.log.mask.Mask;
import co.ke.xently.log.mask.LogProperties;

public record UserDto(
        String name,
        @Mask String ssn,
        @Mask(style = MaskingStyle.PARTIAL, maskCharacter = "#") String cardNumber
) {}
```

Note: `MaskingStyle` is an enum available in the `co.ke.xently.log.mask` package.

## Log forging prevention

Log forging (or log injection) occurs when an attacker includes control characters (like newlines) in input that is then logged, potentially allowing them to spoof log entries or bypass log analysis tools.

The starter automatically sanitizes all simple values (Strings, Numbers, etc.) and provides the `@NoLogForging` annotation for more granular control.

### Automatic sanitization

By default, all simple arguments passed to log messages are sanitized. Control characters (`\n`, `\r`, `\t`) are replaced with the configured replacement (default is `_`).

### Annotation-based prevention

Use `@NoLogForging` on a class, record component, field, or method to ensure its value is sanitized before being logged.

```java
import co.ke.xently.log.mask.NoLogForging;

@NoLogForging
public record TransactionRequest(
    String description, // This will be sanitized
    double amount
) {}
```

### Config

```yaml
log:
  forging:
    replacement: "_" # Character to replace control characters with
    replace-continuous-at-once: true # Replace consecutive control characters with a single replacement character
```

## Highlights

- H2: In-memory database with initial data loaded via `import.sql` for repeatable integration tests.
- Logback: `%msg` converter masks sensitive values in log output without manual `ObjectMapper` calls.
- Java 21: Records for DTOs and modern toolchain.
