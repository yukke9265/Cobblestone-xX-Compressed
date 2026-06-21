package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneDissolutionChamberRecipeDatagen {
    private static final DissolutionChamberRecipeDefinition[] RECIPES = new DissolutionChamberRecipeDefinition[] {
        new DissolutionChamberRecipeDefinition(
            "tier_ruby_compressed_cobblestone_and_redruby_to_molten_tier_ruby_dirty_compressed_cobblestone",
            ModBlocks.TierCompressedCobblestone.RUBY.getBlock().get(),
            new FluidStack(ModFluids.WaterBasedFluid.RED_RUBY.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.RUBY.getFluidEntry().getStillFluid().get(), 1000),
            209715200,
            16384
        ),
        new DissolutionChamberRecipeDefinition(
            "tier_sapphire_compressed_cobblestone_and_shinywater_to_molten_tier_sapphire_dirty_compressed_cobblestone",
            ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock().get(),
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.SAPPHIRE.getFluidEntry().getStillFluid().get(), 1000),
            209715200,
            16384
        ),
        new DissolutionChamberRecipeDefinition(
            "tier_diamond_compressed_cobblestone_and_hot_lava_to_molten_tier_diamond_dirty_compressed_cobblestone",
            ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock().get(),
            new FluidStack(ModFluids.WaterBasedFluid.HOT_LAVA.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            209715200,
            16384
        ),
        new DissolutionChamberRecipeDefinition(
            "tier_emerald_compressed_cobblestone_and_hot_lava_to_molten_tier_emerald_dirty_compressed_cobblestone",
            ModBlocks.TierCompressedCobblestone.EMERALD.getBlock().get(),
            new FluidStack(ModFluids.WaterBasedFluid.HOT_UNSTABLE_EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            209715200,
            16384
        ),
        new DissolutionChamberRecipeDefinition(
            "tier_netherite_compressed_cobblestone_and_hot_lava_to_molten_tier_netherite_dirty_compressed_cobblestone",
            ModBlocks.TierCompressedCobblestone.NETHERITE.getBlock().get(),
            new FluidStack(ModFluids.WaterBasedFluid.HOT_UNSTABLE_EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.NETHERITE.getFluidEntry().getStillFluid().get(), 1000),
            209715200,
            16384
        ),
        new DissolutionChamberRecipeDefinition(
            "tier_obsidian_compressed_cobblestone_and_hot_lava_to_molten_tier_obsidian_dirty_compressed_cobblestone",
            ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock().get(),
            new FluidStack(ModFluids.WaterBasedFluid.HOT_UNSTABLE_EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.OBSIDIAN.getFluidEntry().getStillFluid().get(), 1000),
            209715200,
            16384
        ),
        new DissolutionChamberRecipeDefinition(
            "topazmixture_and_lava_to_glowtopz",
            new ItemStack(ModItems.TOPAZ_MIXTURES.get(), 16),
            new FluidStack(Fluids.LAVA, 1000),
            new FluidStack(ModFluids.WaterBasedFluid.GLOW_TOPAZ.getFluidEntry().getStillFluid().get(), 16000),
            209715200,
            16384
        ),
        new DissolutionChamberRecipeDefinition(
            "rubymixture_and_lava_to_glowtopz",
            new ItemStack(ModItems.RUBY_MIXTURES.get(), 16),
            new FluidStack(Fluids.LAVA, 1000),
            new FluidStack(ModFluids.WaterBasedFluid.RED_RUBY.getFluidEntry().getStillFluid().get(), 16000),
            209715200,
            16384
        ),
        new DissolutionChamberRecipeDefinition(
            "sapphire_mixture_and_lava_to_glowtopz",
            new ItemStack(Items.PACKED_ICE, 16),
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_SAPPHIRE.getFluidEntry().getStillFluid().get(), 16000),
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid().get(), 16000),
            209715200,
            16384
        )
    };

    private CobblestoneDissolutionChamberRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (DissolutionChamberRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneDissolutionChamberRecipe(output, recipe.recipeName, recipe.ingredient, recipe.fluidInput, recipe.fluidOutput, recipe.totalCobblestonePower, recipe.cobblestonePowerPerTick);
        }
    }

    private static SizedIngredient sizedItem(ItemLike item) {
        return sizedItem(item, 1);
    }

    private static SizedIngredient sizedItem(ItemLike item, int count) {
        return new SizedIngredient(Ingredient.of(item), count);
    }

    private static class DissolutionChamberRecipeDefinition {
        private final String recipeName;
        private final SizedIngredient ingredient;
        private final FluidStack fluidInput;
        private final FluidStack fluidOutput;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private DissolutionChamberRecipeDefinition(String recipeName, ItemLike ingredient, FluidStack fluidInput, FluidStack fluidOutput, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this(recipeName, sizedItem(ingredient), fluidInput, fluidOutput, totalCobblestonePower, cobblestonePowerPerTick);
        }

        private DissolutionChamberRecipeDefinition(String recipeName, ItemStack ingredient, FluidStack fluidInput, FluidStack fluidOutput, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this(recipeName, sizedItem(ingredient.getItem(), ingredient.getCount()), fluidInput, fluidOutput, totalCobblestonePower, cobblestonePowerPerTick);
        }

        private DissolutionChamberRecipeDefinition(String recipeName, SizedIngredient ingredient, FluidStack fluidInput, FluidStack fluidOutput, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.fluidInput = fluidInput.copy();
            this.fluidOutput = fluidOutput.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}