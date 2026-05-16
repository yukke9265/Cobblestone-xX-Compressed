package com.yukke9265.cobblestone_xx_compressed.menu;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneFEGeneratorBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestonePoweredFurnaceBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;

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

public class CobblestoneFEGeneratorMenu extends BaseMenu {
    private static final AutomationMode[] FE_GENERATOR_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT
    };

    private static final int DATA_COUNT = 13 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_STORED_POWER = 0;
    private static final int DATA_INDEX_MAX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_ENERGY = 4;
    private static final int DATA_INDEX_MAX_STORED_ENERGY = 6;
    private static final int DATA_INDEX_CONVERTED_ENERGY = 8;
    private static final int DATA_INDEX_EXPORTED_ENERGY = 10;
    private static final int DATA_INDEX_AUTO_EXPORT = 12 + BaseBlockEntity.AUTOMATION_FACE_COUNT;

    private static final int MACHINE_SLOT_X = 8;
    private static final int MACHINE_SLOT_Y = 16;
    private static final int MACHINE_SLOT_COUNT = 1;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_START_INDEX = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMNS * PLAYER_INVENTORY_ROWS;
    private static final int HOTBAR_START_INDEX = PLAYER_INVENTORY_START_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int HOTBAR_END_INDEX = HOTBAR_START_INDEX + HOTBAR_SLOT_COUNT;

    private final CobblestoneFEGeneratorBlockEntity generatorBlockEntity;
    private final ContainerData generatorData;

    public CobblestoneFEGeneratorMenu(
        int containerId,
        Inventory playerInventory,
        CobblestoneFEGeneratorBlockEntity generatorBlockEntity,
        ContainerData generatorData
    ) {
        super(ModMenuType.COBBLESTONE_FE_GENERATOR_MENU.get(), containerId);
        this.generatorBlockEntity = generatorBlockEntity;
        this.generatorData = generatorData;
        ItemStackHandler itemStackHandler = generatorBlockEntity.getItemStackHandler();

        checkContainerDataCount(generatorData, DATA_COUNT);
        this.addDataSlots(generatorData);

        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneFEGeneratorBlockEntity.COBBLESTONE_SLOT_INDEX, MACHINE_SLOT_X, MACHINE_SLOT_Y));
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    public CobblestoneFEGeneratorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_MAX_STORED_POWER);
    }

    public long getStoredForgeEnergy() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_STORED_ENERGY);
    }

    public long getMaxForgeEnergy() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_MAX_STORED_ENERGY);
    }

    public long getLastConvertedForgeEnergy() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_CONVERTED_ENERGY);
    }

    public long getLastExportedForgeEnergy() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_EXPORTED_ENERGY);
    }

    public boolean getIsAvailable() {
        return this.generatorBlockEntity.getIsAvailable();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        int dataIndex = DATA_INDEX_AUTO_EXPORT - BaseBlockEntity.AUTOMATION_FACE_COUNT + automationSide.getIndex();
        return AutomationMode.fromId(this.generatorData.get(dataIndex));
    }

    public boolean isAutoExportEnabled() {
        return this.generatorData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public int getAutomationButtonId(AutomationSide automationSide) {
        return this.getAutomationButtonId(automationSide.getIndex());
    }

    public int getAutoExportButtonId() {
        return this.getAutoExportToggleButtonId();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            this.generatorBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.isAutomationButtonId(id)) {
            this.generatorBlockEntity.cycleAutomationMode(this.getAutomationIndexFromButtonId(id), FE_GENERATOR_AUTOMATION_MODES);
            return true;
        }

        if (this.handleAutoExportButtonClick(this.generatorBlockEntity, id)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.generatorBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.generatorBlockEntity.getBlockPos();
        if (!player.level().getBlockState(blockPos).is(ModBlocks.COBBLESTONE_FE_GENERATOR.get())) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.generatorBlockEntity) {
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
            boolean movedToMachine = CobblestonePoweredFurnaceBlockEntity.isCobblestonePowerItem(sourceStack)
                && this.moveItemStackTo(sourceStack, 0, MACHINE_SLOT_COUNT, false);

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

    private CobblestoneFEGeneratorMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getGeneratorBlockEntity(playerInventory, blockPos),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    private static CobblestoneFEGeneratorBlockEntity getGeneratorBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneFEGeneratorBlockEntity generatorBlockEntity) {
            return generatorBlockEntity;
        }

        throw new IllegalStateException("Cobblestone FE Generator の BlockEntity を取得できませんでした: " + blockPos);
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