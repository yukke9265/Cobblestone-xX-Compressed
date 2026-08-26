package com.yukke9265.cobblestone_xx_compressed.machine.filter;

import java.util.List;

/*
 * 方針:
 * スロット別フィルタを持つ機械が公開する最小APIです。
 * 対象一覧が空なら GUI のフィルタ行は出しません。
 */
public interface ISlotFilterHost {
    MachineSlotFilters getSlotFilters();

    List<FilterTarget> getFilterTargets();
}
