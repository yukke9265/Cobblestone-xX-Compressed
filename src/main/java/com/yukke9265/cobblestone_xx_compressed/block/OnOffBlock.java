package com.yukke9265.cobblestone_xx_compressed.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class OnOffBlock extends RotatingBlock implements EntityBlock {
    //コンストラクタでブロックのONOFF状態を管理するためのプロパティを追加します。
    public static final BooleanProperty ON = BooleanProperty.create("on");

     // コンストラクタでブロックの初期状態を設定します。デフォルトはOFFにします。
    public OnOffBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any()
            .setValue(ON, false)
            .setValue(FACING, Direction.NORTH)
        );
    }

    // ブロックの状態定義に ON プロパティを追加します。
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ON);
    }

    // ブロックが配置されたときの状態を決定します。デフォルトはOFFにします。
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
            .setValue(ON, false) // 配置されたときはOFF状態にします。
            .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}