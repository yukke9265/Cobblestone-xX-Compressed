package com.yukke9265.cobblestone_xx_compressed.screen;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFeCubeMenu;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;
import com.yukke9265.cobblestone_xx_compressed.util.NumberDisplayHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CobblestoneFeCubeScreen extends BaseScreen<CobblestoneFeCubeMenu> {
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
    private static final int AUTO_EXPORT_BUTTON_WIDTH = 94;
    private static final int AUTO_EXPORT_BUTTON_HEIGHT = 20;
    private static final int AUTO_INSERT_BUTTON_WIDTH = 94;
    private static final int AUTO_INSERT_BUTTON_HEIGHT = 20;
    private static final int MUTE_SOUND_BUTTON_WIDTH = 94;
    private static final int MUTE_SOUND_BUTTON_HEIGHT = 20;
    private static final int SIDE_BUTTON_X_OFFSET = 4;

    private static final int FE_LABEL_X = 4;
    private static final int FE_LABEL_Y = 14;
    private static final int RATE_LABEL_X = FE_LABEL_X;
    private static final int IMPORT_RATE_LABEL_Y = FE_LABEL_Y + 8;
    private static final int EXPORT_RATE_LABEL_Y = FE_LABEL_Y + 16;

    private static final int INDICATOR_BORDER_COLOR = 0xFF404040;
    private static final int INDICATOR_BACKGROUND_COLOR = 0xFF101010;
    private static final int FE_INDICATOR_FILL_COLOR = 0xFF3BCB5A;

    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_fe_generator.png");

    private final Button[] automationButtons = new Button[AUTOMATION_SIDES.length];
    private Button autoExportButton;
    private Button autoInsertButton;
    private Button muteSoundButton;
    private final int imageWidth = 176;
    private final int imageHeight = 166;
    private final int titleLabelX = 8;
    private final int titleLabelY = 6;
    private final int inventoryLabelX = 8;
    private final int inventoryLabelY = 72;

    public CobblestoneFeCubeScreen(CobblestoneFeCubeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.autoExportButton = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> this.onAutoExportButtonPressed())
                .bounds(
                    this.leftPos + this.imageWidth + SIDE_BUTTON_X_OFFSET,
                    this.topPos + this.imageHeight - AUTO_EXPORT_BUTTON_HEIGHT,
                    AUTO_EXPORT_BUTTON_WIDTH,
                    AUTO_EXPORT_BUTTON_HEIGHT
                )
                .build()
        );

        this.autoInsertButton = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> this.onAutoInsertButtonPressed())
                .bounds(
                    this.leftPos + this.imageWidth + SIDE_BUTTON_X_OFFSET,
                    this.topPos + this.imageHeight - AUTO_EXPORT_BUTTON_HEIGHT - AUTO_INSERT_BUTTON_HEIGHT - 2,
                    AUTO_INSERT_BUTTON_WIDTH,
                    AUTO_INSERT_BUTTON_HEIGHT
                )
                .build()
        );

        this.muteSoundButton = this.addMuteSoundButton(
            this.leftPos + this.imageWidth + SIDE_BUTTON_X_OFFSET,
            this.topPos + this.imageHeight - AUTO_EXPORT_BUTTON_HEIGHT - AUTO_INSERT_BUTTON_HEIGHT - 4 - MUTE_SOUND_BUTTON_HEIGHT - 2,
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
                button -> this.onAutomationButtonPressed(this.menu.getAutomationButtonId(side))
            ).bounds(x, y, AUTOMATION_BUTTON_WIDTH, AUTOMATION_BUTTON_HEIGHT).build()
        );
    }

    private void onAutomationButtonPressed(int buttonId) {
        this.sendMenuButtonClick(buttonId);
    }

    private void onAutoExportButtonPressed() {
        this.sendMenuButtonClick(this.menu.getAutoExportButtonId());
    }

    private void onAutoInsertButtonPressed() {
        this.sendMenuButtonClick(this.menu.getAutoInsertButtonId());
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

    private void refreshAutoInsertButton() {
        if (this.autoInsertButton != null) {
            this.autoInsertButton.setMessage(this.createCheckboxLabel(this.menu.isAutoInsertEnabled(), "gui.cobblestonexxcompressed.auto_insert"));
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
        this.refreshAutoInsertButton();
        this.refreshMuteSoundButton(this.muteSoundButton);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        this.renderBackgroundTexture(guiGraphics, BACKGROUND_TEXTURE, x, y, this.imageWidth, this.imageHeight);
        this.renderNormalSlotPart(
            guiGraphics,
            x + MachineGuiLayouts.FeCube.CHARGE_SLOT_X,
            y + MachineGuiLayouts.FeCube.CHARGE_SLOT_Y
        );
        this.renderHorizontalBar(
            guiGraphics,
            x + MachineGuiLayouts.FeCube.BAR_X,
            y + MachineGuiLayouts.FeCube.FE_BAR_Y,
            MachineGuiLayouts.FeCube.BAR_WIDTH,
            MachineGuiLayouts.FeCube.BAR_HEIGHT,
            this.menu.getStoredForgeEnergy(),
            this.menu.getMaxForgeEnergy(),
            FE_INDICATOR_FILL_COLOR
        );
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        Component feLabel = Component.literal("FE")
            .append(": ")
            .append(NumberDisplayHelper.formatFeRange(this.menu.getStoredForgeEnergy(), this.menu.getMaxForgeEnergy()));
        guiGraphics.drawString(this.font, feLabel, FE_LABEL_X, FE_LABEL_Y, 0x404040, false);

        Component importRateLabel = Component.translatable("gui.cobblestonexxcompressed.input_fe_rate")
            .append(": ")
            .append(NumberDisplayHelper.formatFePerTick(this.menu.getLastImportedForgeEnergy()));
        guiGraphics.drawString(this.font, importRateLabel, RATE_LABEL_X, IMPORT_RATE_LABEL_Y, 0x404040, false);

        Component exportRateLabel = Component.translatable("gui.cobblestonexxcompressed.output_fe_rate")
            .append(": ")
            .append(NumberDisplayHelper.formatFePerTick(this.menu.getLastExportedForgeEnergy()));
        guiGraphics.drawString(this.font, exportRateLabel, RATE_LABEL_X, EXPORT_RATE_LABEL_Y, 0x404040, false);

        int legendX = 0 - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        int legendY = AUTOMATION_PANEL_Y - AUTOMATION_LEGEND_LINE_HEIGHT;
        guiGraphics.drawString(this.font, Component.literal("Off").withStyle(AutomationMode.DISABLED.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Input").withStyle(AutomationMode.INPUT.createLabelComponent().getStyle()), legendX + AUTOMATION_LEGEND_SECOND_COLUMN_X, legendY + AUTOMATION_LEGEND_LINE_HEIGHT, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Output").withStyle(AutomationMode.OUTPUT.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 2, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("In/Out").withStyle(AutomationMode.IN_OUT.createLabelComponent().getStyle()), legendX + AUTOMATION_LEGEND_SECOND_COLUMN_X, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 2, 0x404040, false);
    }

    @Override
    protected void renderHoverLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.renderHorizontalBarHoverLabel(
            guiGraphics,
            mouseX,
            mouseY,
            MachineGuiLayouts.FeCube.BAR_X,
            MachineGuiLayouts.FeCube.FE_BAR_Y,
            MachineGuiLayouts.FeCube.BAR_WIDTH,
            MachineGuiLayouts.FeCube.BAR_HEIGHT,
            Component.literal("FE: ")
                .append(NumberDisplayHelper.formatFeRange(this.menu.getStoredForgeEnergy(), this.menu.getMaxForgeEnergy()))
        );
        for (AutomationSide side : AUTOMATION_SIDES) {
            this.renderButtonHoverLabel(guiGraphics, mouseX, mouseY, this.automationButtons[side.getIndex()], this.createAutomationHoverLabel(side));
        }
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

    private void renderHorizontalBar(
        GuiGraphics guiGraphics,
        int x,
        int y,
        int width,
        int height,
        long stored,
        long max,
        int fillColor
    ) {
        guiGraphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, INDICATOR_BORDER_COLOR);
        guiGraphics.fill(x, y, x + width, y + height, INDICATOR_BACKGROUND_COLOR);

        if (max <= 0L || stored <= 0L) {
            return;
        }

        int filledWidth = (int) Math.max(1L, Math.round(stored / (double) max * width));
        guiGraphics.fill(x, y, x + filledWidth, y + height, fillColor);
    }

    private void renderHorizontalBarHoverLabel(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        int barX,
        int barY,
        int barWidth,
        int barHeight,
        Component tooltip
    ) {
        if (!this.isHovering(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
            return;
        }

        this.renderHoverLabel(guiGraphics, mouseX, mouseY, tooltip);
    }
}
