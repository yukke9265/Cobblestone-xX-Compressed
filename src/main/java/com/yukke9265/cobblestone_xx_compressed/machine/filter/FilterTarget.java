package com.yukke9265.cobblestone_xx_compressed.machine.filter;

/*
 * 方針:
 * GUI で選択できる「1つの入力経路」を表します。
 * id は NBT / カード保存のキー、slotX/Y と highlight サイズは選択ハイライト用です。
 * 通常スロットは 18x18、Tank の横長ゲージなどだけサイズを変えます。
 */
public record FilterTarget(
    String id,
    FilterTargetType type,
    int slotX,
    int slotY,
    int highlightWidth,
    int highlightHeight
) {
    public static final int DEFAULT_HIGHLIGHT_SIZE = 18;

    public static FilterTarget item(String id, int slotX, int slotY) {
        return new FilterTarget(
            id,
            FilterTargetType.ITEM,
            slotX,
            slotY,
            DEFAULT_HIGHLIGHT_SIZE,
            DEFAULT_HIGHLIGHT_SIZE
        );
    }

    public static FilterTarget fluid(String id, int slotX, int slotY) {
        return new FilterTarget(
            id,
            FilterTargetType.FLUID,
            slotX,
            slotY,
            DEFAULT_HIGHLIGHT_SIZE,
            DEFAULT_HIGHLIGHT_SIZE
        );
    }

    public static FilterTarget fluid(String id, int slotX, int slotY, int highlightWidth, int highlightHeight) {
        return new FilterTarget(id, FilterTargetType.FLUID, slotX, slotY, highlightWidth, highlightHeight);
    }
}
