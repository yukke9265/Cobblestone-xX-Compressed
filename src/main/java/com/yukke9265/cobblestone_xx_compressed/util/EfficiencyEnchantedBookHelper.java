package com.yukke9265.cobblestone_xx_compressed.util;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public final class EfficiencyEnchantedBookHelper {
    public static final int FIRST_EFFICIENCY_BOOK_LEVEL = 4;
    public static final int LAST_EFFICIENCY_BOOK_LEVEL = 12;

    // 効率 5 以降の段階強化で使うシンギュラリティは、
    // 幸運本と同じレベル対応（幸運 5 = アクアマリン …）にそろえます。
    // 効率 4 だけはレッドストーンブロック囲みなので、ここには含めません。
    private static final ModItems.TierCompressedCobblestoneSingularity[] EFFICIENCY_UPGRADE_TIERS = new ModItems.TierCompressedCobblestoneSingularity[] {
        ModItems.TierCompressedCobblestoneSingularity.AQUAMARINE,
        ModItems.TierCompressedCobblestoneSingularity.TOPAZ,
        ModItems.TierCompressedCobblestoneSingularity.RUBY,
        ModItems.TierCompressedCobblestoneSingularity.SAPPHIRE,
        ModItems.TierCompressedCobblestoneSingularity.DIAMOND,
        ModItems.TierCompressedCobblestoneSingularity.EMERALD,
        ModItems.TierCompressedCobblestoneSingularity.NETHERITE,
        ModItems.TierCompressedCobblestoneSingularity.OBSIDIAN
    };

    private EfficiencyEnchantedBookHelper() {
    }

    public static Holder<Enchantment> getEfficiencyEnchantment(HolderLookup.Provider holderLookup) {
        return holderLookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY);
    }

    public static ItemStack createEfficiencyEnchantedBook(HolderLookup.Provider holderLookup, int efficiencyLevel) {
        validateEfficiencyLevel(efficiencyLevel);

        // 保存エンチャント付きの本を、その場で完成状態の ItemStack として作ります。
        // creative tab の表示や datagen の結果出力へ共通利用します。
        ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
        Holder<Enchantment> efficiency = getEfficiencyEnchantment(holderLookup);

        EnchantmentHelper.updateEnchantments(enchantedBook, enchantments -> enchantments.set(efficiency, efficiencyLevel));
        return enchantedBook;
    }

    /**
     * 効率 5 以降の強化レシピで使うシンギュラリティを返します。
     * 効率 4 はレッドストーンブロック囲みなので、このメソッドは呼びません。
     */
    public static ModItems.TierCompressedCobblestoneSingularity getRequiredSingularityTier(int efficiencyLevel) {
        if (efficiencyLevel <= FIRST_EFFICIENCY_BOOK_LEVEL || efficiencyLevel > LAST_EFFICIENCY_BOOK_LEVEL) {
            throw new IllegalArgumentException("Unsupported efficiency upgrade level: " + efficiencyLevel);
        }
        return EFFICIENCY_UPGRADE_TIERS[efficiencyLevel - (FIRST_EFFICIENCY_BOOK_LEVEL + 1)];
    }

    private static void validateEfficiencyLevel(int efficiencyLevel) {
        if (efficiencyLevel < FIRST_EFFICIENCY_BOOK_LEVEL || efficiencyLevel > LAST_EFFICIENCY_BOOK_LEVEL) {
            throw new IllegalArgumentException("Unsupported efficiency level: " + efficiencyLevel);
        }
    }
}
