package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneExtremeCompressorBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.MachineUpgradeHelper;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiRecipeTransferDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CobblestoneExtremeCompressorMenu extends BaseMenu {
    private static final AutomationMode[] EXTREME_COMPRESSOR_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT,
        AutomationMode.COBBLESTONE_INPUT
    };

    private static final int DATA_COUNT = 12 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_ITEM_COUNT = 6;
    private static final int DATA_INDEX_REQUIRED_ITEM_COUNT = 7;
    private static final int DATA_INDEX_STORED_ITEM_ID = 8;
    private static final int DATA_INDEX_CURRENT_POWER_RATE = 9 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_CURRENT_POWER_RATE_UPPER = DATA_INDEX_CURRENT_POWER_RATE + 1;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_CURRENT_POWER_RATE_UPPER + 1;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int HOTBAR_SLOT_COUNT = 9;

    private static final int MACHINE_SLOT_COUNT = 5;
    private static final int PLAYER_INVENTORY_START_INDEX = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS;
    private static final int HOTBAR_START_INDEX = PLAYER_INVENTORY_START_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int HOTBAR_END_INDEX = HOTBAR_START_INDEX + HOTBAR_SLOT_COUNT;

    private final CobblestoneExtremeCompressorBlockEntity extremeCompressorBlockEntity;
    private final ContainerData extremeCompressorData;

    public CobblestoneExtremeCompressorMenu(
        int containerId,
        Inventory playerInventory,
        CobblestoneExtremeCompressorBlockEntity extremeCompressorBlockEntity,
        ContainerData extremeCompressorData
    ) {
        super(ModMenuType.COBBLESTONE_EXTREME_COMPRESSOR_MENU.get(), containerId);
        this.extremeCompressorBlockEntity = extremeCompressorBlockEntity;
        this.extremeCompressorData = extremeCompressorData;

        checkContainerDataCount(extremeCompressorData, DATA_COUNT);
        this.addDataSlots(extremeCompressorData);

        this.addExtremeCompressorSlots();
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    public CobblestoneExtremeCompressorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public int getProgress() {
        return this.extremeCompressorData.get(DATA_INDEX_PROGRESS);
    }

    public int getMaxProgress() {
        return this.extremeCompressorData.get(DATA_INDEX_MAX_PROGRESS);
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.extremeCompressorData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.extremeCompressorData, DATA_INDEX_STORED_POWER + 2);
    }

    @Override
    public long getCurrentCobblestonePowerRate() {
        return this.getLongFromData(this.extremeCompressorData, DATA_INDEX_CURRENT_POWER_RATE);
    }

    public int getStoredInputItemCount() {
        return this.extremeCompressorData.get(DATA_INDEX_STORED_ITEM_COUNT);
    }

    public int getRequiredItemCount() {
        return this.extremeCompressorData.get(DATA_INDEX_REQUIRED_ITEM_COUNT);
    }

    public boolean getIsAvailable() {
        return this.extremeCompressorBlockEntity.getIsAvailable();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        int dataIndex = DATA_INDEX_STORED_ITEM_ID + 1 + automationSide.getIndex();
        return AutomationMode.fromId(this.extremeCompressorData.get(dataIndex));
    }

    public boolean isAutoExportEnabled() {
        return this.extremeCompressorData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public int getAutomationButtonId(AutomationSide automationSide) {
        return this.getAutomationButtonId(automationSide.getIndex());
    }

    public int getAutoExportButtonId() {
        return this.getAutoExportToggleButtonId();
    }

    public ItemStack getDisplayedInputItem() {
        int storedItemId = this.extremeCompressorData.get(DATA_INDEX_STORED_ITEM_ID);
        if (storedItemId >= 0 && this.getStoredInputItemCount() > 0) {
            Item item = BuiltInRegistries.ITEM.byId(storedItemId);
            if (item != null) {
                return new ItemStack(item);
            }
        }

        Slot inputSlot = this.slots.get(CobblestoneExtremeCompressorBlockEntity.INPUT_SLOT_INDEX);
        return inputSlot.getItem().copy();
    }

    public Component getDisplayedInputItemName() {
        ItemStack displayedItem = this.getDisplayedInputItem();
        if (displayedItem.isEmpty()) {
            return Component.empty();
        }

        return displayedItem.getHoverName();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            this.extremeCompressorBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.handleAutomationButtonClick(this.extremeCompressorBlockEntity, id, EXTREME_COMPRESSOR_AUTOMATION_MODES)) {
            return true;
        }

        if (this.handleAutoExportButtonClick(this.extremeCompressorBlockEntity, id)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.extremeCompressorBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.extremeCompressorBlockEntity.getBlockPos();
        if (!player.level().getBlockState(blockPos).is(ModBlocks.COBBLESTONE_EXTREME_COMPRESSOR.get())) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.extremeCompressorBlockEntity) {
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
            if (MachineUpgradeHelper.isAccelerationChip(sourceStack)) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneExtremeCompressorBlockEntity.ACCELERATION_SLOT_INDEX,
                    CobblestoneExtremeCompressorBlockEntity.ACCELERATION_SLOT_INDEX + 1,
                    false
                );
            } else if (MachineUpgradeHelper.isEnergizedCube(sourceStack)) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneExtremeCompressorBlockEntity.ENERGIZED_CUBE_SLOT_INDEX,
                    CobblestoneExtremeCompressorBlockEntity.ENERGIZED_CUBE_SLOT_INDEX + 1,
                    false
                );
            } else if (CobblestoneExtremeCompressorBlockEntity.isCobblestonePowerItem(sourceStack)) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneExtremeCompressorBlockEntity.POWER_SLOT_INDEX,
                    CobblestoneExtremeCompressorBlockEntity.POWER_SLOT_INDEX + 1,
                    false
                );
            }

            if (!movedToMachine) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneExtremeCompressorBlockEntity.INPUT_SLOT_INDEX,
                    CobblestoneExtremeCompressorBlockEntity.INPUT_SLOT_INDEX + 1,
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

    private CobblestoneExtremeCompressorMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getExtremeCompressorBlockEntity(playerInventory, blockPos),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    private static CobblestoneExtremeCompressorBlockEntity getExtremeCompressorBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneExtremeCompressorBlockEntity extremeCompressorBlockEntity) {
            return extremeCompressorBlockEntity;
        }

        throw new IllegalStateException("Cobblestone Extreme Compressor の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addExtremeCompressorSlots() {
        ItemStackHandler itemStackHandler = this.extremeCompressorBlockEntity.getItemStackHandler();

        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneExtremeCompressorBlockEntity.INPUT_SLOT_INDEX, MachineGuiLayouts.ExtremeCompressor.INPUT_SLOT_X, MachineGuiLayouts.ExtremeCompressor.MACHINE_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneExtremeCompressorBlockEntity.POWER_SLOT_INDEX, MachineGuiLayouts.ExtremeCompressor.POWER_SLOT_X, MachineGuiLayouts.ExtremeCompressor.POWER_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CobblestoneExtremeCompressorBlockEntity.isCobblestonePowerItem(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneExtremeCompressorBlockEntity.OUTPUT_SLOT_INDEX, MachineGuiLayouts.ExtremeCompressor.OUTPUT_SLOT_X, MachineGuiLayouts.ExtremeCompressor.MACHINE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneExtremeCompressorBlockEntity.ACCELERATION_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ACCELERATION_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneExtremeCompressorBlockEntity.ENERGIZED_CUBE_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isEnergizedCube(stack);
            }
        });
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

    @Override
    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        return this.createSingleJeiRecipeTransferDefinition(
            ModJeiIds.COBBLESTONE_EXTREME_COMPRESSOR,
            CobblestoneExtremeCompressorBlockEntity.INPUT_SLOT_INDEX,
            1,
            PLAYER_INVENTORY_START_INDEX,
            PLAYER_INVENTORY_SLOT_COUNT + HOTBAR_SLOT_COUNT
        );
    }
}