# UenkerLabs Android collection — development sources only

Open `android/` in Android Studio. The configured native application modules are:
- `calendar` — calendar provider view and event creation, relying on Android sync adapters for Google/Infomaniak/kSync/Outlook.
- `messages` — read-only SMS viewer with local spam labels and manual blocklist. No MMS/RCS or default SMS role.
- `phone` — dial pad, call-log viewer and optional local call-screening service. Not a full default dialer.
- `alarm` — daily/one-time alarms, sound service and five Android home-screen widgets.

Five game concepts and the universal mail client, browser and driving helper are currently **design backlog only**; no functional app source is published for them.

## Build requirements

JDK 17, Android SDK 35, Android Gradle Plugin 8.8.2, Gradle 8.10.2, no wrapper binary checked in. With a compatible local Gradle installation, build for example `gradle :alarm:assembleDebug`.
No Android APK build or device UI/integration test was completed in this workspace. **Do not replace the system alarm, phone or SMS client** until a test campaign and separate release approval.

No in-app tracking or Internet permission is declared by the four current prototypes. Treat their permission and security implementation as unreviewed until final release QA.
