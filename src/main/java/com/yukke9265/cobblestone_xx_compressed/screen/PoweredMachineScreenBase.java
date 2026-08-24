package com.yukke9265.cobblestone_xx_compressed.screen;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiClickableAreaDefinition;
import com.yukke9265.cobblestone_xx_compressed.menu.BaseMenu;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;
import com.yukke9265.cobblestone_xx_compressed.util.NumberDisplayHelper;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/*
 * 方針:
 * PoweredMachineScreenBase は、電力機械 GUI で毎回繰り返していた見た目と操作の流れを固定します。
 * ここでは共通ボタン、進捗バー、CP バー、automation パネルを持ち、
 * 継承先にはテクスチャ、座標、追加ラベルなど「見た目の差分」だけを残します。
 */
public abstract class PoweredMachineScreenBase<T extends BaseMenu> extends BaseScreen<T> {
    private static final AutomationSide[] AUTOMATION_SIDES = new AutomationSide[] {
        AutomationSide.UP,
        AutomationSide.DOWN,
        AutomationSide.FRONT,
        AutomationSide.BACK,
        AutomationSide.LEFT,
        AutomationSide.RIGHT
    };
    private static final int AUTOMATION_PANEL_X_OFFSET = 4;
    private static final int AUTOMATION_PANEL_Y = 8;
    private static final int AUTOMATION_BUTTON_WIDTH = 56;
    private static final int AUTOMATION_BUTTON_HEIGHT = 16;
    private static final int AUTOMATION_BUTTON_SPACING = 18;
    private static final int AUTOMATION_LEGEND_LINE_HEIGHT = 10;
    private static final int AUTOMATION_LEGEND_SECOND_COLUMN_X = 36;
    private static final int POWER_BAR_BORDER_COLOR = 0xFF404040;
    private static final int POWER_BAR_BACKGROUND_COLOR = 0xFF111111;
    private static final int POWER_BAR_FILL_COLOR = 0xFFD6D6D6;
    private static final int START_BUTTON_WIDTH = 62;
    private static final int START_BUTTON_HEIGHT = 20;
    private static final int START_BUTTON_X_OFFSET = 4;
    private static final int AUTO_EXPORT_BUTTON_WIDTH = 94;
    private static final int AUTO_EXPORT_BUTTON_HEIGHT = 20;
    private static final int AUTO_INSERT_BUTTON_WIDTH = 94;
    private static final int AUTO_INSERT_BUTTON_HEIGHT = 20;
    private static final int MUTE_SOUND_BUTTON_WIDTH = 94;
    private static final int MUTE_SOUND_BUTTON_HEIGHT = 20;
    private static final Component ACCELERATION_TOOLTIP = Component.literal("acceleration_chip");
    private static final Component ENERGIZED_CUBE_TOOLTIP = Component.literal("energized_cube");
    private static final Component PARALLEL_TOOLTIP = Component.literal("parallel_chip");

    private final Button[] automationButtons = new Button[AUTOMATION_SIDES.length];
    private Button autoExportButton;
    private Button autoInsertButton;
    private Button muteSoundButton;

    protected PoweredMachineScreenBase(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = MachineGuiLayouts.STANDARD_IMAGE_WIDTH;
        this.imageHeight = MachineGuiLayouts.STANDARD_IMAGE_HEIGHT;
        this.titleLabelX = MachineGuiLayouts.STANDARD_TITLE_LABEL_X;
        this.titleLabelY = MachineGuiLayouts.STANDARD_TITLE_LABEL_Y;
        this.inventoryLabelX = MachineGuiLayouts.STANDARD_INVENTORY_LABEL_X;
        this.inventoryLabelY = MachineGuiLayouts.STANDARD_INVENTORY_LABEL_Y;
    }

    protected abstract ResourceLocation getBackgroundTexture();

    protected abstract ResourceLocation getProgressBarTexture();

    protected abstract int getMachineInputSlotX();

    protected abstract int getMachineInputSlotY();

    protected abstract int getMachineOutputSlotX();

    protected abstract int getMachineOutputSlotY();

    protected abstract int getPowerSlotX();

    protected abstract int getPowerSlotY();

    protected abstract int getProgressBarX();

    protected abstract int getProgressBarY();

