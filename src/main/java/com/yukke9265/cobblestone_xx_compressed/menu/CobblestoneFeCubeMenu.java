package com.yukke9265.cobblestone_xx_compressed.menu;

import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneFeCubeBlock;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneFEGeneratorBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneFeCubeBlockEntity;
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
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CobblestoneFeCubeMenu extends BaseMenu {
    private static final AutomationMode[] FE_CUBE_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT
    };

    private static final int DATA_INDEX_STORED_ENERGY = 0;
    private static final int DATA_INDEX_MAX_STORED_ENERGY = 2;
    private static final int DATA_INDEX_IMPORTED_ENERGY = 4;
    private static final int DATA_INDEX_EXPORTED_ENERGY = 6;
    private static final int DATA_INDEX_AUTOMATION_START = 8;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_INSERT = DATA_INDEX_AUTO_EXPORT + 1;
    private static final int DATA_INDEX_SOUND_MUTED = DATA_INDEX_AUTO_INSERT + 1;
    // BlockEntity の ContainerData と同じ個数に揃える（不一致だと GUI 同期で切断される）。
    private static final int DATA_COUNT = DATA_INDEX_SOUND_MUTED + 1;

    private static final int MACHINE_SLOT_COUNT = 1;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_START_INDEX = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMNS * PLAYER_INVENTORY_ROWS;
    private static final int HOTBAR_START_INDEX = PLAYER_INVENTORY_START_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int HOTBAR_END_INDEX = HOTBAR_START_INDEX + HOTBAR_SLOT_COUNT;

    private final CobblestoneFeCubeBlockEntity feCubeBlockEntity;
    private final ContainerData feCubeData;

    public CobblestoneFeCubeMenu(
        int containerId,
        Inventory playerInventory,
        CobblestoneFeCubeBlockEntity feCubeBlockEntity,
        ContainerData feCubeData
    ) {
        super(ModMenuType.COBBLESTONE_FE_CUBE_MENU.get(), containerId);
        this.feCubeBlockEntity = feCubeBlockEntity;
        this.feCubeData = feCubeData;
        ItemStackHandler itemStackHandler = feCubeBlockEntity.getItemStackHandler();

        checkContainerDataCount(feCubeData, DATA_COUNT);
        this.addDataSlots(feCubeData);

        this.addSlot(new SlotItemHandler(
            itemStackHandler,
            CobblestoneFeCubeBlockEntity.CHARGE_SLOT_INDEX,
            MachineGuiLayouts.FeCube.CHARGE_SLOT_X,
            MachineGuiLayouts.FeCube.CHARGE_SLOT_Y
        ));
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    public CobblestoneFeCubeMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public long getStoredForgeEnergy() {
        return this.getLongFromData(this.feCubeData, DATA_INDEX_STORED_ENERGY);
    }

    public long getMaxForgeEnergy() {
        return this.getLongFromData(this.feCubeData, DATA_INDEX_MAX_STORED_ENERGY);
    }

    public long getLastImportedForgeEnergy() {
        return this.getLongFromData(this.feCubeData, DATA_INDEX_IMPORTED_ENERGY);
    }

    public long getLastExportedForgeEnergy() {
        return this.getLongFromData(this.feCubeData, DATA_INDEX_EXPORTED_ENERGY);
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        int dataIndex = DATA_INDEX_AUTOMATION_START + automationSide.getIndex();
        return AutomationMode.fromId(this.feCubeData.get(dataIndex));
    }

    public boolean isAutoExportEnabled() {
        return this.feCubeData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public boolean isAutoInsertEnabled() {
        return this.feCubeData.get(DATA_INDEX_AUTO_INSERT) != 0;
    }

    @Override
    public boolean isSoundMuted() {
        return this.feCubeData.get(DATA_INDEX_SOUND_MUTED) != 0;
    }

    public int getAutomationButtonId(AutomationSide automationSide) {
        return this.getAutomationButtonId(automationSide.getIndex());
    }

    public int getAutoExportButtonId() {
        return this.getAutoExportToggleButtonId();
    }

    public int getAutoInsertButtonId() {
        return this.getAutoInsertToggleButtonId();
    }

    @Override
    protected boolean handleMachineMenuButton(Player player, int id) {
        if (this.handleAutomationButtonClick(this.feCubeBlockEntity, id, FE_CUBE_AUTOMATION_MODES)) {
            return true;
        }

        if (this.handleAutoExportButtonClick(this.feCubeBlockEntity, id)) {
            return true;
        }

        if (this.handleAutoInsertButtonClick(this.feCubeBlockEntity, id)) {
            return true;
        }

        if (this.handleMuteSoundButtonClick(this.feCubeBlockEntity, id)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.feCubeBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.feCubeBlockEntity.getBlockPos();
        if (!(player.level().getBlockState(blockPos).getBlock() instanceof CobblestoneFeCubeBlock)) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.feCubeBlockEntity) {
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

        if (index < MACHINE_SLOT_COUNT) {
            if (!this.moveItemStackTo(sourceStack, PLAYER_INVENTORY_START_INDEX, HOTBAR_END_INDEX, true)) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(sourceStack, copiedStack);
        } else {
            boolean movedToMachine = false;
            if (CobblestoneFEGeneratorBlockEntity.isChargeableItem(sourceStack)) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneFeCubeBlockEntity.CHARGE_SLOT_INDEX,
                    CobblestoneFeCubeBlockEntity.CHARGE_SLOT_INDEX + 1,
                    false
                );
            }

            if (!movedToMachine) {
                if (index < HOTBAR_START_INDEX) {
                    if (!this.moveItemStackTo(sourceStack, HOTBAR_START_INDEX, HOTBAR_END_INDEX, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(sourceStack, PLAYER_INVENTORY_START_INDEX, HOTBAR_START_INDEX, false)) {
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

    private CobblestoneFeCubeMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getFeCubeBlockEntity(playerInventory, blockPos),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    private static CobblestoneFeCubeBlockEntity getFeCubeBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneFeCubeBlockEntity feCubeBlockEntity) {
            return feCubeBlockEntity;
        }

        throw new IllegalStateException("Cobblestone FE Cube の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        int startX = 8;
        int startY = 84;
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
                int slotIndex = column + row * PLAYER_INVENTORY_COLUMNS + PLAYER_INVENTORY_COLUMNS;
                int x = startX + column * 18;
                int y = startY + row * 18;
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
    }

    private void addPlayerHotbarSlots(Inventory playerInventory) {
        int startX = 8;
        int startY = 142;
        for (int slotIndex = 0; slotIndex < HOTBAR_SLOT_COUNT; slotIndex++) {
            int x = startX + slotIndex * 18;
            this.addSlot(new Slot(playerInventory, slotIndex, x, startY));
        }
    }
}
