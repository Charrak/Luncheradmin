# Seniorenverwaltung: APKs mit GitHub Actions bauen

Dieses Repository enthält beide Projekte: `SeniorenBeheerder` (Betreuer-App) und `SeniorenLauncher` (angepasstes Empfangsgerät). Die APKs werden durch `.github/workflows/apk.yml` gebaut. Eine Installations- und PIN-Anleitung steht in `ANLEITUNG.md`.

## Auf GitHub hochladen und APKs herunterladen

1. Ein eigenes GitHub-Repository erstellen. Das ZIP **entpacken** und den Inhalt des Ordners `Seniorenverwaltung-GitHub` ins **Hauptverzeichnis** des Repositorys hochladen – einschließlich `.github/workflows/apk.yml`. Der Ordner `.github` muss direkt neben `SeniorenBeheerder` und `SeniorenLauncher` liegen; versteckte Dateien beim Hochladen mitnehmen.
2. Die Dateien auf den Branch `main` oder `master` committen. Der Workflow startet bei jedem Push auf diese Branches und bei Pull Requests. Alternativ im Repository auf **Actions → Android APKs bauen → Run workflow** klicken und den Branch auswählen. GitHub Actions muss für das Repository aktiviert sein.
3. Den Lauf öffnen. Wenn beide Jobs erfolgreich sind, stehen am unteren Ende unter **Artifacts** die Dateien **Seniorenverwaltung-Betreuer-Debug-APK** und **Senioren-Launcher-Debug-APK** zum Download bereit. GitHub verpackt jedes Artefakt als ZIP; darin liegt `app-debug.apk`. Der Upload läuft nur nach erfolgreichen Tests und erfolgreichem Build. Artefakte bleiben 30 Tage verfügbar. Diese werden nicht automatisch als GitHub Release veröffentlicht.
4. Die passende APK auf dem jeweiligen Android-Gerät installieren. Für den PIN-Schutz müssen beide angepassten Apps verwendet und die jeweiligen Schalter unabhängig voneinander eingeschaltet werden; siehe `ANLEITUNG.md`.

Es sind keine Secrets erforderlich. Die Debug-APKs werden mit dem automatisch erzeugten Debug-Schlüssel signiert. Sie eignen sich zum Testen, nicht als dauerhaft signierte Veröffentlichung. Eine bereits anders signierte Installation lässt sich damit normalerweise nicht aktualisieren. Bei jedem Workflow-Lauf kann ein anderer Debug-Schlüssel verwendet werden; eine Aktualisierung zwischen solchen Builds ist dann ebenfalls nicht möglich. Beim Deinstallieren können App-Daten verloren gehen. Für dauerhafte Updates ist ein eigener Release-Signaturprozess mit sicher verwahrtem Keystore notwendig.

Dieser Workflow wurde vorbereitet, aber hier nicht auf GitHub ausgeführt. Falls ein Job fehlschlägt, öffne dessen Protokoll unter Actions. Tests und APKs ersetzen keine Überprüfung des SMS-Ablaufs auf beiden Geräten.

## Fertig gebaute APKs

Die beiden Debug-APKs liegen direkt im Ordner `apk/`:

- `apk/Seniorenverwaltung-Betreuer-debug.apk` – Betreuer-App (`com.example.seniorenbeheerder`, Version 1.0)
- `apk/Senioren-Launcher-debug.apk` – Senioren-Launcher (`com.seniorenlauncher`, Version 0.8.8)

Sie sind mit dem automatisch erzeugten Debug-Schlüssel signiert und direkt auf dem Gerät installierbar (Unbekannte Quellen bzw. „Installation aus dieser Quelle zulassen“ erlauben). Für neue Builds: Actions → **Android APKs bauen** → **Run workflow**; die Ergebnisse stehen dort als Artefakte bereit.

### APKs als GitHub Release veröffentlichen

`.github/workflows/release.yml` baut bei einem Tag `v*` beide Apps und hängt die APKs als Release-Assets an:

```
git tag v1.0
git push origin v1.0
```

Der Lauf erscheint unter Actions → **APKs veröffentlichen**, die Dateien anschließend unter Releases.
