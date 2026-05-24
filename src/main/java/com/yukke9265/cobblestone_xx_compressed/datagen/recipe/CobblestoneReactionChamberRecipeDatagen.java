package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneReactionChamberRecipeDatagen {
    private static final ReactionChamberRecipeDefinition[] RECIPES = new ReactionChamberRecipeDefinition[] {
        new ReactionChamberRecipeDefinition(
            "water_and_aquamarine_mixture_and_aquamarine_cobblestone_to_aquamarine_cobblestone_dust",
            new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 1000),
            ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock().get(),
            ModItems.AQUAMARINE_MIXTURE.get(),
            new ItemStack(ModItems.TIER_AQUAMARINE_COBBLESTONE_DUST.get()),
            3276800,
            1024
        ),
        new ReactionChamberRecipeDefinition(
            "glowtopaz_and_topaz_cobblestone_and_amethyst_dust_to_topaz_cobblestone_dust",
            new FluidStack(ModFluids.WaterBasedFluid.GLOW_TOPAZ.getFluidEntry().getStillFluid(), 1000),
            ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock().get(),
            ModItems.AMETHYST_DUST.get(),
            new ItemStack(ModItems.TIER_TOPAZ_COBBLESTONE_DUST.get()),
            3276800,
            1024
        ),
        new ReactionChamberRecipeDefinition(
            "molten_dirty_ruby_cobblestone_and_obsidian_dust_and_amethyst_dust_to_ruby_cobblestone_dust",
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.RUBY.getFluidEntry().getStillFluid(), 1000),
            ModItems.OBSIDIAN_DUST.get(),
            ModItems.AMETHYST_DUST.get(),
            new ItemStack(ModItems.TIER_RUBY_COBBLESTONE_DUST.get()),
            3276800,
            1024
        ),
        new ReactionChamberRecipeDefinition(
            "molten_dirty_sapphire_cobblestone_and_obsidian_dust_and_amethyst_dust_to_sapphire_cobblestone_dust",
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.SAPPHIRE.getFluidEntry().getStillFluid(), 1000),
            ModItems.OBSIDIAN_DUST.get(),
            ModItems.AMETHYST_DUST.get(),
            new ItemStack(ModItems.TIER_SAPPHIRE_COBBLESTONE_DUST.get()),
            3276800,
            1024
        ),
        new ReactionChamberRecipeDefinition(
            "shiny_water_and_dirty_diamond_cobblestone_and_redstone_to_diamond_cobblestone_dust",
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid(), 1000),
            ModItems.TIER_DIAMOND_COBBLESTONE_DIRTY_DUST.get(),
            Items.REDSTONE,
            new ItemStack(ModItems.TIER_DIAMOND_COBBLESTONE_DUST.get()),
            3276800,
            1024
        ),
        new ReactionChamberRecipeDefinition(
            "shiny_water_and_dirty_emerald_cobblestone_and_redstone_to_emerald_cobblestone_dust",
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid(), 1000),
            ModItems.TIER_EMERALD_COBBLESTONE_DIRTY_DUST.get(),
            Items.REDSTONE,
            new ItemStack(ModItems.TIER_EMERALD_COBBLESTONE_DUST.get()),
            3276800,
            1024
        ),
        new ReactionChamberRecipeDefinition(
            "shiny_water_and_dirty_netherite_cobblestone_and_redstone_to_netherite_cobblestone_dust",
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid(), 1000),
            ModItems.TIER_NETHERITE_COBBLESTONE_DIRTY_DUST.get(),
            Items.REDSTONE,
            new ItemStack(ModItems.TIER_NETHERITE_COBBLESTONE_DUST.get()),
            3276800,
            1024
        ),
        new ReactionChamberRecipeDefinition(
            "shiny_water_and_dirty_obsidian_cobblestone_and_redstone_to_obsidian_cobblestone_dust",
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid(), 1000),
            ModItems.TIER_OBSIDIAN_COBBLESTONE_DIRTY_DUST.get(),
            Items.REDSTONE,
            new ItemStack(ModItems.TIER_OBSIDIAN_COBBLESTONE_DUST.get()),
            3276800,
            1024
        )
    };

    private CobblestoneReactionChamberRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (ReactionChamberRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneReactionChamberRecipe(
                output,
                recipe.recipeName,
                recipe.fluidInput,
                recipe.firstIngredient,
                recipe.secondIngredient,
                recipe.result,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static class ReactionChamberRecipeDefinition {
        private final String recipeName;
        private final FluidStack fluidInput;
        private final ItemLike firstIngredient;
        private final ItemLike secondIngredient;
        private final ItemStack result;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private ReactionChamberRecipeDefinition(String recipeName, FluidStack fluidInput, ItemLike firstIngredient, ItemLike secondIngredient, ItemStack result, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.fluidInput = fluidInput.copy();
            this.firstIngredient = firstIngredient;
            this.secondIngredient = secondIngredient;
            this.result = result.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}