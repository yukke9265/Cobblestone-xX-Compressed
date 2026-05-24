package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public final class CobblestoneExtremeCompressorRecipeDatagen {
    private static final ExtremeCompressorRecipeDefinition[] RECIPES = new ExtremeCompressorRecipeDefinition[] {
        new ExtremeCompressorRecipeDefinition(
            "obsidian_compressed_cobblestone_to_bedrock",
            ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock().get(),
            Items.BEDROCK,
            5000,
            409600,
            4096
        )
    };

    private CobblestoneExtremeCompressorRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (ExtremeCompressorRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneExtremeCompressorRecipe(
                output,
                recipe.recipeName,
                recipe.ingredient,
                recipe.result,
                recipe.requiredItemCount,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static class ExtremeCompressorRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemLike result;
        private final int requiredItemCount;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private ExtremeCompressorRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            ItemLike result,
            int requiredItemCount,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.result = result;
            this.requiredItemCount = requiredItemCount;
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}