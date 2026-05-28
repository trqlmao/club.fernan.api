# Disclaimer

This project is an **unofficial, independent, community-maintained** Java
client for the [fernan.club](https://fernan.club) REST API. It is **not
affiliated with, endorsed by, sponsored by, or otherwise connected to**
fernan.club, its operators, or its administrators in any way.

## What this project is

A drop-in Java library that translates fernan.club's publicly addressable
HTTP endpoints into typed Java method calls. It is published openly so
third-party Java applications can integrate with the service without each
maintainer having to re-implement the same HTTP/JSON plumbing.

## What this project is not

- An endorsement of fernan.club, its products, its pricing, or its policies.
- A guarantee that the upstream service is correctly implemented, lawful in
  any particular jurisdiction, available, reliable, secure, honest, or fit
  for any particular purpose.
- A statement that any goods or services delivered by fernan.club are
  lawfully owned or freely transferable to the end user.
- An invitation to violate the terms of service of fernan.club, of any
  account provider whose property is exchanged through fernan.club
  (including but not limited to Mojang Studios and Microsoft for Minecraft
  accounts), or of any other third party.

## Maintainer's relationship to fernan.club

The maintainer of this repository:

- has no business, financial, or operational relationship with fernan.club;
- receives no compensation, referral revenue, share of sales, or other
  consideration from fernan.club in connection with this project;
- does not operate, control, audit, or have privileged insight into
  fernan.club's back-end systems, account sources, fulfillment workflow,
  refund handling, or terms of service;
- maintains this wrapper purely as community tooling.

## Provided as-is

This software is provided **AS IS, WITHOUT WARRANTY OF ANY KIND**, express
or implied, including but not limited to warranties of merchantability,
fitness for a particular purpose, and non-infringement. The full
[MIT License](LICENSE) text governs.

In plain language: the library does its best to construct correct HTTP
requests and parse correct HTTP responses against the API as documented at
the time of release. It cannot, and does not, vouch for the upstream
service's behavior, security, lawfulness, or honesty. If the upstream
service does something you did not expect, that is between you and the
upstream service.

## User responsibility

By using this library, you acknowledge and agree that you are solely
responsible for:

1. Reading and complying with fernan.club's terms of service.
2. Reading and complying with the terms of service of any third party
   whose property you interact with through fernan.club — including, where
   applicable, Mojang Studios, Microsoft, or any other account provider.
3. The lawfulness in your jurisdiction of (a) the goods or services you
   acquire from fernan.club and (b) any actions you take using software
   built with this library.
4. Any consequences — civil, criminal, contractual, or otherwise — that
   arise from your use of this library or the upstream service.

## Educational / informational purpose

This project is published for **educational and integrational purposes**:
to demonstrate how to design a typed Java client for a JSON HTTP API and
to give downstream developers a clean, tested starting point. Nothing in
this repository should be read as legal, financial, or commercial advice.

## Reporting concerns

If you believe the existence of this wrapper itself — rather than any
particular use of it — infringes on rights you hold, please open an issue
on this repository or use the contacts in [SECURITY.md](SECURITY.md). Good-
faith requests will be reviewed promptly.
