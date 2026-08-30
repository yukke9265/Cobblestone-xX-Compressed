package com.yukke9265.cobblestone_xx_compressed.screen;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.ShieldProjectorBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiClickableAreaDefinition;
import com.yukke9265.cobblestone_xx_compressed.menu.ShieldProjectorMenu;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;
import com.yukke9265.cobblestone_xx_compressed.util.NumberDisplayHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * シールドプロジェクター GUI。
 * 通常の CP / 進捗に加え、シールド値と生成速度を表示します。
 */
public class ShieldProjectorScreen extends PoweredMachineScreenBase<ShieldProjectorMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_crusher.png");

    private static final ResourceLocation PROGRESS_BAR_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_crusher_progress_bar.png");

    private static final int SHIELD_LABEL_X = 56;
    private static final int SHIELD_LABEL_Y = 38;
    private static final int SHIELD_RATE_LABEL_Y = SHIELD_LABEL_Y + 10;

    public ShieldProjectorScreen(ShieldProjectorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        this.renderBackgroundTexture(guiGraphics, this.getBackgroundTexture(), x, y, this.imageWidth, this.imageHeight);
        this.renderCobblestoneSlotPart(guiGraphics, x + this.getPowerSlotX(), y + this.getPowerSlotY());
        this.renderNormalSlotPart(guiGraphics, x + this.getAccelerationSlotX(), y + this.getAccelerationSlotY());
        this.renderNormalSlotPart(guiGraphics, x + this.getEnergizedCubeSlotX(), y + this.getEnergizedCubeSlotY());
        this.renderNormalSlotPart(guiGraphics, x + this.getParallelSlotX(), y + this.getParallelSlotY());

        for (int index = 0; index < 4; index++) {
            int slotX = ShieldProjectorBlockEntity.GuiSlots.CUSTOM_UPGRADE_START_X
                + index * ShieldProjectorBlockEntity.GuiSlots.CUSTOM_UPGRADE_SPACING;
            this.renderNormalSlotPart(
                guiGraphics,
                x + slotX,
                y + ShieldProjectorBlockEntity.GuiSlots.CUSTOM_UPGRADE_Y
            );
        }

        this.renderProgressFramePart(guiGraphics, x + this.getProgressBarX(), y + this.getProgressBarY());

        int progress = this.getProgressValue();
        int maxProgress = this.getMaxProgressValue();
        if (maxProgress > 0) {
            int currentProgressBarWidth = (int) (progress / (float) maxProgress * this.getProgressBarWidth());
            guiGraphics.blit(
                this.getProgressBarTexture(),
                x + this.getProgressBarX(),
                y + this.getProgressBarY(),
                0,
                0,
                currentProgressBarWidth,
                this.getProgressBarHeight(),
                this.getProgressBarWidth(),
                this.getProgressBarHeight()
            );
        }

        int powerBarLeft = x + this.getPowerBarX();
        int powerBarTop = y + this.getPowerBarY();
        guiGraphics.fill(powerBarLeft - 1, powerBarTop - 1, powerBarLeft + this.getPowerBarWidth() + 1, powerBarTop + this.getPowerBarHeight() + 1, 0xFF404040);
        guiGraphics.fill(powerBarLeft, powerBarTop, powerBarLeft + this.getPowerBarWidth(), powerBarTop + this.getPowerBarHeight(), 0xFF111111);

        long storedPower = this.menu.getStoredCobblestonePower();
        long maxStoredPower = this.menu.getMaxCobblestonePower();
        if (maxStoredPower > 0L) {
            int filledWidth = (int) (storedPower / (double) maxStoredPower * this.getPowerBarWidth());
            guiGraphics.fill(powerBarLeft, powerBarTop, powerBarLeft + filledWidth, powerBarTop + this.getPowerBarHeight(), 0xFFD6D6D6);
        }
    }

    @Override
    protected void renderAdditionalLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component shieldLabel = Component.translatable("gui.cobblestonexxcompressed.shield")
            .append(": ")
            .append(NumberDisplayHelper.formatCpRange(this.menu.getStoredShield(), this.menu.getMaxShield()));
        guiGraphics.drawString(this.font, shieldLabel, SHIELD_LABEL_X, SHIELD_LABEL_Y, 0x404040, false);

        Component rateLabel = Component.translatable("gui.cobblestonexxcompressed.shield_rate")
            .append(": ")
            .append(NumberDisplayHelper.format(this.menu.getShieldGenerationRate()))
            .append("/t");
        guiGraphics.drawString(this.font, rateLabel, SHIELD_LABEL_X, SHIELD_RATE_LABEL_Y, 0x404040, false);
    }

    @Override
    public List<JeiClickableAreaDefinition> getJeiClickableAreaDefinitions() {
        return List.of();
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
        return 48;
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
    protected int getAutoInsertButtonId() {
        return this.menu.getAutoInsertButtonId();
    }

    @Override
    protected boolean isAutoInsertEnabled() {
        return this.menu.isAutoInsertEnabled();
    }

    @Override
    protected ResourceLocation getJeiRecipeCategoryId() {
        return ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "shield_projector");
    }
}
