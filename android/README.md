# UenkerLabs Android – Beta-Module

**Signierte APK-Dateien im `gh-pages`-Branch (Stand 11.11.2026):**
- `calendar`: 0.1.0-dev und 0.2.0-dev; aktuelle Beta ist versionCode 2.
- `alarm`: 0.1.0-dev, experimenteller Wecker mit fünf Widgets.
- `arcade`: 0.1.0-dev, fünf Offline-Minispiele.

**Nicht im aktuellen F-Droid-Katalog:** `phone` und `messages`. Die Quelltexte werden als pausierte Prototypen archiviert; weder Vollständigkeit noch produktive Nutzbarkeit ist behauptet.

**Build-Werkzeuge laut Quellkonfiguration:** Java 17, Android SDK 35, Android Gradle Plugin 8.8.2 und Gradle 8.10.2. Das Repository enthält keinen ausführbaren Gradle-Wrapper; die manuellen GitHub-Actions-Workflows richten Gradle selbst ein. Vorhandene frühere CI-Ergebnisse sind keine neue Reproduzierbarkeits- oder Geräteabnahme.

**Wichtig:** Die `gh-pages`-Indexdateien sind noch auf die alte GitHub-Pages-Adresse signiert. Nach der Umbenennung des Kontos ist die öffentliche Quelle nicht abschließend getestet. APK-Dateien und Schlüsselidentitäten nicht ohne gesonderte Signierprüfung ändern. Siehe [../docs/ARCHITECTURE.md](../docs/ARCHITECTURE.md).
