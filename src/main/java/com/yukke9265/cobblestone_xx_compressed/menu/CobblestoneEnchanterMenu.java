package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneCrusherBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneEnchanterBlockEntity;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

@SuppressWarnings("null")
public class CobblestoneEnchanterMenu extends BaseMenu {
    private static final AutomationMode[] ENCHANTER_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.INPUT_1,
        AutomationMode.INPUT_2,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT,
        AutomationMode.COBBLESTONE_INPUT
    };

    private static final int DATA_COUNT = 9 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_CURRENT_POWER_RATE = 6 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_CURRENT_POWER_RATE + 2;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int MACHINE_SLOT_COUNT = 6;
    private static final int PLAYER_INVENTORY_START_INDEX = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS;
    private static final int HOTBAR_START_INDEX = PLAYER_INVENTORY_START_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int HOTBAR_END_INDEX = HOTBAR_START_INDEX + HOTBAR_SLOT_COUNT;

    private final CobblestoneEnchanterBlockEntity enchanterBlockEntity;
    private final ContainerData enchanterData;

    public CobblestoneEnchanterMenu(int containerId, Inventory playerInventory, CobblestoneEnchanterBlockEntity enchanterBlockEntity, ContainerData enchanterData) {
        super(ModMenuType.COBBLESTONE_ENCHANTER_MENU.get(), containerId);
        this.enchanterBlockEntity = enchanterBlockEntity;
        this.enchanterData = enchanterData;

        checkContainerDataCount(enchanterData, DATA_COUNT);
        this.addDataSlots(enchanterData);

        this.addEnchanterSlots();
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    public CobblestoneEnchanterMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public int getProgress() {
        return this.enchanterData.get(DATA_INDEX_PROGRESS);
    }

    public int getMaxProgress() {
        return this.enchanterData.get(DATA_INDEX_MAX_PROGRESS);
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.enchanterData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.enchanterData, DATA_INDEX_MAX_STORED_POWER);
    }

    @Override
    public long getCurrentCobblestonePowerRate() {
        return this.getLongFromData(this.enchanterData, DATA_INDEX_CURRENT_POWER_RATE);
    }

    public boolean getIsAvailable() {
        return this.enchanterBlockEntity.getIsAvailable();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        int dataIndex = DATA_INDEX_MAX_STORED_POWER + 2 + automationSide.getIndex();
        return AutomationMode.fromId(this.enchanterData.get(dataIndex));
    }

    public boolean isAutoExportEnabled() {
        return this.enchanterData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public int getAutomationButtonId(AutomationSide automationSide) {
        return this.getAutomationButtonId(automationSide.getIndex());
    }

    public int getAutoExportButtonId() {
        return this.getAutoExportToggleButtonId();
    }

    @Override
    public boolean clickMenuButton(@Nonnull Player player, int id) {
        if (id == 0) {
            this.enchanterBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.handleAutomationButtonClick(this.enchanterBlockEntity, id, ENCHANTER_AUTOMATION_MODES)) {
            return true;
        }

        return this.handleAutoExportButtonClick(this.enchanterBlockEntity, id);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.enchanterBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.enchanterBlockEntity.getBlockPos();
        if (!player.level().getBlockState(blockPos).is(ModBlocks.COBBLESTONE_ENCHANTER.get())) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.enchanterBlockEntity) {
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
                movedToMachine = this.moveItemStackTo(sourceStack, CobblestoneEnchanterBlockEntity.ACCELERATION_SLOT_INDEX, CobblestoneEnchanterBlockEntity.ACCELERATION_SLOT_INDEX + 1, false);
            } else if (MachineUpgradeHelper.isEnergizedCube(sourceStack)) {
                movedToMachine = this.moveItemStackTo(sourceStack, CobblestoneEnchanterBlockEntity.ENERGIZED_CUBE_SLOT_INDEX, CobblestoneEnchanterBlockEntity.ENERGIZED_CUBE_SLOT_INDEX + 1, false);
            } else if (CobblestoneCrusherBlockEntity.isCobblestonePowerItem(sourceStack)) {
                movedToMachine = this.moveItemStackTo(sourceStack, CobblestoneEnchanterBlockEntity.POWER_SLOT_INDEX, CobblestoneEnchanterBlockEntity.POWER_SLOT_INDEX + 1, false);
            } else if (sourceStack.is(Items.ENCHANTED_BOOK)) {
                movedToMachine = this.moveItemStackTo(sourceStack, CobblestoneEnchanterBlockEntity.BOOK_INPUT_SLOT_INDEX, CobblestoneEnchanterBlockEntity.BOOK_INPUT_SLOT_INDEX + 1, false);
            } else if (CobblestoneEnchanterBlockEntity.isValidToolCandidate(sourceStack)) {
                movedToMachine = this.moveItemStackTo(sourceStack, CobblestoneEnchanterBlockEntity.TOOL_INPUT_SLOT_INDEX, CobblestoneEnchanterBlockEntity.TOOL_INPUT_SLOT_INDEX + 1, false);
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
            ModJeiIds.COBBLESTONE_ENCHANTER,
            CobblestoneEnchanterBlockEntity.TOOL_INPUT_SLOT_INDEX,
            2,
            PLAYER_INVENTORY_START_INDEX,
            PLAYER_INVENTORY_SLOT_COUNT + HOTBAR_SLOT_COUNT
        );
    }

    private CobblestoneEnchanterMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(containerId, playerInventory, getEnchanterBlockEntity(playerInventory, blockPos), new SimpleContainerData(DATA_COUNT));
    }

    private static CobblestoneEnchanterBlockEntity getEnchanterBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneEnchanterBlockEntity enchanterBlockEntity) {
            return enchanterBlockEntity;
        }

        throw new IllegalStateException("Cobblestone Enchanter の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addEnchanterSlots() {
        ItemStackHandler itemStackHandler = this.enchanterBlockEntity.getItemStackHandler();

        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneEnchanterBlockEntity.POWER_SLOT_INDEX, MachineGuiLayouts.Enchanter.POWER_SLOT_X, MachineGuiLayouts.Enchanter.POWER_SLOT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return CobblestoneCrusherBlockEntity.isCobblestonePowerItem(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneEnchanterBlockEntity.TOOL_INPUT_SLOT_INDEX, MachineGuiLayouts.Enchanter.TOOL_SLOT_X, MachineGuiLayouts.Enchanter.TOOL_SLOT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return CobblestoneEnchanterBlockEntity.isValidToolCandidate(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneEnchanterBlockEntity.BOOK_INPUT_SLOT_INDEX, MachineGuiLayouts.Enchanter.BOOK_SLOT_X, MachineGuiLayouts.Enchanter.BOOK_SLOT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return stack.is(Items.ENCHANTED_BOOK);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneEnchanterBlockEntity.OUTPUT_SLOT_INDEX, MachineGuiLayouts.Enchanter.OUTPUT_SLOT_X, MachineGuiLayouts.Enchanter.OUTPUT_SLOT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneEnchanterBlockEntity.ACCELERATION_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ACCELERATION_SLOT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneEnchanterBlockEntity.ENERGIZED_CUBE_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y) {
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
        for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
            int x = MachineGuiLayouts.PLAYER_INVENTORY_START_X + column * MachineGuiLayouts.SLOT_SIZE;
            this.addSlot(new Slot(playerInventory, column, x, MachineGuiLayouts.HOTBAR_START_Y));
        }
    }
}