package com.yukke9265.cobblestone_xx_compressed.loot;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public final class StoneBreakSimulatorLootHelper {
    private StoneBreakSimulatorLootHelper() {
    }

    public static GeneratedDrops generateDrops(CompressedStoneLootDefinition definition, int brokenBlockCount, int fortuneLevel, RandomSource random) {
        if (definition == null) {
            return new GeneratedDrops(ItemStack.EMPTY, List.of());
        }

        int safeBrokenBlockCount = Math.max(0, brokenBlockCount);
        if (safeBrokenBlockCount <= 0) {
            return new GeneratedDrops(ItemStack.EMPTY, List.of());
        }

        ItemStack mainDrop = new ItemStack(definition.getCobblestoneBlock().get(), safeBrokenBlockCount);
        List<ItemStack> subDrops = new ArrayList<>();

        for (CompressedStoneLootDefinition.BonusLootEntry bonusDrop : definition.getBonusDrops()) {
            if (!bonusDrop.hasResolvedItem()) {
                continue;
            }

            int totalDropCount = 0;
            for (int index = 0; index < safeBrokenBlockCount; index++) {
                if (random.nextDouble() >= bonusDrop.getChance()) {
                    continue;
                }

                totalDropCount += applyOreDropsFortune(1, fortuneLevel, random);
            }

            if (totalDropCount <= 0) {
                continue;
            }

            mergeStack(subDrops, new ItemStack(bonusDrop.getItem().get(), totalDropCount));
        }

        return new GeneratedDrops(mainDrop, List.copyOf(subDrops));
    }

    private static int applyOreDropsFortune(int baseCount, int fortuneLevel, RandomSource random) {
        int safeBaseCount = Math.max(1, baseCount);
        if (fortuneLevel <= 0) {
            return safeBaseCount;
        }

        int bonus = random.nextInt(fortuneLevel + 2) - 1;
        if (bonus < 0) {
            bonus = 0;
        }

        return safeBaseCount * (bonus + 1);
    }

    private static void mergeStack(List<ItemStack> stacks, ItemStack stackToMerge) {
        if (stackToMerge.isEmpty()) {
            return;
        }

        for (ItemStack existingStack : stacks) {
            if (!ItemStack.isSameItemSameComponents(existingStack, stackToMerge)) {
                continue;
            }

            existingStack.grow(stackToMerge.getCount());
            return;
        }

        stacks.add(stackToMerge.copy());
    }

    public record GeneratedDrops(ItemStack mainDrop, List<ItemStack> subDrops) {
    }
}