package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.blockentity.StoneNetworkPointBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 隣接機械の搬入出面を、他の面から使えるようにするネットワークポイントです。
 *
 * 向きは持たない。どちらが本体かは、問い合わせが来た面から決める。
 * 隣が導体なら塗りつぶし、機械なら終点キャッシュだけ捨てる。
 */
public class StoneNetworkPointBlock extends Block implements EntityBlock {
    public StoneNetworkPointBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StoneNetworkPointBlockEntity(pos, state);
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
        if (level.getBlockEntity(pos) instanceof StoneNetworkPointBlockEntity pointBlockEntity) {
            pointBlockEntity.onNeighborChanged(neighborPos);
        }
    }
}
