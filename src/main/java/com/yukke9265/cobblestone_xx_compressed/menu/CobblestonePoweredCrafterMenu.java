package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestonePoweredCrafterBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.MachineUpgradeHelper;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiRecipeTransferDefinition;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CobblestonePoweredCrafterMenu extends PoweredMachineMenuBase<CobblestonePoweredCrafterBlockEntity> {
    private static final AutomationMode[] POWERED_CRAFTER_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT,
        AutomationMode.COBBLESTONE_INPUT
    };

    private static final int DATA_COUNT = 11 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_POWER_UPPER = 3;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    private static final int DATA_INDEX_CURRENT_POWER_RATE = 6 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_CURRENT_POWER_RATE_UPPER = DATA_INDEX_CURRENT_POWER_RATE + 1;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_CURRENT_POWER_RATE_UPPER + 1;
    private static final int DATA_INDEX_AUTO_INSERT = DATA_INDEX_AUTO_EXPORT + 1;
    private static final int DATA_INDEX_SOUND_MUTED = DATA_INDEX_AUTO_INSERT + 1;
    private static final int MACHINE_SLOT_COUNT = 14;

    private final CobblestonePoweredCrafterBlockEntity poweredCrafterBlockEntity;
    private final ContainerData poweredCrafterData;

    public CobblestonePoweredCrafterMenu(
        int containerId,
        Inventory playerInventory,
        CobblestonePoweredCrafterBlockEntity poweredCrafterBlockEntity,
        ContainerData poweredCrafterData
    ) {
        super(ModMenuType.COBBLESTONE_POWERED_CRAFTER_MENU.get(), containerId);
        this.poweredCrafterBlockEntity = poweredCrafterBlockEntity;
        this.poweredCrafterData = poweredCrafterData;

        checkContainerDataCount(poweredCrafterData, DATA_COUNT);
        this.addDataSlots(poweredCrafterData);

        this.addPoweredCrafterSlots();
        this.addPlayerInventorySlots(
            playerInventory,
            MachineGuiLayouts.PLAYER_INVENTORY_START_X,
            MachineGuiLayouts.PLAYER_INVENTORY_START_Y,
            MachineGuiLayouts.SLOT_SIZE
        );
        this.addPlayerHotbarSlots(
            playerInventory,
            MachineGuiLayouts.PLAYER_INVENTORY_START_X,
            MachineGuiLayouts.HOTBAR_START_Y,
            MachineGuiLayouts.SLOT_SIZE
        );
        this.initSlotFilterSupport();
    }

    public CobblestonePoweredCrafterMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public int getProgress() {
        return this.poweredCrafterData.get(DATA_INDEX_PROGRESS);
    }

    public int getMaxProgress() {
        return this.poweredCrafterData.get(DATA_INDEX_MAX_PROGRESS);
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.poweredCrafterData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.poweredCrafterData, DATA_INDEX_MAX_STORED_POWER);
    }

    @Override
    public long getCurrentCobblestonePowerRate() {
        return this.getLongFromData(this.poweredCrafterData, DATA_INDEX_CURRENT_POWER_RATE);
    }

    public boolean getIsAvailable() {
        return this.poweredCrafterBlockEntity.getIsAvailable();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        int dataIndex = DATA_INDEX_MAX_STORED_POWER_UPPER + 1 + automationSide.getIndex();
        return AutomationMode.fromId(this.poweredCrafterData.get(dataIndex));
    }

    public boolean isAutoExportEnabled() {
        return this.poweredCrafterData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public boolean isAutoInsertEnabled() {
        return this.poweredCrafterData.get(DATA_INDEX_AUTO_INSERT) != 0;
    }

    @Override
    public boolean isSoundMuted() {
        return this.poweredCrafterData.get(DATA_INDEX_SOUND_MUTED) != 0;
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
    protected CobblestonePoweredCrafterBlockEntity getMachineBlockEntity() {
        return this.poweredCrafterBlockEntity;
    }

    @Override
    protected Block getMachineBlock() {
        return ModBlocks.COBBLESTONE_POWERED_CRAFTER.get();
    }

    @Override
    protected int getMachineSlotCount() {
        return MACHINE_SLOT_COUNT;
    }

    @Override
    protected boolean moveStackToMachine(ItemStack sourceStack) {
        boolean movedToMachine = false;
        if (MachineUpgradeHelper.isAccelerationChip(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestonePoweredCrafterBlockEntity.ACCELERATION_SLOT_INDEX,
                CobblestonePoweredCrafterBlockEntity.ACCELERATION_SLOT_INDEX + 1,
                false
            );
        } else if (MachineUpgradeHelper.isEnergizedCube(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestonePoweredCrafterBlockEntity.ENERGIZED_CUBE_SLOT_INDEX,
                CobblestonePoweredCrafterBlockEntity.ENERGIZED_CUBE_SLOT_INDEX + 1,
                false
            );
        } else if (MachineUpgradeHelper.isParallelChip(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestonePoweredCrafterBlockEntity.PARALLEL_SLOT_INDEX,
                CobblestonePoweredCrafterBlockEntity.PARALLEL_SLOT_INDEX + 1,
                false
            );
        } else if (CobblestonePoweredCrafterBlockEntity.isCobblestonePowerItem(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestonePoweredCrafterBlockEntity.POWER_SLOT_INDEX,
                CobblestonePoweredCrafterBlockEntity.POWER_SLOT_INDEX + 1,
                false
            );
        } else if (this.poweredCrafterBlockEntity.canQuickMoveToInput(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestonePoweredCrafterBlockEntity.FIRST_GRID_SLOT_INDEX,
                CobblestonePoweredCrafterBlockEntity.POWER_SLOT_INDEX,
                false
            );
        }

        return movedToMachine;
    }

    @Override
    protected boolean handleMachineMenuButton(Player player, int id) {
        if (id == 0) {
            this.poweredCrafterBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.handleAutomationButtonClick(this.poweredCrafterBlockEntity, id, POWERED_CRAFTER_AUTOMATION_MODES)) {
            return true;
        }

        if (this.handleAutoExportButtonClick(this.poweredCrafterBlockEntity, id)) {
            return true;
        }

        if (this.handleAutoInsertButtonClick(this.poweredCrafterBlockEntity, id)) {
            return true;
        }

        if (this.handleMuteSoundButtonClick(this.poweredCrafterBlockEntity, id)) {
            return true;
        }

        return false;
    }

    @Override
    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        return List.of();
    }

    private CobblestonePoweredCrafterMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getPoweredCrafterBlockEntity(playerInventory, blockPos),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    private static CobblestonePoweredCrafterBlockEntity getPoweredCrafterBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestonePoweredCrafterBlockEntity poweredCrafterBlockEntity) {
            return poweredCrafterBlockEntity;
        }

        throw new IllegalStateException("Powered Crafter の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addPoweredCrafterSlots() {
        ItemStackHandler itemStackHandler = this.poweredCrafterBlockEntity.getItemStackHandler();

        for (int gridIndex = 0; gridIndex < 9; gridIndex++) {
            int slotIndex = CobblestonePoweredCrafterBlockEntity.FIRST_GRID_SLOT_INDEX + gridIndex;
            this.addSlot(new SlotItemHandler(
                itemStackHandler,
                slotIndex,
                MachineGuiLayouts.PoweredCrafter.getInputSlotX(gridIndex),
                MachineGuiLayouts.PoweredCrafter.getInputSlotY(gridIndex)
            ));
        }

        this.addSlot(new SlotItemHandler(
            itemStackHandler,
            CobblestonePoweredCrafterBlockEntity.POWER_SLOT_INDEX,
            MachineGuiLayouts.PoweredCrafter.POWER_SLOT_X,
            MachineGuiLayouts.PoweredCrafter.POWER_SLOT_Y
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CobblestonePoweredCrafterBlockEntity.isCobblestonePowerItem(stack);
            }
        });

        this.addSlot(new SlotItemHandler(
            itemStackHandler,
            CobblestonePoweredCrafterBlockEntity.OUTPUT_SLOT_INDEX,
            MachineGuiLayouts.PoweredCrafter.OUTPUT_SLOT_X,
            MachineGuiLayouts.PoweredCrafter.OUTPUT_SLOT_Y
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        this.addSlot(new SlotItemHandler(
            itemStackHandler,
            CobblestonePoweredCrafterBlockEntity.ACCELERATION_SLOT_INDEX,
            MachineGuiLayouts.UPGRADE_SLOT_X,
            MachineGuiLayouts.ACCELERATION_SLOT_Y
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }
        });

        this.addSlot(new SlotItemHandler(
            itemStackHandler,
            CobblestonePoweredCrafterBlockEntity.ENERGIZED_CUBE_SLOT_INDEX,
            MachineGuiLayouts.UPGRADE_SLOT_X,
            MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isEnergizedCube(stack);
            }
        });

        this.addSlot(new SlotItemHandler(
            itemStackHandler,
            CobblestonePoweredCrafterBlockEntity.PARALLEL_SLOT_INDEX,
            MachineGuiLayouts.UPGRADE_SLOT_X,
            MachineGuiLayouts.PARALLEL_SLOT_Y
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isParallelChip(stack);
            }
        });
    }
}
