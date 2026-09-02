package com.yukke9265.cobblestone_xx_compressed.item;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * 丸石パン専用アイテムです。
 * 食べ物効果に加えて、tier ごとのフレーバーテキストをツールチップへ出します。
 */
public class CobblestoneBreadItem extends Item {
    private final String descriptionKey;

    public CobblestoneBreadItem(Properties properties, String descriptionKey) {
        super(properties);
        this.descriptionKey = descriptionKey;
    }

    @Override
    public void appendHoverText(
        @Nonnull ItemStack stack,
        @Nonnull TooltipContext context,
        @Nonnull List<Component> tooltipComponents,
        @Nonnull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(this.descriptionKey).withStyle(ChatFormatting.GRAY));
    }
}
