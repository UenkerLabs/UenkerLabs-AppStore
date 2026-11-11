# UenkerLabs F-Droid – Paketquelle prüfen und reparieren

**Stand:** 11.11.2026 nach Änderung des GitHub-Benutzernamens.

## Fakten statt überholter Anleitungen

- Aktuelles Repository: https://github.com/UenkerLabs/UenkerLabs-AppStore
- Das `gh-pages`-Verzeichnis `repo/` enthält **vier signierte Beta-APKs** für **drei App-Paketnamen**; die alte Aussage „0 installierbare Apps“ ist überholt.
- Signierte Index-Dateien sind vorhanden. Die darin signierte **alte** Repository-Adresse ist `https://yzue1980.github.io/UenkerLabs-AppStore/repo`.
- Der neue GitHub-Benutzername ist `UenkerLabs`. Die vermutlich künftig verwendbare Pages-Adresse `https://uenkerlabs.github.io/UenkerLabs-AppStore/repo` ist **noch nicht über eine öffentliche HTTPS-Prüfung bestätigt**.
- Repository-Zertifikatsfingerabdruck SHA-256: `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`. Er darf nicht durch einen neuen Schlüssel ersetzt werden.

## Sichere Prüf- und Reparaturfolge (keine automatisch ausgeführte Änderung)

1. GitHub `Settings → Pages` im Repository aufrufen und die tatsächliche Zieladresse sowie die Veröffentlichungsquelle verifizieren: https://github.com/UenkerLabs/UenkerLabs-AppStore/settings/pages
2. Die **verifizierte** HTTPS-Adresse für `/repo/entry.json`, `/repo/entry.jar`, `/repo/index-v1.jar`, `/repo/index-v2.json` und die vier APKs tatsächlich abrufen. 404, HTML-Fehlerseite oder DNS-Fehler sind keine gültige Paketquelle.
3. Alte und neue Adresse aus der Auslieferung mit den Feldern `repo.address` in v1/v2 und `repo_url` abgleichen. Diese Werte enthalten derzeit den alten Pages-Host.
4. **Erst nach bestätigter Hostwahl:** die Indizes kontrolliert neu erstellen und mit dem bisherigen Repository-Schlüssel offline signieren, ohne APK-Signer, App-IDs oder Versioncodes zu wechseln.
5. Beide signierten JAR-Dateien, JSON-Hashes, Indexinhalte, Fingerabdruck und APK-Hashes prüfen. Danach einmalig bewusst veröffentlichen.
6. Mit dem F-Droid-Client die freigegebene neue Adresse als Paketquelle hinzufügen und Installation sowie ein In-Place-Update physisch testen.

Die bisherige Meldung „ungültige Paketquelle“ kann mehrere Ursachen haben; aus dem Repository allein lässt sich die aktuelle Ursache nicht sicher bestimmen. Ein erfolgreicher GitHub-Actions-Lauf ist kein Gerätnachweis.

## Historischer Fehler

Am 11.11.2026 fehlte in einem früheren v2-Index das erforderliche `index.numPackages`-Feld; das wurde im späteren Index auf **3** korrigiert. Der ältere Reparaturbericht dokumentiert den damaligen Zustand und ist nicht als aktuelle Null-Paket-Aussage zu lesen.

Primärquellen:
- https://f-droid.org/en/docs/Setup_an_F-Droid_App_Repo/
- https://f-droid.org/en/docs/All_our_APIs/
- https://docs.github.com/en/account-and-profile/concepts/username-changes
