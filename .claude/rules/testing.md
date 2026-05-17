---
description: 테스트 파일 작성/수정 시에만 적용되는 규칙
globs:
  - "src/test/**/*.java"
  - "**/*Test.java"
---

# Testing Rules — EveryGSM-server-v2

## Framework
- JUnit 5 (Jupiter) — provided via `spring-boot-starter-test` (transitive)
- Mockito 5 (inline mock maker)
- Test slice starters available: `spring-boot-starter-data-jpa-test`, `spring-boot-starter-security-test`, `spring-boot-starter-webmvc-test`
- BDD-style assertions and stubbing

> The `src/test/` tree does not exist in the repository yet — when adding the first test, create the matching directory structure under `src/test/java/team/themoment/everygsm/server/v2/...`.

## Test Class Structure
Use the **Describe / Context / It** nested pattern:

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("{Feature} {Subject} 테스트")
class ActionFeatureServiceTest {

    @Mock
    private DependencyRepository dependencyRepository;

    @InjectMocks
    private ActionFeatureService service;

    @Nested
    @DisplayName("execute 메서드는")
    class Describe_execute {

        @Nested
        @DisplayName("정상적인 입력이 주어진 경우")
        class Context_with_valid_input {

            @BeforeEach
            void setUp() {
                given(dependencyRepository.findById(1L)).willReturn(Optional.of(entity));
            }

            @Test
            @DisplayName("결과를 반환한다")
            void it_returns_result() {
                // arrange / act / assert
            }
        }

        @Nested
        @DisplayName("존재하지 않는 ID가 주어진 경우")
        class Context_with_nonexistent_id {

            @Test
            @DisplayName("ExpectedException을 던진다")
            void it_throws_expected_exception() {
                assertThrows(ExpectedException.class, () -> service.execute(99L));
            }
        }
    }
}
```

## Naming Conventions

| Part            | Pattern                                  |
|-----------------|------------------------------------------|
| Test class      | `{ClassUnderTest}Test`                   |
| Outer class     | `@DisplayName("{Subject} 테스트")`       |
| Nested Describe | `Describe_{methodName}`                  |
| Nested Context  | `Context_{condition}`                    |
| Test method     | `it_{expected_behavior}()`               |

## Mockito Initialization
- Prefer `@ExtendWith(MockitoExtension.class)` for JUnit 5 unit tests
- Do not call `MockitoAnnotations.openMocks(this)` in new tests
- Keep a top-level `@BeforeEach` only when the test needs additional setup beyond mock initialization

## Stubbing Style — BDD (Preferred)
```java
// CORRECT — BDD style
given(repo.findById(1L)).willReturn(Optional.of(entity));
given(repo.existsById(1L)).willReturn(true);

// AVOID — Mockito classic style
when(repo.findById(1L)).thenReturn(Optional.of(entity));
```

## Verification
```java
verify(repo).save(any(ProjectJpaEntity.class));
verify(repo, never()).delete(any());
```

## Inline Mock Creation
```java
// Use mock() for one-off objects in test methods
UserJpaEntity user = mock(UserJpaEntity.class);
given(user.getId()).willReturn(1L);
```

## Exception Testing
```java
assertThrows(ExpectedException.class, () -> service.execute(invalidId));
```

## Source of Truth
**Service/business logic code is the source of truth — tests must match it.**
When a test fails because the service was correctly updated, fix the test, not the service.

## What to Test
- Service classes (under each `domain/{feature}/service/`) are the primary test target
- Controller tests are optional (covered by integration tests via `spring-boot-starter-webmvc-test` if added)
- Repository custom queries: test with a real DB in integration tests, not unit tests

## File Location
```
src/test/java/team/themoment/everygsm/server/v2/domain/{feature}/service/{ServiceName}Test.java
```

## Prohibited Patterns
- No `@SpringBootTest` for unit tests — use `@InjectMocks` + `@Mock`
- No mocking of entity fields unless necessary — prefer constructing via the entity's `@Builder`
- No testing internal implementation details — test observable behavior
- No `Thread.sleep()` in unit tests
- Do not fix a failing test by weakening the assertion — find the root cause
