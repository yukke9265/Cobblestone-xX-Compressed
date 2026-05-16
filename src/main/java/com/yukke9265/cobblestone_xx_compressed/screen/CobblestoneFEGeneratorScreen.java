package com.yukke9265.cobblestone_xx_compressed.screen;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFEGeneratorMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CobblestoneFEGeneratorScreen extends BaseScreen<CobblestoneFEGeneratorMenu> {
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
    private static final int START_BUTTON_WIDTH = 62;
    private static final int START_BUTTON_HEIGHT = 20;
    private static final int START_BUTTON_X_OFFSET = 4;
    private static final int AUTO_EXPORT_BUTTON_WIDTH = 94;
    private static final int AUTO_EXPORT_BUTTON_HEIGHT = 20;

    

    private static final int CP_INDICATOR_X = 56;
    private static final int CP_INDICATOR_Y = 13;
    private static final int CP_INDICATOR_WIDTH = 14;
    private static final int CP_INDICATOR_HEIGHT = 14;
    private static final int TRANSFER_INDICATOR_X = 76;
    private static final int TRANSFER_INDICATOR_Y = 13;
    private static final int TRANSFER_INDICATOR_WIDTH = 14;
    private static final int TRANSFER_INDICATOR_HEIGHT = 14;
    private static final int FE_INDICATOR_X = 98;
    private static final int FE_INDICATOR_Y = 11;
    private static final int FE_INDICATOR_WIDTH = 22;
    private static final int FE_INDICATOR_HEIGHT = 20;

    private static final int CP_labelX = 28;
    private static final int CP_labelY = 50;
    private static final int FE_labelX = CP_labelX;
    private static final int FE_labelY = CP_labelY + 10;

    private static final int INDICATOR_BORDER_COLOR = 0xFF404040;
    private static final int INDICATOR_BACKGROUND_COLOR = 0xFF101010;
    private static final int CP_INDICATOR_FILL_COLOR = 0xFFD6D6D6;
    private static final int FE_INDICATOR_FILL_COLOR = 0xFF3BCB5A;
    private static final int TRANSFER_INDICATOR_FILL_COLOR = 0xFF4F6CFF;

    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_fe_generator.png");

    private final Button[] automationButtons = new Button[AUTOMATION_SIDES.length];
    private Button autoExportButton;
    private final int imageWidth = 176;
    private final int imageHeight = 166;
    private final int titleLabelX = 8;
    private final int titleLabelY = 6;
    private final int inventoryLabelX = 8;
    private final int inventoryLabelY = 72;
    private final int rateLabelX = 124;
    private final int convertRateLabelY = 36;
    private final int exportRateLabelY = 48;

    public CobblestoneFEGeneratorScreen(CobblestoneFEGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
            Button.builder(Component.literal("start/stop"), button -> this.onStartButtonPressed())
                .bounds(
                    this.leftPos + this.imageWidth + START_BUTTON_X_OFFSET,
                    this.topPos + this.imageHeight - START_BUTTON_HEIGHT,
                    START_BUTTON_WIDTH,
                    START_BUTTON_HEIGHT
                )
                .build()
        );

        this.autoExportButton = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> this.onAutoExportButtonPressed())
                .bounds(
                    this.leftPos + this.imageWidth + START_BUTTON_X_OFFSET,
                    this.topPos + this.imageHeight - START_BUTTON_HEIGHT - AUTO_EXPORT_BUTTON_HEIGHT - 2,
                    AUTO_EXPORT_BUTTON_WIDTH,
                    AUTO_EXPORT_BUTTON_HEIGHT
                )
                .build()
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
        this.renderIndicator(guiGraphics, x + CP_INDICATOR_X, y + CP_INDICATOR_Y, CP_INDICATOR_WIDTH, CP_INDICATOR_HEIGHT,
            this.menu.getStoredCobblestonePower(), this.menu.getMaxCobblestonePower(), CP_INDICATOR_FILL_COLOR);
        this.renderIndicator(guiGraphics, x + FE_INDICATOR_X, y + FE_INDICATOR_Y, FE_INDICATOR_WIDTH, FE_INDICATOR_HEIGHT,
            this.menu.getStoredForgeEnergy(), this.menu.getMaxForgeEnergy(), FE_INDICATOR_FILL_COLOR);

        if (this.menu.getIsAvailable()) {
            guiGraphics.fill(
                x + TRANSFER_INDICATOR_X,
                y + TRANSFER_INDICATOR_Y,
                x + TRANSFER_INDICATOR_X + TRANSFER_INDICATOR_WIDTH,
                y + TRANSFER_INDICATOR_Y + TRANSFER_INDICATOR_HEIGHT,
                TRANSFER_INDICATOR_FILL_COLOR
            );
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        Component cpLabel = Component.translatable("gui.cobblestonexxcompressed.cobblestone_power")
            .append(": ")
            .append(String.valueOf(this.menu.getStoredCobblestonePower()))
            .append(" / ")
            .append(String.valueOf(this.menu.getMaxCobblestonePower()));
        guiGraphics.drawString(this.font, cpLabel, CP_labelX, CP_labelY, 0x404040, false);

        Component feLabel = Component.translatable("gui.cobblestonexxcompressed.rf_energy")
            .append(": ")
            .append(String.valueOf(this.menu.getStoredForgeEnergy()))
            .append(" / ")
            .append(String.valueOf(this.menu.getMaxForgeEnergy()));
        guiGraphics.drawString(this.font, feLabel, FE_labelX, FE_labelY, 0x404040, false);

        Component convertRateLabel = Component.translatable("gui.cobblestonexxcompressed.convert_fe_rate")
            .append(": ")
            .append(String.valueOf(this.menu.getLastConvertedForgeEnergy()))
            .append("/t");
        guiGraphics.drawString(this.font, convertRateLabel, this.rateLabelX, this.convertRateLabelY, 0x404040, false);

        Component exportRateLabel = Component.translatable("gui.cobblestonexxcompressed.output_fe_rate")
            .append(": ")
            .append(String.valueOf(this.menu.getLastExportedForgeEnergy()))
            .append("/t");
        guiGraphics.drawString(this.font, exportRateLabel, this.rateLabelX, this.exportRateLabelY, 0x404040, false);

        int legendX = 0 - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        int legendY = AUTOMATION_PANEL_Y;
        guiGraphics.drawString(this.font, Component.literal("Off").withStyle(AutomationMode.DISABLED.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Input").withStyle(AutomationMode.INPUT.createLabelComponent().getStyle()), legendX + AUTOMATION_LEGEND_SECOND_COLUMN_X, legendY + AUTOMATION_LEGEND_LINE_HEIGHT, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Output").withStyle(AutomationMode.OUTPUT.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 2, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("In/Out").withStyle(AutomationMode.IN_OUT.createLabelComponent().getStyle()), legendX + AUTOMATION_LEGEND_SECOND_COLUMN_X, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 2, 0x404040, false);
    }

    private void renderIndicator(GuiGraphics guiGraphics, int x, int y, int width, int height, long stored, long max, int fillColor) {
        guiGraphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, INDICATOR_BORDER_COLOR);
        guiGraphics.fill(x, y, x + width, y + height, INDICATOR_BACKGROUND_COLOR);

        if (max <= 0L || stored <= 0L) {
            return;
        }

        int filledHeight = (int) Math.max(1L, Math.round(stored / (double) max * height));
        guiGraphics.fill(x, y + height - filledHeight, x + width, y + height, fillColor);
    }
}