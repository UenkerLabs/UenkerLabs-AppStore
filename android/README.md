# UenkerLabs Android app collection — source only

This is one Android Studio / Gradle multi-module source project. Currently configured application modules:
- `calendar` — Android calendar-provider day/month view and one-hour event creation (no direct account sign-in).
- `messages` — read-only SMS viewer, local sender blocklist and heuristic labels (not a default SMS/RCS app).
- `phone` — dial pad, call-log preview and optional manually configured call screening (not a default dialer).
- `alarm` — named one-shot/daily alarms, ringtone service and five Android home-screen widgets.

`arcade/` contains **preparatory Gradle files only** and is excluded from settings until functioning game sources exist; it is **not** a built game. Mail, browser and driving tools remain design tasks, not finished code. Do not list them as available apps.

## Build
Open `android/` in Android Studio with JDK 17 and SDK 35. Requires Android Gradle Plugin 8.8.2 and Gradle 8.10.2.
Build each configured module separately, for example `gradle :alarm:assembleDebug` using an installed Gradle CLI.
The binary Gradle wrapper JAR is intentionally not checked in; generate locally with `gradle wrapper` if needed.

No Android APK was built in this environment; no device/emulator tests were run. No runtime functionality has been validated. Never replace a primary telephone, SMS or alarm app before release QA.

All source in this project must be independently reviewed for security, privacy and appropriate Android system roles.
There is no INTERNET permission in the modules above.
