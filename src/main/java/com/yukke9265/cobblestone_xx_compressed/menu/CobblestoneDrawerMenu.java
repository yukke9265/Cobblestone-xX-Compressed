package com.yukke9265.cobblestone_xx_compressed.menu;

import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneDrawerBlock;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.BaseBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneDrawerBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CobblestoneDrawerMenu extends BaseMenu {
    private static final AutomationMode[] DRAWER_ITEM_AUTOMATION_MODES = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.INPUT,
        AutomationMode.OUTPUT,
        AutomationMode.IN_OUT
    };

    private static final int VOID_OVERFLOW_BUTTON_ID = 203;

    private static final int DATA_COUNT = 5 + BaseBlockEntity.AUTOMATION_FACE_COUNT + 3;
    private static final int DATA_INDEX_STORED_AMOUNT = 0;
    private static final int DATA_INDEX_MAX_AMOUNT = 2;
    private static final int DATA_INDEX_ITEM_ID = 4;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 5;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_ITEM_AUTOMATION_START + BaseBlockEntity.AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_INSERT = DATA_INDEX_AUTO_EXPORT + 1;
    private static final int DATA_INDEX_VOID_OVERFLOW = DATA_INDEX_AUTO_INSERT + 1;

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

    private final CobblestoneDrawerBlockEntity drawerBlockEntity;
    private final ContainerData drawerData;

    public CobblestoneDrawerMenu(int containerId, Inventory playerInventory, CobblestoneDrawerBlockEntity drawerBlockEntity, ContainerData drawerData) {
        super(ModMenuType.COBBLESTONE_DRAWER_MENU.get(), containerId);
        this.drawerBlockEntity = drawerBlockEntity;
        this.drawerData = drawerData;

        checkContainerDataCount(drawerData, DATA_COUNT);
        this.addDataSlots(drawerData);

        this.addDrawerSlots();
        this.addPlayerInventorySlots(playerInventory);
        this.addPlayerHotbarSlots(playerInventory);
        this.initSlotFilterSupport(this.drawerBlockEntity);
    }

    public CobblestoneDrawerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readBlockPos());
    }

    public long getStoredAmount() {
        return this.getLongFromData(this.drawerData, DATA_INDEX_STORED_AMOUNT);
    }

    public long getMaxStoredAmount() {
        return this.getLongFromData(this.drawerData, DATA_INDEX_MAX_AMOUNT);
    }

    public ItemStack getDisplayedStoredStack() {
        long storedAmount = this.getStoredAmount();
        if (storedAmount <= 0L) {
            return ItemStack.EMPTY;
        }

        // クライアントは BlockEntity の storedItem が遅延しやすいので、同期済みの itemId から復元する。
        int itemId = this.drawerData.get(DATA_INDEX_ITEM_ID);
        if (itemId < 0) {
            return ItemStack.EMPTY;
        }

        Item item = BuiltInRegistries.ITEM.byId(itemId);
        if (item == null) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item);
    }

    public AutomationMode getItemAutomationMode(AutomationSide automationSide) {
        return AutomationMode.fromId(this.drawerData.get(DATA_INDEX_ITEM_AUTOMATION_START + automationSide.getIndex()));
    }

    public boolean isAutoExportEnabled() {
        return this.drawerData.get(DATA_INDEX_AUTO_EXPORT) != 0;
    }

    public boolean isAutoInsertEnabled() {
        return this.drawerData.get(DATA_INDEX_AUTO_INSERT) != 0;
    }

    public boolean isVoidOverflowEnabled() {
        return this.drawerData.get(DATA_INDEX_VOID_OVERFLOW) != 0;
    }

    public int getItemAutomationButtonId(AutomationSide automationSide) {
        return this.getAutomationButtonId(automationSide.getIndex());
    }

    public int getAutoExportButtonId() {
        return this.getAutoExportToggleButtonId();
    }

    public int getAutoInsertButtonId() {
        return this.getAutoInsertToggleButtonId();
    }

    public int getVoidOverflowButtonId() {
        return VOID_OVERFLOW_BUTTON_ID;
    }

    @Override
    protected boolean handleMachineMenuButton(Player player, int id) {
        if (this.handleAutomationButtonClick(this.drawerBlockEntity, id, DRAWER_ITEM_AUTOMATION_MODES)) {
            return true;
        }

        if (this.handleAutoExportButtonClick(this.drawerBlockEntity, id)) {
            return true;
        }

        if (this.handleAutoInsertButtonClick(this.drawerBlockEntity, id)) {
            return true;
        }

        return this.handleVoidOverflowButtonClick(id);
    }

    private boolean handleVoidOverflowButtonClick(int buttonId) {
        if (buttonId != VOID_OVERFLOW_BUTTON_ID) {
            return false;
        }

        this.drawerBlockEntity.toggleVoidOverflowEnabled();
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.drawerBlockEntity.isRemoved()) {
            return false;
        }

        BlockPos blockPos = this.drawerBlockEntity.getBlockPos();
        if (!(player.level().getBlockState(blockPos).getBlock() instanceof CobblestoneDrawerBlock)) {
            return false;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
        if (blockEntity != this.drawerBlockEntity) {
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
            boolean movedToMachine = this.drawerBlockEntity.canAcceptItem(sourceStack)
                && this.moveItemStackTo(sourceStack, CobblestoneDrawerBlockEntity.INPUT_SLOT_INDEX, CobblestoneDrawerBlockEntity.INPUT_SLOT_INDEX + 1, false);

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

    private CobblestoneDrawerMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(containerId, playerInventory, getDrawerBlockEntity(playerInventory, blockPos), new SimpleContainerData(DATA_COUNT));
    }

    private static CobblestoneDrawerBlockEntity getDrawerBlockEntity(Inventory playerInventory, BlockPos blockPos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(blockPos);
        if (blockEntity instanceof CobblestoneDrawerBlockEntity drawerBlockEntity) {
            return drawerBlockEntity;
        }

        throw new IllegalStateException("Cobblestone Drawer の BlockEntity を取得できませんでした: " + blockPos);
    }

    private void addDrawerSlots() {
        ItemStackHandler itemStackHandler = this.drawerBlockEntity.getItemStackHandler();
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneDrawerBlockEntity.INPUT_SLOT_INDEX, INPUT_SLOT_X, MACHINE_SLOT_Y));
        this.addSlot(new SlotItemHandler(itemStackHandler, CobblestoneDrawerBlockEntity.OUTPUT_SLOT_INDEX, OUTPUT_SLOT_X, MACHINE_SLOT_Y) {
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
