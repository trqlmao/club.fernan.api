# club.fernan.api

[![build](https://github.com/trqlmao/club.fernan.api/actions/workflows/build.yml/badge.svg)](https://github.com/trqlmao/club.fernan.api/actions/workflows/build.yml)
[![JitPack](https://jitpack.io/v/trqlmao/club.fernan.api.svg)](https://jitpack.io/#trqlmao/club.fernan.api)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://adoptium.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Issues](https://img.shields.io/github/issues/trqlmao/club.fernan.api)](https://github.com/trqlmao/club.fernan.api/issues)
[![Stars](https://img.shields.io/github/stars/trqlmao/club.fernan.api?style=flat)](https://github.com/trqlmao/club.fernan.api/stargazers)

An unofficial Java client for the [fernan.club](https://fernan.club) REST API.
Java 17 or newer, Gson as the only dependency.

> **Unofficial.** This is not a fernan.club project. fernan.club does not run
> or endorse it. It is published as an educational example of a typed
> Java API client. The maintainer does not supply what the store sells, and does
> not know where it comes from or whether it is legal to buy or use where you
> live. Read the [disclaimer](DISCLAIMER.md) first.

## Install

Gradle:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.trqlmao:club.fernan.api:0.4.0")
}
```

Maven:

```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>com.github.trqlmao</groupId>
  <artifactId>club.fernan.api</artifactId>
  <version>0.4.0</version>
</dependency>
```

## Quickstart

```java
FernanClient client = FernanClient.builder()
        .apiKey(System.getenv("FERNAN_KEY"))
        .userAgent("my-app/1.0")
        .build();

client.user().me()
        .thenAccept(me -> System.out.println("Balance: " + me.balance()))
        .exceptionally(t -> { t.printStackTrace(); return null; });

// On exit:
client.shutdown();
```

Your API key can spend your balance and read your account data. Keep it out of
source control and logs.

Build one client and reuse it. Every call returns a `CompletableFuture`, so chain
it. `.join()` blocks the calling thread, which is fine in a script and a bad idea
on a UI, render or game thread.

More in [`examples/`](examples/).

## Endpoints

These are in fernan.club's API reference:

| Method | Request |
|---|---|
| `user().me()` | `GET /user/me` |
| `user().apiKey()` | `GET /user/key` |
| `user().regenerateApiKey()` | `POST /user/key/regenerate` |
| `user().redeemKey(key)` | `POST /user/redeem` |
| `store().stock()` | `GET /store/stock` |
| `store().cooldowns()` | `GET /store/cooldown` |
| `store().purchase(productId, amount, referral)` | `POST /store/purchase` |
| `store().purchases(limit, offset)` | `GET /store/purchases` |
| `store().purchaseDetail(id)` | `GET /store/purchases/:id` |
| `refunds().create(purchaseId, uuids, reason)` | `POST /store/refunds` |
| `refunds().list(limit, offset)` | `GET /store/refunds` |
| `refunds().get(id)` | `GET /store/refunds/:id` |

These are not in it and may change or stop working:

| Method | Request |
|---|---|
| `store().validateReferral(code)` | `POST /store/referral/validate` |
| `refunds().cancel(id)` | `POST /store/refunds/cancel` |
| `referrals().create`, `list`, `stats`, `toggle`, `delete` | `/referrals/*` |
| `health().get()`, `simple()` | `GET /health`, `GET /health/simple` |

`stock` and `cooldowns` also take a `FernanLocale`: `EN`, `ES`, `DE`, `FR`, `JA`,
`PL`, `RU`, `ZH` or `TW`.

## Errors

A failed request completes with a `FernanException`. Check `type()`:

```java
client.store().purchase(1, 2, ReferralChoice.none())
        .exceptionally(t -> {
            Throwable cause = t instanceof CompletionException ? t.getCause() : t;
            if (cause instanceof FernanException e) {
                switch (e.type()) {
                    case COOLDOWN -> System.out.println("Wait until " + e.cooldownEndsAt());
                    case RATE_LIMITED -> System.out.println("Retry in " + e.retryAfter() + "s");
                    default -> System.out.println(e.getMessage());
                }
            }
            return null;
        });
```

| Type | When |
|---|---|
| `NETWORK` | No response from the server |
| `AUTHENTICATION` | 401, or 403 without a ban |
| `BANNED` | 403 with a ban |
| `VALIDATION` | 400 or 422 |
| `NOT_FOUND` | 404 |
| `CONFLICT` | 409 |
| `COOLDOWN` | 429 with a cooldown end time |
| `RATE_LIMITED` | Any other 429; `retryAfter()` may be null |
| `SERVER_ERROR` | 500, 502, 503 or 504 |
| `UNKNOWN` | Anything else, including a body that is not JSON |

Do not retry a purchase after `NETWORK` or `SERVER_ERROR` without checking
`purchases()` first. The order may have gone through.

## Referral codes

`purchase` always takes a `ReferralChoice`: `ReferralChoice.of(code)` or
`ReferralChoice.none()`. The library contains no referral code and never adds one.
If your app uses one, let the user choose, and tell them if you earn from it.

## Identifying your app

`.integration("my-app")` on the builder adds an `X-Integration: my-app` header. It
is off by default, carries no referral, and is not in the API reference, so the
server may ignore it.

## For AI agents

Read [CLAUDE.md](CLAUDE.md) for how to use the library and [llms.txt](llms.txt)
for an index of the repo.

## Help

- Library bugs: [issues](https://github.com/trqlmao/club.fernan.api/issues)
- Library questions: [discussions](https://github.com/trqlmao/club.fernan.api/discussions)
- Security: [SECURITY.md](SECURITY.md)
- Orders, refunds, balances or accounts: fernan.club. The maintainer cannot help.

## License

[MIT](LICENSE). Changes are in the [changelog](CHANGELOG.md).
