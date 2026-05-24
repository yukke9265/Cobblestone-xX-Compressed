package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.world.item.Items;

public final class CobblestoneMelterRecipeDatagen {
    private static final MelterRecipeDefinition[] RECIPES = new MelterRecipeDefinition[] {
        new MelterRecipeDefinition(
            "topaz_mixture_to_glowtopaz_fluid",
             ModItems.TOPAZ_MIXTURES.get(),
            new FluidStack(ModFluids.WaterBasedFluid.GLOW_TOPAZ.getFluidEntry().getStillFluid().get(), 1000),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "ruby_mixture_to_redruby_fluid",
             ModItems.RUBY_MIXTURES.get(),
            new FluidStack(ModFluids.WaterBasedFluid.RED_RUBY.getFluidEntry().getStillFluid().get(), 1000),
            26214400,
            4096
        ),
        new MelterRecipeDefinition(
            "sapphire_mixture_to_bluesapphire_fluid",
             ModItems.SAPPHIRE_MIXTURE.get(),
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_SAPPHIRE.getFluidEntry().getStillFluid().get(), 1000),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "blaze_mixture_to_blaze_fluid",
             ModItems.BLAZE_MIXTURES.get(),
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_BLAZE.getFluidEntry().getStillFluid().get(), 1000),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "emerald_mixture_to_unstable_emerald_fluid",
             ModItems.EMERALD_MIXTURES.get(),
            new FluidStack(ModFluids.WaterBasedFluid.UNSTABLE_EMERALD.getFluidEntry().getStillFluid().get(), 1000),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "ancient_mixture_to_molten_netherite",
             ModItems.ANCIENT_MIXTURES.get(),
            new FluidStack(ModFluids.WaterBasedFluid.MOLTEN_NETHERITE.getFluidEntry().getStillFluid().get(), 1000),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "enderpearl_to_molten_ender",
             Items.ENDER_PEARL,
            new FluidStack(ModFluids.WaterBasedFluid.MOLTEN_ENDER.getFluidEntry().getStillFluid().get(), 500),
             26214400,
             4096
        )
    };

    private CobblestoneMelterRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (MelterRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneMelterRecipe(output, recipe.recipeName, recipe.ingredient, recipe.fluidResult, recipe.totalCobblestonePower, recipe.cobblestonePowerPerTick);
        }
    }

    private static class MelterRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final FluidStack fluidResult;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private MelterRecipeDefinition(String recipeName, ItemLike ingredient, FluidStack fluidResult, int totalCobblestonePower, int cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.fluidResult = fluidResult.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}