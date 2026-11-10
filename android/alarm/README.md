# UenkerLabs Wecker — development version 0.1

A native, offline Android alarm application. No INTERNET permission, analytics, tracking or advertising.

## Functional source implementation
- Set named one-shot and daily repeating alarms with Android `AlarmManager.setAlarmClock()`.
- Explicit alarm toggle, delete with confirmation (long press), boot/timezone rescheduling.
- Alarm notification with full-screen intent where the OS permits it.
- Foreground ringtone service; dismiss or snooze for ten minutes.
- Five distinct launcher widgets: **next alarm**, **live digital clock**, **5-minute quick alarm**, **15-minute quick alarm**, **alarm manager with active count**.
- Android 13+ notification permission is requested. `USE_EXACT_ALARM` is declared because this is an actual alarm-clock app; see official Android docs for exact-alarm restrictions.

## Important QA before replacing the system alarm
NOT DEVICE-TESTED. Check initial permission experience, notifications disabled, Do Not Disturb, reboot, time-zone and DST, phone locked, screen off, power saving, existing alarm ringing and new overlapping alarms, sound missing, full-screen-intent settings and widgets on a Nothing Phone and another Android device. Exact delivery and wake-up behavior **must not be claimed without device tests**.

No APK has been built or signed. Releasing it through F-Droid also requires a separate signed index with persistent signing-key custody.

### Sources / policy
- https://developer.android.com/develop/background-work/services/alarms
- https://developer.android.com/about/versions/14/changes/schedule-exact-alarms
- https://developer.android.com/develop/ui/views/appwidgets
