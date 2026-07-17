package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCrystallizationChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrystallizationChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CrystallizationChamberRecipeInput;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

/**
 * fluid を item に変換する powered machine です。
 *
 * CP、progress、保存、同期、停止処理は PoweredMachineBlockEntityBase に統一し、
 * 流体タンク、fluid capability、container 操作だけをこのクラスに残します。
 */
@SuppressWarnings("null")
public class CobblestoneCrystallizationChamberBlockEntity extends PoweredMachineBlockEntityBase<CobblestoneCrystallizationChamberRecipe> implements MenuProvider {
    public static final int POWER_SLOT_INDEX = 0;
    public static final int OUTPUT_SLOT_INDEX = 1;
    public static final int ACCELERATION_SLOT_INDEX = 2;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 3;
    public static final long MAX_COBBLESTONE_POWER = 262144000L;
    public static final long MAX_FLUID_AMOUNT = 64_000L;

    private static final int MACHINE_SPECIFIC_DATA_COUNT = 5;
    private static final int DATA_INDEX_STORED_FLUID = 6;
    private static final int DATA_INDEX_STORED_FLUID_UPPER = 7;
    private static final int DATA_INDEX_MAX_FLUID = 8;
    private static final int DATA_INDEX_MAX_FLUID_UPPER = 9;
    private static final int DATA_INDEX_FLUID_ID = 10;
    private long storedFluidAmount;
    private FluidStack storedFluid = FluidStack.EMPTY;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == OUTPUT_SLOT_INDEX) {
                return false;
            }
            if (slot == POWER_SLOT_INDEX) {
                return CobblestoneCrusherBlockEntity.isCobblestonePowerItem(stack);
            }
            if (slot == ACCELERATION_SLOT_INDEX) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }
            if (slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return MachineUpgradeHelper.isEnergizedCube(stack);
            }
            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneCrystallizationChamberBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return 1;
            }
            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler cobblestoneInputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        POWER_SLOT_INDEX
    );
    private final IItemHandler outputAutomationHandler = AutomationItemHandlerHelper.createExtractOnlyHandler(
        this.itemStackHandler,
        OUTPUT_SLOT_INDEX
    );
    private final IItemHandler automationAccessHandler = AutomationItemHandlerHelper.createRestrictedAccessHandler(
        this.itemStackHandler,
        new int[0],
        new int[] {OUTPUT_SLOT_INDEX}
    );

    private final IFluidHandler internalFluidHandler = new CrystallizationChamberFluidHandler(true, true);
    private final IFluidHandler inputFluidHandler = new CrystallizationChamberFluidHandler(true, false);
    private final IFluidHandler outputFluidHandler = new CrystallizationChamberFluidHandler(false, true);

    public CobblestoneCrystallizationChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_CRYSTALLIZATION_CHAMBER_BLOCK_ENTITY.get(), pos, state);
        for (int index = 0; index < AUTOMATION_FACE_COUNT; index++) {
            this.setAutomationMode(index, AutomationMode.DISABLED);
            this.setFluidAutomationMode(index, AutomationMode.DISABLED);
        }
    }

    public long getStoredFluidAmount() {
        return this.storedFluidAmount;
    }

    public long getMaxFluidAmount() {
        return MAX_FLUID_AMOUNT;
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

    @Override
    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public IItemHandler getAutomationItemHandler(@Nullable Direction side) {
        if (side == null) {
            return this.automationAccessHandler;
        }

        return this.getConfiguredAutomationItemHandler(
            side,
            EmptyItemHandler.INSTANCE,
            this.cobblestoneInputAutomationHandler,
            this.outputAutomationHandler,
            this.automationAccessHandler
        );
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
    protected void onAutoExportFluid() {
        if (!this.isAutoExportEnabled() || this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return;
        }

        FluidStack exportStack = this.storedFluid.copyWithAmount((int) Math.min(this.storedFluidAmount, Integer.MAX_VALUE));
        int originalAmount = exportStack.getAmount();
        FluidStack remainingFluid = this.pushFluidToConfiguredSides(exportStack, AutomationMode.OUTPUT, AutomationMode.IN_OUT);
        int exportedAmount = originalAmount - remainingFluid.getAmount();
        if (exportedAmount > 0) {
            this.drainInternal(exportedAmount, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    protected long getBaseMaxCobblestonePower() {
        return MAX_COBBLESTONE_POWER;
    }

    @Override
    protected int getPowerSlotIndex() {
        return POWER_SLOT_INDEX;
    }

    @Override
    protected int getOutputSlotIndex() {
        return OUTPUT_SLOT_INDEX;
    }

    @Override
    protected Optional<CobblestoneCrystallizationChamberRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null || this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return Optional.empty();
        }

        CrystallizationChamberRecipeInput input = new CrystallizationChamberRecipeInput(this.getDisplayedFluid());
        Optional<RecipeHolder<CobblestoneCrystallizationChamberRecipe>> recipeHolder = currentLevel.getRecipeManager().getRecipeFor(
            ModRecipeTypes.COBBLESTONE_CRYSTALLIZATION_CHAMBER.get(),
            input,
            currentLevel
        );
        return recipeHolder.map(RecipeHolder::value);
    }

    @Override
    protected boolean canProcessRecipe(CobblestoneCrystallizationChamberRecipe recipe) {
        return this.canOutput(recipe) && this.storedFluidAmount >= recipe.getFluidInput().getAmount();
    }

    @Override
    protected boolean shouldResetProgress(CobblestoneCrystallizationChamberRecipe recipe) {
        return !this.canProcessRecipe(recipe);
    }

    @Override
    protected int getRecipeProcessingTime(CobblestoneCrystallizationChamberRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestoneCrystallizationChamberRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    private boolean canOutput(CobblestoneCrystallizationChamberRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return false;
        }

        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
        if (resultStack.isEmpty()) {
            return false;
        }

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
            return false;
        }
        return outputStack.getCount() + resultStack.getCount() <= outputStack.getMaxStackSize();
    }

    @Override
    protected void finishProcessing(CobblestoneCrystallizationChamberRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        this.drainInternal(recipe.getFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);

        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, resultStack.copy());
        } else {
            outputStack.grow(resultStack.getCount());
        }
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

        return transferredAmount > 0 ? fluidHandlerItem.getContainer().copy() : ItemStack.EMPTY;
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

        return transferredAmount > 0 ? fluidHandlerItem.getContainer().copy() : ItemStack.EMPTY;
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

    private int fillInternal(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        if (!this.storedFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(this.storedFluid, resource)) {
            return 0;
        }

        long remainingCapacity = MAX_FLUID_AMOUNT - this.storedFluidAmount;
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

    @Override
    protected void saveAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong("storedFluidAmount", this.storedFluidAmount);
        if (!this.storedFluid.isEmpty()) {
            tag.put("storedFluid", this.storedFluid.save(registries));
        }
    }

    @Override
    protected void loadAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_crystallization_chamber");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData crystallizationChamberData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_STORED_FLUID) {
                    return LongDataHelper.lowerInt(CobblestoneCrystallizationChamberBlockEntity.this.storedFluidAmount);
                }
                if (index == DATA_INDEX_STORED_FLUID_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneCrystallizationChamberBlockEntity.this.storedFluidAmount);
                }
                if (index == DATA_INDEX_MAX_FLUID) {
                    return LongDataHelper.lowerInt(MAX_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_FLUID_UPPER) {
                    return LongDataHelper.upperInt(MAX_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_FLUID_ID) {
                    return CobblestoneCrystallizationChamberBlockEntity.this.getDisplayedFluidId();
                }
                return CobblestoneCrystallizationChamberBlockEntity.this.getPoweredMachineCommonData(
                    index,
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }

            @Override
            public void set(int index, int value) {
                CobblestoneCrystallizationChamberBlockEntity.this.setPoweredMachineCommonData(
                    index,
                    value,
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }

            @Override
            public int getCount() {
                return CobblestoneCrystallizationChamberBlockEntity.this.getPoweredMachineDataCount(
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }
        };

        return new CobblestoneCrystallizationChamberMenu(containerId, playerInventory, this, crystallizationChamberData);
    }

    private class CrystallizationChamberFluidHandler implements IFluidHandler {
        private final boolean canFill;
        private final boolean canDrain;

        private CrystallizationChamberFluidHandler(boolean canFill, boolean canDrain) {
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
            return CobblestoneCrystallizationChamberBlockEntity.this.getDisplayedFluid();
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
            return tank == 0 && (CobblestoneCrystallizationChamberBlockEntity.this.storedFluid.isEmpty()
                || FluidStack.isSameFluidSameComponents(CobblestoneCrystallizationChamberBlockEntity.this.storedFluid, stack));
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!this.canFill) {
                return 0;
            }
            return CobblestoneCrystallizationChamberBlockEntity.this.fillInternal(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (!this.canDrain || resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, CobblestoneCrystallizationChamberBlockEntity.this.storedFluid)) {
                return FluidStack.EMPTY;
            }
            return CobblestoneCrystallizationChamberBlockEntity.this.drainInternal(resource.getAmount(), action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            if (!this.canDrain) {
                return FluidStack.EMPTY;
            }
            return CobblestoneCrystallizationChamberBlockEntity.this.drainInternal(maxDrain, action);
        }
    }
}
