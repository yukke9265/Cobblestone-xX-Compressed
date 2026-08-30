package com.yukke9265.cobblestone_xx_compressed.shield;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.util.NumberDisplayHelper;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * 範囲内にいるとき、ホットバー左へ現在シールドを表示します。
 */
@EventBusSubscriber(modid = CobblestonexXCompressed.MODID, value = Dist.CLIENT)
public final class ShieldProjectorHudOverlay {
    private static final long DISPLAY_MAX_AGE_MILLIS = 1500L;
    private static final int TEXT_COLOR = 0xA0E0FFFF;
    private static final int BACKGROUND_COLOR = 0x80101010;

    private ShieldProjectorHudOverlay() {
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
            VanillaGuiLayers.HOTBAR,
            ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "shield_projector_hud"),
            ShieldProjectorHudOverlay::render
        );
    }

    private static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ShieldProjectorClientState.hasActiveDisplay(DISPLAY_MAX_AGE_MILLIS)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        if (minecraft.screen != null) {
            return;
        }

        long storedShield = ShieldProjectorClientState.getStoredShield();
        long maxShield = ShieldProjectorClientState.getMaxShield();
        Component label = Component.translatable("gui.cobblestonexxcompressed.shield")
            .append(": ")
            .append(NumberDisplayHelper.formatCpRange(storedShield, maxShield));

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        int textWidth = minecraft.font.width(label);
        // ホットバー左端の少し外側に寄せます。
        int x = screenWidth / 2 - 91 - textWidth - 8;
        int y = screenHeight - 35;

        if (x < 2) {
            x = 2;
        }

        guiGraphics.fill(x - 2, y - 2, x + textWidth + 2, y + minecraft.font.lineHeight + 2, BACKGROUND_COLOR);
        guiGraphics.drawString(minecraft.font, label, x, y, TEXT_COLOR, true);
    }
}
