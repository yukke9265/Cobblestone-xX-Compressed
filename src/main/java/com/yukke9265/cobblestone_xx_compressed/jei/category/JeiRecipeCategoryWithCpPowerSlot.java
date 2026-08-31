package com.yukke9265.cobblestone_xx_compressed.jei.category;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;

/**
 * CP 機械の JEI カテゴリ共通処理。
 * 空の丸石スロットは JEI がホバー判定しないため、座標で CP 供給説明を出します。
 */
public abstract class JeiRecipeCategoryWithCpPowerSlot<R> implements IRecipeCategory<R> {
    protected abstract int getCpPowerSlotX();

    protected abstract int getCpPowerSlotY();

    @Override
    public void getTooltip(ITooltipBuilder tooltip, R recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        JeiCobblestonePowerItems.addTooltipIfHovered(tooltip, mouseX, mouseY, getCpPowerSlotX(), getCpPowerSlotY());
    }
}
