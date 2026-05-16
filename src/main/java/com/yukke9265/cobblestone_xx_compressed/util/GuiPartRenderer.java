package com.yukke9265.cobblestone_xx_compressed.util;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class GuiPartRenderer {
    public static final int NORMAL_SLOT_TEXTURE_SIZE = 18;
    public static final int COBBLESTONE_SLOT_TEXTURE_SIZE = 24;
    public static final int PROGRESS_FRAME_TEXTURE_SIZE = 18;
    public static final int NORMAL_SLOT_OFFSET = 1;
    public static final int COBBLESTONE_SLOT_OFFSET = 4;
    public static final int PROGRESS_FRAME_OFFSET = 1;

    public static final ResourceLocation NORMAL_SLOT_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/parts/gui_normalsize_slot.png");
    public static final ResourceLocation COBBLESTONE_SLOT_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/parts/gui_cobblestone_slot.png");
    public static final ResourceLocation PROGRESS_FRAME_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/parts/gui_progressbar_frame.png");

    private GuiPartRenderer() {
    }

    public static void renderBackground(GuiGraphics guiGraphics, ResourceLocation texture, int left, int top, int width, int height) {
        guiGraphics.blit(texture, left, top, 0, 0, width, height, width, height);
    }

    public static void renderNormalSlot(GuiGraphics guiGraphics, int left, int top) {
        guiGraphics.blit(
            NORMAL_SLOT_TEXTURE,
            left - NORMAL_SLOT_OFFSET,
            top - NORMAL_SLOT_OFFSET,
            0,
            0,
            NORMAL_SLOT_TEXTURE_SIZE,
            NORMAL_SLOT_TEXTURE_SIZE,
            NORMAL_SLOT_TEXTURE_SIZE,
            NORMAL_SLOT_TEXTURE_SIZE
        );
    }

    public static void renderCobblestoneSlot(GuiGraphics guiGraphics, int left, int top) {
        guiGraphics.blit(
            COBBLESTONE_SLOT_TEXTURE,
            left - COBBLESTONE_SLOT_OFFSET,
            top - COBBLESTONE_SLOT_OFFSET,
            0,
            0,
            COBBLESTONE_SLOT_TEXTURE_SIZE,
            COBBLESTONE_SLOT_TEXTURE_SIZE,
            COBBLESTONE_SLOT_TEXTURE_SIZE,
            COBBLESTONE_SLOT_TEXTURE_SIZE
        );
    }

    public static void renderProgressFrame(GuiGraphics guiGraphics, int left, int top) {
        guiGraphics.blit(
            PROGRESS_FRAME_TEXTURE,
            left - PROGRESS_FRAME_OFFSET,
            top - PROGRESS_FRAME_OFFSET,
            0,
            0,
            PROGRESS_FRAME_TEXTURE_SIZE,
            PROGRESS_FRAME_TEXTURE_SIZE,
            PROGRESS_FRAME_TEXTURE_SIZE,
            PROGRESS_FRAME_TEXTURE_SIZE
        );
    }
}