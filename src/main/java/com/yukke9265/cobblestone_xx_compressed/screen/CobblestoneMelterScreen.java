package com.yukke9265.cobblestone_xx_compressed.screen;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiClickableAreaDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMelterMenu;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class CobblestoneMelterScreen extends BaseScreen<CobblestoneMelterMenu> {
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
    private static final int AUTO_EXPORT_BUTTON_WIDTH = 94;
    private static final int AUTO_EXPORT_BUTTON_HEIGHT = 20;
    private static final int SIDE_BUTTON_WIDTH = 62;
    private static final int SIDE_BUTTON_HEIGHT = 20;
    private static final int SIDE_BUTTON_X_OFFSET = 4;
    private static final int JEI_CLICK_AREA_X = MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_X;
    private static final int JEI_CLICK_AREA_Y = MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_Y;
    private static final int JEI_CLICK_AREA_WIDTH = MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_WIDTH;
    private static final int JEI_CLICK_AREA_HEIGHT = MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_HEIGHT;
    private static final int POWER_BAR_BORDER_COLOR = 0xFF404040;
    private static final int POWER_BAR_BACKGROUND_COLOR = 0xFF111111;
    private static final int POWER_BAR_FILL_COLOR = 0xFFD6D6D6;
    private static final int FLUID_INDICATOR_BACKGROUND_COLOR = 0xFF101010;
    private static final int FLUID_INDICATOR_FILL_COLOR = 0xFF3B8BFF;
    private static final int FLUID_SLOT_X = MachineGuiLayouts.PoweredMachine.OUTPUT_SLOT_X;
    private static final int FLUID_SLOT_Y = MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y;
    private static final Component ACCELERATION_TOOLTIP = Component.literal("acceleration_chip");
    private static final Component ENERGIZED_CUBE_TOOLTIP = Component.literal("energized_cube");

    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_melter.png");

    private static final ResourceLocation PROGRESS_BAR_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_melter_progress_bar.png");

    private final Button[] itemAutomationButtons = new Button[AUTOMATION_SIDES.length];
    private final Button[] fluidAutomationButtons = new Button[AUTOMATION_SIDES.length];
    private Button autoExportButton;

    public CobblestoneMelterScreen(CobblestoneMelterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        this.imageWidth = MachineGuiLayouts.STANDARD_IMAGE_WIDTH;
        this.imageHeight = MachineGuiLayouts.STANDARD_IMAGE_HEIGHT;
        this.titleLabelX = MachineGuiLayouts.STANDARD_TITLE_LABEL_X;
        this.titleLabelY = MachineGuiLayouts.STANDARD_TITLE_LABEL_Y;
        this.inventoryLabelX = MachineGuiLayouts.STANDARD_INVENTORY_LABEL_X;
        this.inventoryLabelY = MachineGuiLayouts.STANDARD_INVENTORY_LABEL_Y;
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.cobblestonexxcompressed.start_stop"), button -> this.sendMenuButtonClick(0))
                .bounds(
                    this.leftPos + this.imageWidth + SIDE_BUTTON_X_OFFSET,
                    this.topPos + this.imageHeight - SIDE_BUTTON_HEIGHT,
                    SIDE_BUTTON_WIDTH,
                    SIDE_BUTTON_HEIGHT
                )
                .build()
        );

        this.autoExportButton = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> this.sendMenuButtonClick(this.menu.getAutoExportButtonId()))
                .bounds(
                    this.leftPos + this.imageWidth + SIDE_BUTTON_X_OFFSET,
                    this.topPos + this.imageHeight - SIDE_BUTTON_HEIGHT - AUTO_EXPORT_BUTTON_HEIGHT - 2,
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
            this.itemAutomationButtons[side.getIndex()] = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> this.sendMenuButtonClick(this.menu.getItemAutomationButtonId(side)))
                    .bounds(itemPanelX, y, AUTOMATION_BUTTON_WIDTH, AUTOMATION_BUTTON_HEIGHT)
                    .build()
            );
            this.fluidAutomationButtons[side.getIndex()] = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> this.sendMenuButtonClick(this.menu.getFluidAutomationButtonId(side)))
                    .bounds(fluidPanelX, y, AUTOMATION_BUTTON_WIDTH, AUTOMATION_BUTTON_HEIGHT)
                    .build()
            );
        }

        this.refreshButtons();
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

    private Component createAutomationHoverLabel(Component categoryLabel, AutomationSide side, AutomationMode mode) {
        return categoryLabel.copy()
            .append(Component.literal(" / "))
            .append(Component.translatable(this.getAutomationSideTranslationKey(side)))
            .append(Component.literal(": "))
            .append(mode.createLabelComponent());
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
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.PoweredMachine.INPUT_SLOT_X, y + MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + FLUID_SLOT_X, y + FLUID_SLOT_Y);
        this.renderCobblestoneSlotPart(guiGraphics, x + MachineGuiLayouts.PoweredMachine.POWER_SLOT_X, y + MachineGuiLayouts.PoweredMachine.POWER_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.UPGRADE_SLOT_X, y + MachineGuiLayouts.ACCELERATION_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.UPGRADE_SLOT_X, y + MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y);
        this.renderProgressFramePart(guiGraphics, x + MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_X, y + MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_Y);

        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (maxProgress > 0) {
            int currentProgressBarWidth = (int) (progress / (float) maxProgress * MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_WIDTH);
            guiGraphics.blit(
                PROGRESS_BAR_TEXTURE,
                x + MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_X,
                y + MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_Y,
                0,
                0,
                currentProgressBarWidth,
                MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_HEIGHT,
                MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_WIDTH,
                MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_HEIGHT
            );
        }

        int powerBarLeft = x + MachineGuiLayouts.PoweredMachine.POWER_BAR_X;
        int powerBarTop = y + MachineGuiLayouts.PoweredMachine.POWER_BAR_Y;
        guiGraphics.fill(powerBarLeft - 1, powerBarTop - 1, powerBarLeft + MachineGuiLayouts.PoweredMachine.POWER_BAR_WIDTH + 1, powerBarTop + MachineGuiLayouts.PoweredMachine.POWER_BAR_HEIGHT + 1, POWER_BAR_BORDER_COLOR);
        guiGraphics.fill(powerBarLeft, powerBarTop, powerBarLeft + MachineGuiLayouts.PoweredMachine.POWER_BAR_WIDTH, powerBarTop + MachineGuiLayouts.PoweredMachine.POWER_BAR_HEIGHT, POWER_BAR_BACKGROUND_COLOR);

        long storedPower = this.menu.getStoredCobblestonePower();
        long maxStoredPower = this.menu.getMaxCobblestonePower();
        if (maxStoredPower > 0L) {
            int filledWidth = (int) (storedPower / (double) maxStoredPower * MachineGuiLayouts.PoweredMachine.POWER_BAR_WIDTH);
            guiGraphics.fill(powerBarLeft, powerBarTop, powerBarLeft + filledWidth, powerBarTop + MachineGuiLayouts.PoweredMachine.POWER_BAR_HEIGHT, POWER_BAR_FILL_COLOR);
        }

        int fluidLeft = x + FLUID_SLOT_X;
        int fluidTop = y + FLUID_SLOT_Y;
        guiGraphics.fill(fluidLeft, fluidTop, fluidLeft + 16, fluidTop + 16, FLUID_INDICATOR_BACKGROUND_COLOR);

        long storedFluidAmount = this.menu.getStoredFluidAmount();
        long maxFluidAmount = this.menu.getMaxFluidAmount();
        if (maxFluidAmount > 0L && storedFluidAmount > 0L) {
            int fillColor = this.getFluidIndicatorFillColor();
            int filledHeight = (int) Math.max(1L, Math.round(storedFluidAmount / (double) maxFluidAmount * 16.0D));
            guiGraphics.fill(
                fluidLeft,
                fluidTop + 16 - filledHeight,
                fluidLeft + 16,
                fluidTop + 16,
                fillColor
            );
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
        guiGraphics.drawString(this.font, powerLabel, MachineGuiLayouts.PoweredMachine.POWER_BAR_X, MachineGuiLayouts.PoweredMachine.POWER_BAR_Y - 10, 0x404040, false);

        int itemPanelX = 0 - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        int fluidPanelX = itemPanelX - AUTOMATION_BUTTON_WIDTH - 4;
        int headerY = AUTOMATION_PANEL_Y - 10;
        guiGraphics.drawString(this.font, Component.translatable("gui.cobblestonexxcompressed.item"), itemPanelX + 18, headerY, 0x404040, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.cobblestonexxcompressed.fluid"), fluidPanelX + 16, headerY, 0x404040, false);
    }

    @Override
    protected void renderHoverLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.renderCobblestonePowerHoverLabel(guiGraphics, mouseX, mouseY, MachineGuiLayouts.PoweredMachine.POWER_BAR_X, MachineGuiLayouts.PoweredMachine.POWER_BAR_Y, MachineGuiLayouts.PoweredMachine.POWER_BAR_WIDTH, MachineGuiLayouts.PoweredMachine.POWER_BAR_HEIGHT);
        for (AutomationSide side : AUTOMATION_SIDES) {
            int index = side.getIndex();
            this.renderButtonHoverLabel(
                guiGraphics,
                mouseX,
                mouseY,
                this.itemAutomationButtons[index],
                this.createAutomationHoverLabel(Component.translatable("gui.cobblestonexxcompressed.item"), side, this.menu.getItemAutomationMode(side))
            );
            this.renderButtonHoverLabel(
                guiGraphics,
                mouseX,
                mouseY,
                this.fluidAutomationButtons[index],
                this.createAutomationHoverLabel(Component.translatable("gui.cobblestonexxcompressed.fluid"), side, this.menu.getFluidAutomationMode(side))
            );
        }

        this.renderExternalSlotHoverLabel(guiGraphics, mouseX, mouseY, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ACCELERATION_SLOT_Y, ACCELERATION_TOOLTIP);
        this.renderExternalSlotHoverLabel(guiGraphics, mouseX, mouseY, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y, ENERGIZED_CUBE_TOOLTIP);

        if (this.isMouseOverFluidIndicator(mouseX, mouseY)) {
            this.renderFluidHoverLabel(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AutomationSide side : AUTOMATION_SIDES) {
            int index = side.getIndex();
            if (this.handleAutomationButtonRightClick(button, this.itemAutomationButtons[index], this.menu.getReverseAutomationButtonId(index))) {
                return true;
            }

            if (this.handleAutomationButtonRightClick(button, this.fluidAutomationButtons[index], this.menu.getReverseFluidAutomationButtonId(index))) {
                return true;
            }
        }

        if (this.handleFluidIndicatorClick(
            button,
            this.isMouseOverFluidIndicator((int) mouseX, (int) mouseY),
            this.menu.getFluidInteractionButtonId(),
            this.menu.getFluidInteractionShiftButtonId()
        )) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isMouseOverFluidIndicator(this.getLastMouseX(), this.getLastMouseY())
            && this.handleJeiFluidLookupKeyPress(this.menu.getDisplayedFluid(), keyCode, scanCode)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public List<JeiClickableAreaDefinition> getJeiClickableAreaDefinitions() {
        return this.createSingleJeiClickableAreaDefinition(
            JEI_CLICK_AREA_X,
            JEI_CLICK_AREA_Y,
            JEI_CLICK_AREA_WIDTH,
            JEI_CLICK_AREA_HEIGHT,
            ModJeiIds.COBBLESTONE_MELTER
        );
    }

    private boolean isMouseOverFluidIndicator(int mouseX, int mouseY) {
        int left = this.leftPos + FLUID_SLOT_X;
        int top = this.topPos + FLUID_SLOT_Y;
        return mouseX >= left && mouseX < left + MachineGuiLayouts.SLOT_SIZE && mouseY >= top && mouseY < top + MachineGuiLayouts.SLOT_SIZE;
    }

    private int getFluidIndicatorFillColor() {
        FluidStack displayedFluid = this.menu.getDisplayedFluid();
        if (displayedFluid.isEmpty()) {
            return FLUID_INDICATOR_FILL_COLOR;
        }

        int tintColor = IClientFluidTypeExtensions.of(displayedFluid.getFluid()).getTintColor(displayedFluid);
        return tintColor | 0xFF000000;
    }

    private void renderFluidHoverLabel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        FluidStack displayedFluid = this.menu.getDisplayedFluid();
        Component label = displayedFluid.isEmpty()
            ? Component.translatable("gui.cobblestonexxcompressed.empty")
            : displayedFluid.getHoverName();
        Component amount = Component.literal(" (" + this.menu.getStoredFluidAmount() + " / " + this.menu.getMaxFluidAmount() + " mB)");
        this.renderHoverLabel(guiGraphics, mouseX, mouseY, label.copy().append(amount));
    }
}