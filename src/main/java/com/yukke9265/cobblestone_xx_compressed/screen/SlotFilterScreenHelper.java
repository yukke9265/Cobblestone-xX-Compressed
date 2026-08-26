package com.yukke9265.cobblestone_xx_compressed.screen;

import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTarget;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.SlotFilterMode;
import com.yukke9265.cobblestone_xx_compressed.menu.BaseMenu;
import com.yukke9265.cobblestone_xx_compressed.menu.SlotFilterMenuSupport;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/*
 * 方針:
 * フィルタ行はデフォルト非表示とし、トグルボタンだけで開閉します。
 * 開いているときだけ ghost・矢印・WLBL・選択ハイライトを出します。
 */
public final class SlotFilterScreenHelper {
    private final BaseScreen<? extends BaseMenu> screen;
    private Button filterToggleButton;
    private Button filterPrevButton;
    private Button filterNextButton;
    private Button filterModeButton;

    public SlotFilterScreenHelper(BaseScreen<? extends BaseMenu> screen) {
        this.screen = screen;
    }

    public void initWidgets() {
        BaseMenu menu = this.screen.getMenu();
        if (!menu.hasSlotFilterUi()) {
            return;
        }

        this.filterToggleButton = this.screen.addSlotFilterWidget(
            Button.builder(
                Component.empty(),
                button -> this.screen.sendMenuButtonClick(SlotFilterMenuSupport.FILTER_TOGGLE_BUTTON_ID)
            ).bounds(
                this.screen.getGuiLeft() + MachineGuiLayouts.SlotFilter.TOGGLE_BUTTON_X,
                this.screen.getGuiTop() + MachineGuiLayouts.SlotFilter.TOGGLE_BUTTON_Y,
                MachineGuiLayouts.SlotFilter.TOGGLE_BUTTON_WIDTH,
                MachineGuiLayouts.SlotFilter.BUTTON_HEIGHT
            ).build()
        );

        this.filterPrevButton = this.screen.addSlotFilterWidget(
            Button.builder(
                Component.literal("<"),
                button -> this.screen.sendMenuButtonClick(SlotFilterMenuSupport.FILTER_PREV_BUTTON_ID)
            ).bounds(
                this.screen.getGuiLeft() + MachineGuiLayouts.SlotFilter.PREV_BUTTON_X,
                this.screen.getGuiTop() + MachineGuiLayouts.SlotFilter.BUTTON_Y,
                MachineGuiLayouts.SlotFilter.PREV_BUTTON_WIDTH,
                MachineGuiLayouts.SlotFilter.BUTTON_HEIGHT
            ).build()
        );

        this.filterNextButton = this.screen.addSlotFilterWidget(
            Button.builder(
                Component.literal(">"),
                button -> this.screen.sendMenuButtonClick(SlotFilterMenuSupport.FILTER_NEXT_BUTTON_ID)
            ).bounds(
                this.screen.getGuiLeft() + MachineGuiLayouts.SlotFilter.NEXT_BUTTON_X,
                this.screen.getGuiTop() + MachineGuiLayouts.SlotFilter.BUTTON_Y,
                MachineGuiLayouts.SlotFilter.NEXT_BUTTON_WIDTH,
                MachineGuiLayouts.SlotFilter.BUTTON_HEIGHT
            ).build()
        );

        this.filterModeButton = this.screen.addSlotFilterWidget(
            Button.builder(
                Component.empty(),
                button -> this.screen.sendMenuButtonClick(SlotFilterMenuSupport.FILTER_MODE_BUTTON_ID)
            ).bounds(
                this.screen.getGuiLeft() + MachineGuiLayouts.SlotFilter.MODE_BUTTON_X,
                this.screen.getGuiTop() + MachineGuiLayouts.SlotFilter.BUTTON_Y,
                MachineGuiLayouts.SlotFilter.MODE_BUTTON_WIDTH,
                MachineGuiLayouts.SlotFilter.BUTTON_HEIGHT
            ).build()
        );

        this.refreshWidgets();
    }

    public void tick() {
        this.refreshWidgets();
    }

