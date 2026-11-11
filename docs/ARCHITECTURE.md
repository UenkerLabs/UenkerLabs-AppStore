# UenkerLabs AppStore – Verteilung, Signaturkette und Betrieb

**Aktueller Prüfstand: 11.11.2026.** Originalbericht [GitHub-Aktualitätsprüfung](https://drive.google.com/file/d/1fuJ6g-VhtErJEr2sOfEsUiwF8LaULdZK/view). Historische Fassungen stehen im Git-Verlauf.

## Führender Stand

- Quellcode, Entwicklungsanleitungen und Prüfskripte: `main`.
- Öffentlich veröffentlichte Website, APKs und signierte v1/v2/XML-F-Droid-Indizes: `gh-pages`.
- **Kanonische Paketquelle:** https://uenkerlabs.github.io/UenkerLabs-AppStore/repo
- Quellcode und Issue-Tracker: https://github.com/UenkerLabs/UenkerLabs-AppStore
- Repository-Zertifikat SHA-256: `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`
- App-Zertifikat SHA-256: `D903BD3C7F9BB4FC9738B86982B9064EE9E493C47DC0C2F587B5C7828EA25D30`
- **Fünf aktive Beta-App-Pakete:** Kalender 0.2.0-dev, Wecker 0.1.0-dev, Neon Bastion 1.0.0-beta, Ember Atlas 1.0.0-beta, Skybound Shift 1.0.0-beta.
- **Nicht mehr öffentlich angeboten:** SMS, Telefon und die frühere Arcade-Minispielsammlung. Ihre historischen Quellen und Git-Versionen bleiben erhalten.

Die signierten `repo/entry.jar` und `repo/index-v1.jar` beinhalten jeweils die zugehörigen gehosteten JSON-Dateien. `repo/index-v2.json`, beide APK-Signer, Checksummen sowie `repo/index.xml` werden kontrolliert. Am 11.11.2026 wurde ein erneutes **Metadaten-Update** mit unverändertem Repository-Schlüssel signiert; keine APK wurde neu signiert.

## Beleggrenzen

Öffentliche Hosting-, Schema-, Index-, Hash- und JAR-Signaturprüfungen sind technische Nachweise. Eine Installation im echten F-Droid-Client, ein In-Place-Update, verlässliche Alarme, die Framerate und das Durchspielen aller 28 Spielabschnitte sind **noch nicht als vollständig abgenommen dokumentiert**. Die fünf Anwendungen dürfen nur als frühe Betas beschrieben werden.

## Sicherer Updateprozess

1. Quelle und Inhaberauftrag prüfen; Änderungen im Entwicklungsbranch erstellen.
2. APK bauen, Lizenz-/Berechtigungsprüfung und die verfügbaren automatischen Tests ausführen.
3. Echte Gerätetests durchführen; offene Einschränkungen dokumentieren.
4. Finale APK mit dem bisherigen App-Schlüssel signieren, Zertifikat und versionCode kontrollieren.
5. Metadaten aus den tatsächlichen Artefakten erzeugen bzw. kontrolliert anhand des F-Droid-Schemas aktualisieren. Alte GitHub-Kontonamen dürfen **nicht** in den aktiven Einträgen erscheinen.
6. `index-v1.jar` und `entry.jar` offline mit dem bisherigen F-Droid-Schlüssel signieren. `index-v2.json`-Digest, Dateigröße, Zertifikate und unveränderte APK-Hashes kontrollieren.
7. Quellcode, Website und signierte Metadaten kontrolliert veröffentlichen; Live-HTTPS-Test und GitHub-Actions-Protokolle prüfen.
8. Anschließend im echten Android-F-Droid-Client Repository-Akzeptanz, Installation, Datenhaltung und signiertes Update dokumentieren.

**Schlüsselverwaltung:** PKCS#12-Dateien und Passwörter bleiben in privaten berechtigten Ablagen, niemals in GitHub, Actions-Logs, öffentlichen Artefakten oder Screenshots. Keine Schlüsselrotation ohne gesonderten Auftrag. Ein veröffentlichter Beta-Build gilt nicht automatisch als vollständige Produktabnahme.

## GitHub Pages

Die aktive Paketadresse verwendet `uenkerlabs.github.io` statt `yzue1980.github.io`. Ausgelöste GitHub-Actions-Prüfungen kontrollieren F-Droid-Signaturen und Live-HTTPS, ersetzen aber keinen Endgerätetest. Die GitHub-Repository-Homepage wird **separat in den GitHub-Einstellungen** gepflegt.
