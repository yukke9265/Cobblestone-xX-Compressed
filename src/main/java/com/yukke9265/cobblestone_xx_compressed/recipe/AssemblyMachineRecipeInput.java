package com.yukke9265.cobblestone_xx_compressed.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class AssemblyMachineRecipeInput implements RecipeInput {
    private final ItemStack[] itemInputs;
    private final FluidStack fluidInput;

    public AssemblyMachineRecipeInput(
        ItemStack firstItem,
        ItemStack secondItem,
        ItemStack thirdItem,
        ItemStack fourthItem,
        ItemStack fifthItem,
        ItemStack sixthItem,
        FluidStack fluidInput
    ) {
        this.itemInputs = new ItemStack[] {
            firstItem,
            secondItem,
            thirdItem,
            fourthItem,
            fifthItem,
            sixthItem
        };
        this.fluidInput = fluidInput;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index < 0 || index >= this.itemInputs.length) {
            return ItemStack.EMPTY;
        }

        return this.itemInputs[index];
    }

    @Override
    public int size() {
        return 7;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemInput : this.itemInputs) {
            if (!itemInput.isEmpty()) {
                return false;
            }
        }

        return this.fluidInput.isEmpty();
    }

    public FluidStack getFluidInput() {
        return this.fluidInput;
    }
}