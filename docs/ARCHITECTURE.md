# UenkerLabs AppStore – Verteilung und Signaturkette

**Prüfstand:** 11.11.2026, GitHub-Konto nach Umbenennung `UenkerLabs`.

## Belegbarer GitHub-Stand

- `main`: fünf Android-Quellmodule, davon drei aktuell im signierten Beta-Katalog; Website und Release-Werkzeuge.
- `gh-pages`: drei F-Droid-App-Paketnamen (Kalender, Wecker, Spiele) und **vier APK-Dateien** (Kalender 0.1.0 und 0.2.0, Wecker 0.1.0, Spiele 0.1.0).
- Signierte Repository-Dateien: `repo/entry.jar`, `repo/index-v1.jar`, außerdem `repo/entry.json`, `repo/index-v1.json`, `repo/index-v2.json` und `repo/index.xml`.
- **F-Droid-Repository-Zertifikat SHA-256**: `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`.
- **APK-Signer-Zertifikat SHA-256 (Android-Apps)**: `D903BD3C7F9BB4FC9738B86982B9064EE9E493C47DC0C2F587B5C7828EA25D30`.
- Der archivierte Index und `gh-pages/config.yml` referenzieren `https://yzue1980.github.io/UenkerLabs-AppStore/repo`.

**Entscheidende Grenze:** Repository-Dateien und Git-Historie belegen ihre Existenz, nicht die heute funktionierende GitHub-Pages-Auslieferung, F-Droid-Client-Annahme oder eine erfolgreiche Aktualisierung auf dem Gerät.

## Umbenennung `YZUe1980` → `UenkerLabs`

GitHub leitet Repository-Weblinks normalerweise weiter, aber daraus folgt **keine Zusage** für die GitHub-Pages-Domain oder eine im signierten F-Droid-Index enthaltene Repository-Adresse. Die naheliegende neue Pages-URL `https://uenkerlabs.github.io/UenkerLabs-AppStore/repo` ist ein **zu prüfender Zielwert**, keine bestätigte aktive Paketquelle.

**Kein blindes Suchen-und-Ersetzen in `entry.json`, `index-v1.json` oder `index-v2.json`.** Diese Inhalte sind durch JAR-Signaturen beziehungsweise Hashwerte gebunden. Eine neue Adresse erfordert einen kontrollierten Rebuild der Indizes mit dem **bestehenden Repository-Schlüssel** und anschließende erneute Signierung. Bereits eingetragene F-Droid-Quellen müssen gegebenenfalls auf die verifizierte Adresse migriert werden.

## Kontrollierter Releaseablauf

1. Zieladresse über GitHub Pages-Einstellungen und öffentliches HTTPS tatsächlich bestätigen.
2. Bereits signierte APKs unangetastet lassen; vier APKs und drei App-Paketnamen abgleichen.
3. `gh-pages/config.yml` und F-Droid-Metadaten auf die **bestätigte** Zieladresse anpassen.
4. Indizes mit einem gepflegten `fdroidserver`-Werkzeug generieren, APK-Dateien und Hashwerte unverändert lassen.
5. `entry.jar` und `index-v1.jar` mit dem **vorhandenen** Repository-Zertifikat signieren; Fingerabdruck prüfen.
6. Index-/JAR-Inhalte, APK-Hashes und Host-Adresse **vor** Veröffentlichung vergleichen.
7. Gezielt manuell veröffentlichen; öffentliche HTTPS-Dateien erneut lesen und prüfen.
8. Die Quelle auf dem echten Android-Gerät hinzufügen, alle drei Apps installieren und den Kalender 0.1.0 → 0.2.0 als In-Place-Update kontrollieren.

Private Schlüssel und Kennwörter ausschließlich in berechtigten, verschlüsselten Verwahrorten; niemals in GitHub, Actions-Logs oder öffentlichen Artefakten. Wo die Schlüssel derzeit tatsächlich verwahrt werden, wurde im Rahmen dieser Quellcodeprüfung **nicht unabhängig geprüft**.
