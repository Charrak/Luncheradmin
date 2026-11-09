# Seniorenverwaltung – Deutsch/Niederländisch und SMS-PIN

Dieses Paket enthält zwei Android-Quellprojekte:

* `SeniorenBeheerder/` – die Betreuer-App mit deutscher Oberfläche und Sprachauswahl Deutsch/Niederländisch.
* `SeniorenLauncher/` – die angepasste Empfänger-App. Der ursprüngliche Launcher erkennt SMS mit vorangestellter PIN nicht und überprüft diese auch nicht. Für echte PIN-Prüfung muss diese angepasste Version auf dem Empfangsgerät laufen.

## Einrichtung

1. Beide Apps aus dem Quellcode bauen und auf dem jeweiligen Gerät installieren. Der Launcher muss auf dem Empfangsgerät als Standard-SMS-App eingerichtet sein. Die Telefonnummer des Betreuers muss dort weiterhin als SOS-Kontakt autorisiert sein.
2. In der Betreuer-App unter **Einstellungen → SMS-PIN-Schutz** dieselbe vierstellige PIN speichern, die auf dem Empfangsgerät für die Einstellungen verwendet wird. **PIN für SMS verwenden** einschalten.
3. Auf dem Empfangsgerät im Senioren Launcher unter Einstellungen → **Veiligheid & Noodhulp → SMS PIN-beveiliging** den Schutz ebenfalls einschalten. Die beiden Schalter sind unabhängig voneinander; die Betreuer-App ändert den Empfängerschalter nicht automatisch.
4. Eine harmlose Anfrage wie die Standortabfrage testen. SMS-Befehle werden bei aktiviertem Senderschalter als `1234 #WAAR` bzw. `1234 LAUN_ZOEK` verschickt. Der Empfänger akzeptiert bei eingeschaltetem Empfängerschutz ausschließlich Befehle mit der korrekten PIN und einem Leerzeichen; falsche oder fehlende PINs werden verworfen. Die bestehende SOS-Kontaktprüfung bleibt zusätzlich aktiv.
   Nach der Antwort erscheint der Standort in der Betreuer-App als Karte. Über **In Maps öffnen** (unten rechts auf der Karte) startet die Karten-App des Betreuer-Geräts an genau dieser Position: zuerst Google Maps per `geo:`-Link, sonst der per SMS empfangene Link, zuletzt der Google-Maps-Weblink im Browser. Ohne installierte Karten-App erscheint ein Hinweis.
5. Zum Abschalten beide Schalter ausschalten. Ohne Empfängerschutz werden alte SMS ohne PIN wieder akzeptiert; zusätzlich werden korrekt präfixierte SMS akzeptiert.

Die Sprachauswahl ist beim ersten Start und in der Betreuer-App unter **Einstellungen → Sprache** verfügbar. Die SMS-Befehle selbst bleiben für Kompatibilität unverändert; Antworten der Empfänger-App können weiterhin niederländisch sein.

## PIN ändern und Sicherheit

Der Befehl zum Ändern der Empfänger-PIN verwendet bei eingeschaltetem Schutz zunächst die bisherige PIN, z. B. `1234 #PIN 5678`. Erst nach Bestätigung durch den Empfänger die neue PIN in der Betreuer-App speichern. Falls die Bestätigung ausbleibt, am Empfänger nachsehen, bevor weitere Befehle gesendet werden.

SMS sind nicht Ende-zu-Ende-verschlüsselt: Die PIN und Befehle können im Netz und gegebenenfalls in SMS-Verläufen sichtbar sein. Die PIN wird im privaten App-Speicher lokal gespeichert, aber nicht zusätzlich verschlüsselt. Sie ist vom Android-Backup der Betreuer-App ausgenommen. Das ist kein Schutz gegen Zugriff auf ein kompromittiertes Gerät oder gefälschte Absendernummern. Bitte eine eigene, nicht anderweitig verwendete PIN wählen und für vertrauliche oder sicherheitskritische Anwendungen einen sichereren Kanal nutzen.

## Bauen und Prüfstand

Jedes Verzeichnis ist ein eigenständiges Gradle-Projekt. Mit Android Studio, JDK 21 und dem passenden Android SDK öffnen oder im jeweiligen Projektverzeichnis `./gradlew assembleDebug` ausführen. Die Betreuer-App benötigt SDK 36.1, der Launcher SDK 36. Debug-APKs haben eine eigene Signatur und können eine anders signierte bestehende Installation nicht ohne Weiteres aktualisieren; eine Deinstallation kann Daten löschen. Für Updates auf bestehenden Geräten ist die ursprüngliche Signatur notwendig.

Die reine PIN-Formatierung und Authentifizierung wurden mit einem Kotlin-Smoke-Test für korrekte, falsche, fehlende und deaktivierte PIN geprüft. Die Betreuer-App wurde kompiliert, die APK-Erstellung in dieser Umgebung aber nicht erfolgreich abgeschlossen. Der Launcher-Build und die vollständigen Abläufe auf realen Android-Geräten wurden nicht abgeschlossen bzw. nicht getestet. Es liegt daher keine fertig getestete APK bei; vor produktiver Nutzung beide Apps auf Testgeräten prüfen, insbesondere SMS-Empfang, Berechtigungen, SOS-Kontakte und PIN-Wechsel.

Ausgangsversionen: SeniorenBeheerder `9e5411a0d3b578fe9b2d75055be2bf2bcc0a1334`; Senioren Launcher `eab0bf21a85f43b8b15ad8a7f99d90c64f6e4282`.
