package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneFurnaceBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.player.Player;

public class CobblestoneFurnaceBlock extends OnOffBlock {
    public CobblestoneFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // このブロックでは、共通のBaseBlockEntityではなく専用のBlockEntityを生成します。
        return new CobblestoneFurnaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        // getTicker は「このブロックに、毎ティック実行する処理を渡すかどうか」を決めるメソッドです。
        // 今回は CobblestoneFurnaceBlockEntity にだけ tick 処理を流したいので、
        // 渡された BlockEntityType が炉専用の型と一致したときだけ ticker を返します。
        if (blockEntityType == ModBlockEntities.COBBLESTONE_FURNACE_BLOCK_ENTITY.get()) {
            return (currentLevel, currentPos, currentState, blockEntity) -> {
                // 実行時に受け取る blockEntity はジェネリクス上は T 型ですが、
                // 上の if で型を絞っているため、ここでは炉専用 BlockEntity として扱えます。
                if (blockEntity instanceof CobblestoneFurnaceBlockEntity) {
                    CobblestoneFurnaceBlockEntity furnaceBlockEntity = (CobblestoneFurnaceBlockEntity) blockEntity;

                    // 実際の毎ティック処理本体は BlockEntity 側にまとめてあるので、
                    // ブロック側の ticker からはその tick() を呼び出すだけにしています。
                    furnaceBlockEntity.tick();
                }
            };
        }

        // 対象の型でない場合は ticker を返しません。
        // これにより、関係ない BlockEntity にこの tick 処理が流れ込むのを防ぎます。
        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CobblestoneFurnaceBlockEntity furnaceBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(furnaceBlockEntity, pos);
        }

        return InteractionResult.CONSUME;
    }
}