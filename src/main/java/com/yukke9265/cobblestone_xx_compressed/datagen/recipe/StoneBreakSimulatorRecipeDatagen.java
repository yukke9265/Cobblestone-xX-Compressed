package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.loot.CompressedStoneLootDefinition;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class StoneBreakSimulatorRecipeDatagen {
    private static final RecipePower BASE_POWER = new RecipePower(100L, 1L);
    private static final RecipePower COPPER_POWER = new RecipePower(800L, 4L);
    private static final RecipePower IRON_POWER = new RecipePower(6400L, 16L);
    private static final RecipePower GOLD_POWER = new RecipePower(51200L, 64L);
    private static final RecipePower AMETHYST_POWER = new RecipePower(1024000L, 256L);
    private static final RecipePower AQUAMARINE_POWER = new RecipePower(3276800L, 1024L);
    private static final RecipePower TOPAZ_POWER = new RecipePower(26214400L, 4096L);
    private static final RecipePower RUBY_POWER = new RecipePower(209715200L, 16384L);
    private static final RecipePower SAPPHIRE_POWER = new RecipePower(1677721600L, 65536L);
    private static final RecipePower DIAMOND_POWER = new RecipePower(13421772800L, 262144L);
    private static final RecipePower EMERALD_POWER = new RecipePower(MachineRecipePowerTiers.CHEMICAL_REACTOR_TOTAL_CP, 1048576L);
    private static final RecipePower NETHERITE_POWER = new RecipePower(MachineRecipePowerTiers.ASSEMBLY_MACHINE_TOTAL_CP, 4194304L);
    private static final RecipePower OBSIDIAN_POWER = new RecipePower(MachineRecipePowerTiers.EXTREME_COMPRESSOR_TOTAL_CP, 33554432L);

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