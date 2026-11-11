# UenkerLabs Android Apps

## Aktive Beta-Versionen
- `calendar`: UenkerLabs Kalender 0.2.0-dev, Monatsansicht.
- `alarm`: UenkerLabs Wecker 0.1.0-dev, Weckfunktion und Widgets.
- `neon`: Neon Bastion 1.0.0-beta, 2,5D Raycasting-Shooter, acht Sektoren.
- `ember`: Ember Atlas 1.0.0-beta, Fantasy-Action-RPG, acht Kapitel.
- `skybound`: Skybound Shift 1.0.0-beta, Jump-and-Run, zwölf Levels.
- `gamekit`: Gemeinsamer, statisch eingebundener Android-Game-Unterbau; keine separate installierbare App.

## Historische Testprojekte
`arcade`, `phone` und `messages` bleiben im Git-Verlauf und in den Quellen zur Nachvollziehbarkeit, werden aber nicht mehr im aktuellen F-Droid-Katalog angeboten.

## Android-Builds
JDK 17, Android SDK 35, AGP 8.8.2, Gradle 8.10.2. Die drei unabhängigen Spiele wurden über GitHub Actions zusammen in Debug und Release gebaut. Erste statische Source-QA bestanden. Keine realen Geräte-/Framerate-/End-to-End-Spieltests durchgeführt; Beta-Status beibehalten. Die Spiele haben keine INTERNET-Berechtigung und speichern Fortschritt lokal.
