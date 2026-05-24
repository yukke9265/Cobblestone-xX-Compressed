package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

public final class CobblestoneFurnaceRecipeDatagen {
    private CobblestoneFurnaceRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        // 既存の cobblestone -> stone も含めて、丸石かまど用レシピをここへ集約します。
        MachineRecipeOutputHelper.saveCobblestoneFurnaceRecipe(output, "cobblestone_to_stone", Items.COBBLESTONE, Items.STONE, 100);

        for (CompressedStoneMachineRecipeData.MachineRecipeDefinition recipe : CompressedStoneMachineRecipeData.RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneFurnaceRecipe(
                output,
                recipe.getRecipeName(),
                recipe.getIngredient(),
                recipe.getResult(),
                recipe.getProcessingTime()
            );
        }
    }
}