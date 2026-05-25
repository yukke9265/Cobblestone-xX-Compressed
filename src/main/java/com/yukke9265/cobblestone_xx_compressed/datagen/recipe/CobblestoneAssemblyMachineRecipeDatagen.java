package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneAssemblyMachineRecipeDatagen {
    private static final AssemblyMachineRecipeDefinition[] RECIPES = new AssemblyMachineRecipeDefinition[] {
        new AssemblyMachineRecipeDefinition(
            "netherite_processor",
            new ItemStack(Items.NETHERITE_INGOT, 64),
            new ItemStack(ModItems.TierCobblestoneWire.NETHERITE.getItem().get(), 16),
            new ItemStack(ModItems.TierCobblestoneCircuit.NETHERITE.getItem().get(), 4),
            new ItemStack(ModItems.TierCobblestoneCircuitPackage.NETHERITE.getItem().get(), 1),
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            new FluidStack(ModFluids.WaterBasedFluid.MOLTEN_NETHERITE.getFluidEntry().getStillFluid().get(), 1000),
            new ItemStack(ModItems.TierCobblestoneProcessor.NETHERITE.getItem().get(), 1),
            91912300134400L,
            4194304
        ),
        new AssemblyMachineRecipeDefinition(
            "obsidian_processor",
            new ItemStack(Blocks.OBSIDIAN, 64),
            new ItemStack(Blocks.OBSIDIAN, 64),
            new ItemStack(ModItems.TierCobblestoneWire.OBSIDIAN.getItem().get(), 16),
            new ItemStack(ModItems.TierCobblestoneCircuit.OBSIDIAN.getItem().get(), 4),
            new ItemStack(ModItems.TierCobblestoneCircuitPackage.OBSIDIAN.getItem().get(), 1),
            ItemStack.EMPTY,
            new FluidStack(ModFluids.WaterBasedFluid.UNSTABLE_ENDERITE.getFluidEntry().getStillFluid().get(), 1000),
            new ItemStack(ModItems.TierCobblestoneProcessor.OBSIDIAN.getItem().get(), 1),
            91912300134400L,
            4194304
        ),
        new AssemblyMachineRecipeDefinition(
            "canned_stable_endrite",
            new ItemStack(Items.NETHERITE_INGOT, 64),
            new ItemStack(ModItems.TierCobblestoneProcessor.NETHERITE.getItem().get(), 1),
            new ItemStack(ModItems.TierCobblestoneMachineCasingItem.NETHERITE.getItem().get(), 1),
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            new FluidStack(ModFluids.WaterBasedFluid.UNSTABLE_ENDERITE.getFluidEntry().getStillFluid().get(), 10000),
            new ItemStack(ModItems.CANNED_STABLE_ENDERITE.get(), 1),
            91912300134400L,
            4194304
        ),
        new AssemblyMachineRecipeDefinition(
            "canned_compressed_endrite",
            new ItemStack(ModItems.EXTREME_COMPRESSED_OBSIDIAN.get(), 1),
            new ItemStack(ModItems.TierCobblestoneProcessor.OBSIDIAN.getItem().get(), 1),
            new ItemStack(ModItems.TierCobblestoneMachineCasingItem.OBSIDIAN.getItem().get(), 1),
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            new FluidStack(ModFluids.WaterBasedFluid.UNSTABLE_ENDERITE.getFluidEntry().getStillFluid().get(), 64000),
            new ItemStack(ModItems.EXTREME_COMPRESSED_CANNED_ENDERITE.get(), 1),
            91912300134400L,
            4194304
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