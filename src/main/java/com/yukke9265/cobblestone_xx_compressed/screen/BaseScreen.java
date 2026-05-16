package com.yukke9265.cobblestone_xx_compressed.screen;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiClickableAreaDefinition;
import com.yukke9265.cobblestone_xx_compressed.menu.BaseMenu;
import com.yukke9265.cobblestone_xx_compressed.util.GuiPartRenderer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class BaseScreen<T extends BaseMenu> extends AbstractContainerScreen<T> {
    protected static final int EXTERNAL_SLOT_SIZE = 18;
    private static final int EXTERNAL_SLOT_BORDER_COLOR = 0xFF8B8B8B;
    private static final int EXTERNAL_SLOT_BACKGROUND_COLOR = 0xFF373737;
    private static final int HOVER_LABEL_BACKGROUND_COLOR = 0xF0100010;
    private static final int HOVER_LABEL_BORDER_COLOR = 0xFF505050;
    private static final int HOVER_LABEL_TEXT_COLOR = 0xFFFFFFFF;

    // 共通の基底スクリーンクラスです。全てのスクリーンはこれを継承します。
    public BaseScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    // JEI のクリック領域は、機械ごとに数値だけが変わりやすいので、
    // 継承先では座標とカテゴリ ID を渡すだけで定義を返せるようにします。
    protected final List<JeiClickableAreaDefinition> createSingleJeiClickableAreaDefinition(
        int x,
        int y,
        int width,
        int height,
        net.minecraft.resources.ResourceLocation recipeCategoryId
    ) {
        return List.of(new JeiClickableAreaDefinition(x, y, width, height, recipeCategoryId));
    }

    // まだ特に共通の処理はありませんが、将来的に全スクリーンで共通のロジックをここにまとめることができます。
    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderHoverLabels(guiGraphics, mouseX, mouseY);
    }

    protected final void sendMenuButtonClick(int buttonId) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    protected final Component createCheckboxLabel(boolean checked, String translationKey) {
        return Component.literal(checked ? "[x] " : "[ ] ")
            .append(Component.translatable(translationKey));
    }

    protected final void renderExternalSlotFrame(GuiGraphics guiGraphics, int left, int top) {
        guiGraphics.fill(left, top, left + EXTERNAL_SLOT_SIZE, top + EXTERNAL_SLOT_SIZE, EXTERNAL_SLOT_BORDER_COLOR);
        guiGraphics.fill(left + 1, top + 1, left + EXTERNAL_SLOT_SIZE - 1, top + EXTERNAL_SLOT_SIZE - 1, EXTERNAL_SLOT_BACKGROUND_COLOR);
    }

    protected final void renderBackgroundTexture(
        GuiGraphics guiGraphics,
        net.minecraft.resources.ResourceLocation texture,
        int left,
        int top,
        int width,
        int height
    ) {
        GuiPartRenderer.renderBackground(guiGraphics, texture, left, top, width, height);
    }

    protected final void renderNormalSlotPart(GuiGraphics guiGraphics, int left, int top) {
        // 通常スロットの枠は、見た目を合わせるために 1px 左上へ寄せて描画します。
        GuiPartRenderer.renderNormalSlot(guiGraphics, left, top);
    }

    protected final void renderCobblestoneSlotPart(GuiGraphics guiGraphics, int left, int top) {
        // 丸石スロットは絵柄が大きいので、4px 左上へ寄せて中心を合わせます。
        GuiPartRenderer.renderCobblestoneSlot(guiGraphics, left, top);
    }

    protected final void renderProgressFramePart(GuiGraphics guiGraphics, int left, int top) {
        // 進捗バーの枠も 1px 左上へ寄せて、実際のバーと中心が合うようにします。
        GuiPartRenderer.renderProgressFrame(guiGraphics, left, top);
    }

    protected final void renderExternalSlotHoverLabel(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        int slotX,
        int slotY,
        Component tooltip
    ) {
        if (this.isHovering(slotX, slotY, EXTERNAL_SLOT_SIZE, EXTERNAL_SLOT_SIZE, mouseX, mouseY)) {
            this.renderHoverLabel(guiGraphics, mouseX, mouseY, tooltip);
        }
    }

    protected void renderHoverLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    protected final void renderHoverLabel(GuiGraphics guiGraphics, int mouseX, int mouseY, Component text) {
        int textWidth = this.font.width(text);
        int textHeight = 8;
        int labelLeft = mouseX + 8;
        int labelTop = mouseY - 12;
        int labelRight = labelLeft + textWidth + 8;
        int labelBottom = labelTop + textHeight + 8;

        guiGraphics.fill(labelLeft, labelTop, labelRight, labelBottom, HOVER_LABEL_BACKGROUND_COLOR);
        guiGraphics.fill(labelLeft, labelTop, labelRight, labelTop + 1, HOVER_LABEL_BORDER_COLOR);
        guiGraphics.fill(labelLeft, labelBottom - 1, labelRight, labelBottom, HOVER_LABEL_BORDER_COLOR);
        guiGraphics.fill(labelLeft, labelTop, labelLeft + 1, labelBottom, HOVER_LABEL_BORDER_COLOR);
        guiGraphics.fill(labelRight - 1, labelTop, labelRight, labelBottom, HOVER_LABEL_BORDER_COLOR);
        guiGraphics.drawString(this.font, text, labelLeft + 4, labelTop + 4, HOVER_LABEL_TEXT_COLOR, false);
    }

    protected final void renderButtonHoverLabel(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        Button button,
        Component text
    ) {
        if (button != null && button.isHovered()) {
            this.renderHoverLabel(guiGraphics, mouseX, mouseY, text);
        }
    }

    // renderBg はスクリーンの背景を描画するためのメソッドですが、今回はまだ実装していないので、空のままにしています。
    @Override
        protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    // JEI のレシピ一覧を開くクリック領域です。
    // Screen 側は JEI API を直接知らず、座標とカテゴリ ID だけを返します。
    public List<JeiClickableAreaDefinition> getJeiClickableAreaDefinitions() {
        return List.of();
    }

}
