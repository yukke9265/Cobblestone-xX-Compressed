package com.yukke9265.cobblestone_xx_compressed.screen;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiClickableAreaDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFluidMixerMenu;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class CobblestoneFluidMixerScreen extends BaseScreen<CobblestoneFluidMixerMenu> {
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
    private static final int JEI_CLICK_AREA_X = MachineGuiLayouts.FluidMixer.PROGRESS_BAR_X;
    private static final int JEI_CLICK_AREA_Y = MachineGuiLayouts.FluidMixer.PROGRESS_BAR_Y;
    private static final int JEI_CLICK_AREA_WIDTH = MachineGuiLayouts.FluidMixer.PROGRESS_BAR_WIDTH;
    private static final int JEI_CLICK_AREA_HEIGHT = MachineGuiLayouts.FluidMixer.PROGRESS_BAR_HEIGHT;
    private static final int POWER_BAR_BORDER_COLOR = 0xFF404040;
    private static final int POWER_BAR_BACKGROUND_COLOR = 0xFF111111;
    private static final int POWER_BAR_FILL_COLOR = 0xFFD6D6D6;
    private static final int FLUID_INDICATOR_BACKGROUND_COLOR = 0xFF101010;
    private static final int FLUID_INDICATOR_FILL_COLOR = 0xFF3B8BFF;
    private static final Component ACCELERATION_TOOLTIP = Component.literal("acceleration_chip");
    private static final Component ENERGIZED_CUBE_TOOLTIP = Component.literal("energized_cube");

    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_fluid_mixer.png");

    private static final ResourceLocation PROGRESS_BAR_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_fluid_mixer_progress_bar.png");

    private final Button[] itemAutomationButtons = new Button[AUTOMATION_SIDES.length];
    private final Button[] fluidAutomationButtons = new Button[AUTOMATION_SIDES.length];
    private Button autoExportButton;

    public CobblestoneFluidMixerScreen(CobblestoneFluidMixerMenu menu, Inventory inventory, Component title) {
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
        for (AutomationSide side : AUTOMATION_SIDES) {
            int y = this.topPos + AUTOMATION_PANEL_Y + side.getIndex() * AUTOMATION_BUTTON_SPACING;
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
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.FluidMixer.INPUT_FLUID_1_SLOT_X, y + MachineGuiLayouts.FluidMixer.INPUT_FLUID_1_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.FluidMixer.INPUT_FLUID_2_SLOT_X, y + MachineGuiLayouts.FluidMixer.INPUT_FLUID_2_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.FluidMixer.OUTPUT_FLUID_SLOT_X, y + MachineGuiLayouts.FluidMixer.OUTPUT_FLUID_SLOT_Y);
        this.renderCobblestoneSlotPart(guiGraphics, x + MachineGuiLayouts.FluidMixer.POWER_SLOT_X, y + MachineGuiLayouts.FluidMixer.POWER_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.UPGRADE_SLOT_X, y + MachineGuiLayouts.ACCELERATION_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + MachineGuiLayouts.UPGRADE_SLOT_X, y + MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y);
        this.renderProgressFramePart(guiGraphics, x + MachineGuiLayouts.FluidMixer.PROGRESS_BAR_X, y + MachineGuiLayouts.FluidMixer.PROGRESS_BAR_Y);

        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        if (maxProgress > 0) {
            int currentProgressBarWidth = (int) (progress / (float) maxProgress * MachineGuiLayouts.FluidMixer.PROGRESS_BAR_WIDTH);
            guiGraphics.blit(
                PROGRESS_BAR_TEXTURE,
                x + MachineGuiLayouts.FluidMixer.PROGRESS_BAR_X,
                y + MachineGuiLayouts.FluidMixer.PROGRESS_BAR_Y,
                0,
                0,
                currentProgressBarWidth,
                MachineGuiLayouts.FluidMixer.PROGRESS_BAR_HEIGHT,
                MachineGuiLayouts.FluidMixer.PROGRESS_BAR_WIDTH,
                MachineGuiLayouts.FluidMixer.PROGRESS_BAR_HEIGHT
            );
        }

        int powerBarLeft = x + MachineGuiLayouts.FluidMixer.POWER_BAR_X;
        int powerBarTop = y + MachineGuiLayouts.FluidMixer.POWER_BAR_Y;
        guiGraphics.fill(powerBarLeft - 1, powerBarTop - 1, powerBarLeft + MachineGuiLayouts.FluidMixer.POWER_BAR_WIDTH + 1, powerBarTop + MachineGuiLayouts.FluidMixer.POWER_BAR_HEIGHT + 1, POWER_BAR_BORDER_COLOR);
        guiGraphics.fill(powerBarLeft, powerBarTop, powerBarLeft + MachineGuiLayouts.FluidMixer.POWER_BAR_WIDTH, powerBarTop + MachineGuiLayouts.FluidMixer.POWER_BAR_HEIGHT, POWER_BAR_BACKGROUND_COLOR);

        long storedPower = this.menu.getStoredCobblestonePower();
        long maxStoredPower = this.menu.getMaxCobblestonePower();
        if (maxStoredPower > 0L) {
            int filledWidth = (int) (storedPower / (double) maxStoredPower * MachineGuiLayouts.FluidMixer.POWER_BAR_WIDTH);
            guiGraphics.fill(powerBarLeft, powerBarTop, powerBarLeft + filledWidth, powerBarTop + MachineGuiLayouts.FluidMixer.POWER_BAR_HEIGHT, POWER_BAR_FILL_COLOR);
        }

        this.renderFluidSlotFill(
            guiGraphics,
            x + MachineGuiLayouts.FluidMixer.INPUT_FLUID_1_FILL_X,
            y + MachineGuiLayouts.FluidMixer.INPUT_FLUID_1_FILL_Y,
            this.menu.getStoredInputFluid1Amount(),
            this.menu.getMaxInputFluid1Amount(),
            this.menu.getDisplayedInputFluid1()
        );
        this.renderFluidSlotFill(
            guiGraphics,
            x + MachineGuiLayouts.FluidMixer.INPUT_FLUID_2_FILL_X,
            y + MachineGuiLayouts.FluidMixer.INPUT_FLUID_2_FILL_Y,
            this.menu.getStoredInputFluid2Amount(),
            this.menu.getMaxInputFluid2Amount(),
            this.menu.getDisplayedInputFluid2()
        );
        this.renderFluidSlotFill(
            guiGraphics,
            x + MachineGuiLayouts.FluidMixer.OUTPUT_FLUID_FILL_X,
            y + MachineGuiLayouts.FluidMixer.OUTPUT_FLUID_FILL_Y,
            this.menu.getStoredOutputFluidAmount(),
            this.menu.getMaxOutputFluidAmount(),
            this.menu.getDisplayedOutputFluid()
        );
    }

    private void renderFluidSlotFill(GuiGraphics guiGraphics, int left, int top, long storedAmount, long maxAmount, FluidStack displayedFluid) {
        guiGraphics.fill(
            left,
            top,
            left + MachineGuiLayouts.FluidMixer.FLUID_FILL_WIDTH,
            top + MachineGuiLayouts.FluidMixer.FLUID_FILL_HEIGHT,
            FLUID_INDICATOR_BACKGROUND_COLOR
        );

        if (maxAmount <= 0L || storedAmount <= 0L) {
            return;
        }

        int fillColor = this.getFluidIndicatorFillColor(displayedFluid);
        int filledHeight = (int) Math.max(1L, Math.round(storedAmount / (double) maxAmount * MachineGuiLayouts.FluidMixer.FLUID_FILL_HEIGHT));
        guiGraphics.fill(
            left,
            top + MachineGuiLayouts.FluidMixer.FLUID_FILL_HEIGHT - filledHeight,
            left + MachineGuiLayouts.FluidMixer.FLUID_FILL_WIDTH,
            top + MachineGuiLayouts.FluidMixer.FLUID_FILL_HEIGHT,
            fillColor
        );
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
        guiGraphics.drawString(this.font, powerLabel, MachineGuiLayouts.FluidMixer.POWER_BAR_X, MachineGuiLayouts.FluidMixer.POWER_BAR_Y - 10, 0x404040, false);

        int itemPanelX = 0 - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        int fluidPanelX = itemPanelX - AUTOMATION_BUTTON_WIDTH - 4;
        int headerY = AUTOMATION_PANEL_Y - 10;
        guiGraphics.drawString(this.font, Component.translatable("gui.cobblestonexxcompressed.item"), itemPanelX + 18, headerY, 0x404040, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.cobblestonexxcompressed.fluid"), fluidPanelX + 16, headerY, 0x404040, false);
    }

    @Override
    protected void renderHoverLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.renderCobblestonePowerHoverLabel(guiGraphics, mouseX, mouseY, MachineGuiLayouts.FluidMixer.POWER_BAR_X, MachineGuiLayouts.FluidMixer.POWER_BAR_Y, MachineGuiLayouts.FluidMixer.POWER_BAR_WIDTH, MachineGuiLayouts.FluidMixer.POWER_BAR_HEIGHT);
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

        if (this.isMouseOverInputFluid1Indicator(mouseX, mouseY)) {
            this.renderFluidHoverLabel(guiGraphics, mouseX, mouseY, this.menu.getDisplayedInputFluid1(), this.menu.getStoredInputFluid1Amount(), this.menu.getMaxInputFluid1Amount(), Component.literal("Input 1 "));
        } else if (this.isMouseOverInputFluid2Indicator(mouseX, mouseY)) {
            this.renderFluidHoverLabel(guiGraphics, mouseX, mouseY, this.menu.getDisplayedInputFluid2(), this.menu.getStoredInputFluid2Amount(), this.menu.getMaxInputFluid2Amount(), Component.literal("Input 2 "));
        } else if (this.isMouseOverOutputFluidIndicator(mouseX, mouseY)) {
            this.renderFluidHoverLabel(guiGraphics, mouseX, mouseY, this.menu.getDisplayedOutputFluid(), this.menu.getStoredOutputFluidAmount(), this.menu.getMaxOutputFluidAmount(), Component.literal("Output "));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isMouseOverInputFluid1Indicator((int) mouseX, (int) mouseY)) {
            int buttonId = Screen.hasShiftDown() ? this.menu.getInputFluid1InteractionShiftButtonId() : this.menu.getInputFluid1InteractionButtonId();
            this.sendMenuButtonClick(buttonId);
            return true;
        }

        if (button == 0 && this.isMouseOverInputFluid2Indicator((int) mouseX, (int) mouseY)) {
            int buttonId = Screen.hasShiftDown() ? this.menu.getInputFluid2InteractionShiftButtonId() : this.menu.getInputFluid2InteractionButtonId();
            this.sendMenuButtonClick(buttonId);
            return true;
        }

        if (button == 0 && this.isMouseOverOutputFluidIndicator((int) mouseX, (int) mouseY)) {
            int buttonId = Screen.hasShiftDown() ? this.menu.getOutputFluidInteractionShiftButtonId() : this.menu.getOutputFluidInteractionButtonId();
            this.sendMenuButtonClick(buttonId);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public List<JeiClickableAreaDefinition> getJeiClickableAreaDefinitions() {
        return this.createSingleJeiClickableAreaDefinition(
            JEI_CLICK_AREA_X,
            JEI_CLICK_AREA_Y,
            JEI_CLICK_AREA_WIDTH,
            JEI_CLICK_AREA_HEIGHT,
            ModJeiIds.COBBLESTONE_FLUID_MIXER
        );
    }

    private boolean isMouseOverInputFluid1Indicator(int mouseX, int mouseY) {
        return this.isMouseOverFluidIndicator(mouseX, mouseY, MachineGuiLayouts.FluidMixer.INPUT_FLUID_1_SLOT_X, MachineGuiLayouts.FluidMixer.INPUT_FLUID_1_SLOT_Y);
    }

    private boolean isMouseOverInputFluid2Indicator(int mouseX, int mouseY) {
        return this.isMouseOverFluidIndicator(mouseX, mouseY, MachineGuiLayouts.FluidMixer.INPUT_FLUID_2_SLOT_X, MachineGuiLayouts.FluidMixer.INPUT_FLUID_2_SLOT_Y);
    }

    private boolean isMouseOverOutputFluidIndicator(int mouseX, int mouseY) {
        return this.isMouseOverFluidIndicator(mouseX, mouseY, MachineGuiLayouts.FluidMixer.OUTPUT_FLUID_SLOT_X, MachineGuiLayouts.FluidMixer.OUTPUT_FLUID_SLOT_Y);
    }

    private boolean isMouseOverFluidIndicator(int mouseX, int mouseY, int slotX, int slotY) {
        int left = this.leftPos + slotX;
        int top = this.topPos + slotY;
        return mouseX >= left
            && mouseX < left + MachineGuiLayouts.SLOT_SIZE
            && mouseY >= top
            && mouseY < top + MachineGuiLayouts.SLOT_SIZE;
    }

    private int getFluidIndicatorFillColor(FluidStack displayedFluid) {
        if (displayedFluid.isEmpty()) {
            return FLUID_INDICATOR_FILL_COLOR;
        }

        int tintColor = IClientFluidTypeExtensions.of(displayedFluid.getFluid()).getTintColor(displayedFluid);
        return tintColor | 0xFF000000;
    }

    private void renderFluidHoverLabel(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        FluidStack displayedFluid,
        long storedAmount,
        long maxAmount,
        Component prefix
    ) {
        Component label = displayedFluid.isEmpty()
            ? Component.translatable("gui.cobblestonexxcompressed.empty")
            : displayedFluid.getHoverName();
        Component amount = Component.literal(" (" + storedAmount + " / " + maxAmount + " mB)");
        this.renderHoverLabel(guiGraphics, mouseX, mouseY, prefix.copy().append(label).append(amount));
    }
}