# UenkerLabs Android replacement roadmap — 2026-11-11

Goal: replace selected proprietary Android apps gradually without removing vital Android system services.

## Source-code prototypes, NOT released APKs
- Calendar: Android Calendar Provider reading/editing and one-hour event creation; Google, Infomaniak/kSync and Outlook rely on installed sync adapters.
- SMS: read-only SMS display with local sender labels and blocklist; not MMS/RCS or a default SMS app.
- Phone: dial-pad handoff, call-log viewer and manually configured CallScreeningService; not a full default phone.
- Alarm: exact-alarm scheduling and five home-screen widgets; reliability still untested on device.
- Arcade: five original offline mini-games in one app, no network or personal data permissions.

## Design backlog
- Universal e-mail app with IMAP/SMTP and OAuth-based Gmail/Outlook support; security and licensing review mandatory.
- Privacy-first browser; prefer maintained browser engines instead of promising an unmaintained Chromium fork.
- Legal driving assistant with GPS speed and planning features. Active speed-camera warnings while driving in Germany must not be included.
- Contacts, Notes, Tasks, Files, Photos, Calculator, Weather.

## Release gate
1. Reproducible Gradle build, no placeholders represented as complete apps.
2. Real-device tests, accessibility checks and permission review.
3. Security review, third-party licensing review, removal of secrets and personal data.
4. APK and repository signing using safely backed-up keys.
5. Verify GitHub Pages HTTPS delivery, F-Droid client integration, installation and update.
6. List only fully verified released apps publicly.
