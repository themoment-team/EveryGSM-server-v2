# CommonApiResponse — Reference

Import: `team.themoment.sdk.response.CommonApiResponse`

## Factory Methods

```java
CommonApiResponse.success(String message)              // HTTP 200
CommonApiResponse.created(String message)              // HTTP 201
CommonApiResponse.error(String message, HttpStatus status)
```

## JSON Shape

```json
{
  "status": "OK",
  "code": 200,
  "message": "수정되었습니다.",
  "data": null
}
```

`data` is always `null` for `success()` and `created()` — omitted in JSON via `@JsonInclude(NON_NULL)`.

## Usage in EveryGSM-server-v2

In this project the SDK's automatic response wrapping is **not** the primary pattern — controllers return DTOs (or `void`) directly. `CommonApiResponse` appears mainly inside:

- `AppExceptionHandler` — error responses (`CommonApiResponse.error(...)`)
- `JwtAccessDeniedHandler`, `JwtAuthenticationEntryPoint`, `CustomAuthenticationEntryPoint`, `RateLimitFilter` — security/filter error responses

```java
// AppExceptionHandler example
return ResponseEntity.status(e.getStatusCode())
        .body(CommonApiResponse.error(e.getMessage(), e.getStatusCode()));
```

```java
// Controller — DO NOT wrap in CommonApiResponse, return DTO directly
@GetMapping("/me")
public UserResDto queryMe(@AuthenticationPrincipal Long userId) {
    return queryUserService.execute(userId);
}
```

If automatic wrapping is enabled later via `sdk.response.enabled: true` in `application*.yml`, controllers may rely on the SDK to wrap their return values — but until then, do not call `CommonApiResponse.success(...)` / `.created(...)` from controller methods.
