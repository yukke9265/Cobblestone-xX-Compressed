package com.yukke9265.cobblestone_xx_compressed.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.registry.ModToolTiers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;

public class CompressedCobblestonePickaxeItem extends PickaxeItem {
    private static final String TOOLTIP_KEY = "tooltip.cobblestonexxcompressed.compressed_cobblestone_pickaxe.stone_break_simulator_bonus";

    private final ModToolTiers.CobblestonePickaxeMaterial material;

    public CompressedCobblestonePickaxeItem(ModToolTiers.CobblestonePickaxeMaterial material, Properties properties) {
        super(material.getTier(), properties);
        this.material = material;
    }

    @Override
    public void appendHoverText(
        @Nonnull ItemStack stack,
        @Nonnull TooltipContext context,
        @Nonnull List<Component> tooltipComponents,
        @Nonnull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(
            Component.translatable(TOOLTIP_KEY, this.material.getStoneBreakSimulatorUnbreakingBonus())
                .withStyle(ChatFormatting.GRAY)
        );
    }
}
