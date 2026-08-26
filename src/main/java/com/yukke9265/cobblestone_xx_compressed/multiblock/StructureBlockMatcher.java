package com.yukke9265.cobblestone_xx_compressed.multiblock;

import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * マルチブロック1マスの「置いてよいブロック」条件です。
 * 前提: 単一ブロック・カテゴリ・独自判定のいずれかを表す。
 * 結果: test で BlockState が条件を満たすか分かる。
 */
public final class StructureBlockMatcher {
    private final Predicate<BlockState> predicate;

    private StructureBlockMatcher(Predicate<BlockState> predicate) {
        this.predicate = predicate;
    }

    public boolean test(BlockState state) {
        return this.predicate.test(state);
    }

    public static StructureBlockMatcher air() {
        return new StructureBlockMatcher(BlockState::isAir);
    }

    public static StructureBlockMatcher any() {
        return new StructureBlockMatcher(state -> true);
    }

    public static StructureBlockMatcher block(Supplier<? extends Block> block) {
        return new StructureBlockMatcher(state -> state.is(block.get()));
    }

    public static StructureBlockMatcher category(StructureCategory category) {
        return new StructureBlockMatcher(category::matches);
    }

    public static StructureBlockMatcher category(String categoryId) {
        return new StructureBlockMatcher(state -> StructureCategories.require(categoryId).matches(state));
    }

    public static StructureBlockMatcher custom(Predicate<BlockState> predicate) {
        return new StructureBlockMatcher(predicate);
    }
}
