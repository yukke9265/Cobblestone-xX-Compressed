package com.yukke9265.cobblestone_xx_compressed.blockitem;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestonePowerHelper;
import com.yukke9265.cobblestone_xx_compressed.util.TooltipTranslationKeys;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class CobblestoneGeneratorBlockItem extends DescribedBlockItem {
    public CobblestoneGeneratorBlockItem(Block block, Item.Properties properties, String... tooltipTranslationKeys) {
        super(block, properties, tooltipTranslationKeys);
    }

    @Override
    public void appendHoverText(
        @Nonnull ItemStack stack,
        @Nonnull TooltipContext context,
        @Nonnull List<Component> tooltipComponents,
        @Nonnull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        long catalystRate = CobblestonePowerHelper.getCatalystRatePerTick(stack);
        if (catalystRate <= 0L) {
            return;
        }

        // 設置時の生産量と同じ CP を、機械の丸石スロットでは消費せず供給します。
        tooltipComponents.add(
            Component.translatable(TooltipTranslationKeys.cobblestoneGeneratorCatalystRate(), catalystRate)
                .withStyle(ChatFormatting.GRAY)
        );
    }
}
