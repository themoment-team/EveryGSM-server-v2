# Coding Style Rules — EveryGSM-server-v2

## Language & Framework
- Java 25, Spring Boot 4.0.3, Gradle
- Base package: `team.themoment.everygsm.server.v2`
- Jakarta EE (not `javax`) for validation, persistence annotations
- Lombok is mandatory — never write boilerplate manually

## Class-Level Annotations

### Controller
```java
@Tag(name = "...", description = "...")   // Korean descriptions for Swagger
@RestController
@RequestMapping("/api/v2/{resource}")
@RequiredArgsConstructor
public class XxxController { }
```

### Service
```java
@Service
@RequiredArgsConstructor
// @Slf4j only when the class actually logs
public class ActionFeatureService { }
```

### Entity
```java
@Entity
@Table(name = "{plural_snake_case}")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class EntityNameJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ...fields...

    @Builder
    public EntityNameJpaEntity(...) {
        this.field = field;
    }
}
```

- Entity classes use the `JpaEntity` suffix (e.g., `UserJpaEntity`, `ProjectJpaEntity`)
- Use a single `@Builder` constructor — do NOT use `@AllArgsConstructor`
- `@EntityListeners(AuditingEntityListener.class)` is NOT used in this project; timestamps are managed manually via `@Column(name = "...", nullable = false, updatable = false)` + service-layer assignment

### Request DTO (record)
```java
public record ActionEntityReqDto(
    @NotBlank String fieldName,
    @NotNull EnumType enumField
) { }
```

### Response DTO (record)
```java
public record FoundEntityResDto(
    Long id,
    String name
) { }
```

## Naming Conventions

| Component       | Pattern                          | Example                              |
|-----------------|----------------------------------|--------------------------------------|
| Controller      | `{Feature}Controller`            | `ProjectController`, `AdminController` |
| Service         | `{Action}{Feature}Service`       | `CreateProjectService`, `QueryUserService` |
| Request DTO     | `{Entity}{Action}ReqDto` or `{Action}ReqDto` | `CreateProjectReqDto`, `OAuthSignInReqDto` |
| Response DTO    | `{ResultDesc}ResDto`             | `ProjectResDto`, `MyPageResDto`      |
| Internal DTO    | `{Description}Dto`               | `TechStackDto`                       |
| Repository      | `{Entity-stripped}Repository`    | `UserRepository`, `ProjectRepository` (entity = `UserJpaEntity`) |
| Entity          | `{DomainName}JpaEntity`          | `UserJpaEntity`, `ProjectJpaEntity`, `LikeJpaEntity` |
| Enum Type       | plain noun in `entity/constant/` | `Role`, `Status`                     |
| DB table        | plural snake_case (no prefix)    | `users`, `projects`                  |

## Domains
The current `domain/` packages are: `auth`, `admin`, `user`, `image`, `project`. Each domain typically has `controller/`, `service/`, `dto/{request,response,common}/`, `entity/`, `entity/constant/`, `repository/`, `mapper/`, `util/`.

## Lombok Rules
- Use `@RequiredArgsConstructor` instead of manual constructors
- Use `@Getter` on entities (no `@Setter` on entities)
- Use a manually-declared `@Builder` constructor on entities for controlled field assignment
- Use `@Slf4j` only on classes that actually emit log statements
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` on JPA entities

## Immutability
- Prefer Java records for DTOs (request and response)
- Never expose `@Setter` on JPA entities; use dedicated update methods instead
- Enum fields on entities must use `@Enumerated(EnumType.STRING)`

## Code Formatting (Spotless)
- 4-space indentation (no tabs) — `leadingTabsToSpaces(4)`
- Eclipse formatter config: `src/main/resources/eclipse-java-formatter.xml`
- Import order (Spotless `importOrder`): `java` → `javax` → `org` → `com` → blank — `jakarta.*`, `lombok.*`, `team.*` fall in the blank group
- Remove unused imports automatically (`removeUnusedImports()`)
- Trim trailing whitespace, end files with newline
- Run `./gradlew spotlessApply` before committing

## Spring Data JPA
- Repository interface extends `JpaRepository<{Entity}JpaEntity, Long>`
- Custom QueryDSL queries live in `repository/custom/impl/` when used
- Audit fields (`createdAt`, etc.) are declared explicitly as `@Column` — no `@CreatedDate`/`@LastModifiedDate` Spring auditing in this project

## Validation
- Use Jakarta Bean Validation on records: `@NotBlank`, `@NotNull`, `@Pattern`
- Custom validators live in `domain/{feature}/annotation/` if introduced
- Always annotate controller parameters with `@Valid` when binding `@RequestBody` DTOs

## Prohibited Patterns
- No `System.out.println` — use `log.*` via `@Slf4j`
- No raw `new RuntimeException` — use `ExpectedException`
- No `@Setter` on JPA entities
- No `javax.*` imports — use `jakarta.*`
- No `tb_` prefix on table names (this project uses plain plural names)
- No `@AllArgsConstructor` on entities — use a manually-written `@Builder` constructor