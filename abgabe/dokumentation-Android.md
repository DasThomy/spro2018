# Installation der Android-App:

Nach dem Klonen des Git repositories findet sich im Ordner „abgabe“ die installierbare APK Datei:

Commit: `c8c52df8b35d68687ed6f3b78f67e89c6c1f83c8` (Git-Tag: 1.0-release-app).
Die erste APK wurde vom Compiler fehlerhaft gebaut und die 2te enthält keine Änderungen im Code.

APK Datei: `nichtsverändertaberdercompilerewardoof.apk`


Diese APK-Datei dann auf dem gewünschte Android-Gerät speichern (per USB) und dort installieren. Anschließend sollte die App funktionieren.

Oder ADB auf der Konsole starten und mit dem Gerät verbinden.
Dann `adb install -r nichtsverändertaberdercompilerewardoof.apk` ausführen

## Vom Sourcecode
(Alternativlösung bei auftretenden Fehlern während der Installation):

Vorrausetzung: Gradle, Android Studio, Konsole/Terminal

Mit Hilfe von Android Studio das Projekt SwarmComposerApp starten. Anschließend werden die local.properties erstellt.

In der Konsole nun auf den Ordner SwarmComposerApp zugreifen.
Anschließend gradle wrapper aufrufen. Danach gradlew assembleDebug (bei Mac: ./gradlew assembleDebug) aufrufen.
Danach existiert in dem Ordner debug (Dateipfad: SoSe18_WSP3_1⁩ ▸ ⁨SwarmComposerApp⁩ ▸ ⁨app⁩ ▸ ⁨build⁩ ▸ ⁨outputs⁩ ▸ ⁨apk⁩ ▸ ⁨debug) die entsprechende apk.

Diese apk auf das gewünschte Android-Gerät ziehen und installieren. Anschließend sollte die App funktionieren.
