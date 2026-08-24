package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItemTags;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public final class CobblestoneCrusherRecipeDatagen {
    private static final String AE2_MOD_ID = "ae2";
    private static final long CRUSHER_TOTAL_CP = 800L;
    private static final long CRUSHER_CP_PER_TICK = 4L;

    private static final CrusherRecipeDefinition[] RECIPES = new CrusherRecipeDefinition[] {
        new CrusherRecipeDefinition("tier_copper_compressed_cobblestone_to_tier_copper_dirty_cobblestone_dust", ModBlocks.TierCompressedCobblestone.COPPER.getBlock().get(), ModItems.TIER_COPPER_COBBLESTONE_DIRTY_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_iron_compressed_cobblestone_to_tier_iron_dirty_cobblestone_dust", ModBlocks.TierCompressedCobblestone.IRON.getBlock().get(), ModItems.TIER_IRON_COBBLESTONE_DIRTY_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_gold_compressed_cobblestone_to_tier_gold_dirty_cobblestone_dust", ModBlocks.TierCompressedCobblestone.GOLD.getBlock().get(), ModItems.TIER_GOLD_COBBLESTONE_DIRTY_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_amethyst_compressed_cobblestone_to_tier_amethyst_dirty_cobblestone_dust", ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get(), ModItems.TIER_AMETHYST_COBBLESTONE_DIRTY_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("ancient_debris_to_ancient_debris_dust", Items.ANCIENT_DEBRIS, ModItems.ANCIENT_DEBRIS_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("ender_pearl_to_ender_dust", Items.ENDER_PEARL, ModItems.ENDER_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("blaze_rod_to_blaze_dust", Items.BLAZE_ROD, new ItemStack(Items.BLAZE_POWDER,4), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("coal_to_coal_dust", Items.COAL, ModItems.COAL_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("obsidian_to_obsidian_dust", Items.OBSIDIAN, new ItemStack(ModItems.OBSIDIAN_DUST.get(), 4), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("copper_ingot_to_copper_dust", Items.COPPER_INGOT, ModItems.COPPER_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("gold_ingot_to_gold_dust", Items.GOLD_INGOT, ModItems.GOLD_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("iron_ingot_to_iron_dust", Items.IRON_INGOT, ModItems.IRON_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("lapis_lazuli_to_lapis_dust", Items.LAPIS_LAZULI, ModItems.LAPIS_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("diamond_to_diamond_dust", Items.DIAMOND, ModItems.DIAMOND_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("emerald_to_emerald_dust", Items.EMERALD, ModItems.EMERALD_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("amethyst_shard_to_amethyst_dust", Items.AMETHYST_SHARD, ModItems.AMETHYST_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("aquamarine_shard_to_aquamarine_dust", ModItems.AQUAMARINE_SHARD.get(), ModItems.AQUAMARINE_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("topaz_shard_to_topaz_dust", ModItems.TOPAZ_SHARD.get(), ModItems.TOPAZ_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("ruby_shard_to_ruby_dust", ModItems.RUBY_SHARD.get(), ModItems.RUBY_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("sapphire_shard_to_sapphire_dust", ModItems.SAPPHIRE_SHARD.get(), ModItems.SAPPHIRE_DUST.get(), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("wool_to_string", Blocks.WHITE_WOOL, new ItemStack(Items.STRING, 4), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("cobblestone_wire_to_string", ModItems.COBBLESTONE_WIRE.get(), new ItemStack(Items.STRING, 1), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_copper_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.COPPER.getItem().get(), new ItemStack(Items.STRING, 2), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_iron_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.IRON.getItem().get(), new ItemStack(Items.STRING, 4), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_gold_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.GOLD.getItem().get(), new ItemStack(Items.STRING, 8), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_amethyst_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.AMETHYST.getItem().get(), new ItemStack(Items.STRING, 16), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_aquamarine_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.AQUAMARINE.getItem().get(), new ItemStack(Items.STRING, 32), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_topaz_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.TOPAZ.getItem().get(), new ItemStack(Items.STRING, 64), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_ruby_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.RUBY.getItem().get(), new ItemStack(Items.STRING, 64), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_sapphire_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.SAPPHIRE.getItem().get(), new ItemStack(Items.STRING, 64), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_diamond_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.DIAMOND.getItem().get(), new ItemStack(Items.STRING, 64), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_emerald_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.EMERALD.getItem().get(), new ItemStack(Items.STRING, 64), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_netherite_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.NETHERITE.getItem().get(), new ItemStack(Items.STRING, 64), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("tier_obsidian_cobblestone_wire_to_string", ModItems.TierCobblestoneWire.OBSIDIAN.getItem().get(), new ItemStack(Items.STRING, 64), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("coal_ore_to_coal", Blocks.COAL_ORE, new ItemStack(Items.COAL,2), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("redstone_ore_to_redstone", Blocks.REDSTONE_ORE, new ItemStack(Items.REDSTONE,6), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("lapis_ore_to_lapis_lazuli", Blocks.LAPIS_ORE, new ItemStack(Items.LAPIS_LAZULI,6), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("diamond_ore_to_diamond", Blocks.DIAMOND_ORE, new ItemStack(Items.DIAMOND,2), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("emerald_ore_to_emerald", Blocks.EMERALD_ORE, new ItemStack(Items.EMERALD,2), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("nether_quartz_ore_to_quartz", Blocks.NETHER_QUARTZ_ORE, new ItemStack(Items.QUARTZ,3), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("glowstone_to_glowstone_dust", Blocks.GLOWSTONE, new ItemStack(Items.GLOWSTONE_DUST,4), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("amethyst_to_amethyst_shard", Blocks.AMETHYST_BLOCK, new ItemStack(Items.AMETHYST_SHARD,4), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("nether_quartz_block_to_quartz", Blocks.QUARTZ_BLOCK, new ItemStack(Items.QUARTZ,4), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("blue_ice_to_packed_ice", Blocks.BLUE_ICE, new ItemStack(Items.PACKED_ICE,9), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("packed_ice_to_ice_block", Blocks.PACKED_ICE, new ItemStack(Items.ICE,9), CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("cobblestone_to_gravel", Items.COBBLESTONE, Items.GRAVEL, CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK),
        new CrusherRecipeDefinition("gravel_to_sand", Items.GRAVEL, Items.SAND, CRUSHER_TOTAL_CP, CRUSHER_CP_PER_TICK)
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

        registerAe2Recipes(output);
    }

    private static void registerAe2Recipes(RecipeOutput output) {
        // AE2 の刻印機レシピと同じく、入力 1 → ダスト 1 です。
        RecipeOutput ae2Output = output.withConditions(new ModLoadedCondition(AE2_MOD_ID));

        MachineRecipeOutputHelper.saveCobblestoneCrusherRecipe(
            ae2Output,
            "ae2_certus_quartz_to_certus_quartz_dust",
            Ingredient.of(ModItemTags.GEMS_CERTUS_QUARTZ),
            ResourceLocation.fromNamespaceAndPath(AE2_MOD_ID, "certus_quartz_dust"),
            1,
            CRUSHER_TOTAL_CP,
            CRUSHER_CP_PER_TICK
        );
        MachineRecipeOutputHelper.saveCobblestoneCrusherRecipe(
            ae2Output,
            "ae2_fluix_crystal_to_fluix_dust",
            Ingredient.of(ModItemTags.GEMS_FLUIX),
            ResourceLocation.fromNamespaceAndPath(AE2_MOD_ID, "fluix_dust"),
            1,
            CRUSHER_TOTAL_CP,
            CRUSHER_CP_PER_TICK
        );
        MachineRecipeOutputHelper.saveCobblestoneCrusherRecipe(
            ae2Output,
            "ae2_sky_stone_to_sky_dust",
            Ingredient.of(ModItemTags.AE2_SKY_STONE),
            ResourceLocation.fromNamespaceAndPath(AE2_MOD_ID, "sky_dust"),
            1,
            CRUSHER_TOTAL_CP,
            CRUSHER_CP_PER_TICK
        );
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
