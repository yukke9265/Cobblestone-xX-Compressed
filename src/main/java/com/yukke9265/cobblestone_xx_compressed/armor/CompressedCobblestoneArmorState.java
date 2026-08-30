package com.yukke9265.cobblestone_xx_compressed.armor;

import com.yukke9265.cobblestone_xx_compressed.registry.ModArmorMaterials.CobblestoneArmorMaterial;

/**
 * 装備中の圧縮丸石防具状態です。
 * 被ダメ計算用のキャッシュとして Attachment に保持します。
 */
public final class CompressedCobblestoneArmorState {
    public static final CompressedCobblestoneArmorState EMPTY = new CompressedCobblestoneArmorState(0, 0.0f);

    private final int advancedPieceCount;
    private final float damageReduction;

    public CompressedCobblestoneArmorState(int advancedPieceCount, float damageReduction) {
        this.advancedPieceCount = advancedPieceCount;
        this.damageReduction = damageReduction;
    }

    public int getAdvancedPieceCount() {
        return this.advancedPieceCount;
    }

    public float getDamageReduction() {
        return this.damageReduction;
    }

    public boolean hasAdvancedArmor() {
        return this.advancedPieceCount > 0 && this.damageReduction > 0.0f;
    }

    public static CompressedCobblestoneArmorState empty() {
        return EMPTY;
    }
}
