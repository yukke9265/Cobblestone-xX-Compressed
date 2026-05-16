package com.yukke9265.cobblestone_xx_compressed.blockentity;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.world.item.ItemStack;

public final class MachineUpgradeHelper {
    private static final int NORMAL_ACCELERATION_MULTIPLIER = 2;
    private static final int FIRST_TIER_ACCELERATION_MULTIPLIER = 4;
    private static final int NORMAL_ENERGIZED_CUBE_MULTIPLIER = 4;
    private static final int FIRST_TIER_ENERGIZED_CUBE_MULTIPLIER = 16;

    private MachineUpgradeHelper() {
    }

    public static boolean isAccelerationChip(ItemStack stack) {
        return getAccelerationMultiplier(stack) > 0;
    }

    public static boolean isEnergizedCube(ItemStack stack) {
        return getEnergizedCubeMultiplier(stack) > 0;
    }

    public static int getAccelerationMultiplier(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        if (stack.is(ModItems.COBBLESTONE_ACCELERATION_CHIP.get())) {
            return NORMAL_ACCELERATION_MULTIPLIER;
        }

        int multiplier = FIRST_TIER_ACCELERATION_MULTIPLIER;
        for (ModItems.TierCobblestoneAccelerationChip tier : ModItems.TierCobblestoneAccelerationChip.values()) {
            if (stack.is(tier.getItem().get())) {
                return multiplier;
            }

            multiplier *= 2;
        }

        return 0;
    }

    public static int getEnergizedCubeMultiplier(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        if (stack.is(ModItems.COBBLESTONE_ENERGIZED_CUBE.get())) {
            return NORMAL_ENERGIZED_CUBE_MULTIPLIER;
        }

        int multiplier = FIRST_TIER_ENERGIZED_CUBE_MULTIPLIER;
        for (ModItems.TierCobblestoneEnergizedCube tier : ModItems.TierCobblestoneEnergizedCube.values()) {
            if (stack.is(tier.getItem().get())) {
                return multiplier;
            }

            multiplier *= 4;
        }

        return 0;
    }
}