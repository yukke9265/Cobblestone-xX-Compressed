package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.multiblock.MultiblockFormIndex;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * マルチブロックのメンバ（筐体・ポート・アップグレード）用ブロックです。
 * BE は持たず、設置・破壊時にコアへ再検証を依頼するだけです。
 * formed はコアが構造完成時に書き込み、見た目切替に使います。
 */
public class MultiblockMemberBlock extends Block {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public MultiblockMemberBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FORMED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FORMED);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        // ブロック種別が変わったときだけ再検証する（formed だけの変更では回さない）。
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
