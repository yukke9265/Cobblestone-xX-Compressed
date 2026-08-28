package com.yukke9265.cobblestone_xx_compressed.item;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class FlyingStoneItem extends Item {
    private static final String TOOLTIP_KEY = "tooltip.cobblestonexxcompressed.flying_stone.description";

    public FlyingStoneItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(
            @Nonnull ItemStack stack,
            @Nonnull TooltipContext context,
            @Nonnull List<Component> tooltipComponents,
            @Nonnull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(TOOLTIP_KEY).withStyle(ChatFormatting.GRAY));
    }
}
