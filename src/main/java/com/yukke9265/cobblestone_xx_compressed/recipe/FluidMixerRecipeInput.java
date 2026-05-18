package com.yukke9265.cobblestone_xx_compressed.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidMixerRecipeInput implements RecipeInput {
    private final FluidStack firstFluidInput;
    private final FluidStack secondFluidInput;

    public FluidMixerRecipeInput(FluidStack firstFluidInput, FluidStack secondFluidInput) {
        this.firstFluidInput = firstFluidInput;
        this.secondFluidInput = secondFluidInput;
    }

    @Override
    public ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }

    public FluidStack getFirstFluidInput() {
        return this.firstFluidInput;
    }

    public FluidStack getSecondFluidInput() {
        return this.secondFluidInput;
    }
}