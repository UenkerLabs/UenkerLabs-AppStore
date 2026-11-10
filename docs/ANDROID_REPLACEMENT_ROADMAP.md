# UenkerLabs Android replacement roadmap — 2026-11-10

Aim: reduce reliance on Google-branded Android applications gradually, **not** to remove critical Android system services or compromise device functionality.

## Code present (not APK-released)
- Calendar: Calendar Provider event list and creation; relies on installed Google/kSync/Outlook sync adapters; account round-trip untested.
- SMS: read-only SMS display; manually curated local sender list and keyword labels; not MMS/RCS or a default SMS app.
- Phone: dial-pad hand-off, call-log viewer and optional CallScreeningService; no full InCallService, not a default phone.
- Alarm: real exact-alarm scheduling code, foreground audio, five appwidgets; device testing mandatory.

## Design backlog (no functional code committed yet)
- Universal email (Infomaniak IMAP/SMTP, Gmail OAuth/IMAP, Microsoft Graph OAuth/IMAP as available).
- Privacy-oriented browser (evaluate current GeckoView / Chromium-based engines, update responsibility, blocker APIs, user control).
- Five original mini-game concepts: Memory Grid, Number Merge, Pattern Path, Reflex Tap, Logic Lines.
- Driving assistant: route preparation, GPS speed and traffic information; do **not** enable prohibited active speed-camera alerts for drivers in Germany.
- Contacts, Notes, Tasks, File manager, Gallery, Calculator, Weather, Clock/timers.

## Legal and licensing gate
Consult official APIs, platform restrictions and each upstream license before any code reuse. Reading public repository code does not confer unlimited reuse. No proprietary app graphics or trademarks copied. Open-source work must preserve attribution and license compatibility.

## Release gate
1. Functional build from reproducible source.
2. Tests covering sensitive roles, notifications, wake reliability, privacy and permissions.
3. Independent review of source files, secrets and third-party licenses.
4. APK signing-key custody; signing certificate verification; F-Droid index signing.
5. Verified HTTPS public delivery, F-Droid client sync and clean installation/update.
6. Only then update app catalog to `released`.
