package dev.spawnerhl.client;

import dev.spawnerhl.SpawnerMod;
import dev.spawnerhl.config.ConfigManager;
import dev.spawnerhl.gui.MenuScreen;
import dev.spawnerhl.gui.Toasts;
import dev.spawnerhl.gui.UiTheme;
import dev.spawnerhl.spawner.SpawnerMap;
import dev.spawnerhl.spawner.SpawnerTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

/** Client entry point: config, keybind, spawner tracking and the HUD layer. */
public final class SpawnerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigManager.load();
        ModKeys.init();

        // Only per-tick work: checking whether the menu key was pressed.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ModKeys.OPEN.consumeClick()) {
                if (client.screen == null && client.player != null) {
                    client.setScreen(new MenuScreen());
                }
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.saveIfDirty());

        // Spawner detection: the server sends every block entity of a loaded chunk to the client.
        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> {
            if (blockEntity instanceof SpawnerBlockEntity) SpawnerTracker.add(blockEntity.getBlockPos(), level);
        });
        ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) -> {
            if (blockEntity instanceof SpawnerBlockEntity) SpawnerTracker.remove(blockEntity.getBlockPos());
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> SpawnerTracker.clear());

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(SpawnerMod.MOD_ID, "overlay"),
                (graphics, delta) -> renderOverlay(graphics));

        SpawnerMod.LOGGER.info("{} loaded", SpawnerMod.NAME);
    }

    private static void renderOverlay(GuiGraphicsExtractor g) {
        Minecraft mc = Minecraft.getInstance();
        SpawnerMap.render(g);
        if (Toasts.active() && !(mc.screen instanceof MenuScreen)) {
            UiTheme.refresh();
            Toasts.render(g, mc.font, mc.getWindow().getGuiScaledWidth() - 10, mc.getWindow().getGuiScaledHeight() - 36);
        }
    }
}
