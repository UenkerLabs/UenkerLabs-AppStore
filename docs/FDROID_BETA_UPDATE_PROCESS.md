# UenkerLabs – F-Droid Beta Updateprozess

Stand 11.11.2026: fünf aktive Apps (Kalender, Wecker, Neon Bastion, Ember Atlas, Skybound Shift). Die frühere Spielesammlung, Telefon und SMS werden nicht mehr aktiv angeboten. Originalquellcode und Historie bleiben erhalten.

**Sicherheitskontinuität:** Android-App-Signer SHA-256 `D903BD3C7F9BB4FC9738B86982B9064EE9E493C47DC0C2F587B5C7828EA25D30`, F-Droid-Repository-Signer SHA-256 `1A25F39551B1615980705043AD003D9F3258A8E96E91539DDB0D89ABFCD3B0F8`. Private Schlüssel und Passwörter werden nicht auf GitHub veröffentlicht.

1. Quellcodeänderungen erstellen, versionCode erhöhen, Java/Android-Test/Build durchführen.
2. Release-APK mit **bestehendem** App-Signierschlüssel außerhalb GitHub Actions signieren und v2/v3 plus Zertifikatsfingerabdruck prüfen.
3. Neue signierte APKs und originale App-Symbole im `gh-pages/repo/` ablegen.
4. Metadaten mit `fdroidserver` erzeugen oder bestehenden `fdroidserver`-Schemasatz kontrolliert und nachvollziehbar aktualisieren.
5. Index-v1.jar und entry.jar außerhalb GitHub Actions mit demselben Repository-Schlüssel signieren; Pflichtfelder und APK-Hashes prüfen.
6. GitHub Pages Deploy + Live-HTTPS-Tests ausführen; danach realen F-Droid-Client und Android-Update testen.

**Wichtig:** Ein bestandener Build und eine gültige Signatur belegen noch keine vollständige Funktion, keine gleichmäßige Framerate und keine Tests aller Levels. Die drei neuen Spiele sind unabhängige Beta-Pakete und noch nicht auf dem Nutzergerät spielerisch abgenommen.
