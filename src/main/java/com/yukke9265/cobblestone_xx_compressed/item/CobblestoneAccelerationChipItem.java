package com.yukke9265.cobblestone_xx_compressed.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.MachineUpgradeHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class CobblestoneAccelerationChipItem extends Item {
    private static final String TOOLTIP_KEY = "tooltip.cobblestonexxcompressed.cobblestone_acceleration_chip.rate";

    public CobblestoneAccelerationChipItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int multiplier = MachineUpgradeHelper.getAccelerationMultiplier(stack);
        if (multiplier <= 0) {
            return;
        }

        // 加速チップは機械の進行回数と CP 消費量の両方を同じ倍率で増やすため、
        // 実際に使われる CP/t 倍率をそのまま tooltip へ出します。
        tooltipComponents.add(Component.translatable(TOOLTIP_KEY, multiplier).withStyle(ChatFormatting.GRAY));
    }
}