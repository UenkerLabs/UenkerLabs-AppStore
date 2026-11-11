# UenkerLabs F-Droid – signierte Beta-Updates

**Prüfstand 11.11.2026:** drei aktive App-Pakete, vier APK-Dateien. Kalender 0.2.0-dev / versionCode 2, Wecker und Spiele jeweils 0.1.0-dev / versionCode 1. Telefon und Nachrichten sind nicht mehr im aktuellen aktiven Katalog.

- **Repository-Zertifikatsfingerabdruck SHA-256:** `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`
- **App-Signierer SHA-256:** `D903BD3C7F9BB4FC9738B86982B9064EE9E493C47DC0C2F587B5C7828EA25D30`
- Die Indizes enthalten derzeit noch die alte Pages-Adresse `https://yzue1980.github.io/UenkerLabs-AppStore/repo`. Nach der Umbenennung darf eine neue öffentliche Adresse **erst nach Prüfung, Neubildung und Signierung** als funktionierende Paketquelle beworben werden.
- Dass Schlüssel gesichert sind und wo genau sie verwahrt werden, wurde bei dieser Repository-Sichtung nicht unabhängig überprüft. Private Schlüssel niemals in dieses öffentliche Repository oder in Actions hochladen.

## Kontrollierter Vorgang

1. Eine APK-Änderung bewusst freigeben, versionCode erhöhen und die APK mit **dem bisherigen App-Signierschlüssel** signieren.
2. Den signierten APK-Inhalt, Android-Paketnamen, Zertifikat und die Berechtigungen prüfen.
3. Die tatsächlich funktionierende Pages-/Host-Adresse gesondert verifizieren.
4. `fdroidserver` für die Indexdateien mit dem **bestehenden Repository-Schlüssel** benutzen; neue Adresse konsistent in `config.yml` und v1/v2 erfassen.
5. Beide signierten JARs, `entry.json`, v1/v2, APK- und Icon-Hashes, Zeitstempel und `numPackages` prüfen.
6. Eine manuelle Veröffentlichung erst nach der vollständigen lokalen Prüfung durchführen. Anschließend öffentliche HTTPS-Dateien mit den erwarteten Bytes vergleichen.
7. Die Paketquelle und das In-Place-Update auf dem Android-Gerät wirklich prüfen.

**Noch offen:** Nachweisbare öffentliche Erreichbarkeit nach Namenswechsel, F-Droid-Client-Installation und Kalender-Update auf dem Zielgerät. Ein erfolgreicher Build oder JAR-Check ist keine Geräteabnahme.
