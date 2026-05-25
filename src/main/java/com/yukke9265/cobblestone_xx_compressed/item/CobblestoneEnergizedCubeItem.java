package com.yukke9265.cobblestone_xx_compressed.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.MachineUpgradeHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class CobblestoneEnergizedCubeItem extends Item {
    private static final String TOOLTIP_KEY = "tooltip.cobblestonexxcompressed.cobblestone_energized_cube.capacity";

    public CobblestoneEnergizedCubeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int multiplier = MachineUpgradeHelper.getEnergizedCubeMultiplier(stack);
        if (multiplier <= 0) {
            return;
        }

        // Energized Cube は機械内部の CP 容量を倍率で拡張するため、
        // 容量倍率をそのまま tooltip へ表示して分かりやすくします。
        tooltipComponents.add(Component.translatable(TOOLTIP_KEY, multiplier).withStyle(ChatFormatting.GRAY));
    }
}