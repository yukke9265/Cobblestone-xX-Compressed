package com.yukke9265.cobblestone_xx_compressed.menu;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestonePowerHelper;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneWaterGeneratorBlockEntity;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CobblestoneWaterGeneratorMenu extends BaseMenu {
    private static final AutomationMode[] ITEM_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.COBBLESTONE_INPUT,
        AutomationMode.IN_OUT
    };
    private static final AutomationMode[] FLUID_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT
    };

    private static final int DATA_COUNT = 28;
    private static final int DATA_INDEX_STORED_POWER = 0;
    private static final int DATA_INDEX_MAX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_FLUID = 4;
    private static final int DATA_INDEX_MAX_FLUID = 6;
    private static final int DATA_INDEX_FLUID_ID = 8;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 9;
    private static final int DATA_INDEX_FLUID_AUTOMATION_START = DATA_INDEX_ITEM_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_FLUID_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_INSERT = DATA_INDEX_AUTO_EXPORT + 1;
    private static final int DATA_INDEX_SOUND_MUTED = DATA_INDEX_AUTO_INSERT + 1;
    private static final int DATA_INDEX_CONVERTED_FLUID = DATA_INDEX_SOUND_MUTED + 1;
    private static final int DATA_INDEX_CURRENT_POWER_RATE = DATA_INDEX_CONVERTED_FLUID + 2;

    private static final int SLOT_SIZE = 18;
    private static final int POWER_SLOT_X = 26;
    private static final int BUCKET_SLOT_X = 132;
    private static final int MACHINE_SLOT_Y = 17;
    private static final int MACHINE_SLOT_COUNT = 2;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_START_X = 8;
    private static final int PLAYER_INVENTORY_START_Y = 84;
    private static final int HOTBAR_START_Y = 142;
    private static final int PLAYER_INVENTORY_START_INDEX = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMNS * PLAYER_INVENTORY_ROWS;
    private static final int HOTBAR_START_INDEX = PLAYER_INVENTORY_START_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int HOTBAR_END_INDEX = HOTBAR_START_INDEX + HOTBAR_SLOT_COUNT;

    private final CobblestoneWaterGeneratorBlockEntity waterGeneratorBlockEntity;
    private final ContainerData generatorData;

    public CobblestoneWaterGeneratorMenu(
        int containerId,
        Inventory playerInventory,
        CobblestoneWaterGeneratorBlockEntity waterGeneratorBlockEntity,
        ContainerData generatorData
    ) {
        super(ModMenuType.COBBLESTONE_WATER_GENERATOR_MENU.get(), containerId);
        this.waterGeneratorBlockEntity = waterGeneratorBlockEntity;
        this.generatorData = generatorData;

        checkContainerDataCount(generatorData, DATA_COUNT);
        this.addDataSlots(generatorData);
        this.addMachineSlots();
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    public CobblestoneWaterGeneratorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_MAX_STORED_POWER);
    }

    public long getStoredFluidAmount() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_STORED_FLUID);
    }

    public long getMaxFluidAmount() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_MAX_FLUID);
    }

    public long getLastConvertedFluidAmount() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_CONVERTED_FLUID);
    }

    @Override
    public long getCurrentCobblestonePowerRate() {
        return this.getLongFromData(this.generatorData, DATA_INDEX_CURRENT_POWER_RATE);
    }

    public FluidStack getDisplayedFluid() {
        return this.getFluidFromData(this.generatorData, DATA_INDEX_STORED_FLUID, DATA_INDEX_FLUID_ID);
    }

    public boolean getIsAvailable() {
        return this.waterGeneratorBlockEntity.getIsAvailable();
    }

    public AutomationMode getItemAutomationMode(AutomationSide automationSide) {
        return AutomationMode.fromId(this.generatorData.get(DATA_INDEX_ITEM_AUTOMATION_START + automationSide.getIndex()));
    }

    public AutomationMode getFluidAutomationMode(AutomationSide automationSide) {
        return AutomationMode.fromId(this.generatorData.get(DATA_INDEX_FLUID_AUTOMATION_START + automationSide.getIndex()));
    }

    public boolean isAutoExportEnabled() {
        return this.generatorData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public boolean isAutoInsertEnabled() {
        return this.generatorData.get(DATA_INDEX_AUTO_INSERT) != 0;
    }

    @Override
    public boolean isSoundMuted() {
        return this.generatorData.get(DATA_INDEX_SOUND_MUTED) != 0;
    }

    public int getItemAutomationButtonId(AutomationSide automationSide) {
        return this.getAutomationButtonId(automationSide.getIndex());
    }

    public int getFluidAutomationButtonId(AutomationSide automationSide) {
        return this.getFluidAutomationButtonId(automationSide.getIndex());
    }

    public int getAutoExportButtonId() {
        return this.getAutoExportToggleButtonId();
    }

    public int getAutoInsertButtonId() {
        return this.getAutoInsertToggleButtonId();
    }

    public int getFluidInteractionButtonId() {
        return super.getFluidIndicatorButtonId();
    }

    public int getFluidInteractionShiftButtonId() {
        return super.getFluidIndicatorShiftButtonId();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            this.waterGeneratorBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.handleAutomationButtonClick(this.waterGeneratorBlockEntity, id, ITEM_AUTOMATION_MODES)) {
            return true;
        }

        if (this.handleFluidAutomationButtonClick(this.waterGeneratorBlockEntity, id, FLUID_AUTOMATION_MODES)) {
            return true;
        }

        if (this.isFluidIndicatorButtonId(id)) {
            return this.waterGeneratorBlockEntity.handleFluidIndicatorClick(player, false);
        }

        if (this.isFluidIndicatorShiftButtonId(id)) {
            return this.waterGeneratorBlockEntity.handleFluidIndicatorClick(player, true);
        }

        if (this.handleAutoExportButtonClick(this.waterGeneratorBlockEntity, id)) {
            return true;
        }

        if (this.handleAutoInsertButtonClick(this.waterGeneratorBlockEntity, id)) {
            return true;
        }

        return this.handleMuteSoundButtonClick(this.waterGeneratorBlockEntity, id);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.waterGeneratorBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.waterGeneratorBlockEntity.getBlockPos();
        if (!player.level().getBlockState(blockPos).is(ModBlocks.COBBLESTONE_WATER_GENERATOR.get())) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.waterGeneratorBlockEntity) {
            return false;
        }

        return player.distanceToSqr(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D) <= 64.0D;
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
            if (CobblestoneWaterGeneratorBlockEntity.isEmptyFluidContainerItem(sourceStack)) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneWaterGeneratorBlockEntity.BUCKET_SLOT_INDEX,
                    CobblestoneWaterGeneratorBlockEntity.BUCKET_SLOT_INDEX + 1,
                    false
                );
            }

            if (!movedToMachine && CobblestonePowerHelper.isCobblestonePowerItem(sourceStack)) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneWaterGeneratorBlockEntity.POWER_SLOT_INDEX,
                    CobblestoneWaterGeneratorBlockEntity.POWER_SLOT_INDEX + 1,
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

    private CobblestoneWaterGeneratorMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getWaterGeneratorBlockEntity(playerInventory, blockPos),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    private static CobblestoneWaterGeneratorBlockEntity getWaterGeneratorBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneWaterGeneratorBlockEntity waterGeneratorBlockEntity) {
            return waterGeneratorBlockEntity;
        }

        throw new IllegalStateException("Cobblestone Water Generator の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addMachineSlots() {
        ItemStackHandler itemStackHandler = this.waterGeneratorBlockEntity.getItemStackHandler();
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneWaterGeneratorBlockEntity.POWER_SLOT_INDEX, POWER_SLOT_X, MACHINE_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneWaterGeneratorBlockEntity.BUCKET_SLOT_INDEX, BUCKET_SLOT_X, MACHINE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
                int slotIndex = column + row * PLAYER_INVENTORY_COLUMNS + PLAYER_INVENTORY_COLUMNS;
                int x = PLAYER_INVENTORY_START_X + column * SLOT_SIZE;
                int y = PLAYER_INVENTORY_START_Y + row * SLOT_SIZE;
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }
    }

    private void addPlayerHotbarSlots(Inventory playerInventory) {
        for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
            int x = PLAYER_INVENTORY_START_X + column * SLOT_SIZE;
            this.addSlot(new Slot(playerInventory, column, x, HOTBAR_START_Y));
        }
    }
}
