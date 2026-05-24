package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneCrystallizationChamberRecipeDatagen {
    private static final CrystallizationChamberRecipeDefinition[] RECIPES = new CrystallizationChamberRecipeDefinition[] {
        new CrystallizationChamberRecipeDefinition(
            "molten_diamond_compressed_cobblestone_to_diamond_cobblestone_dirty_dust",
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            ModItems.TIER_DIAMOND_COBBLESTONE_DIRTY_DUST.get(),
            13421772800L,
            262144
        )
    };

    private CobblestoneCrystallizationChamberRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (CrystallizationChamberRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneCrystallizationChamberRecipe(output, recipe.recipeName, recipe.fluidInput, recipe.result, recipe.totalCobblestonePower, recipe.cobblestonePowerPerTick);
        }
    }

    private static class CrystallizationChamberRecipeDefinition {
        private final String recipeName;
        private final FluidStack fluidInput;
        private final ItemLike result;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private CrystallizationChamberRecipeDefinition(String recipeName, FluidStack fluidInput, ItemLike result, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.fluidInput = fluidInput.copy();
            this.result = result;
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}