package com.yukke9265.cobblestone_xx_compressed.screen;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneTankMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

public class CobblestoneTankScreen extends BaseScreen<CobblestoneTankMenu> {
    private static final AutomationSide[] AUTOMATION_SIDES = new AutomationSide[] {
        AutomationSide.UP,
        AutomationSide.DOWN,
        AutomationSide.FRONT,
        AutomationSide.BACK,
        AutomationSide.LEFT,
        AutomationSide.RIGHT
    };

    private static final int AUTOMATION_PANEL_X_OFFSET = 4;
    private static final int AUTOMATION_BUTTON_WIDTH = 56;
    private static final int AUTOMATION_BUTTON_HEIGHT = 16;
    private static final int AUTOMATION_BUTTON_SPACING = 18;
    private static final int AUTOMATION_PANEL_Y = 20;

    private static final int FLUID_GAUGE_X = 80;
    private static final int FLUID_GAUGE_Y = 17;
    private static final int FLUID_GAUGE_WIDTH = 16;
    private static final int FLUID_GAUGE_HEIGHT = 52;
    private static final int INPUT_SLOT_X = 56;
    private static final int OUTPUT_SLOT_X = 116;
    private static final int MACHINE_SLOT_Y = 17;
    private static final int FLUID_GAUGE_BORDER_COLOR = 0xFF404040;
    private static final int FLUID_GAUGE_BACKGROUND_COLOR = 0xFF101010;
    private static final int FLUID_GAUGE_FILL_COLOR = 0xFF3B8BFF;

    private static final int AUTO_EXPORT_BUTTON_WIDTH = 94;
    private static final int AUTO_EXPORT_BUTTON_HEIGHT = 20;
    private static final int AUTO_EXPORT_BUTTON_X_OFFSET = 4;

    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_tank.png");

    private final Button[] itemAutomationButtons = new Button[AUTOMATION_SIDES.length];
    private final Button[] fluidAutomationButtons = new Button[AUTOMATION_SIDES.length];
    private Button autoExportButton;

    private final int imageWidth = 176;
    private final int imageHeight = 166;
    private final int titleLabelX = 8;
    private final int titleLabelY = 6;
    private final int inventoryLabelX = 8;
    private final int inventoryLabelY = 72;

