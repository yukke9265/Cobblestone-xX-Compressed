package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneChemicalReactorRecipeDatagen {
    private static final ChemicalReactorRecipeDefinition[] RECIPES = new ChemicalReactorRecipeDefinition[] {
        new ChemicalReactorRecipeDefinition(
            "redstone_and_emerald_diamond_fluids_to_obsidian",
            new ItemStack(Items.REDSTONE, 16),
            ItemStack.EMPTY,
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            new ItemStack(Items.OBSIDIAN),
            new ItemStack(ModItems.DIRTY_MIXTURE.get()),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            102400,
            1024
        )
    };

    private CobblestoneChemicalReactorRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (ChemicalReactorRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneChemicalReactorRecipe(
                output,
                recipe.recipeName,
                recipe.firstItemInput,
                recipe.secondItemInput,
                recipe.firstFluidInput,
                recipe.secondFluidInput,
                recipe.firstResultItem,
                recipe.secondResultItem,
                recipe.firstResultFluid,
                recipe.secondResultFluid,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static class ChemicalReactorRecipeDefinition {
        private final String recipeName;
        private final ItemStack firstItemInput;
        private final ItemStack secondItemInput;
        private final FluidStack firstFluidInput;
        private final FluidStack secondFluidInput;
        private final ItemStack firstResultItem;
        private final ItemStack secondResultItem;
        private final FluidStack firstResultFluid;
        private final FluidStack secondResultFluid;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private ChemicalReactorRecipeDefinition(
            String recipeName,
            ItemStack firstItemInput,
            ItemStack secondItemInput,
            FluidStack firstFluidInput,
            FluidStack secondFluidInput,
            ItemStack firstResultItem,
            ItemStack secondResultItem,
            FluidStack firstResultFluid,
            FluidStack secondResultFluid,
            long totalCobblestonePower,
            long cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.firstItemInput = firstItemInput.copy();
            this.secondItemInput = secondItemInput.copy();
            this.firstFluidInput = firstFluidInput.copy();
            this.secondFluidInput = secondFluidInput.copy();
            this.firstResultItem = firstResultItem.copy();
            this.secondResultItem = secondResultItem.copy();
            this.firstResultFluid = firstResultFluid.copy();
            this.secondResultFluid = secondResultFluid.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}