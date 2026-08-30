package com.yukke9265.cobblestone_xx_compressed.item;

import com.yukke9265.cobblestone_xx_compressed.registry.ModArmorMaterials.CobblestoneArmorMaterial;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

/**
 * 圧縮丸石防具共通の Item です。
 * tier 素材を保持し、advanced 防具判定に使います。
 */
public class CompressedCobblestoneArmorItem extends ArmorItem {
    private final CobblestoneArmorMaterial material;

    public CompressedCobblestoneArmorItem(
        CobblestoneArmorMaterial material,
        Type armorType,
        Properties properties
    ) {
        super(material.getHolder(), armorType, properties);
        this.material = material;
    }

    public CobblestoneArmorMaterial getCobblestoneMaterial() {
        return this.material;
    }

    public boolean isAdvancedProtectionArmor() {
        return this.material.usesCustomProtection();
    }

    /**
     * 4 部位そろい時の独自軽減率（0.0〜1.0）です。
     */
    public float getFullSetCustomDamageReduction() {
        return com.yukke9265.cobblestone_xx_compressed.armor.CompressedCobblestoneArmorProtectionRules
            .getFullSetDamageReduction(this.material);
    }

    /**
     * この 1 部位が advanced 防具として与える軽減寄与です。
     */
    public float getPieceCustomDamageReduction() {
        return com.yukke9265.cobblestone_xx_compressed.armor.CompressedCobblestoneArmorProtectionRules
            .getPieceDamageReduction(this.material);
    }
}
