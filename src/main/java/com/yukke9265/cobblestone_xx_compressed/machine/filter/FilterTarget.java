package com.yukke9265.cobblestone_xx_compressed.machine.filter;

/*
 * 方針:
 * GUI で選択できる「1つの入力経路」を表します。
 * id は NBT / カード保存のキー、slotX/Y は選択ハイライト用の GUI 座標です。
 */
public record FilterTarget(
    String id,
    FilterTargetType type,
    int slotX,
    int slotY
) {
    public static FilterTarget item(String id, int slotX, int slotY) {
        return new FilterTarget(id, FilterTargetType.ITEM, slotX, slotY);
    }

    public static FilterTarget fluid(String id, int slotX, int slotY) {
        return new FilterTarget(id, FilterTargetType.FLUID, slotX, slotY);
    }
}
