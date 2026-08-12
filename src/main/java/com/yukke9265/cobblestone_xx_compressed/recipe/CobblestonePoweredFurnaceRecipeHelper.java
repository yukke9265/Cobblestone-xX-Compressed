package com.yukke9265.cobblestone_xx_compressed.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

/**
 * Powered Furnace 用のレシピ照合ヘルパー。
 * 独自レシピを優先し、無ければ通常かまど（SMELTING）を CP 換算して使う。
 */
public final class CobblestonePoweredFurnaceRecipeHelper {
    // cookingTime を total CP としてそのまま使い、1 tick あたり 1 CP 消費する。
    private static final long VANILLA_SMELTING_CP_PER_TICK = 1L;

    private CobblestonePoweredFurnaceRecipeHelper() {
    }

    public static Optional<CobblestonePoweredFurnaceRecipe> findMatchingRecipe(Level level, ItemStack inputStack) {
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(inputStack);

        Optional<RecipeHolder<CobblestonePoweredFurnaceRecipe>> customHolder = level.getRecipeManager().getRecipeFor(
            ModRecipeTypes.COBBLESTONE_POWERED_FURNACE.get(),
            input,
            level
        );
        if (customHolder.isPresent()) {
            return Optional.of(customHolder.get().value());
        }

        Optional<RecipeHolder<SmeltingRecipe>> smeltingHolder = level.getRecipeManager().getRecipeFor(
            RecipeType.SMELTING,
            input,
            level
        );
        if (smeltingHolder.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(fromSmeltingRecipe(smeltingHolder.get().value(), level.registryAccess()));
    }

    public static boolean isValidInput(Level level, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        for (RecipeHolder<CobblestonePoweredFurnaceRecipe> recipeHolder : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_POWERED_FURNACE.get())) {
            if (recipeHolder.value().getIngredient().test(stack)) {
                return true;
            }
        }

        SingleRecipeInput input = new SingleRecipeInput(stack);
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level).isPresent();
    }

    /**
     * JEI 表示用。独自レシピのあとに、通常かまどの全レシピを変換して並べる。
     */
    public static List<CobblestonePoweredFurnaceRecipe> collectAllDisplayRecipes(Level level) {
        List<CobblestonePoweredFurnaceRecipe> recipes = new ArrayList<>();
        HolderLookup.Provider registries = level.registryAccess();

        for (RecipeHolder<CobblestonePoweredFurnaceRecipe> recipeHolder : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_POWERED_FURNACE.get())) {
            recipes.add(recipeHolder.value());
        }

        for (RecipeHolder<SmeltingRecipe> recipeHolder : level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)) {
            CobblestonePoweredFurnaceRecipe converted = fromSmeltingRecipe(recipeHolder.value(), registries);
            if (converted.getIngredient().isEmpty() || converted.getResult().isEmpty()) {
                continue;
            }

            recipes.add(converted);
        }

        return recipes;
    }

    public static CobblestonePoweredFurnaceRecipe fromSmeltingRecipe(SmeltingRecipe smeltingRecipe, HolderLookup.Provider registries) {
        Ingredient ingredient = smeltingRecipe.getIngredients().isEmpty()
            ? Ingredient.EMPTY
            : smeltingRecipe.getIngredients().get(0);
        ItemStack result = smeltingRecipe.getResultItem(registries);
        long totalCobblestonePower = Math.max(1L, smeltingRecipe.getCookingTime());
        return new CobblestonePoweredFurnaceRecipe(ingredient, result, totalCobblestonePower, VANILLA_SMELTING_CP_PER_TICK);
    }
}
