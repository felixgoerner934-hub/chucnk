package dev.spawnerhl.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Central definition of every setting. Add a line here and it shows up in the menu and config automatically. */
public final class Settings {
    private static final List<Setting> ALL = new ArrayList<>();
    private static final Map<String, Setting> BY_ID = new HashMap<>();

    private static <T extends Setting> T reg(T s) {
        ALL.add(s);
        BY_ID.put(s.id, s);
        return s;
    }

    private static BoolSetting bool(Category c, String g, String id, String n, String d, boolean def) {
        return reg(new BoolSetting(c, g, id, n, d, def));
    }

    private static NumberSetting num(Category c, String g, String id, String n, String d,
                                     double def, double min, double max, double step, String suffix) {
        return reg(new NumberSetting(c, g, id, n, d, def, min, max, step, suffix));
    }

    private static EnumSetting en(Category c, String g, String id, String n, String d, int def, String... o) {
        return reg(new EnumSetting(c, g, id, n, d, def, o));
    }

    private static ColorSetting col(Category c, String g, String id, String n, String d, int def) {
        return reg(new ColorSetting(c, g, id, n, d, def));
    }

    // ------------------------------------------------------------------ Spawners
    public static final BoolSetting SP_ENABLED = bool(Category.SPAWNERS, "Highlight", "spawner.enabled",
            "Highlight Spawner Chunks", "Show the chunk map with every chunk that contains a spawner.", true);
    public static final BoolSetting NOTIFICATIONS = bool(Category.SPAWNERS, "Highlight", "spawner.notify",
            "Found Notification", "Pop-up when a new spawner chunk is discovered.", true);
    public static final BoolSetting SP_INFO = bool(Category.SPAWNERS, "Info", "spawner.info",
            "Info Text", "Show chunk count and the nearest spawner chunk below the map.", true);
    public static final BoolSetting SP_COORDS = bool(Category.SPAWNERS, "Info", "spawner.coords",
            "Nearest Spawner Coordinates", "Show the block coordinates of the closest spawner.", false);

    // ------------------------------------------------------------------ Map
    public static final NumberSetting MAP_RADIUS = num(Category.MAP, "Layout", "map.radius",
            "Map Radius", "How many chunks around you are shown.", 6, 2, 16, 1, " chunks");
    public static final NumberSetting MAP_CELL = num(Category.MAP, "Layout", "map.cell",
            "Chunk Size", "Size of one chunk on the map.", 8, 4, 16, 1, " px");
    public static final EnumSetting MAP_POSITION = en(Category.MAP, "Layout", "map.position",
            "Position", "Screen corner of the map.", 1, "Top Left", "Top Right", "Bottom Left", "Bottom Right");
    public static final NumberSetting MAP_MARGIN = num(Category.MAP, "Layout", "map.margin",
            "Edge Margin", "Distance to the screen edge.", 6, 0, 30, 1, " px");
    public static final NumberSetting MAP_SCALE = num(Category.MAP, "Layout", "map.scale",
            "Map Scale", "Overall size of the map.", 1.0, 0.5, 2.0, 0.05, "x");

    public static final NumberSetting MAP_OPACITY = num(Category.MAP, "Style", "map.opacity",
            "Map Opacity", "Overall opacity.", 1.0, 0.2, 1.0, 0.05, "");
    public static final BoolSetting MAP_BG = bool(Category.MAP, "Style", "map.background",
            "Background", "Draw a translucent background.", true);
    public static final NumberSetting MAP_BG_OPACITY = num(Category.MAP, "Style", "map.bg_opacity",
            "Background Opacity", "Opacity of the background.", 0.55, 0.0, 1.0, 0.05, "");
    public static final BoolSetting MAP_GRID = bool(Category.MAP, "Style", "map.grid",
            "Grid Lines", "Draw chunk grid lines.", true);
    public static final BoolSetting MAP_OUTLINE = bool(Category.MAP, "Style", "map.outline",
            "Highlight Outline", "Bright border around spawner chunks.", true);
    public static final BoolSetting MAP_PULSE = bool(Category.MAP, "Style", "map.pulse",
            "Pulse Animation", "Spawner chunks gently pulse.", true);
    public static final NumberSetting MAP_ROUND = num(Category.MAP, "Style", "map.rounded",
            "Rounded Corners", "Corner radius of the background.", 3, 0, 10, 1, " px");

    public static final ColorSetting MAP_COL_HL = col(Category.MAP, "Colors", "map.color_highlight",
            "Highlight Color", "Colour of spawner chunks.", 0xFF3B5C);
    public static final ColorSetting MAP_COL_PLAYER = col(Category.MAP, "Colors", "map.color_player",
            "Player Color", "Colour of your own marker.", 0xFFFFFF);
    public static final ColorSetting MAP_COL_BG = col(Category.MAP, "Colors", "map.color_background",
            "Background Color", "Colour of the map background.", 0x000000);
    public static final ColorSetting MAP_COL_GRID = col(Category.MAP, "Colors", "map.color_grid",
            "Grid Color", "Colour of the grid lines.", 0xFFFFFF);

