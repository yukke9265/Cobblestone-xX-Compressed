package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

public final class CobblestonePoweredFurnaceRecipeDatagen {
    private CobblestonePoweredFurnaceRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        MachineRecipeOutputHelper.saveCobblestonePoweredFurnaceRecipe(output, "cobblestone_to_stone", Items.COBBLESTONE, Items.STONE, 100, 1);

        for (CompressedStoneMachineRecipeData.MachineRecipeDefinition recipe : CompressedStoneMachineRecipeData.RECIPES) {
            MachineRecipeOutputHelper.saveCobblestonePoweredFurnaceRecipe(
                output,
                recipe.getRecipeName(),
                recipe.getIngredient(),
                recipe.getResult(),
                recipe.getProcessingTime(),
                1
            );
        }
    }
}