    protected abstract int getProgressBarWidth();

    protected abstract int getProgressBarHeight();

    protected abstract int getPowerBarX();

    protected abstract int getPowerBarY();

    protected abstract int getPowerBarWidth();

    protected abstract int getPowerBarHeight();

    protected abstract int getProgressValue();

    protected abstract int getMaxProgressValue();

    protected abstract int getAutomationButtonId(AutomationSide side);

    protected abstract AutomationMode getAutomationMode(AutomationSide side);

    protected abstract int getAutoExportButtonId();

    protected abstract boolean isAutoExportEnabled();

    protected abstract int getAutoInsertButtonId();

    protected abstract boolean isAutoInsertEnabled();

    protected abstract ResourceLocation getJeiRecipeCategoryId();

    protected int getAccelerationSlotX() {
        return MachineGuiLayouts.UPGRADE_SLOT_X;
    }

    protected int getAccelerationSlotY() {
        return MachineGuiLayouts.ACCELERATION_SLOT_Y;
    }

    protected int getEnergizedCubeSlotX() {
        return MachineGuiLayouts.UPGRADE_SLOT_X;
    }

    protected int getEnergizedCubeSlotY() {
        return MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y;
    }

    protected int getParallelSlotX() {
        return MachineGuiLayouts.UPGRADE_SLOT_X;
    }

    protected int getParallelSlotY() {
        return MachineGuiLayouts.PARALLEL_SLOT_Y;
    }

    protected int getJeiClickAreaX() {
        return this.getProgressBarX();
    }

    protected int getJeiClickAreaY() {
        return this.getProgressBarY();
    }

    protected int getJeiClickAreaWidth() {
        return 16;
    }

    protected int getJeiClickAreaHeight() {
        return 16;
    }

