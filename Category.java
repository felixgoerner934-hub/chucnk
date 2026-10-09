package dev.spawnerhl.setting;

/** Pages of the settings menu. {@code library} pages list saved themes instead of settings. */
public enum Category {
    SPAWNERS("Spawners", false),
    MAP("Map", false),
    MENU("Menu", false),
    COLORS("Menu Colors", false),
    THEMES("Themes", true);

    public final String label;
    public final boolean library;

    Category(String label, boolean library) {
        this.label = label;
        this.library = library;
    }
}
