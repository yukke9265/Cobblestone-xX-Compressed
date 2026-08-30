package com.yukke9265.cobblestone_xx_compressed.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.ShieldProjectorUpgradeHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * シールドプロジェクターの変換量を加算し、総消費 CP も同じ比率で増やすモジュールです。
 */
public class ShieldRateModuleItem extends BaseMachineUpgradeItem {
    private static final String TOOLTIP_KEY = "tooltip.cobblestonexxcompressed.shield_rate_module.bonus";

    public ShieldRateModuleItem(Properties properties) {
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

        int bonus = ShieldProjectorUpgradeHelper.getConversionBonus(stack);
        if (bonus <= 0) {
            return;
        }

        tooltipComponents.add(Component.translatable(TOOLTIP_KEY, bonus).withStyle(ChatFormatting.GRAY));
    }
}
