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

public final class FortuneEnchantedBookHelper {
    public static final int FIRST_FORTUNE_BOOK_LEVEL = 4;
    public static final int LAST_FORTUNE_BOOK_LEVEL = 12;

    // Fortune 本の段階強化で使うシンギュラリティを、
    // 実際のクラフト順と同じ並びで固定しておきます。
    private static final ModItems.TierCompressedCobblestoneSingularity[] FORTUNE_BOOK_TIERS = new ModItems.TierCompressedCobblestoneSingularity[] {
        ModItems.TierCompressedCobblestoneSingularity.AMETHYST,
        ModItems.TierCompressedCobblestoneSingularity.AQUAMARINE,
        ModItems.TierCompressedCobblestoneSingularity.TOPAZ,
        ModItems.TierCompressedCobblestoneSingularity.RUBY,
        ModItems.TierCompressedCobblestoneSingularity.SAPPHIRE,
        ModItems.TierCompressedCobblestoneSingularity.DIAMOND,
        ModItems.TierCompressedCobblestoneSingularity.EMERALD,
        ModItems.TierCompressedCobblestoneSingularity.NETHERITE,
        ModItems.TierCompressedCobblestoneSingularity.OBSIDIAN
    };

    private FortuneEnchantedBookHelper() {
    }

    public static ItemStack createFortuneEnchantedBook(HolderLookup.Provider holderLookup, int fortuneLevel) {
        validateFortuneLevel(fortuneLevel);

        // 保存エンチャント付きの本を、その場で完成状態の ItemStack として作ります。
        // これを creative tab の表示や datagen の結果出力へ共通利用します。
        ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
        Holder<Enchantment> fortune = holderLookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);

        EnchantmentHelper.updateEnchantments(enchantedBook, enchantments -> enchantments.set(fortune, fortuneLevel));
        return enchantedBook;
    }

    public static ModItems.TierCompressedCobblestoneSingularity getRequiredSingularityTier(int fortuneLevel) {
        validateFortuneLevel(fortuneLevel);
        return FORTUNE_BOOK_TIERS[fortuneLevel - FIRST_FORTUNE_BOOK_LEVEL];
    }

    private static void validateFortuneLevel(int fortuneLevel) {
        if (fortuneLevel < FIRST_FORTUNE_BOOK_LEVEL || fortuneLevel > LAST_FORTUNE_BOOK_LEVEL) {
            throw new IllegalArgumentException("Unsupported fortune level: " + fortuneLevel);
        }
    }
}