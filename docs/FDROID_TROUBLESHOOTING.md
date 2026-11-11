# F-Droid – aktuelle UenkerLabs-Paketquelle

**Stand: 11.11.2026.** Diese Datei löst die **historische** Fehlersuchanleitung ab, die vor der ersten Veröffentlichung noch „null APKs“ auswies. Diese damalige Aussage ist heute falsch, bleibt aber im Git-Verlauf belegbar.

## Aktuelle Quelle

- GitHub: https://github.com/UenkerLabs/UenkerLabs-AppStore
- F-Droid-Adresse: `https://uenkerlabs.github.io/UenkerLabs-AppStore/repo`
- Öffentlicher Einstieg: https://uenkerlabs.github.io/UenkerLabs-AppStore/repo/
- Index: https://uenkerlabs.github.io/UenkerLabs-AppStore/repo/entry.json
- Repository-Fingerabdruck SHA-256: `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`

Die Quelle hat **fünf aktive Beta-Pakete** (Kalender 0.2.0, Wecker 0.1.0, Neon Bastion, Ember Atlas, Skybound Shift). Telefon, SMS und die bisherige Arcade-Sammel-App gehören **nicht** zum aktiven Index.

## Bei „Ungültiges Repository“

1. In F-Droid unter *Paketquellen* die **neue** Adresse eintragen. Die ehemalige `yzue1980.github.io`-Adresse ist nach der Kontoumbenennung überholt.
2. Wenn das Client-Formular einen separaten Fingerabdruck verlangt, den oben dokumentierten SHA-256-Fingerabdruck verwenden. URL und Fingerabdruck nicht widersprüchlich kombinieren.
3. `entry.json` und `index-v2.json` über HTTPS abrufen; ein HTML-404 ist **kein** gültiger Index.
4. GitHub Actions / `scripts/check-public-fdroid.py` verwenden, um Zertifikat, v1/v2/XML, SHA256, Namen und Signaturkontinuität unabhängig zu überprüfen.
5. Falls der Client trotz grüner Transportprüfung ablehnt, die **konkrete Fehlermeldung und F-Droid-Version** sowie Datum und URL dokumentieren. Ohne echten Android-Clienttest wird keine erfolgreiche Geräteinstallation behauptet.

## Releases

Neuen Release-APK-Build mit unveränderter App-Signatur prüfen, Katalogdaten aus signierten APKs aktualisieren, v1- und entry-JAR mit unverändertem Repository-Zertifikat signieren, erst danach veröffentlichen und auf einem echten Gerät kontrollieren. Schlüssel und Passwörter gehören nicht in GitHub.

Offizielle Dokumentation: https://f-droid.org/en/docs/Setup_an_F-Droid_App_Repo/
