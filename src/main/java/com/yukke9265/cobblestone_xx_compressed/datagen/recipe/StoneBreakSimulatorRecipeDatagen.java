package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.loot.CompressedStoneLootDefinition;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public final class StoneBreakSimulatorRecipeDatagen {
    private static final String MEKANISM_MOD_ID = "mekanism";
    private static final String MEKANISM_EXTRAS_MOD_ID = "mekanism_extras";

    // 各段は合計CP×8、CP/t×4、処理tick×2。
    private static final RecipePower BASE_POWER = new RecipePower(100L, 1L);
    private static final RecipePower COPPER_POWER = new RecipePower(800L, 4L);
    private static final RecipePower IRON_POWER = new RecipePower(6400L, 16L);
    private static final RecipePower GOLD_POWER = new RecipePower(51200L, 64L);
    private static final RecipePower AMETHYST_POWER = new RecipePower(409600L, 256L);
    private static final RecipePower AQUAMARINE_POWER = new RecipePower(3276800L, 1024L);
    private static final RecipePower TOPAZ_POWER = new RecipePower(26214400L, 4096L);
    private static final RecipePower RUBY_POWER = new RecipePower(209715200L, 16384L);
    private static final RecipePower SAPPHIRE_POWER = new RecipePower(1677721600L, 65536L);
    private static final RecipePower DIAMOND_POWER = new RecipePower(13421772800L, 262144L);
    private static final RecipePower EMERALD_POWER = new RecipePower(MachineRecipePowerTiers.CHEMICAL_REACTOR_TOTAL_CP, 1048576L);
    private static final RecipePower NETHERITE_POWER = new RecipePower(MachineRecipePowerTiers.ASSEMBLY_MACHINE_TOTAL_CP, 4194304L);
    private static final RecipePower OBSIDIAN_POWER = new RecipePower(MachineRecipePowerTiers.EXTREME_COMPRESSOR_TOTAL_CP, 16777216L);
    private static final RecipePower ORE_POWER = COPPER_POWER;

    private StoneBreakSimulatorRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (CompressedStoneLootDefinition definition : CompressedStoneLootDefinition.getDefinitions()) {
            Block stoneBlock = definition.getStoneBlock().get();
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(stoneBlock);
            RecipePower recipePower = getRecipePower(stoneBlock);

            MachineRecipeOutputHelper.saveStoneBreakSimulatorRecipe(
                output,
                blockId.getPath(),
                stoneBlock,
                recipePower.totalCobblestonePower(),
                recipePower.cobblestonePowerPerTick()
            );
        }

        registerVanillaOreRecipes(output);
        registerModOreRecipes(output);
    }

    private static void registerVanillaOreRecipes(RecipeOutput output) {
        saveOreItemRecipe(output, "coal_ore", Blocks.COAL_ORE);
        saveOreItemRecipe(output, "deepslate_coal_ore", Blocks.DEEPSLATE_COAL_ORE);
        saveOreItemRecipe(output, "iron_ore", Blocks.IRON_ORE);
        saveOreItemRecipe(output, "deepslate_iron_ore", Blocks.DEEPSLATE_IRON_ORE);
        saveOreItemRecipe(output, "copper_ore", Blocks.COPPER_ORE);
        saveOreItemRecipe(output, "deepslate_copper_ore", Blocks.DEEPSLATE_COPPER_ORE);
        saveOreItemRecipe(output, "gold_ore", Blocks.GOLD_ORE);
        saveOreItemRecipe(output, "deepslate_gold_ore", Blocks.DEEPSLATE_GOLD_ORE);
        saveOreItemRecipe(output, "redstone_ore", Blocks.REDSTONE_ORE);
        saveOreItemRecipe(output, "deepslate_redstone_ore", Blocks.DEEPSLATE_REDSTONE_ORE);
        saveOreItemRecipe(output, "emerald_ore", Blocks.EMERALD_ORE);
        saveOreItemRecipe(output, "deepslate_emerald_ore", Blocks.DEEPSLATE_EMERALD_ORE);
        saveOreItemRecipe(output, "lapis_ore", Blocks.LAPIS_ORE);
        saveOreItemRecipe(output, "deepslate_lapis_ore", Blocks.DEEPSLATE_LAPIS_ORE);
        saveOreItemRecipe(output, "diamond_ore", Blocks.DIAMOND_ORE);
        saveOreItemRecipe(output, "deepslate_diamond_ore", Blocks.DEEPSLATE_DIAMOND_ORE);
        saveOreItemRecipe(output, "nether_gold_ore", Blocks.NETHER_GOLD_ORE);
        saveOreItemRecipe(output, "nether_quartz_ore", Blocks.NETHER_QUARTZ_ORE);
        saveOreItemRecipe(output, "ancient_debris", Blocks.ANCIENT_DEBRIS);
    }

    private static void registerModOreRecipes(RecipeOutput output) {
        RecipeOutput mekanismOutput = output.withConditions(new ModLoadedCondition(MEKANISM_MOD_ID));
        saveOreTagRecipe(mekanismOutput, "mekanism_tin_ores", commonOreTag("tin"));
        saveOreTagRecipe(mekanismOutput, "mekanism_osmium_ores", commonOreTag("osmium"));
        saveOreTagRecipe(mekanismOutput, "mekanism_lead_ores", commonOreTag("lead"));
        saveOreTagRecipe(mekanismOutput, "mekanism_uranium_ores", commonOreTag("uranium"));
        saveOreTagRecipe(mekanismOutput, "mekanism_fluorite_ores", commonOreTag("fluorite"));

        RecipeOutput extrasOutput = output.withConditions(new ModLoadedCondition(MEKANISM_EXTRAS_MOD_ID));
        saveOreTagRecipe(extrasOutput, "mekanism_extras_naquadah_ores", commonOreTag("naquadah"));
        saveOreTagRecipe(extrasOutput, "mekanism_extras_end_naquadah_ores", commonOreTag("end_naquadah"));
    }

    private static void saveOreItemRecipe(RecipeOutput output, String recipeName, Block oreBlock) {
        MachineRecipeOutputHelper.saveStoneBreakSimulatorRecipe(
            output,
            recipeName,
            oreBlock,
            ORE_POWER.totalCobblestonePower(),
            ORE_POWER.cobblestonePowerPerTick()
        );
    }

    private static void saveOreTagRecipe(RecipeOutput output, String recipeName, TagKey<Item> oreTag) {
        MachineRecipeOutputHelper.saveStoneBreakSimulatorRecipe(
            output,
            recipeName,
            Ingredient.of(oreTag),
            ORE_POWER.totalCobblestonePower(),
            ORE_POWER.cobblestonePowerPerTick()
        );
    }

    private static TagKey<Item> commonOreTag(String orePath) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores/" + orePath));
    }

    private static RecipePower getRecipePower(Block stoneBlock) {
        if (stoneBlock == ModBlocks.COMPRESSED_STONE.get()) {
            return BASE_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.COPPER.getBlock().get()) {
            return COPPER_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.IRON.getBlock().get()) {
            return IRON_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.GOLD.getBlock().get()) {
            return GOLD_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.AMETHYST.getBlock().get()) {
            return AMETHYST_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.AQUAMARINE.getBlock().get()) {
            return AQUAMARINE_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.TOPAZ.getBlock().get()) {
            return TOPAZ_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.RUBY.getBlock().get()) {
            return RUBY_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.SAPPHIRE.getBlock().get()) {
            return SAPPHIRE_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.DIAMOND.getBlock().get()) {
            return DIAMOND_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.EMERALD.getBlock().get()) {
            return EMERALD_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.NETHERITE.getBlock().get()) {
            return NETHERITE_POWER;
        }

        if (stoneBlock == ModBlocks.TierCompressedStone.OBSIDIAN.getBlock().get()) {
            return OBSIDIAN_POWER;
        }

        throw new IllegalArgumentException("Stone Break Simulator の CP 値が未定義の圧縮石です: " + blockId(stoneBlock));
    }

    private static String blockId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    private record RecipePower(long totalCobblestonePower, long cobblestonePowerPerTick) {
    }
}
