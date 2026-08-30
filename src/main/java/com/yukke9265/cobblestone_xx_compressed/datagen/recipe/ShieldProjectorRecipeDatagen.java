package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import net.minecraft.data.recipes.RecipeOutput;

/**
 * シールドプロジェクターの基本レシピ（20 tick / 10 CP/t / シールド +10）。
 */
public final class ShieldProjectorRecipeDatagen {
    private ShieldProjectorRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        MachineRecipeOutputHelper.saveShieldProjectorRecipe(
            output,
            "basic_shield_generation",
            200L,
            1L,
            10L
        );
    }
}
