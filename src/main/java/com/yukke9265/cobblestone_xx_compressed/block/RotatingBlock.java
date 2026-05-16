package com.yukke9265.cobblestone_xx_compressed.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class RotatingBlock extends Block {
    // ブロックの向きを表すプロパティを定義します。水平4方向をサポートします。
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // コンストラクタでブロックの初期状態を設定します。デフォルトは北向きにします。
    public RotatingBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // ブロックの状態定義に FACING プロパティを追加します。
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // ブロックが配置されたときの状態を決定します。プレイヤーの向きに基づいてブロックの向きを設定します。
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}