    protected void renderAdditionalLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    protected void renderAdditionalHoverLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
            Button.builder(
                Component.literal("start/stop"), button -> this.onStartButtonPressed()
            ).bounds(
                this.leftPos + this.imageWidth + START_BUTTON_X_OFFSET,
                this.topPos + this.imageHeight - START_BUTTON_HEIGHT,
                START_BUTTON_WIDTH,
                START_BUTTON_HEIGHT
            ).build()
        );

        this.autoExportButton = this.addRenderableWidget(
            Button.builder(
                Component.empty(), button -> this.onAutoExportButtonPressed()
            ).bounds(
                this.leftPos + this.imageWidth + START_BUTTON_X_OFFSET,
                this.topPos + this.imageHeight - START_BUTTON_HEIGHT - AUTO_EXPORT_BUTTON_HEIGHT - 2,
                AUTO_EXPORT_BUTTON_WIDTH,
                AUTO_EXPORT_BUTTON_HEIGHT
            ).build()
        );

        this.autoInsertButton = this.addRenderableWidget(
            Button.builder(
                Component.empty(), button -> this.onAutoInsertButtonPressed()
            ).bounds(
                this.leftPos + this.imageWidth + START_BUTTON_X_OFFSET,
                this.topPos + this.imageHeight - START_BUTTON_HEIGHT - AUTO_EXPORT_BUTTON_HEIGHT - AUTO_INSERT_BUTTON_HEIGHT - 4,
                AUTO_INSERT_BUTTON_WIDTH,
                AUTO_INSERT_BUTTON_HEIGHT
            ).build()
        );

        this.muteSoundButton = this.addMuteSoundButton(
            this.leftPos + this.imageWidth + START_BUTTON_X_OFFSET,
            this.topPos + this.imageHeight - START_BUTTON_HEIGHT - AUTO_EXPORT_BUTTON_HEIGHT - AUTO_INSERT_BUTTON_HEIGHT - 4 - MUTE_SOUND_BUTTON_HEIGHT - 2,
            MUTE_SOUND_BUTTON_WIDTH,
            MUTE_SOUND_BUTTON_HEIGHT
        );

        int automationPanelX = this.leftPos - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        for (int index = 0; index < AUTOMATION_SIDES.length; index++) {
            AutomationSide side = AUTOMATION_SIDES[index];
            int y = this.topPos + AUTOMATION_PANEL_Y + AUTOMATION_LEGEND_LINE_HEIGHT * 3 + 6 + index * AUTOMATION_BUTTON_SPACING;
            this.addAutomationButton(side, automationPanelX, y);
        }

        this.refreshAutomationButtons();
        this.refreshAutoExportButton();
        this.refreshAutoInsertButton();
        this.refreshMuteSoundButton(this.muteSoundButton);
    }

    private void addAutomationButton(AutomationSide side, int x, int y) {
        int buttonIndex = side.getIndex();
        this.automationButtons[buttonIndex] = this.addRenderableWidget(
            Button.builder(
                Component.empty(),
                button -> this.onAutomationButtonPressed(this.getAutomationButtonId(side))
            ).bounds(x, y, AUTOMATION_BUTTON_WIDTH, AUTOMATION_BUTTON_HEIGHT).build()
        );
    }

    private void onStartButtonPressed() {
        this.sendMenuButtonClick(0);
    }

    private void onAutomationButtonPressed(int buttonId) {
        this.sendMenuButtonClick(buttonId);
    }

    private void onAutoExportButtonPressed() {
        this.sendMenuButtonClick(this.getAutoExportButtonId());
    }

    private void onAutoInsertButtonPressed() {
        this.sendMenuButtonClick(this.getAutoInsertButtonId());
    }

    private void refreshAutomationButtons() {
        for (AutomationSide side : AUTOMATION_SIDES) {
            int index = side.getIndex();
            Button button = this.automationButtons[index];
            if (button != null) {
                button.setMessage(this.createAutomationButtonLabel(side, this.getAutomationMode(side)));
            }
        }
    }

    private void refreshAutoExportButton() {
        if (this.autoExportButton != null) {
            this.autoExportButton.setMessage(this.createCheckboxLabel(this.isAutoExportEnabled(), "gui.cobblestonexxcompressed.auto_export"));
        }
    }

    private void refreshAutoInsertButton() {
        if (this.autoInsertButton != null) {
            this.autoInsertButton.setMessage(this.createCheckboxLabel(this.isAutoInsertEnabled(), "gui.cobblestonexxcompressed.auto_insert"));
        }

        this.refreshMuteSoundButton(this.muteSoundButton);
    }

    private Component createAutomationButtonLabel(AutomationSide side, AutomationMode mode) {
        return Component.translatable(this.getAutomationSideTranslationKey(side))
            .withStyle(mode.createLabelComponent().getStyle());
    }

    private Component createAutomationHoverLabel(AutomationSide side) {
        return Component.translatable(this.getAutomationSideTranslationKey(side))
            .append(Component.literal(": "))
            .append(this.getAutomationMode(side).createLabelComponent());
    }

    private String getAutomationSideTranslationKey(AutomationSide side) {
        return switch (side) {
            case DOWN -> "gui.cobblestonexxcompressed.automation.down";
            case UP -> "gui.cobblestonexxcompressed.automation.up";
            case FRONT -> "gui.cobblestonexxcompressed.automation.front";
            case BACK -> "gui.cobblestonexxcompressed.automation.back";
            case LEFT -> "gui.cobblestonexxcompressed.automation.right";
            case RIGHT -> "gui.cobblestonexxcompressed.automation.left";
        };
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.refreshAutomationButtons();
        this.refreshAutoExportButton();
        this.refreshAutoInsertButton();
        this.refreshMuteSoundButton(this.muteSoundButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        this.renderBackgroundTexture(guiGraphics, this.getBackgroundTexture(), x, y, this.imageWidth, this.imageHeight);
        this.renderCobblestoneSlotPart(guiGraphics, x + this.getPowerSlotX(), y + this.getPowerSlotY());
        this.renderNormalSlotPart(guiGraphics, x + this.getMachineInputSlotX(), y + this.getMachineInputSlotY());
        this.renderNormalSlotPart(guiGraphics, x + this.getMachineOutputSlotX(), y + this.getMachineOutputSlotY());
        this.renderNormalSlotPart(guiGraphics, x + this.getAccelerationSlotX(), y + this.getAccelerationSlotY());
        this.renderNormalSlotPart(guiGraphics, x + this.getEnergizedCubeSlotX(), y + this.getEnergizedCubeSlotY());
        this.renderNormalSlotPart(guiGraphics, x + this.getParallelSlotX(), y + this.getParallelSlotY());
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
        guiGraphics.fill(powerBarLeft - 1, powerBarTop - 1, powerBarLeft + this.getPowerBarWidth() + 1, powerBarTop + this.getPowerBarHeight() + 1, POWER_BAR_BORDER_COLOR);
        guiGraphics.fill(powerBarLeft, powerBarTop, powerBarLeft + this.getPowerBarWidth(), powerBarTop + this.getPowerBarHeight(), POWER_BAR_BACKGROUND_COLOR);

        long storedPower = this.menu.getStoredCobblestonePower();
        long maxStoredPower = this.menu.getMaxCobblestonePower();
        if (maxStoredPower > 0L) {
            int filledWidth = (int) (storedPower / (double) maxStoredPower * this.getPowerBarWidth());
            guiGraphics.fill(powerBarLeft, powerBarTop, powerBarLeft + filledWidth, powerBarTop + this.getPowerBarHeight(), POWER_BAR_FILL_COLOR);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        Component powerLabel = Component.translatable("gui.cobblestonexxcompressed.cobblestone_power")
            .append(": ")
            .append(NumberDisplayHelper.formatCpRange(this.menu.getStoredCobblestonePower(), this.menu.getMaxCobblestonePower()))
            .withStyle(AutomationMode.COBBLESTONE_INPUT.createLabelComponent().getStyle());
        guiGraphics.drawString(this.font, powerLabel, this.getPowerBarX(), this.getPowerBarY() - 10, 0x404040, false);

        this.renderAdditionalLabels(guiGraphics, mouseX, mouseY);

        int legendX = 0 - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        int legendY = AUTOMATION_PANEL_Y - AUTOMATION_LEGEND_LINE_HEIGHT;
        guiGraphics.drawString(this.font, Component.literal("Off").withStyle(AutomationMode.DISABLED.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Input").withStyle(AutomationMode.INPUT.createLabelComponent().getStyle()), legendX + AUTOMATION_LEGEND_SECOND_COLUMN_X, legendY + AUTOMATION_LEGEND_LINE_HEIGHT, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Output").withStyle(AutomationMode.OUTPUT.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 2, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("In/Out").withStyle(AutomationMode.IN_OUT.createLabelComponent().getStyle()), legendX + AUTOMATION_LEGEND_SECOND_COLUMN_X, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 2, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Cobble").withStyle(AutomationMode.COBBLESTONE_INPUT.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 3, 0x404040, false);
    }

    @Override
    protected void renderHoverLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.renderCobblestonePowerHoverLabel(guiGraphics, mouseX, mouseY, this.getPowerBarX(), this.getPowerBarY(), this.getPowerBarWidth(), this.getPowerBarHeight());
        for (AutomationSide side : AUTOMATION_SIDES) {
            this.renderButtonHoverLabel(guiGraphics, mouseX, mouseY, this.automationButtons[side.getIndex()], this.createAutomationHoverLabel(side));
        }
        this.renderExternalSlotHoverLabel(guiGraphics, mouseX, mouseY, this.getAccelerationSlotX(), this.getAccelerationSlotY(), ACCELERATION_TOOLTIP);
        this.renderExternalSlotHoverLabel(guiGraphics, mouseX, mouseY, this.getEnergizedCubeSlotX(), this.getEnergizedCubeSlotY(), ENERGIZED_CUBE_TOOLTIP);
        this.renderExternalSlotHoverLabel(guiGraphics, mouseX, mouseY, this.getParallelSlotX(), this.getParallelSlotY(), PARALLEL_TOOLTIP);
        this.renderAdditionalHoverLabels(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AutomationSide side : AUTOMATION_SIDES) {
            int index = side.getIndex();
            if (this.handleAutomationButtonRightClick(button, this.automationButtons[index], this.menu.getReverseAutomationButtonId(index))) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public List<JeiClickableAreaDefinition> getJeiClickableAreaDefinitions() {
        return this.createSingleJeiClickableAreaDefinition(
            this.getJeiClickAreaX(),
            this.getJeiClickAreaY(),
            this.getJeiClickAreaWidth(),
            this.getJeiClickAreaHeight(),
            this.getJeiRecipeCategoryId()
        );
    }
}