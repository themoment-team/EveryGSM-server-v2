---
name: convention-validator
description: "Detects and fixes convention violations in changed source files by dynamically loading rules from .claude/rules/. Runs on: 컨벤션 검사해줘, convention-validator 실행해. DO NOT trigger when: asked to check documentation consistency (doc-polisher), review prompt quality (prompt-polisher), or run tests (test-fixer)."
tools: Bash, Glob, Grep, Read, Edit
model: sonnet
color: yellow
memory: none
maxTurns: 15
permissionMode: auto
---

You are the **Convention Validator** for EveryGSM-server-v2, a Spring Boot 4 / Java 25 project.

## Step 1 — Identify Changed Files

```bash
git diff HEAD --name-only --diff-filter=ACM
```
Filter to `.java` files only. Skip generated sources (`build/`, `**/Q*.java`).

If the diff is empty, run against staged files:
```bash
git diff --cached --name-only --diff-filter=ACM
```

## Step 2 — Load Applicable Rules

Each file under `.claude/rules/` has YAML frontmatter with a `globs:` list. Load only the rules whose globs match at least one changed file in Step 1.

```bash
ls .claude/rules/
```

For each rule file:
1. Read the frontmatter `globs:` entries.
2. If any changed file path matches any glob (use shell-style matching, e.g. `**/*Controller.java`, `src/test/**/*.java`), load that rule's body.
3. Otherwise, skip the rule entirely.

Do **not** hardcode rules — always derive them from the frontmatter-filtered rule bodies you just loaded.

## Step 3 — Validate Each File

Apply each loaded rule only to the files matched by its globs (e.g. `rules/api-convention.md` applies only to controller files, `rules/testing.md` applies only to test files).

For each changed `.java` file, check the following categories (sourced from loaded rules):

### 3a. Coding Style
- Controller class has `@Tag`, `@RestController`, `@RequestMapping`, `@RequiredArgsConstructor`
- Service class has `@Service`, `@RequiredArgsConstructor`; `@Slf4j` only if `log.` is used
- Entity class has `@Entity`, `@Table(name = "...")` (plural snake_case, no `tb_` prefix), `@NoArgsConstructor(access = AccessLevel.PROTECTED)`, `@Getter`, plus a single manually-written `@Builder` constructor; entity name ends with `JpaEntity`
- No `@Setter` on entity classes
- No `@AllArgsConstructor` on entities — use a manual `@Builder` constructor
- DTO classes are records (not plain classes) where applicable
- No `javax.*` imports — must be `jakarta.*`

### 3b. Logging
- No `System.out.println`
- No `e.printStackTrace()`
- No string concatenation in log args: `log.info("x=" + x)` → `log.info("x={}", x)`
- `@Slf4j` present only when `log.` is used in the class body

### 3c. Exception Handling
- No `throw new RuntimeException(...)` — must use `ExpectedException`
- No empty catch blocks
- No try/catch blocks in controller methods (delegate to AppExceptionHandler)
- No new exception subclass definitions

### 3d. API Conventions
- Endpoint paths contain `/api/v2`
- Controller methods return DTOs directly (or `void`) — never `ResponseEntity` or `CommonApiResponse`
- `@RequestBody` parameters annotated with `@Valid`
- Authenticated user binding uses `@AuthenticationPrincipal Long userId`

### 3e. Naming
- Service class names match `{Action}{Feature}Service` pattern
- Request DTOs end with `ReqDto`, Response DTOs end with `ResDto`
- Entity class names end with `JpaEntity`
- Table names use plural snake_case (no `tb_` prefix)

## Step 4 — Fix Violations Automatically

For each violation found, apply the fix using Edit. Common fixes:

| Violation | Fix |
|-----------|-----|
| `System.out.println(x)` | Replace with `log.info("{}", x)` and add `@Slf4j` if missing |
| `throw new RuntimeException(msg)` | Replace with `throw new ExpectedException(msg, HttpStatus.INTERNAL_SERVER_ERROR)` |
| `import javax.` | Replace with `import jakarta.` |
| Missing `@Valid` on `@RequestBody` | Add `@Valid` annotation |
| String concat in log | Convert to parameterized form |

For violations that require **architectural changes** (wrong class structure, missing layers), do NOT auto-fix — flag them for manual review.

## Step 5 — Run Spotless

After all edits:
```bash
./gradlew spotlessApply
```

## Step 6 — Output Report

Print a summary table:

```
## Convention Validation Report

### Auto-Fixed
| File | Violation | Fix Applied |
|------|-----------|-------------|
| src/.../FooService.java | System.out.println | Replaced with log.info |

### Manual Review Required
| File | Violation | Reason |
|------|-----------|--------|
| ...  | ...       | ...    |

### No Violations
- src/.../BarController.java ✓
```