package com.yukke9265.cobblestone_xx_compressed.screen;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneDrawerMenu;
import com.yukke9265.cobblestone_xx_compressed.util.NumberDisplayHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class CobblestoneDrawerScreen extends BaseScreen<CobblestoneDrawerMenu> {
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

    private static final int INPUT_SLOT_X = 26;
    private static final int OUTPUT_SLOT_X = 132;
    private static final int MACHINE_SLOT_Y = 17;
    private static final int STORAGE_INDICATOR_X = INPUT_SLOT_X + 22;
    private static final int STORAGE_INDICATOR_Y = MACHINE_SLOT_Y + 3;
    private static final int STORAGE_INDICATOR_WIDTH = OUTPUT_SLOT_X - INPUT_SLOT_X - 26;
    private static final int STORAGE_INDICATOR_HEIGHT = 12;
    private static final int STORAGE_INDICATOR_HOVER_X = INPUT_SLOT_X + 18;
    private static final int STORAGE_INDICATOR_HOVER_Y = MACHINE_SLOT_Y - 2;
    private static final int STORAGE_INDICATOR_HOVER_WIDTH = OUTPUT_SLOT_X - INPUT_SLOT_X - 18;
    private static final int STORAGE_INDICATOR_HOVER_HEIGHT = 20;
    private static final int STORAGE_INDICATOR_BORDER_COLOR = 0xFF404040;
    private static final int STORAGE_INDICATOR_BACKGROUND_COLOR = 0xFF101010;
    private static final int STORAGE_INDICATOR_FILL_COLOR = 0xFF8B6914;
    private static final int STORAGE_ITEM_NAME_Y = 36;
    private static final int STORAGE_AMOUNT_Y = 46;

    private static final int AUTO_EXPORT_BUTTON_WIDTH = 94;
    private static final int AUTO_EXPORT_BUTTON_HEIGHT = 20;
    private static final int AUTO_INSERT_BUTTON_WIDTH = 94;
    private static final int AUTO_INSERT_BUTTON_HEIGHT = 20;
    private static final int VOID_OVERFLOW_BUTTON_WIDTH = 94;
    private static final int VOID_OVERFLOW_BUTTON_HEIGHT = 20;
    private static final int AUTO_EXPORT_BUTTON_X_OFFSET = 4;

    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_drawer.png");

    private final Button[] itemAutomationButtons = new Button[AUTOMATION_SIDES.length];
    private Button autoExportButton;
    private Button autoInsertButton;
    private Button voidOverflowButton;

    private final int imageWidth = 176;
    private final int imageHeight = 166;
    private final int titleLabelX = 8;
    private final int titleLabelY = 6;
    private final int inventoryLabelX = 8;
    private final int inventoryLabelY = 72;

    public CobblestoneDrawerScreen(CobblestoneDrawerMenu menu, Inventory inventory, Component title) {
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

        this.autoInsertButton = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> this.sendMenuButtonClick(this.menu.getAutoInsertButtonId()))
                .bounds(
                    this.leftPos + this.imageWidth + AUTO_EXPORT_BUTTON_X_OFFSET,
                    this.topPos + this.imageHeight - AUTO_EXPORT_BUTTON_HEIGHT - AUTO_INSERT_BUTTON_HEIGHT - 2,
                    AUTO_INSERT_BUTTON_WIDTH,
                    AUTO_INSERT_BUTTON_HEIGHT
                )
                .build()
        );

        this.voidOverflowButton = this.addRenderableWidget(
            Button.builder(Component.empty(), button -> this.sendMenuButtonClick(this.menu.getVoidOverflowButtonId()))
                .bounds(
                    this.leftPos + this.imageWidth + AUTO_EXPORT_BUTTON_X_OFFSET,
                    this.topPos + this.imageHeight - AUTO_EXPORT_BUTTON_HEIGHT - AUTO_INSERT_BUTTON_HEIGHT - VOID_OVERFLOW_BUTTON_HEIGHT - 4,
                    VOID_OVERFLOW_BUTTON_WIDTH,
                    VOID_OVERFLOW_BUTTON_HEIGHT
                )
                .build()
        );

        int itemPanelX = this.leftPos - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        for (int index = 0; index < AUTOMATION_SIDES.length; index++) {
            AutomationSide side = AUTOMATION_SIDES[index];
            int y = this.topPos + AUTOMATION_PANEL_Y + index * AUTOMATION_BUTTON_SPACING;
            this.addItemAutomationButton(side, itemPanelX, y);
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

    @Override
    protected void containerTick() {
        super.containerTick();
        this.refreshButtons();
    }

    private void refreshButtons() {
        for (AutomationSide side : AUTOMATION_SIDES) {
            Button itemButton = this.itemAutomationButtons[side.getIndex()];
            if (itemButton != null) {
                itemButton.setMessage(this.createAutomationButtonLabel(side, this.menu.getItemAutomationMode(side)));
            }
        }

        if (this.autoExportButton != null) {
            this.autoExportButton.setMessage(this.createCheckboxLabel(this.menu.isAutoExportEnabled(), "gui.cobblestonexxcompressed.auto_export"));
        }

        if (this.autoInsertButton != null) {
            this.autoInsertButton.setMessage(this.createCheckboxLabel(this.menu.isAutoInsertEnabled(), "gui.cobblestonexxcompressed.auto_insert"));
        }

        if (this.voidOverflowButton != null) {
            this.voidOverflowButton.setMessage(this.createCheckboxLabel(this.menu.isVoidOverflowEnabled(), "gui.cobblestonexxcompressed.void_overflow"));
        }
    }

    private Component createAutomationButtonLabel(AutomationSide side, AutomationMode mode) {
        return Component.translatable(this.getAutomationSideTranslationKey(side))
            .withStyle(mode.createLabelComponent().getStyle());
    }

    private Component createAutomationHoverLabel(AutomationSide side, AutomationMode mode) {
        return Component.translatable("gui.cobblestonexxcompressed.item")
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
    protected void renderMachineBackground(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        this.renderBackgroundTexture(guiGraphics, BACKGROUND_TEXTURE, x, y, this.imageWidth, this.imageHeight);
        this.renderNormalSlotPart(guiGraphics, x + INPUT_SLOT_X, y + MACHINE_SLOT_Y);
        this.renderNormalSlotPart(guiGraphics, x + OUTPUT_SLOT_X, y + MACHINE_SLOT_Y);
        guiGraphics.fill(
            x + STORAGE_INDICATOR_X - 1,
            y + STORAGE_INDICATOR_Y - 1,
            x + STORAGE_INDICATOR_X + STORAGE_INDICATOR_WIDTH + 1,
            y + STORAGE_INDICATOR_Y + STORAGE_INDICATOR_HEIGHT + 1,
            STORAGE_INDICATOR_BORDER_COLOR
        );
        guiGraphics.fill(
            x + STORAGE_INDICATOR_X,
            y + STORAGE_INDICATOR_Y,
            x + STORAGE_INDICATOR_X + STORAGE_INDICATOR_WIDTH,
            y + STORAGE_INDICATOR_Y + STORAGE_INDICATOR_HEIGHT,
            STORAGE_INDICATOR_BACKGROUND_COLOR
        );

        long storedAmount = this.menu.getStoredAmount();
        long maxStoredAmount = this.menu.getMaxStoredAmount();
        if (maxStoredAmount > 0L && storedAmount > 0L) {
            int filledWidth = (int) Math.max(1L, Math.round(storedAmount / (double) maxStoredAmount * STORAGE_INDICATOR_WIDTH));
            guiGraphics.fill(
                x + STORAGE_INDICATOR_X,
                y + STORAGE_INDICATOR_Y,
                x + STORAGE_INDICATOR_X + filledWidth,
                y + STORAGE_INDICATOR_Y + STORAGE_INDICATOR_HEIGHT,
                STORAGE_INDICATOR_FILL_COLOR
            );
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        int centerX = this.imageWidth / 2;
        Component itemNameLabel = this.getStoredItemNameLabel();
        guiGraphics.drawString(this.font, itemNameLabel, centerX - this.font.width(itemNameLabel) / 2, STORAGE_ITEM_NAME_Y, 0x404040, false);

        Component amountLabel = Component.literal(NumberDisplayHelper.formatRange(this.menu.getStoredAmount(), this.menu.getMaxStoredAmount()));
        guiGraphics.drawString(this.font, amountLabel, centerX - this.font.width(amountLabel) / 2, STORAGE_AMOUNT_Y, 0x404040, false);

        int itemPanelX = 0 - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        int headerY = AUTOMATION_PANEL_Y - 10;
        guiGraphics.drawString(this.font, Component.translatable("gui.cobblestonexxcompressed.item"), itemPanelX + 18, headerY, 0x404040, false);
    }

    @Override
    protected void renderHoverLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (AutomationSide side : AUTOMATION_SIDES) {
            int index = side.getIndex();
            this.renderButtonHoverLabel(
                guiGraphics,
                mouseX,
                mouseY,
                this.itemAutomationButtons[index],
                this.createAutomationHoverLabel(side, this.menu.getItemAutomationMode(side))
            );
        }

        if (this.isMouseOverStorageIndicator(mouseX, mouseY)) {
            this.renderStorageHoverLabel(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AutomationSide side : AUTOMATION_SIDES) {
            int index = side.getIndex();
            if (this.handleAutomationButtonRightClick(button, this.itemAutomationButtons[index], this.menu.getReverseAutomationButtonId(index))) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOverStorageIndicator(int mouseX, int mouseY) {
        int hoverLeft = this.leftPos + STORAGE_INDICATOR_HOVER_X;
        int hoverTop = this.topPos + STORAGE_INDICATOR_HOVER_Y;
        int hoverRight = hoverLeft + STORAGE_INDICATOR_HOVER_WIDTH;
        int hoverBottom = hoverTop + STORAGE_INDICATOR_HOVER_HEIGHT;
        return mouseX >= hoverLeft && mouseX < hoverRight && mouseY >= hoverTop && mouseY < hoverBottom;
    }

    private Component getStoredItemNameLabel() {
        ItemStack storedStack = this.menu.getDisplayedStoredStack();
        if (storedStack.isEmpty()) {
            return Component.translatable("gui.cobblestonexxcompressed.empty");
        }

        return storedStack.getHoverName();
    }

    private void renderStorageHoverLabel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component label = this.getStoredItemNameLabel();
        Component amount = Component.literal(" (" + NumberDisplayHelper.formatRange(this.menu.getStoredAmount(), this.menu.getMaxStoredAmount()) + ")");
        this.renderHoverLabel(guiGraphics, mouseX, mouseY, label.copy().append(amount));
    }
}
