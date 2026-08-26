package com.yukke9265.cobblestone_xx_compressed.machine.filter;

/*
 * 方針:
 * getFilterTargets と allowsItem / allowsFluid で同じ文字列を共有するための定数です。
 * 手打ちの typo で「制限なし」になるのを防ぎます。
 */
public final class FilterTargetIds {
    public static final String ITEM_INPUT = "item:input";
    public static final String ITEM_INPUT_1 = "item:input1";
    public static final String ITEM_INPUT_2 = "item:input2";
    public static final String ITEM_INPUT_3 = "item:input3";
    public static final String ITEM_INPUT_4 = "item:input4";
    public static final String ITEM_INPUT_5 = "item:input5";
    public static final String ITEM_INPUT_6 = "item:input6";
    public static final String FLUID_INPUT = "fluid:input";
    public static final String FLUID_INPUT_1 = "fluid:input1";
    public static final String FLUID_INPUT_2 = "fluid:input2";

    private FilterTargetIds() {
    }
}
