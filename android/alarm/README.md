# UenkerLabs Wecker – 0.1.0-dev

Native Offline-Android-Beta-App ohne INTERNET-Berechtigung, Werbung oder Tracking.

**Implementierter Quellstand:** benannte einmalige und tägliche Alarme mit `AlarmManager.setAlarmClock()`, Aktivierung/Deaktivierung, Löschbestätigung, Wiedereinplanung nach relevanten Systemereignissen, Alarmbenachrichtigung, Klingelservice, 10-Minuten-Schlummern und fünf Widgets.

**Korrigierter Veröffentlichungsstatus (11.11.2026):** Eine signierte Beta-APK liegt unter `gh-pages/repo/UenkerLabs-alarm-beta-0.1.0.apk`. Die frühere Aussage „keine APK gebaut oder signiert“ ist überholt. Ein funktionierender F-Droid-Download nach dem GitHub-Namenswechsel und die echte Geräteabnahme sind **nicht** belegt.

**Vor einer verlässlichen Wecker-Nutzung auf Geräten prüfen:** Benachrichtigungs- und Exact-Alarm-Rechte, Sperrbildschirm, Vollbild-Intent, Nicht-stören, Stromsparmodus, Neustart, Sommerzeit und Zeitzonenwechsel, überschneidende Wecker, Klingeltonausfall und Widget-Aktionen. Für wichtige Alarme den Systemwecker weiterverwenden.

Referenzen: https://developer.android.com/develop/background-work/services/alarms und https://developer.android.com/develop/ui/views/appwidgets
