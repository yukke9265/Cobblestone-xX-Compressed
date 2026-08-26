package com.yukke9265.cobblestone_xx_compressed.multiblock;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.world.level.block.state.BlockState;

/**
 * アップグレードブロックの倍率判定です。
 * 既存チップと同じ倍率表をブロック側でも使います。
 */
public final class MachineUpgradeBlockHelper {
    private static final int NORMAL_ACCELERATION_MULTIPLIER = 2;
    private static final int FIRST_TIER_ACCELERATION_MULTIPLIER = 4;
    private static final int NORMAL_ENERGIZED_MULTIPLIER = 4;
    private static final int FIRST_TIER_ENERGIZED_MULTIPLIER = 16;
    private static final int NORMAL_PARALLEL_EXTRA_CRAFT_COUNT = 1;
    private static final int FIRST_TIER_PARALLEL_EXTRA_CRAFT_COUNT = 2;

    private MachineUpgradeBlockHelper() {
    }

    public static boolean isAccelerationUpgradeBlock(BlockState state) {
        return getAccelerationMultiplier(state) > 0;
    }

    public static boolean isEnergizedUpgradeBlock(BlockState state) {
        return getEnergizedMultiplier(state) > 0;
    }

    public static boolean isParallelUpgradeBlock(BlockState state) {
        return getParallelExtraCraftCount(state) > 0;
    }

    /**
     * 加速・蓄電・並列のいずれかのアップグレードブロックか判定します。
     * 前提: 空気や筐体は含めない。
     */
    public static boolean isAnyUpgradeBlock(BlockState state) {
        return isAccelerationUpgradeBlock(state)
            || isEnergizedUpgradeBlock(state)
            || isParallelUpgradeBlock(state);
    }

    public static int getAccelerationMultiplier(BlockState state) {
        if (state.is(ModBlocks.MULTIBLOCK_ACCELERATION_UPGRADE.get())) {
            return NORMAL_ACCELERATION_MULTIPLIER;
        }

        int multiplier = FIRST_TIER_ACCELERATION_MULTIPLIER;
        for (ModBlocks.TierMultiblockAccelerationUpgrade tier : ModBlocks.TierMultiblockAccelerationUpgrade.values()) {
            if (state.is(tier.getBlock().get())) {
                return multiplier;
            }

            multiplier *= 2;
        }

        return 0;
    }

    public static int getEnergizedMultiplier(BlockState state) {
        if (state.is(ModBlocks.MULTIBLOCK_ENERGIZED_UPGRADE.get())) {
            return NORMAL_ENERGIZED_MULTIPLIER;
        }

        int multiplier = FIRST_TIER_ENERGIZED_MULTIPLIER;
        for (ModBlocks.TierMultiblockEnergizedUpgrade tier : ModBlocks.TierMultiblockEnergizedUpgrade.values()) {
            if (state.is(tier.getBlock().get())) {
                return multiplier;
            }

            multiplier *= 4;
        }

        return 0;
    }

    public static int getParallelExtraCraftCount(BlockState state) {
        if (state.is(ModBlocks.MULTIBLOCK_PARALLEL_UPGRADE.get())) {
            return NORMAL_PARALLEL_EXTRA_CRAFT_COUNT;
        }

        int extraCraftCount = FIRST_TIER_PARALLEL_EXTRA_CRAFT_COUNT;
        for (ModBlocks.TierMultiblockParallelUpgrade tier : ModBlocks.TierMultiblockParallelUpgrade.values()) {
            if (state.is(tier.getBlock().get())) {
                return extraCraftCount;
            }

            extraCraftCount++;
        }

        return 0;
    }
}
