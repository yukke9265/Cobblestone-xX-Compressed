package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;
import com.yukke9265.cobblestone_xx_compressed.util.BlockItemDropHelper;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings("null")
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

    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        net.minecraft.world.InteractionHand hand,
        BlockHitResult hitResult
    ) {
        if (stack.is(ModItems.CONFIGURATION_CARD.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockItemDropHelper.dropAllItems(level, pos, state);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }
}