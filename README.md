# UenkerLabs AppStore
Eigene signierte Android-Beta-Paketquelle für Tests, Feedback und Updates.

- **Website:** https://yzue1980.github.io/UenkerLabs-AppStore/
- **F-Droid-Repositorium:** https://yzue1980.github.io/UenkerLabs-AppStore/repo
- **Issues:** https://github.com/YZUe1980/UenkerLabs-AppStore/issues

## Veröffentlichungen vom 11.11.2026

| App | Version | Status |
| --- | --- | --- |
| Kalender | **0.2.0-dev (versionCode 2)** | Start mit Monatsansicht, farbige Termine, Ansichten und Wisch-Navigation |
| Wecker | 0.1.0-dev (versionCode 1) | Frühe Beta, kritische Alarme weiterhin mit dem Systemwecker |
| Spiele | 0.1.0-dev (versionCode 1) | Fünf Offline-Minispiele nur als Techniktest |

**Telefon und SMS sind auf Wunsch des Inhabers aus dem öffentlichen Katalog und den APK-Verteilungsdateien entfernt**; der historische Quellcode wird zunächst nicht gelöscht. Bereits auf Geräten installierte Apps werden nicht automatisch deinstalliert.

Der Kalender **0.2.0** ist mit dem gleichen App-Signierschlüssel wie Version 0.1.0 signiert. Beide Versionen bleiben im F-Droid-Index für nachvollziehbare Updates. APK- und Indexsignaturen und Checksummen wurden technisch geprüft; Installation und Aktualisierung auf dem Nothing Phone stehen noch zur Praxiskontrolle aus.

## Paketquelle

`https://yzue1980.github.io/UenkerLabs-AppStore/repo`

Repository-Fingerabdruck SHA-256:
`1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`

Unabhängiges selbst gehostetes F-Droid-Repository, nicht der offizielle Hauptkatalog.

## Entwicklung und Veröffentlichungen

- `main`: Quellcode, Tests, Konfiguration, Anleitungen.
- `gh-pages`: öffentliche Website, signierte APKs, signierte Paketindizes.
- `scripts/check-public-fdroid.py`: Prüft verpflichtende Indexfelder, APK-Hashwerte, Signaturen und Paketliste.
- `docs/FDROID_BETA_UPDATE_PROCESS.md`: Signaturkette und künftiger Updateablauf.

Private Signing-Keys und Passwörter werden nicht in GitHub gespeichert. Builds auf GitHub sind keine Android-Geräteabnahme.
