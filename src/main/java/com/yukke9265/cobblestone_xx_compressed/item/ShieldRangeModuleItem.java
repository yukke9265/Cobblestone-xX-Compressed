package com.yukke9265.cobblestone_xx_compressed.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.ShieldProjectorUpgradeHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * シールドプロジェクターの範囲を加算するモジュールです。
 */
public class ShieldRangeModuleItem extends BaseMachineUpgradeItem {
    private static final String TOOLTIP_KEY = "tooltip.cobblestonexxcompressed.shield_range_module.bonus";

    public ShieldRangeModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(
        @Nonnull ItemStack stack,
        @Nonnull TooltipContext context,
        @Nonnull List<Component> tooltipComponents,
        @Nonnull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int bonus = ShieldProjectorUpgradeHelper.getRangeBonus(stack);
        if (bonus <= 0) {
            return;
        }

        tooltipComponents.add(Component.translatable(TOOLTIP_KEY, bonus).withStyle(ChatFormatting.GRAY));
    }
}
