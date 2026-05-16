package com.yukke9265.cobblestone_xx_compressed.menu;

import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneTankBlock;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneTankBlockEntity;
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

public class CobblestoneTankMenu extends BaseMenu {
    private static final AutomationMode[] TANK_ITEM_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT
    };
    private static final AutomationMode[] TANK_FLUID_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT
    };

    private static final int DATA_COUNT = 5 + BaseBlockEntity.AUTOMATION_FACE_COUNT * 2;
    private static final int DATA_INDEX_STORED_FLUID = 0;
    private static final int DATA_INDEX_MAX_FLUID = 2;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 4;
    private static final int DATA_INDEX_FLUID_AUTOMATION_START = DATA_INDEX_ITEM_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_FLUID_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;

    private static final int SLOT_SIZE = 18;
    private static final int INPUT_SLOT_X = 26;
    private static final int OUTPUT_SLOT_X = 132;
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

    private final CobblestoneTankBlockEntity tankBlockEntity;
    private final ContainerData tankData;

    public CobblestoneTankMenu(int containerId, Inventory playerInventory, CobblestoneTankBlockEntity tankBlockEntity, ContainerData tankData) {
        super(ModMenuType.COBBLESTONE_TANK_MENU.get(), containerId);
        this.tankBlockEntity = tankBlockEntity;
        this.tankData = tankData;

        checkContainerDataCount(tankData, DATA_COUNT);
        this.addDataSlots(tankData);

        this.addTankSlots();
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    public CobblestoneTankMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public long getStoredFluidAmount() {
        return this.getLongFromData(this.tankData, DATA_INDEX_STORED_FLUID);
    }

    public long getMaxFluidAmount() {
        return this.getLongFromData(this.tankData, DATA_INDEX_MAX_FLUID);
    }

    public ItemStack getDisplayedFluidContainer() {
        return this.tankBlockEntity.getDisplayedFluid().getFluid().getBucket().getDefaultInstance();
    }

    public net.neoforged.neoforge.fluids.FluidStack getDisplayedFluid() {
        return this.tankBlockEntity.getDisplayedFluid();
    }

    public AutomationMode getItemAutomationMode(AutomationSide automationSide) {
        return AutomationMode.fromId(this.tankData.get(DATA_INDEX_ITEM_AUTOMATION_START + automationSide.getIndex()));
    }

    public AutomationMode getFluidAutomationMode(AutomationSide automationSide) {
        return AutomationMode.fromId(this.tankData.get(DATA_INDEX_FLUID_AUTOMATION_START + automationSide.getIndex()));
    }

    public boolean isAutoExportEnabled() {
        return this.tankData.get(DATA_INDEX_AUTO_EXPORT) != 0;
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

    public int getFluidInteractionButtonId() {
        return super.getFluidIndicatorButtonId();
    }

    public int getFluidInteractionShiftButtonId() {
        return super.getFluidIndicatorShiftButtonId();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.isAutomationButtonId(id)) {
            this.tankBlockEntity.cycleAutomationMode(this.getAutomationIndexFromButtonId(id), TANK_ITEM_AUTOMATION_MODES);
            return true;
        }

        if (this.isFluidAutomationButtonId(id)) {
            this.tankBlockEntity.cycleFluidAutomationMode(this.getFluidAutomationIndexFromButtonId(id), TANK_FLUID_AUTOMATION_MODES);
            return true;
        }

        if (this.isFluidIndicatorButtonId(id)) {
            return this.tankBlockEntity.handleFluidIndicatorClick(player, false);
        }

        if (this.isFluidIndicatorShiftButtonId(id)) {
            return this.tankBlockEntity.handleFluidIndicatorClick(player, true);
        }

        return this.handleAutoExportButtonClick(this.tankBlockEntity, id);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.tankBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.tankBlockEntity.getBlockPos();
        if (!(player.level().getBlockState(blockPos).getBlock() instanceof CobblestoneTankBlock)) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.tankBlockEntity) {
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
            boolean movedToMachine = CobblestoneTankBlockEntity.isFluidContainerItem(sourceStack)
                && this.moveItemStackTo(sourceStack, CobblestoneTankBlockEntity.INPUT_SLOT_INDEX, CobblestoneTankBlockEntity.INPUT_SLOT_INDEX + 1, false);

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

    private CobblestoneTankMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(containerId, playerInventory, getTankBlockEntity(playerInventory, blockPos), new SimpleContainerData(DATA_COUNT));
    }

    private static CobblestoneTankBlockEntity getTankBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneTankBlockEntity tankBlockEntity) {
            return tankBlockEntity;
        }

        throw new IllegalStateException("Cobblestone Tank の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addTankSlots() {
        ItemStackHandler itemStackHandler = this.tankBlockEntity.getItemStackHandler();
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneTankBlockEntity.INPUT_SLOT_INDEX, INPUT_SLOT_X, MACHINE_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneTankBlockEntity.OUTPUT_SLOT_INDEX, OUTPUT_SLOT_X, MACHINE_SLOT_Y) {
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