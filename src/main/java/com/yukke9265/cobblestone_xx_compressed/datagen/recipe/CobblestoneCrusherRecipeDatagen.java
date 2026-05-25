package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public final class CobblestoneCrusherRecipeDatagen {
    private static final CrusherRecipeDefinition[] RECIPES = new CrusherRecipeDefinition[] {
        new CrusherRecipeDefinition("tier_copper_compressed_cobblestone_to_tier_copper_dirty_cobblestone_dust", ModBlocks.TierCompressedCobblestone.COPPER.getBlock().get(), ModItems.TIER_COPPER_COBBLESTONE_DIRTY_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("tier_iron_compressed_cobblestone_to_tier_iron_dirty_cobblestone_dust", ModBlocks.TierCompressedCobblestone.IRON.getBlock().get(), ModItems.TIER_IRON_COBBLESTONE_DIRTY_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("tier_gold_compressed_cobblestone_to_tier_gold_dirty_cobblestone_dust", ModBlocks.TierCompressedCobblestone.GOLD.getBlock().get(), ModItems.TIER_GOLD_COBBLESTONE_DIRTY_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("tier_amethyst_compressed_cobblestone_to_tier_amethyst_dirty_cobblestone_dust", ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get(), ModItems.TIER_AMETHYST_COBBLESTONE_DIRTY_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("ancient_debris_to_ancient_debris_dust", Items.ANCIENT_DEBRIS, ModItems.ANCIENT_DEBRIS_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("ender_pearl_to_ender_dust", Items.ENDER_PEARL, ModItems.ENDER_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("blaze_rod_to_blaze_dust", Items.BLAZE_ROD, new ItemStack(Items.BLAZE_POWDER,4), 800, 4),
        new CrusherRecipeDefinition("coal_to_coal_dust", Items.COAL, ModItems.COAL_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("obsidian_to_obsidian_dust", Items.OBSIDIAN, new ItemStack(ModItems.OBSIDIAN_DUST.get(), 4), 800, 4),
        new CrusherRecipeDefinition("copper_ingot_to_copper_dust", Items.COPPER_INGOT, ModItems.COPPER_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("gold_ingot_to_gold_dust", Items.GOLD_INGOT, ModItems.GOLD_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("iron_ingot_to_iron_dust", Items.IRON_INGOT, ModItems.IRON_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("lapis_lazuli_to_lapis_dust", Items.LAPIS_LAZULI, ModItems.LAPIS_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("diamond_to_diamond_dust", Items.DIAMOND, ModItems.DIAMOND_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("emerald_to_emerald_dust", Items.EMERALD, ModItems.EMERALD_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("amethyst_shard_to_amethyst_dust", Items.AMETHYST_SHARD, ModItems.AMETHYST_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("aquamarine_shard_to_aquamarine_dust", ModItems.AQUAMARINE_SHARD.get(), ModItems.AQUAMARINE_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("topaz_shard_to_topaz_dust", ModItems.TOPAZ_SHARD.get(), ModItems.TOPAZ_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("ruby_shard_to_ruby_dust", ModItems.RUBY_SHARD.get(), ModItems.RUBY_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("sapphire_shard_to_sapphire_dust", ModItems.SAPPHIRE_SHARD.get(), ModItems.SAPPHIRE_DUST.get(), 800, 4),
        new CrusherRecipeDefinition("wool_to_string", Blocks.WHITE_WOOL, new ItemStack(Items.STRING, 4), 800, 4),
        new CrusherRecipeDefinition("compressed_cobblestone_to_string", ModBlocks.COMPRESSED_COBBLESTONE.get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_copper_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.COPPER.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_iron_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.IRON.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_gold_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.GOLD.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_amethyst_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.AMETHYST.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_aquamarine_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.AQUAMARINE.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_topaz_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.TOPAZ.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_ruby_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.RUBY.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_sapphire_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.SAPPHIRE.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_diamond_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.DIAMOND.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_emerald_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.EMERALD.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_netherite_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.NETHERITE.getItem().get(), Items.STRING, 800, 4),
        new CrusherRecipeDefinition("tier_obsidian_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.OBSIDIAN.getItem().get(), Items.STRING, 800, 4)
    };

    private CobblestoneCrusherRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (CrusherRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneCrusherRecipe(
                output,
                recipe.recipeName,
                recipe.ingredient,
                recipe.result,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static class CrusherRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemStack result;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private CrusherRecipeDefinition(String recipeName, ItemLike ingredient, ItemLike result, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this(recipeName, ingredient, new ItemStack(result), totalCobblestonePower, cobblestonePowerPerTick);
        }

        private CrusherRecipeDefinition(String recipeName, ItemLike ingredient, ItemStack result, long totalCobblestonePower, long cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.result = result.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}