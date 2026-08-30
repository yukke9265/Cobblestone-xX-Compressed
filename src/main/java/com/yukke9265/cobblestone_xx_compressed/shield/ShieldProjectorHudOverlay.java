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
 * 範囲内にいるとき、ホットバー左へ現在シールドと生成量を表示します。
 */
@EventBusSubscriber(modid = CobblestonexXCompressed.MODID, value = Dist.CLIENT)
public final class ShieldProjectorHudOverlay {
    private static final long DISPLAY_MAX_AGE_MILLIS = 1500L;
    private static final int TEXT_COLOR = 0xA0E0FFFF;
    private static final int BACKGROUND_COLOR = 0x80101010;
    private static final int LINE_SPACING = 1;
    private static final int BAR_HEIGHT = 5;
    private static final int BAR_WIDTH = 80;
    private static final int BAR_GAP = 2;
    // ホットバー左なので下端まで下げてよく、画面端から少しだけ空けます。
    private static final int BOTTOM_MARGIN = 4;
    // アクアマリン tier の代表色です。溶融圧縮丸石の tint と同じ値に揃えています。
    private static final int BAR_FILL_COLOR = 0xFF66D6C3;
    private static final int BAR_HIGHLIGHT_COLOR = 0xFFA8F0E4;
    private static final int BAR_BACKGROUND_COLOR = 0xFF102824;
    private static final int BAR_BORDER_COLOR = 0xFF2A6B62;

    private ShieldProjectorHudOverlay() {
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
            VanillaGuiLayers.HOTBAR,
            ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_shield_projector_hud"),
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
        long generationRate = ShieldProjectorClientState.getShieldGenerationRate();
        Component shieldLabel = Component.translatable("gui.cobblestonexxcompressed.shield")
            .append(": ")
            .append(NumberDisplayHelper.formatCpRange(storedShield, maxShield));
        Component generationLabel = Component.translatable(
            "gui.cobblestonexxcompressed.shield_generation",
            NumberDisplayHelper.format(generationRate)
        );

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        int lineHeight = minecraft.font.lineHeight;
        int textWidth = Math.max(minecraft.font.width(shieldLabel), minecraft.font.width(generationLabel));
        int contentWidth = Math.max(textWidth, BAR_WIDTH);
        int textHeight = lineHeight * 2 + LINE_SPACING;
        int totalHeight = textHeight + BAR_GAP + BAR_HEIGHT;
        // ホットバー左端の少し外側に寄せ、下端から少し空けて置きます。
        int x = screenWidth / 2 - 91 - contentWidth - 8;
        int y = screenHeight - totalHeight - 2 - BOTTOM_MARGIN;

        if (x < 2) {
            x = 2;
        }

        guiGraphics.fill(x - 2, y - 2, x + contentWidth + 2, y + totalHeight + 2, BACKGROUND_COLOR);
        guiGraphics.drawString(minecraft.font, shieldLabel, x, y, TEXT_COLOR, true);
        guiGraphics.drawString(
            minecraft.font,
            generationLabel,
            x,
            y + lineHeight + LINE_SPACING,
            TEXT_COLOR,
            true
        );
        renderShieldBar(
            guiGraphics,
            x + contentWidth - BAR_WIDTH,
            y + textHeight + BAR_GAP,
            storedShield,
            maxShield
        );
    }

    private static void renderShieldBar(
        GuiGraphics guiGraphics,
        int x,
        int y,
        long storedShield,
        long maxShield
    ) {
        guiGraphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, BAR_BORDER_COLOR);
        guiGraphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, BAR_BACKGROUND_COLOR);

        int filledWidth = getFilledBarWidth(storedShield, maxShield, BAR_WIDTH);
        if (filledWidth <= 0) {
            return;
        }

        guiGraphics.fill(x, y, x + filledWidth, y + BAR_HEIGHT, BAR_FILL_COLOR);
        guiGraphics.fill(x, y, x + filledWidth, y + 1, BAR_HIGHLIGHT_COLOR);
    }

    private static int getFilledBarWidth(long storedShield, long maxShield, int barWidth) {
        if (storedShield <= 0L || maxShield <= 0L || barWidth <= 0) {
            return 0;
        }

        if (storedShield >= maxShield) {
            return barWidth;
        }

        int filledWidth = (int) (storedShield / (double) maxShield * barWidth);
        if (filledWidth <= 0) {
            return 1;
        }

        return filledWidth;
    }
}
