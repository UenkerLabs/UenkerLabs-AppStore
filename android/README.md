# UenkerLabs Android collection — development sources only

Open `android/` in Android Studio. The configured native application modules are:
- `calendar` — calendar-provider view and event creation, relying on Android sync adapters for Google/Infomaniak/kSync/Outlook.
- `messages` — read-only SMS viewer with local spam labels and manual blocklist. No MMS/RCS or default SMS role.
- `phone` — dial pad, call-log viewer and optional local call-screening service. Not a full default dialer.
- `alarm` — daily/one-time alarms, sound service and five Android home-screen widgets.
- `arcade` — five original, offline mini-games. See [arcade/README.md](arcade/README.md).

Universal mail client, browser, and legal driving assistant are currently **design backlog**; no functional app source is published for them.

## Build requirements

JDK 17, Android SDK 35, Android Gradle Plugin 8.8.2, Gradle 8.10.2. No binary wrapper checked in.
With an installed Gradle executable, build `gradle :arcade:assembleDebug` or another module.
No Android APK build or device UI/integration test was completed in this workspace. **Do not replace the system alarm, phone or SMS client** until a test campaign and separate release approval.

No in-app tracking is intended. The five modules have no declared INTERNET permission. All code still needs real build verification and user-facing QA.
