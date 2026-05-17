package com.yukke9265.cobblestone_xx_compressed.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class DissolutionChamberRecipeInput implements RecipeInput {
    private final ItemStack itemInput;
    private final FluidStack fluidInput;

    public DissolutionChamberRecipeInput(ItemStack itemInput, FluidStack fluidInput) {
        this.itemInput = itemInput;
        this.fluidInput = fluidInput;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index == 0) {
            return this.itemInput;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }

    public FluidStack getFluidInput() {
        return this.fluidInput;
    }
}