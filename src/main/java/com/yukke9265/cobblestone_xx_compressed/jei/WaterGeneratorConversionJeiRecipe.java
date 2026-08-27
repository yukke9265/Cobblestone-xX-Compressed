package com.yukke9265.cobblestone_xx_compressed.jei;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

@SuppressWarnings("null")
public class WaterGeneratorConversionJeiRecipe {
    public static final ResourceLocation ID =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "water_generator_conversion");

    private final ResourceLocation id;
    private final ItemStack catalyst;
    private final ItemStack cpExample;
    private final FluidStack waterOutput;

    public WaterGeneratorConversionJeiRecipe(ResourceLocation id, ItemStack catalyst, ItemStack cpExample, FluidStack waterOutput) {
        this.id = id;
        this.catalyst = catalyst;
        this.cpExample = cpExample;
        this.waterOutput = waterOutput;
    }

    public ResourceLocation getId() {
        return this.id;
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
            ID,
            new ItemStack(ModBlocks.COBBLESTONE_WATER_GENERATOR.get()),
            new ItemStack(net.minecraft.world.item.Items.COBBLESTONE),
            new FluidStack(Fluids.WATER, 1)
        ));
    }
}
