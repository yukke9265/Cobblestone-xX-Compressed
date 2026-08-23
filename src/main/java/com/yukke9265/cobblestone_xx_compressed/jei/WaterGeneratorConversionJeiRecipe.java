package com.yukke9265.cobblestone_xx_compressed.jei;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

@SuppressWarnings("null")
public class WaterGeneratorConversionJeiRecipe {
    private final ItemStack catalyst;
    private final ItemStack cpExample;
    private final FluidStack waterOutput;

    public WaterGeneratorConversionJeiRecipe(ItemStack catalyst, ItemStack cpExample, FluidStack waterOutput) {
        this.catalyst = catalyst;
        this.cpExample = cpExample;
        this.waterOutput = waterOutput;
    }

    public ItemStack getCatalyst() {
        return this.catalyst;
    }

    public ItemStack getCpExample() {
        return this.cpExample;
    }

    public FluidStack getWaterOutput() {
        return this.waterOutput;
    }

    public static List<WaterGeneratorConversionJeiRecipe> createRecipes() {
        return List.of(new WaterGeneratorConversionJeiRecipe(
            new ItemStack(ModBlocks.COBBLESTONE_WATER_GENERATOR.get()),
            new ItemStack(net.minecraft.world.item.Items.COBBLESTONE),
            new FluidStack(Fluids.WATER, 1)
        ));
    }
}
