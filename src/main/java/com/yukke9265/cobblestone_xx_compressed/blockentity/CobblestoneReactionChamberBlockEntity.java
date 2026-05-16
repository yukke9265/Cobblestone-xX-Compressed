package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneReactionChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneReactionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.ReactionChamberRecipeInput;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

public class CobblestoneReactionChamberBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int INPUT_SLOT_1_INDEX = 0;
    public static final int INPUT_SLOT_2_INDEX = 1;
    public static final int POWER_SLOT_INDEX = 2;
    public static final int OUTPUT_SLOT_INDEX = 3;
    public static final int ACCELERATION_SLOT_INDEX = 4;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 5;
    public static final long MAX_COBBLESTONE_POWER = CobblestoneCrusherBlockEntity.MAX_COBBLESTONE_POWER;
    public static final long MAX_FLUID_AMOUNT = 16_000L;

    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_POWER_UPPER = 3;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    private static final int DATA_INDEX_STORED_FLUID = 6;
    private static final int DATA_INDEX_STORED_FLUID_UPPER = 7;
    private static final int DATA_INDEX_MAX_FLUID = 8;
    private static final int DATA_INDEX_MAX_FLUID_UPPER = 9;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 10;
    private static final int DATA_INDEX_FLUID_AUTOMATION_START = DATA_INDEX_ITEM_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_FLUID_AUTOMATION_START + AUTOMATION_FACE_COUNT;

    private int progress;
    private int maxProgress;
    private long storedCobblestonePower;
    private long storedFluidAmount;
    private FluidStack storedFluid = FluidStack.EMPTY;
    private boolean isAvailable = true;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(6) {
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
            CobblestoneReactionChamberBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler inputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot == 0) {
                return CobblestoneReactionChamberBlockEntity.this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX);
            }
            if (slot == 1) {
                return CobblestoneReactionChamberBlockEntity.this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0 && slot != 1) {
                return stack;
            }

            ItemStack remainingStack = CobblestoneReactionChamberBlockEntity.this.itemStackHandler.insertItem(INPUT_SLOT_1_INDEX, stack, simulate);
            if (!remainingStack.isEmpty()) {
                remainingStack = CobblestoneReactionChamberBlockEntity.this.itemStackHandler.insertItem(INPUT_SLOT_2_INDEX, remainingStack, simulate);
            }
            return remainingStack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 || slot == 1 ? CobblestoneReactionChamberBlockEntity.this.itemStackHandler.getSlotLimit(INPUT_SLOT_1_INDEX) : 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == 0 || slot == 1;
        }
    };

    private final IItemHandler inputSlot1AutomationHandler = new SingleSlotAutomationHandler(INPUT_SLOT_1_INDEX, true, false);
    private final IItemHandler inputSlot2AutomationHandler = new SingleSlotAutomationHandler(INPUT_SLOT_2_INDEX, true, false);
    private final IItemHandler cobblestoneInputAutomationHandler = new SingleSlotAutomationHandler(POWER_SLOT_INDEX, true, false);
    private final IItemHandler outputAutomationHandler = new SingleSlotAutomationHandler(OUTPUT_SLOT_INDEX, false, true);

    private final IItemHandler automationAccessHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 6;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot < 0 || slot >= 6) {
                return ItemStack.EMPTY;
            }
            return CobblestoneReactionChamberBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == INPUT_SLOT_1_INDEX || slot == INPUT_SLOT_2_INDEX || slot == POWER_SLOT_INDEX) {
                return CobblestoneReactionChamberBlockEntity.this.itemStackHandler.insertItem(slot, stack, simulate);
            }
            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != OUTPUT_SLOT_INDEX) {
                return ItemStack.EMPTY;
            }
            return CobblestoneReactionChamberBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 0 || slot >= 6) {
                return 0;
            }
            return CobblestoneReactionChamberBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot < 0 || slot >= 6) {
                return false;
            }
            return CobblestoneReactionChamberBlockEntity.this.itemStackHandler.isItemValid(slot, stack);
        }
    };

    private final IFluidHandler internalFluidHandler = new ReactionChamberFluidHandler(true, true);
    private final IFluidHandler inputFluidHandler = new ReactionChamberFluidHandler(true, false);
    private final IFluidHandler outputFluidHandler = new ReactionChamberFluidHandler(false, true);

    public CobblestoneReactionChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_REACTION_CHAMBER_BLOCK_ENTITY.get(), pos, state);
        for (int index = 0; index < AUTOMATION_FACE_COUNT; index++) {
            this.setFluidAutomationMode(index, AutomationMode.DISABLED);
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public long getStoredCobblestonePower() {
        return this.storedCobblestonePower;
    }

    public long getMaxCobblestonePower() {
        return MAX_COBBLESTONE_POWER * this.getEnergizedCubeMultiplier();
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
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

    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
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

        AutomationSide automationSide = AutomationSide.fromWorldSide(side, this.getBlockState());
        AutomationMode automationMode = this.getAutomationMode(automationSide);
        if (automationMode == AutomationMode.INPUT) {
            return this.inputAutomationHandler;
        }
        if (automationMode == AutomationMode.INPUT_1) {
            return this.inputSlot1AutomationHandler;
        }
        if (automationMode == AutomationMode.INPUT_2) {
            return this.inputSlot2AutomationHandler;
        }
        if (automationMode == AutomationMode.COBBLESTONE_INPUT) {
            return this.cobblestoneInputAutomationHandler;
        }
        if (automationMode == AutomationMode.OUTPUT) {
            return this.outputAutomationHandler;
        }
        if (automationMode == AutomationMode.IN_OUT) {
            return this.automationAccessHandler;
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
        boolean shouldTurnOn = false;

        this.clampStoredCobblestonePower();
        this.tryAbsorbCobblestonePower();

        Optional<RecipeHolder<CobblestoneReactionChamberRecipe>> recipeHolder = this.getCurrentRecipe();
        if (this.isAvailable && recipeHolder.isPresent()) {
            CobblestoneReactionChamberRecipe recipe = recipeHolder.get().value();
            if (this.maxProgress != recipe.getProcessingTime()) {
                this.maxProgress = recipe.getProcessingTime();
                this.setChanged();
            }

            if (this.canProcess(recipe)) {
                int progressStep = this.getProgressStep(recipe.getCobblestonePowerPerTick());
                this.progress += progressStep;
                this.storedCobblestonePower -= (long) recipe.getCobblestonePowerPerTick() * progressStep;
                shouldTurnOn = true;
                this.setChanged();

                if (this.progress >= this.maxProgress) {
                    this.craft(recipe);
                    this.progress = 0;
                    this.setChanged();
                }
            } else if (this.progress != 0) {
                this.progress = 0;
                this.setChanged();
            }
        } else {
            if (this.progress != 0) {
                this.progress = 0;
                this.setChanged();
            }

            if (this.maxProgress != 0) {
                this.maxProgress = 0;
                this.setChanged();
            }
        }

        BlockState updatedState = currentState.setValue(OnOffBlock.ON, shouldTurnOn);
        if (updatedState != currentState) {
            currentLevel.setBlock(this.worldPosition, updatedState, 3);
        }

        this.pushOutputToConfiguredSides();
        this.autoExportFluid();
    }

    private void pushOutputToConfiguredSides() {
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            return;
        }

        ItemStack remainingOutput = this.pushItemStackToConfiguredSides(outputStack.copy(), AutomationMode.OUTPUT, AutomationMode.IN_OUT);
        this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, remainingOutput);
    }

    private void autoExportFluid() {
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

    private void tryAbsorbCobblestonePower() {
        ItemStack powerStack = this.itemStackHandler.getStackInSlot(POWER_SLOT_INDEX);
        long convertedPower = CobblestoneCrusherBlockEntity.getCobblestonePowerValueForAutomation(powerStack);
        if (convertedPower <= 0 || this.storedCobblestonePower + convertedPower > this.getMaxCobblestonePower()) {
            return;
        }

        powerStack.shrink(1);
        this.storedCobblestonePower += convertedPower;
        this.setChanged();
    }

    private Optional<RecipeHolder<CobblestoneReactionChamberRecipe>> getCurrentRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack firstInputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX);
        ItemStack secondInputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX);
        if (firstInputStack.isEmpty() || secondInputStack.isEmpty() || this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return Optional.empty();
        }

        ReactionChamberRecipeInput input = new ReactionChamberRecipeInput(firstInputStack, secondInputStack, this.getDisplayedFluid());
        return currentLevel.getRecipeManager().getRecipeFor(ModRecipeTypes.COBBLESTONE_REACTION_CHAMBER.get(), input, currentLevel);
    }

    private boolean canProcess(CobblestoneReactionChamberRecipe recipe) {
        if (this.getProgressStep(recipe.getCobblestonePowerPerTick()) <= 0) {
            return false;
        }
        if (!this.canOutput(recipe)) {
            return false;
        }
        return this.storedFluidAmount >= recipe.getFluidInput().getAmount();
    }

    private boolean canOutput(CobblestoneReactionChamberRecipe recipe) {
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

    private void craft(CobblestoneReactionChamberRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX).shrink(1);
        this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX).shrink(1);
        this.drainInternal(recipe.getFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);

        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, resultStack.copy());
        } else {
            outputStack.grow(resultStack.getCount());
        }
    }

    private int getProgressStep(int cobblestonePowerPerTick) {
        if (cobblestonePowerPerTick <= 0) {
            return 0;
        }

        int accelerationMultiplier = this.getAccelerationMultiplier();
        int remainingProgress = this.maxProgress - this.progress;
        if (remainingProgress <= 0) {
            return 0;
        }

        int maxProgressStep = Math.min(accelerationMultiplier, remainingProgress);
        long maxPowerStep = this.storedCobblestonePower / cobblestonePowerPerTick;
        return Math.min(maxProgressStep, (int) Math.min(Integer.MAX_VALUE, maxPowerStep));
    }

    private int getAccelerationMultiplier() {
        ItemStack accelerationStack = this.itemStackHandler.getStackInSlot(ACCELERATION_SLOT_INDEX);
        int multiplier = MachineUpgradeHelper.getAccelerationMultiplier(accelerationStack);
        return Math.max(1, multiplier);
    }

    private int getEnergizedCubeMultiplier() {
        ItemStack energizedCubeStack = this.itemStackHandler.getStackInSlot(ENERGIZED_CUBE_SLOT_INDEX);
        int multiplier = MachineUpgradeHelper.getEnergizedCubeMultiplier(energizedCubeStack);
        return Math.max(1, multiplier);
    }

    private void clampStoredCobblestonePower() {
        long maxCobblestonePower = this.getMaxCobblestonePower();
        if (this.storedCobblestonePower > maxCobblestonePower) {
            this.storedCobblestonePower = maxCobblestonePower;
            this.setChanged();
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
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", this.progress);
        tag.putInt("maxProgress", this.maxProgress);
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
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("maxProgress");
        this.storedCobblestonePower = tag.getLong("storedCobblestonePower");
        this.storedFluidAmount = tag.getLong("storedFluidAmount");
        this.isAvailable = tag.getBoolean("isAvailable");
        this.loadAutomationModes(tag);
        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        }

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
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_reaction_chamber");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData reactionChamberData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_PROGRESS) {
                    return progress;
                }
                if (index == DATA_INDEX_MAX_PROGRESS) {
                    return maxProgress;
                }
                if (index == DATA_INDEX_STORED_POWER) {
                    return LongDataHelper.lowerInt(storedCobblestonePower);
                }
                if (index == DATA_INDEX_STORED_POWER_UPPER) {
                    return LongDataHelper.upperInt(storedCobblestonePower);
                }
                if (index == DATA_INDEX_MAX_STORED_POWER) {
                    return LongDataHelper.lowerInt(getMaxCobblestonePower());
                }
                if (index == DATA_INDEX_MAX_STORED_POWER_UPPER) {
                    return LongDataHelper.upperInt(getMaxCobblestonePower());
                }
                if (index == DATA_INDEX_STORED_FLUID) {
                    return LongDataHelper.lowerInt(storedFluidAmount);
                }
                if (index == DATA_INDEX_STORED_FLUID_UPPER) {
                    return LongDataHelper.upperInt(storedFluidAmount);
                }
                if (index == DATA_INDEX_MAX_FLUID) {
                    return LongDataHelper.lowerInt(MAX_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_FLUID_UPPER) {
                    return LongDataHelper.upperInt(MAX_FLUID_AMOUNT);
                }

                if (index >= DATA_INDEX_ITEM_AUTOMATION_START && index < DATA_INDEX_FLUID_AUTOMATION_START) {
                    return CobblestoneReactionChamberBlockEntity.this.getAutomationModeId(index - DATA_INDEX_ITEM_AUTOMATION_START);
                }

                if (index >= DATA_INDEX_FLUID_AUTOMATION_START && index < DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneReactionChamberBlockEntity.this.getFluidAutomationModeId(index - DATA_INDEX_FLUID_AUTOMATION_START);
                }

                if (index == DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneReactionChamberBlockEntity.this.getAutoExportEnabledId();
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

        return new CobblestoneReactionChamberMenu(containerId, playerInventory, this, reactionChamberData);
    }

    private class SingleSlotAutomationHandler implements IItemHandler {
        private final int slotIndex;
        private final boolean canInsert;
        private final boolean canExtract;

        private SingleSlotAutomationHandler(int slotIndex, boolean canInsert, boolean canExtract) {
            this.slotIndex = slotIndex;
            this.canInsert = canInsert;
            this.canExtract = canExtract;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            return slot == 0 ? CobblestoneReactionChamberBlockEntity.this.itemStackHandler.getStackInSlot(this.slotIndex) : ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!this.canInsert || slot != 0) {
                return stack;
            }
            return CobblestoneReactionChamberBlockEntity.this.itemStackHandler.insertItem(this.slotIndex, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!this.canExtract || slot != 0) {
                return ItemStack.EMPTY;
            }
            return CobblestoneReactionChamberBlockEntity.this.itemStackHandler.extractItem(this.slotIndex, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? CobblestoneReactionChamberBlockEntity.this.itemStackHandler.getSlotLimit(this.slotIndex) : 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return this.canInsert && slot == 0 && CobblestoneReactionChamberBlockEntity.this.itemStackHandler.isItemValid(this.slotIndex, stack);
        }
    }

    private class ReactionChamberFluidHandler implements IFluidHandler {
        private final boolean canFill;
        private final boolean canDrain;

        private ReactionChamberFluidHandler(boolean canFill, boolean canDrain) {
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
            return CobblestoneReactionChamberBlockEntity.this.getDisplayedFluid();
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
            return tank == 0 && (CobblestoneReactionChamberBlockEntity.this.storedFluid.isEmpty()
                || FluidStack.isSameFluidSameComponents(CobblestoneReactionChamberBlockEntity.this.storedFluid, stack));
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!this.canFill) {
                return 0;
            }
            return CobblestoneReactionChamberBlockEntity.this.fillInternal(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (!this.canDrain || resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, CobblestoneReactionChamberBlockEntity.this.storedFluid)) {
                return FluidStack.EMPTY;
            }
            return CobblestoneReactionChamberBlockEntity.this.drainInternal(resource.getAmount(), action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            if (!this.canDrain) {
                return FluidStack.EMPTY;
            }
            return CobblestoneReactionChamberBlockEntity.this.drainInternal(maxDrain, action);
        }
    }
}