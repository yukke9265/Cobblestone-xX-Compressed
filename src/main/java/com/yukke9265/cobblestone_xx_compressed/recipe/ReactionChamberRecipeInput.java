package com.yukke9265.cobblestone_xx_compressed.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class ReactionChamberRecipeInput implements RecipeInput {
    private final ItemStack firstItem;
    private final ItemStack secondItem;
    private final FluidStack fluidInput;

    public ReactionChamberRecipeInput(ItemStack firstItem, ItemStack secondItem, FluidStack fluidInput) {
        this.firstItem = firstItem;
        this.secondItem = secondItem;
        this.fluidInput = fluidInput;
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
        return 2;
    }

    public FluidStack getFluidInput() {
        return this.fluidInput;
    }
}