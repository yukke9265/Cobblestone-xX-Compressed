package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItemTags;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

@SuppressWarnings("null")
public final class CobblestoneMixerRecipeDatagen {
    private static final MixerRecipeDefinition[] RECIPES = new MixerRecipeDefinition[] {
        new MixerRecipeDefinition("tier_iron_dirty_cobblestone_dust_and_coal_dust_to_tier_iron_cobblestone_mixed_dust", sizedItem(ModItems.TIER_IRON_COBBLESTONE_DIRTY_DUST.get()), sizedTag(ModItemTags.DUSTS_COAL), new ItemStack(ModItems.TIER_IRON_COBBLESTONE_MIXED_DUST.get()), 6400, 16),
        new MixerRecipeDefinition("tier_gold_cobblestone_dust_and_redstone_dust_to_tier_gold_cobblestone_mixed_dust", sizedItem(ModItems.TIER_GOLD_COBBLESTONE_DUST.get()), sizedItem(Items.REDSTONE), new ItemStack(ModItems.TIER_GOLD_COBBLESTONE_MIXED_DUST.get()), 6400, 16),
        new MixerRecipeDefinition("tier_amethyst_cobblestone_dust_and_amethyst_dust_to_tier_amethyst_cobblestone_mixed_dust", sizedItem(ModItems.TIER_AMETHYST_COBBLESTONE_DUST.get()), sizedTag(ModItemTags.DUSTS_AMETHYST), new ItemStack(ModItems.TIER_AMETHYST_COBBLESTONE_MIXED_DUST.get()), 6400, 16),
        new MixerRecipeDefinition("ancient_debris_dust_and_gold_dust_to_ancient_mixtures", sizedTag(ModItemTags.DUSTS_ANCIENT_DEBRIS), sizedTag(ModItemTags.DUSTS_GOLD), new ItemStack(ModItems.ANCIENT_MIXTURES.get(), 2), 6400, 16),
        new MixerRecipeDefinition("aquamarine_dust_and_lapis_dust_to_aquamarine_mixture", sizedTag(ModItemTags.DUSTS_AQUAMARINE), sizedTag(ModItemTags.DUSTS_LAPIS), new ItemStack(ModItems.AQUAMARINE_MIXTURE.get(), 2), 6400, 16),
        new MixerRecipeDefinition("blaze_powder_and_diamond_dust_to_blaze_mixtures", sizedItem(Items.BLAZE_POWDER, 2), sizedTag(ModItemTags.DUSTS_DIAMOND), new ItemStack(ModItems.BLAZE_MIXTURES.get(), 3), 6400, 16),
        new MixerRecipeDefinition("emerald_dust_and_ender_dust_to_emerald_mixtures", sizedTag(ModItemTags.DUSTS_EMERALD), sizedTag(ModItemTags.DUSTS_ENDER), new ItemStack(ModItems.EMERALD_MIXTURES.get(), 2), 6400, 16),
        new MixerRecipeDefinition("ruby_dust_and_redstone_to_ruby_mixtures", sizedTag(ModItemTags.DUSTS_RUBY), sizedItem(Items.REDSTONE), new ItemStack(ModItems.RUBY_MIXTURES.get(), 2), 6400, 16),
        new MixerRecipeDefinition("sapphire_dust_and_diamond_dust_to_sapphire_mixture", sizedTag(ModItemTags.DUSTS_SAPPHIRE), sizedTag(ModItemTags.DUSTS_DIAMOND), new ItemStack(ModItems.SAPPHIRE_MIXTURE.get(), 2), 6400, 16),
        new MixerRecipeDefinition("topaz_dust_and_glowstone_dust_to_topaz_mixtures", sizedTag(ModItemTags.DUSTS_TOPAZ), sizedItem(Items.GLOWSTONE_DUST), new ItemStack(ModItems.TOPAZ_MIXTURES.get(), 2), 6400, 16)
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

    private static SizedIngredient sizedItem(ItemLike item) {
        return sizedItem(item, 1);
    }

    private static SizedIngredient sizedItem(ItemLike item, int count) {
        return new SizedIngredient(Ingredient.of(item), count);
    }

    private static SizedIngredient sizedTag(TagKey<Item> tag) {
        return sizedTag(tag, 1);
    }

    private static SizedIngredient sizedTag(TagKey<Item> tag, int count) {
        // common dust タグを使って、他 mod の粉も同じレシピ入力として扱えるようにします。
        return new SizedIngredient(Ingredient.of(tag), count);
    }

    private static class MixerRecipeDefinition {
        private final String recipeName;
        private final SizedIngredient firstIngredient;
        private final SizedIngredient secondIngredient;
        private final ItemStack result;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private MixerRecipeDefinition(String recipeName, SizedIngredient firstIngredient, SizedIngredient secondIngredient, ItemStack result, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.firstIngredient = firstIngredient;
            this.secondIngredient = secondIngredient;
            this.result = result.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}