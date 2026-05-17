# Commit Convention Rules — EveryGSM-server-v2

## Format
```
{type}({scope}): {description}
```

- **No capital letter** at start of description
- **Korean** descriptions are standard in this project
- **No period** at end of description
- Subject line ≤ 72 characters

## Types

| Type      | When to use                                         | Example                                          |
|-----------|-----------------------------------------------------|--------------------------------------------------|
| `feat`    | New feature added                                   | `feat(project): 프로젝트 등록 기능 추가`          |
| `add`     | Adding non-feature artifacts (assets, configs, etc.)| `add(global): idea 아이콘 추가`                  |
| `fix`     | Bug fix                                             | `fix(project): 프로젝트에 startYear 필드 추가`   |
| `update`  | Enhancement / version bump on existing feature      | `update(global): 버전 변경`                      |
| `refactor`| Code restructuring without behavior change          | `refactor(project): startYear 필드에 final을 붙여서 통일성` |
| `test`    | Test additions or fixes                             | `test(project): CreateProjectService 테스트 추가` |
| `chore`   | Build config, dependency updates, tooling           | `chore(global): AWS BOM 버전 변경`               |
| `ci/cd`   | CI/CD pipeline, Docker, deployment config           | `ci/cd(global): Docker에서 Java 버전 변경`       |
| `docs`    | Documentation only changes                          | `docs(global): CLAUDE.md 규칙 섹션 추가`         |
| `hotfix`  | Emergency production fix (often without scope)      | `hotfix: 스케줄링 시간 롤백`                     |

`hotfix` may be used scopeless when the fix is urgent and immediately rolled to `main`.

## Scopes

| Scope       | Applies to                                                             |
|-------------|------------------------------------------------------------------------|
| `global`    | Shared infrastructure, config, security, exception handler, third-party clients, scheduler, build/Gradle |
| `auth`      | Auth domain (OAuth sign-in, JWT, principal handling)                   |
| `admin`     | Admin domain (project approval/rejection, admin queries)               |
| `user`      | User domain (entity, service, controller, DTO)                         |
| `image`     | Image domain (S3 upload, presigned URL)                                |
| `project`   | Project domain (entity, like, mapper, scheduler, sync, queries)        |

Pick the scope that matches the *primary* affected `domain/{feature}` package, or `global` for cross-cutting/infrastructure changes.

## Multi-file Commits
- Group logically related changes in one commit
- Split unrelated changes into separate commits
- Keep each commit buildable and test-passing (or compile-passing if no tests yet)

## Branch Strategy (Git Flow)
- `main` — production releases only
- `develop` — integration branch
- `feature/{description}` — feature branches cut from `develop`
- `fix/{description}` — bugfix branches cut from `develop`
- `refactor/{description}` — refactoring branches cut from `develop`
- `hotfix/{description}` — cut from `main`, merged to both `main` and `develop`

## Pull Request
- Target branch: `develop` (not `main`) — except hotfix branches, which target `main`
- PR title mirrors the commit subject: `{type}({scope}): {description}`
- PR body: Korean description of what changed and why

## Prohibited Patterns
- No `git commit --amend` on pushed commits
- No force push to `main` or `develop`
- No `--no-verify` to skip Spotless formatting hooks
- No commits with WIP code that breaks the build
