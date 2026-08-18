package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.network.StoneNetworkBlocks;
import com.yukke9265.cobblestone_xx_compressed.network.StoneNetworkTopology;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 石ネットワークのリレーです。
 *
 * BlockEntity は持たない。tick・capability・バッファも無い。
 * 置く・壊すときだけ導体として塗りつぶし、ネットワークの分岐と分割を担当する。
 */
public class StoneNetworkRelayBlock extends Block {
    public StoneNetworkRelayBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide || oldState.is(state.getBlock())) {
            return;
        }

        StoneNetworkTopology.scheduleRebuild(level, pos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(newState.getBlock())) {
            StoneNetworkTopology.scheduleRebuildFromNeighbors(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected void neighborChanged(
        BlockState state,
        Level level,
        BlockPos pos,
        Block neighborBlock,
        BlockPos neighborPos,
        boolean movedByPiston
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (level.isClientSide) {
            return;
        }

        // 機械の付け外しでは塗りつぶさない。導体が増減したときだけつなぎ直す。
        if (StoneNetworkBlocks.isConductor(level.getBlockState(neighborPos))) {
            StoneNetworkTopology.scheduleRebuild(level, pos);
        }
    }
}
