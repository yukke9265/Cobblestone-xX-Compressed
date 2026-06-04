package com.yukke9265.cobblestone_xx_compressed.menu;

import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiRecipeTransferDefinition;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/*
 * 方針:
 * PoweredMachineMenuBase は、電力を使う機械メニューで毎回ほぼ同じになる流れだけをまとめます。
 * ここでは stillValid、プレイヤーインベントリ追加、Shift+クリック移動の共通骨格を持ち、
 * 機械ごとに異なる「どのブロックを対象にするか」「どのスロットへ優先投入するか」だけを継承先へ残します。
 */
public abstract class PoweredMachineMenuBase<T extends BaseBlockEntity> extends BaseMenu {
    protected static final int PLAYER_INVENTORY_COLUMNS = 9;
    protected static final int PLAYER_INVENTORY_ROWS = 3;
    protected static final int HOTBAR_SLOT_COUNT = 9;

    protected PoweredMachineMenuBase(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    protected abstract T getMachineBlockEntity();

    protected abstract Block getMachineBlock();

    protected abstract int getMachineSlotCount();

    protected abstract boolean moveStackToMachine(ItemStack stack);

    protected final int getPlayerInventoryStartIndex() {
        return this.getMachineSlotCount();
    }

    protected final int getPlayerInventorySlotCount() {
        return PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS;
    }

    protected final int getHotbarStartIndex() {
        return this.getPlayerInventoryStartIndex() + this.getPlayerInventorySlotCount();
    }

    protected final int getHotbarEndIndex() {
        return this.getHotbarStartIndex() + HOTBAR_SLOT_COUNT;
    }

    @Override
    public boolean stillValid(Player player) {
        T machineBlockEntity = this.getMachineBlockEntity();
        if (machineBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = machineBlockEntity.getBlockPos();
        if (!player.level().getBlockState(blockPos).is(this.getMachineBlock())) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != machineBlockEntity) {
            return false;
        }

        return player.distanceToSqr(
            blockPos.getX() + 0.5D,
            blockPos.getY() + 0.5D,
            blockPos.getZ() + 0.5D
        ) <= 64.0D;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = slot.getItem();
        ItemStack copiedStack = sourceStack.copy();

        if (index < this.getMachineSlotCount()) {
            if (!this.moveItemStackTo(sourceStack, this.getPlayerInventoryStartIndex(), this.getHotbarEndIndex(), true)) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(sourceStack, copiedStack);
        } else {
            boolean movedToMachine = this.moveStackToMachine(sourceStack);

            if (!movedToMachine) {
                if (index < this.getHotbarStartIndex()) {
                    if (!this.moveItemStackTo(sourceStack, this.getHotbarStartIndex(), this.getHotbarEndIndex(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(sourceStack, this.getPlayerInventoryStartIndex(), this.getHotbarStartIndex(), false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (sourceStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (sourceStack.getCount() == copiedStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, sourceStack);
        return copiedStack;
    }

    protected final void addPlayerInventorySlots(Inventory playerInventory, int startX, int startY, int slotSize) {
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
                int slotIndex = column + row * PLAYER_INVENTORY_COLUMNS + PLAYER_INVENTORY_COLUMNS;
                int x = startX + column * slotSize;
                int y = startY + row * slotSize;
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
    }

    protected final void addPlayerHotbarSlots(Inventory playerInventory, int startX, int startY, int slotSize) {
        for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
            int x = startX + column * slotSize;
            this.addSlot(new Slot(playerInventory, column, x, startY));
        }
    }

    protected final List<JeiRecipeTransferDefinition> createPoweredMachineJeiRecipeTransferDefinition(
        ResourceLocation recipeCategoryId,
        int recipeSlotStart,
        int recipeSlotCount
    ) {
        return this.createSingleJeiRecipeTransferDefinition(
            recipeCategoryId,
            recipeSlotStart,
            recipeSlotCount,
            this.getPlayerInventoryStartIndex(),
            this.getPlayerInventorySlotCount() + HOTBAR_SLOT_COUNT
        );
    }

    protected static <T extends BlockEntity> T getRequiredBlockEntity(
        Inventory playerInventory,
        BlockPos blockPos,
        Class<T> blockEntityClass,
        String machineName
    ) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntityClass.isInstance(blockEntity)) {
            return blockEntityClass.cast(blockEntity);
        }

        throw new IllegalStateException(machineName + " の BlockEntity を取得できませんでした: " + blockPos);
    }
}