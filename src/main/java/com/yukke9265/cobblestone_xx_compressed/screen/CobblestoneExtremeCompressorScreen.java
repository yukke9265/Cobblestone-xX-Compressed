package com.yukke9265.cobblestone_xx_compressed.screen;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiClickableAreaDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneExtremeCompressorMenu;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CobblestoneExtremeCompressorScreen extends BaseScreen<CobblestoneExtremeCompressorMenu> {
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
    private static final int JEI_CLICK_AREA_X = MachineGuiLayouts.ExtremeCompressor.PROGRESS_BAR_X;
    private static final int JEI_CLICK_AREA_Y = MachineGuiLayouts.ExtremeCompressor.PROGRESS_BAR_Y;
    private static final int JEI_CLICK_AREA_WIDTH = 16;
    private static final int JEI_CLICK_AREA_HEIGHT = 16;
    private static final int POWER_BAR_X = MachineGuiLayouts.ExtremeCompressor.POWER_BAR_X;
    private static final int POWER_BAR_Y = MachineGuiLayouts.ExtremeCompressor.POWER_BAR_Y;
    private static final int POWER_BAR_WIDTH = MachineGuiLayouts.ExtremeCompressor.POWER_BAR_WIDTH;
    private static final int POWER_BAR_HEIGHT = MachineGuiLayouts.ExtremeCompressor.POWER_BAR_HEIGHT;
    private static final int POWER_BAR_BORDER_COLOR = 0xFF404040;
    private static final int POWER_BAR_BACKGROUND_COLOR = 0xFF111111;
    private static final int POWER_BAR_FILL_COLOR = 0xFFD6D6D6;
    private static final int START_BUTTON_WIDTH = 62;
    private static final int START_BUTTON_HEIGHT = 20;
    private static final int START_BUTTON_X_OFFSET = 4;
    private static final int AUTO_EXPORT_BUTTON_WIDTH = 94;
    private static final int AUTO_EXPORT_BUTTON_HEIGHT = 20;
    private static final int INPUT_LABEL_X = MachineGuiLayouts.ExtremeCompressor.INPUT_NAME_LABEL_X;
    private static final int INPUT_NAME_Y = MachineGuiLayouts.ExtremeCompressor.INPUT_NAME_LABEL_Y;
    private static final int INPUT_COUNT_Y = MachineGuiLayouts.ExtremeCompressor.INPUT_COUNT_LABEL_Y;
    private static final Component ACCELERATION_TOOLTIP = Component.literal("acceleration_chip");
    private static final Component ENERGIZED_CUBE_TOOLTIP = Component.literal("energized_cube");

    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_extreme_compressor.png");

    private static final ResourceLocation PROGRESS_BAR_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_extreme_compressor_progress_bar.png");

    private final Button[] automationButtons = new Button[AUTOMATION_SIDES.length];
    private Button autoExportButton;
    private final int progressBarX = MachineGuiLayouts.ExtremeCompressor.PROGRESS_BAR_X;
    private final int progressBarY = MachineGuiLayouts.ExtremeCompressor.PROGRESS_BAR_Y;
    private final int progressBarWidth = MachineGuiLayouts.ExtremeCompressor.PROGRESS_BAR_WIDTH;
    private final int progressBarHeight = MachineGuiLayouts.ExtremeCompressor.PROGRESS_BAR_HEIGHT;
    private final int imageWidth = MachineGuiLayouts.STANDARD_IMAGE_WIDTH;
    private final int imageHeight = MachineGuiLayouts.STANDARD_IMAGE_HEIGHT;
    private final int titleLabelX = MachineGuiLayouts.STANDARD_TITLE_LABEL_X;
    private final int titleLabelY = MachineGuiLayouts.STANDARD_TITLE_LABEL_Y;
    private final int inventoryLabelX = MachineGuiLayouts.STANDARD_INVENTORY_LABEL_X;
    private final int inventoryLabelY = MachineGuiLayouts.STANDARD_INVENTORY_LABEL_Y;

    public CobblestoneExtremeCompressorScreen(CobblestoneExtremeCompressorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
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

        int automationPanelX = this.leftPos - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        for (int index = 0; index < AUTOMATION_SIDES.length; index++) {
            AutomationSide side = AUTOMATION_SIDES[index];
            int y = this.topPos + AUTOMATION_PANEL_Y + AUTOMATION_LEGEND_LINE_HEIGHT * 3 + 6 + index * AUTOMATION_BUTTON_SPACING;
            this.addAutomationButton(side, automationPanelX, y);
        }

        this.refreshAutomationButtons();
        this.refreshAutoExportButton();
    }

    private void addAutomationButton(AutomationSide side, int x, int y) {
        int buttonIndex = side.getIndex();
        this.automationButtons[buttonIndex] = this.addRenderableWidget(
            Button.builder(
                Component.empty(),
                button -> this.onAutomationButtonPressed(this.menu.getAutomationButtonId(side))
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
        this.sendMenuButtonClick(this.menu.getAutoExportButtonId());
    }

    private void refreshAutomationButtons() {
        for (AutomationSide side : AUTOMATION_SIDES) {
            int index = side.getIndex();
            Button button = this.automationButtons[index];
            if (button != null) {
                button.setMessage(this.createAutomationButtonLabel(side, this.menu.getAutomationMode(side)));
            }
        }
    }

    private void refreshAutoExportButton() {
        if (this.autoExportButton != null) {
            this.autoExportButton.setMessage(this.createCheckboxLabel(this.menu.isAutoExportEnabled(), "gui.cobblestonexxcompressed.auto_export"));
        }
    }

    private Component createAutomationButtonLabel(AutomationSide side, AutomationMode mode) {
        return Component.translatable(this.getAutomationSideTranslationKey(side))
            .withStyle(mode.createLabelComponent().getStyle());
    }

    private Component createAutomationHoverLabel(AutomationSide side) {
        return Component.translatable(this.getAutomationSideTranslationKey(side))
            .append(Component.literal(": "))
            .append(this.menu.getAutomationMode(side).createLabelComponent());
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
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        this.renderBackgroundTexture(guiGraphics, BACKGROUND_TEXTURE, x, y, this.imageWidth, this.imageHeight);
        this.renderCobblestoneSlotPart(guiGraphics, x + MachineGuiLayouts.ExtremeCompressor.POWER_SLOT_X, y + MachineGuiLayouts.ExtremeCompressor.POWER_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.ExtremeCompressor.INPUT_SLOT_X, y + MachineGuiLayouts.ExtremeCompressor.MACHINE_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.ExtremeCompressor.OUTPUT_SLOT_X, y + MachineGuiLayouts.ExtremeCompressor.MACHINE_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.UPGRADE_SLOT_X, y + MachineGuiLayouts.ACCELERATION_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.UPGRADE_SLOT_X, y + MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y);
        this.renderProgressFramePart(guiGraphics, x + this.progressBarX, y + this.progressBarY);

        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();

        if (maxProgress > 0) {
            int currentProgressBarWidth = (int) (progress / (float) maxProgress * this.progressBarWidth);
            guiGraphics.blit(
                PROGRESS_BAR_TEXTURE,
                x + this.progressBarX,
                y + this.progressBarY,
                0,
                0,
                currentProgressBarWidth,
                this.progressBarHeight,
                this.progressBarWidth,
                this.progressBarHeight
            );
        }

        int powerBarLeft = x + POWER_BAR_X;
        int powerBarTop = y + POWER_BAR_Y;
        guiGraphics.fill(powerBarLeft - 1, powerBarTop - 1, powerBarLeft + POWER_BAR_WIDTH + 1, powerBarTop + POWER_BAR_HEIGHT + 1, POWER_BAR_BORDER_COLOR);
        guiGraphics.fill(powerBarLeft, powerBarTop, powerBarLeft + POWER_BAR_WIDTH, powerBarTop + POWER_BAR_HEIGHT, POWER_BAR_BACKGROUND_COLOR);

        long storedPower = this.menu.getStoredCobblestonePower();
        long maxStoredPower = this.menu.getMaxCobblestonePower();
        if (maxStoredPower > 0) {
            int filledWidth = (int) (storedPower / (double) maxStoredPower * POWER_BAR_WIDTH);
            guiGraphics.fill(powerBarLeft, powerBarTop, powerBarLeft + filledWidth, powerBarTop + POWER_BAR_HEIGHT, POWER_BAR_FILL_COLOR);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        Component powerLabel = Component.translatable("gui.cobblestonexxcompressed.cobblestone_power")
            .append(": ")
            .append(String.valueOf(this.menu.getStoredCobblestonePower()))
            .append(" / ")
            .append(String.valueOf(this.menu.getMaxCobblestonePower()))
            .withStyle(AutomationMode.COBBLESTONE_INPUT.createLabelComponent().getStyle());
        guiGraphics.drawString(this.font, powerLabel, POWER_BAR_X, POWER_BAR_Y - 10, 0x404040, false);

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
        this.renderCobblestonePowerHoverLabel(guiGraphics, mouseX, mouseY, POWER_BAR_X, POWER_BAR_Y, POWER_BAR_WIDTH, POWER_BAR_HEIGHT);
        for (AutomationSide side : AUTOMATION_SIDES) {
            this.renderButtonHoverLabel(guiGraphics, mouseX, mouseY, this.automationButtons[side.getIndex()], this.createAutomationHoverLabel(side));
        }
        this.renderExternalSlotHoverLabel(guiGraphics, mouseX, mouseY, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ACCELERATION_SLOT_Y, ACCELERATION_TOOLTIP);
        this.renderExternalSlotHoverLabel(guiGraphics, mouseX, mouseY, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y, ENERGIZED_CUBE_TOOLTIP);
    }

    @Override
    public List<JeiClickableAreaDefinition> getJeiClickableAreaDefinitions() {
        return this.createSingleJeiClickableAreaDefinition(
            JEI_CLICK_AREA_X,
            JEI_CLICK_AREA_Y,
            JEI_CLICK_AREA_WIDTH,
            JEI_CLICK_AREA_HEIGHT,
            ModJeiIds.COBBLESTONE_EXTREME_COMPRESSOR
        );
    }
}