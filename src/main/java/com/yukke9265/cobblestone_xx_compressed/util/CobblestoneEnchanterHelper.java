package com.yukke9265.cobblestone_xx_compressed.util;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class CobblestoneEnchanterHelper {
    public static final int DEFAULT_PROCESSING_TICKS = 200;

    private CobblestoneEnchanterHelper() {
    }

    public static EvaluationResult evaluate(ItemStack toolStack, ItemStack bookStack, HolderLookup.Provider holderLookup) {
        if (toolStack.isEmpty() || bookStack.isEmpty() || !bookStack.is(Items.ENCHANTED_BOOK)) {
            return EvaluationResult.invalid();
        }

        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = holderLookup.lookupOrThrow(Registries.ENCHANTMENT);
        ItemEnchantments bookEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(bookStack);
        if (bookEnchantments.isEmpty()) {
            return EvaluationResult.invalid();
        }

        ItemEnchantments currentEnchantments = toolStack.getAllEnchantments(enchantmentLookup);
        List<AppliedEnchantment> enchantmentsToApply = new ArrayList<>();

        for (Object2IntMap.Entry<Holder<Enchantment>> bookEntry : bookEnchantments.entrySet()) {
            Holder<Enchantment> enchantment = bookEntry.getKey();
            int targetLevel = bookEntry.getIntValue();

            if (!canApplyToItem(toolStack, enchantment)) {
                continue;
            }

            if (!isCompatibleWithExistingEnchantments(currentEnchantments, enchantmentsToApply, enchantment)) {
                return EvaluationResult.invalid();
            }

            int currentLevel = currentEnchantments.getLevel(enchantment);
            if (targetLevel <= currentLevel) {
                continue;
            }

            enchantmentsToApply.add(new AppliedEnchantment(enchantment, targetLevel));
        }

        if (enchantmentsToApply.isEmpty()) {
            return EvaluationResult.invalid();
        }

        ItemStack resultStack = toolStack.copyWithCount(1);
        EnchantmentHelper.updateEnchantments(resultStack, enchantments -> {
            for (AppliedEnchantment enchantment : enchantmentsToApply) {
                enchantments.set(enchantment.enchantment(), enchantment.level());
            }
        });

        int highestEnchantmentLevel = 0;
        for (AppliedEnchantment enchantment : enchantmentsToApply) {
            highestEnchantmentLevel = Math.max(highestEnchantmentLevel, enchantment.level());
        }

        long cobblestonePowerPerTick = calculatePowerPerTick(highestEnchantmentLevel);
        long totalCobblestonePower = saturatingMultiply(cobblestonePowerPerTick, DEFAULT_PROCESSING_TICKS);
        totalCobblestonePower = saturatingMultiply(totalCobblestonePower, enchantmentsToApply.size());
        return new EvaluationResult(resultStack, cobblestonePowerPerTick, totalCobblestonePower);
    }

    private static boolean canApplyToItem(ItemStack stack, Holder<Enchantment> enchantment) {
        return stack.is(enchantment.value().getSupportedItems()) && stack.isPrimaryItemFor(enchantment);
    }

    private static boolean isCompatibleWithExistingEnchantments(
        ItemEnchantments currentEnchantments,
        List<AppliedEnchantment> enchantmentsToApply,
        Holder<Enchantment> targetEnchantment
    ) {
        for (Object2IntMap.Entry<Holder<Enchantment>> currentEntry : currentEnchantments.entrySet()) {
            Holder<Enchantment> currentEnchantment = currentEntry.getKey();
            if (currentEnchantment.equals(targetEnchantment)) {
                continue;
            }

            if (!Enchantment.areCompatible(currentEnchantment, targetEnchantment)) {
                return false;
            }
        }

        for (AppliedEnchantment appliedEnchantment : enchantmentsToApply) {
            if (appliedEnchantment.enchantment().equals(targetEnchantment)) {
                continue;
            }

            if (!Enchantment.areCompatible(appliedEnchantment.enchantment(), targetEnchantment)) {
                return false;
            }
        }

        return true;
    }

    private static long calculatePowerPerTick(int enchantmentLevel) {
        if (enchantmentLevel <= 0) {
            return 0L;
        }

        if (enchantmentLevel >= 21) {
            return Long.MAX_VALUE;
        }

        return 1L << ((enchantmentLevel - 1) * 3);
    }

    private static long saturatingMultiply(long value, int multiplier) {
        if (value <= 0L || multiplier <= 0) {
            return 0L;
        }

        if (value > Long.MAX_VALUE / multiplier) {
            return Long.MAX_VALUE;
        }

        return value * multiplier;
    }

    private record AppliedEnchantment(Holder<Enchantment> enchantment, int level) {
    }

    public record EvaluationResult(ItemStack resultStack, long cobblestonePowerPerTick, long totalCobblestonePower) {
        public static EvaluationResult invalid() {
            return new EvaluationResult(ItemStack.EMPTY, 0L, 0L);
        }

        public boolean isValid() {
            return !this.resultStack.isEmpty() && this.cobblestonePowerPerTick > 0L && this.totalCobblestonePower > 0L;
        }
    }
}