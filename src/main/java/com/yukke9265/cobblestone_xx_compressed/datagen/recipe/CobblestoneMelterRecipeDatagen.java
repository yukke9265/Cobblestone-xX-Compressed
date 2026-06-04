package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

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
        ),
        new MelterRecipeDefinition(
            "diamond_cobblestone_to_molten_diamond_cobblestone",
             ModItems.TIER_DIAMOND_COBBLESTONE_DUST.get(),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "compressed_cobblestone_to_4mblava",
             ModBlocks.COMPRESSED_COBBLESTONE.get(),
            new FluidStack(Fluids.LAVA, 4),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "copper_compressed_cobblestone_to_32mblava",
             ModBlocks.TierCompressedStone.COPPER.getBlock().get(),
            new FluidStack(Fluids.LAVA, 32),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "iron_compressed_cobblestone_to_256mblava",
             ModBlocks.TierCompressedStone.IRON.getBlock().get(),
            new FluidStack(Fluids.LAVA, 256),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "gold_compressed_cobblestone_to_2048mblava",
             ModBlocks.TierCompressedStone.GOLD.getBlock().get(),
            new FluidStack(Fluids.LAVA, 2048),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "amethyst_compressed_cobblestone_to_16384mblava",
             ModBlocks.TierCompressedStone.AMETHYST.getBlock().get(),
            new FluidStack(Fluids.LAVA, 16384),
             26214400,
             4096
        ),
        new MelterRecipeDefinition(
            "aquamarine_compressed_cobblestone_to_64000mblava",
             ModBlocks.TierCompressedStone.AQUAMARINE.getBlock().get(),
            new FluidStack(Fluids.LAVA, 64000),
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
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private MelterRecipeDefinition(String recipeName, ItemLike ingredient, FluidStack fluidResult, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.fluidResult = fluidResult.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}