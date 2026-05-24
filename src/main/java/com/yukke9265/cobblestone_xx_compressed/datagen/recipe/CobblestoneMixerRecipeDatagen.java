package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class CobblestoneMixerRecipeDatagen {
    private static final MixerRecipeDefinition[] RECIPES = new MixerRecipeDefinition[] {
        new MixerRecipeDefinition("tier_iron_dirty_cobblestone_dust_and_coal_dust_to_tier_iron_cobblestone_mixed_dust", new ItemStack(ModItems.TIER_IRON_COBBLESTONE_DIRTY_DUST.get()), new ItemStack(ModItems.COAL_DUST.get()), new ItemStack(ModItems.TIER_IRON_COBBLESTONE_MIXED_DUST.get()), 6400, 16),
        new MixerRecipeDefinition("tier_gold_cobblestone_dust_and_redstone_dust_to_tier_gold_cobblestone_mixed_dust", new ItemStack(ModItems.TIER_GOLD_COBBLESTONE_DUST.get()), new ItemStack(Items.REDSTONE), new ItemStack(ModItems.TIER_GOLD_COBBLESTONE_MIXED_DUST.get()), 6400, 16),
        new MixerRecipeDefinition("tier_amethyst_cobblestone_dust_and_amethyst_dust_to_tier_amethyst_cobblestone_mixed_dust", new ItemStack(ModItems.TIER_AMETHYST_COBBLESTONE_DUST.get()), new ItemStack(ModItems.AMETHYST_DUST.get()), new ItemStack(ModItems.TIER_AMETHYST_COBBLESTONE_MIXED_DUST.get()), 6400, 16),
        new MixerRecipeDefinition("ancient_debris_dust_and_gold_dust_to_ancient_mixtures", new ItemStack(ModItems.ANCIENT_DEBRIS_DUST.get()), new ItemStack(ModItems.GOLD_DUST.get()), new ItemStack(ModItems.ANCIENT_MIXTURES.get(), 2), 6400, 16),
        new MixerRecipeDefinition("aquamarine_dust_and_lapis_dust_to_aquamarine_mixture", new ItemStack(ModItems.AQUAMARINE_DUST.get()), new ItemStack(ModItems.LAPIS_DUST.get()), new ItemStack(ModItems.AQUAMARINE_MIXTURE.get(), 2), 6400, 16),
        new MixerRecipeDefinition("blaze_powder_and_diamond_dust_to_blaze_mixtures", new ItemStack(Items.BLAZE_POWDER, 2), new ItemStack(ModItems.DIAMOND_DUST.get()), new ItemStack(ModItems.BLAZE_MIXTURES.get(), 3), 6400, 16),
        new MixerRecipeDefinition("emerald_dust_and_ender_dust_to_emerald_mixtures", new ItemStack(ModItems.EMERALD_DUST.get()), new ItemStack(ModItems.ENDER_DUST.get()), new ItemStack(ModItems.EMERALD_MIXTURES.get(), 2), 6400, 16),
        new MixerRecipeDefinition("ruby_dust_and_redstone_to_ruby_mixtures", new ItemStack(ModItems.RUBY_DUST.get()), new ItemStack(Items.REDSTONE), new ItemStack(ModItems.RUBY_MIXTURES.get(), 2), 6400, 16),
        new MixerRecipeDefinition("sapphire_dust_and_diamond_dust_to_sapphire_mixture", new ItemStack(ModItems.SAPPHIRE_DUST.get()), new ItemStack(ModItems.DIAMOND_DUST.get()), new ItemStack(ModItems.SAPPHIRE_MIXTURE.get(), 2), 6400, 16),
        new MixerRecipeDefinition("topaz_dust_and_glowstone_dust_to_topaz_mixtures", new ItemStack(ModItems.TOPAZ_DUST.get()), new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(ModItems.TOPAZ_MIXTURES.get(), 2), 6400, 16)
    };

    private CobblestoneMixerRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (MixerRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneMixerRecipe(
                output,
                recipe.recipeName,
                recipe.firstIngredient,
                recipe.secondIngredient,
                recipe.result,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static class MixerRecipeDefinition {
        private final String recipeName;
        private final ItemStack firstIngredient;
        private final ItemStack secondIngredient;
        private final ItemStack result;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private MixerRecipeDefinition(String recipeName, ItemStack firstIngredient, ItemStack secondIngredient, ItemStack result, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.firstIngredient = firstIngredient.copy();
            this.secondIngredient = secondIngredient.copy();
            this.result = result.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}