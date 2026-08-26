package com.yukke9265.cobblestone_xx_compressed.multiblock;

/**
 * マルチブロック相対パターンの 1 マスの役割です。
 * 前提: コア向き基準のローカル座標で定義する。
 * 結果: 検証時に「何があれば完成か」をセル単位で判定できる。
 */
public enum MultiblockCellType {
    CORE,
    AIR,
    CASING,
    ITEM_IN,
    ITEM_OUT,
    FLUID_IN,
    FLUID_OUT,
    COBBLE_IN,
    UPGRADE_ACCEL,
    UPGRADE_ENERGY,
    UPGRADE_PARALLEL
}
