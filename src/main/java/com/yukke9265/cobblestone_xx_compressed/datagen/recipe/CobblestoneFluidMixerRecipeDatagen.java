package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;

import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneFluidMixerRecipeDatagen {
    private static final FluidMixerRecipeDefinition[] RECIPES = new FluidMixerRecipeDefinition[] {
        new FluidMixerRecipeDefinition(
            "shiny_sapphire_and_water_to_shiny_water",
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_SAPPHIRE.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 10000),
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid().get(), 1000),
            1677721600L,
            65536
        ),
        new FluidMixerRecipeDefinition(
            "shiny_blaze_and_lava_to_hot_lava",
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_BLAZE.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(net.minecraft.world.level.material.Fluids.LAVA, 1000),
            new FluidStack(ModFluids.WaterBasedFluid.HOT_LAVA.getFluidEntry().getStillFluid().get(), 1000),
            1677721600L,
            65536
        ),
        new FluidMixerRecipeDefinition(
            "unstable_emerald_and_hot_lava_to_hot_unstable_emerald",
            new FluidStack(ModFluids.WaterBasedFluid.UNSTABLE_EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.WaterBasedFluid.HOT_LAVA.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.WaterBasedFluid.HOT_UNSTABLE_EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            1677721600L,
            65536
        ),
        new FluidMixerRecipeDefinition(
            "netherite_and_ender_to_unstable_enderite",
            new FluidStack(ModFluids.WaterBasedFluid.MOLTEN_NETHERITE.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.WaterBasedFluid.MOLTEN_ENDER.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.WaterBasedFluid.UNSTABLE_ENDERITE.getFluidEntry().getStillFluid().get(), 1000),
            1677721600L,
            65536
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
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private FluidMixerRecipeDefinition(String recipeName, FluidStack firstFluidInput, FluidStack secondFluidInput, FluidStack fluidOutput, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.firstFluidInput = firstFluidInput.copy();
            this.secondFluidInput = secondFluidInput.copy();
            this.fluidOutput = fluidOutput.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}