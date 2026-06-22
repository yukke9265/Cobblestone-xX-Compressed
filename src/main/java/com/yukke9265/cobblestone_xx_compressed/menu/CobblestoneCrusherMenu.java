package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/*
 * 方針:
 * このクラスでは Crusher 固有の差分だけを持ち、共通のメニュー処理は PoweredMachineMenuBase に任せます。
 * 具体的には、同期データの意味、機械スロット構成、Shift+クリック時の投入優先順位だけをここへ残します。
 */
public class CobblestoneCrusherMenu extends PoweredMachineMenuBase<CobblestoneCrusherBlockEntity> {
    private static final AutomationMode[] CRUSHER_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT,
        AutomationMode.COBBLESTONE_INPUT
    };

    private static final int DATA_COUNT = 9 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_POWER_UPPER = 3;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    private static final int DATA_INDEX_CURRENT_POWER_RATE = 6 + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_CURRENT_POWER_RATE_UPPER = DATA_INDEX_CURRENT_POWER_RATE + 1;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_CURRENT_POWER_RATE_UPPER + 1;
    private static final int MACHINE_SLOT_COUNT = 5;

    private final CobblestoneCrusherBlockEntity crusherBlockEntity;
    private final ContainerData crusherData;

    public CobblestoneCrusherMenu(int containerId, Inventory playerInventory, CobblestoneCrusherBlockEntity crusherBlockEntity, ContainerData crusherData) {
        super(ModMenuType.COBBLESTONE_CRUSHER_MENU.get(), containerId);
        this.crusherBlockEntity = crusherBlockEntity;
        this.crusherData = crusherData;

        checkContainerDataCount(crusherData, DATA_COUNT);
        this.addDataSlots(crusherData);

        this.addCrusherSlots();
        this.addPlayerInventorySlots(playerInventory, MachineGuiLayouts.PLAYER_INVENTORY_START_X, MachineGuiLayouts.PLAYER_INVENTORY_START_Y, MachineGuiLayouts.SLOT_SIZE);
        this.addPlayerHotbarSlots(playerInventory, MachineGuiLayouts.PLAYER_INVENTORY_START_X, MachineGuiLayouts.HOTBAR_START_Y, MachineGuiLayouts.SLOT_SIZE);
    }

    public CobblestoneCrusherMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public int getProgress() {
        return this.crusherData.get(DATA_INDEX_PROGRESS);
    }

    public int getMaxProgress() {
        return this.crusherData.get(DATA_INDEX_MAX_PROGRESS);
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.crusherData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.getLongFromData(this.crusherData, DATA_INDEX_MAX_STORED_POWER);
    }

    @Override
    public long getCurrentCobblestonePowerRate() {
        return this.getLongFromData(this.crusherData, DATA_INDEX_CURRENT_POWER_RATE);
    }

    public boolean getIsAvailable() {
        return this.crusherBlockEntity.getIsAvailable();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        int dataIndex = DATA_INDEX_MAX_STORED_POWER_UPPER + 1 + automationSide.getIndex();
        return AutomationMode.fromId(this.crusherData.get(dataIndex));
    }

    public boolean isAutoExportEnabled() {
        return this.crusherData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public int getAutomationButtonId(AutomationSide automationSide) {
        return this.getAutomationButtonId(automationSide.getIndex());
    }

    public int getAutoExportButtonId() {
        return this.getAutoExportToggleButtonId();
    }

    @Override
    protected CobblestoneCrusherBlockEntity getMachineBlockEntity() {
        return this.crusherBlockEntity;
    }

    @Override
    protected net.minecraft.world.level.block.Block getMachineBlock() {
        return ModBlocks.COBBLESTONE_CRUSHER.get();
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
                CobblestoneCrusherBlockEntity.ACCELERATION_SLOT_INDEX,
                CobblestoneCrusherBlockEntity.ACCELERATION_SLOT_INDEX + 1,
                false
            );
        } else if (MachineUpgradeHelper.isEnergizedCube(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestoneCrusherBlockEntity.ENERGIZED_CUBE_SLOT_INDEX,
                CobblestoneCrusherBlockEntity.ENERGIZED_CUBE_SLOT_INDEX + 1,
                false
            );
        } else if (this.crusherBlockEntity.canQuickMoveToInput(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestoneCrusherBlockEntity.INPUT_SLOT_INDEX,
                CobblestoneCrusherBlockEntity.INPUT_SLOT_INDEX + 1,
                false
            );
        }

        if (!movedToMachine && CobblestoneCrusherBlockEntity.isCobblestonePowerItem(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                CobblestoneCrusherBlockEntity.POWER_SLOT_INDEX,
                CobblestoneCrusherBlockEntity.POWER_SLOT_INDEX + 1,
                false
            );
        }

        return movedToMachine;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            this.crusherBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.handleAutomationButtonClick(this.crusherBlockEntity, id, CRUSHER_AUTOMATION_MODES)) {
            return true;
        }

        if (this.handleAutoExportButtonClick(this.crusherBlockEntity, id)) {
            return true;
        }

        return false;
    }

    private CobblestoneCrusherMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getRequiredBlockEntity(playerInventory, blockPos, CobblestoneCrusherBlockEntity.class, "Cobblestone Crusher"),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    private void addCrusherSlots() {
        ItemStackHandler itemStackHandler = this.crusherBlockEntity.getItemStackHandler();

        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCrusherBlockEntity.INPUT_SLOT_INDEX, MachineGuiLayouts.PoweredMachine.INPUT_SLOT_X, MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y));
        // 丸石系の燃料スロットは、右下の CP インジケータのすぐ上に寄せて配置します。
        // これにより、入力スロットと役割を視覚的に分けやすくします。
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCrusherBlockEntity.POWER_SLOT_INDEX, MachineGuiLayouts.PoweredMachine.POWER_SLOT_X, MachineGuiLayouts.PoweredMachine.POWER_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CobblestoneCrusherBlockEntity.isCobblestonePowerItem(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCrusherBlockEntity.OUTPUT_SLOT_INDEX, MachineGuiLayouts.PoweredMachine.OUTPUT_SLOT_X, MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCrusherBlockEntity.ACCELERATION_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ACCELERATION_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneCrusherBlockEntity.ENERGIZED_CUBE_SLOT_INDEX, MachineGuiLayouts.UPGRADE_SLOT_X, MachineGuiLayouts.ENERGIZED_CUBE_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isEnergizedCube(stack);
            }
        });
    }

    @Override
    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        return this.createPoweredMachineJeiRecipeTransferDefinition(
            ModJeiIds.COBBLESTONE_CRUSHER,
            CobblestoneCrusherBlockEntity.INPUT_SLOT_INDEX,
            1
        );
    }
}