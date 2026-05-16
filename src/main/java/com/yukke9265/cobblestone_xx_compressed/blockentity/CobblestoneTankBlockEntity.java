package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneTankBlock;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneTankMenu;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

public class CobblestoneTankBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int OUTPUT_SLOT_INDEX = 1;

    private static final int DATA_INDEX_STORED_FLUID = 0;
    private static final int DATA_INDEX_STORED_FLUID_UPPER = 1;
    private static final int DATA_INDEX_MAX_FLUID = 2;
    private static final int DATA_INDEX_MAX_FLUID_UPPER = 3;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 4;
    private static final int DATA_INDEX_FLUID_AUTOMATION_START = DATA_INDEX_ITEM_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_FLUID_AUTOMATION_START + AUTOMATION_FACE_COUNT;

    private final long maxFluidAmount;
    private long storedFluidAmount;
    private FluidStack storedFluid = FluidStack.EMPTY;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot != INPUT_SLOT_INDEX) {
                return false;
            }

            return CobblestoneTankBlockEntity.isFluidContainerItem(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneTankBlockEntity.this.setChanged();
        }
    };

    private final IItemHandler inputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneTankBlockEntity.this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0) {
                return stack;
            }

            return CobblestoneTankBlockEntity.this.itemStackHandler.insertItem(INPUT_SLOT_INDEX, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot != 0) {
                return 0;
            }

            return CobblestoneTankBlockEntity.this.itemStackHandler.getSlotLimit(INPUT_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == 0 && CobblestoneTankBlockEntity.this.itemStackHandler.isItemValid(INPUT_SLOT_INDEX, stack);
        }
    };

    private final IItemHandler outputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneTankBlockEntity.this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneTankBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot != 0) {
                return 0;
            }

            return CobblestoneTankBlockEntity.this.itemStackHandler.getSlotLimit(OUTPUT_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };

    private final IItemHandler combinedAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot < 0 || slot > OUTPUT_SLOT_INDEX) {
                return ItemStack.EMPTY;
            }

            return CobblestoneTankBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != INPUT_SLOT_INDEX) {
                return stack;
            }

            return CobblestoneTankBlockEntity.this.itemStackHandler.insertItem(INPUT_SLOT_INDEX, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != OUTPUT_SLOT_INDEX) {
                return ItemStack.EMPTY;
            }

            return CobblestoneTankBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 0 || slot > OUTPUT_SLOT_INDEX) {
                return 0;
            }

            return CobblestoneTankBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == INPUT_SLOT_INDEX && CobblestoneTankBlockEntity.this.itemStackHandler.isItemValid(INPUT_SLOT_INDEX, stack);
        }
    };

    private final IFluidHandler internalFluidHandler = new CobblestoneTankFluidHandler(true, true);
    private final IFluidHandler inputFluidHandler = new CobblestoneTankFluidHandler(true, false);
    private final IFluidHandler outputFluidHandler = new CobblestoneTankFluidHandler(false, true);

    public CobblestoneTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_TANK_BLOCK_ENTITY.get(), pos, state);
        this.maxFluidAmount = getCapacityFromState(state);

        for (int index = 0; index < AUTOMATION_FACE_COUNT; index++) {
            this.setAutomationMode(index, AutomationMode.DISABLED);
            this.setFluidAutomationMode(index, AutomationMode.DISABLED);
        }
    }

    public static boolean isFluidContainerItem(ItemStack stack) {
        return FluidUtil.getFluidHandler(stack).isPresent();
    }

    public long getStoredFluidAmount() {
        return this.storedFluidAmount;
    }

    public long getMaxFluidAmount() {
        return this.maxFluidAmount;
    }

    public FluidStack getDisplayedFluid() {
        if (this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        return this.storedFluid.copyWithAmount((int) Math.min(this.storedFluidAmount, Integer.MAX_VALUE));
    }

    public boolean handleFluidIndicatorClick(Player player, boolean processAll) {
        ItemStack carriedStack = player.containerMenu.getCarried();
        if (this.tryProcessPlayerFluidContainer(player, carriedStack, true, processAll)) {
            return true;
        }

        int selectedSlot = player.getInventory().selected;
        ItemStack selectedStack = player.getInventory().getItem(selectedSlot);
        return this.tryProcessPlayerFluidContainer(player, selectedStack, false, processAll);
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

    @Nullable
    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        if (side == null) {
            return this.internalFluidHandler;
        }

        AutomationMode automationMode = this.getFluidAutomationMode(AutomationSide.fromWorldSide(side, this.getBlockState()));
        if (automationMode == AutomationMode.IN_OUT) {
            return this.internalFluidHandler;
        }

        if (automationMode == AutomationMode.INPUT) {
            return this.inputFluidHandler;
        }

        if (automationMode == AutomationMode.OUTPUT) {
            return this.outputFluidHandler;
        }

        return null;
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        this.processFluidContainerItem();
        this.autoExportFluid();
    }

    private void processFluidContainerItem() {
        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty()) {
            return;
        }

        ItemStack singleContainer = inputStack.copy();
        singleContainer.setCount(1);

        Optional<IFluidHandlerItem> optionalHandler = FluidUtil.getFluidHandler(singleContainer);
        if (optionalHandler.isEmpty()) {
            return;
        }

        IFluidHandlerItem fluidHandlerItem = optionalHandler.get();
        if (this.tryDrainContainerIntoTank(inputStack, fluidHandlerItem, true)) {
            return;
        }

        this.tryFillContainerFromTank(inputStack, fluidHandlerItem, true);
    }

    private boolean tryProcessPlayerFluidContainer(Player player, ItemStack sourceStack, boolean carriedStack, boolean processAll) {
        if (sourceStack.isEmpty() || !CobblestoneTankBlockEntity.isFluidContainerItem(sourceStack)) {
            return false;
        }

        ItemStack singleContainer = sourceStack.copy();
        singleContainer.setCount(1);

        Optional<IFluidHandlerItem> optionalHandler = FluidUtil.getFluidHandler(singleContainer);
        if (optionalHandler.isEmpty()) {
            return false;
        }

        IFluidHandlerItem fluidHandlerItem = optionalHandler.get();
        ItemStack processedContainer = this.tryDrainPlayerContainer(fluidHandlerItem, processAll);
        if (processedContainer.isEmpty()) {
            processedContainer = this.tryFillPlayerContainer(fluidHandlerItem, processAll);
        }

        if (processedContainer.isEmpty()) {
            return false;
        }

        this.replaceProcessedPlayerContainer(player, sourceStack, processedContainer, carriedStack);
        return true;
    }

    private ItemStack tryDrainPlayerContainer(IFluidHandlerItem fluidHandlerItem, boolean processAll) {
        int transferredAmount = 0;
        while (true) {
            int stepTransferredAmount = this.drainFluidHandlerIntoTank(fluidHandlerItem);
            if (stepTransferredAmount <= 0) {
                break;
            }

            transferredAmount += stepTransferredAmount;
            if (!processAll) {
                break;
            }
        }

        if (transferredAmount <= 0) {
            return ItemStack.EMPTY;
        }

        return fluidHandlerItem.getContainer().copy();
    }

    private ItemStack tryFillPlayerContainer(IFluidHandlerItem fluidHandlerItem, boolean processAll) {
        int transferredAmount = 0;
        while (true) {
            int stepTransferredAmount = this.fillFluidHandlerFromTank(fluidHandlerItem);
            if (stepTransferredAmount <= 0) {
                break;
            }

            transferredAmount += stepTransferredAmount;
            if (!processAll) {
                break;
            }
        }

        if (transferredAmount <= 0) {
            return ItemStack.EMPTY;
        }

        return fluidHandlerItem.getContainer().copy();
    }

    private void replaceProcessedPlayerContainer(Player player, ItemStack sourceStack, ItemStack processedContainer, boolean carriedStack) {
        if (sourceStack.getCount() == 1) {
            if (carriedStack) {
                player.containerMenu.setCarried(processedContainer);
            } else {
                player.getInventory().setItem(player.getInventory().selected, processedContainer);
            }
            return;
        }

        sourceStack.shrink(1);
        if (!player.getInventory().add(processedContainer)) {
            player.drop(processedContainer, false);
        }
    }

    private boolean tryDrainContainerIntoTank(ItemStack originalInputStack, IFluidHandlerItem fluidHandlerItem, boolean processAll) {
        int transferredAmount = 0;
        while (true) {
            int stepTransferredAmount = this.drainFluidHandlerIntoTank(fluidHandlerItem);
            if (stepTransferredAmount <= 0) {
                break;
            }

            transferredAmount += stepTransferredAmount;
            if (!processAll) {
                break;
            }
        }

        if (transferredAmount <= 0) {
            return false;
        }

        ItemStack processedContainer = fluidHandlerItem.getContainer().copy();
        if (!this.canMoveProcessedContainer(processedContainer)) {
            return false;
        }

        this.finishContainerProcessing(originalInputStack, processedContainer);
        return true;
    }

    private boolean tryFillContainerFromTank(ItemStack originalInputStack, IFluidHandlerItem fluidHandlerItem, boolean processAll) {
        int transferredAmount = 0;
        while (true) {
            int stepTransferredAmount = this.fillFluidHandlerFromTank(fluidHandlerItem);
            if (stepTransferredAmount <= 0) {
                break;
            }

            transferredAmount += stepTransferredAmount;
            if (!processAll) {
                break;
            }
        }

        if (transferredAmount <= 0) {
            return false;
        }

        ItemStack processedContainer = fluidHandlerItem.getContainer().copy();
        if (!this.canMoveProcessedContainer(processedContainer)) {
            return false;
        }

        this.finishContainerProcessing(originalInputStack, processedContainer);
        return true;
    }

    private int drainFluidHandlerIntoTank(IFluidHandlerItem fluidHandlerItem) {
        for (int tankIndex = 0; tankIndex < fluidHandlerItem.getTanks(); tankIndex++) {
            FluidStack fluidInItem = fluidHandlerItem.getFluidInTank(tankIndex);
            if (fluidInItem.isEmpty()) {
                continue;
            }

            int acceptedAmount = this.fillInternal(fluidInItem, IFluidHandler.FluidAction.SIMULATE);
            if (acceptedAmount <= 0) {
                continue;
            }

            FluidStack drainedFluid = fluidHandlerItem.drain(fluidInItem.copyWithAmount(acceptedAmount), IFluidHandler.FluidAction.EXECUTE);
            if (drainedFluid.isEmpty()) {
                continue;
            }

            this.fillInternal(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
            return drainedFluid.getAmount();
        }

        return 0;
    }

    private int fillFluidHandlerFromTank(IFluidHandlerItem fluidHandlerItem) {
        if (this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return 0;
        }

        FluidStack availableFluid = this.storedFluid.copyWithAmount((int) Math.min(this.storedFluidAmount, Integer.MAX_VALUE));
        int filledAmount = fluidHandlerItem.fill(availableFluid, IFluidHandler.FluidAction.EXECUTE);
        if (filledAmount <= 0) {
            return 0;
        }

        this.drainInternal(filledAmount, IFluidHandler.FluidAction.EXECUTE);
        return filledAmount;
    }

    private boolean canMoveProcessedContainer(ItemStack processedContainer) {
        if (processedContainer.isEmpty()) {
            return false;
        }

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameComponents(outputStack, processedContainer)) {
            return false;
        }

        return outputStack.getCount() + processedContainer.getCount() <= outputStack.getMaxStackSize();
    }

    private void finishContainerProcessing(ItemStack originalInputStack, ItemStack processedContainer) {
        originalInputStack.shrink(1);

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, processedContainer);
        } else {
            outputStack.grow(processedContainer.getCount());
            this.setChanged();
        }

        this.syncToClient();
    }

    private void autoExportFluid() {
        if (!this.isAutoExportEnabled() || this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return;
        }

        FluidStack exportStack = this.storedFluid.copyWithAmount((int) Math.min(this.storedFluidAmount, Integer.MAX_VALUE));
        int originalAmount = exportStack.getAmount();
        FluidStack remainingFluid = this.pushFluidToConfiguredSides(exportStack, AutomationMode.OUTPUT, AutomationMode.IN_OUT);
        int exportedAmount = originalAmount - remainingFluid.getAmount();
        if (exportedAmount <= 0) {
            return;
        }

        this.drainInternal(exportedAmount, IFluidHandler.FluidAction.EXECUTE);
    }

    private int fillInternal(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }

        if (!this.storedFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(this.storedFluid, resource)) {
            return 0;
        }

        long remainingCapacity = this.maxFluidAmount - this.storedFluidAmount;
        if (remainingCapacity <= 0L) {
            return 0;
        }

        int acceptedAmount = (int) Math.min(remainingCapacity, resource.getAmount());
        if (acceptedAmount <= 0) {
            return 0;
        }

        if (action.execute()) {
            if (this.storedFluid.isEmpty()) {
                this.storedFluid = resource.copyWithAmount(1);
            }

            this.storedFluidAmount += acceptedAmount;
            this.syncToClient();
        }

        return acceptedAmount;
    }

    private FluidStack drainInternal(int amount, IFluidHandler.FluidAction action) {
        if (this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L || amount <= 0) {
            return FluidStack.EMPTY;
        }

        int drainedAmount = (int) Math.min(this.storedFluidAmount, amount);
        FluidStack drainedFluid = this.storedFluid.copyWithAmount(drainedAmount);

        if (action.execute()) {
            this.storedFluidAmount -= drainedAmount;
            if (this.storedFluidAmount <= 0L) {
                this.storedFluidAmount = 0L;
                this.storedFluid = FluidStack.EMPTY;
            }

            this.syncToClient();
        }

        return drainedFluid;
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
        if (state.getBlock() instanceof CobblestoneTankBlock tankBlock) {
            return tankBlock.getCapacity();
        }

        throw new IllegalStateException("Cobblestone Tank 以外の block state で生成しようとしました: " + state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData tankData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_STORED_FLUID) {
                    return LongDataHelper.lowerInt(CobblestoneTankBlockEntity.this.storedFluidAmount);
                }

                if (index == DATA_INDEX_STORED_FLUID_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneTankBlockEntity.this.storedFluidAmount);
                }

                if (index == DATA_INDEX_MAX_FLUID) {
                    return LongDataHelper.lowerInt(CobblestoneTankBlockEntity.this.maxFluidAmount);
                }

                if (index == DATA_INDEX_MAX_FLUID_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneTankBlockEntity.this.maxFluidAmount);
                }

                if (index >= DATA_INDEX_ITEM_AUTOMATION_START && index < DATA_INDEX_FLUID_AUTOMATION_START) {
                    return CobblestoneTankBlockEntity.this.getAutomationModeId(index - DATA_INDEX_ITEM_AUTOMATION_START);
                }

                if (index >= DATA_INDEX_FLUID_AUTOMATION_START && index < DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneTankBlockEntity.this.getFluidAutomationModeId(index - DATA_INDEX_FLUID_AUTOMATION_START);
                }

                if (index == DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneTankBlockEntity.this.isAutoExportEnabled() ? 1 : 0;
                }

                return 0;
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return DATA_INDEX_AUTO_EXPORT + 1;
            }
        };

        return new CobblestoneTankMenu(containerId, playerInventory, this, tankData);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.saveAutomationModes(tag);
        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
        tag.putLong("storedFluidAmount", this.storedFluidAmount);
        if (!this.storedFluid.isEmpty()) {
            tag.put("storedFluid", this.storedFluid.save(registries));
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.loadAutomationModes(tag);
        if (tag.contains("inventory", CompoundTag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        }

        this.storedFluidAmount = tag.getLong("storedFluidAmount");
        if (tag.contains("storedFluid", CompoundTag.TAG_COMPOUND)) {
            this.storedFluid = FluidStack.parseOptional(registries, tag.getCompound("storedFluid"));
            if (!this.storedFluid.isEmpty()) {
                this.storedFluid = this.storedFluid.copyWithAmount(1);
            }
        } else {
            this.storedFluid = FluidStack.EMPTY;
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

    private class CobblestoneTankFluidHandler implements IFluidHandler {
        private final boolean canFill;
        private final boolean canDrain;

        private CobblestoneTankFluidHandler(boolean canFill, boolean canDrain) {
            this.canFill = canFill;
            this.canDrain = canDrain;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            if (tank != 0) {
                return FluidStack.EMPTY;
            }

            return CobblestoneTankBlockEntity.this.getDisplayedFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            if (tank != 0) {
                return 0;
            }

            return (int) Math.min(CobblestoneTankBlockEntity.this.maxFluidAmount, Integer.MAX_VALUE);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0 && (CobblestoneTankBlockEntity.this.storedFluid.isEmpty()
                || FluidStack.isSameFluidSameComponents(CobblestoneTankBlockEntity.this.storedFluid, stack));
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!this.canFill) {
                return 0;
            }

            return CobblestoneTankBlockEntity.this.fillInternal(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (!this.canDrain || resource.isEmpty() || CobblestoneTankBlockEntity.this.storedFluid.isEmpty()) {
                return FluidStack.EMPTY;
            }

            if (!FluidStack.isSameFluidSameComponents(CobblestoneTankBlockEntity.this.storedFluid, resource)) {
                return FluidStack.EMPTY;
            }

            return CobblestoneTankBlockEntity.this.drainInternal(resource.getAmount(), action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            if (!this.canDrain) {
                return FluidStack.EMPTY;
            }

            return CobblestoneTankBlockEntity.this.drainInternal(maxDrain, action);
        }
    }
}