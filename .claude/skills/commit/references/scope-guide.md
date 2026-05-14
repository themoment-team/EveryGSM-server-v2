# Commit Type & Scope Selection Guide

## Type Selection Table

| Situation | Type | Example |
|-----------|------|---------|
| New API endpoint, new service class, new feature | `feat` | `feat(project): 프로젝트 등록 기능 추가` |
| Adding non-feature artifacts (assets, icons, configs) | `add` | `add(global): idea 아이콘 추가` |
| Bug fix, incorrect behavior corrected | `fix` | `fix(project): 프로젝트 동기화 중 생기는 오류 수정` |
| Enhancement of existing feature, dependency / version bump | `update` | `update(global): 버전 변경` |
| Code restructuring, no behavior change | `refactor` | `refactor(project): startYear 필드에 final을 붙여서 통일성` |
| Test additions or fixes only | `test` | `test(project): CreateProjectService 단위 테스트 추가` |
| Docker, CI/CD pipelines, GitHub Actions | `ci/cd` | `ci/cd(global): Docker Java 버전 변경` |
| Documentation changes only | `docs` | `docs(global): CLAUDE.md 규칙 섹션 추가` |
| Gradle config, dependency management (non-feature) | `chore` | `chore(global): AWS BOM 버전 변경` |
| Emergency production fix | `hotfix` (often scopeless) | `hotfix: 스케줄링 시간 롤백` |

## Scope Selection Table

`global` is fixed regardless of code structure. All other scopes correspond to a domain package.

| Scope | What it covers |
|-------|---------------|
| `global` | Shared infra, security config, AppExceptionHandler, ratelimit, scheduler, third-party clients, build/Gradle, Discord webhook |

Domain scopes correspond to subdirectories under `src/main/java/team/themoment/everygsm/server/v2/domain/`. Currently:

| Scope | Domain |
|-------|--------|
| `auth` | OAuth sign-in, JWT issuance |
| `admin` | Admin project approval/rejection, admin queries |
| `user` | User domain |
| `image` | Image upload (S3 + presigned URL) |
| `project` | Project, ProjectLike, project sync, project scheduler |

Discover the live list dynamically by listing `domain/` — do not assume the table above is exhaustive forever.

## Composite Changes

When a change touches multiple scopes, use the **primary affected scope**:

```
# Feature spans project + admin
feat(project): 프로젝트 승인 알림 기능 추가

# Affects global config AND project
update(global): RateLimit 설정 변경
```

If truly equal weight across multiple domains:
```
refactor(global): 공통 유틸리티 패키지 구조 개선
```

## Breaking Down Large Changes

When `git diff` shows changes in 3+ unrelated areas, split into multiple commits:

1. Infrastructure/config changes first: `chore(global): ...`
2. Domain logic next: `feat(project): ...`
3. Tests last (or together with domain): `test(project): ...`

Always ask the user before splitting if unclear.
