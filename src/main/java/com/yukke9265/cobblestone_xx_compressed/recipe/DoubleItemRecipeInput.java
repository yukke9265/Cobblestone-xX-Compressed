package com.yukke9265.cobblestone_xx_compressed.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record DoubleItemRecipeInput(ItemStack firstInput, ItemStack secondInput) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if (index == 0) {
            return this.firstInput;
        }

        if (index == 1) {
            return this.secondInput;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return this.firstInput.isEmpty() || this.secondInput.isEmpty();
    }
}