package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneDissolutionChamberRecipeDatagen {
    private static final DissolutionChamberRecipeDefinition[] RECIPES = new DissolutionChamberRecipeDefinition[] {
        new DissolutionChamberRecipeDefinition(
            "tier_ruby_compressed_cobblestone_to_molten_tier_ruby_dirty_compressed_cobblestone",
            ModBlocks.TierCompressedCobblestone.RUBY.getBlock().get(),
            new FluidStack(net.minecraft.world.level.material.Fluids.LAVA, 100),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.RUBY.getFluidEntry().getStillFluid().get(), 1000),
            12800,
            128
        )
    };

    private CobblestoneDissolutionChamberRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (DissolutionChamberRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneDissolutionChamberRecipe(output, recipe.recipeName, recipe.ingredient, recipe.fluidInput, recipe.fluidOutput, recipe.totalCobblestonePower, recipe.cobblestonePowerPerTick);
        }
    }

    private static class DissolutionChamberRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final FluidStack fluidInput;
        private final FluidStack fluidOutput;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private DissolutionChamberRecipeDefinition(String recipeName, ItemLike ingredient, FluidStack fluidInput, FluidStack fluidOutput, int totalCobblestonePower, int cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.fluidInput = fluidInput.copy();
            this.fluidOutput = fluidOutput.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}