package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestonePoweredFurnaceBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.MachineUpgradeHelper;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.JeiRecipeTransferDefinition;
import com.yukke9265.cobblestone_xx_compressed.compat.jei.ModJeiIds;
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
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

@SuppressWarnings("null")
public class CobblestonePoweredFurnaceMenu extends PoweredMachineMenuBase<CobblestonePoweredFurnaceBlockEntity> {
    private static final AutomationMode[] POWERED_FURNACE_AUTOMATION_MODES = new AutomationMode[] {
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
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    private static final int DATA_INDEX_CURRENT_POWER_RATE = 6 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_CURRENT_POWER_RATE_UPPER = DATA_INDEX_CURRENT_POWER_RATE + 1;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_CURRENT_POWER_RATE_UPPER + 1;
    private static final int DATA_INDEX_AUTO_INSERT = DATA_INDEX_AUTO_EXPORT + 1;
    private static final int DATA_INDEX_SOUND_MUTED = DATA_INDEX_AUTO_INSERT + 1;
    private static final int MACHINE_SLOT_COUNT = 6;

    private final CobblestonePoweredFurnaceBlockEntity poweredFurnaceBlockEntity;
    private final ContainerData poweredFurnaceData;

    public CobblestonePoweredFurnaceMenu(
        int containerId,
        Inventory playerInventory,
        CobblestonePoweredFurnaceBlockEntity poweredFurnaceBlockEntity,
        ContainerData poweredFurnaceData
    ) {
        super(ModMenuType.COBBLESTONE_POWERED_FURNACE_MENU.get(), containerId);
        this.poweredFurnaceBlockEntity = poweredFurnaceBlockEntity;
        this.poweredFurnaceData = poweredFurnaceData;

        checkContainerDataCount(poweredFurnaceData, DATA_COUNT);
        this.addDataSlots(poweredFurnaceData);

        this.addPoweredFurnaceSlots();
        this.addPlayerInventorySlots(playerInventory, MachineGuiLayouts.PLAYER_INVENTORY_START_X, MachineGuiLayouts.PLAYER_INVENTORY_START_Y, MachineGuiLayouts.SLOT_SIZE);
        this.addPlayerHotbarSlots(playerInventory, MachineGuiLayouts.PLAYER_INVENTORY_START_X, MachineGuiLayouts.HOTBAR_START_Y, MachineGuiLayouts.SLOT_SIZE);
    }

    public CobblestonePoweredFurnaceMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public int getProgress() {
        return this.poweredFurnaceData.get(DATA_INDEX_PROGRESS);
    }

    public int getMaxProgress() {
        return this.poweredFurnaceData.get(DATA_INDEX_MAX_PROGRESS);
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.poweredFurnaceData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.poweredFurnaceData, DATA_INDEX_MAX_STORED_POWER);
    }

    @Override
    public long getCurrentCobblestonePowerRate() {
        return this.getLongFromData(this.poweredFurnaceData, DATA_INDEX_CURRENT_POWER_RATE);
    }

    public boolean getIsAvailable() {
        return this.poweredFurnaceBlockEntity.getIsAvailable();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        int dataIndex = DATA_INDEX_MAX_STORED_POWER_UPPER + 1 + automationSide.getIndex();
        return AutomationMode.fromId(this.poweredFurnaceData.get(dataIndex));
    }

    public boolean isAutoExportEnabled() {
        return this.poweredFurnaceData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public boolean isAutoInsertEnabled() {
        return this.poweredFurnaceData.get(DATA_INDEX_AUTO_INSERT) != 0;
    }

    @Override
    public boolean isSoundMuted() {
        return this.poweredFurnaceData.get(DATA_INDEX_SOUND_MUTED) != 0;
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
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            this.poweredFurnaceBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.handleAutomationButtonClick(this.poweredFurnaceBlockEntity, id, POWERED_FURNACE_AUTOMATION_MODES)) {
            return true;
        }

        if (this.handleAutoExportButtonClick(this.poweredFurnaceBlockEntity, id)) {
            return true;
        }

        if (this.handleAutoInsertButtonClick(this.poweredFurnaceBlockEntity, id)) {
            return true;
        }

        if (this.handleMuteSoundButtonClick(this.poweredFurnaceBlockEntity, id)) {
            return true;
        }

        return false;
    }

