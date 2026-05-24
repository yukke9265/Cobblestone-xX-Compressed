package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;

import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneFluidMixerRecipeDatagen {
    private static final FluidMixerRecipeDefinition[] RECIPES = new FluidMixerRecipeDefinition[] {
        new FluidMixerRecipeDefinition(
            "dirty_sapphire_and_water_to_sapphire",
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.SAPPHIRE.getFluidEntry().getStillFluid().get(), 100),
            new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 10000),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.SAPPHIRE.getFluidEntry().getStillFluid().get(), 100),
            25600,
            256
        )
    };

    private CobblestoneFluidMixerRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (FluidMixerRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneFluidMixerRecipe(output, recipe.recipeName, recipe.firstFluidInput, recipe.secondFluidInput, recipe.fluidOutput, recipe.totalCobblestonePower, recipe.cobblestonePowerPerTick);
        }
    }

    private static class FluidMixerRecipeDefinition {
        private final String recipeName;
        private final FluidStack firstFluidInput;
        private final FluidStack secondFluidInput;
        private final FluidStack fluidOutput;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private FluidMixerRecipeDefinition(String recipeName, FluidStack firstFluidInput, FluidStack secondFluidInput, FluidStack fluidOutput, int totalCobblestonePower, int cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.firstFluidInput = firstFluidInput.copy();
            this.secondFluidInput = secondFluidInput.copy();
            this.fluidOutput = fluidOutput.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}