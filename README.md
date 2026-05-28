# club.fernan.api

[![build](https://github.com/trqlmao/club.fernan.api/actions/workflows/build.yml/badge.svg)](https://github.com/trqlmao/club.fernan.api/actions/workflows/build.yml)
[![JitPack](https://jitpack.io/v/trqlmao/club.fernan.api.svg)](https://jitpack.io/#trqlmao/club.fernan.api)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://adoptium.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Issues](https://img.shields.io/github/issues/trqlmao/club.fernan.api)](https://github.com/trqlmao/club.fernan.api/issues)
[![Stars](https://img.shields.io/github/stars/trqlmao/club.fernan.api?style=flat)](https://github.com/trqlmao/club.fernan.api/stargazers)

Drop-in Java client for the [fernan.club](https://fernan.club) REST API.
Async-first, JDK 17+, zero dependencies beyond Gson, safe to embed in mods,
launchers, or standalone JVM apps.

- One `FernanClient` entry point exposing five typed services
  (`user`, `store`, `refunds`, `referrals`, `health`).
- All endpoints return `CompletableFuture<T>`.
- One `FernanException` with a typed `ErrorType` enum covering every API
  error mode (auth, banned, validation, not-found, conflict, cooldown,
  rate-limit, server error).
- Explicit `ReferralChoice` type — the library never silently auto-applies a
  referral code; the caller decides per call.
- Optional `X-Integration` partner-attribution signal for client devs who want
  fernan.club to know who's driving traffic.

## Install

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.trqlmao:club.fernan.api:0.1.0")
}
```

### Gradle (Groovy DSL)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.trqlmao:club.fernan.api:0.1.0'
}
```

### Maven

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.trqlmao</groupId>
  <artifactId>club.fernan.api</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Quickstart

```java
import club.fernan.api.FernanClient;
import club.fernan.api.model.referral.ReferralChoice;
import club.fernan.api.model.store.Purchase;
import club.fernan.api.model.user.User;

FernanClient client = FernanClient.builder()
        .apiKey(System.getenv("FERNAN_KEY"))
        .userAgent("my-app/1.0")
        .integration("my-app")
        .build();

User me = client.user().me().join();
System.out.println("balance: " + me.balance());

Purchase order = client.store()
        .purchase(1, 5, ReferralChoice.none())
        .join();

client.shutdown();
```

## Services

| Service              | Endpoints                                                                                 |
|----------------------|-------------------------------------------------------------------------------------------|
| `client.user()`      | `me`, `getApiKey`, `regenerateApiKey`, `redeemKey`                                        |
| `client.store()`     | `getStock`, `getCooldowns`, `purchase`, `getPurchases`, `getPurchase`, `validateReferral` |
| `client.refunds()`   | `create`, `cancel`, `list`, `get`                                                         |
| `client.referrals()` | `create`, `list`, `stats`, `toggle`, `delete` (MediaPlus+)                                |
| `client.health()`    | `get`, `simple`                                                                           |

## Recipes

See [`examples/`](examples/) for self-contained snippets:

- [`MinimalExample.java`](examples/MinimalExample.java) — smallest possible
  "build a client, fetch user, print balance".
- [`AltManagerExample.java`](examples/AltManagerExample.java) — canonical
  flow for clients with an alt-storage layer: list stock → prompt for referral
  → purchase → decode credentials → register with a host-app `AltStore`.
- [`ErrorHandlingExample.java`](examples/ErrorHandlingExample.java) —
  recovering from rate limits, cooldowns, and insufficient-balance failures.

## Referral codes

The library never silently applies a referral code. Every purchase requires an
explicit `ReferralChoice`:

```java
// Apply a code the user selected:
client.store().purchase(productId, qty, ReferralChoice.of("creator123"));

// User explicitly declined / no preference:
client.store().purchase(productId, qty, ReferralChoice.none());
```

If you surface referrals to end users, prompt them to choose rather than
auto-defaulting to your own code.

## Error handling

```java
import club.fernan.api.exception.ErrorType;
import club.fernan.api.exception.FernanException;

client.store().purchase(1, 100, ReferralChoice.none())
        .exceptionally(t -> {
            FernanException e = (FernanException) t.getCause();
            switch (e.getType()) {
                case INSUFFICIENT_BALANCE -> notifyTopUp();
                case COOLDOWN -> scheduleRetry(e.getCooldownEndsAt());
                case RATE_LIMITED -> backoff(e.getRetryAfter());
                case AUTHENTICATION -> promptRelogin();
                default -> log(e);
            }
            return null;
        });
```

## Locales

```java
import club.fernan.api.locale.FernanLocale;

client.store().getStock(FernanLocale.JA).join();
```

Supported: `EN`, `ES`, `DE`, `JA`, `ZH`, `TW`.

## Integration signal

Identify your application to fernan.club without claiming referral revenue:

```java
FernanClient.builder()
        .apiKey(key)
        .integration("my-app")
        .build();
```

This sends `X-Integration: my-app` on every request.

## Roadmap

- **0.2.0** — wire `UserService.getPreferredReferral` /
  `setPreferredReferral` once the upstream endpoints are finalized.
- **0.2.0** — reconcile the `X-Integration` header with whatever final shape
  the upstream lands on.
- **Future** — pluggable retry policy with exponential backoff for transient
  5xx + 429 responses.

See [CHANGELOG.md](CHANGELOG.md) for released changes.

## Requirements

- Java 17 or newer (compiled with `--release 17`; verified on 17, 21, and 25).
- One transitive dependency: [Gson](https://github.com/google/gson) 2.11+.

## Community

- [Discussions](https://github.com/trqlmao/club.fernan.api/discussions) for
  questions and integration recipes.
- [Issues](https://github.com/trqlmao/club.fernan.api/issues) for bugs and
  feature requests (read [CONTRIBUTING.md](CONTRIBUTING.md) first).
- [Security policy](SECURITY.md) for vulnerability reports.

## License

[MIT](LICENSE).
