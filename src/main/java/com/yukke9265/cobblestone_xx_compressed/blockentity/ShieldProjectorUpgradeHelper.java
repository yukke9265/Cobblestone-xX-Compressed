package com.yukke9265.cobblestone_xx_compressed.blockentity;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * シールドプロジェクター独自アップグレードの読み取り経路です。
 * アイテム実装前は基本値だけを返し、スロット投入も拒否します。
 */
public final class ShieldProjectorUpgradeHelper {
    private ShieldProjectorUpgradeHelper() {
    }

    public static boolean isValidCustomUpgrade(int slot, ItemStack stack) {
        // 後からアイテムを追加したときに、この判定だけ差し替えます。
        return false;
    }

    public static long getMaxShieldCapacity(ItemStackHandler itemStackHandler, long baseMaxShield) {
        // 独自アップグレード未実装のため基本値を返します。
        return baseMaxShield;
    }

    public static double getEffectiveRange(ItemStackHandler itemStackHandler, double baseRange) {
        // 独自アップグレード未実装のため基本値を返します。
        return baseRange;
    }
}
