package com.yukke9265.cobblestone_xx_compressed.compat.jei;

import net.minecraft.resources.ResourceLocation;

// JEI のレシピ転送で使うスロット範囲を、JEI API に依存しない形で持つ定義です。
// Menu 側は「どのカテゴリに対して、どの index 範囲を入力欄と所持品欄として扱うか」だけを返します。
public record JeiRecipeTransferDefinition(
    ResourceLocation recipeCategoryId,
    int recipeSlotStart,
    int recipeSlotCount,
    int inventorySlotStart,
    int inventorySlotCount
) {
}