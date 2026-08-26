package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneMultiblockCrusherBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiRecipeTransferDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.multiblock.VirtualItemHandler;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CobblestoneMultiblockCrusherMenu extends BaseMenu {
    private static final int MACHINE_SLOT_COUNT = 2;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int HOTBAR_SLOT_COUNT = 9;

    private final CobblestoneMultiblockCrusherBlockEntity crusherBlockEntity;
    private final ContainerData data;

    public CobblestoneMultiblockCrusherMenu(
        int containerId,
        Inventory playerInventory,
        CobblestoneMultiblockCrusherBlockEntity crusherBlockEntity,
        ContainerData data
    ) {
        super(ModMenuType.COBBLESTONE_MULTIBLOCK_CRUSHER_MENU.get(), containerId);
        this.crusherBlockEntity = crusherBlockEntity;
        this.data = data;

        checkContainerDataCount(data, crusherBlockEntity.getMultiblockDataCount());
        this.addDataSlots(data);

        this.addSlot(new SlotItemHandler(
            new VirtualItemHandler(crusherBlockEntity.getInputBufferPublic(), true, true, crusherBlockEntity::setChanged),
            0,
            MachineGuiLayouts.PoweredMachine.INPUT_SLOT_X,
            MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y
        ));
        this.addSlot(new SlotItemHandler(
            new VirtualItemHandler(crusherBlockEntity.getOutputBufferPublic(), false, true, crusherBlockEntity::setChanged),
            0,
            MachineGuiLayouts.PoweredMachine.OUTPUT_SLOT_X,
            MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y
        ));

        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    public CobblestoneMultiblockCrusherMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    private CobblestoneMultiblockCrusherMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getRequiredBlockEntity(playerInventory, blockPos),
            new SimpleContainerData(MultiblockDataCountHolder.DATA_COUNT)
        );
    }

    private static CobblestoneMultiblockCrusherBlockEntity getRequiredBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneMultiblockCrusherBlockEntity crusherBlockEntity) {
            return crusherBlockEntity;
        }

        throw new IllegalStateException("マルチブロッククラッシャーの BlockEntity を取得できませんでした: " + blockPos);
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.data, 2);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.data, 4);
    }

    public boolean isFormed() {
        return this.data.get(6) != 0;
    }

    public long getCurrentCobblestonePowerRate() {
        return this.getLongFromData(this.data, 7);
    }

    public long getInputCount() {
        return this.getLongFromData(this.data, 9);
    }

    public long getOutputCount() {
        return this.getLongFromData(this.data, 11);
    }

    public int getStructureMatchMask() {
        return this.data.get(13);
    }

    public int getStructureMatchedCount() {
        return this.data.get(14);
    }

    public int getStructureRequiredCount() {
        return this.data.get(15);
    }

    public boolean isStructureCellMatched(int index) {
        return (this.getStructureMatchMask() & (1 << index)) != 0;
    }

    public CobblestoneMultiblockCrusherBlockEntity getCrusherBlockEntity() {
        return this.crusherBlockEntity;
    }

    public boolean getIsAvailable() {
        return this.crusherBlockEntity.getIsAvailable();
    }

    @Override
    protected boolean handleMachineMenuButton(Player player, int id) {
        if (id == 0) {
            this.crusherBlockEntity.reverseIsAvailable();
            return true;
        }

        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.crusherBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.crusherBlockEntity.getBlockPos();
        if (!player.level().getBlockState(blockPos).is(ModBlocks.COBBLESTONE_MULTIBLOCK_CRUSHER.get())) {
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
        int playerStart = MACHINE_SLOT_COUNT;
        int hotbarStart = playerStart + PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS;
        int hotbarEnd = hotbarStart + HOTBAR_SLOT_COUNT;

        if (index < MACHINE_SLOT_COUNT) {
            if (!this.moveItemStackTo(sourceStack, playerStart, hotbarEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(sourceStack, 0, 1, false)) {
                if (index < hotbarStart) {
                    if (!this.moveItemStackTo(sourceStack, hotbarStart, hotbarEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(sourceStack, playerStart, hotbarStart, false)) {
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

    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        return List.of(new JeiRecipeTransferDefinition(
            ModJeiIds.COBBLESTONE_CRUSHER,
            0,
            1,
            MACHINE_SLOT_COUNT,
            PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS + HOTBAR_SLOT_COUNT
        ));
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
                int slotIndex = column + row * PLAYER_INVENTORY_COLUMNS + PLAYER_INVENTORY_COLUMNS;
                int x = MachineGuiLayouts.PLAYER_INVENTORY_START_X + column * MachineGuiLayouts.SLOT_SIZE;
                int y = MachineGuiLayouts.PLAYER_INVENTORY_START_Y + row * MachineGuiLayouts.SLOT_SIZE;
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
    }

    private void addPlayerHotbarSlots(Inventory playerInventory) {
        for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
            int x = MachineGuiLayouts.PLAYER_INVENTORY_START_X + column * MachineGuiLayouts.SLOT_SIZE;
            this.addSlot(new Slot(playerInventory, column, x, MachineGuiLayouts.HOTBAR_START_Y));
        }
    }

    private static final class MultiblockDataCountHolder {
        private static final int DATA_COUNT = 16;
    }
}
