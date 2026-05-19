package com.yukke9265.cobblestone_xx_compressed.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class ChemicalReactorRecipeInput implements RecipeInput {
    private final ItemStack firstItem;
    private final ItemStack secondItem;
    private final FluidStack firstFluidInput;
    private final FluidStack secondFluidInput;

    public ChemicalReactorRecipeInput(ItemStack firstItem, ItemStack secondItem, FluidStack firstFluidInput, FluidStack secondFluidInput) {
        this.firstItem = firstItem;
        this.secondItem = secondItem;
        this.firstFluidInput = firstFluidInput;
        this.secondFluidInput = secondFluidInput;
    }

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> this.firstItem;
            case 1 -> this.secondItem;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        return this.firstItem.isEmpty()
            && this.secondItem.isEmpty()
            && this.firstFluidInput.isEmpty()
            && this.secondFluidInput.isEmpty();
    }

    public FluidStack getFirstFluidInput() {
        return this.firstFluidInput;
    }

    public FluidStack getSecondFluidInput() {
        return this.secondFluidInput;
    }
}