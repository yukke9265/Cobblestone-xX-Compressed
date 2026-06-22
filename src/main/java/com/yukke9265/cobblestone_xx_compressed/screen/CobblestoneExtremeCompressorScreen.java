package com.yukke9265.cobblestone_xx_compressed.screen;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneExtremeCompressorMenu;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/*
 * 方針:
 * このクラスでは Extreme Compressor の画面差分だけを持ち、共通の描画やボタン処理は PoweredMachineScreenBase に任せます。
 * ここにはテクスチャ、レイアウト座標、入力アイテム表示の追加ラベルだけを残します。
 */
public class CobblestoneExtremeCompressorScreen extends PoweredMachineScreenBase<CobblestoneExtremeCompressorMenu> {
    private static final int INPUT_LABEL_X = MachineGuiLayouts.ExtremeCompressor.INPUT_NAME_LABEL_X;
    private static final int INPUT_NAME_Y = MachineGuiLayouts.ExtremeCompressor.INPUT_NAME_LABEL_Y;
    private static final int INPUT_COUNT_Y = MachineGuiLayouts.ExtremeCompressor.INPUT_COUNT_LABEL_Y;

    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_extreme_compressor.png");

    private static final ResourceLocation PROGRESS_BAR_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_extreme_compressor_progress_bar.png");

    public CobblestoneExtremeCompressorScreen(CobblestoneExtremeCompressorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected int getMachineInputSlotX() {
        return MachineGuiLayouts.ExtremeCompressor.INPUT_SLOT_X;
    }

    @Override
    protected int getMachineInputSlotY() {
        return MachineGuiLayouts.ExtremeCompressor.MACHINE_SLOT_Y;
    }

    @Override
    protected int getMachineOutputSlotX() {
        return MachineGuiLayouts.ExtremeCompressor.OUTPUT_SLOT_X;
    }

    @Override
    protected int getMachineOutputSlotY() {
        return MachineGuiLayouts.ExtremeCompressor.MACHINE_SLOT_Y;
    }

    @Override
    protected int getPowerSlotX() {
        return MachineGuiLayouts.ExtremeCompressor.POWER_SLOT_X;
    }

    @Override
    protected int getPowerSlotY() {
        return MachineGuiLayouts.ExtremeCompressor.POWER_SLOT_Y;
    }

    @Override
    protected int getProgressBarX() {
        return MachineGuiLayouts.ExtremeCompressor.PROGRESS_BAR_X;
    }

    @Override
    protected int getProgressBarY() {
        return MachineGuiLayouts.ExtremeCompressor.PROGRESS_BAR_Y;
    }

    @Override
    protected int getProgressBarWidth() {
        return MachineGuiLayouts.ExtremeCompressor.PROGRESS_BAR_WIDTH;
    }

    @Override
    protected int getProgressBarHeight() {
        return MachineGuiLayouts.ExtremeCompressor.PROGRESS_BAR_HEIGHT;
    }

    @Override
    protected int getPowerBarX() {
        return MachineGuiLayouts.ExtremeCompressor.POWER_BAR_X;
    }

    @Override
    protected int getPowerBarY() {
        return MachineGuiLayouts.ExtremeCompressor.POWER_BAR_Y;
    }

    @Override
    protected int getPowerBarWidth() {
        return MachineGuiLayouts.ExtremeCompressor.POWER_BAR_WIDTH;
    }

    @Override
    protected int getPowerBarHeight() {
        return MachineGuiLayouts.ExtremeCompressor.POWER_BAR_HEIGHT;
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return BACKGROUND_TEXTURE;
    }

    @Override
    protected ResourceLocation getProgressBarTexture() {
        return PROGRESS_BAR_TEXTURE;
    }

    @Override
    protected int getProgressValue() {
        return this.menu.getProgress();
    }

    @Override
    protected int getMaxProgressValue() {
        return this.menu.getMaxProgress();
    }

    @Override
    protected int getAutomationButtonId(AutomationSide side) {
        return this.menu.getAutomationButtonId(side);
    }

    @Override
    protected AutomationMode getAutomationMode(AutomationSide side) {
        return this.menu.getAutomationMode(side);
    }

    @Override
    protected int getAutoExportButtonId() {
        return this.menu.getAutoExportButtonId();
    }

    @Override
    protected boolean isAutoExportEnabled() {
        return this.menu.isAutoExportEnabled();
    }

    @Override
    protected ResourceLocation getJeiRecipeCategoryId() {
        return ModJeiIds.COBBLESTONE_EXTREME_COMPRESSOR;
    }

    @Override
    protected void renderAdditionalLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component itemName = this.menu.getDisplayedInputItemName();
        if (!itemName.getString().isEmpty()) {
            Component trimmedName = Component.literal(this.font.plainSubstrByWidth(itemName.getString(), 176));
            guiGraphics.drawString(this.font, trimmedName, INPUT_LABEL_X, INPUT_NAME_Y, 0x404040, false);
        }

        int requiredItemCount = this.menu.getRequiredItemCount();
        if (requiredItemCount > 0) {
            String countText = this.menu.getStoredInputItemCount() + " / " + requiredItemCount;
            guiGraphics.drawString(this.font, countText, INPUT_LABEL_X, INPUT_COUNT_Y, 0x404040, false);
        }
    }
}