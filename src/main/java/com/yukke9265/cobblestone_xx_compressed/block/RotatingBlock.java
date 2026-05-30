package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings("null")
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

    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        if (stack.is(ModItems.CONFIGURATION_CARD.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    protected boolean shouldPassInteractionToConfigurationCard(Player player) {
        return player.getMainHandItem().is(ModItems.CONFIGURATION_CARD.get())
            || player.getOffhandItem().is(ModItems.CONFIGURATION_CARD.get());
    }
}