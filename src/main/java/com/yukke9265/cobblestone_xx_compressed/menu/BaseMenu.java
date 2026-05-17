package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiRecipeTransferDefinition;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;


public class BaseMenu extends AbstractContainerMenu {
    protected static final int AUTOMATION_BUTTON_ID_BASE = 100;
    protected static final int AUTO_EXPORT_BUTTON_ID = 200;
    protected static final int FLUID_AUTOMATION_BUTTON_ID_BASE = 300;
    protected static final int FLUID_INDICATOR_BUTTON_ID = 400;
    protected static final int FLUID_INDICATOR_SHIFT_BUTTON_ID = 401;

    // 共通の基底メニュークラスです。全てのメニューはこれを継承します。
    public BaseMenu(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    protected final int getAutomationButtonId(int automationIndex) {
        return AUTOMATION_BUTTON_ID_BASE + automationIndex;
    }

    protected final boolean isAutomationButtonId(int buttonId) {
        return buttonId >= AUTOMATION_BUTTON_ID_BASE
            && buttonId < AUTOMATION_BUTTON_ID_BASE + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    }

    protected final int getAutomationIndexFromButtonId(int buttonId) {
        return buttonId - AUTOMATION_BUTTON_ID_BASE;
    }

    protected final int getFluidAutomationButtonId(int automationIndex) {
        return FLUID_AUTOMATION_BUTTON_ID_BASE + automationIndex;
    }

    protected final boolean isFluidAutomationButtonId(int buttonId) {
        return buttonId >= FLUID_AUTOMATION_BUTTON_ID_BASE
            && buttonId < FLUID_AUTOMATION_BUTTON_ID_BASE + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    }

    protected final int getFluidAutomationIndexFromButtonId(int buttonId) {
        return buttonId - FLUID_AUTOMATION_BUTTON_ID_BASE;
    }

    protected final boolean handleAutomationButtonClick(BaseBlockEntity blockEntity, int buttonId) {
        if (!this.isAutomationButtonId(buttonId)) {
            return false;
        }

        blockEntity.cycleAutomationMode(this.getAutomationIndexFromButtonId(buttonId));
        return true;
    }

    protected final boolean handleFluidAutomationButtonClick(BaseBlockEntity blockEntity, int buttonId) {
        if (!this.isFluidAutomationButtonId(buttonId)) {
            return false;
        }

        blockEntity.cycleFluidAutomationMode(this.getFluidAutomationIndexFromButtonId(buttonId));
        return true;
    }

    protected final int getAutoExportToggleButtonId() {
        return AUTO_EXPORT_BUTTON_ID;
    }

    protected final boolean isAutoExportButtonId(int buttonId) {
        return buttonId == AUTO_EXPORT_BUTTON_ID;
    }

    protected final boolean handleAutoExportButtonClick(BaseBlockEntity blockEntity, int buttonId) {
        if (!this.isAutoExportButtonId(buttonId)) {
            return false;
        }

        blockEntity.toggleAutoExportEnabled();
        return true;
    }

    protected final int getFluidIndicatorButtonId() {
        return FLUID_INDICATOR_BUTTON_ID;
    }

    protected final boolean isFluidIndicatorButtonId(int buttonId) {
        return buttonId == FLUID_INDICATOR_BUTTON_ID;
    }

    protected final int getFluidIndicatorShiftButtonId() {
        return FLUID_INDICATOR_SHIFT_BUTTON_ID;
    }

    protected final boolean isFluidIndicatorShiftButtonId(int buttonId) {
        return buttonId == FLUID_INDICATOR_SHIFT_BUTTON_ID;
    }

    protected final long getLongFromData(ContainerData data, int lowerIndex) {
        return LongDataHelper.toLong(data.get(lowerIndex), data.get(lowerIndex + 1));
    }

    protected final FluidStack getFluidFromData(ContainerData data, int amountLowerIndex, int fluidIdIndex) {
        long storedAmount = this.getLongFromData(data, amountLowerIndex);
        if (storedAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        int fluidId = data.get(fluidIdIndex);
        if (fluidId < 0) {
            return FluidStack.EMPTY;
        }

        Fluid fluid = BuiltInRegistries.FLUID.byId(fluidId);
        if (fluid == Fluids.EMPTY) {
            return FluidStack.EMPTY;
        }

        return new FluidStack(fluid, (int) Math.min(storedAmount, Integer.MAX_VALUE));
    }

    // JEI 連携では毎回同じ形の definition を組み立てるので、
    // 継承先は「どのカテゴリに、どの範囲を渡すか」だけを書けば済むようにします。
    protected final List<JeiRecipeTransferDefinition> createSingleJeiRecipeTransferDefinition(
        net.minecraft.resources.ResourceLocation recipeCategoryId,
        int recipeSlotStart,
        int recipeSlotCount,
        int inventorySlotStart,
        int inventorySlotCount
    ) {
        return List.of(new JeiRecipeTransferDefinition(
            recipeCategoryId,
            recipeSlotStart,
            recipeSlotCount,
            inventorySlotStart,
            inventorySlotCount
        ));
    }
    
    // まだ特に共通の処理はありませんが、将来的に全メニューで共通のロジックをここにまとめることができます。
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    // quickMoveStack は Shift + クリックでアイテムを素早く移動させるためのメソッドですが、
    // 今回はまだ実装していないので、例外を投げるようにしています。
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        throw new UnsupportedOperationException("Unimplemented method 'quickMoveStack'");
    }

    // JEI のレシピ転送で使うスロット範囲です。
    // JEI 非導入環境でも安全に読み込めるよう、JEI API ではなく自前 record を返します。
    // 対応する機械だけが override して返せばよく、未対応メニューは空のままで構いません。
    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        return List.of();
    }
}
