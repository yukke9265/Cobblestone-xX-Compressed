package com.yukke9265.cobblestone_xx_compressed.util;

/**
 * 銅〜黒曜石までの素材 tier を 1 か所で管理します。
 * COPPER=1 から OBSIDIAN=12 まで、Tank など既存の tierLevel と同じ番号です。
 */
public enum CobblestoneTier {
    COPPER(1),
    IRON(2),
    GOLD(3),
    AMETHYST(4),
    AQUAMARINE(5),
    TOPAZ(6),
    RUBY(7),
    SAPPHIRE(8),
    DIAMOND(9),
    EMERALD(10),
    NETHERITE(11),
    OBSIDIAN(12);

    private final int level;

    CobblestoneTier(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public static CobblestoneTier fromMaterialName(String materialName) {
        return valueOf(materialName);
    }

    public static CobblestoneTier fromMaterialTier(Enum<?> materialTierEnum) {
        return fromMaterialName(materialTierEnum.name());
    }
}
