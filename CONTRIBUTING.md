# Contributing to club.fernan.api

Thanks for considering a contribution. This library is an unofficial Java client
for the [fernan.club](https://fernan.club) API. Contributions are welcome —
bug reports, feature requests, model additions when the upstream API evolves,
docs improvements, and integration recipes.

## Quick start

```bash
git clone https://github.com/trqlmao/club.fernan.api.git
cd club.fernan.api
./gradlew build
```

The build provisions JDK 17 automatically via the Gradle toolchain plugin
(foojay resolver) — you do not need JDK 17 pre-installed locally.

## Ways to contribute

| Kind                              | Process                                                            |
|-----------------------------------|--------------------------------------------------------------------|
| Bug report                        | Open a [GitHub issue](https://github.com/trqlmao/club.fernan.api/issues/new/choose) using the *Bug report* template. |
| Feature request                   | Open a *Feature request* issue. For new endpoints, link the upstream API doc. |
| Security vulnerability            | **Do not open a public issue.** See [SECURITY.md](SECURITY.md).    |
| Question about the upstream API   | Ask the fernan.club admins, not this repo.                         |
| Pull request                      | Fork, branch off `main`, open a PR. See *PR workflow* below.       |

## Pull request workflow

1. **Discuss first** for any change beyond a typo or a one-line bug fix. Open an
   issue describing the problem and your proposed approach. This avoids
   surprises and saves rework.
2. **Fork** the repo and create a feature branch:
   `git checkout -b feat/short-description` or `fix/short-description`.
3. **Write or update tests** for any behavior change. Unit tests live in
   `src/test/java`; we deliberately do not run integration tests against the
   live API.
4. **Format** with `./gradlew spotlessApply`. CI will reject misformatted code.
5. **Build green**: `./gradlew build` must pass on JDK 17 and 21.
6. **Commit messages** must follow [Conventional Commits](https://www.conventionalcommits.org/).
   See *Commit messages* below for the canonical format and types accepted.
7. **PR description**: explain *why*, not just *what*. If the change adds a new
   endpoint, link the upstream API documentation that justifies the shape.
8. **One concern per PR** when reasonable. Multiple unrelated changes are
   easier to review separately.

## Commit messages

Every commit subject **must** follow Conventional Commits:

```
<type>(<optional scope>): <imperative summary, lowercase, no period>

<optional body — wrap at ~72 chars — explain the why>

<optional Fixes #123 or BREAKING CHANGE: ... footer>
```

### Accepted types

| Type       | Use for                                                            |
|------------|--------------------------------------------------------------------|
| `feat`     | A new user-visible feature or public API surface                   |
| `fix`      | A bug fix                                                          |
| `docs`     | Documentation only (README, CONTRIBUTING, javadoc, examples)       |
| `style`    | Formatting, whitespace — no logic change                           |
| `fmt`      | Synonym for `style` when running the formatter                     |
| `refactor` | Code change that is neither a feature nor a fix                    |
| `perf`     | Performance improvement                                            |
| `test`     | Adding or correcting tests                                         |
| `build`    | Gradle config, dependency bumps, build scripts                     |
| `ci`       | GitHub Actions workflow or other CI configuration                  |
| `chore`    | Maintenance not covered above                                      |
| `revert`   | Reverts a previous commit; reference its hash in the body          |

### Scopes used in this repo

Keep the scope short and pick from the existing surface where possible:

- `models` / `model(<area>)` — `model(store)`, `model(refund)`, etc.
- `http` — transport and response handling
- `auth` — authentication strategies
- `service(<area>)` — `service(store)`, `service(refunds)`, etc.
- `exception` — error types and mapping
- `integration`, `locale`, `referral`
- `build`, `ci`, `examples`, `docs`

### Examples

```
feat(models): add Product.imageUrl
fix(http): preserve cooldown_ends_at when 429 carries it
docs(examples): replace placeholder API key with env var
build(gradle): bump Spotless to 6.26.0
ci: pin actions/setup-java to v4
test(refund): cover handled_at null on pending refund
fmt: apply spotlessApply after Java 21 syntax updates
```

### Hard rules

- Subject under ~72 chars; body wrapped at ~72 chars per line.
- Lowercase after the colon (`feat: add foo`, not `feat: Add foo`).
- No trailing period in the subject.
- Imperative mood (`add`, `fix`, `remove` — not `added`, `fixes`, `removed`).
- One logical change per commit. If multiple types fit, split the commit.
- Breaking changes add `!` after the type/scope and a `BREAKING CHANGE:` footer.
- Do **not** add `Co-Authored-By: Claude ...` (or any AI-attribution) trailers.
  Authors are responsible for the code they commit; AI tooling is not a co-author.

## Coding conventions

- Java 17 source level. No language features above Java 17 (project compiles
  with `--release 17` so newer features will fail the build).
- Records for DTOs. Immutable wherever practical (`final` fields, no setters).
- Gson `@SerializedName` for every snake_case JSON field — keep Java
  identifiers idiomatic (camelCase) regardless of wire format.
- One `FernanException` for all failures, categorized via `ErrorType`. Do not
  introduce new exception subclasses unless an entirely new error category
  arises that doesn't fit the existing enum.
- Public methods get Javadoc. Test methods do not (their names are the spec).
- Spotless + Palantir Java Format is the source of truth on style — let it
  reformat rather than hand-formatting.

## Adding a new endpoint

1. Add the model record(s) under `src/main/java/club/fernan/api/model/<domain>/`
   with Gson `@SerializedName` for every snake_case field.
2. Add the method to the appropriate service in
   `src/main/java/club/fernan/api/service/`.
3. Surface a top-level shortcut on `FernanClient` only if the endpoint is
   high-frequency and likely to be invoked from outside a service holder.
4. Add a Gson round-trip test for the model and (where reasonable) a builder
   test for the service method.
5. Update `CHANGELOG.md` under `[Unreleased]`.

## Reporting upstream API drift

If fernan.club ships a new field or a breaking shape change, please open an
issue with:

- The endpoint path
- The new request/response shape (verbatim)
- A link to wherever the upstream documented the change (forum, Discord, blog)

The maintainer will update the models; you're welcome to open a PR if you want
to do the work yourself.

## Code of Conduct

This project follows the [Contributor Covenant 2.1](CODE_OF_CONDUCT.md).
By participating, you agree to abide by it.

## License

By contributing, you agree that your contributions will be licensed under the
[MIT License](LICENSE).
