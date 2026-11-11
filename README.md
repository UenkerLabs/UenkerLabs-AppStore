# UenkerLabs AppStore

Offenes Android-Testprojekt mit einer **eigenen, signierten F-Droid-Beta-Paketquelle**. Es handelt sich **nicht** um den offiziellen F-Droid-Hauptkatalog und **nicht** um produktionsreife System-App-Ersatzprogramme.

**Stand:** 11.11.2026 – GitHub-Benutzername auf `UenkerLabs` geändert. Die Hosting-Adresse der Paketquelle ist nach dem Namenswechsel noch **nicht unabhängig verifiziert**.

## Repository und aktueller Beta-Katalog

- **Quellcode und Issues:** https://github.com/UenkerLabs/UenkerLabs-AppStore
- **`main`**: Android-Quellcode, Webvorlage, Anleitungen und ausschließlich gezielt ausgelöste Prüfungen.
- **`gh-pages`**: veröffentlichungsbereite Website, vier vorhandene signierte APK-Dateien und signierte F-Droid-Indexdateien.
- **Aktiver F-Droid-Katalog: drei Paketnamen**, nicht fünf. Kalender enthält zwei APK-Versionen.
- **Pausiert:** Telefon und Nachrichten. Quellcode bleibt aus historischen Gründen vorhanden; APKs sind nicht mehr im aktuellen Verteilungsbranch enthalten.

| App | Betaversion | Paketname | Gerätestatus |
| --- | --- | --- | --- |
| Kalender | 0.2.0-dev, versionCode 2 (0.1.0 bleibt als ältere Version) | `com.uenkerlabs.calendar` | Android-Installation und In-Place-Update nicht abgenommen |
| Wecker | 0.1.0-dev, versionCode 1 | `com.uenkerlabs.alarm` | Kritische Alarmfunktionen nicht auf Endgeräten abgenommen |
| Spiele | 0.1.0-dev, versionCode 1 | `com.uenkerlabs.arcade` | Gameplay auf Endgeräten nicht abgenommen |

## GitHub-Umbenennung und F-Droid-Adresse

Die **signierten** v1-/v2-Indizes in `gh-pages/repo` enthalten noch die damalige Repository-Adresse:

`https://yzue1980.github.io/UenkerLabs-AppStore/repo`

Nach der GitHub-Umbenennung ist diese Adresse **nicht als funktionierend bestätigt**. Als neue GitHub-Pages-Adresse kommt `https://uenkerlabs.github.io/UenkerLabs-AppStore/repo` in Betracht; **Verfügbarkeit und GitHub-Pages-Einstellungen müssen geprüft werden**. Die Adresse darf erst nach kontrollierter Neu-Erzeugung und Signierung der passenden F-Droid-Indizes als neue Paketquelle beworben werden. Eine bloße Ersetzung in signierten JSON-Dateien wäre unzulässig.

Bisheriger öffentlicher **F-Droid-Repository-Zertifikatsfingerabdruck (SHA-256)**:

`1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`

Dieser Fingerabdruck ist nicht mit dem APK-Signer oder einem Dateihash zu verwechseln.

## Veröffentlichungsgrenzen

APK-Dateien und signierte Indizes sind im Git-Branch vorhanden. Daraus folgen **keine** bestätigte Erreichbarkeit über HTTPS, F-Droid-Client-Kompatibilität oder erfolgreiche Installation/Updates auf einem Android-Gerät.

- Prozess und Schlüsselidentität: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
- Reparatur und Re-Hosting: [docs/FDROID_TROUBLESHOOTING.md](docs/FDROID_TROUBLESHOOTING.md)
- Geprüfter Beta-Updateablauf: [docs/FDROID_BETA_UPDATE_PROCESS.md](docs/FDROID_BETA_UPDATE_PROCESS.md)
- Öffentliche Prüfroutine: [scripts/check-public-fdroid.py](scripts/check-public-fdroid.py)

Private Signierschlüssel und Passwörter gehören **niemals** in GitHub, Workflows, Artefakte oder die Website. Kein erneuter Schlüssel ohne bewusst geplante Migration; keine automatische Veröffentlichung bei Quellcodeänderungen.
