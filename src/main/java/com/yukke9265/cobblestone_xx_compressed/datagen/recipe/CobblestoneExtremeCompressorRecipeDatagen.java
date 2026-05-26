package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.ItemLike;

public final class CobblestoneExtremeCompressorRecipeDatagen {
    private static final long TOTAL_CP = MachineRecipePowerTiers.EXTREME_COMPRESSOR_TOTAL_CP;

    private static final ExtremeCompressorRecipeDefinition[] RECIPES = new ExtremeCompressorRecipeDefinition[] {
        new ExtremeCompressorRecipeDefinition(
            "obsidian_to_extreme_compressed_obsidian",
            Blocks.OBSIDIAN,
            ModItems.EXTREME_COMPRESSED_OBSIDIAN.get(),
            4096,
            TOTAL_CP,
            33554432
        ),
        // 同じ入力素材に複数レシピをぶら下げると、RecipeManager が先に見つけた 1 件だけを返します。
        // そのため Bedrock は 1 段階後ろへずらして、Extreme Compressed Obsidian から作る連鎖にします。
        new ExtremeCompressorRecipeDefinition(
            "obsidian_cobblestone_gem_to_bedrock",
            ModItems.TIER_OBSIDIAN_COBBLESTONE_GEM.get(),
            Blocks.BEDROCK,
            4096,
            TOTAL_CP,
            33554432
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
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private ExtremeCompressorRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            ItemLike result,
            int requiredItemCount,
            long totalCobblestonePower,
            long cobblestonePowerPerTick
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