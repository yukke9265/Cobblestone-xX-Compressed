package com.yukke9265.cobblestone_xx_compressed.util;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;
import com.yukke9265.cobblestone_xx_compressed.registry.ModToolTiers;

import net.minecraft.world.item.ItemStack;

public final class CompressedCobblestonePickaxeHelper {
    private CompressedCobblestonePickaxeHelper() {
    }

    /**
     * 圧縮丸石ピッケルなら Stone Break Simulator 用の耐久相当ボーナスを返します。
     * それ以外のツルハシは 0 です。
     */
    public static int getStoneBreakSimulatorUnbreakingBonus(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        if (stack.is(ModItems.COMPRESSED_COBBLESTONE_PICKAXE.get())) {
            return ModToolTiers.CobblestonePickaxeMaterial.BASE.getStoneBreakSimulatorUnbreakingBonus();
        }

        for (ModItems.TierCompressedCobblestonePickaxe tier : ModItems.TierCompressedCobblestonePickaxe.values()) {
            if (stack.is(tier.getItem().get())) {
                return tier.getMaterial().getStoneBreakSimulatorUnbreakingBonus();
            }
        }

        return 0;
    }
}
