# Examples

Self-contained snippets that show idiomatic integrations of `club.fernan.api`.
These are not compiled by the main build — they exist as readable references.

| File                                | Shows                                                                              |
|-------------------------------------|------------------------------------------------------------------------------------|
| [`AltManagerExample.java`](AltManagerExample.java) | The canonical pattern for clients with an alt-storage layer: list stock, prompt for referral, purchase, decode credentials, hand off to a host-app `AltStore` interface. |
| [`MinimalExample.java`](MinimalExample.java)       | Smallest possible "fetch user, print balance" program.                              |
| [`ErrorHandlingExample.java`](ErrorHandlingExample.java) | Recovering from `RATE_LIMITED`, `COOLDOWN`, and `INSUFFICIENT_BALANCE`. |

## Copying these

Treat them as starting templates rather than as a library API. Adapt to your
host application's storage, prompting, and threading model.
