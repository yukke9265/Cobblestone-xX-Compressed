package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneMelterRecipeDatagen {
    private static final MelterRecipeDefinition[] RECIPES = new MelterRecipeDefinition[] {
        new MelterRecipeDefinition("tier_topaz_compressed_cobblestone_to_molten_tier_topaz_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock().get(), new FluidStack(ModFluids.TierMoltenCompressedCobblestone.TOPAZ.getFluidEntry().getStillFluid().get(), 1000), 6400, 64),
        new MelterRecipeDefinition("tier_diamond_compressed_cobblestone_to_molten_tier_diamond_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock().get(), new FluidStack(ModFluids.TierMoltenCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000), 51200, 512)
    };

    private CobblestoneMelterRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (MelterRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneMelterRecipe(output, recipe.recipeName, recipe.ingredient, recipe.fluidResult, recipe.totalCobblestonePower, recipe.cobblestonePowerPerTick);
        }
    }

    private static class MelterRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final FluidStack fluidResult;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private MelterRecipeDefinition(String recipeName, ItemLike ingredient, FluidStack fluidResult, int totalCobblestonePower, int cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.fluidResult = fluidResult.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}