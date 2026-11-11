# UenkerLabs AppStore – Außenauftritt aktualisiert

**Stand:** 11.11.2026 · **Bereich:** öffentliche Website, F-Droid-Metadaten, GitHub-Präsentation

## Bestätigter Ausgangsbefund
Die alte Startseite trug weiterhin „Apps are on the way“ und zeigte einen leeren Katalog, obwohl fünf signierte Beta-APKs vorhanden waren. Sprache und Versprechen passten nicht zum tatsächlichen Entwicklungsstand.

## Überarbeitung
- Deutsche Startseite mit fünf realen Beta-App-Karten, Funktions- und Sicherheitsgrenzen.
- Installation, Repository-Adresse, öffentlicher SHA-256-Zertifikatsfingerabdruck und direkte APK-Links nachvollziehbar aufgeführt.
- Eigenständige Seite `repo/index.html` stilistisch und inhaltlich angepasst.
- F-Droid-App-Beschreibungen deutsch/englisch aktualisiert; APKs, Paketnamen, Versioncodes und App-Signaturen unverändert.
- v1-/v2-Indexdaten nach Metadatenänderung erneut mit dem bestehenden Repository-Schlüssel signiert; öffentliche Signaturidentität erhalten.
- Externe Google-Fonts-Ladeanforderung entfernt, lokale CSS-Systemfonts verwendet.
- Website-Quellcode im Hauptbranch mit der veröffentlichten Seite synchronisiert.

## QS-Status
**Lokal überprüft:** fünf App-Einträge, interne Links, keine doppelten HTML-IDs, Index-Paketanzahl, Hashes von APKs/Icon, signierte JAR-Inhalte, Zertifikatsfingerabdruck und de/en-US-Metadaten. **Nicht nachgewiesen:** Layout-Abnahme auf einem realen Mobilgerät sowie F-Droid-Installation und Update auf Nutzersmartphone.

## Offene Veröffentlichungsfragen
Ein rechtlich ausreichendes Impressum bzw. eine Datenschutzerklärung für einen öffentlich erreichbaren, möglicherweise geschäftsmäßig betriebenen Webauftritt wurde nicht geprüft. Keine persönlichen Anschriften oder ungesicherten Rechtstexte erfinden. Vor aktiver kommerzieller Bewerbung durch die zuständige Fachrolle prüfen lassen.

## Nachtrag – tatsächlicher Stand später am 11.11.2026
Die hier beschriebene Einführungsversion wurde durch Kalender 0.2.0 und drei **eigenständige** signierte Spiele ergänzt. Aus dem aktiven Katalog wurden SMS, Telefon und die frühere Arcade-Sammlung entfernt; der Katalog enthält weiterhin fünf Apps. Die GitHub-Identität wurde von `YZUe1980` zu `UenkerLabs` geändert. Die kanonische Paketquelle ist https://uenkerlabs.github.io/UenkerLabs-AppStore/repo . Die älteren Aussagen oben bleiben als zeitgebundener Entwicklungsnachweis erhalten.

Quellcode: https://github.com/UenkerLabs/UenkerLabs-AppStore
