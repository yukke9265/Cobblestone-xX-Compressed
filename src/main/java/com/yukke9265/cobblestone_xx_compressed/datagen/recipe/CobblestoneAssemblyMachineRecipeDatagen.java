package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneAssemblyMachineRecipeDatagen {
    private static final AssemblyMachineRecipeDefinition[] RECIPES = new AssemblyMachineRecipeDefinition[] {
        new AssemblyMachineRecipeDefinition(
            "netherite_processor",
            new ItemStack(ModItems.TierCobblestoneCircuit.NETHERITE.getItem().get(), 4),
            new ItemStack(ModItems.TierCobblestoneCircuit.EMERALD.getItem().get(), 4),
            new ItemStack(ModItems.TierCobblestoneCircuitPackage.NETHERITE.getItem().get(), 1),
            new ItemStack(ModItems.TierCobblestoneWire.NETHERITE.getItem().get(), 16),
            new ItemStack(ModItems.TIER_NETHERITE_COBBLESTONE_GEM.get(), 16),
            ItemStack.EMPTY,
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.NETHERITE.getFluidEntry().getStillFluid().get(), 1000),
            new ItemStack(ModItems.TierCobblestoneProcessor.NETHERITE.getItem().get(), 1),
            204800,
            2048
        )
    };

    private CobblestoneAssemblyMachineRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (AssemblyMachineRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneAssemblyMachineRecipe(
                output,
                recipe.recipeName,
                recipe.firstItemInput,
                recipe.secondItemInput,
                recipe.thirdItemInput,
                recipe.fourthItemInput,
                recipe.fifthItemInput,
                recipe.sixthItemInput,
                recipe.fluidInput,
                recipe.resultItem,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static class AssemblyMachineRecipeDefinition {
        private final String recipeName;
        private final ItemStack firstItemInput;
        private final ItemStack secondItemInput;
        private final ItemStack thirdItemInput;
        private final ItemStack fourthItemInput;
        private final ItemStack fifthItemInput;
        private final ItemStack sixthItemInput;
        private final FluidStack fluidInput;
        private final ItemStack resultItem;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private AssemblyMachineRecipeDefinition(
            String recipeName,
            ItemStack firstItemInput,
            ItemStack secondItemInput,
            ItemStack thirdItemInput,
            ItemStack fourthItemInput,
            ItemStack fifthItemInput,
            ItemStack sixthItemInput,
            FluidStack fluidInput,
            ItemStack resultItem,
            long totalCobblestonePower,
            long cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.firstItemInput = firstItemInput.copy();
            this.secondItemInput = secondItemInput.copy();
            this.thirdItemInput = thirdItemInput.copy();
            this.fourthItemInput = fourthItemInput.copy();
            this.fifthItemInput = fifthItemInput.copy();
            this.sixthItemInput = sixthItemInput.copy();
            this.fluidInput = fluidInput.copy();
            this.resultItem = resultItem.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}