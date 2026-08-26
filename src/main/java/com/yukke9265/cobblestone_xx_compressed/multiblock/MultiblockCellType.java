package com.yukke9265.cobblestone_xx_compressed.multiblock;

/**
 * マルチブロック 1 マスの役割（Role）です。
 * 前提: 「置いてよいブロック」は StructureBlockMatcher / StructureCategory 側で決める。
 * 結果: formed 後のポート集計やアップグレード倍率の扱いに使う。
 */
public enum MultiblockCellType {
    CORE,
    AIR,
    CASING,
    INOUT,
    ITEM_IN,
    ITEM_OUT,
    FLUID_IN,
    FLUID_OUT,
    COBBLE_IN,
    UPGRADE
}
