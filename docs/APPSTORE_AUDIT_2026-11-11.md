# Audit – UenkerLabs AppStore Repository (11.11.2026)

**Audit-Status:** Bestandsaufnahme und gezielte Korrekturfassung, **keine vollständige Android-/F-Droid-/Sicherheits- oder Endgeräteabnahme**.

**Quellbezug:** `main` bei `5c78064a8a3be2512dfdac41b15727bea6c1f4ae`; `gh-pages` bei `cb91e88adc2fc019367015617bd56c4cc88450e2`; offizieller Stand über verbundenen GitHub-Lesezugriff am 11.11.2026.

## Umfang und Grenzen

- Der rekursive `main`-Git-Baum war vollständig, `truncated=false`: **68 Dateien**, **126 Datei- und Verzeichniseinträge**, zusammen **249.172 Byte** im Baum.
- Die Textquellen (Dokumentation, Workflows, Skripte, Java-/Gradle-/Manifest-/XML-Dateien, HTML/CSS/JS, Lizenz, SVG) wurden quellenbezogen durchgesehen. **`icon.png`** ist Binärinhalt: nur Pfad, Objekt-SHA und Dateigröße wurden eingesehen, nicht jedes Pixel oder die exakten Bytes analysiert.
- Der Veröffentlichungsbranch `gh-pages` wurde zusätzlich auf Website, Metadaten, signierte F-Droid-Indexstrukturen und APK-Dateivorkommen geprüft. **APK-Binärinhalte und JAR-Signaturen wurden nicht neu heruntergeladen oder kryptografisch neu verifiziert**.
- Keine SDK-Kompilierung, kein Gradle-Test, kein Gerätetest, keine aktive F-Droid-Installation, kein Relaunch bestehender GitHub-Actions-Läufe. Die aktuelle HTTPS-Verfügbarkeit von `github.io` blieb mangels erfolgreichem externem Abruf **UNGEKLÄRT**.

## Befunde

| ID | Beobachtung | Belegstatus | Einordnung/Korrektur |
| --- | --- | --- | --- |
| AS-001 | GitHub-Benutzername wurde `YZUe1980` → `UenkerLabs` geändert; alte GitHub-Links verblieben in aktiven Texten. | **BELEGT** (GitHub-Account, Quelltexte) | Gezielte Anpassung in beiden vorgeschlagenen PRs. |
| AS-002 | Die `main/index.html` zeigte fünf App-Downloadkarten einschließlich SMS/Telefon, während `gh-pages` drei Paketnamen und vier APK-Dateien enthält. | **BELEGT** (beide Git-Bäume/Index) | Website-Quelle auf real dokumentierten Drei-App-Katalog abgeglichen; pausierte APK-Links entfernt. |
| AS-003 | Aktuelle Anleitungen widersprachen dem Quellenstand („0 Apps“, „keine APK gebaut“) und historische Berichte mit älteren Zahlen wurden nicht eingeordnet. | **BELEGT** (README, Docs, App-READMEs) | Aktive Dokumente aktualisiert, historische Berichte mit klarer Chronologie versehen, Originaltext bewahrt. |
| AS-004 | Signierte F-Droid-Indizes und `config.yml` referenzieren noch `https://yzue1980.github.io/UenkerLabs-AppStore/repo`. | **BELEGT** (Index-JSON und Konfiguration) | Keine Direktänderung: Signatur-/Hashkette würde ungültig; neue Hostadresse und Offline-Re-Signierung noch offen. |
| AS-005 | Frühere Workflows enthielten `push`-Trigger, die bei Änderungen einzelne CI- bzw. Veröffentlichungsabläufe auslösen konnten. | **BELEGT** (Workflowdateien) | Im Vorschlagsbranch auf manuellen Aufruf umgestellt und Veröffentlichungs-Guard ergänzt. |
| AS-006 | Gradle ist in `gradle-wrapper.properties` beschrieben; ausführbarer `gradlew` und Wrapper-JAR liegen nicht im geprüften Baum. | **BELEGT** (vollständiger Baum) | Dokumentiert; Actions-Workflows verwenden explizit Gradle-Setup. Keine automatische Reproduzierbarkeitszusage. |
| AS-007 | In `AlarmStore.add()/quick()` wird Persistenz vor `schedule()` ausgeführt. Bei Scheitern der Planung kann ein gespeicherter Alarm als aktiviert erscheinen, ohne dass dieser Aufruf ihn eingeplant hat. | **BELEGT als Code-Ablauf**, tatsächlicher Gerätefehler **UNGEKLÄRT** | Funktions- und Fehlerfalltest vor Änderung erforderlich. |
| AS-008 | `AlarmAudioService` verwendet für Alarme eine konstante Notification-ID; Gleichzeitigkeit/Überschneidungen sind geräteseitig nicht nachgewiesen. | **BELEGT als Implementierungsdetail**, Konsequenz **VORLÄUFIG** | Vor produktiver Wecker-Nutzung Zuverlässigkeit testen. |
| AS-009 | `prepare-fdroid-beta-index.yml` installiert `fdroidserver` ohne feste Versionsbindung. | **BELEGT** | Reproduzierbarkeit der Indexgenerierung offen; nicht stillschweigend upgraden. |
| AS-010 | Aktueller Pages-Host, F-Droid-Client-Interoperabilität, App-Installation und Kalender-In-Place-Update sind nicht durch diese Prüfung belegt. | **UNGEKLÄRT** | Echte HTTPS-/Android-Abnahme vor Freigabe zwingend. |

## Konkrete Korrekturen in den Vorschlägen

