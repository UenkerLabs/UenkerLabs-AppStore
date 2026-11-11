# UenkerLabs AppStore

**Android-Apps in offener Entwicklung.** Eigener signierter F-Droid-Beta-Kanal für Installation, Tests, Verbesserungen und spätere Updates.

- **Website und App-Vorstellung:** https://yzue1980.github.io/UenkerLabs-AppStore/
- **F-Droid-Paketquelle:** https://yzue1980.github.io/UenkerLabs-AppStore/repo
- **Quellcode:** https://github.com/YZUe1980/UenkerLabs-AppStore
- **Feedback:** https://github.com/YZUe1980/UenkerLabs-AppStore/issues

## Fünf veröffentlichte Beta-Apps (0.1.0-dev)

| App | Aktueller Funktionsumfang | Einschränkungen |
|---|---|---|
| **Kalender** | Android-Kalendertermine anzeigen und erstellen | Nutzt vorhandene Kalender-Synchronisierung |
| **Nachrichten** | SMS-Ansicht, lokale Spam-Markierung und Sperrliste | Noch kein vollständiger Standard-SMS-Client; kein MMS/RCS |
| **Telefon** | Wählhilfe, Anrufliste, optionale lokale Anrufprüfung | Noch kein vollständiger Standard-Dialer |
| **Wecker** | Alarme, Snooze und fünf Startbildschirm-Widgets | Als alleiniger Wecker für wichtige Termine noch nicht gerätegetestet |
| **Spiele** | Fünf Offline-Minispiele | Beta-Testversion, kein Internetzugriff angefordert |

Alle fünf Android-Release-APKs sind mit **demselben dauerhaft vorgesehenen App-Signierschlüssel** signiert und im F-Droid-Repository gelistet. Der Repository-Signierschlüssel ist davon getrennt.

**Wichtig:** Erfolgreicher Build, gültige Signaturen und erreichbare Indexdateien sind keine Bestätigung einer fehlerfreien Installation oder Funktion auf Android-Geräten. Der praktische Beta-Test und spätere Updates werden gesondert geprüft.

## Paketquelle hinzufügen

F-Droid öffnen → Paketquellen → Neue Paketquelle → diese Adresse einfügen:

`https://yzue1980.github.io/UenkerLabs-AppStore/repo`

Repository-Zertifikat, SHA-256:
`1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`

Eine unabhängige Paketquelle, **nicht** der offizielle F-Droid-Hauptkatalog. Behalte für SMS, Telefon und wichtige Wecktermine vorerst die bisherigen Standard-Apps.

## Nachvollziehbare Entwicklung

- **`main`** – Android-Quellcode, Website, Werkzeuge und Prüfscripte.
- **`gh-pages`** – öffentliche Website, signierte Beta-APKs und F-Droid-Indexdateien.
- Repository-Metadaten beruhen auf `fdroidserver`; die aktuellen Texte sind in Deutsch und Englisch gepflegt.
- `scripts/check-public-fdroid.py` kontrolliert öffentliche Dateien, Index-Hashes, Paketanzahl und Signaturen.
- Neue Versionen benötigen eine erhöhte `versionCode` und denselben App-Signierschlüssel.
- Keine privaten Schlüssel, Passwörter oder Nutzerdaten in GitHub-Repositories oder GitHub-Actions-Artefakten.

Weitere technische Informationen: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) und [docs/FDROID_BETA_UPDATE_PROCESS.md](docs/FDROID_BETA_UPDATE_PROCESS.md).

## Weitere Ideen

Weitere Anwendungen wie Mailclient oder Browser sind **nicht** Teil der aktuellen Beta-Veröffentlichung und werden nicht als vorhandene Apps beworben.

## Lizenz

Website und Repository-Dokumentation unter [Apache-2.0](LICENSE). App-Code ist in Entwicklung; Drittanbieter-Abhängigkeiten und rechtliche Anforderungen müssen pro Veröffentlichung gesondert überprüft werden.
