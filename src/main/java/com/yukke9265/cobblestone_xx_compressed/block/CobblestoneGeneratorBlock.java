package com.yukke9265.cobblestone_xx_compressed.block;

import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneGeneratorBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.util.BlockItemDropHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CobblestoneGeneratorBlock extends Block implements EntityBlock {
    private final ModBlocks.TierCobblestoneGenerator generatorVariant;

    public CobblestoneGeneratorBlock(Properties properties, ModBlocks.TierCobblestoneGenerator generatorVariant) {
        super(properties);
        this.generatorVariant = generatorVariant;
    }

    public ModBlocks.TierCobblestoneGenerator getGeneratorVariant() {
        return this.generatorVariant;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CobblestoneGeneratorBlockEntity(pos, state, this.generatorVariant);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType == ModBlockEntities.COBBLESTONE_GENERATOR_BLOCK_ENTITY.get()) {
            return (currentLevel, currentPos, currentState, blockEntity) -> {
                if (blockEntity instanceof CobblestoneGeneratorBlockEntity generatorBlockEntity) {
                    generatorBlockEntity.tick();
                }
            };
        }

        return null;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockItemDropHelper.dropAllItems(level, pos, state);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }
}