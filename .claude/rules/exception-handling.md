# Exception Handling Rules — EveryGSM-server-v2

## Exception Hierarchy
There is exactly **one custom exception class** in this project:

```java
// team.themoment.everygsm.server.v2.global.exception.error.ExpectedException
@Getter
public class ExpectedException extends RuntimeException {
    private final HttpStatus statusCode;

    public ExpectedException(String message, HttpStatus statusCode) { ... }
    public ExpectedException(HttpStatus statusCode) { ... }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;  // Skip stack trace for performance
    }
}
```

**Never create additional custom exception subclasses.** All business errors go through `ExpectedException`.

## Throwing Exceptions

### With a descriptive message (preferred)
```java
throw new ExpectedException("해당 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
```

### Message-only status (when status is self-explanatory)
```java
throw new ExpectedException(HttpStatus.UNAUTHORIZED);
```

## Message Format
- Korean messages for business errors
- Include the offending identifier when relevant: `"해당 유저가 존재하지 않습니다. userId=" + userId`
- Keep messages concise and user-readable

## HTTP Status Usage

| Scenario                              | HttpStatus                    |
|---------------------------------------|-------------------------------|
| Resource not found                    | `NOT_FOUND` (404)             |
| Invalid input / business rule failure | `BAD_REQUEST` (400)           |
| Unauthenticated access                | `UNAUTHORIZED` (401)          |
| Insufficient permissions              | `FORBIDDEN` (403)             |
| Conflict (duplicate resource)         | `CONFLICT` (409)              |
| Internal server fault                 | `INTERNAL_SERVER_ERROR` (500) |

## Error Response Format (`CommonApiResponse`)
`CommonApiResponse` is provided by the external `the-sdk` library (`team.themoment.sdk.response.CommonApiResponse`) and is used inside the global exception handler:

```java
return ResponseEntity.status(e.getStatusCode())
        .body(CommonApiResponse.error(e.getMessage(), e.getStatusCode()));
```

Note: in this project controllers generally return DTOs directly, not `CommonApiResponse`. The wrapper appears mainly in error responses and select write endpoints.

## Global Exception Handler (`AppExceptionHandler`)
- Located at `team.themoment.everygsm.server.v2.global.exception.AppExceptionHandler`
- `@RestControllerAdvice` centralizes all handling — do NOT add local try/catch in controllers
- 5xx `ExpectedException` and any unhandled `Exception` are also forwarded to `DiscordWebhookService` for alerting
- Handled exception types:

| Exception                              | Response status              | Notes |
|----------------------------------------|------------------------------|-------|
| `ExpectedException`                    | exception's `statusCode`     | 5xx variants additionally trigger Discord alert |
| `NoResourceFoundException`             | NOT_FOUND (404)              | |
| `HttpMessageNotReadableException`      | BAD_REQUEST (400)            | "요청 바디를 읽을 수 없습니다." |
| `MethodArgumentNotValidException`      | BAD_REQUEST (400)            | First binding error message |
| `MissingServletRequestParameterException` | BAD_REQUEST (400)         | Includes parameter name |
| `MethodArgumentTypeMismatchException`  | BAD_REQUEST (400)            | Includes parameter name |
| `Exception` (catch-all)                | INTERNAL_SERVER_ERROR (500)  | Logs as `[UNHANDLED-EXCEPTION]` + Discord alert |

## Async Exception Handler
- `GlobalAsyncExceptionHandler` (`@Component`, implements `AsyncUncaughtExceptionHandler`) handles `@Async` method failures
- Logs `[ASYNC-DISCORD-ERROR]` at `error` level with method name
- Forwards the failure to `DiscordWebhookService.sendServerError(...)`
- Does NOT return a response (async fire-and-forget)

## Prohibited Patterns
- Do not create new exception subclasses
- Do not swallow exceptions with empty catch blocks
- Do not use checked exceptions for business logic
- Do not throw `RuntimeException` directly — use `ExpectedException`
- Do not add try/catch in controllers — delegate to `AppExceptionHandler`