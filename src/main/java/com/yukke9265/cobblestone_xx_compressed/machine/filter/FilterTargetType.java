package com.yukke9265.cobblestone_xx_compressed.machine.filter;

/*
 * 方針:
 * フィルタ対象はアイテム入力スロットか流体タンクかを区別し、
 * ghost への登録方法と照合処理を切り替えます。
 */
public enum FilterTargetType {
    ITEM,
    FLUID
}
