---
name: write-pr
description: Analyzes commits since branching from base, generates PR title/body following project conventions, and creates the PR via GitHub CLI.
---

You are executing the **write-pr** skill for EveryGSM-server-v2.

## Step 1 — Determine Base Branch

```bash
git branch --show-current
git log --oneline origin/develop..HEAD
```

Default base is `develop`. If on a hotfix branch, base is `main`.

Confirm with user if base branch is unclear.

## Step 2 — Analyze Commits

```bash
# All commits since branching from base
git log origin/develop..HEAD --oneline
git diff origin/develop...HEAD --stat
git diff origin/develop...HEAD
```

Read the full diff to understand:
- Which modules/packages changed (explore the diff to discover affected domains dynamically)
- What was added vs modified vs deleted
- Whether tests were added
- Whether documentation was updated

## Step 3 — Load Commit Convention

Read `.claude/skills/commit/references/commit-convention.md` for context on scope names. Note that **PR titles in this project do NOT follow the commit message format** — they use a different `[domain]` prefix style described in Step 4.

## Step 4 — Compose PR Title

Format: `[{domain}] {Korean description}`

Rules:
- The domain tag in `[...]` is one of the project scopes: `global`, `auth`, `admin`, `user`, `image`, `project`
- Pick the **primary domain** affected by the PR (not necessarily the union of all touched packages)
- Use `[global]` for cross-cutting / infrastructure / config / security / SDK / build changes
- Description is in Korean, no leading commit type, no trailing period
- Title ≤ 72 characters
- Release PRs (e.g., `v20260427.0`) are an exception — they use the version string as the entire title

Examples (drawn from real project history):
- `[project] 프로젝트 삭제 API 구현`
- `[project] 프로젝트 등록 요청 시 디스코드 웹훅으로 메시지 전송하는 기능 구현`
- `[global] CORS 허용 출처 경로 추가`
- `[global] IP 기반 Rate-Limit 기능 구현`
- `[admin] 프로젝트 거절 사유 검증 로직 수정`

## Step 5 — Compose PR Body

Follow the structure defined in @.github/PULL_REQUEST_TEMPLATE.md. The template's sections are:

- `# 개요` — 1~3 sentences explaining the purpose and background of the PR
- `## 작업 내용` — concrete list of what changed in this PR
- `## 관련 이슈` — linked issue (delete the section if none)
- `## 체크리스트` — keep all three checkboxes; tick the ones already verified
- `## 기타` — additional context (delete the section if none)

Rules for body composition:
- Write in Korean
- Drop empty optional sections (`관련 이슈`, `기타`) rather than leaving them blank
- Keep the `## 체크리스트` section as-is from the template; do not add or remove items
- `## 작업 내용` should be a bullet list grouped by area when multiple domains are touched

## Step 6 — Check Remote & Create PR

```bash
# Push if not yet pushed
git push -u origin $(git branch --show-current)
```

Create PR with gh CLI:
```bash
gh pr create \
  --base develop \
  --title "{title}" \
  --body "$(cat <<'EOF'
{body content here}
EOF
)"
```

## Step 7 — Output

After creating the PR, output the result in this format:

```
## PR Created

Title: {title}
Base:  develop ← {current-branch}
URL:   {pr-url}
```

## Prohibited Patterns
- Do not merge the PR
- Do not force-push to prepare for PR
- Do not create PR against `main` unless it's a hotfix branch
