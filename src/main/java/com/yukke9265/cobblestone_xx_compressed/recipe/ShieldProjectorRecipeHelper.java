package com.yukke9265.cobblestone_xx_compressed.recipe;

import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

/**
 * シールドプロジェクターは入力照合が無いので、登録済みレシピの先頭を使います。
 */
public final class ShieldProjectorRecipeHelper {
    private ShieldProjectorRecipeHelper() {
    }

    public static Optional<ShieldProjectorRecipe> findRecipe(Level level) {
        for (RecipeHolder<ShieldProjectorRecipe> recipeHolder : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.SHIELD_PROJECTOR.get())) {
            return Optional.of(recipeHolder.value());
        }

        return Optional.empty();
    }
}
