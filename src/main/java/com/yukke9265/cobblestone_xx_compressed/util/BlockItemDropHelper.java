package com.yukke9265.cobblestone_xx_compressed.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public final class BlockItemDropHelper {
    private BlockItemDropHelper() {
    }

    public static void dropAllItems(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.isRemoved()) {
            return;
        }

        // null side では機械の全体在庫を返す実装にそろっているので、
        // 破壊時はこの handler をそのまま走査して全スロットを落とします。
        IItemHandler itemHandler = Capabilities.ItemHandler.BLOCK.getCapability(level, pos, state, blockEntity, null);
        if (itemHandler == null) {
            return;
        }

        for (int slotIndex = 0; slotIndex < itemHandler.getSlots(); slotIndex++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slotIndex);
            if (stackInSlot.isEmpty()) {
                continue;
            }

            Block.popResource(level, pos, stackInSlot.copy());
        }
    }
}