    public void renderPanel(GuiGraphics guiGraphics) {
        BaseMenu menu = this.screen.getMenu();
        if (!menu.hasSlotFilterUi() || !menu.isSlotFilterPanelOpen()) {
            return;
        }

        int guiLeft = this.screen.getGuiLeft();
        int guiTop = this.screen.getGuiTop();
        int panelTop = guiTop - MachineGuiLayouts.SlotFilter.PANEL_HEIGHT;
        int panelBottom = guiTop;
        int panelLeft = guiLeft + MachineGuiLayouts.SlotFilter.PREV_BUTTON_X - 2;
        int panelRight = guiLeft + MachineGuiLayouts.SlotFilter.MODE_BUTTON_X + MachineGuiLayouts.SlotFilter.MODE_BUTTON_WIDTH + 2;
        guiGraphics.fill(panelLeft, panelTop, panelRight, panelBottom, MachineGuiLayouts.SlotFilter.PANEL_BACKGROUND_COLOR);
        guiGraphics.fill(panelLeft, panelTop, panelRight, panelTop + 1, MachineGuiLayouts.SlotFilter.PANEL_BORDER_COLOR);
        guiGraphics.fill(panelLeft, panelBottom - 1, panelRight, panelBottom, MachineGuiLayouts.SlotFilter.PANEL_BORDER_COLOR);
        guiGraphics.fill(panelLeft, panelTop, panelLeft + 1, panelBottom, MachineGuiLayouts.SlotFilter.PANEL_BORDER_COLOR);
        guiGraphics.fill(panelRight - 1, panelTop, panelRight, panelBottom, MachineGuiLayouts.SlotFilter.PANEL_BORDER_COLOR);

        for (int index = 0; index < 9; index++) {
            int slotX = guiLeft + MachineGuiLayouts.SlotFilter.GHOST_START_X + index * MachineGuiLayouts.SLOT_SIZE;
            int slotY = guiTop + MachineGuiLayouts.SlotFilter.GHOST_SLOT_Y;
            this.screen.renderNormalSlotPart(guiGraphics, slotX, slotY);
        }
    }

    public void renderSelectedHighlight(GuiGraphics guiGraphics) {
        BaseMenu menu = this.screen.getMenu();
        if (!menu.hasSlotFilterUi() || !menu.isSlotFilterPanelOpen()) {
            return;
        }

        FilterTarget target = menu.getSelectedFilterTarget();
        if (target == null) {
            return;
        }

        int slotLeft = this.screen.getGuiLeft() + target.slotX();
        int slotTop = this.screen.getGuiTop() + target.slotY();
        guiGraphics.fill(slotLeft - 1, slotTop - 1, slotLeft + 18, slotTop, MachineGuiLayouts.SlotFilter.HIGHLIGHT_COLOR);
        guiGraphics.fill(slotLeft - 1, slotTop + 17, slotLeft + 18, slotTop + 18, MachineGuiLayouts.SlotFilter.HIGHLIGHT_COLOR);
        guiGraphics.fill(slotLeft - 1, slotTop, slotLeft, slotTop + 17, MachineGuiLayouts.SlotFilter.HIGHLIGHT_COLOR);
        guiGraphics.fill(slotLeft + 17, slotTop, slotLeft + 18, slotTop + 17, MachineGuiLayouts.SlotFilter.HIGHLIGHT_COLOR);
    }

    private void refreshWidgets() {
        BaseMenu menu = this.screen.getMenu();
        boolean panelOpen = menu.hasSlotFilterUi() && menu.isSlotFilterPanelOpen();

        if (this.filterToggleButton != null) {
            String toggleKey = panelOpen
                ? "gui.cobblestonexxcompressed.filter.close"
                : "gui.cobblestonexxcompressed.filter.open";
            this.filterToggleButton.setMessage(Component.translatable(toggleKey));
            this.filterToggleButton.visible = menu.hasSlotFilterUi();
        }

        if (this.filterPrevButton != null) {
            this.filterPrevButton.visible = panelOpen;
        }
        if (this.filterNextButton != null) {
            this.filterNextButton.visible = panelOpen;
        }
        if (this.filterModeButton != null) {
            this.filterModeButton.visible = panelOpen;
            if (panelOpen) {
                SlotFilterMode mode = menu.getSelectedFilterMode();
                String key = mode == SlotFilterMode.WHITELIST
                    ? "gui.cobblestonexxcompressed.filter.whitelist"
                    : "gui.cobblestonexxcompressed.filter.blacklist";
                this.filterModeButton.setMessage(Component.translatable(key));
            }
        }
    }
}
