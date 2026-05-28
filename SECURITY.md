# Security Policy

This repository hosts an unofficial Java client for the
[fernan.club](https://fernan.club) API. The library and the upstream service are
two different things, and security issues should be reported to the right party.

## What to report where

| Issue type                                                              | Report to                                                          |
|-------------------------------------------------------------------------|--------------------------------------------------------------------|
| Vulnerability in the upstream **fernan.club service / API** (auth bypass, data exposure, server bug, etc.) | Contact the fernan.club admins directly (Discord / official support channel). Do **not** post to this repo. |
| Vulnerability in **this library's code** (parsing, response handling, header injection, denial-of-service through malformed input, dependency vulnerabilities) | Use [GitHub's private security advisory feature](https://github.com/trqlmao/club.fernan.api/security/advisories/new) for this repository. |
| Leaked API keys or credentials of yours                                 | Rotate via the fernan.club dashboard immediately, then contact the fernan.club admins. This library never logs or transmits secrets, but a rotation is the right first move. |

## What counts as a library vulnerability

Examples of things worth reporting privately on this repo:

- A response payload from the upstream API that, when parsed by this library,
  triggers an unchecked exception in a way that callers can't recover from.
- A construct that allows a malicious `baseUrl` override to exfiltrate the API
  key (e.g. trusting non-HTTPS, opening to redirects, leaking the key in error
  text).
- A dependency CVE that affects users of this library at runtime.
- A way to bypass the explicit `ReferralChoice` requirement.

## What is *not* a security issue here

- API errors returned by the upstream service (cooldowns, rate limits,
  validation rejections). Those are normal behavior surfaced through
  `FernanException`.
- Account bans, refund denials, or other policy actions taken by fernan.club.
- Disagreement with how the upstream API works — that's an upstream concern.

## Disclosure handling

We aim to acknowledge GitHub Security Advisories within **7 days** and ship a
fix within **30 days** for confirmed library vulnerabilities. Severe issues
will move faster. Once a fix is released, the advisory will be published with
credit to the reporter unless they request otherwise.

## Supported versions

Only the latest minor release receives security fixes. Older `0.x` versions
will not be backported; bump to the latest before reporting.

| Version | Supported          |
|---------|--------------------|
| 0.1.x   | :white_check_mark: |
| < 0.1   | :x:                |