    // ------------------------------------------------------------------ Menu
    public static final NumberSetting GUI_SCALE = num(Category.MENU, "Layout", "gui.scale",
            "UI Scale", "Size of this menu.", 1.0, 0.75, 1.5, 0.05, "x");
    public static final NumberSetting GUI_OPACITY = num(Category.MENU, "Layout", "gui.opacity",
            "UI Opacity", "Opacity of the menu panel.", 0.95, 0.4, 1.0, 0.01, "");
    public static final NumberSetting GUI_BACKDROP = num(Category.MENU, "Layout", "gui.backdrop",
            "Background Dim", "How much the game is darkened behind the menu.", 0.40, 0.0, 0.8, 0.05, "");
    public static final NumberSetting GUI_RADIUS = num(Category.MENU, "Style", "gui.corner_radius",
            "Corner Radius", "Roundness of the menu.", 6, 0, 12, 1, " px");
    public static final NumberSetting GUI_SHADOW = num(Category.MENU, "Style", "gui.shadow",
            "Shadow Strength", "Drop shadow below the menu.", 0.6, 0.0, 1.0, 0.05, "");
    public static final BoolSetting MENU_SHADOW = bool(Category.MENU, "Style", "gui.shadow_enabled",
            "Menu Shadow", "Disable to save a few draw calls.", true);
    public static final BoolSetting GUI_GLOW = bool(Category.MENU, "Style", "gui.glow",
            "Accent Glow", "Soft accent glow and moon motif.", true);
    public static final BoolSetting GUI_DESC = bool(Category.MENU, "Style", "gui.descriptions",
            "Show Descriptions", "Show a description below each setting.", true);
    public static final BoolSetting GUI_COMPACT = bool(Category.MENU, "Style", "gui.compact",
            "Compact Rows", "Smaller rows (hides descriptions).", false);

    public static final BoolSetting ANIM_ENABLED = bool(Category.MENU, "Animations", "anim.enabled",
            "Animations", "Master switch for all animations.", true);
    public static final NumberSetting ANIM_SPEED = num(Category.MENU, "Animations", "anim.speed",
            "Animation Speed", "0 disables animations, higher is faster.", 1.0, 0.0, 3.0, 0.1, "x");
    public static final EnumSetting ANIM_OPEN = en(Category.MENU, "Animations", "anim.open_style",
            "Open Style", "How the menu appears.", 0, "Scale + Fade", "Slide + Fade", "Fade", "None");
    public static final BoolSetting ANIM_SCROLL = bool(Category.MENU, "Animations", "anim.smooth_scroll",
            "Smooth Scrolling", "Ease the scroll position.", true);
    public static final BoolSetting ANIM_HOVER = bool(Category.MENU, "Animations", "anim.hover",
            "Hover Animation", "Fade highlights on hover.", true);
    public static final BoolSetting ANIM_FLASH = bool(Category.MENU, "Animations", "anim.flash",
            "Setting Change Flash", "Briefly highlight a row when it changes.", true);

    public static final BoolSetting REDUCE_MOTION = bool(Category.MENU, "Accessibility", "access.reduce_motion",
            "Reduce Motion", "Disables every animation.", false);
    public static final BoolSetting HIGH_CONTRAST = bool(Category.MENU, "Accessibility", "access.high_contrast",
            "High Contrast", "Brighter secondary text.", false);
    public static final BoolSetting KEY_HINTS = bool(Category.MENU, "Accessibility", "access.keyboard_hints",
            "Keyboard Hints", "Show keyboard shortcuts at the bottom of the menu.", true);

    // ------------------------------------------------------------------ Menu colors
    public static final ColorSetting C_ACCENT = col(Category.COLORS, "Menu Colors", "colors.accent",
            "Accent Color", "Highlights, toggles and sliders.", 0xF0507A);
    public static final ColorSetting C_BG = col(Category.COLORS, "Menu Colors", "colors.background",
            "Background Color", "Main panel colour.", 0x0F0F14);
    public static final ColorSetting C_PANEL = col(Category.COLORS, "Menu Colors", "colors.panel",
            "Secondary Color", "Sidebar and input fields.", 0x181820);
    public static final ColorSetting C_TEXT = col(Category.COLORS, "Menu Colors", "colors.text",
            "Text Color", "Primary text.", 0xF2F2F6);
    public static final ColorSetting C_TEXT2 = col(Category.COLORS, "Menu Colors", "colors.text_secondary",
            "Secondary Text Color", "Descriptions and hints.", 0x8E8E9C);

    private Settings() {}

    public static List<Setting> all() { return Collections.unmodifiableList(ALL); }

    public static Setting byId(String id) { return BY_ID.get(id); }

    /** This mod has no presets, only themes. */
    public static boolean isPresetScoped(Setting s) { return false; }

    public static final String[] THEME_IDS = {
            "colors.accent", "colors.background", "colors.panel", "colors.text", "colors.text_secondary",
            "gui.opacity", "gui.corner_radius", "gui.shadow"
    };

    public static boolean isThemeScoped(Setting s) {
        for (String id : THEME_IDS) if (id.equals(s.id)) return true;
        return false;
    }
}
