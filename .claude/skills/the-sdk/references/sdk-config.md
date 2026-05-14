# SDK Configuration — Reference

`com.github.themoment-team:the-sdk:1.5` is on the classpath. SDK features are configured under the `sdk:` key in profile-specific application config (`application-local.yml`, `application-stage.yml`, `application-prod.yml`).

## Current Project Configuration

EveryGSM-server-v2 uses a minimal SDK configuration. The most common live setting is just:

```yaml
sdk:
  exception:
    use-english-message: false
```

The project's own `AppExceptionHandler` and `GlobalAsyncExceptionHandler` cover exception handling, and controllers return DTOs directly rather than relying on the SDK's response wrapper. Adjust the toggles below only if you intentionally want to delegate that work to the SDK.

## Full Configuration Example (all options)

```yaml
sdk:
  logging:
    enabled: true
    not-logging-urls:
      - "/v3/api-docs/**"
      - "/swagger-ui/**"

  response:
    enabled: false              # Off in this project — controllers return DTOs directly
    not-wrapping-urls:
      - "/v3/api-docs/**"

  exception:
    enabled: false              # Off — AppExceptionHandler handles errors
    use-english-message: false

  swagger:
    enabled: true
    title: "EveryGSM API"
    paths-to-match:
      - "/api/v2/**"
```

## Options

### `sdk.logging`
| Key | Type | Description |
|-----|------|-------------|
| `enabled` | boolean | Enable/disable automatic HTTP request/response logging |
| `not-logging-urls` | list | URL patterns to exclude from logging (Ant-style) |

### `sdk.response`
| Key | Type | Description |
|-----|------|-------------|
| `enabled` | boolean | Enable/disable automatic `CommonApiResponse` wrapping (off by default in this project) |
| `not-wrapping-urls` | list | URL patterns to exclude from wrapping (Ant-style) |

### `sdk.exception`
| Key | Type | Description |
|-----|------|-------------|
| `enabled` | boolean | Enable/disable SDK's `ExpectedException` → error response conversion (off — `AppExceptionHandler` is used instead) |
| `use-english-message` | boolean | Use English for the default fallback error messages |

### `sdk.swagger`
| Key | Type | Description |
|-----|------|-------------|
| `enabled` | boolean | Enable/disable Swagger auto-configuration |
| `title` | string | API title shown in Swagger UI |
| `paths-to-match` | list | URL patterns to include in Swagger documentation |
