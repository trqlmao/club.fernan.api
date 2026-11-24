# CLAUDE.md

How to use and change club.fernan.api, an unofficial Java client for the
fernan.club REST API. [llms.txt](llms.txt) indexes the repo.

## Mental model

```
FernanClient.builder().apiKey(key).build()
    .user()       account, API key, redeeming
    .store()      stock, cooldowns, purchases
    .refunds()    refund requests
    .referrals()  owned referral codes (undocumented API)
    .health()     status (undocumented API)
```

- Every endpoint returns `CompletableFuture<T>`.
- A failed request completes with `FernanException`, categorized by `ErrorType`.
- Responses are records. Fields the API leaves out are null or zero.

Dependency: `com.github.trqlmao:club.fernan.api:0.4.0` from `https://jitpack.io`.

## Builder

| Call | Default |
|---|---|
| `apiKey(String)` | required |
| `userAgent(String)` | library name and version |
| `integration(String)` | off; sends `X-Integration` |
| `baseUrl(String)` | `https://api.fernan.club/api/v1` |
| `connectTimeoutMillis(int)` | 10000 |
| `executor(ExecutorService)` | internal daemon pool |
| `onApiKeyChange(Consumer<String>)` | none |

Reuse one client and call `shutdown()` on exit. If you pass an executor, you own
it and `shutdown()` leaves it running.

## Async and threading

Chain futures. Use `.join()` only in scripts: it blocks the thread and wraps
failures in `CompletionException`.

Continuations run on the client's executor. Move UI or heavy work to your own:

```java
client.user().me().thenAcceptAsync(this::render, uiThread);
```

## Errors

Unwrap `CompletionException`, then switch on `type()`. The README lists every type.

```java
Throwable cause = t instanceof CompletionException ? t.getCause() : t;
if (cause instanceof FernanException e && e.type() == ErrorType.COOLDOWN) {
    scheduleAt(e.cooldownEndsAt());
}
```

`INSUFFICIENT_BALANCE` exists but is never raised, because the API reference gives
no way to detect it.

## Key rotation

`regenerateApiKey()` switches the client to the new key as soon as the response
arrives and calls the `onApiKeyChange` listener. The old key stops working at once,
so persist the new one there.

## Pitfalls

- Do not add a default referral code. Pass the user's choice or
  `ReferralChoice.none()`.
- Do not retry a purchase after `NETWORK` or `SERVER_ERROR` without checking
  `purchases()`. It may have gone through.
- Do not log API keys, `accessToken` or `data`. They grant access to accounts.
- `preferredReferral` throws `UnsupportedOperationException`.
- Do not describe this library as official, endorsed or a partner of fernan.club.

## Changing the library

- `./gradlew spotlessApply build`. Run Gradle on JDK 17 or 21: the pinned formatter
  does not work on 25.
- Every record component has `@SerializedName`. Tests use sample JSON and never
  call the live API.
- Minor versions can break callers while at 0.x. Record them in
  [CHANGELOG.md](CHANGELOG.md).
- Rules are in [CONTRIBUTING.md](CONTRIBUTING.md). You own your commits: no
  AI-attribution trailers.
