package com.yukke9265.cobblestone_xx_compressed.machine.filter;

/*
 * 方針:
 * スロットごとの許可方式はホワイトリスト／ブラックリストの2択に固定します。
 * 空のフィルタは「制限なし」として扱うため、モード単体では拒否しません。
 */
public enum SlotFilterMode {
    WHITELIST(0),
    BLACKLIST(1);

    private final int id;

    SlotFilterMode(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public SlotFilterMode next() {
        return this == WHITELIST ? BLACKLIST : WHITELIST;
    }

    public static SlotFilterMode fromId(int id) {
        if (id == BLACKLIST.id) {
            return BLACKLIST;
        }
        return WHITELIST;
    }
}
