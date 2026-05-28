# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned

- Wire `UserService.getPreferredReferral` / `setPreferredReferral` once the upstream
  endpoint paths are published.
- Reconcile `X-Integration` header with the upstream integration field once finalized.

## [0.1.0] — 2026-05-28

### Added

- Initial public release.
- `FernanClient` facade with five typed services: `UserService`, `StoreService`,
  `RefundService`, `ReferralService`, `HealthService`.
- JDK `java.net.http`-backed transport with virtual-thread executor.
- Typed `FernanException` with `ErrorType` enum covering every documented error mode.
- `ReferralChoice` value type to force explicit caller decisions on referral codes.
- `IntegrationSignal` (`X-Integration` header) for partner-attribution tracking.
- `FernanLocale` enum for the six locales the store/cooldown endpoints accept.
- Configurable `User-Agent` header on the client builder.
- Unit-test coverage for response/error mapping and Gson model round-trips.
