# Spawner Highlight

Clientseitige Fabric-Mod für **Minecraft Java Edition 26.1.2**. Sie merkt sich jeden Spawner, den dein Client lädt,
und hebt **jeden Chunk, in dem ein Spawner liegt**, auf einer kleinen Chunk-Karte im HUD hervor.
Dazu gibt es ein Menü mit Anpassungsoptionen (Taste **Ü**).

> **Hinweis:** Dieses Projekt wurde ohne Internetzugang geschrieben und konnte dort **nicht kompiliert oder im Spiel getestet werden**.
> Es nutzt nur APIs, die in der Fabric-Dokumentation für 26.1.2 belegt sind. Falls der GitHub-Build einen Fehler meldet,
> kopiere die Fehlermeldung (siehe „Fehlersuche“).
>
> Auf Multiplayer-Servern gelten deren Regeln: Viele Server verbieten Mods, die Informationen über nicht sichtbare Dinge anzeigen. Nutze die Mod nur, wo es erlaubt ist.

## So funktioniert es

* Der Server schickt dem Client alle Block-Entities (also auch Spawner) der geladenen Chunks. Die Mod merkt sich die Position jedes Spawners.
* Die **Chunk-Karte** zeigt die Chunks rund um dich (Norden oben). Chunks mit Spawner leuchten (optional pulsierend, mit Rand).
  Dein eigener Standort ist ein kleines Quadrat.
* Optional: Infozeile („X chunks, Y spawners“, nächster Spawner-Chunk), Koordinaten des nächsten Spawners und eine Benachrichtigung, wenn ein neuer Spawner-Chunk gefunden wird.
* Es werden nur Chunks erkannt, die dein Client geladen hat (Renderdistanz des Servers/Spiels).

**Wichtig:** Die Hervorhebung ist eine 2D-Karte im HUD, **keine Boxen in der 3D-Welt**. Die Welt-Render-API hat sich in 26.1
grundlegend geändert (eigene Render-Pipelines); das ließ sich ohne Compiler nicht verlässlich bauen.

## Versionen

| Komponente | Version |
|---|---|
| Minecraft | 26.1.2 |
| Fabric Loader | 0.19.3 |
| Fabric API | 0.155.2+26.1.2 |
| Fabric Loom | 1.15.5 |
| Gradle | 9.4.1 |
| Java | 25 |

## Installieren

1. Fabric Loader für 26.1.2 installieren (https://fabricmc.net/use/).
2. **Fabric API** für 26.1.2 in den `mods`-Ordner legen.
3. `spawner-highlight-1.0.0.jar` ebenfalls in den `mods`-Ordner legen.

## Bedienung

* **Ü** öffnet/schließt das Menü (im Spiel, ohne offenen Bildschirm).
* Maus: klicken, Slider ziehen, Mausrad scrollt. Rechtsklick auf eine Zeile setzt sie zurück.
* Tastatur: `↑/↓` wählen, `←/→` ändern, `Enter` umschalten, `Entf` zurücksetzen, `Tab` nächste Seite, `Strg+F` Suche, `Esc` schließen.

### Keybind ändern

`Optionen → Steuerung → Tastenbelegung → Spawner Highlight → Open Visual Settings`

(Das „Ü“ ist die Taste links neben „+“; intern `GLFW_KEY_LEFT_BRACKET`.)

## Einstellungen

* **Spawners:** Karte an/aus, Benachrichtigung, Infotext, Koordinaten des nächsten Spawners.
* **Map:** Radius (2–16 Chunks), Chunk-Größe, Ecke, Abstand, Skalierung, Deckkraft, Hintergrund, Gitter, Rand, Pulsieren, Rundung,
  Farben (Highlight, Spieler, Hintergrund, Gitter).
* **Menu / Menu Colors / Themes:** UI-Größe, Deckkraft, Eckenradius, Schatten, Animationen (Geschwindigkeit, Öffnungsstil), Farben,
  eigene Themes speichern/laden/löschen (eingebaut: Dark, Midnight, Minimal, Glass, AMOLED, Light).

Config: `.minecraft/config/spawnerhl/config.json` (wird automatisch gespeichert).

## Bauen

### Variante A – GitHub (empfohlen)

1. Neues Repository anlegen und den **Inhalt** dieses Ordners hochladen (inklusive Ordner `.github`).
2. Reiter **Actions** → Workflow **Build** läuft automatisch.
3. Nach grünem Haken den Lauf öffnen → unten **Artifacts** → `spawner-highlight-jar` herunterladen. Darin liegt die `.jar`.

### Variante B – lokal

Benötigt JDK 25 und einmalig Gradle 9.4+:

```bash
gradle wrapper --gradle-version 9.4.1
./gradlew build
```

Ergebnis: `build/libs/spawner-highlight-1.0.0.jar` (die `-sources.jar` wird nicht gebraucht).

## Fehlersuche

* **Build-Fehler:** GitHub → Actions → Lauf → Schritt „Build“ → die ersten ~30 Zeilen der Fehlerausgabe kopieren.
* **Keine Spawner sichtbar:** Es werden nur geladene Chunks erkannt; auf manchen Servern sind Block-Entities weit entfernter Chunks nicht vorhanden.
* **Java-Fehler:** Es wird Java 25 benötigt.

## Struktur

```
src/main/java/dev/spawnerhl/
  client/    SpawnerClient (Einstieg, Events), ModKeys (Keybind)
  spawner/   SpawnerTracker (Spawner pro Chunk), SpawnerMap (HUD-Karte)
  setting/   Settings (alle Optionen), Setting-Klassen, Category
  config/    ConfigManager (JSON), LibraryService + BuiltIns (Themes)
  gui/       MenuScreen, Gfx, UiTheme, Colors, Toasts
  anim/      Anim, Motion
```

Neue Option: eine Zeile in `Settings.java` hinzufügen – Menü, Config und Zurücksetzen funktionieren automatisch.

MIT-Lizenz, siehe `LICENSE`.
