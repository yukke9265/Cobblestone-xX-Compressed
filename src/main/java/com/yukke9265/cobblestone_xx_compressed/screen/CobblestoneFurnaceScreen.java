package com.yukke9265.cobblestone_xx_compressed.screen;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFurnaceMenu;
import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiClickableAreaDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;


public class CobblestoneFurnaceScreen extends BaseScreen<CobblestoneFurnaceMenu> {
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
    private static final int AUTO_EXPORT_BUTTON_WIDTH = 94;
    private static final int AUTO_EXPORT_BUTTON_HEIGHT = 20;
    private static final int JEI_CLICK_AREA_X = 80;
    private static final int JEI_CLICK_AREA_Y = 35;
    private static final int JEI_CLICK_AREA_WIDTH = 16;
    private static final int JEI_CLICK_AREA_HEIGHT = 16;

    private int progressBarX;
    private int progressBarY;
    private int progressBarWidth;
    private int progressBarHeight;
    private final Button[] automationButtons = new Button[AUTOMATION_SIDES.length];
    private Button autoExportButton;

    // CobblestoneFurnaceScreen クラスは、Cobblestone Furnace のインターフェースを定義するスクリーンクラスです。
    // 引数には、対応するメニュー、プレイヤーのインベントリ、そしてスクリーンのタイトルが渡されます。
    public CobblestoneFurnaceScreen(CobblestoneFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
        this.progressBarX = 80;//176/2 - 8
        this.progressBarY = 35;//166/2 - 8 - 40
        this.progressBarWidth = 32;
        this.progressBarHeight = 32;
    }

    private static final ResourceLocation BACKGROUND_TEXTURE =
    ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_furnace.png");

    private static final ResourceLocation PROGRESS_BAR_TEXTURE =
    ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_furnace_progress_bar.png");

    @Override
    protected void init() {
        super.init();
        // ここでは、スクリーンが初期化されたときに必要なウィジェットやボタンを追加することができます。
        // 例えば、特定の位置にアイテムスロットを配置したり、操作用のボタンを追加したりすることができます。
        this.addRenderableWidget(
            Button.builder(
                Component.literal("start/stop"), button -> this.onStartButtonPressed()
            ).bounds(this.leftPos + 110, this.topPos + 60, 60, 20).build()
        );

        this.autoExportButton = this.addRenderableWidget(
            Button.builder(
                Component.empty(), button -> this.onAutoExportButtonPressed()
            ).bounds(
                this.leftPos + this.imageWidth + 4,
                this.topPos + this.imageHeight - AUTO_EXPORT_BUTTON_HEIGHT,
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
        // Screen はクライアント専用なので、ここで BlockEntity を直接変更しません。
        // 代わりに「この Menu のボタン 0 が押された」という通知をサーバーへ送ります。
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
            // 画面上の表記は、機械基準ではなくプレイヤーが正面から見た左右に合わせます。
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
        // ここでは、スクリーンの背景を描画するためのコードを記述します。
        // 例えば、特定のテクスチャを背景として描画したり、装飾的な要素を追加したりすることができます。

        int x = this.leftPos;// スクリーンの左上隅のX座標を取得します。
        int y = this.topPos;// スクリーンの左上隅のY座標を取得します。

        // テクスチャを描画するためのコードを記述します。ここでは、BACKGROUND_TEXTURE 変数で指定されたテクスチャをスクリーンの背景として描画しています。
        this.renderBackgroundTexture(guiGraphics, BACKGROUND_TEXTURE, x, y, this.imageWidth, this.imageHeight);
        this.renderNormalSlotPart(guiGraphics, x + 56, y + 35);
        this.renderNormalSlotPart(guiGraphics, x + 109, y + 35);
        this.renderProgressFramePart(guiGraphics, x + this.progressBarX, y + this.progressBarY);

        //進行度バーの描画例（燃焼時間に応じてバーの長さを変える）
        int burnTime = this.menu.getBurnTime(); // メニューから燃焼時間を取得するメソッドを呼び出す（実装はメニュー側で行う必要があります）
        int maxBurnTime = this.menu.getMaxBurnTime(); // メニューから最大燃焼時間を取得するメソッドを呼び出す（実装はメニュー側で行う必要があります）
        
        //pose().pushPose() と pose().popPose() を使用して、描画のスケーリングを行っています。これにより、進行度バーを半分のサイズで描画することができます。
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);

        if (maxBurnTime > 0) {
            int burnBarWidth = (int) ((burnTime) / (float) maxBurnTime * this.progressBarWidth); // 燃焼時間に応じてバーの幅を計算
            guiGraphics.blit(
                PROGRESS_BAR_TEXTURE,
                (x + this.progressBarX) * 2,// 描画位置をスケーリングに合わせて2倍にします
                (y + this.progressBarY) * 2,// 描画位置をスケーリングに合わせて2倍にします
                0, 0,// テクスチャの左上隅から描画を開始します
                burnBarWidth,// 描画するバーの幅を指定
                this.progressBarHeight,// 描画するバーの高さを指定
                this.progressBarWidth,// テクスチャ全体の幅を指定
                this.progressBarHeight// テクスチャ全体の高さを指定
            ); // 燃焼時間に応じたバーを描画
        }

        guiGraphics.pose().popPose();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        int legendX = 0 - AUTOMATION_PANEL_X_OFFSET - AUTOMATION_BUTTON_WIDTH;
        int legendY = AUTOMATION_PANEL_Y - AUTOMATION_LEGEND_LINE_HEIGHT;
        guiGraphics.drawString(this.font, Component.literal("Off").withStyle(AutomationMode.DISABLED.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Input").withStyle(AutomationMode.INPUT.createLabelComponent().getStyle()), legendX + 28, legendY + AUTOMATION_LEGEND_LINE_HEIGHT, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("Output").withStyle(AutomationMode.OUTPUT.createLabelComponent().getStyle()), legendX, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 2, 0x404040, false);
        guiGraphics.drawString(this.font, Component.literal("In/Out").withStyle(AutomationMode.IN_OUT.createLabelComponent().getStyle()), legendX + 28, legendY + AUTOMATION_LEGEND_LINE_HEIGHT * 2, 0x404040, false);
    }

    @Override
    protected void renderHoverLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (AutomationSide side : AUTOMATION_SIDES) {
            this.renderButtonHoverLabel(guiGraphics, mouseX, mouseY, this.automationButtons[side.getIndex()], this.createAutomationHoverLabel(side));
        }
    }

    @Override
    public List<JeiClickableAreaDefinition> getJeiClickableAreaDefinitions() {
        // 炉 GUI では進行バー付近を JEI の入口にします。
        return this.createSingleJeiClickableAreaDefinition(
            JEI_CLICK_AREA_X,
            JEI_CLICK_AREA_Y,
            JEI_CLICK_AREA_WIDTH,
            JEI_CLICK_AREA_HEIGHT,
            ModJeiIds.COBBLESTONE_FURNACE
        );
    }
    
}
