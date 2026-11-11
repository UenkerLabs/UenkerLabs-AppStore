> **Nachtrag 11.11.2026:** Der ursprüngliche folgende Bericht entstand **vor** der Veröffentlichung. **Aktueller Stand:** Kalender `0.2.0-dev` (versionCode 2) ist signiert und im öffentlichen F-Droid-Beta-Katalog mit fünf Apps veröffentlicht. Telefon/SMS sind aus dem aktiven Katalog entfernt, die alte Arcade-Sammlung wurde durch drei eigenständige Beta-Spiele ersetzt. Vollständiger Android-Geräte-/In-Place-Update-Test bleibt offen. Historische Aussagen wie „noch nicht veröffentlicht“ unten beziehen sich ausschließlich auf den damaligen Prüfstand.

# UenkerLabs Kalender – Umsetzung KAL-001 und KAL-002

Stand: 11. November 2026. Ausgangsbranch: `main`, Beta 0.1.0-dev / versionCode 1.

## Nutzervorgaben (freigegeben zur Umsetzung)
- KAL-001: Beim Kaltstart die aktuelle Monatsansicht anzeigen.
- KAL-002: Monatsdarstellung und Bedienprinzipien orientieren sich an Google Kalender; vertraute Monatsnavigation, farbige Terminbalken, Hervorhebung des heutigen Datums, Tag- und Termininteraktion sowie `+` für neue Termine. Eigenständiger UenkerLabs-Code und eigene Farb-/Markengestaltung.

## Implementiert in den Entwicklungsquellen (noch nicht veröffentlicht)
- Montag-bis-Sonntag-Raster für 4 bis 6 sichtbare Wochen, inklusive angeschnittener Tage anderer Monate.
- Bis zu zwei farblich gekennzeichnete Termintitel pro Tageszelle; zusätzlicher Terminzähler für mehr Einträge, detailreiche Tagesliste unter dem Monatsraster.
- Navigation per Pfeiltasten und horizontaler Wischgeste, „Heute“, Datumsauswahl und Auswahl des Tages.
- Über Menü: Monat, Woche, Tag und Terminübersicht mit 21 Tagen.
- Lesender Zugriff auf alle sichtbaren lokalen Android-Kalender einschließlich der vom Betriebssystem synchronisierten Konten (Google usw.); keine eigene Google OAuth-/Cloud-Integration.
- Wiederkehrende Termine über den `CalendarContract.Instances`-Provider, ganztägige sowie mehrtägige Einträge, Kalenderfarben.
- Termin-Details im Dialog, externe Bearbeitung über die verfügbare Android-Kalender-App; einfache Terminerstellung im eigenen Dialog unter Schreibrecht.
- Provider-Abfragen auf eigenem Hintergrund-Executor, Aktualisierung per ContentObserver, keine zusätzliche Netzwerkberechtigung.
- `MonthGrid` als getrennte testbare Datumslogik mit Java-Smoke-Test über 192 Monate.
- Anhebung des Quellcode-VersionCode auf 2 / 0.2.0-dev für einen zukünftigen signierten F-Droid-Upgradetest.

## Grenzen / QA
- Eine optische Annäherung an Google Kalender ersetzt keine 1:1-Kopie seiner geschützten Logos, Marken oder Quellcodes.
- Bearbeiten läuft vorläufig über eine andere installierte Android-Kalender-App; eigene vollumfängliche Bearbeitung und Wiederholungsdialoge noch nicht implementiert.
- Berechtigungsdialog und Android-Provider müssen real auf einem Gerät geprüft werden. Besonders: mehrere Kalender, All-day-/Recurring-Events, Wisch-/Scroll-Navigation, Termin-Updates, kleine Displays und Rotationswechsel.
- Kein APK signiert oder veröffentlicht. F-Droid bleibt beim früheren signierten Kalender 0.1.0-dev; keine Änderung der Paketquelle ohne nachfolgende Freigabe.
- Telefon und SMS: vom Nutzer abgewählt; über die Entfernung aus dem öffentlichen Beta-Katalog muss vor dem nächsten Release eine getrennte kontrollierte Indexaktualisierung erfolgen. Git-Historie und bisherige Build-Quellen bleiben zunächst erhalten. Spiele: Testprojekt, keine Weiterentwicklung.

## Primärquellen
- https://developer.android.com/reference/android/provider/CalendarContract.Instances – Zeitfenster-Abfrage, rekurrente Ereignisse, Abfragen nicht auf dem UI-Thread.
- https://developer.android.com/reference/android/provider/CalendarContract.CalendarColumns – Kalenderrechte, Sichtbarkeit, Kalenderfarbe.
- https://developer.android.com/reference/android/widget/GridLayout – Layout-Spezifikation.
