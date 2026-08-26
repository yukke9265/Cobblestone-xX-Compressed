package com.yukke9265.cobblestone_xx_compressed.multiblock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 名前付きの「置けるブロック集合」です。
 * 前提: 複数機械のパターンから同じ id で参照する。
 * 結果: matches でそのカテゴリとして有効か判定できる。
 */
public final class StructureCategory {
    private final String id;
    private final List<Supplier<? extends Block>> blocks;
    private final boolean allowAir;
    private final Predicate<BlockState> extraMatch;

    private StructureCategory(
        String id,
        List<Supplier<? extends Block>> blocks,
        boolean allowAir,
        Predicate<BlockState> extraMatch
    ) {
        this.id = id;
        this.blocks = List.copyOf(blocks);
        this.allowAir = allowAir;
        this.extraMatch = extraMatch;
    }

    public String getId() {
        return this.id;
    }

    public boolean matches(BlockState state) {
        if (this.allowAir && state.isAir()) {
            return true;
        }

        if (this.extraMatch != null && this.extraMatch.test(state)) {
            return true;
        }

        for (Supplier<? extends Block> block : this.blocks) {
            if (state.is(block.get())) {
                return true;
            }
        }

        return false;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final String id;
        private final List<Supplier<? extends Block>> blocks = new ArrayList<>();
        private boolean allowAir;
        private Predicate<BlockState> extraMatch;

        private Builder(String id) {
            this.id = id;
        }

        public Builder allowAir() {
            this.allowAir = true;
            return this;
        }

        public Builder add(Supplier<? extends Block> block) {
            this.blocks.add(block);
            return this;
        }

        public Builder addAll(Iterable<? extends Supplier<? extends Block>> blocks) {
            for (Supplier<? extends Block> block : blocks) {
                this.blocks.add(block);
            }
            return this;
        }

        /**
         * ブロック一覧以外の独自判定を足します（アップグレード階層など）。
         */
        public Builder matchIf(Predicate<BlockState> predicate) {
            this.extraMatch = predicate;
            return this;
        }

        public StructureCategory build() {
            return new StructureCategory(this.id, this.blocks, this.allowAir, this.extraMatch);
        }
    }
}
