package com.yukke9265.cobblestone_xx_compressed.screen;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiClickableAreaDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMixerMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CobblestoneMixerScreen extends BaseScreen<CobblestoneMixerMenu> {
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
    private static final int AUTOMATION_LEGEND_SECOND_COLUMN_X = 30;
    private static final int JEI_CLICK_AREA_X = 80;
    private static final int JEI_CLICK_AREA_Y = 31;
    private static final int JEI_CLICK_AREA_WIDTH = 16;
    private static final int JEI_CLICK_AREA_HEIGHT = 16;
    private static final int POWER_BAR_X = 36;
    private static final int POWER_BAR_Y = 63;
    private static final int POWER_BAR_WIDTH = 124;
    private static final int POWER_BAR_HEIGHT = 8;
    private static final int POWER_BAR_BORDER_COLOR = 0xFF404040;
    private static final int POWER_BAR_BACKGROUND_COLOR = 0xFF111111;
    private static final int POWER_BAR_FILL_COLOR = 0xFFD6D6D6;
    private static final int START_BUTTON_WIDTH = 62;
    private static final int START_BUTTON_HEIGHT = 20;
    private static final int START_BUTTON_X_OFFSET = 4;
    private static final int AUTO_EXPORT_BUTTON_WIDTH = 94;
    private static final int AUTO_EXPORT_BUTTON_HEIGHT = 20;
    private static final int UPGRADE_SLOT_X = 176;
    private static final int ACCELERATION_SLOT_Y = 12;
    private static final int ENERGIZED_CUBE_SLOT_Y = 30;
    private static final Component ACCELERATION_TOOLTIP = Component.literal("acceleration_chip");
    private static final Component ENERGIZED_CUBE_TOOLTIP = Component.literal("energized_cube");

    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_mixer.png");

    private static final ResourceLocation PROGRESS_BAR_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_mixer_progress_bar.png");

    private final Button[] automationButtons = new Button[AUTOMATION_SIDES.length];
    private Button autoExportButton;
    private final int progressBarX = 80;
    private final int progressBarY = 31;
    private final int progressBarWidth = 16;
    private final int progressBarHeight = 16;

    public CobblestoneMixerScreen(CobblestoneMixerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
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

        guiGraphics.blit(BACKGROUND_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        this.renderExternalSlotFrame(guiGraphics, x + UPGRADE_SLOT_X, y + ACCELERATION_SLOT_Y);
        this.renderExternalSlotFrame(guiGraphics, x + UPGRADE_SLOT_X, y + ENERGIZED_CUBE_SLOT_Y);

        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        //if (maxProgress > 0) {
        //    int currentProgressBarWidth = (int) (progress / (float) maxProgress * 32.0F);
        //    guiGraphics.blit(PROGRESS_BAR_TEXTURE, x + 90, y + 17, 0, 0, currentProgressBarWidth, 16, 32, 16);
        //}

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

        int legendX = 0 - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        int legendY = AUTOMATION_PANEL_Y;
        guiGraphics.drawString(this.font, Component.literal("Off").withStyle(AutomationMode.DISABLED.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Input").withStyle(AutomationMode.INPUT.createLabelComponent().getStyle()), legendX + AUTOMATION_LEGEND_SECOND_COLUMN_X, legendY + AUTOMATION_LEGEND_LINE_HEIGHT, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Input1").withStyle(AutomationMode.INPUT_1.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 2, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Input2").withStyle(AutomationMode.INPUT_2.createLabelComponent().getStyle()), legendX + AUTOMATION_LEGEND_SECOND_COLUMN_X, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 2, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Output").withStyle(AutomationMode.OUTPUT.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 3, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("In/Out").withStyle(AutomationMode.IN_OUT.createLabelComponent().getStyle()), legendX + AUTOMATION_LEGEND_SECOND_COLUMN_X, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 3, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Cobble").withStyle(AutomationMode.COBBLESTONE_INPUT.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 4, 0x404040, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderExternalSlotTooltip(guiGraphics, mouseX, mouseY, UPGRADE_SLOT_X, ACCELERATION_SLOT_Y, ACCELERATION_TOOLTIP);
        this.renderExternalSlotTooltip(guiGraphics, mouseX, mouseY, UPGRADE_SLOT_X, ENERGIZED_CUBE_SLOT_Y, ENERGIZED_CUBE_TOOLTIP);
    }

    @Override
    public List<JeiClickableAreaDefinition> getJeiClickableAreaDefinitions() {
        return this.createSingleJeiClickableAreaDefinition(
            JEI_CLICK_AREA_X,
            JEI_CLICK_AREA_Y,
            JEI_CLICK_AREA_WIDTH,
            JEI_CLICK_AREA_HEIGHT,
            ModJeiIds.COBBLESTONE_MIXER
        );
    }
}