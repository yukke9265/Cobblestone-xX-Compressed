package com.yukke9265.cobblestone_xx_compressed.blockitem;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class CompressedCobblestoneBlockItem extends BlockItem {
    private static final String COMPRESSION_TOOLTIP_KEY = "tooltip.cobblestonexxcompressed.compressed_cobblestone.compression";
    private static final long BASE_COMPRESSED_AMOUNT = 4L;
    private static final long FIRST_TIER_COMPRESSED_AMOUNT = 32L;
    private static final long TIER_MULTIPLIER = 8L;

    public CompressedCobblestoneBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        long compressionAmount = getCompressionAmount(stack);
        if (compressionAmount <= 0L) {
            return;
        }

        // 無印は 4 個圧縮、tier 版は 1 段ごとに前 tier の 8 倍になるため、
        // 現在の stack が何個分の丸石を表すかを tooltip で追えるようにします。
        tooltipComponents.add(Component.translatable(COMPRESSION_TOOLTIP_KEY, compressionAmount).withStyle(ChatFormatting.GRAY));
    }

    private static long getCompressionAmount(ItemStack stack) {
        if (stack.getItem() == ModItems.COMPRESSED_COBBLESTONE_ITEM.get()) {
            return BASE_COMPRESSED_AMOUNT;
        }

        long compressionAmount = FIRST_TIER_COMPRESSED_AMOUNT;
        for (ModItems.TierCompressedCobblestoneItem tier : ModItems.TierCompressedCobblestoneItem.values()) {
            if (stack.getItem() == tier.getItem().get()) {
                return compressionAmount;
            }

            compressionAmount *= TIER_MULTIPLIER;
        }

        return 0L;
    }
}