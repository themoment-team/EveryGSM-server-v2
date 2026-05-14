# API Convention Rules — EveryGSM-server-v2

## URL Structure
```
/api/v2/{resource}[/{id}][/{sub-resource}]
```
- **Versioning:** always `/api/v2` prefix
- **Lowercase, hyphenated** path segments
- **Plural nouns** for collection resources

## Endpoint Patterns

| HTTP Method | Path pattern                          | Purpose                        |
|-------------|---------------------------------------|--------------------------------|
| `GET`       | `/api/v2/users/me`                    | Get current user's resource    |
| `GET`       | `/api/v2/projects/my`                 | Get caller's collection        |
| `GET`       | `/api/v2/projects/my/{id}`            | Get caller's single resource   |
| `GET`       | `/api/v2/admin/requests`              | Admin listing                  |
| `GET`       | `/api/v2/admin/requests/{id}`         | Admin single resource          |
| `POST`      | `/api/v2/auth/signin`                 | Action (auth)                  |
| `POST`      | `/api/v2/projects`                    | Create resource                |
| `POST`      | `/api/v2/images` (multipart/form-data)| Upload resource                |
| `PATCH`     | `/api/v2/admin/approve/{projectId}`   | Status update                  |
| `PATCH`     | `/api/v2/admin/reject/{projectId}`    | Status update with body        |

## Request Binding

```java
// Path variable
@PathVariable Long id
@PathVariable("projectId") Long projectId

// Query parameter
@RequestParam(defaultValue = "0") int page

// Request body — always @Valid for DTO bodies
@RequestBody @Valid CreateProjectReqDto reqDto

// Multipart upload
@RequestPart MultipartFile image

// Authenticated user ID — taken from Spring Security principal
@AuthenticationPrincipal Long userId
```

The principal is stored as a `Long` (the user ID) by the project's JWT authentication layer, so `@AuthenticationPrincipal Long userId` is the standard binding for the calling user.

## Controller Return Pattern

Controllers in this project **return DTOs (or `void`) directly** — they do NOT wrap responses in `ResponseEntity` or `CommonApiResponse`. Spring serializes the returned DTO to JSON.

```java
@GetMapping("/me")
public UserResDto queryMe(@AuthenticationPrincipal Long userId) {
    return queryUserService.execute(userId);
}

@PostMapping("/signin")
public OAuthSignInResDto signIn(@RequestBody @Valid OAuthSignInReqDto reqDto) {
    return oAuthSignInService.execute(reqDto);
}

@PatchMapping("/approve/{projectId}")
public ProjectResDto approve(@PathVariable("projectId") Long projectId) {
    return adminApproveProjectService.execute(projectId);
}
```

`CommonApiResponse` (from `the-sdk`) is reserved for the global exception handler's error responses — not for normal controller success returns.

## Swagger / OpenAPI Annotations

```java
@Tag(name = "Project", description = "프로젝트 API")
@RestController
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
public class ProjectController {

    @Operation(summary = "프로젝트 등록", description = "새 프로젝트를 등록합니다")
    @PostMapping
    public ProjectResDto create(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid CreateProjectReqDto reqDto) { ... }
}
```

- `@Tag` description: Korean
- `@Operation` summary/description: Korean
- `@Parameter(description = "...")` is used on `@PathVariable` arguments where useful
- `@ApiResponses` with `@ApiResponse` is used on auth-related endpoints (e.g., `AuthController`)
- All Swagger annotations are **required** on new endpoints

## Pagination
- Use Spring Data `Pageable` for list endpoints when needed
- Default: `page=0`, `size=10`
- Endpoint returns the page contents as a DTO (typically wrapping `List<T>` plus metadata)

## Date / Time
- JSON format: `yyyy-MM-dd` for `LocalDate`
- Annotate DTO fields: `@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")` when serialization needs to be pinned
- Timezone: `Asia/Seoul`

## Prohibited Patterns
- No `ResponseEntity` returns from controllers (only used inside `AppExceptionHandler`)
- No direct entity exposure in responses — always use DTO
- No non-versioned endpoints (must include `/api/v2`)
- No `@ResponseBody` in controllers — use `@RestController`
- No bare `String` / `Map` returns — use a typed `*ResDto` record