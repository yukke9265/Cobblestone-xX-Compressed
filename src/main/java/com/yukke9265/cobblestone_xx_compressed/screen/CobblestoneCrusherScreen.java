package com.yukke9265.cobblestone_xx_compressed.screen;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCrusherMenu;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/*
 * 方針:
 * このクラスでは Crusher の画面差分だけを持ち、共通の描画やボタン処理は PoweredMachineScreenBase に任せます。
 * ここにはテクスチャ、レイアウト座標、Menu から読む値の結び付けだけを残します。
 */
public class CobblestoneCrusherScreen extends PoweredMachineScreenBase<CobblestoneCrusherMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_crusher.png");

    private static final ResourceLocation PROGRESS_BAR_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_crusher_progress_bar.png");

    public CobblestoneCrusherScreen(CobblestoneCrusherMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected int getMachineInputSlotX() {
        return MachineGuiLayouts.PoweredMachine.INPUT_SLOT_X;
    }

    @Override
    protected int getMachineInputSlotY() {
        return MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y;
    }

    @Override
    protected int getMachineOutputSlotX() {
        return MachineGuiLayouts.PoweredMachine.OUTPUT_SLOT_X;
    }

    @Override
    protected int getMachineOutputSlotY() {
        return MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y;
    }

    @Override
    protected int getPowerSlotX() {
        return MachineGuiLayouts.PoweredMachine.POWER_SLOT_X;
    }

    @Override
    protected int getPowerSlotY() {
        return MachineGuiLayouts.PoweredMachine.POWER_SLOT_Y;
    }

    @Override
    protected int getProgressBarX() {
        return MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_X;
    }

    @Override
    protected int getProgressBarY() {
        return MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_Y;
    }

    @Override
    protected int getProgressBarWidth() {
        return MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_WIDTH;
    }

    @Override
    protected int getProgressBarHeight() {
        return MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_HEIGHT;
    }

    @Override
    protected int getPowerBarX() {
        return MachineGuiLayouts.PoweredMachine.POWER_BAR_X;
    }

    @Override
    protected int getPowerBarY() {
        return MachineGuiLayouts.PoweredMachine.POWER_BAR_Y;
    }

    @Override
    protected int getPowerBarWidth() {
        return MachineGuiLayouts.PoweredMachine.POWER_BAR_WIDTH;
    }

    @Override
    protected int getPowerBarHeight() {
        return MachineGuiLayouts.PoweredMachine.POWER_BAR_HEIGHT;
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
        return ModJeiIds.COBBLESTONE_CRUSHER;
    }
}