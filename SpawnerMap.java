package dev.spawnerhl.spawner;

import dev.spawnerhl.gui.Colors;
import dev.spawnerhl.gui.Gfx;
import dev.spawnerhl.gui.MenuScreen;
import dev.spawnerhl.gui.Toasts;
import dev.spawnerhl.setting.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws a small north-up chunk map in a screen corner. Every chunk that contains a spawner is highlighted.
 * Only filled rectangles are used, so the cost is a handful of draw calls per frame.
 */
public final class SpawnerMap {
    private static final List<String> LINES = new ArrayList<>();
    private static long lastBuild = 0L;
    private static long lastToast = 0L;

    private SpawnerMap() {}

    public static void render(GuiGraphicsExtractor g) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        // Discovery notification (throttled so a freshly loaded area does not spam).
        long nowMs = System.currentTimeMillis();
        if (nowMs - lastToast > 1500) {
            int found = SpawnerTracker.takeFound();
            if (found > 0 && Settings.SP_ENABLED.get()) {
                lastToast = nowMs;
                Toasts.push(found == 1
                        ? "Spawner chunk found: " + SpawnerTracker.lastFoundX() + ", " + SpawnerTracker.lastFoundZ()
                        : found + " new spawner chunks found");
            }
        }

        if (!Settings.SP_ENABLED.get()) return;
        if (mc.screen != null && !(mc.screen instanceof MenuScreen)) return;
        Font font = mc.font;

        int radius = Settings.MAP_RADIUS.getI();
        int cell = Settings.MAP_CELL.getI();
        int n = radius * 2 + 1;
        int size = n * cell;
        int pad = 4;
        int headerH = font.lineHeight + 3;

        double px = player.getX();
        double pz = player.getZ();
        int pcx = ((int) Math.floor(px)) >> 4;
        int pcz = ((int) Math.floor(pz)) >> 4;

        if (nowMs - lastBuild > 250) {
            lastBuild = nowMs;
            rebuildLines(px, pz, pcx, pcz);
        }

        int infoLines = Settings.SP_INFO.get() || Settings.SP_COORDS.get() ? LINES.size() : 0;
        int w = size + pad * 2;
        int textW = 0;
        for (int i = 0; i < infoLines; i++) textW = Math.max(textW, font.width(LINES.get(i)));
        w = Math.max(w, textW + pad * 2);
        int h = pad + headerH + size + pad + infoLines * (font.lineHeight + 1) + (infoLines > 0 ? 2 : 0);

        float scale = Settings.MAP_SCALE.getF();
        int margin = Settings.MAP_MARGIN.getI();
        float sw = mc.getWindow().getGuiScaledWidth();
        float sh = mc.getWindow().getGuiScaledHeight();
        int pos = Settings.MAP_POSITION.index();
        float x = (pos == 1 || pos == 3) ? sw - margin - w * scale : margin;
        float y = (pos >= 2) ? sh - margin - h * scale : margin;

        Gfx.ga = Settings.MAP_OPACITY.getF();
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(scale, scale);

        if (Settings.MAP_BG.get()) {
            Gfx.round(g, 0, 0, w, h, Settings.MAP_ROUND.getI(),
                    Colors.alpha(Settings.MAP_COL_BG.get(), Settings.MAP_BG_OPACITY.getF()));
        }
        Gfx.text(g, font, "Spawner chunks", pad, pad, 0xFF000000 | Settings.MAP_COL_HL.get(), true);
        Gfx.textRight(g, font, "N", w - pad, pad, 0xFFB0B0B0, true);

        int ox = (w - size) / 2;
        int oy = pad + headerH;

        // grid
        if (Settings.MAP_GRID.get()) {
            int grid = Colors.alpha(Settings.MAP_COL_GRID.get(), 0.12f);
            for (int i = 0; i <= n; i++) {
                Gfx.fill(g, ox + i * cell, oy, 1, size, grid);
                Gfx.fill(g, ox, oy + i * cell, size, 1, grid);
            }
        }

        // highlighted chunks
        float pulse = Settings.MAP_PULSE.get() ? 0.72f + 0.28f * (float) Math.sin(System.currentTimeMillis() / 380.0) : 1f;
        int hl = Settings.MAP_COL_HL.get();
        int fillC = Colors.alpha(hl, 0.55f * pulse);
        int edgeC = Colors.alpha(hl, 1f);
        boolean outline = Settings.MAP_OUTLINE.get();
        for (long k : SpawnerTracker.chunkKeys()) {
            int dx = SpawnerTracker.keyX(k) - pcx;
            int dz = SpawnerTracker.keyZ(k) - pcz;
            if (dx < -radius || dx > radius || dz < -radius || dz > radius) continue;
            int cx = ox + (dx + radius) * cell;
            int cy = oy + (dz + radius) * cell;
            Gfx.fill(g, cx + 1, cy + 1, cell - 1, cell - 1, fillC);
            if (outline) Gfx.outline(g, cx + 1, cy + 1, cell - 1, cell - 1, edgeC);
        }

        // player marker: small square inside the centre cell, positioned by the exact offset in the chunk
        int mcx = ox + radius * cell;
        int mcy = oy + radius * cell;
        int markerSize = Math.max(3, cell / 2);
        int offX = Math.round((float) ((px - (pcx << 4)) / 16.0) * (cell - markerSize));
        int offZ = Math.round((float) ((pz - (pcz << 4)) / 16.0) * (cell - markerSize));
        int playerC = 0xFF000000 | Settings.MAP_COL_PLAYER.get();
        Gfx.fill(g, mcx + offX - 1, mcy + offZ - 1, markerSize + 2, markerSize + 2, 0xFF000000);
        Gfx.fill(g, mcx + offX, mcy + offZ, markerSize, markerSize, playerC);

        // info text
        int ty = oy + size + pad + 2;
        for (int i = 0; i < infoLines; i++) {
            Gfx.text(g, font, LINES.get(i), pad, ty, 0xFFE8E8E8, true);
            ty += font.lineHeight + 1;
        }

        g.pose().popMatrix();
        Gfx.ga = 1f;
    }

    private static void rebuildLines(double px, double pz, int pcx, int pcz) {
        LINES.clear();
        if (Settings.SP_INFO.get()) {
            LINES.add(SpawnerTracker.chunkCount() + " chunks, " + SpawnerTracker.spawnerCount() + " spawners");
            double best = Double.MAX_VALUE;
            long bestKey = 0L;
            boolean any = false;
            for (long k : SpawnerTracker.chunkKeys()) {
                double dx = (SpawnerTracker.keyX(k) << 4) + 8 - px;
                double dz = (SpawnerTracker.keyZ(k) << 4) + 8 - pz;
                double d = dx * dx + dz * dz;
                if (d < best) {
                    best = d;
                    bestKey = k;
                    any = true;
                }
            }
            if (any) {
                LINES.add("Nearest chunk: " + (SpawnerTracker.keyX(bestKey) - pcx) + ", "
                        + (SpawnerTracker.keyZ(bestKey) - pcz) + " (" + Math.round(Math.sqrt(best)) + " m)");
            } else {
                LINES.add("No spawner chunks found yet");
            }
        }
        if (Settings.SP_COORDS.get()) {
            long p = SpawnerTracker.nearestSpawner(px, pz);
            if (p != Long.MIN_VALUE) {
                LINES.add("Spawner: " + BlockPos.getX(p) + " " + BlockPos.getY(p) + " " + BlockPos.getZ(p));
            }
        }
    }
}
