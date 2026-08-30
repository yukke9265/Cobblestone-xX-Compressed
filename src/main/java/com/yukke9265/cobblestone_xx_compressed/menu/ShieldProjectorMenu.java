package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.MachineUpgradeHelper;
import com.yukke9265.cobblestone_xx_compressed.blockentity.ShieldProjectorBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.ShieldProjectorUpgradeHelper;
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
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * シールドプロジェクター用メニュー。
 * CP スロット・通常 upgrade 3・独自 upgrade 8 を持ちます。
 */
public class ShieldProjectorMenu extends PoweredMachineMenuBase<ShieldProjectorBlockEntity> {
    private static final AutomationMode[] PROJECTOR_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.COBBLESTONE_INPUT
    };

    private static final int MACHINE_SPECIFIC_DATA_COUNT = 4;
    private static final int DATA_COUNT = 11 + MACHINE_SPECIFIC_DATA_COUNT + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_SHIELD = 6;
    private static final int DATA_INDEX_AUTOMATION_START = 6 + MACHINE_SPECIFIC_DATA_COUNT;
    private static final int DATA_INDEX_CURRENT_POWER_RATE = DATA_INDEX_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_CURRENT_POWER_RATE_UPPER = DATA_INDEX_CURRENT_POWER_RATE + 1;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_CURRENT_POWER_RATE_UPPER + 1;
    private static final int DATA_INDEX_AUTO_INSERT = DATA_INDEX_AUTO_EXPORT + 1;
    private static final int DATA_INDEX_SOUND_MUTED = DATA_INDEX_AUTO_INSERT + 1;
    private static final int MACHINE_SLOT_COUNT = ShieldProjectorBlockEntity.INVENTORY_SLOT_COUNT;

    private final ShieldProjectorBlockEntity projectorBlockEntity;
    private final ContainerData projectorData;

    public ShieldProjectorMenu(
        int containerId,
        Inventory playerInventory,
        ShieldProjectorBlockEntity projectorBlockEntity,
        ContainerData projectorData
    ) {
        super(ModMenuType.COBBLESTONE_SHIELD_PROJECTOR_MENU.get(), containerId);
        this.projectorBlockEntity = projectorBlockEntity;
        this.projectorData = projectorData;

        checkContainerDataCount(projectorData, DATA_COUNT);
        this.addDataSlots(projectorData);

        this.addProjectorSlots();
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
    }

    public ShieldProjectorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    private ShieldProjectorMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(
            containerId,
            playerInventory,
            getRequiredBlockEntity(playerInventory, blockPos, ShieldProjectorBlockEntity.class, "Shield Projector"),
            new SimpleContainerData(DATA_COUNT)
        );
    }

    public int getProgress() {
        return this.projectorData.get(DATA_INDEX_PROGRESS);
    }

    public int getMaxProgress() {
        return this.projectorData.get(DATA_INDEX_MAX_PROGRESS);
    }

    public long getStoredCobblestonePower() {
        return this.getLongFromData(this.projectorData, DATA_INDEX_STORED_POWER);
    }

    public long getMaxCobblestonePower() {
        return this.projectorBlockEntity.getMaxCobblestonePower();
    }

    public long getStoredShield() {
        return this.getLongFromData(this.projectorData, DATA_INDEX_STORED_SHIELD);
    }

    public long getMaxShield() {
        return this.projectorBlockEntity.getMaxShieldCapacity();
    }

    public long getPreviewTotalCobblestonePower() {
        return this.projectorBlockEntity.getPreviewTotalCobblestonePower();
    }

    public long getPreviewCobblestonePowerPerTick() {
        return this.projectorBlockEntity.getPreviewCobblestonePowerPerTick();
    }

    @Override
    public long getCurrentCobblestonePowerRate() {
        return this.getLongFromData(this.projectorData, DATA_INDEX_CURRENT_POWER_RATE);
    }

    public boolean getIsAvailable() {
        return this.projectorBlockEntity.getIsAvailable();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        int dataIndex = DATA_INDEX_AUTOMATION_START + automationSide.getIndex();
        return AutomationMode.fromId(this.projectorData.get(dataIndex));
    }

    public boolean isAutoExportEnabled() {
        return this.projectorData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public boolean isAutoInsertEnabled() {
        return this.projectorData.get(DATA_INDEX_AUTO_INSERT) != 0;
    }

    @Override
    public boolean isSoundMuted() {
        return this.projectorData.get(DATA_INDEX_SOUND_MUTED) != 0;
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
    protected ShieldProjectorBlockEntity getMachineBlockEntity() {
        return this.projectorBlockEntity;
    }

    @Override
    protected net.minecraft.world.level.block.Block getMachineBlock() {
        return ModBlocks.COBBLESTONE_SHIELD_PROJECTOR.get();
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
                ShieldProjectorBlockEntity.ACCELERATION_SLOT_INDEX,
                ShieldProjectorBlockEntity.ACCELERATION_SLOT_INDEX + 1,
                false
            );
        } else if (MachineUpgradeHelper.isEnergizedCube(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                ShieldProjectorBlockEntity.ENERGIZED_CUBE_SLOT_INDEX,
                ShieldProjectorBlockEntity.ENERGIZED_CUBE_SLOT_INDEX + 1,
                false
            );
        } else if (MachineUpgradeHelper.isParallelChip(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                ShieldProjectorBlockEntity.PARALLEL_SLOT_INDEX,
                ShieldProjectorBlockEntity.PARALLEL_SLOT_INDEX + 1,
                false
            );
        } else if (ShieldProjectorUpgradeHelper.isValidCustomUpgrade(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                ShieldProjectorBlockEntity.CUSTOM_UPGRADE_SLOT_0_INDEX,
                ShieldProjectorBlockEntity.CUSTOM_UPGRADE_LAST_INDEX + 1,
                false
            );
        }

        if (!movedToMachine && ShieldProjectorBlockEntity.isCobblestonePowerItem(sourceStack)) {
            movedToMachine = this.moveItemStackTo(
                sourceStack,
                ShieldProjectorBlockEntity.POWER_SLOT_INDEX,
                ShieldProjectorBlockEntity.POWER_SLOT_INDEX + 1,
                false
            );
        }

        return movedToMachine;
    }

    @Override
    protected boolean handleMachineMenuButton(Player player, int id) {
        if (id == 0) {
            this.projectorBlockEntity.reverseIsAvailable();
            return true;
        }

        if (this.handleAutomationButtonClick(this.projectorBlockEntity, id, PROJECTOR_AUTOMATION_MODES)) {
            return true;
        }

        if (this.handleAutoExportButtonClick(this.projectorBlockEntity, id)) {
            return true;
        }

        if (this.handleAutoInsertButtonClick(this.projectorBlockEntity, id)) {
            return true;
        }

        if (this.handleMuteSoundButtonClick(this.projectorBlockEntity, id)) {
            return true;
        }

        return false;
    }

    private void addProjectorSlots() {
        ItemStackHandler itemStackHandler = this.projectorBlockEntity.getItemStackHandler();

        this.addSlot(new SlotItemHandler(
            itemStackHandler,
            ShieldProjectorBlockEntity.POWER_SLOT_INDEX,
            MachineGuiLayouts.PoweredMachine.POWER_SLOT_X,
            MachineGuiLayouts.PoweredMachine.POWER_SLOT_Y
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ShieldProjectorBlockEntity.isCobblestonePowerItem(stack);
            }
        });

        this.addSlot(new SlotItemHandler(
            itemStackHandler,
            ShieldProjectorBlockEntity.ACCELERATION_SLOT_INDEX,
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
            ShieldProjectorBlockEntity.ENERGIZED_CUBE_SLOT_INDEX,
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
            ShieldProjectorBlockEntity.PARALLEL_SLOT_INDEX,
            MachineGuiLayouts.UPGRADE_SLOT_X,
            MachineGuiLayouts.PARALLEL_SLOT_Y
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return MachineUpgradeHelper.isParallelChip(stack);
            }
        });

        for (int index = 0; index < ShieldProjectorBlockEntity.CUSTOM_UPGRADE_SLOT_COUNT; index++) {
            int slotIndex = ShieldProjectorBlockEntity.CUSTOM_UPGRADE_SLOT_0_INDEX + index;
            this.addSlot(new SlotItemHandler(
                itemStackHandler,
                slotIndex,
                ShieldProjectorBlockEntity.GuiSlots.getCustomUpgradeSlotX(index),
                ShieldProjectorBlockEntity.GuiSlots.getCustomUpgradeSlotY(index)
            ) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return ShieldProjectorUpgradeHelper.isValidCustomUpgrade(stack);
                }
            });
        }
    }

    @Override
    public List<JeiRecipeTransferDefinition> getJeiRecipeTransferDefinitions() {
        return List.of();
    }
}
