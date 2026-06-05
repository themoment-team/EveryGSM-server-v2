# ExpectedException — Reference

Import: `team.themoment.everygsm.server.v2.global.exception.error.ExpectedException`

> Note: although `the-sdk` ships its own `ExpectedException` (`team.themoment.sdk.exception.ExpectedException`), EveryGSM-server-v2 defines its **own** `ExpectedException` in the project's `global.exception.error` package and uses that one throughout. Always import the project class.

## Constructor Signatures

```java
// With descriptive message (preferred)
new ExpectedException(String message, HttpStatus statusCode)

// Status only (when status is self-explanatory)
new ExpectedException(HttpStatus statusCode)
```

Stack trace is skipped for performance (`fillInStackTrace()` returns `this`).

## Throw Patterns

```java
// Resource not found — include the offending ID
throw new ExpectedException("해당 유저가 존재하지 않습니다. userId=" + userId, HttpStatus.NOT_FOUND);

// Conflict — duplicate resource
throw new ExpectedException("이미 등록된 프로젝트입니다.", HttpStatus.CONFLICT);

// Bad request — business rule failure
throw new ExpectedException("이미지 파일이 비어있습니다.", HttpStatus.BAD_REQUEST);

// Unauthorized — unauthenticated access
throw new ExpectedException(HttpStatus.UNAUTHORIZED);

// Forbidden — insufficient permissions (e.g., non-admin trying admin-only action)
throw new ExpectedException(HttpStatus.FORBIDDEN);
```

## HTTP Status Mapping

| Scenario | HttpStatus |
|----------|-----------|
| Resource not found | `NOT_FOUND` (404) |
| Invalid input / business rule failure | `BAD_REQUEST` (400) |
| Unauthenticated access | `UNAUTHORIZED` (401) |
| Insufficient permissions | `FORBIDDEN` (403) |
| Duplicate resource | `CONFLICT` (409) |
| Internal server fault | `INTERNAL_SERVER_ERROR` (500) |

## Error Response Shape

`AppExceptionHandler` converts `ExpectedException` into:

```json
{
  "status": "NOT_FOUND",
  "code": 404,
  "message": "해당 유저가 존재하지 않습니다. userId=42",
  "data": null
}
```

5xx variants are also forwarded to `DiscordWebhookService` for alerting.

## Rules

- Messages should be Korean for business errors
- Include the offending identifier when relevant: `"... userId=" + userId`
- Never create subclasses — use `ExpectedException` for all business errors
- Never throw `RuntimeException` directly
