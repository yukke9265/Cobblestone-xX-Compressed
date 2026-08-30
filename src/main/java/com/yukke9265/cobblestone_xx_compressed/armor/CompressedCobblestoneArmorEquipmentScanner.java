package com.yukke9265.cobblestone_xx_compressed.armor;

import com.yukke9265.cobblestone_xx_compressed.item.CompressedCobblestoneArmorItem;
import com.yukke9265.cobblestone_xx_compressed.registry.ModArmorMaterials.CobblestoneArmorMaterial;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 防具スロットを走査して、advanced tier 防具の装備状態を作ります。
 */
public final class CompressedCobblestoneArmorEquipmentScanner {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET
    };

    private CompressedCobblestoneArmorEquipmentScanner() {
    }

    public static CompressedCobblestoneArmorState scan(LivingEntity entity) {
        int advancedPieceCount = 0;
        float pieceReductionSum = 0.0f;

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof CompressedCobblestoneArmorItem armorItem)) {
                continue;
            }

            CobblestoneArmorMaterial material = armorItem.getCobblestoneMaterial();
            if (!material.usesCustomProtection()) {
                continue;
            }

            advancedPieceCount++;
            pieceReductionSum += CompressedCobblestoneArmorProtectionRules.getPieceDamageReduction(material);
        }

        float totalReduction = CompressedCobblestoneArmorProtectionRules.computeTotalDamageReduction(
            advancedPieceCount,
            pieceReductionSum
        );
        return new CompressedCobblestoneArmorState(advancedPieceCount, totalReduction);
    }
}
