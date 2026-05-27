package com.yukke9265.cobblestone_xx_compressed.blockitem;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class DescribedBlockItem extends BlockItem {
    private final String[] tooltipTranslationKeys;

    public DescribedBlockItem(Block block, Item.Properties properties, String... tooltipTranslationKeys) {
        super(block, properties);
        this.tooltipTranslationKeys = tooltipTranslationKeys;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        // 説明文の本文は翻訳キー側に寄せておき、
        // BlockItem 側は「どの説明文を表示するか」だけを担当させます。
        for (String tooltipTranslationKey : this.tooltipTranslationKeys) {
            tooltipComponents.add(Component.translatable(tooltipTranslationKey).withStyle(ChatFormatting.GRAY));
        }
    }
}