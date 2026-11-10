# UenkerLabs Android apps (development)

This is one Android Studio / Gradle multi-module source project. Modules produce **four independent APKs**:
- `calendar` — calendar view and event creation through Android Calendar Provider.
- `messages` — on-device inbox preview, manual local sender blocklist and heuristic *labels*, SMS composition delegated to the installed default SMS app.
- `phone` — dial-pad and local manual blocklist using Android's optional Call Screening role. No full default dialer / in-call UI yet.
- `arcade` — five offline mini-games (implementation in progress).

## Build
Open **android/** as a project in Android Studio (JDK 17 / Android SDK 35); AGP 8.8.2, Gradle 8.10.2.
Use your installed Gradle CLI `gradle :calendar:assembleDebug` (and the other modules).
The repository does not contain binary Gradle wrapper JAR; generate a wrapper locally if needed.
No APK has been built or device-tested in this repository yet. Do not distribute before QA.

## Privacy & security
No INTERNET permission in any of these modules. No ad SDK, trackers or telemetry.
- Calendar: READ_CALENDAR/WRITE_CALENDAR, accounts synced by existing Android sync adapters such as kSync and Outlook, not by this app.
- Messages: READ_SMS; does not receive, erase, send or intercept messages; no RCS or MMS support.
- Phone: READ_CALL_LOG when explicitly granted; CallScreeningService **only** when Android grants its user-selected role. Explicit manual number matches only, no crowdsourced score or silent upload.
- Arcade: no requested runtime permissions.

**None** of these apps replaces a system default for calling or SMS yet. Preserve the existing Google/Android apps until separate QA and an explicit opt-in migration.

## Publication
Source commits alone do not publish releases to the F-Droid index.
Require signed APK, reproducible build evidence, functional and accessibility checks, and signature continuity. See docs/ARCHITECTURE.md.

