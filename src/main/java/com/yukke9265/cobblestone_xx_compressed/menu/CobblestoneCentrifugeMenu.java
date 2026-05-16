package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneCentrifugeBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneCrusherBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.MachineUpgradeHelper;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiRecipeTransferDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
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

public class CobblestoneCentrifugeMenu extends BaseMenu {
    private static final AutomationMode[] CENTRIFUGE_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT,
        AutomationMode.OUTPUT_1,
        AutomationMode.OUTPUT_2,
        AutomationMode.COBBLESTONE_INPUT
    };

    private static final int DATA_COUNT = 7 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_POWER_UPPER = 3;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    private static final int DATA_INDEX_AUTO_EXPORT = 6 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int SLOT_SIZE = 18;
    private static final int MACHINE_SLOT_Y = 31;
    private static final int POWER_SLOT_X = 10;
    private static final int POWER_SLOT_Y = 51;
    private static final int INPUT_SLOT_X = 56;
    private static final int OUTPUT_SLOT_1_X = INPUT_SLOT_X + SLOT_SIZE + 30;
    private static final int OUTPUT_SLOT_2_X = OUTPUT_SLOT_1_X + SLOT_SIZE;
    private static final int ACCELERATION_SLOT_X = 176;
    private static final int ACCELERATION_SLOT_Y = 12;
    private static final int ENERGIZED_CUBE_SLOT_X = ACCELERATION_SLOT_X;
    private static final int ENERGIZED_CUBE_SLOT_Y = ACCELERATION_SLOT_Y + SLOT_SIZE;
    private static final int PLAYER_INVENTORY_START_X = 8;
    private static final int PLAYER_INVENTORY_START_Y = 84;
    private static final int HOTBAR_START_Y = 142;
    private static final int MACHINE_SLOT_COUNT = 6;
    private static final int PLAYER_INVENTORY_START_INDEX = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS;
    private static final int HOTBAR_START_INDEX = PLAYER_INVENTORY_START_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int HOTBAR_END_INDEX = HOTBAR_START_INDEX + HOTBAR_SLOT_COUNT;

    private final CobblestoneCentrifugeBlockEntity centrifugeBlockEntity;
    private final ContainerData centrifugeData;

    public CobblestoneCentrifugeMenu(
        int containerId,
        Inventory playerInventory,
        CobblestoneCentrifugeBlockEntity centrifugeBlockEntity,
        ContainerData centrifugeData
    ) {
        super(ModMenuType.COBBLESTONE_CENTRIFUGE_MENU.get(), containerId);
        this.centrifugeBlockEntity = centrifugeBlockEntity;
        this.centrifugeData = centrifugeData;

        checkContainerDataCount(centrifugeData, DATA_COUNT);
        this.addDataSlots(centrifugeData);

        this.addCentrifugeSlots();
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
    }

    public CobblestoneCentrifugeMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public int getProgress() {
        return this.centrifugeData.get(DATA_INDEX_PROGRESS);
    }

    public int getMaxProgress() {
        return this.centrifugeData.get(DATA_INDEX_MAX_PROGRESS);
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.centrifugeData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.centrifugeData, DATA_INDEX_MAX_STORED_POWER);
    }

    public boolean getIsAvailable() {
        return this.centrifugeBlockEntity.getIsAvailable();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        int dataIndex = DATA_INDEX_MAX_STORED_POWER_UPPER + 1 + automationSide.getIndex();
        return AutomationMode.fromId(this.centrifugeData.get(dataIndex));
    }

    public boolean isAutoExportEnabled() {
        return this.centrifugeData.get(DATA_INDEX_AUTO_EXPORT) != 0;
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
            this.centrifugeBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.isAutomationButtonId(id)) {
            this.centrifugeBlockEntity.cycleAutomationMode(this.getAutomationIndexFromButtonId(id), CENTRIFUGE_AUTOMATION_MODES);
            return true;
        }

        if (this.handleAutoExportButtonClick(this.centrifugeBlockEntity, id)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.centrifugeBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.centrifugeBlockEntity.getBlockPos();
        if (!player.level().getBlockState(blockPos).is(ModBlocks.COBBLESTONE_CENTRIFUGE.get())) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.centrifugeBlockEntity) {
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
                    CobblestoneCentrifugeBlockEntity.ACCELERATION_SLOT_INDEX,
                    CobblestoneCentrifugeBlockEntity.ACCELERATION_SLOT_INDEX + 1,
                    false
                );
            } else if (MachineUpgradeHelper.isEnergizedCube(sourceStack)) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneCentrifugeBlockEntity.ENERGIZED_CUBE_SLOT_INDEX,
                    CobblestoneCentrifugeBlockEntity.ENERGIZED_CUBE_SLOT_INDEX + 1,
                    false
                );
            } else if (CobblestoneCrusherBlockEntity.isCobblestonePowerItem(sourceStack)) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneCentrifugeBlockEntity.POWER_SLOT_INDEX,
                    CobblestoneCentrifugeBlockEntity.POWER_SLOT_INDEX + 1,
                    false
                );
            }

            if (!movedToMachine) {
                movedToMachine = this.moveItemStackTo(
                    sourceStack,
                    CobblestoneCentrifugeBlockEntity.INPUT_SLOT_INDEX,
                    CobblestoneCentrifugeBlockEntity.INPUT_SLOT_INDEX + 1,
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

    private CobblestoneCentrifugeMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getCentrifugeBlockEntity(playerInventory, blockPos),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    private static CobblestoneCentrifugeBlockEntity getCentrifugeBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneCentrifugeBlockEntity centrifugeBlockEntity) {
            return centrifugeBlockEntity;
        }

        throw new IllegalStateException("Cobblestone Centrifuge の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addCentrifugeSlots() {
        ItemStackHandler itemStackHandler = this.centrifugeBlockEntity.getItemStackHandler();

        // 入力 1 枠、出力 2 枠、下段の CP スロットという構成に固定します。
        // 出力を 2 枠に分けることで、自動化側の出力1/出力2設定と GUI 上の見た目を一致させます。
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCentrifugeBlockEntity.POWER_SLOT_INDEX, POWER_SLOT_X, POWER_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CobblestoneCrusherBlockEntity.isCobblestonePowerItem(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCentrifugeBlockEntity.INPUT_SLOT_INDEX, INPUT_SLOT_X, MACHINE_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCentrifugeBlockEntity.OUTPUT_SLOT_1_INDEX, OUTPUT_SLOT_1_X, MACHINE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCentrifugeBlockEntity.OUTPUT_SLOT_2_INDEX, OUTPUT_SLOT_2_X, MACHINE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCentrifugeBlockEntity.ACCELERATION_SLOT_INDEX, ACCELERATION_SLOT_X, ACCELERATION_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCentrifugeBlockEntity.ENERGIZED_CUBE_SLOT_INDEX, ENERGIZED_CUBE_SLOT_X, ENERGIZED_CUBE_SLOT_Y) {
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

    @Override
    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        return this.createSingleJeiRecipeTransferDefinition(
            ModJeiIds.COBBLESTONE_CENTRIFUGE,
            1,
            1,
            PLAYER_INVENTORY_START_INDEX,
            PLAYER_INVENTORY_SLOT_COUNT + HOTBAR_SLOT_COUNT
        );
    }
}