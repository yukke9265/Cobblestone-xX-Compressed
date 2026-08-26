package com.yukke9265.cobblestone_xx_compressed.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.world.level.block.Block;

/**
 * マルチブロック用の共通カテゴリ置き場です。
 * 前提: パターン定義より先に参照できるよう静的に登録する。
 * 結果: id または定数で StructureCategory を取得できる。
 */
public final class StructureCategories {
    private static final Map<String, StructureCategory> BY_ID = new HashMap<>();

    public static final StructureCategory CASING = register(
        StructureCategory.builder("casing")
            .add(ModBlocks.COBBLESTONE_MACHINE_CASING)
            .addAll(casingTierBlocks())
            .build()
    );

    /**
     * アップグレード枠用。加速・蓄電・並列のいずれでも可。空気・筐体は不可。
     */
    public static final StructureCategory UPGRADE = register(
        StructureCategory.builder("upgrade")
            .matchIf(MachineUpgradeBlockHelper::isAnyUpgradeBlock)
            .build()
    );

    /**
     * 入出力枠用。CP/アイテム/流体ポート、または筐体を許可。空気は不可。
     */
    public static final StructureCategory INOUT = register(
        StructureCategory.builder("inout")
            .add(ModBlocks.MULTIBLOCK_ITEM_INPUT_PORT)
            .add(ModBlocks.MULTIBLOCK_ITEM_OUTPUT_PORT)
            .add(ModBlocks.MULTIBLOCK_FLUID_INPUT_PORT)
            .add(ModBlocks.MULTIBLOCK_FLUID_OUTPUT_PORT)
            .add(ModBlocks.MULTIBLOCK_COBBLE_INPUT_PORT)
            .matchIf(CASING::matches)
            .build()
    );

    private StructureCategories() {
    }

    public static StructureCategory require(String id) {
        StructureCategory category = BY_ID.get(id);
        if (category == null) {
            throw new IllegalArgumentException("未登録の StructureCategory です: " + id);
        }

        return category;
    }

    private static StructureCategory register(StructureCategory category) {
        StructureCategory previous = BY_ID.put(category.getId(), category);
        if (previous != null) {
            throw new IllegalStateException("StructureCategory が重複しています: " + category.getId());
        }

        return category;
    }

    private static List<Supplier<? extends Block>> casingTierBlocks() {
        List<Supplier<? extends Block>> blocks = new ArrayList<>();
        for (ModBlocks.TierCobblestoneMachineCasing tier : ModBlocks.TierCobblestoneMachineCasing.values()) {
            blocks.add(tier.getBlock());
        }

        return blocks;
    }
}
