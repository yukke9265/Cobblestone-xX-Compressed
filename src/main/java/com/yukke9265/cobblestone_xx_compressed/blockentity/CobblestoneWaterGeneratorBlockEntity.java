package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneWaterGeneratorMenu;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

/**
 * CP を水へ 1:1 (mb) で変換する機械です。
 *
 * FE Generator の CP バッファ上限と Tank の流体搬出・バケツ充填を組み合わせ、
 * 入力バケツスロットは持たず出力側だけで容器を扱います。
 */
@SuppressWarnings("null")
public class CobblestoneWaterGeneratorBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int POWER_SLOT_INDEX = 0;
    public static final int BUCKET_SLOT_INDEX = 1;

    // FE Generator と同じ理論上限を CP バッファと水タンクの両方に使います。
    public static final long MAX_COBBLESTONE_POWER = 17_592_186_044_416L;
    public static final long MAX_FLUID_AMOUNT = 17_592_186_044_416L;

    private static final int DATA_INDEX_STORED_POWER = 0;
    private static final int DATA_INDEX_STORED_POWER_UPPER = 1;
    private static final int DATA_INDEX_MAX_STORED_POWER = 2;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 3;
    private static final int DATA_INDEX_STORED_FLUID = 4;
    private static final int DATA_INDEX_STORED_FLUID_UPPER = 5;
    private static final int DATA_INDEX_MAX_FLUID = 6;
    private static final int DATA_INDEX_MAX_FLUID_UPPER = 7;
    private static final int DATA_INDEX_FLUID_ID = 8;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 9;
    private static final int DATA_INDEX_FLUID_AUTOMATION_START = DATA_INDEX_ITEM_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_FLUID_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_INSERT = DATA_INDEX_AUTO_EXPORT + 1;
    private static final int DATA_INDEX_SOUND_MUTED = DATA_INDEX_AUTO_INSERT + 1;
    private static final int DATA_INDEX_CONVERTED_FLUID = DATA_INDEX_SOUND_MUTED + 1;
    private static final int DATA_INDEX_CONVERTED_FLUID_UPPER = DATA_INDEX_CONVERTED_FLUID + 1;
    private static final int DATA_INDEX_CURRENT_POWER_RATE = DATA_INDEX_CONVERTED_FLUID_UPPER + 1;
    private static final int DATA_INDEX_CURRENT_POWER_RATE_UPPER = DATA_INDEX_CURRENT_POWER_RATE + 1;

    private long storedCobblestonePower;
    private long storedFluidAmount;
    private FluidStack storedFluid = FluidStack.EMPTY;
    private long lastConvertedFluidAmount;
    private boolean isAvailable = true;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == POWER_SLOT_INDEX) {
                return CobblestonePowerHelper.isCobblestonePowerItem(stack);
            }

            if (slot == BUCKET_SLOT_INDEX) {
                return CobblestoneWaterGeneratorBlockEntity.isEmptyFluidContainerItem(stack);
            }

            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneWaterGeneratorBlockEntity.this.setChanged();
        }
    };

    private final IItemHandler cobblestoneInputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            return slot == 0
                ? CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.getStackInSlot(POWER_SLOT_INDEX)
                : ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0) {
                return stack;
            }

            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.insertItem(POWER_SLOT_INDEX, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.getSlotLimit(POWER_SLOT_INDEX) : 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == 0 && CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.isItemValid(POWER_SLOT_INDEX, stack);
        }
    };

    private final IItemHandler bucketInputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            return slot == 0
                ? CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.getStackInSlot(BUCKET_SLOT_INDEX)
                : ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0) {
                return stack;
            }

            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.insertItem(BUCKET_SLOT_INDEX, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.getSlotLimit(BUCKET_SLOT_INDEX) : 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == 0 && CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.isItemValid(BUCKET_SLOT_INDEX, stack);
        }
    };

    private final IItemHandler bucketOutputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            return slot == 0
                ? CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.getStackInSlot(BUCKET_SLOT_INDEX)
                : ItemStack.EMPTY;
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

            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.extractItem(BUCKET_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.getSlotLimit(BUCKET_SLOT_INDEX) : 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };

    private final IItemHandler bucketCombinedAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.getStackInSlot(BUCKET_SLOT_INDEX);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.insertItem(BUCKET_SLOT_INDEX, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.extractItem(BUCKET_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.getSlotLimit(BUCKET_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.isItemValid(BUCKET_SLOT_INDEX, stack);
        }
    };

    private final IItemHandler automationAccessHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot < POWER_SLOT_INDEX || slot > BUCKET_SLOT_INDEX) {
                return ItemStack.EMPTY;
            }

            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot < POWER_SLOT_INDEX || slot > BUCKET_SLOT_INDEX) {
                return stack;
            }

            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.insertItem(slot, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != BUCKET_SLOT_INDEX) {
                return ItemStack.EMPTY;
            }

            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.extractItem(BUCKET_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < POWER_SLOT_INDEX || slot > BUCKET_SLOT_INDEX) {
                return 0;
            }

            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot < POWER_SLOT_INDEX || slot > BUCKET_SLOT_INDEX) {
                return false;
            }

            return CobblestoneWaterGeneratorBlockEntity.this.itemStackHandler.isItemValid(slot, stack);
        }
    };

    private final IFluidHandler internalFluidHandler = new WaterGeneratorFluidHandler(true, true);
    private final IFluidHandler outputFluidHandler = new WaterGeneratorFluidHandler(false, true);

    public CobblestoneWaterGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_WATER_GENERATOR_BLOCK_ENTITY.get(), pos, state);

        for (int index = 0; index < AUTOMATION_FACE_COUNT; index++) {
            this.setAutomationMode(index, AutomationMode.DISABLED);
            this.setFluidAutomationMode(index, AutomationMode.DISABLED);
        }
    }

    public static boolean isFluidContainerItem(ItemStack stack) {
        return FluidUtil.getFluidHandler(stack).isPresent();
    }

    public static boolean isEmptyFluidContainerItem(ItemStack stack) {
        Optional<IFluidHandlerItem> optionalHandler = FluidUtil.getFluidHandler(stack);
        if (optionalHandler.isEmpty()) {
            return false;
        }

        IFluidHandlerItem fluidHandlerItem = optionalHandler.get();
        for (int tankIndex = 0; tankIndex < fluidHandlerItem.getTanks(); tankIndex++) {
            if (fluidHandlerItem.getFluidInTank(tankIndex).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public long getStoredCobblestonePower() {
        return this.storedCobblestonePower;
    }

    public long getMaxCobblestonePower() {
        return MAX_COBBLESTONE_POWER;
    }

    public long getStoredFluidAmount() {
        return this.storedFluidAmount;
    }

    public long getMaxFluidAmount() {
        return MAX_FLUID_AMOUNT;
    }

    public long getLastConvertedFluidAmount() {
        return this.lastConvertedFluidAmount;
    }

    public long getCurrentCobblestonePowerConsumption() {
        if (!this.isAvailable) {
            return 0L;
        }

        long neededFluid = MAX_FLUID_AMOUNT - this.storedFluidAmount;
        if (neededFluid <= 0L || this.storedCobblestonePower <= 0L) {
            return 0L;
        }

        return Math.min(this.storedCobblestonePower, neededFluid);
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
    }

    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public FluidStack getDisplayedFluid() {
        if (this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        return this.storedFluid.copyWithAmount((int) Math.min(this.storedFluidAmount, Integer.MAX_VALUE));
    }

    private int getDisplayedFluidId() {
        if (this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return -1;
        }

        return BuiltInRegistries.FLUID.getId(this.storedFluid.getFluid());
    }

    public void reverseIsAvailable() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        this.isAvailable = !this.isAvailable;
        this.setChanged();
    }

    public IItemHandler getAutomationItemHandler(@Nullable Direction side) {
        if (side == null) {
            return this.automationAccessHandler;
        }

        AutomationMode automationMode = this.getAutomationMode(AutomationSide.fromWorldSide(side, this.getBlockState()));
        if (automationMode == AutomationMode.COBBLESTONE_INPUT) {
            return this.cobblestoneInputAutomationHandler;
        }

        if (automationMode == AutomationMode.INPUT) {
            return this.bucketInputAutomationHandler;
        }

        if (automationMode == AutomationMode.OUTPUT) {
            return this.bucketOutputAutomationHandler;
        }

        if (automationMode == AutomationMode.IN_OUT) {
            return this.bucketCombinedAutomationHandler;
        }

        return EmptyItemHandler.INSTANCE;
    }

    @Nullable
    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        if (side == null) {
            return this.internalFluidHandler;
        }

        AutomationMode automationMode = this.getFluidAutomationMode(AutomationSide.fromWorldSide(side, this.getBlockState()));
        if (automationMode == AutomationMode.OUTPUT || automationMode == AutomationMode.IN_OUT) {
            return this.outputFluidHandler;
        }

        return null;
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

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        Level currentLevel = this.level;
        BlockState currentState = this.getBlockState();

        this.clampStoredCobblestonePower();
        this.pullInputsFromConfiguredSides();
        this.tryAbsorbCobblestonePower();
        this.lastConvertedFluidAmount = 0L;

        long convertedFluid = 0L;
        if (this.isAvailable) {
            convertedFluid = this.convertCobblestonePowerToWater();
        }
        this.lastConvertedFluidAmount = convertedFluid;

        boolean shouldTurnOn = convertedFluid > 0L;
        BlockState updatedState = currentState.setValue(OnOffBlock.ON, shouldTurnOn);
        if (updatedState != currentState) {
            currentLevel.setBlock(this.worldPosition, updatedState, 3);
        }

        this.processFluidContainerItem();
        this.autoExportFluid();
    }

    private void tryAbsorbCobblestonePower() {
        ItemStack powerStack = this.itemStackHandler.getStackInSlot(POWER_SLOT_INDEX);
        long updatedPower = CobblestonePowerHelper.absorbPowerFromSlot(
            powerStack,
            this.storedCobblestonePower,
            this.getMaxCobblestonePower()
        );
        if (updatedPower == this.storedCobblestonePower) {
            return;
        }

        this.storedCobblestonePower = updatedPower;
        this.setChanged();
    }

    private long convertCobblestonePowerToWater() {
        long neededFluid = MAX_FLUID_AMOUNT - this.storedFluidAmount;
        if (neededFluid <= 0L || this.storedCobblestonePower <= 0L) {
            return 0L;
        }

        long toConvert = Math.min(this.storedCobblestonePower, neededFluid);
        this.storedCobblestonePower -= toConvert;
        if (this.storedFluid.isEmpty()) {
            this.storedFluid = new FluidStack(Fluids.WATER, 1);
        }

        this.storedFluidAmount += toConvert;
        this.syncToClient();
        return toConvert;
    }

    private void clampStoredCobblestonePower() {
        if (this.storedCobblestonePower > MAX_COBBLESTONE_POWER) {
            this.storedCobblestonePower = MAX_COBBLESTONE_POWER;
            this.setChanged();
        }
    }

    private void processFluidContainerItem() {
        ItemStack bucketStack = this.itemStackHandler.getStackInSlot(BUCKET_SLOT_INDEX);
        if (bucketStack.isEmpty()) {
            return;
        }

        ItemStack singleContainer = bucketStack.copy();
        singleContainer.setCount(1);

        Optional<IFluidHandlerItem> optionalHandler = FluidUtil.getFluidHandler(singleContainer);
        if (optionalHandler.isEmpty()) {
            return;
        }

        this.tryFillContainerFromTank(bucketStack, optionalHandler.get(), true);
    }

    private boolean tryProcessPlayerFluidContainer(Player player, ItemStack sourceStack, boolean carriedStack, boolean processAll) {
        if (sourceStack.isEmpty() || !isFluidContainerItem(sourceStack)) {
            return false;
        }

        ItemStack singleContainer = sourceStack.copy();
        singleContainer.setCount(1);

        Optional<IFluidHandlerItem> optionalHandler = FluidUtil.getFluidHandler(singleContainer);
        if (optionalHandler.isEmpty()) {
            return false;
        }

        ItemStack processedContainer = this.tryFillPlayerContainer(optionalHandler.get(), processAll);
        if (processedContainer.isEmpty()) {
            return false;
        }

        this.replaceProcessedPlayerContainer(player, sourceStack, processedContainer, carriedStack);
        return true;
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

    private boolean tryFillContainerFromTank(ItemStack originalInputStack, IFluidHandlerItem fluidHandlerItem, boolean processAll) {
        ItemStack processedContainerPreview = this.previewProcessedContainerAfterFilling(fluidHandlerItem, processAll);
        if (processedContainerPreview.isEmpty() || !this.canMoveProcessedContainer(processedContainerPreview)) {
            return false;
        }

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

        this.finishContainerProcessing(originalInputStack, fluidHandlerItem.getContainer().copy());
        return true;
    }

    private ItemStack previewProcessedContainerAfterFilling(IFluidHandlerItem fluidHandlerItem, boolean processAll) {
        Optional<IFluidHandlerItem> optionalPreviewHandler = FluidUtil.getFluidHandler(fluidHandlerItem.getContainer().copy());
        if (optionalPreviewHandler.isEmpty()) {
            return ItemStack.EMPTY;
        }

        IFluidHandlerItem previewHandler = optionalPreviewHandler.get();
        int transferredAmount = 0;
        while (true) {
            int stepTransferredAmount = this.simulateFillFluidHandlerFromTank(previewHandler);
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

        return previewHandler.getContainer().copy();
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

    private int simulateFillFluidHandlerFromTank(IFluidHandlerItem fluidHandlerItem) {
        if (this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return 0;
        }

        FluidStack availableFluid = this.storedFluid.copyWithAmount((int) Math.min(this.storedFluidAmount, Integer.MAX_VALUE));
        return fluidHandlerItem.fill(availableFluid, IFluidHandler.FluidAction.EXECUTE);
    }

    private boolean canMoveProcessedContainer(ItemStack processedContainer) {
        if (processedContainer.isEmpty()) {
            return false;
        }

        ItemStack bucketStack = this.itemStackHandler.getStackInSlot(BUCKET_SLOT_INDEX);
        if (bucketStack.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameComponents(bucketStack, processedContainer)) {
            return false;
        }

        return bucketStack.getCount() + processedContainer.getCount() <= bucketStack.getMaxStackSize();
    }

    private void finishContainerProcessing(ItemStack originalInputStack, ItemStack processedContainer) {
        originalInputStack.shrink(1);

        ItemStack bucketStack = this.itemStackHandler.getStackInSlot(BUCKET_SLOT_INDEX);
        if (bucketStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(BUCKET_SLOT_INDEX, processedContainer);
        } else {
            bucketStack.grow(processedContainer.getCount());
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
        return 0;
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

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("storedCobblestonePower", this.storedCobblestonePower);
        tag.putLong("storedFluidAmount", this.storedFluidAmount);
        tag.putBoolean("isAvailable", this.isAvailable);
        this.saveAutomationModes(tag);
        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
        if (!this.storedFluid.isEmpty()) {
            tag.put("storedFluid", this.storedFluid.save(registries));
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.storedCobblestonePower = tag.getLong("storedCobblestonePower");
        this.storedFluidAmount = tag.getLong("storedFluidAmount");
        this.isAvailable = tag.getBoolean("isAvailable");
        this.loadAutomationModes(tag);
        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        }

        if (tag.contains("storedFluid", Tag.TAG_COMPOUND)) {
            this.storedFluid = FluidStack.parseOptional(registries, tag.getCompound("storedFluid"));
            if (!this.storedFluid.isEmpty()) {
                this.storedFluid = this.storedFluid.copyWithAmount(1);
            }
        } else {
            this.storedFluid = FluidStack.EMPTY;
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_water_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData generatorData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_STORED_POWER) {
                    return LongDataHelper.lowerInt(CobblestoneWaterGeneratorBlockEntity.this.storedCobblestonePower);
                }

                if (index == DATA_INDEX_STORED_POWER_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneWaterGeneratorBlockEntity.this.storedCobblestonePower);
                }

                if (index == DATA_INDEX_MAX_STORED_POWER) {
                    return LongDataHelper.lowerInt(MAX_COBBLESTONE_POWER);
                }

                if (index == DATA_INDEX_MAX_STORED_POWER_UPPER) {
                    return LongDataHelper.upperInt(MAX_COBBLESTONE_POWER);
                }

                if (index == DATA_INDEX_STORED_FLUID) {
                    return LongDataHelper.lowerInt(CobblestoneWaterGeneratorBlockEntity.this.storedFluidAmount);
                }

                if (index == DATA_INDEX_STORED_FLUID_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneWaterGeneratorBlockEntity.this.storedFluidAmount);
                }

                if (index == DATA_INDEX_MAX_FLUID) {
                    return LongDataHelper.lowerInt(MAX_FLUID_AMOUNT);
                }

                if (index == DATA_INDEX_MAX_FLUID_UPPER) {
                    return LongDataHelper.upperInt(MAX_FLUID_AMOUNT);
                }

                if (index == DATA_INDEX_FLUID_ID) {
                    return CobblestoneWaterGeneratorBlockEntity.this.getDisplayedFluidId();
                }

                if (index >= DATA_INDEX_ITEM_AUTOMATION_START && index < DATA_INDEX_FLUID_AUTOMATION_START) {
                    return CobblestoneWaterGeneratorBlockEntity.this.getAutomationModeId(index - DATA_INDEX_ITEM_AUTOMATION_START);
                }

                if (index >= DATA_INDEX_FLUID_AUTOMATION_START && index < DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneWaterGeneratorBlockEntity.this.getFluidAutomationModeId(index - DATA_INDEX_FLUID_AUTOMATION_START);
                }

                if (index == DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneWaterGeneratorBlockEntity.this.isAutoExportEnabled() ? 1 : 0;
                }

                if (index == DATA_INDEX_AUTO_INSERT) {
                    return CobblestoneWaterGeneratorBlockEntity.this.getAutoInsertEnabledId();
                }

                if (index == DATA_INDEX_SOUND_MUTED) {
                    return CobblestoneWaterGeneratorBlockEntity.this.getSoundMutedId();
                }

                if (index == DATA_INDEX_CONVERTED_FLUID) {
                    return LongDataHelper.lowerInt(CobblestoneWaterGeneratorBlockEntity.this.lastConvertedFluidAmount);
                }

                if (index == DATA_INDEX_CONVERTED_FLUID_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneWaterGeneratorBlockEntity.this.lastConvertedFluidAmount);
                }

                if (index == DATA_INDEX_CURRENT_POWER_RATE) {
                    return LongDataHelper.lowerInt(CobblestoneWaterGeneratorBlockEntity.this.getCurrentCobblestonePowerConsumption());
                }

                if (index == DATA_INDEX_CURRENT_POWER_RATE_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneWaterGeneratorBlockEntity.this.getCurrentCobblestonePowerConsumption());
                }

                return 0;
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return DATA_INDEX_CURRENT_POWER_RATE_UPPER + 1;
            }
        };

        return new CobblestoneWaterGeneratorMenu(containerId, playerInventory, this, generatorData);
    }

    private class WaterGeneratorFluidHandler implements IFluidHandler {
        private final boolean canFill;
        private final boolean canDrain;

        private WaterGeneratorFluidHandler(boolean canFill, boolean canDrain) {
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

            return CobblestoneWaterGeneratorBlockEntity.this.getDisplayedFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            if (tank != 0) {
                return 0;
            }

            return (int) Math.min(MAX_FLUID_AMOUNT, Integer.MAX_VALUE);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0 && stack.getFluid().isSame(Fluids.WATER);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (!this.canDrain || resource.isEmpty() || CobblestoneWaterGeneratorBlockEntity.this.storedFluid.isEmpty()) {
                return FluidStack.EMPTY;
            }

            if (!FluidStack.isSameFluidSameComponents(CobblestoneWaterGeneratorBlockEntity.this.storedFluid, resource)) {
                return FluidStack.EMPTY;
            }

            return CobblestoneWaterGeneratorBlockEntity.this.drainInternal(resource.getAmount(), action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            if (!this.canDrain) {
                return FluidStack.EMPTY;
            }

            return CobblestoneWaterGeneratorBlockEntity.this.drainInternal(maxDrain, action);
        }
    }
}
