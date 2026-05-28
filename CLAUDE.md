# CLAUDE.md — Integrating club.fernan.api

This file is intended for AI coding assistants (Claude Code, Copilot, Cursor, etc.)
that are helping a developer integrate this library into a Java project. The
goal is to give the assistant enough context to write correct code on the first
try.

## What this library is

A Java client for the [fernan.club](https://fernan.club) REST API
(`https://api.fernan.club/api/v1`). Pure JDK + Gson. No reflection that breaks
under shading. Safe to drop into Minecraft mods, standalone JVM apps, or any
JDK 17+ codebase.

Public surface lives entirely under the `club.fernan.api` package; nothing
escapes it.

## Adding the dependency

JitPack-hosted. Gradle (Kotlin DSL):

```kotlin
repositories { maven { url = uri("https://jitpack.io") } }
dependencies { implementation("com.github.trqlmao:club.fernan.api:0.1.0") }
```

## The 30-second mental model

```
FernanClient.builder()
    .apiKey(...)
    .build()
        .user()   -> UserService
        .store()  -> StoreService
        .refunds()-> RefundService
        .referrals()-> ReferralService
        .health() -> HealthService

Every service method returns CompletableFuture<T>.
Every error surfaces as FernanException with a typed ErrorType.
```

## Building a client

```java
FernanClient client = FernanClient.builder()
        .apiKey(apiKey)                  // required
        .userAgent("my-app/1.0")         // optional, identifies your app
        .integration("my-app")           // optional, partner-attribution
        .baseUrl("https://api.fernan.club/api/v1")  // optional override
        .connectTimeoutMillis(10_000)    // optional
        .build();
```

Call `client.shutdown()` on application exit to release the virtual-thread executor.

## Async patterns

All methods are non-blocking. Pick the pattern that fits your call site:

```java
// Blocking join (fine for CLI / scripts / sync code paths):
User me = client.user().me().join();

// Continuation:
client.user().me().thenAccept(user -> render(user));

// With error handling:
client.store().purchase(1, 5, ReferralChoice.none())
    .thenAccept(this::onSuccess)
    .exceptionally(t -> { onFailure(t); return null; });
```

If you need to marshal the result back onto a specific thread (UI thread, game
tick thread, etc.) wrap it in your runtime's executor: e.g.
`.thenAcceptAsync(handler, gameThread)`.

## Error handling

Every API failure surfaces as `FernanException`. Inspect `getType()`, not the
raw HTTP status:

```java
import club.fernan.api.exception.ErrorType;
import club.fernan.api.exception.FernanException;

future.exceptionally(t -> {
    FernanException e = (FernanException) (t instanceof java.util.concurrent.CompletionException ? t.getCause() : t);
    switch (e.getType()) {
        case AUTHENTICATION       -> /* invalid/missing key */;
        case BANNED               -> /* account is banned */;
        case VALIDATION           -> /* bad request */;
        case NOT_FOUND            -> /* resource gone */;
        case INSUFFICIENT_BALANCE -> /* need to top up */;
        case COOLDOWN             -> /* e.getCooldownEndsAt() */;
        case RATE_LIMITED         -> /* e.getRetryAfter() seconds */;
        case CONFLICT             -> /* duplicate refund */;
        case SERVER_ERROR         -> /* e.getErrorId() for support */;
        case NETWORK              -> /* connection failure */;
        default                   -> /* UNKNOWN */;
    }
    return null;
});
```

## Referral codes — important

The library deliberately requires an explicit `ReferralChoice` on every purchase.
Do **not** silently default to a fixed referral code in your integration.
If you're surfacing referrals to end users, prompt them to choose:

```java
import club.fernan.api.model.referral.ReferralChoice;

// Apply a chosen creator's code:
client.store().purchase(productId, qty, ReferralChoice.of(userPickedCode));

// User explicitly declined / there is no preference:
client.store().purchase(productId, qty, ReferralChoice.none());
```

Validate referrals before showing them to the user:

```java
client.store().validateReferral(code).thenAccept(v -> {
    if (v.valid()) showDiscount(v.discountPercent());
});
```

## Locales

Pass `FernanLocale` to endpoints that support it:

```java
client.store().getStock(FernanLocale.JA);
client.store().getCooldowns(FernanLocale.EN);
```

## Integration signal

If you're a third-party integrator, identify yourself for partner attribution:

```java
FernanClient.builder().apiKey(k).integration("my-app").build();
```

This sends `X-Integration: my-app` on every request. Use a stable identifier
(your app slug works well). Don't include user data — this header identifies
the *integration*, not the user.

## Pitfalls to avoid

- **Don't create a new `FernanClient` per request.** Build one at startup, reuse.
- **Don't forget `client.shutdown()`** — leaks the virtual-thread executor otherwise.
- **Don't catch `FernanException` directly** off a `CompletableFuture` — it's
  wrapped in `CompletionException`. Use `.exceptionally` / `.handle` and unwrap
  via `getCause()`.
- **Don't silently apply your own referral code.** Always make the user's choice
  explicit, or pass `ReferralChoice.none()`.

## Versioning

The library follows semver. While at `0.x`, minor versions may include breaking
changes for new upstream API features (e.g. when referral-settings endpoints
land). Check [CHANGELOG.md](CHANGELOG.md) before bumping.