    public CobblestoneTankScreen(CobblestoneTankMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.autoExportButton = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> this.sendMenuButtonClick(this.menu.getAutoExportButtonId()))
                .bounds(
                    this.leftPos + this.imageWidth + AUTO_EXPORT_BUTTON_X_OFFSET,
                    this.topPos + this.imageHeight - AUTO_EXPORT_BUTTON_HEIGHT,
                    AUTO_EXPORT_BUTTON_WIDTH,
                    AUTO_EXPORT_BUTTON_HEIGHT
                )
                .build()
        );

        int itemPanelX = this.leftPos - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        int fluidPanelX = itemPanelX - AUTOMATION_BUTTON_WIDTH - 4;
        for (int index = 0; index < AUTOMATION_SIDES.length; index++) {
            AutomationSide side = AUTOMATION_SIDES[index];
            int y = this.topPos + AUTOMATION_PANEL_Y + index * AUTOMATION_BUTTON_SPACING;
            this.addItemAutomationButton(side, itemPanelX, y);
            this.addFluidAutomationButton(side, fluidPanelX, y);
        }

        this.refreshButtons();
    }

    private void addItemAutomationButton(AutomationSide side, int x, int y) {
        this.itemAutomationButtons[side.getIndex()] = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> this.sendMenuButtonClick(this.menu.getItemAutomationButtonId(side)))
                .bounds(x, y, AUTOMATION_BUTTON_WIDTH, AUTOMATION_BUTTON_HEIGHT)
                .build()
        );
    }

    private void addFluidAutomationButton(AutomationSide side, int x, int y) {
        this.fluidAutomationButtons[side.getIndex()] = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> this.sendMenuButtonClick(this.menu.getFluidAutomationButtonId(side)))
                .bounds(x, y, AUTOMATION_BUTTON_WIDTH, AUTOMATION_BUTTON_HEIGHT)
                .build()
        );
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.refreshButtons();
    }

    private void refreshButtons() {
        for (AutomationSide side : AUTOMATION_SIDES) {
            int index = side.getIndex();

            Button itemButton = this.itemAutomationButtons[index];
            if (itemButton != null) {
                itemButton.setMessage(this.createAutomationButtonLabel(side, this.menu.getItemAutomationMode(side)));
            }

            Button fluidButton = this.fluidAutomationButtons[index];
            if (fluidButton != null) {
                fluidButton.setMessage(this.createAutomationButtonLabel(side, this.menu.getFluidAutomationMode(side)));
            }
        }

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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        this.renderBackgroundTexture(guiGraphics, BACKGROUND_TEXTURE, x, y, this.imageWidth, this.imageHeight);
        this.renderNormalSlotPart(guiGraphics, x + INPUT_SLOT_X, y + MACHINE_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + OUTPUT_SLOT_X, y + MACHINE_SLOT_Y);
        guiGraphics.fill(x + FLUID_GAUGE_X - 1, y + FLUID_GAUGE_Y - 1, x + FLUID_GAUGE_X + FLUID_GAUGE_WIDTH + 1, y + FLUID_GAUGE_Y + FLUID_GAUGE_HEIGHT + 1, FLUID_GAUGE_BORDER_COLOR);
        guiGraphics.fill(x + FLUID_GAUGE_X, y + FLUID_GAUGE_Y, x + FLUID_GAUGE_X + FLUID_GAUGE_WIDTH, y + FLUID_GAUGE_Y + FLUID_GAUGE_HEIGHT, FLUID_GAUGE_BACKGROUND_COLOR);

        long storedFluidAmount = this.menu.getStoredFluidAmount();
        long maxFluidAmount = this.menu.getMaxFluidAmount();
        if (maxFluidAmount > 0L && storedFluidAmount > 0L) {
            int filledHeight = (int) Math.max(1L, Math.round(storedFluidAmount / (double) maxFluidAmount * FLUID_GAUGE_HEIGHT));
            guiGraphics.fill(
                x + FLUID_GAUGE_X,
                y + FLUID_GAUGE_Y + FLUID_GAUGE_HEIGHT - filledHeight,
                x + FLUID_GAUGE_X + FLUID_GAUGE_WIDTH,
                y + FLUID_GAUGE_Y + FLUID_GAUGE_HEIGHT,
                FLUID_GAUGE_FILL_COLOR
            );
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        Component fluidAmountLabel = Component.translatable("gui.cobblestonexxcompressed.fluid_amount")
            .append(": ")
            .append(String.valueOf(this.menu.getStoredFluidAmount()))
            .append(" / ")
            .append(String.valueOf(this.menu.getMaxFluidAmount()))
            .append(" mB");
        guiGraphics.drawString(this.font, fluidAmountLabel, 28, 50, 0x404040, false);

        int itemPanelX = 0 - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        int fluidPanelX = itemPanelX - AUTOMATION_BUTTON_WIDTH - 4;
        int headerY = AUTOMATION_PANEL_Y - 10;
        guiGraphics.drawString(this.font, Component.translatable("gui.cobblestonexxcompressed.item"), itemPanelX + 18, headerY, 0x404040, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.cobblestonexxcompressed.fluid"), fluidPanelX + 16, headerY, 0x404040, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if (this.isHovering(FLUID_GAUGE_X, FLUID_GAUGE_Y, FLUID_GAUGE_WIDTH, FLUID_GAUGE_HEIGHT, mouseX, mouseY)) {
            FluidStack displayedFluid = this.menu.getDisplayedFluid();
            Component fluidName = displayedFluid.isEmpty()
                ? Component.translatable("gui.cobblestonexxcompressed.empty")
                : displayedFluid.getHoverName();
            Component fluidAmount = Component.literal(this.menu.getStoredFluidAmount() + " / " + this.menu.getMaxFluidAmount() + " mB");
            guiGraphics.renderTooltip(this.font, java.util.List.of(fluidName, fluidAmount), java.util.Optional.empty(), mouseX, mouseY);
        }
    }
}