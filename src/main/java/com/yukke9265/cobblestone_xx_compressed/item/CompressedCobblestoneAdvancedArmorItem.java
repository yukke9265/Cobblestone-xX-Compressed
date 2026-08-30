package com.yukke9265.cobblestone_xx_compressed.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.registry.ModArmorMaterials.CobblestoneArmorMaterial;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * DIAMOND 以降の圧縮丸石防具です。
 * SAPPHIRE 相当のアーマー値に加え、イベント側の独自 % 軽減も使います。
 */
public class CompressedCobblestoneAdvancedArmorItem extends CompressedCobblestoneArmorItem {
    private static final String FULL_SET_TOOLTIP_KEY =
        "tooltip.cobblestonexxcompressed.compressed_cobblestone_armor.custom_protection.full_set";
    private static final String PIECE_TOOLTIP_KEY =
        "tooltip.cobblestonexxcompressed.compressed_cobblestone_armor.custom_protection.piece";
    private static final String KNOCKBACK_IMMUNITY_TOOLTIP_KEY =
        "tooltip.cobblestonexxcompressed.compressed_cobblestone_armor.knockback_immunity";

    public CompressedCobblestoneAdvancedArmorItem(
        CobblestoneArmorMaterial material,
        Type armorType,
        Properties properties
    ) {
        super(material, armorType, properties);
    }

    @Override
    public void appendHoverText(
        @Nonnull ItemStack stack,
        @Nonnull TooltipContext context,
        @Nonnull List<Component> tooltipComponents,
        @Nonnull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int fullSetPercent = Math.round(this.getFullSetCustomDamageReduction() * 100.0f);
        int piecePercent = Math.round(this.getPieceCustomDamageReduction() * 100.0f);
        tooltipComponents.add(
            Component.translatable(FULL_SET_TOOLTIP_KEY, fullSetPercent)
                .withStyle(ChatFormatting.BLUE)
        );
        tooltipComponents.add(
            Component.translatable(PIECE_TOOLTIP_KEY, piecePercent)
                .withStyle(ChatFormatting.GRAY)
        );
        tooltipComponents.add(
            Component.translatable(KNOCKBACK_IMMUNITY_TOOLTIP_KEY)
                .withStyle(ChatFormatting.GRAY)
        );
    }
}
