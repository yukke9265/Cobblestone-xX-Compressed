package com.yukke9265.cobblestone_xx_compressed.multiblock;

import com.yukke9265.cobblestone_xx_compressed.block.MultiblockMemberBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * メンバブロックの formed 見た目を一括同期します。
 * 前提: コアの再検証成功/失敗時だけ呼ぶ。FORMED を持つブロックだけ更新する。
 * 結果: クライアントへ見た目だけ伝わり、再検証ループは起きない。
 */
public final class MultiblockFormedHelper {
    private MultiblockFormedHelper() {
    }

    public static void setFormed(Level level, Iterable<BlockPos> positions, boolean formed) {
        if (level == null || level.isClientSide || positions == null) {
            return;
        }

        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            if (!state.hasProperty(MultiblockMemberBlock.FORMED)) {
                continue;
            }

            if (state.getValue(MultiblockMemberBlock.FORMED) == formed) {
                continue;
            }

            // 隣人更新はせずクライアント見た目だけ更新する（フラグ 2 = UPDATE_CLIENTS）。
            level.setBlock(pos, state.setValue(MultiblockMemberBlock.FORMED, formed), 2);
        }
    }
}
