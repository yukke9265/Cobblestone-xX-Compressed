package com.yukke9265.cobblestone_xx_compressed.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.MachineUpgradeHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class CobblestoneParallelChipItem extends BaseMachineUpgradeItem {
    private static final String TOOLTIP_KEY = "tooltip.cobblestonexxcompressed.cobblestone_parallel_chip.extra";

    public CobblestoneParallelChipItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int extraCraftCount = MachineUpgradeHelper.getParallelExtraCraftCount(stack);
        if (extraCraftCount <= 0) {
            return;
        }

        // パラレルチップは、1 tick で余った CP を使って追加完了できる回数を加算します。
        tooltipComponents.add(Component.translatable(TOOLTIP_KEY, extraCraftCount).withStyle(ChatFormatting.GRAY));
    }
}
