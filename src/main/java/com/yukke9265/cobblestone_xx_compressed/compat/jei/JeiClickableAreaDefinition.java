package com.yukke9265.cobblestone_xx_compressed.compat.jei;

import net.minecraft.resources.ResourceLocation;

// JEI のクリック領域を、JEI API に依存しない形で表す定義です。
// 共通 Screen 側ではこの情報だけを返し、実際の JEI 登録は plugin 側で行います。
public record JeiClickableAreaDefinition(
    int x,
    int y,
    int width,
    int height,
    ResourceLocation recipeCategoryId
) {
}