package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneChemicalReactorBlockEntity;
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

public class CobblestoneChemicalReactorMenu extends BaseMenu {
    private static final AutomationMode[] ITEM_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.INPUT_1,
        AutomationMode.INPUT_2,
        AutomationMode.OUTPUT,
        AutomationMode.OUTPUT_1,
        AutomationMode.OUTPUT_2,
        AutomationMode.IN_OUT,
        AutomationMode.COBBLESTONE_INPUT
    };
    private static final AutomationMode[] FLUID_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.INPUT_1,
        AutomationMode.INPUT_2,
        AutomationMode.OUTPUT,
        AutomationMode.OUTPUT_1,
        AutomationMode.OUTPUT_2,
        AutomationMode.IN_OUT
    };

    private static final int DATA_COUNT = 27 + BaseBlockEntity.AUTOMATION_FACE_COUNT * 2;
    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_INPUT_FLUID_1 = 6;
    private static final int DATA_INDEX_MAX_INPUT_FLUID_1 = 8;
    private static final int DATA_INDEX_INPUT_FLUID_1_ID = 10;
    private static final int DATA_INDEX_INPUT_FLUID_2 = 11;
    private static final int DATA_INDEX_MAX_INPUT_FLUID_2 = 13;
    private static final int DATA_INDEX_INPUT_FLUID_2_ID = 15;
    private static final int DATA_INDEX_OUTPUT_FLUID_1 = 16;
    private static final int DATA_INDEX_MAX_OUTPUT_FLUID_1 = 18;
    private static final int DATA_INDEX_OUTPUT_FLUID_1_ID = 20;
    private static final int DATA_INDEX_OUTPUT_FLUID_2 = 21;
    private static final int DATA_INDEX_MAX_OUTPUT_FLUID_2 = 23;
    private static final int DATA_INDEX_OUTPUT_FLUID_2_ID = 25;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 26;
    private static final int DATA_INDEX_FLUID_AUTOMATION_START = DATA_INDEX_ITEM_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_FLUID_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;

    private static final int INPUT_FLUID_2_BUTTON_ID = 402;
    private static final int INPUT_FLUID_2_SHIFT_BUTTON_ID = 403;
    private static final int OUTPUT_FLUID_1_BUTTON_ID = 404;
    private static final int OUTPUT_FLUID_1_SHIFT_BUTTON_ID = 405;
    private static final int OUTPUT_FLUID_2_BUTTON_ID = 406;
    private static final int OUTPUT_FLUID_2_SHIFT_BUTTON_ID = 407;
    private static final int MACHINE_SLOT_COUNT = 7;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_START_INDEX = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS;
    private static final int HOTBAR_START_INDEX = PLAYER_INVENTORY_START_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int HOTBAR_END_INDEX = HOTBAR_START_INDEX + HOTBAR_SLOT_COUNT;

    private final CobblestoneChemicalReactorBlockEntity chemicalReactorBlockEntity;
    private final ContainerData chemicalReactorData;

    public CobblestoneChemicalReactorMenu(
        int containerId,
        Inventory playerInventory,
        CobblestoneChemicalReactorBlockEntity chemicalReactorBlockEntity,
        ContainerData chemicalReactorData
    ) {
        super(ModMenuType.COBBLESTONE_CHEMICAL_REACTOR_MENU.get(), containerId);
        this.chemicalReactorBlockEntity = chemicalReactorBlockEntity;
        this.chemicalReactorData = chemicalReactorData;

        checkContainerDataCount(chemicalReactorData, DATA_COUNT);
        this.addDataSlots(chemicalReactorData);

        this.addChemicalReactorSlots();
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    public CobblestoneChemicalReactorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public int getProgress() {
        return this.chemicalReactorData.get(DATA_INDEX_PROGRESS);
    }

    public int getMaxProgress() {
        return this.chemicalReactorData.get(DATA_INDEX_MAX_PROGRESS);
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.chemicalReactorData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.chemicalReactorData, DATA_INDEX_MAX_STORED_POWER);
    }

    public long getStoredInputFluid1Amount() {
        return this.getLongFromData(this.chemicalReactorData, DATA_INDEX_INPUT_FLUID_1);
    }

    public long getMaxInputFluid1Amount() {
        return this.getLongFromData(this.chemicalReactorData, DATA_INDEX_MAX_INPUT_FLUID_1);
    }

    public FluidStack getDisplayedInputFluid1() {
        return this.getFluidFromData(this.chemicalReactorData, DATA_INDEX_INPUT_FLUID_1, DATA_INDEX_INPUT_FLUID_1_ID);
    }

    public long getStoredInputFluid2Amount() {
        return this.getLongFromData(this.chemicalReactorData, DATA_INDEX_INPUT_FLUID_2);
    }

    public long getMaxInputFluid2Amount() {
        return this.getLongFromData(this.chemicalReactorData, DATA_INDEX_MAX_INPUT_FLUID_2);
    }

    public FluidStack getDisplayedInputFluid2() {
        return this.getFluidFromData(this.chemicalReactorData, DATA_INDEX_INPUT_FLUID_2, DATA_INDEX_INPUT_FLUID_2_ID);
    }

    public long getStoredOutputFluid1Amount() {
        return this.getLongFromData(this.chemicalReactorData, DATA_INDEX_OUTPUT_FLUID_1);
    }

    public long getMaxOutputFluid1Amount() {
        return this.getLongFromData(this.chemicalReactorData, DATA_INDEX_MAX_OUTPUT_FLUID_1);
    }

    public FluidStack getDisplayedOutputFluid1() {
        return this.getFluidFromData(this.chemicalReactorData, DATA_INDEX_OUTPUT_FLUID_1, DATA_INDEX_OUTPUT_FLUID_1_ID);
    }

    public long getStoredOutputFluid2Amount() {
        return this.getLongFromData(this.chemicalReactorData, DATA_INDEX_OUTPUT_FLUID_2);
    }

    public long getMaxOutputFluid2Amount() {
        return this.getLongFromData(this.chemicalReactorData, DATA_INDEX_MAX_OUTPUT_FLUID_2);
    }

    public FluidStack getDisplayedOutputFluid2() {
        return this.getFluidFromData(this.chemicalReactorData, DATA_INDEX_OUTPUT_FLUID_2, DATA_INDEX_OUTPUT_FLUID_2_ID);
    }

    public boolean getIsAvailable() {
        return this.chemicalReactorBlockEntity.getIsAvailable();
    }

    public AutomationMode getItemAutomationMode(AutomationSide automationSide) {
        return AutomationMode.fromId(this.chemicalReactorData.get(DATA_INDEX_ITEM_AUTOMATION_START + automationSide.getIndex()));
    }

    public AutomationMode getFluidAutomationMode(AutomationSide automationSide) {
        return AutomationMode.fromId(this.chemicalReactorData.get(DATA_INDEX_FLUID_AUTOMATION_START + automationSide.getIndex()));
    }

    public boolean isAutoExportEnabled() {
        return this.chemicalReactorData.get(DATA_INDEX_AUTO_EXPORT) != 0;
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

    public int getInputFluid1InteractionButtonId() {
        return super.getFluidIndicatorButtonId();
    }

    public int getInputFluid1InteractionShiftButtonId() {
        return super.getFluidIndicatorShiftButtonId();
    }

    public int getInputFluid2InteractionButtonId() {
        return INPUT_FLUID_2_BUTTON_ID;
    }

    public int getInputFluid2InteractionShiftButtonId() {
        return INPUT_FLUID_2_SHIFT_BUTTON_ID;
    }

    public int getOutputFluid1InteractionButtonId() {
        return OUTPUT_FLUID_1_BUTTON_ID;
    }

    public int getOutputFluid1InteractionShiftButtonId() {
        return OUTPUT_FLUID_1_SHIFT_BUTTON_ID;
    }

    public int getOutputFluid2InteractionButtonId() {
        return OUTPUT_FLUID_2_BUTTON_ID;
    }

    public int getOutputFluid2InteractionShiftButtonId() {
        return OUTPUT_FLUID_2_SHIFT_BUTTON_ID;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            this.chemicalReactorBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.isAutomationButtonId(id)) {
            this.chemicalReactorBlockEntity.cycleAutomationMode(this.getAutomationIndexFromButtonId(id), ITEM_AUTOMATION_MODES);
            return true;
        }

        if (this.isFluidAutomationButtonId(id)) {
            this.chemicalReactorBlockEntity.cycleFluidAutomationMode(this.getFluidAutomationIndexFromButtonId(id), FLUID_AUTOMATION_MODES);
            return true;
        }

        if (this.isFluidIndicatorButtonId(id)) {
            return this.chemicalReactorBlockEntity.handleInputFluid1IndicatorClick(player, false);
        }

        if (this.isFluidIndicatorShiftButtonId(id)) {
            return this.chemicalReactorBlockEntity.handleInputFluid1IndicatorClick(player, true);
        }

        if (id == INPUT_FLUID_2_BUTTON_ID) {
            return this.chemicalReactorBlockEntity.handleInputFluid2IndicatorClick(player, false);
        }

        if (id == INPUT_FLUID_2_SHIFT_BUTTON_ID) {
            return this.chemicalReactorBlockEntity.handleInputFluid2IndicatorClick(player, true);
        }

        if (id == OUTPUT_FLUID_1_BUTTON_ID) {
            return this.chemicalReactorBlockEntity.handleOutputFluid1IndicatorClick(player, false);
        }

        if (id == OUTPUT_FLUID_1_SHIFT_BUTTON_ID) {
            return this.chemicalReactorBlockEntity.handleOutputFluid1IndicatorClick(player, true);
        }

        if (id == OUTPUT_FLUID_2_BUTTON_ID) {
            return this.chemicalReactorBlockEntity.handleOutputFluid2IndicatorClick(player, false);
        }

        if (id == OUTPUT_FLUID_2_SHIFT_BUTTON_ID) {
            return this.chemicalReactorBlockEntity.handleOutputFluid2IndicatorClick(player, true);
        }

        return this.handleAutoExportButtonClick(this.chemicalReactorBlockEntity, id);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.chemicalReactorBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.chemicalReactorBlockEntity.getBlockPos();
        if (!player.level().getBlockState(blockPos).is(ModBlocks.COBBLESTONE_CHEMICAL_REACTOR.get())) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.chemicalReactorBlockEntity) {
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
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneChemicalReactorBlockEntity.ACCELERATION_SLOT_INDEX,
                    CobblestoneChemicalReactorBlockEntity.ACCELERATION_SLOT_INDEX + 1,
                    false
                );
            } else if (MachineUpgradeHelper.isEnergizedCube(sourceStack)) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneChemicalReactorBlockEntity.ENERGIZED_CUBE_SLOT_INDEX,
                    CobblestoneChemicalReactorBlockEntity.ENERGIZED_CUBE_SLOT_INDEX + 1,
                    false
                );
            } else if (CobblestoneCrusherBlockEntity.isCobblestonePowerItem(sourceStack)) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneChemicalReactorBlockEntity.POWER_SLOT_INDEX,
                    CobblestoneChemicalReactorBlockEntity.POWER_SLOT_INDEX + 1,
                    false
                );
            }

            if (!movedToMachine) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneChemicalReactorBlockEntity.INPUT_SLOT_1_INDEX,
                    CobblestoneChemicalReactorBlockEntity.POWER_SLOT_INDEX,
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

    @Override
    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        return this.createSingleJeiRecipeTransferDefinition(
            ModJeiIds.COBBLESTONE_CHEMICAL_REACTOR,
            CobblestoneChemicalReactorBlockEntity.INPUT_SLOT_1_INDEX,
            2,
            PLAYER_INVENTORY_START_INDEX,
            PLAYER_INVENTORY_SLOT_COUNT + HOTBAR_SLOT_COUNT
        );
    }

    private CobblestoneChemicalReactorMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getChemicalReactorBlockEntity(playerInventory, blockPos),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    private static CobblestoneChemicalReactorBlockEntity getChemicalReactorBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneChemicalReactorBlockEntity chemicalReactorBlockEntity) {
            return chemicalReactorBlockEntity;
        }

        throw new IllegalStateException("Cobblestone Chemical Reactor の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addChemicalReactorSlots() {
        ItemStackHandler itemStackHandler = this.chemicalReactorBlockEntity.getItemStackHandler();

        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneChemicalReactorBlockEntity.INPUT_SLOT_1_INDEX, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_1_SLOT_X, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_1_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneChemicalReactorBlockEntity.INPUT_SLOT_2_INDEX, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_2_SLOT_X, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_2_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneChemicalReactorBlockEntity.POWER_SLOT_INDEX, MachineGuiLayouts.ChemicalReactor.POWER_SLOT_X, MachineGuiLayouts.ChemicalReactor.POWER_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CobblestoneCrusherBlockEntity.isCobblestonePowerItem(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneChemicalReactorBlockEntity.OUTPUT_SLOT_1_INDEX, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_1_SLOT_X, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_1_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneChemicalReactorBlockEntity.OUTPUT_SLOT_2_INDEX, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_2_SLOT_X, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_2_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneChemicalReactorBlockEntity.ACCELERATION_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ACCELERATION_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneChemicalReactorBlockEntity.ENERGIZED_CUBE_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y) {
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
}