- `audit/appstore-cleanup-2026-11-11` gegen `main`: Basis-Dokumentation, aktuelle Drei-App-Webquelle, historische Statusvermerke, manuelle CI-Auslöser, Release-Sperre und Prüfscripts.
- `audit/appstore-public-links-2026-11-11` gegen `gh-pages`: veröffentlichte HTML-Vorlagen und drei App-Metadaten-Links; **keine** APK/JAR/Index-Änderung.
- Veröffentlichungs-/Signaturschutz: **`gh-pages` bleibt unverändert**, bis Betreiberadresse und mit vorhandenem Schlüssel signierter Index geprüft sind.
- Wichtig: Die frühere E-Mail in historischen Git-Commits ist durch eine Account-Profiländerung nicht automatisch aus Git-History entfernt; es wurde **keine Historienumschreibung** vorgenommen.

## Freigabegrenze

Diese Korrekturen sind **Entwürfe**, keine betriebsbereite Veröffentlichung. Kein Merge, keine externe Veröffentlichung, kein DNS-Wechsel und keine neue Schlüsselgenerierung. Die Freigabe verbleibt beim Inhaber.

## Datei-Inventar des `main`-Branches

### GitHub Actions / CI (5)
- `.github/workflows/manual-android-qa.yml`
- `.github/workflows/manual-calendar-v2-qa.yml`
- `.github/workflows/manual-fdroid-source-check.yml`
- `.github/workflows/manual-publish-fdroid-pages.yml`
- `.github/workflows/prepare-fdroid-beta-index.yml`

### Repository-Grunddateien (5)
- `.gitignore`
- `CONTRIBUTING.md`
- `LICENSE`
- `README.md`
- `SECURITY.md`

### Android-Quellcode und Buildkonfiguration (44)
- `android/README.md`
- `android/alarm/README.md`
- `android/alarm/build.gradle`
- `android/alarm/src/main/AndroidManifest.xml`
- `android/alarm/src/main/java/com/uenkerlabs/alarm/AlarmActivity.java`
- `android/alarm/src/main/java/com/uenkerlabs/alarm/AlarmAudioService.java`
- `android/alarm/src/main/java/com/uenkerlabs/alarm/AlarmBroadcast.java`
- `android/alarm/src/main/java/com/uenkerlabs/alarm/AlarmControlReceiver.java`
- `android/alarm/src/main/java/com/uenkerlabs/alarm/AlarmStore.java`
- `android/alarm/src/main/java/com/uenkerlabs/alarm/AlarmWidgets.java`
- `android/alarm/src/main/java/com/uenkerlabs/alarm/RescheduleReceiver.java`
- `android/alarm/src/main/java/com/uenkerlabs/alarm/RingActivity.java`
- `android/alarm/src/main/java/com/uenkerlabs/alarm/WidgetActionReceiver.java`
- `android/alarm/src/main/res/drawable/widget_bg.xml`
- `android/alarm/src/main/res/layout/widget_card.xml`
- `android/alarm/src/main/res/layout/widget_clock.xml`
- `android/alarm/src/main/res/values/strings.xml`
- `android/alarm/src/main/res/xml/widget_clock.xml`
- `android/alarm/src/main/res/xml/widget_manage.xml`
- `android/alarm/src/main/res/xml/widget_next.xml`
- `android/alarm/src/main/res/xml/widget_quick15.xml`
- `android/alarm/src/main/res/xml/widget_quick5.xml`
- `android/arcade/README.md`
- `android/arcade/build.gradle`
- `android/arcade/src/main/AndroidManifest.xml`
- `android/arcade/src/main/java/com/uenkerlabs/arcade/ArcadeActivity.java`
- `android/arcade/src/main/java/com/uenkerlabs/arcade/SlidePuzzle.java`
- `android/arcade/src/test/java/com/uenkerlabs/arcade/SlidePuzzleSmoke.java`
- `android/build.gradle`
- `android/calendar/build.gradle`
- `android/calendar/src/main/AndroidManifest.xml`
- `android/calendar/src/main/java/com/uenkerlabs/calendar/CalendarActivity.java`
- `android/calendar/src/main/java/com/uenkerlabs/calendar/MonthGrid.java`
- `android/calendar/src/test/java/com/uenkerlabs/calendar/MonthGridSmokeTest.java`
- `android/gradle.properties`
- `android/gradle/wrapper/gradle-wrapper.properties`
- `android/messages/build.gradle`
- `android/messages/src/main/AndroidManifest.xml`
- `android/messages/src/main/java/com/uenkerlabs/messages/MessagesActivity.java`
- `android/phone/build.gradle`
- `android/phone/src/main/AndroidManifest.xml`
- `android/phone/src/main/java/com/uenkerlabs/phone/LocalScreeningService.java`
- `android/phone/src/main/java/com/uenkerlabs/phone/PhoneActivity.java`
- `android/settings.gradle`

### Weboberfläche und Grafiken (5)
- `assets/main.js`
- `assets/style.css`
- `favicon.svg`
- `icon.png` — Binärdatei: nur Dateimetadaten, keine Bildinhaltprüfung
- `index.html`

### Technische Dokumentation (7)
- `docs/ANDROID_REPLACEMENT_ROADMAP.md`
- `docs/ARCHITECTURE.md`
- `docs/CALENDAR_BETA_V2_DEV.md`
- `docs/FDROID_BETA_UPDATE_PROCESS.md`
- `docs/FDROID_INDEX_REPAIR_2026-11-11.md`
- `docs/FDROID_TROUBLESHOOTING.md`
- `docs/PUBLIC_STORE_UPDATE_2026-11-11.md`

### Prüfskripte (2)
- `scripts/check-public-fdroid.py`
- `scripts/check-source.sh`
