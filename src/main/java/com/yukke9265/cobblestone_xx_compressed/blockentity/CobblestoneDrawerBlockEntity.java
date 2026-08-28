package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneDrawerBlock;
import com.yukke9265.cobblestone_xx_compressed.Config;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTarget;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTargetIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneDrawerMenu;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

public class CobblestoneDrawerBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int OUTPUT_SLOT_INDEX = 1;
    // CobblestoneDrawerMenu の入力スロット座標と揃えます。
    private static final int FILTER_INPUT_SLOT_X = 26;
    private static final int FILTER_INPUT_SLOT_Y = 17;

    private static final int DATA_INDEX_STORED_AMOUNT = 0;
    private static final int DATA_INDEX_STORED_AMOUNT_UPPER = 1;
    private static final int DATA_INDEX_MAX_AMOUNT = 2;
    private static final int DATA_INDEX_MAX_AMOUNT_UPPER = 3;
    private static final int DATA_INDEX_ITEM_ID = 4;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 5;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_ITEM_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_INSERT = DATA_INDEX_AUTO_EXPORT + 1;
    private static final int DATA_INDEX_VOID_OVERFLOW = DATA_INDEX_AUTO_INSERT + 1;

    private final long maxStoredAmount;
    private long storedAmount;
    private ItemStack storedItem = ItemStack.EMPTY;
    private boolean voidOverflowEnabled;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot != INPUT_SLOT_INDEX || stack.isEmpty()) {
                return false;
            }

            return CobblestoneDrawerBlockEntity.this.canAcceptItem(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneDrawerBlockEntity.this.setChanged();
        }
    };

    private final IItemHandler combinedAutomationHandler = new DrawerAutomationItemHandler(true, true, true, true);
    private final IItemHandler inputAutomationHandler = new DrawerAutomationItemHandler(true, false, true, false);
    private final IItemHandler outputAutomationHandler = new DrawerAutomationItemHandler(false, true, false, true);

    public CobblestoneDrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_DRAWER_BLOCK_ENTITY.get(), pos, state);
        this.maxStoredAmount = getCapacityFromState(state);

        for (int index = 0; index < AUTOMATION_FACE_COUNT; index++) {
            this.setAutomationMode(index, AutomationMode.DISABLED);
        }
    }

    public long getStoredAmount() {
        return this.storedAmount;
    }

    public long getMaxStoredAmount() {
        return this.maxStoredAmount;
    }

    public ItemStack getStoredItem() {
        return this.storedItem;
    }

    public boolean isVoidOverflowEnabled() {
        return this.voidOverflowEnabled;
    }

    public int getVoidOverflowEnabledId() {
        return this.voidOverflowEnabled ? 1 : 0;
    }

    public void setVoidOverflowEnabled(boolean voidOverflowEnabled) {
        this.voidOverflowEnabled = voidOverflowEnabled;
        this.setChanged();
    }

    public void toggleVoidOverflowEnabled() {
        this.setVoidOverflowEnabled(!this.voidOverflowEnabled);
    }

    private int getExternalSlotCount() {
        return Config.DRAWER_EXTERNAL_SLOT_COUNT.get();
    }

    public ItemStack getDisplayedStoredStack() {
        if (this.storedItem.isEmpty() || this.storedAmount <= 0L) {
            return ItemStack.EMPTY;
        }

        return this.storedItem.copyWithCount(this.getAmountInExternalSlot(0));
    }

    private int getMaxStackSize() {
        if (this.storedItem.isEmpty()) {
            return 64;
        }

        return this.storedItem.getMaxStackSize();
    }

    private int getAmountInExternalSlot(int slot) {
        if (slot < 0 || slot >= this.getExternalSlotCount() || this.storedItem.isEmpty() || this.storedAmount <= 0L) {
            return 0;
        }

        int maxStackSize = this.getMaxStackSize();
        long slotOffset = (long) slot * maxStackSize;
        if (slotOffset >= this.storedAmount) {
            return 0;
        }

        return (int) Math.min(maxStackSize, this.storedAmount - slotOffset);
    }

    private int getInsertableAmountInExternalSlot(int slot) {
        if (slot < 0 || slot >= this.getExternalSlotCount()) {
            return 0;
        }

        long remainingCapacity = this.maxStoredAmount - this.storedAmount;
        if (remainingCapacity <= 0L) {
            if (this.voidOverflowEnabled) {
                return this.getMaxStackSize();
            }

            return 0;
        }

        int maxStackSize = this.getMaxStackSize();
        long slotOffset = (long) slot * maxStackSize;
        if (slotOffset >= this.maxStoredAmount) {
            return 0;
        }

        long slotCapacity = Math.min(maxStackSize, this.maxStoredAmount - slotOffset);
        long occupiedInSlot = Math.max(0L, this.storedAmount - slotOffset);
        long freeInSlot = slotCapacity - Math.min(occupiedInSlot, slotCapacity);
        return (int) Math.min(remainingCapacity, freeInSlot);
    }

    private int getDisplayedItemId() {
        if (this.storedItem.isEmpty() || this.storedAmount <= 0L) {
            return -1;
        }

        return BuiltInRegistries.ITEM.getId(this.storedItem.getItem());
    }

    public boolean canAcceptItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (!this.getSlotFilters().allowsItem(FilterTargetIds.ITEM_INPUT, stack)) {
            return false;
        }

        return this.storedItem.isEmpty() || ItemStack.isSameItemSameComponents(this.storedItem, stack);
    }

    @Override
    public List<FilterTarget> getFilterTargets() {
        return List.of(
            FilterTarget.item(
                FilterTargetIds.ITEM_INPUT,
                FILTER_INPUT_SLOT_X,
                FILTER_INPUT_SLOT_Y
            )
        );
    }

    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public IItemHandler getAutomationItemHandler(@Nullable Direction side) {
        if (side == null) {
            return this.combinedAutomationHandler;
        }

        AutomationMode automationMode = this.getAutomationMode(AutomationSide.fromWorldSide(side, this.getBlockState()));
        if (automationMode == AutomationMode.IN_OUT) {
            return this.combinedAutomationHandler;
        }

        if (automationMode == AutomationMode.INPUT) {
            return this.inputAutomationHandler;
        }

        if (automationMode == AutomationMode.OUTPUT) {
            return this.outputAutomationHandler;
        }

        return EmptyItemHandler.INSTANCE;
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        this.pullInputsFromConfiguredSides();
        this.processManualInputSlot();
        this.processManualOutputSlot();
        this.autoExportStoredItems();
    }

    private void processManualInputSlot() {
        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty()) {
            return;
        }

        int acceptedAmount = this.fillInternal(inputStack, false);
        if (acceptedAmount > 0) {
            inputStack.shrink(acceptedAmount);
            this.setChanged();
        }
    }

    private void processManualOutputSlot() {
        if (this.storedItem.isEmpty() || this.storedAmount <= 0L) {
            return;
        }

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        int availableSpace;
        if (outputStack.isEmpty()) {
            availableSpace = this.storedItem.getMaxStackSize();
        } else if (!ItemStack.isSameItemSameComponents(outputStack, this.storedItem)) {
            return;
        } else {
            availableSpace = outputStack.getMaxStackSize() - outputStack.getCount();
        }

        if (availableSpace <= 0) {
            return;
        }

        ItemStack drainedStack = this.drainInternal(availableSpace, false);
        if (drainedStack.isEmpty()) {
            return;
        }

        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, drainedStack);
        } else {
            outputStack.grow(drainedStack.getCount());
            this.setChanged();
        }
    }

    private void autoExportStoredItems() {
        if (!this.isAutoExportEnabled() || this.storedItem.isEmpty() || this.storedAmount <= 0L) {
            return;
        }

        for (int slotIndex = 0; slotIndex < this.getExternalSlotCount(); slotIndex++) {
            int exportCount = this.getAmountInExternalSlot(slotIndex);
            if (exportCount <= 0) {
                break;
            }

            ItemStack exportStack = this.storedItem.copyWithCount(exportCount);
            int originalCount = exportStack.getCount();
            ItemStack remainingStack = this.pushItemStackToConfiguredSides(exportStack, AutomationMode.OUTPUT, AutomationMode.IN_OUT);
            int exportedCount = originalCount - remainingStack.getCount();
            if (exportedCount <= 0) {
                break;
            }

            this.drainInternal(exportedCount, false);
        }
    }

    private int fillInternal(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return 0;
        }

        if (!this.canAcceptItem(stack)) {
            return 0;
        }

        int stackCount = stack.getCount();
        long remainingCapacity = this.maxStoredAmount - this.storedAmount;
        int storedAmount = 0;
        if (remainingCapacity > 0L) {
            storedAmount = (int) Math.min(remainingCapacity, stackCount);
        }

        if (storedAmount <= 0 && !this.voidOverflowEnabled) {
            return 0;
        }

        int consumedAmount = this.voidOverflowEnabled ? stackCount : storedAmount;
        if (consumedAmount <= 0) {
            return 0;
        }

        if (!simulate) {
            if (this.storedItem.isEmpty()) {
                this.storedItem = stack.copyWithCount(1);
            }

            if (storedAmount > 0) {
                this.storedAmount += storedAmount;
            }

            this.syncToClient();
        }

        return consumedAmount;
    }

    private ItemStack drainInternal(int amount, boolean simulate) {
        if (this.storedItem.isEmpty() || this.storedAmount <= 0L || amount <= 0) {
            return ItemStack.EMPTY;
        }

        int drainedAmount = (int) Math.min(this.storedAmount, amount);
        ItemStack drainedStack = this.storedItem.copyWithCount(drainedAmount);

        if (!simulate) {
            this.storedAmount -= drainedAmount;
            if (this.storedAmount <= 0L) {
                this.storedAmount = 0L;
                this.storedItem = ItemStack.EMPTY;
            }

            this.syncToClient();
        }

        return drainedStack;
    }

    private void syncToClient() {
        this.setChanged();

        Level currentLevel = this.level;
        if (currentLevel != null) {
            BlockState currentState = this.getBlockState();
            currentLevel.sendBlockUpdated(this.worldPosition, currentState, currentState, 3);
        }
    }

    private static long getCapacityFromState(BlockState state) {
        if (state.getBlock() instanceof CobblestoneDrawerBlock drawerBlock) {
            return drawerBlock.getCapacity();
        }

        throw new IllegalStateException("Cobblestone Drawer 以外の block state で生成しようとしました: " + state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData drawerData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_STORED_AMOUNT) {
                    return LongDataHelper.lowerInt(CobblestoneDrawerBlockEntity.this.storedAmount);
                }

                if (index == DATA_INDEX_STORED_AMOUNT_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneDrawerBlockEntity.this.storedAmount);
                }

                if (index == DATA_INDEX_MAX_AMOUNT) {
                    return LongDataHelper.lowerInt(CobblestoneDrawerBlockEntity.this.maxStoredAmount);
                }

                if (index == DATA_INDEX_MAX_AMOUNT_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneDrawerBlockEntity.this.maxStoredAmount);
                }

                if (index == DATA_INDEX_ITEM_ID) {
                    return CobblestoneDrawerBlockEntity.this.getDisplayedItemId();
                }

                if (index >= DATA_INDEX_ITEM_AUTOMATION_START && index < DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneDrawerBlockEntity.this.getAutomationModeId(index - DATA_INDEX_ITEM_AUTOMATION_START);
                }

                if (index == DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneDrawerBlockEntity.this.isAutoExportEnabled() ? 1 : 0;
                }

                if (index == DATA_INDEX_AUTO_INSERT) {
                    return CobblestoneDrawerBlockEntity.this.getAutoInsertEnabledId();
                }

                if (index == DATA_INDEX_VOID_OVERFLOW) {
                    return CobblestoneDrawerBlockEntity.this.getVoidOverflowEnabledId();
                }

                return 0;
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return DATA_INDEX_VOID_OVERFLOW + 1;
            }
        };

        return new CobblestoneDrawerMenu(containerId, playerInventory, this, drawerData);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.saveAutomationModes(tag);
        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
        tag.putLong("storedAmount", this.storedAmount);
        tag.putBoolean("voidOverflowEnabled", this.voidOverflowEnabled);
        if (!this.storedItem.isEmpty()) {
            tag.put("storedItem", this.storedItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.loadAutomationModes(tag);
        if (tag.contains("inventory", CompoundTag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        }

        this.storedAmount = tag.getLong("storedAmount");
        this.voidOverflowEnabled = tag.getBoolean("voidOverflowEnabled");
        if (tag.contains("storedItem", CompoundTag.TAG_COMPOUND)) {
            this.storedItem = ItemStack.parseOptional(registries, tag.getCompound("storedItem"));
            if (!this.storedItem.isEmpty()) {
                this.storedItem = this.storedItem.copyWithCount(1);
            }
        } else {
            this.storedItem = ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        this.loadAdditional(tag, registries);
    }

    private enum AutomationSlotKind {
        BULK,
        GUI_INPUT,
        GUI_OUTPUT,
        INVALID
    }

    // 外部 I/O 用スロット番号を、内部ストレージ / GUI 入出力へ振り分けます。
    private AutomationSlotKind getAutomationSlotKind(int slot, boolean includeGuiInput, boolean includeGuiOutput) {
        int bulkSlotCount = this.getExternalSlotCount();
        if (slot >= 0 && slot < bulkSlotCount) {
            return AutomationSlotKind.BULK;
        }

        int nextSlot = bulkSlotCount;
        if (includeGuiInput) {
            if (slot == nextSlot) {
                return AutomationSlotKind.GUI_INPUT;
            }

            nextSlot++;
        }

        if (includeGuiOutput && slot == nextSlot) {
            return AutomationSlotKind.GUI_OUTPUT;
        }

        return AutomationSlotKind.INVALID;
    }

    private int getAutomationSlotCount(boolean includeGuiInput, boolean includeGuiOutput) {
        int slotCount = this.getExternalSlotCount();
        if (includeGuiInput) {
            slotCount++;
        }

        if (includeGuiOutput) {
            slotCount++;
        }

        return slotCount;
    }

    private class DrawerAutomationItemHandler implements IItemHandler {
        private final boolean canFillBulk;
        private final boolean canDrainBulk;
        private final boolean includeGuiInput;
        private final boolean includeGuiOutput;

        private DrawerAutomationItemHandler(
            boolean canFillBulk,
            boolean canDrainBulk,
            boolean includeGuiInput,
            boolean includeGuiOutput
        ) {
            this.canFillBulk = canFillBulk;
            this.canDrainBulk = canDrainBulk;
            this.includeGuiInput = includeGuiInput;
            this.includeGuiOutput = includeGuiOutput;
        }

        @Override
        public int getSlots() {
            return CobblestoneDrawerBlockEntity.this.getAutomationSlotCount(this.includeGuiInput, this.includeGuiOutput);
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            AutomationSlotKind slotKind = CobblestoneDrawerBlockEntity.this.getAutomationSlotKind(
                slot,
                this.includeGuiInput,
                this.includeGuiOutput
            );
            if (slotKind == AutomationSlotKind.BULK) {
                int amountInSlot = CobblestoneDrawerBlockEntity.this.getAmountInExternalSlot(slot);
                if (amountInSlot <= 0) {
                    return ItemStack.EMPTY;
                }

                return CobblestoneDrawerBlockEntity.this.storedItem.copyWithCount(amountInSlot);
            }

            if (slotKind == AutomationSlotKind.GUI_INPUT) {
                return CobblestoneDrawerBlockEntity.this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
            }

            if (slotKind == AutomationSlotKind.GUI_OUTPUT) {
                return CobblestoneDrawerBlockEntity.this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) {
                return stack;
            }

            AutomationSlotKind slotKind = CobblestoneDrawerBlockEntity.this.getAutomationSlotKind(
                slot,
                this.includeGuiInput,
                this.includeGuiOutput
            );
            if (slotKind == AutomationSlotKind.BULK) {
                if (!this.canFillBulk) {
                    return stack;
                }

                int acceptedAmount = CobblestoneDrawerBlockEntity.this.fillInternal(stack, simulate);
                if (acceptedAmount <= 0) {
                    return stack;
                }

                ItemStack remainingStack = stack.copy();
                remainingStack.shrink(acceptedAmount);
                return remainingStack;
            }

            if (slotKind == AutomationSlotKind.GUI_INPUT) {
                return CobblestoneDrawerBlockEntity.this.itemStackHandler.insertItem(INPUT_SLOT_INDEX, stack, simulate);
            }

            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount <= 0) {
                return ItemStack.EMPTY;
            }

            AutomationSlotKind slotKind = CobblestoneDrawerBlockEntity.this.getAutomationSlotKind(
                slot,
                this.includeGuiInput,
                this.includeGuiOutput
            );
            if (slotKind == AutomationSlotKind.BULK) {
                if (!this.canDrainBulk) {
                    return ItemStack.EMPTY;
                }

                // 仮想区画は先頭から順に埋まるため、前の区画が空くまで後ろは取り出し不可。
                for (int priorSlot = 0; priorSlot < slot; priorSlot++) {
                    if (CobblestoneDrawerBlockEntity.this.getAmountInExternalSlot(priorSlot) > 0) {
                        return ItemStack.EMPTY;
                    }
                }

                int availableAmount = CobblestoneDrawerBlockEntity.this.getAmountInExternalSlot(slot);
                int extractAmount = Math.min(amount, availableAmount);
                if (extractAmount <= 0) {
                    return ItemStack.EMPTY;
                }

                return CobblestoneDrawerBlockEntity.this.drainInternal(extractAmount, simulate);
            }

            if (slotKind == AutomationSlotKind.GUI_OUTPUT) {
                return CobblestoneDrawerBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            AutomationSlotKind slotKind = CobblestoneDrawerBlockEntity.this.getAutomationSlotKind(
                slot,
                this.includeGuiInput,
                this.includeGuiOutput
            );
            if (slotKind == AutomationSlotKind.BULK) {
                if (this.canFillBulk && !this.canDrainBulk) {
                    return CobblestoneDrawerBlockEntity.this.getInsertableAmountInExternalSlot(slot);
                }

                return CobblestoneDrawerBlockEntity.this.getMaxStackSize();
            }

            if (slotKind == AutomationSlotKind.GUI_INPUT) {
                return CobblestoneDrawerBlockEntity.this.itemStackHandler.getSlotLimit(INPUT_SLOT_INDEX);
            }

            if (slotKind == AutomationSlotKind.GUI_OUTPUT) {
                return CobblestoneDrawerBlockEntity.this.itemStackHandler.getSlotLimit(OUTPUT_SLOT_INDEX);
            }

            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            AutomationSlotKind slotKind = CobblestoneDrawerBlockEntity.this.getAutomationSlotKind(
                slot,
                this.includeGuiInput,
                this.includeGuiOutput
            );
            if (slotKind == AutomationSlotKind.BULK) {
                return CobblestoneDrawerBlockEntity.this.canAcceptItem(stack);
            }

            if (slotKind == AutomationSlotKind.GUI_INPUT) {
                return CobblestoneDrawerBlockEntity.this.itemStackHandler.isItemValid(INPUT_SLOT_INDEX, stack);
            }

            return false;
        }
    }
}
