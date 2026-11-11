# UenkerLabs Games – drei eigenständige Android-Spiele

Stand: 11.11.2026 | Ursprung: ausdrücklicher Auftrag des Inhabers

## Historischer Ausgangspunkt
Die fünf Offline-Minispiele in `arcade` wurden vom Inhaber als reine Funktionstests eingestuft. Der Wunsch lautet nun: keine Spiele-Sammlung, sondern eigenständig installierbare Android-Spiele, mit eigenständigem Konzept, mehreren Levels und Fortschritt. Der vorhandene Arcade-Quellcode bleibt als historische Entwicklungsstufe erhalten. Die Arcade-APK wird aus dem aktiven F-Droid-Katalog entfernt, sobald die neuen Spiele freigegeben und veröffentlicht werden.

## Originalkonzepte

### Neon Bastion (com.uenkerlabs.neon)
- **Genre:** futuristischer Ego-Shooter mit Raycasting-3D-Perspektive (*2.5D*, nicht vollwertige Polygon-3D-Engine)
- **Schauplatz:** acht neonbeleuchtete Sektoren einer verlassenen Orbitalstation.
- **Ziel:** Wach-Drohnen eliminieren, Schildenergie aufnehmen, das Ausgangsportal erreichen, acht Sektoren sichern.
- **Spielsystme:** Gegner-KI mit Sichtlinie, Waffenenergie und Nachladung, Gesundheitsanzeige, Levels, Highscore, Minimap, virtuelle Steuerung auf Touchscreens, Pause und Wiederanlauf.
- **Progression:** freigeschaltete Sektoren und Gesamtpunktzahl dauerhaft in lokalen Android-Einstellungen.

### Ember Atlas (com.uenkerlabs.ember)
- **Genre:** farbiges Top-down-Fantasy-Action-RPG mit eigener Geschichte.
- **Schauplatz:** acht eigenständige Kapitel von der Mooswarte bis zum Herz der Asche.
- **Ziel:** drei Lichtfunken pro Kapitel einsammeln, Gegner besiegen und zum freigeschalteten Tor finden.
- **Spielsystme:** Nahkampfangriff, Magie mit Manavorrat, Gegnerverfolgung, Bossbegegnungen, Münzen, permanente Verbesserungen für Klinge/Schutz/Tempo, Kapitelwahl, Pause und Neustart.
- **Progression:** Kapitel, Münzen und Fähigkeiten lokal gespeichert.

### Skybound Shift (com.uenkerlabs.skybound)
- **Genre:** Side-scrolling Jump-and-Run.
- **Schauplatz:** drei visuelle Welten mit je vier Levels (insgesamt 12).
- **Ziel:** springend die Portale erreichen, Kristalle sammeln, Gegner überwinden, Checkpoints aktivieren.
- **Spielsystme:** Bewegung, Sprung (ab Level 5 Doppelsprung), ab Level 9 Dash, Kollisionen, Sturz- und Gegnerschaden, Wiederanlauf am Checkpoint, Levelwertung von 1 bis 3 Sternen.
- **Progression:** Level-Freischaltung und Bestwertung je Level lokal gespeichert.

## Architektur, Grafik und Rechte
- Drei **separate** Android-Pakete (`applicationId`), keine gemeinsame App-Oberfläche.
- Gemeinsamer `gamekit`-Quellcode ist lediglich eine statisch eingebundene Android-Bibliothek. Jede APK funktioniert eigenständig, offline und ohne Berechtigungsanfrage für Internetzugang.
- Originale Szenarien, Spielmechanik-Implementierungen, eigene Vektorgrafiken sowie prozedural erzeugte App-Icons. Es wurden keine fremden Spiele, Grafiken oder fremden geschützten Spielinhalte kopiert.
- Die Spielszenen werden auf einem logischen Canvas von 1280 × 720 Pixeln in Gerätedichte skaliert. Das ist **keine** unabhängige visuelle Zertifizierung eines Nintendo-DS-Grafikniveaus und keine Garantie einer hohen Framerate auf beliebigen Geräten.
- Referenz zur technischen Orientierung: libGDX (Apache-2.0, https://github.com/libgdx/libgdx), Godot Engine (MIT, https://github.com/godotengine/godot). Die Spiele verwenden bewusst keine kopierten Komponenten daraus.

## Bekannte Grenzen, QS
- Android-Build und statische Quellprüfungen können Korrektheit des tatsächlichen Spielverlaufs, Touchgesten, Darstellung und Framerate am Nutzergerät nicht bestätigen.
- Vor einer Produktivfreigabe: jedes Level am realen Android-Gerät durchspielen, Speicherstände / Neustart / Bossgegner / Wechsel der Orientierung / Input-Überlappungen prüfen, auf mehreren Displaygrößen und Android-Versionen testen.
- Spielsound fehlt in dieser ersten vollständig codebasierten Beta. Werbung, Online-Dienste, Tracking und Multiplayer sind nicht vorhanden.
- Eigene Software kann Bugs enthalten; Fortschrittsdaten beim Update und Widerruf von Berechtigungen gesondert testen.

## F-Droid-Veröffentlichung
Zuerst Android Release APKs bauen, auf dem lokalen Prüfsystem mit UenkerLabs-App-Key signieren (nicht per GitHub-Action privates Material laden), APK-Signer prüfen; dann drei neue unabhängige Metadateneinträge mit fdroidserver erzeugen und die Indizes offline mit dem **bestehenden** F-Droid-Signierschlüssel signieren. Arcade aus dem aktuellen Store entfernen, historischen Code beibehalten. Neue Pakete ausschließlich nach Bestätigung des technischen Release-Checks veröffentlichen.
