# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned

- Wire `UserService.preferredReferral()` / `preferredReferral(String)` once the
  upstream endpoint paths are published.
- Reconcile `X-Integration` header with the upstream integration field once
  finalized.

## [0.3.0] — 2026-05-28

### Changed

- **BREAKING**: every `get*` / `set*` accessor on the public surface was renamed
  to a bare name, completing the new-code naming convention across the library.
  Callers on `0.2.x` must update:
  - `FernanException`: `getType` → `type`, `getStatusCode` → `statusCode`,
    `getErrorId` → `errorId`, `getRetryAfter` → `retryAfter`,
    `getCooldownEndsAt` → `cooldownEndsAt`.
  - `StoreService`: `getStock` → `stock`, `getCooldowns` → `cooldowns`,
    `getPurchases` → `purchases`, `getPurchase` → `purchaseDetail`.
  - `UserService`: `getPreferredReferral` → `preferredReferral()`,
    `setPreferredReferral(String)` → `preferredReferral(String)`.
  - `Cooldown.getMaxPurchasable(int)` → `maxPurchasable(int)`.
  - `ReferralCode.getRemainingUses()` → `remainingUses()`.
- Formatting standardized on Palantir Java Format (4-space, 120-column) via
  Spotless; code style continues to follow Google Java Style conventions. The
  `examples/` sources are now covered by `spotlessCheck`. No API impact.

## [0.2.0] — 2026-05-28

### Added

- `DISCLAIMER.md` and a top-of-README callout making the unaffiliated
  status, no-compensation status, and AS-IS provision explicit.
- Conventions document in `CONTRIBUTING.md`: bare-name method convention for
  new code, Lombok usage rules, `@SerializedName`-on-every-field rule for
  obfuscation safety, async-first guidance, threading model.
- `FernanClientBuilder.onApiKeyChange(Consumer<String>)` listener fired
  whenever the active key rotates. Useful for persisting the new key to disk,
  vault, etc.
- `FernanClientBuilder.executor(ExecutorService)` so callers can plug their
  own pool. When set, the client does not shut the executor down.
- Lombok as a `compileOnly` + annotation-processor build-time dependency
  (not shipped in the jar). Applied selectively: `@RequiredArgsConstructor`
  on the five `*Service` classes, `@Getter @Accessors(fluent = true)` on
  `FernanClient`.
- `ApiKeyAuthTest` covering rotation, listeners, and null-safety.

### Changed

- **BREAKING**: `UserService.getApiKey()` renamed to `UserService.apiKey()`
  for naming consistency with the rest of the new-code convention. Callers
  on `0.1.x` must update.
- `UserService.regenerateApiKey()` now updates the client's stored API key
  synchronously when the response arrives. Subsequent requests on the same
  client use the rotated key without rebuild. Registered
  `onApiKeyChange` listener fires with the new key.
- Every record field now carries an explicit `@SerializedName("<wire>")`
  annotation, including fields whose Java name already matches the JSON
  name. This is for obfuscation safety: consumers that shade + obfuscate
  the jar (ProGuard / R8 / ZKM) would otherwise lose Gson's name-matching.
- README Quickstart, `CLAUDE.md`, `AltManagerExample`, and
  `ErrorHandlingExample` now lead with async chaining
  (`.thenAccept` / `.exceptionally`). `.join()` is documented as a footgun
  outside CLI/script contexts.
- Internal transport executor is now opt-in: callers passing
  `.executor(...)` own the lifecycle; `client.shutdown()` is a no-op for
  caller-supplied executors.

## [0.1.0] — 2026-05-28

### Added

- Initial public release.
- `FernanClient` facade with five typed services: `UserService`, `StoreService`,
  `RefundService`, `ReferralService`, `HealthService`.
- JDK `java.net.http`-backed transport with a cached daemon-thread executor.
- Typed `FernanException` with `ErrorType` enum covering every documented error mode.
- `ReferralChoice` value type to force explicit caller decisions on referral codes.
- `IntegrationSignal` (`X-Integration` header) for partner-attribution tracking.
- `FernanLocale` enum for the six locales the store/cooldown endpoints accept.
- Configurable `User-Agent` header on the client builder.
- Unit-test coverage for response/error mapping and Gson model round-trips.
