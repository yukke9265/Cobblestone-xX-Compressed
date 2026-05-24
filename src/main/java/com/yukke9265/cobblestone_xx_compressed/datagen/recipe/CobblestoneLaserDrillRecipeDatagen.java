package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public final class CobblestoneLaserDrillRecipeDatagen {
    private static final LaserDrillRecipeDefinition[] RECIPES = new LaserDrillRecipeDefinition[] {
        new LaserDrillRecipeDefinition(
            "amethyst_compressed_cobblestone_to_amethyst_shard_and_diamond",
            ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get(),
            new ItemStack(Items.AMETHYST_SHARD),
            0.5F,
            new ItemStack(Items.DIAMOND),
            0.05F,
            3200,
            16
        )
    };

    private CobblestoneLaserDrillRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (LaserDrillRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneLaserDrillRecipe(
                output,
                recipe.recipeName,
                recipe.ingredient,
                recipe.firstResult,
                recipe.firstResultChance,
                recipe.secondResult,
                recipe.secondResultChance,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static class LaserDrillRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemStack firstResult;
        private final float firstResultChance;
        private final ItemStack secondResult;
        private final float secondResultChance;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private LaserDrillRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            ItemStack firstResult,
            float firstResultChance,
            ItemStack secondResult,
            float secondResultChance,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.firstResult = firstResult.copy();
            this.firstResultChance = firstResultChance;
            this.secondResult = secondResult.copy();
            this.secondResultChance = secondResultChance;
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}