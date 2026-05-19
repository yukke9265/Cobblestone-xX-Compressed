package com.yukke9265.cobblestone_xx_compressed.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class CrystallizationChamberRecipeInput implements RecipeInput {
    private final FluidStack fluidInput;

    public CrystallizationChamberRecipeInput(FluidStack fluidInput) {
        this.fluidInput = fluidInput;
    }

    @Override
    public ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        // この機械はアイテムではなく液体だけを入力として使う。
        // そのため item ベースの既定判定に任せると常に「空入力」扱いになり、
        // レシピ検索自体が開始されない。
        return this.fluidInput.isEmpty();
    }

    public FluidStack getFluidInput() {
        return this.fluidInput;
    }
}
