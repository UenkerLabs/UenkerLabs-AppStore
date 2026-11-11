# UenkerLabs AppStore

Eigenes signiertes F-Droid-Beta-Repository für Android. Die Apps können einzeln installiert und später über dieselbe Paketquelle aktualisiert werden.

- **Website:** https://yzue1980.github.io/UenkerLabs-AppStore/
- **Paketquelle:** https://yzue1980.github.io/UenkerLabs-AppStore/repo
- **GitHub-Quellcode:** https://github.com/UenkerLabs/UenkerLabs-AppStore

## Aktive Android-Beta-Apps

| App | Version | Umfang |
| --- | --- | --- |
| UenkerLabs Kalender | 0.2.0-dev | Monatsansicht, farbige Termine, Navigation und Android-Kalenderintegration |
| UenkerLabs Wecker | 0.1.0-dev | Alarmplanung und Widgets; bitte Systemwecker weiterverwenden |
| **Neon Bastion** | 1.0.0-beta | Originaler 2,5D-Shooter mit 8 Level-Sektoren |
| **Ember Atlas** | 1.0.0-beta | Eigenständiges Action-Rollenspiel mit 8 Kapiteln |
| **Skybound Shift** | 1.0.0-beta | Side-Scrolling-Jump-and-Run mit 12 Levels |

Die drei Spiele sind getrennte Android-Anwendungen mit unabhängigen Paketkennungen. Sie verwenden eigene Canvas-Grafik und originale Inhalte; keine fremden Spielgrafiken oder Quellcodes übernommen. Alle Spiele sind offline. Die Spiele sind **technische Beta-Versionen**, keine durch realen Gerätetest vollständig abgenommenen Endfassungen. Sound, Multiplayer und eine vollwertige 3D-Polygon-Engine sind nicht implementiert.

Die vorherige Minispiel-Sammlung `arcade` sowie die abgebrochenen Projekte Telefon und SMS wurden aus dem öffentlichen Katalog entfernt, aber der historische Quellcode und die Git-Historie sind erhalten. Das Entfernen aus F-Droid deinstalliert nichts automatisch.

## Installation und Update

In F-Droid diese zusätzliche Paketquelle eintragen:

`https://yzue1980.github.io/UenkerLabs-AppStore/repo`

Repository-Fingerabdruck SHA-256:
`1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`

Die APKs sind dauerhaft mit dem UenkerLabs-App-Signierschlüssel signiert; die Indexdateien mit dem separaten F-Droid-Repository-Schlüssel. Keine privaten Schlüssel/Passwörter liegen auf GitHub.

## Entwicklung und Grenzen

- `main`: Android-Quellcode, Tests, F-Droid-Schema-Prüfungen, Dokumentation.
- `gh-pages`: Website, signierte APKs und signierte Indexdateien.
- GitHub Actions prüft Java/Android-Builds und öffentliche Paketdaten.
- Die tatsächliche Darstellung, Touch-Steuerung, Framerate sowie alle 28 Game-Stages müssen auf einem Android-Gerät durchgespielt und überprüft werden. Tests auf realen Geräten sind **noch nicht bestätigt**.
- Status und technische Grenzen: [docs/STANDALONE_GAMES_2026-11-11.md](docs/STANDALONE_GAMES_2026-11-11.md).
