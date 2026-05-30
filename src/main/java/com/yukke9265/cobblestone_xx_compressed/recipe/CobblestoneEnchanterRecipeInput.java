package com.yukke9265.cobblestone_xx_compressed.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class CobblestoneEnchanterRecipeInput implements RecipeInput {
    private final ItemStack toolInput;
    private final ItemStack bookInput;

    public CobblestoneEnchanterRecipeInput(ItemStack toolInput, ItemStack bookInput) {
        this.toolInput = toolInput;
        this.bookInput = bookInput;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index == 0) {
            return this.toolInput;
        }
        if (index == 1) {
            return this.bookInput;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return this.toolInput.isEmpty() && this.bookInput.isEmpty();
    }

    public ItemStack getToolInput() {
        return this.toolInput;
    }

    public ItemStack getBookInput() {
        return this.bookInput;
    }
}