package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.blockentity.StoneBreakSimulatorBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;

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

@SuppressWarnings("null")
public class StoneBreakSimulatorBlock extends OnOffBlock {
    public StoneBreakSimulatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StoneBreakSimulatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType == ModBlockEntities.STONE_BREAK_SIMULATOR_BLOCK_ENTITY.get()) {
            return (currentLevel, currentPos, currentState, blockEntity) -> {
                if (blockEntity instanceof StoneBreakSimulatorBlockEntity stoneBreakSimulatorBlockEntity) {
                    stoneBreakSimulatorBlockEntity.tick();
                }
            };
        }

        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof StoneBreakSimulatorBlockEntity stoneBreakSimulatorBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(stoneBreakSimulatorBlockEntity, pos);
        }

        return InteractionResult.CONSUME;
    }
}