# UenkerLabs F-Droid – signierte Beta-Updates

Stand: 11.11.2026. Aktive Pakete: Kalender (0.2.0-dev/2), Wecker und Spiele (0.1.0-dev/1). Telefon und SMS sind aus dem aktiven v1/v2/XML-Katalog und den aktuellen APK-Dateien entfernt; Versionshistorie auf GitHub erhalten.

Kalender 0.2.0 nutzt den bisherigen App-Signierschlüssel mit SHA-256-Zertifikat `D903BD3C7F9BB4FC9738B86982B9064EE9E493C47DC0C2F587B5C7828EA25D30`.
F-Droid-Repository-Zertifikat SHA-256: `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`.

Private Schlüssel und Passwörter befinden sich ausschließlich im privaten UenkerLabs-Drive und sind nicht in GitHub Actions oder dem öffentlichen Repository hinterlegt. Indexmetadaten basieren auf den zuvor mit fdroidserver erzeugten Dateien; diese konkrete Version wurde lokal bearbeitet und danach erneut signiert.

### Aktualisierungsvorgang
1. Änderungen freigeben und versionCode erhöhen.
2. Android-Release-APK bauen, mit bisherigem App-Schlüssel signieren und APK-Signer verifizieren.
3. Quell-APKs in `gh-pages` bereitstellen; alte kompatible Versionen bei Bedarf behalten.
4. Metadaten mit fdroidserver aktualisieren oder sorgfältig gegen dessen Schema validieren.
5. index-v1.jar und entry.jar mit altem Repository-Schlüssel signieren und Fingerabdruck vergleichen.
6. Öffentliche GitHub-Pages-Veröffentlichung und HTTPS-Integritätsprüfung ausführen.
7. Installation, Datenbestand, Berechtigungen und In-Place-Update am Zielgerät überprüfen.

**Status:** Erfolgreich gebaut und signiert, keine dokumentierte reale Endgeräteabnahme.