    @Override
    protected CobblestonePoweredFurnaceBlockEntity getMachineBlockEntity() {
        return this.poweredFurnaceBlockEntity;
    }

    @Override
    protected Block getMachineBlock() {
        return com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks.COBBLESTONE_POWERED_FURNACE.get();
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
                CobblestonePoweredFurnaceBlockEntity.ACCELERATION_SLOT_INDEX,
                CobblestonePoweredFurnaceBlockEntity.ACCELERATION_SLOT_INDEX + 1,
                false
            );
        } else if (MachineUpgradeHelper.isEnergizedCube(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestonePoweredFurnaceBlockEntity.ENERGIZED_CUBE_SLOT_INDEX,
                CobblestonePoweredFurnaceBlockEntity.ENERGIZED_CUBE_SLOT_INDEX + 1,
                false
            );
        } else if (MachineUpgradeHelper.isParallelChip(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestonePoweredFurnaceBlockEntity.PARALLEL_SLOT_INDEX,
                CobblestonePoweredFurnaceBlockEntity.PARALLEL_SLOT_INDEX + 1,
                false
            );
        } else if (this.poweredFurnaceBlockEntity.canQuickMoveToInput(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestonePoweredFurnaceBlockEntity.INPUT_SLOT_INDEX,
                CobblestonePoweredFurnaceBlockEntity.INPUT_SLOT_INDEX + 1,
                false
            );
        }

        if (!movedToMachine && CobblestonePoweredFurnaceBlockEntity.isCobblestonePowerItem(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestonePoweredFurnaceBlockEntity.POWER_SLOT_INDEX,
                CobblestonePoweredFurnaceBlockEntity.POWER_SLOT_INDEX + 1,
                false
            );
        }

        return movedToMachine;
    }

    private CobblestonePoweredFurnaceMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getRequiredBlockEntity(playerInventory, blockPos, CobblestonePoweredFurnaceBlockEntity.class, "Cobblestone Powered Furnace"),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    private void addPoweredFurnaceSlots() {
        ItemStackHandler itemStackHandler = this.poweredFurnaceBlockEntity.getItemStackHandler();

        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestonePoweredFurnaceBlockEntity.INPUT_SLOT_INDEX, MachineGuiLayouts.PoweredMachine.INPUT_SLOT_X, MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y));
        // CP 用の丸石スロットは crusher と同じ場所に置き、操作感をそろえます。
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestonePoweredFurnaceBlockEntity.POWER_SLOT_INDEX, MachineGuiLayouts.PoweredMachine.POWER_SLOT_X, MachineGuiLayouts.PoweredMachine.POWER_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CobblestonePoweredFurnaceBlockEntity.isCobblestonePowerItem(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestonePoweredFurnaceBlockEntity.OUTPUT_SLOT_INDEX, MachineGuiLayouts.PoweredMachine.OUTPUT_SLOT_X, MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestonePoweredFurnaceBlockEntity.ACCELERATION_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ACCELERATION_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestonePoweredFurnaceBlockEntity.ENERGIZED_CUBE_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isEnergizedCube(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestonePoweredFurnaceBlockEntity.PARALLEL_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.PARALLEL_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isParallelChip(stack);
            }
        });
    }

    @Override
    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        return this.createPoweredMachineJeiRecipeTransferDefinition(
            ModJeiIds.COBBLESTONE_POWERED_FURNACE,
            CobblestonePoweredFurnaceBlockEntity.INPUT_SLOT_INDEX,
            1
        );
    }
}