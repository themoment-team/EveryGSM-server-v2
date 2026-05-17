---
description: 로깅 관련 규칙 (SLF4J / @Slf4j 사용 시)
globs:
  - "**/*.java"
---

# Logging Rules — EveryGSM-server-v2

## Library
- SLF4J via Lombok `@Slf4j` annotation
- Logger field name: `log` (Lombok default)
- Manual `LoggerFactory.getLogger()` only in filters/non-Spring beans
- AWS CloudWatch appender available via `ca.pjer:logback-awslogs-appender:1.6.0`

## Log Levels

| Level   | When to use                                                         |
|---------|---------------------------------------------------------------------|
| `INFO`  | Root default; meaningful runtime events                             |
| `WARN`  | Expected business edge cases, fallback paths, suspicious inputs     |
| `ERROR` | Unexpected runtime exceptions, system failures, async failures, alert-worthy events (e.g., `[UNHANDLED-EXCEPTION]`, `[ASYNC-DISCORD-ERROR]`) |
| `DEBUG` | Local development diagnostics — guard hot paths with `if (log.isDebugEnabled())` |
| `TRACE` | Avoid in production — fine-grained flow tracing only                |

## Usage Patterns

### Service layer — expected edge case
```java
@Slf4j
@Service
public class CreateProjectService {
    public void execute(...) {
        if (alreadyExists) {
            log.warn("중복 프로젝트 감지: title={}", title);
        }
    }
}
```

### Exception handler — expected vs unexpected
```java
// AppExceptionHandler
log.error("[UNHANDLED-EXCEPTION]", e);
```

### Async failures
```java
// GlobalAsyncExceptionHandler
log.error("[ASYNC-DISCORD-ERROR] method: {}, exception: {}", method.getName(), ex);
```

## Format Rules
- Always use parameterized logging: `log.info("id={}", id)` — never string concatenation
- Include relevant identifiers (userId, projectId) in warn/error messages
- Korean messages are acceptable for business-logic warn/error (matches existing codebase)
- Tag well-known error categories with bracketed prefixes (e.g., `[UNHANDLED-EXCEPTION]`, `[ASYNC-DISCORD-ERROR]`) for log aggregation
- Do not log sensitive data: passwords, tokens, OAuth codes, full PII

## Configuration
- Profile-specific application config files: `application.yaml` (defaults), `application-local.yml`, `application-stage.yml`, `application-prod.yml`
- A `logback-spring.xml` is not currently checked into `src/main/resources/`. If/when one is added, route stage/prod profiles to AWS CloudWatch via the `logback-awslogs-appender` already on the classpath
- Critical errors are additionally forwarded to Discord via `DiscordWebhookService` (see `AppExceptionHandler` and `GlobalAsyncExceptionHandler`) — do not duplicate that wiring inside services

## Prohibited Patterns
- `System.out.println(...)` — forbidden
- `e.printStackTrace()` — forbidden; use `log.error("...", e)`
- String concatenation in log args: `log.info("id=" + id)` — use parameterized form
- Logging inside tight loops without guard (`if (log.isDebugEnabled())`)
- Logging access tokens, OAuth `authCode`, JWT contents, or any secret values
