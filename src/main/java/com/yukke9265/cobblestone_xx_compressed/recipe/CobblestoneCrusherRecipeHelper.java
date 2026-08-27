package com.yukke9265.cobblestone_xx_compressed.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.compat.ae2.Ae2InscriberCrushCompat;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * 丸石クラッシャー用のレシピ照合ヘルパー。
 * 独自レシピを優先し、無ければ AE2 刻印機の粉砕相当レシピを使います。
 */
public final class CobblestoneCrusherRecipeHelper {
    private CobblestoneCrusherRecipeHelper() {
    }

    public static Optional<CobblestoneCrusherRecipe> findMatchingRecipe(Level level, ItemStack inputStack) {
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        Optional<RecipeHolder<CobblestoneCrusherRecipe>> customHolder = level.getRecipeManager().getRecipeFor(
            ModRecipeTypes.COBBLESTONE_CRUSHER.get(),
            input,
            level
        );
        if (customHolder.isPresent()) {
            return Optional.of(customHolder.get().value());
        }

        return Ae2InscriberCrushCompat.findMatchingCrushRecipe(level, inputStack);
    }

    public static boolean isValidInput(Level level, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        for (RecipeHolder<CobblestoneCrusherRecipe> recipeHolder : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CRUSHER.get())) {
            if (recipeHolder.value().getIngredient().test(stack)) {
                return true;
            }
        }

        return Ae2InscriberCrushCompat.isValidCrushInput(level, stack);
    }

    /**
     * JEI 表示用。独自レシピのあとに、AE2 刻印機の粉砕相当を並べます。
     * すでに独自レシピがある入力は、AE2 側を重複表示しません。
     * AE2 分は元 Inscriber id 付きの RecipeHolder にして JEIu ブックマーク可能にする。
     */
    public static List<RecipeHolder<CobblestoneCrusherRecipe>> collectAllDisplayRecipes(Level level) {
        List<RecipeHolder<CobblestoneCrusherRecipe>> recipes = new ArrayList<>();

        for (RecipeHolder<CobblestoneCrusherRecipe> recipeHolder : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CRUSHER.get())) {
            recipes.add(recipeHolder);
        }

        for (RecipeHolder<CobblestoneCrusherRecipe> ae2RecipeHolder : Ae2InscriberCrushCompat.collectCrushDisplayRecipes(level)) {
            if (isCoveredByCustomRecipe(level, ae2RecipeHolder.value().getIngredient())) {
                continue;
            }

            recipes.add(ae2RecipeHolder);
        }

        return recipes;
    }

    private static boolean isCoveredByCustomRecipe(Level level, Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            return false;
        }

        for (ItemStack stack : ingredient.getItems()) {
            SingleRecipeInput input = new SingleRecipeInput(stack);
            if (level.getRecipeManager().getRecipeFor(ModRecipeTypes.COBBLESTONE_CRUSHER.get(), input, level).isPresent()) {
                return true;
            }
        }

        return false;
    }
}
