package com.yukke9265.cobblestone_xx_compressed.armor;

import com.yukke9265.cobblestone_xx_compressed.registry.ModArmorMaterials.CobblestoneArmorMaterial;

/**
 * DIAMOND 以降 tier 専用の被ダメ軽減ルールです。
 * 数式や上限を変えたいときは、基本的にここだけを直します。
 */
public final class CompressedCobblestoneArmorProtectionRules {
    /** 4 部位そろい時の軽減率（DIAMOND / OBSIDIAN）。 */
    private static final float FULL_SET_REDUCTION_BASE = 0.9f;
    private static final float FULL_SET_REDUCTION_CAP = 1.0f;

    /** 1 部位あたりの寄与率（4 部位で 100%）。 */
    private static final float PIECE_CONTRIBUTION_RATIO = 0.25f;

    /** 合計軽減率の上限です。 */
    private static final float MAX_TOTAL_REDUCTION = FULL_SET_REDUCTION_CAP;

    /** DIAMOND 以降を 1 部位でも装備していればノックバック無効にします。 */
    public static boolean grantsKnockbackImmunity(int advancedPieceCount) {
        return advancedPieceCount > 0;
    }

    private CompressedCobblestoneArmorProtectionRules() {
    }

    /**
     * 指定 tier の 4 部位そろい時の軽減率を返します。
     */
    public static float getFullSetDamageReduction(CobblestoneArmorMaterial material) {
        if (!material.usesCustomProtection()) {
            return 0.0f;
        }

        return lerpFloat(
            FULL_SET_REDUCTION_BASE,
            FULL_SET_REDUCTION_CAP,
            material.getAdvancedProtectionProgress()
        );
    }

    /**
     * 装備中の advanced 部位数と tier から、合計軽減率を計算します。
     */
    public static float computeTotalDamageReduction(int advancedPieceCount, float pieceReductionSum) {
        if (advancedPieceCount <= 0 || pieceReductionSum <= 0.0f) {
            return 0.0f;
        }

        return Math.min(pieceReductionSum, MAX_TOTAL_REDUCTION);
    }

    /**
     * 1 部位ぶんの軽減寄与を返します。
     */
    public static float getPieceDamageReduction(CobblestoneArmorMaterial material) {
        return getFullSetDamageReduction(material) * PIECE_CONTRIBUTION_RATIO;
    }

    private static float lerpFloat(float baseValue, float capValue, float progress) {
        return baseValue + (capValue - baseValue) * progress;
    }
}
