package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.multiblock.MultiblockFormIndex;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * マルチブロックのメンバ（筐体・ポート・アップグレード）用ブロックです。
 * BE は持たず、設置・破壊時にコアへ再検証を依頼するだけです。
 */
public class MultiblockMemberBlock extends Block {
    public MultiblockMemberBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && state.getBlock() != oldState.getBlock()) {
            MultiblockFormIndex.requestRevalidation(level, pos);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && state.getBlock() != newState.getBlock()) {
            MultiblockFormIndex.requestRevalidation(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
