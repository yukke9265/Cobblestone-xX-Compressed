package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneFeCubeBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.util.BlockItemDropHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CobblestoneFeCubeBlock extends OnOffBlock {
    private final long capacity;

    public CobblestoneFeCubeBlock(Properties properties, long capacity) {
        super(properties);
        this.capacity = capacity;
    }

    public long getCapacity() {
        return this.capacity;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CobblestoneFeCubeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType == ModBlockEntities.COBBLESTONE_FE_CUBE_BLOCK_ENTITY.get()) {
            return (currentLevel, currentPos, currentState, blockEntity) -> {
                if (blockEntity instanceof CobblestoneFeCubeBlockEntity feCubeBlockEntity) {
                    feCubeBlockEntity.tick();
                }
            };
        }

        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (this.shouldPassInteractionToConfigurationCard(player)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CobblestoneFeCubeBlockEntity feCubeBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(feCubeBlockEntity, pos);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockItemDropHelper.dropAllItems(level, pos, state);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }
}
