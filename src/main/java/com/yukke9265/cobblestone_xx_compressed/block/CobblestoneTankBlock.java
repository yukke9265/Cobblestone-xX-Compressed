package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneTankBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings("null")
public class CobblestoneTankBlock extends RotatingBlock implements EntityBlock {
    private final long capacity;

    public CobblestoneTankBlock(Properties properties, long capacity) {
        super(properties);
        this.capacity = capacity;
    }

    public long getCapacity() {
        return this.capacity;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CobblestoneTankBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType == ModBlockEntities.COBBLESTONE_TANK_BLOCK_ENTITY.get()) {
            return (currentLevel, currentPos, currentState, blockEntity) -> {
                if (blockEntity instanceof CobblestoneTankBlockEntity tankBlockEntity) {
                    tankBlockEntity.tick();
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
        if (!(blockEntity instanceof CobblestoneTankBlockEntity tankBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(tankBlockEntity, pos);
        }

        return InteractionResult.CONSUME;
    }

    // copy_components だけのルートテーブルだと BlockItem が落ちないことがあるため、
    // 通常ドロップ後に BlockEntity データと向きを ItemStack へ載せます。
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity == null || drops.isEmpty()) {
            return drops;
        }

        Item blockItem = this.asItem();
        var registries = params.getLevel().registryAccess();
        BlockItemStateProperties blockStateProperties = BlockItemStateProperties.EMPTY.with(FACING, state.getValue(FACING));

        for (int index = 0; index < drops.size(); index++) {
            ItemStack drop = drops.get(index);
            if (!drop.is(blockItem)) {
                continue;
            }

            blockEntity.saveToItem(drop, registries);
            drop.set(DataComponents.BLOCK_STATE, blockStateProperties);
            drops.set(index, drop);
        }

        return drops;
    }
}
