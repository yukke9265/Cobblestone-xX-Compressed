package com.yukke9265.cobblestone_xx_compressed.blockentity;

import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneGeneratorBlock;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

/**
 * 機械の丸石スロットが扱う CP の換算を 1 か所にまとめます。
 *
 * 前提:
 * - 圧縮丸石などは 1 個消費して、まとまった CP をバッファへ入れます。
 * - 丸石ジェネレータは触媒として扱い、消費せず毎 tick だけ CP を足します。
 * 結果:
 * - 燃料と触媒の判定、加算量がここを見れば追えます。
 */
public final class CobblestonePowerHelper {
    private CobblestonePowerHelper() {
    }

    public static boolean isCobblestonePowerItem(ItemStack stack) {
        return getFuelValue(stack) > 0L || getCatalystRatePerTick(stack) > 0L;
    }

    public static long getFuelValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0L;
        }

        if (stack.is(Items.COBBLESTONE)) {
            return 1L;
        }

        if (stack.is(ModBlocks.COMPRESSED_COBBLESTONE.get().asItem())) {
            return 4L;
        }

        long currentPower = 32L;
        for (ModBlocks.TierCompressedCobblestone tier : ModBlocks.TierCompressedCobblestone.values()) {
            if (stack.is(tier.getBlock().get().asItem())) {
                return currentPower;
            }

            currentPower *= 8L;
        }

        return 0L;
    }

    /**
     * 触媒 1 個が 1 tick で足す CP です。
     *
     * S / M / L はそれぞれ生産 1 / 8 / 64 個分、
     * そのジェネレータが作る圧縮丸石 1 個の CP を掛けます。
     * スタック数は見ず、置いてある種類だけを使います。
     */
    public static long getCatalystRatePerTick(ItemStack stack) {
        ModBlocks.TierCobblestoneGenerator generatorVariant = findGeneratorVariant(stack);
        if (generatorVariant == null) {
            return 0L;
        }

        ItemStack producedStack = new ItemStack(generatorVariant.getProducedBlock().get());
        long unitPower = getFuelValue(producedStack);
        if (unitPower <= 0L) {
            return 0L;
        }

        return multiplySaturating(unitPower, generatorVariant.getProductionPerTick());
    }

    /**
     * スロット内のアイテムから CP バッファへ供給します。
     *
     * 触媒なら消費せず空き容量まで加算し、燃料なら 1 個分が入り切るときだけ消費します。
     * 戻り値は更新後の蓄積量です。変わっていなければ呼び出し側は保存しません。
     */
    public static long absorbPowerFromSlot(ItemStack powerStack, long storedPower, long maxPower) {
        long catalystRate = getCatalystRatePerTick(powerStack);
        if (catalystRate > 0L) {
            return addUpToCapacity(storedPower, maxPower, catalystRate);
        }

        long convertedPower = getFuelValue(powerStack);
        if (convertedPower <= 0L) {
            return storedPower;
        }

        if (storedPower + convertedPower > maxPower) {
            return storedPower;
        }

        powerStack.shrink(1);
        return storedPower + convertedPower;
    }

    private static ModBlocks.TierCobblestoneGenerator findGeneratorVariant(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }

        Block block = blockItem.getBlock();
        if (!(block instanceof CobblestoneGeneratorBlock generatorBlock)) {
            return null;
        }

        return generatorBlock.getGeneratorVariant();
    }

    private static long addUpToCapacity(long storedPower, long maxPower, long amount) {
        if (storedPower >= maxPower || amount <= 0L) {
            return storedPower;
        }

        long remainingCapacity = maxPower - storedPower;
        if (amount < remainingCapacity) {
            return storedPower + amount;
        }

        return maxPower;
    }

    private static long multiplySaturating(long value, int multiplier) {
        if (value <= 0L || multiplier <= 0) {
            return 0L;
        }

        if (value > Long.MAX_VALUE / multiplier) {
            return Long.MAX_VALUE;
        }

        return value * multiplier;
    }
}
