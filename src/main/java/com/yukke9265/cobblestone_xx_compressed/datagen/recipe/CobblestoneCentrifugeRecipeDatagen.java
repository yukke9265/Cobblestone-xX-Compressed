package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class CobblestoneCentrifugeRecipeDatagen {
    private static final CentrifugeRecipeDefinition[] RECIPES = new CentrifugeRecipeDefinition[] {
        new CentrifugeRecipeDefinition("cobblestone_dust_to_cobblestone_pure_dust_and_dirty_mixture", ModItems.COBBLESTONE_DUST.get(), new ItemStack(ModItems.COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_copper_cobblestone_dust_to_tier_copper_cobblestone_pure_dust_and_dirty_mixture", ModItems.TIER_COPPER_COBBLESTONE_DUST.get(), new ItemStack(ModItems.TIER_COPPER_COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_iron_cobblestone_dust_to_tier_iron_cobblestone_pure_dust_and_dirty_mixture", ModItems.TIER_IRON_COBBLESTONE_DUST.get(), new ItemStack(ModItems.TIER_IRON_COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_gold_dirty_cobblestone_dust_to_tier_gold_cobblestone_dust_and_dirty_mixture", ModItems.TIER_GOLD_COBBLESTONE_DIRTY_DUST.get(), new ItemStack(ModItems.TIER_GOLD_COBBLESTONE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_amethyst_dirty_cobblestone_dust_to_tier_amethyst_cobblestone_dust_and_dirty_mixture", ModItems.TIER_AMETHYST_COBBLESTONE_DIRTY_DUST.get(), new ItemStack(ModItems.TIER_AMETHYST_COBBLESTONE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_aquamarine_cobblestone_dust_to_tier_aquamarine_cobblestone_pure_dust_and_dirty_mixture", ModItems.TIER_AQUAMARINE_COBBLESTONE_DUST.get(), new ItemStack(ModItems.TIER_AQUAMARINE_COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_topaz_cobblestone_dust_to_tier_topaz_cobblestone_pure_dust_and_dirty_mixture", ModItems.TIER_TOPAZ_COBBLESTONE_DUST.get(), new ItemStack(ModItems.TIER_TOPAZ_COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_ruby_cobblestone_dust_to_tier_ruby_cobblestone_pure_dust_and_dirty_mixture", ModItems.TIER_RUBY_COBBLESTONE_DUST.get(), new ItemStack(ModItems.TIER_RUBY_COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_sapphire_cobblestone_dust_to_tier_sapphire_cobblestone_pure_dust_and_dirty_mixture", ModItems.TIER_SAPPHIRE_COBBLESTONE_DUST.get(), new ItemStack(ModItems.TIER_SAPPHIRE_COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_diamond_cobblestone_dust_to_tier_diamond_cobblestone_pure_dust_and_dirty_mixture", ModItems.TIER_DIAMOND_COBBLESTONE_DUST.get(), new ItemStack(ModItems.TIER_DIAMOND_COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_emerald_cobblestone_dust_to_tier_emerald_cobblestone_pure_dust_and_dirty_mixture", ModItems.TIER_EMERALD_COBBLESTONE_DUST.get(), new ItemStack(ModItems.TIER_EMERALD_COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_netherite_cobblestone_dust_to_tier_netherite_cobblestone_pure_dust_and_dirty_mixture", ModItems.TIER_NETHERITE_COBBLESTONE_DUST.get(), new ItemStack(ModItems.TIER_NETHERITE_COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64),
        new CentrifugeRecipeDefinition("tier_obsidian_cobblestone_dust_to_tier_obsidian_cobblestone_pure_dust_and_dirty_mixture", ModItems.TIER_OBSIDIAN_COBBLESTONE_DUST.get(), new ItemStack(ModItems.TIER_OBSIDIAN_COBBLESTONE_PURE_DUST.get()), 1.0F, new ItemStack(ModItems.DIRTY_MIXTURE.get()), 0.1F, 51200, 64)
    };

    private CobblestoneCentrifugeRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (CentrifugeRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneCentrifugeRecipe(
                output,
                recipe.recipeName,
                recipe.ingredient,
                recipe.firstResult,
                recipe.firstResultChance,
                recipe.secondResult,
                recipe.secondResultChance,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static class CentrifugeRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemStack firstResult;
        private final float firstResultChance;
        private final ItemStack secondResult;
        private final float secondResultChance;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private CentrifugeRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            ItemStack firstResult,
            float firstResultChance,
            ItemStack secondResult,
            float secondResultChance,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.firstResult = firstResult.copy();
            this.firstResultChance = firstResultChance;
            this.secondResult = secondResult.copy();
            this.secondResultChance = secondResultChance;
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}