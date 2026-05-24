package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.world.level.ItemLike;

final class CompressedStoneMachineRecipeData {
    public static final MachineRecipeDefinition[] RECIPES = new MachineRecipeDefinition[] {
        new MachineRecipeDefinition("compressed_cobblestone_to_compressed_stone", ModBlocks.COMPRESSED_COBBLESTONE.get(), ModBlocks.COMPRESSED_STONE.get(), 100),
        new MachineRecipeDefinition("tier_copper_dirty_compressed_cobblestone_dust_to_tier_copper_compressed_stone", ModItems.TIER_COPPER_COBBLESTONE_DIRTY_DUST.get(), ModBlocks.TierCompressedStone.COPPER.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_iron_compressed_cobblestone_mixed_dust_to_tier_iron_compressed_stone", ModItems.TIER_IRON_COBBLESTONE_MIXED_DUST.get(), ModBlocks.TierCompressedStone.IRON.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_gold_compressed_cobblestone_mixed_dust_to_tier_gold_compressed_stone", ModItems.TIER_GOLD_COBBLESTONE_MIXED_DUST.get(), ModBlocks.TierCompressedStone.GOLD.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_amethyst_compressed_cobblestone_mixed_dust_to_tier_amethyst_compressed_stone", ModItems.TIER_AMETHYST_COBBLESTONE_MIXED_DUST.get(), ModBlocks.TierCompressedStone.AMETHYST.getBlock().get(), 100),
        new MachineRecipeDefinition("compressed_cobblestone_pure_dust_to_compressed_stone", ModItems.COBBLESTONE_PURE_DUST.get(), ModBlocks.COMPRESSED_STONE.get(), 100),
        new MachineRecipeDefinition("tier_copper_compressed_cobblestone_pure_dust_to_tier_copper_compressed_stone", ModItems.TIER_COPPER_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.COPPER.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_iron_compressed_cobblestone_pure_dust_to_tier_iron_compressed_stone", ModItems.TIER_IRON_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.IRON.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_gold_compressed_cobblestone_pure_dust_to_tier_gold_compressed_stone", ModItems.TIER_GOLD_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.GOLD.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_amethyst_compressed_cobblestone_pure_dust_to_tier_amethyst_compressed_stone", ModItems.TIER_AMETHYST_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.AMETHYST.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_aquamarine_compressed_cobblestone_pure_dust_to_tier_aquamarine_compressed_stone", ModItems.TIER_AQUAMARINE_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.AQUAMARINE.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_topaz_compressed_cobblestone_pure_dust_to_tier_topaz_compressed_stone", ModItems.TIER_TOPAZ_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.TOPAZ.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_ruby_compressed_cobblestone_pure_dust_to_tier_ruby_compressed_stone", ModItems.TIER_RUBY_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.RUBY.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_sapphire_compressed_cobblestone_pure_dust_to_tier_sapphire_compressed_stone", ModItems.TIER_SAPPHIRE_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.SAPPHIRE.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_diamond_compressed_cobblestone_pure_dust_to_tier_diamond_compressed_stone", ModItems.TIER_DIAMOND_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.DIAMOND.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_emerald_compressed_cobblestone_pure_dust_to_tier_emerald_compressed_stone", ModItems.TIER_EMERALD_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.EMERALD.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_netherite_compressed_cobblestone_pure_dust_to_tier_netherite_compressed_stone", ModItems.TIER_NETHERITE_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.NETHERITE.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_obsidian_compressed_cobblestone_pure_dust_to_tier_obsidian_compressed_stone", ModItems.TIER_OBSIDIAN_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.OBSIDIAN.getBlock().get(), 100)
    };

    private CompressedStoneMachineRecipeData() {
    }

    public static class MachineRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemLike result;
        private final int processingTime;

        public MachineRecipeDefinition(String recipeName, ItemLike ingredient, ItemLike result, int processingTime) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.result = result;
            this.processingTime = processingTime;
        }

        public String getRecipeName() {
            return this.recipeName;
        }

        public ItemLike getIngredient() {
            return this.ingredient;
        }

        public ItemLike getResult() {
            return this.result;
        }

        public int getProcessingTime() {
            return this.processingTime;
        }
    }
}