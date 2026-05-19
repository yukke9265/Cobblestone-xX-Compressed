package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneAssemblyMachineBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneCrusherBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.MachineUpgradeHelper;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiRecipeTransferDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

@SuppressWarnings("null")
public class CobblestoneAssemblyMachineMenu extends BaseMenu {
    private static final AutomationMode[] ITEM_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.INPUT_1,
        AutomationMode.INPUT_2,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT,
        AutomationMode.COBBLESTONE_INPUT
    };
    private static final AutomationMode[] FLUID_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.IN_OUT
    };

    private static final int DATA_COUNT = 12 + BaseBlockEntity.AUTOMATION_FACE_COUNT * 2;
    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_INPUT_FLUID = 6;
    private static final int DATA_INDEX_MAX_INPUT_FLUID = 8;
    private static final int DATA_INDEX_INPUT_FLUID_ID = 10;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 11;
    private static final int DATA_INDEX_FLUID_AUTOMATION_START = DATA_INDEX_ITEM_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_FLUID_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int MACHINE_SLOT_COUNT = 10;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_START_INDEX = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS;
    private static final int HOTBAR_START_INDEX = PLAYER_INVENTORY_START_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int HOTBAR_END_INDEX = HOTBAR_START_INDEX + HOTBAR_SLOT_COUNT;

    private final CobblestoneAssemblyMachineBlockEntity assemblyMachineBlockEntity;
    private final ContainerData assemblyMachineData;

    public CobblestoneAssemblyMachineMenu(
        int containerId,
        Inventory playerInventory,
        CobblestoneAssemblyMachineBlockEntity assemblyMachineBlockEntity,
        ContainerData assemblyMachineData
    ) {
        super(ModMenuType.COBBLESTONE_ASSEMBLY_MACHINE_MENU.get(), containerId);
        this.assemblyMachineBlockEntity = assemblyMachineBlockEntity;
        this.assemblyMachineData = assemblyMachineData;

        checkContainerDataCount(assemblyMachineData, DATA_COUNT);
        this.addDataSlots(assemblyMachineData);

        this.addAssemblyMachineSlots();
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    public CobblestoneAssemblyMachineMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public int getProgress() {
        return this.assemblyMachineData.get(DATA_INDEX_PROGRESS);
    }

    public int getMaxProgress() {
        return this.assemblyMachineData.get(DATA_INDEX_MAX_PROGRESS);
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.assemblyMachineData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.assemblyMachineData, DATA_INDEX_MAX_STORED_POWER);
    }

    public long getStoredInputFluidAmount() {
        return this.getLongFromData(this.assemblyMachineData, DATA_INDEX_INPUT_FLUID);
    }

    public long getMaxInputFluidAmount() {
        return this.getLongFromData(this.assemblyMachineData, DATA_INDEX_MAX_INPUT_FLUID);
    }

    public FluidStack getDisplayedInputFluid() {
        return this.getFluidFromData(this.assemblyMachineData, DATA_INDEX_INPUT_FLUID, DATA_INDEX_INPUT_FLUID_ID);
    }

    public boolean getIsAvailable() {
        return this.assemblyMachineBlockEntity.getIsAvailable();
    }

    public AutomationMode getItemAutomationMode(AutomationSide automationSide) {
        return AutomationMode.fromId(this.assemblyMachineData.get(DATA_INDEX_ITEM_AUTOMATION_START + automationSide.getIndex()));
    }

    public AutomationMode getFluidAutomationMode(AutomationSide automationSide) {
        return AutomationMode.fromId(this.assemblyMachineData.get(DATA_INDEX_FLUID_AUTOMATION_START + automationSide.getIndex()));
    }

    public boolean isAutoExportEnabled() {
        return this.assemblyMachineData.get(DATA_INDEX_AUTO_EXPORT) != 0;
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
    public boolean clickMenuButton(@Nonnull Player player, int id) {
        if (id == 0) {
            this.assemblyMachineBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.isAutomationButtonId(id)) {
            this.assemblyMachineBlockEntity.cycleAutomationMode(this.getAutomationIndexFromButtonId(id), ITEM_AUTOMATION_MODES);
            return true;
        }

        if (this.isFluidAutomationButtonId(id)) {
            this.assemblyMachineBlockEntity.cycleFluidAutomationMode(this.getFluidAutomationIndexFromButtonId(id), FLUID_AUTOMATION_MODES);
            return true;
        }

        if (this.isFluidIndicatorButtonId(id)) {
            return this.assemblyMachineBlockEntity.handleFluidIndicatorClick(player, false);
        }

        if (this.isFluidIndicatorShiftButtonId(id)) {
            return this.assemblyMachineBlockEntity.handleFluidIndicatorClick(player, true);
        }

        return this.handleAutoExportButtonClick(this.assemblyMachineBlockEntity, id);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.assemblyMachineBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.assemblyMachineBlockEntity.getBlockPos();
        if (!player.level().getBlockState(blockPos).is(ModBlocks.COBBLESTONE_ASSEMBLY_MACHINE.get())) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.assemblyMachineBlockEntity) {
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
            if (MachineUpgradeHelper.isAccelerationChip(sourceStack)) {
                movedToMachine = this.moveItemStackTo(sourceStack, CobblestoneAssemblyMachineBlockEntity.ACCELERATION_SLOT_INDEX, CobblestoneAssemblyMachineBlockEntity.ACCELERATION_SLOT_INDEX + 1, false);
            } else if (MachineUpgradeHelper.isEnergizedCube(sourceStack)) {
                movedToMachine = this.moveItemStackTo(sourceStack, CobblestoneAssemblyMachineBlockEntity.ENERGIZED_CUBE_SLOT_INDEX, CobblestoneAssemblyMachineBlockEntity.ENERGIZED_CUBE_SLOT_INDEX + 1, false);
            } else if (CobblestoneCrusherBlockEntity.isCobblestonePowerItem(sourceStack)) {
                movedToMachine = this.moveItemStackTo(sourceStack, CobblestoneAssemblyMachineBlockEntity.POWER_SLOT_INDEX, CobblestoneAssemblyMachineBlockEntity.POWER_SLOT_INDEX + 1, false);
            }

            if (!movedToMachine) {
                movedToMachine = this.moveItemStackTo(sourceStack, CobblestoneAssemblyMachineBlockEntity.INPUT_SLOT_1_INDEX, CobblestoneAssemblyMachineBlockEntity.POWER_SLOT_INDEX, false);
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

    @Override
    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        return this.createSingleJeiRecipeTransferDefinition(
            ModJeiIds.COBBLESTONE_ASSEMBLY_MACHINE,
            CobblestoneAssemblyMachineBlockEntity.INPUT_SLOT_1_INDEX,
            6,
            PLAYER_INVENTORY_START_INDEX,
            PLAYER_INVENTORY_SLOT_COUNT + HOTBAR_SLOT_COUNT
        );
    }

    private CobblestoneAssemblyMachineMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(containerId, playerInventory, getAssemblyMachineBlockEntity(playerInventory, blockPos), new SimpleContainerData(DATA_COUNT));
    }

    private static CobblestoneAssemblyMachineBlockEntity getAssemblyMachineBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneAssemblyMachineBlockEntity assemblyMachineBlockEntity) {
            return assemblyMachineBlockEntity;
        }

        throw new IllegalStateException("Cobblestone Assembly Machine の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addAssemblyMachineSlots() {
        ItemStackHandler itemStackHandler = this.assemblyMachineBlockEntity.getItemStackHandler();

        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneAssemblyMachineBlockEntity.INPUT_SLOT_1_INDEX, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_1_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_1_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneAssemblyMachineBlockEntity.INPUT_SLOT_2_INDEX, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_2_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_2_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneAssemblyMachineBlockEntity.INPUT_SLOT_3_INDEX, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_3_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_3_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneAssemblyMachineBlockEntity.INPUT_SLOT_4_INDEX, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_4_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_4_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneAssemblyMachineBlockEntity.INPUT_SLOT_5_INDEX, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_5_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_5_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneAssemblyMachineBlockEntity.INPUT_SLOT_6_INDEX, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_6_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_6_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneAssemblyMachineBlockEntity.POWER_SLOT_INDEX, MachineGuiLayouts.AssemblyMachine.POWER_SLOT_X, MachineGuiLayouts.AssemblyMachine.POWER_SLOT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return CobblestoneCrusherBlockEntity.isCobblestonePowerItem(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneAssemblyMachineBlockEntity.OUTPUT_SLOT_INDEX, MachineGuiLayouts.AssemblyMachine.OUTPUT_SLOT_X, MachineGuiLayouts.AssemblyMachine.OUTPUT_SLOT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneAssemblyMachineBlockEntity.ACCELERATION_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ACCELERATION_SLOT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneAssemblyMachineBlockEntity.ENERGIZED_CUBE_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
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
        for (int column = 0; column < HOTBAR_SLOT_COUNT; column++) {
            this.addSlot(new Slot(playerInventory, column, MachineGuiLayouts.PLAYER_INVENTORY_START_X + column * MachineGuiLayouts.SLOT_SIZE, MachineGuiLayouts.HOTBAR_START_Y));
        }
    }
}