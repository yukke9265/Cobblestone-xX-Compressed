package com.yukke9265.cobblestone_xx_compressed.blockentity;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * シールドプロジェクター独自アップグレードの判定と加算値読み取りです。
 * 範囲・変換量・容量はスロット内のモジュールをすべて加算します。
 */
public final class ShieldProjectorUpgradeHelper {
    private static final int BASE_RANGE_BONUS = 4;
    private static final int FIRST_TIER_RANGE_BONUS = 8;
    private static final int RANGE_BONUS_STEP = 4;
    private static final int BASE_CONVERSION_BONUS = 10;
    private static final int FIRST_TIER_CONVERSION_BONUS = 20;
    private static final int CONVERSION_BONUS_STEP = 10;
    private static final long BASE_CAPACITY_BONUS = 100L;
    private static final long FIRST_TIER_CAPACITY_BONUS = 200L;
    private static final long CAPACITY_BONUS_STEP = 100L;

    private ShieldProjectorUpgradeHelper() {
    }

    public static boolean isValidCustomUpgrade(int slot, ItemStack stack) {
        return isRangeModule(stack) || isRateModule(stack) || isCapacityModule(stack);
    }

    public static boolean isValidCustomUpgrade(ItemStack stack) {
        return isRangeModule(stack) || isRateModule(stack) || isCapacityModule(stack);
    }

    public static boolean isRangeModule(ItemStack stack) {
        return getRangeBonus(stack) > 0;
    }

    public static boolean isRateModule(ItemStack stack) {
        return getConversionBonus(stack) > 0;
    }

    public static boolean isCapacityModule(ItemStack stack) {
        return getCapacityBonus(stack) > 0L;
    }

    public static int getRangeBonus(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        if (stack.is(ModItems.SHIELD_RANGE_MODULE.get())) {
            return BASE_RANGE_BONUS;
        }

        int bonus = FIRST_TIER_RANGE_BONUS;
        for (ModItems.TierShieldRangeModule tier : ModItems.TierShieldRangeModule.values()) {
            if (stack.is(tier.getItem().get())) {
                return bonus;
            }

            bonus += RANGE_BONUS_STEP;
        }

        return 0;
    }

    public static int getConversionBonus(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        if (stack.is(ModItems.SHIELD_RATE_MODULE.get())) {
            return BASE_CONVERSION_BONUS;
        }

        int bonus = FIRST_TIER_CONVERSION_BONUS;
        for (ModItems.TierShieldRateModule tier : ModItems.TierShieldRateModule.values()) {
            if (stack.is(tier.getItem().get())) {
                return bonus;
            }

            bonus += CONVERSION_BONUS_STEP;
        }

        return 0;
    }

    public static long getCapacityBonus(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0L;
        }

        if (stack.is(ModItems.SHIELD_CAPACITY_MODULE.get())) {
            return BASE_CAPACITY_BONUS;
        }

        long bonus = FIRST_TIER_CAPACITY_BONUS;
        for (ModItems.TierShieldCapacityModule tier : ModItems.TierShieldCapacityModule.values()) {
            if (stack.is(tier.getItem().get())) {
                return bonus;
            }

            bonus += CAPACITY_BONUS_STEP;
        }

        return 0L;
    }

    public static long getMaxShieldCapacity(ItemStackHandler itemStackHandler, long baseMaxShield) {
        return baseMaxShield + getTotalCapacityBonus(itemStackHandler);
    }

    public static double getEffectiveRange(ItemStackHandler itemStackHandler, double baseRange) {
        return baseRange + getTotalRangeBonus(itemStackHandler);
    }

    public static long getEffectiveShieldOutput(ItemStackHandler itemStackHandler, long baseShieldOutput) {
        return baseShieldOutput + getTotalConversionBonus(itemStackHandler);
    }

    public static long getEffectiveTotalCobblestonePower(
        ItemStackHandler itemStackHandler,
        long baseTotalCobblestonePower,
        long baseShieldOutput
    ) {
        long conversionBonus = getTotalConversionBonus(itemStackHandler);
        if (conversionBonus <= 0L || baseShieldOutput <= 0L) {
            return baseTotalCobblestonePower;
        }

        // 変換量の増加分だけ、同じ比率で総消費 CP を増やします。
        long extraTotalCobblestonePower = conversionBonus * baseTotalCobblestonePower / baseShieldOutput;
        return baseTotalCobblestonePower + extraTotalCobblestonePower;
    }

    private static int getTotalRangeBonus(ItemStackHandler itemStackHandler) {
        int total = 0;
        for (int slot = 0; slot < itemStackHandler.getSlots(); slot++) {
            total += getRangeBonus(itemStackHandler.getStackInSlot(slot));
        }

        return total;
    }

    private static long getTotalConversionBonus(ItemStackHandler itemStackHandler) {
        long total = 0L;
        for (int slot = 0; slot < itemStackHandler.getSlots(); slot++) {
            total += getConversionBonus(itemStackHandler.getStackInSlot(slot));
        }

        return total;
    }

    private static long getTotalCapacityBonus(ItemStackHandler itemStackHandler) {
        long total = 0L;
        for (int slot = 0; slot < itemStackHandler.getSlots(); slot++) {
            total += getCapacityBonus(itemStackHandler.getStackInSlot(slot));
        }

        return total;
    }
}
