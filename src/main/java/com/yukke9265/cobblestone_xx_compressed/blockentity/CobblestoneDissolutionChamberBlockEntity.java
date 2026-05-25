package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneDissolutionChamberMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneDissolutionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.DissolutionChamberRecipeInput;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
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

public class CobblestoneDissolutionChamberBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int POWER_SLOT_INDEX = 1;
    public static final int ACCELERATION_SLOT_INDEX = 2;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 3;
    public static final long MAX_COBBLESTONE_POWER = 16384000L;
    public static final long MAX_INPUT_FLUID_AMOUNT = 64_000L;
    public static final long MAX_OUTPUT_FLUID_AMOUNT = 64_000L;

    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_POWER_UPPER = 3;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    private static final int DATA_INDEX_INPUT_FLUID = 6;
    private static final int DATA_INDEX_INPUT_FLUID_UPPER = 7;
    private static final int DATA_INDEX_MAX_INPUT_FLUID = 8;
    private static final int DATA_INDEX_MAX_INPUT_FLUID_UPPER = 9;
    private static final int DATA_INDEX_INPUT_FLUID_ID = 10;
    private static final int DATA_INDEX_OUTPUT_FLUID = 11;
    private static final int DATA_INDEX_OUTPUT_FLUID_UPPER = 12;
    private static final int DATA_INDEX_MAX_OUTPUT_FLUID = 13;
    private static final int DATA_INDEX_MAX_OUTPUT_FLUID_UPPER = 14;
    private static final int DATA_INDEX_OUTPUT_FLUID_ID = 15;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 16;
    private static final int DATA_INDEX_FLUID_AUTOMATION_START = DATA_INDEX_ITEM_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_FLUID_AUTOMATION_START + AUTOMATION_FACE_COUNT;

    private int progress;
    private int maxProgress;
    private long storedCobblestonePower;
    private long storedInputFluidAmount;
    private long storedOutputFluidAmount;
    private FluidStack storedInputFluid = FluidStack.EMPTY;
    private FluidStack storedOutputFluid = FluidStack.EMPTY;
    private boolean isAvailable = true;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == POWER_SLOT_INDEX) {
                return CobblestoneCrusherBlockEntity.isCobblestonePowerItem(stack);
            }

            if (slot == ACCELERATION_SLOT_INDEX) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }

            if (slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return MachineUpgradeHelper.isEnergizedCube(stack);
            }

            return slot == INPUT_SLOT_INDEX;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneDissolutionChamberBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler inputAutomationHandler = new SingleSlotAutomationHandler(INPUT_SLOT_INDEX, true);
    private final IItemHandler cobblestoneInputAutomationHandler = new SingleSlotAutomationHandler(POWER_SLOT_INDEX, true);
    private final IItemHandler automationAccessHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 4;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot < 0 || slot >= 4) {
                return ItemStack.EMPTY;
            }

            return CobblestoneDissolutionChamberBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == INPUT_SLOT_INDEX) {
                return CobblestoneDissolutionChamberBlockEntity.this.itemStackHandler.insertItem(slot, stack, simulate);
            }

            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 0 || slot >= 4) {
                return 0;
            }

            return CobblestoneDissolutionChamberBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot < 0 || slot >= 4) {
                return false;
            }

            return CobblestoneDissolutionChamberBlockEntity.this.itemStackHandler.isItemValid(slot, stack);
        }
    };

    private final IFluidHandler combinedFluidHandler = new DissolutionChamberFluidHandler(true, true, true);
    private final IFluidHandler inputFluidHandler = new DissolutionChamberFluidHandler(true, false, false);
    private final IFluidHandler outputFluidHandler = new DissolutionChamberFluidHandler(false, true, false);

    public CobblestoneDissolutionChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_DISSOLUTION_CHAMBER_BLOCK_ENTITY.get(), pos, state);

        for (int index = 0; index < AUTOMATION_FACE_COUNT; index++) {
            this.setAutomationMode(index, AutomationMode.DISABLED);
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

    public long getStoredInputFluidAmount() {
        return this.storedInputFluidAmount;
    }

    public long getMaxInputFluidAmount() {
        return MAX_INPUT_FLUID_AMOUNT;
    }

    public long getStoredOutputFluidAmount() {
        return this.storedOutputFluidAmount;
    }

    public long getMaxOutputFluidAmount() {
        return MAX_OUTPUT_FLUID_AMOUNT;
    }

    public FluidStack getDisplayedInputFluid() {
        if (this.storedInputFluid.isEmpty() || this.storedInputFluidAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        return this.storedInputFluid.copyWithAmount((int) Math.min(this.storedInputFluidAmount, Integer.MAX_VALUE));
    }

    public FluidStack getDisplayedOutputFluid() {
        if (this.storedOutputFluid.isEmpty() || this.storedOutputFluidAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        return this.storedOutputFluid.copyWithAmount((int) Math.min(this.storedOutputFluidAmount, Integer.MAX_VALUE));
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
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

        AutomationMode automationMode = this.getAutomationMode(AutomationSide.fromWorldSide(side, this.getBlockState()));
        if (automationMode == AutomationMode.INPUT) {
            return this.inputAutomationHandler;
        }

        if (automationMode == AutomationMode.COBBLESTONE_INPUT) {
            return this.cobblestoneInputAutomationHandler;
        }

        if (automationMode == AutomationMode.IN_OUT) {
            return this.automationAccessHandler;
        }

        return EmptyItemHandler.INSTANCE;
    }

    @Nullable
    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        if (side == null) {
            return this.combinedFluidHandler;
        }

        AutomationMode automationMode = this.getFluidAutomationMode(AutomationSide.fromWorldSide(side, this.getBlockState()));
        if (automationMode == AutomationMode.INPUT) {
            return this.inputFluidHandler;
        }

        if (automationMode == AutomationMode.OUTPUT) {
            return this.outputFluidHandler;
        }

        if (automationMode == AutomationMode.IN_OUT) {
            return this.combinedFluidHandler;
        }

        return null;
    }

    public boolean handleInputFluidIndicatorClick(Player player, boolean processAll) {
        ItemStack carriedStack = player.containerMenu.getCarried();
        if (this.tryProcessPlayerFluidContainer(player, carriedStack, true, processAll, true)) {
            return true;
        }

        int selectedSlot = player.getInventory().selected;
        ItemStack selectedStack = player.getInventory().getItem(selectedSlot);
        return this.tryProcessPlayerFluidContainer(player, selectedStack, false, processAll, true);
    }

    public boolean handleOutputFluidIndicatorClick(Player player, boolean processAll) {
        ItemStack carriedStack = player.containerMenu.getCarried();
        if (this.tryProcessPlayerFluidContainer(player, carriedStack, true, processAll, false)) {
            return true;
        }

        int selectedSlot = player.getInventory().selected;
        ItemStack selectedStack = player.getInventory().getItem(selectedSlot);
        return this.tryProcessPlayerFluidContainer(player, selectedStack, false, processAll, false);
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

        Optional<RecipeHolder<CobblestoneDissolutionChamberRecipe>> recipeHolder = this.getCurrentRecipe();
        if (this.isAvailable && recipeHolder.isPresent()) {
            CobblestoneDissolutionChamberRecipe recipe = recipeHolder.get().value();
            if (this.maxProgress != recipe.getProcessingTime()) {
                this.maxProgress = recipe.getProcessingTime();
                this.setChanged();
            }

            if (this.canProcess(recipe)) {
                int progressStep = this.getProgressStep(recipe.getCobblestonePowerPerTick());
                this.progress += progressStep;
                this.storedCobblestonePower -= recipe.getCobblestonePowerPerTick() * progressStep;
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

        this.autoExportFluid();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.itemStackHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("maxProgress");
        this.storedCobblestonePower = tag.getLong("storedCobblestonePower");
        this.isAvailable = !tag.contains("isAvailable", Tag.TAG_BYTE) || tag.getBoolean("isAvailable");

        this.loadStoredFluid(tag, registries, "inputFluid", true);
        this.loadStoredFluid(tag, registries, "outputFluid", false);
        this.loadAutomationModes(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
        tag.putInt("progress", this.progress);
        tag.putInt("maxProgress", this.maxProgress);
        tag.putLong("storedCobblestonePower", this.storedCobblestonePower);
        tag.putBoolean("isAvailable", this.isAvailable);

        this.saveStoredFluid(tag, registries, "inputFluid", this.storedInputFluid, this.storedInputFluidAmount);
        this.saveStoredFluid(tag, registries, "outputFluid", this.storedOutputFluid, this.storedOutputFluidAmount);
        this.saveAutomationModes(tag);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_dissolution_chamber");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CobblestoneDissolutionChamberMenu(containerId, playerInventory, this, new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_PROGRESS) {
                    return CobblestoneDissolutionChamberBlockEntity.this.progress;
                }
                if (index == DATA_INDEX_MAX_PROGRESS) {
                    return CobblestoneDissolutionChamberBlockEntity.this.maxProgress;
                }
                if (index == DATA_INDEX_STORED_POWER) {
                    return LongDataHelper.lowerInt(CobblestoneDissolutionChamberBlockEntity.this.storedCobblestonePower);
                }
                if (index == DATA_INDEX_STORED_POWER_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneDissolutionChamberBlockEntity.this.storedCobblestonePower);
                }
                if (index == DATA_INDEX_MAX_STORED_POWER) {
                    return LongDataHelper.lowerInt(CobblestoneDissolutionChamberBlockEntity.this.getMaxCobblestonePower());
                }
                if (index == DATA_INDEX_MAX_STORED_POWER_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneDissolutionChamberBlockEntity.this.getMaxCobblestonePower());
                }
                if (index == DATA_INDEX_INPUT_FLUID) {
                    return LongDataHelper.lowerInt(CobblestoneDissolutionChamberBlockEntity.this.storedInputFluidAmount);
                }
                if (index == DATA_INDEX_INPUT_FLUID_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneDissolutionChamberBlockEntity.this.storedInputFluidAmount);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID) {
                    return LongDataHelper.lowerInt(MAX_INPUT_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID_UPPER) {
                    return LongDataHelper.upperInt(MAX_INPUT_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_INPUT_FLUID_ID) {
                    return CobblestoneDissolutionChamberBlockEntity.this.getDisplayedInputFluidId();
                }
                if (index == DATA_INDEX_OUTPUT_FLUID) {
                    return LongDataHelper.lowerInt(CobblestoneDissolutionChamberBlockEntity.this.storedOutputFluidAmount);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneDissolutionChamberBlockEntity.this.storedOutputFluidAmount);
                }
                if (index == DATA_INDEX_MAX_OUTPUT_FLUID) {
                    return LongDataHelper.lowerInt(MAX_OUTPUT_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_OUTPUT_FLUID_UPPER) {
                    return LongDataHelper.upperInt(MAX_OUTPUT_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID_ID) {
                    return CobblestoneDissolutionChamberBlockEntity.this.getDisplayedOutputFluidId();
                }
                if (index >= DATA_INDEX_ITEM_AUTOMATION_START && index < DATA_INDEX_FLUID_AUTOMATION_START) {
                    return CobblestoneDissolutionChamberBlockEntity.this.getAutomationModeId(index - DATA_INDEX_ITEM_AUTOMATION_START);
                }
                if (index >= DATA_INDEX_FLUID_AUTOMATION_START && index < DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneDissolutionChamberBlockEntity.this.getFluidAutomationModeId(index - DATA_INDEX_FLUID_AUTOMATION_START);
                }
                if (index == DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneDissolutionChamberBlockEntity.this.getAutoExportEnabledId();
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
        });
    }

    private int getDisplayedInputFluidId() {
        if (this.storedInputFluid.isEmpty() || this.storedInputFluidAmount <= 0L) {
            return -1;
        }

        return BuiltInRegistries.FLUID.getId(this.storedInputFluid.getFluid());
    }

    private int getDisplayedOutputFluidId() {
        if (this.storedOutputFluid.isEmpty() || this.storedOutputFluidAmount <= 0L) {
            return -1;
        }

        return BuiltInRegistries.FLUID.getId(this.storedOutputFluid.getFluid());
    }

    private void autoExportFluid() {
        if (!this.isAutoExportEnabled() || this.storedOutputFluid.isEmpty() || this.storedOutputFluidAmount <= 0L) {
            return;
        }

        FluidStack exportStack = this.storedOutputFluid.copyWithAmount((int) Math.min(this.storedOutputFluidAmount, Integer.MAX_VALUE));
        int originalAmount = exportStack.getAmount();
        FluidStack remainingFluid = this.pushFluidToConfiguredSides(exportStack, AutomationMode.OUTPUT, AutomationMode.IN_OUT);
        int exportedAmount = originalAmount - remainingFluid.getAmount();
        if (exportedAmount > 0) {
            this.drainOutputInternal(exportedAmount, IFluidHandler.FluidAction.EXECUTE);
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

    private Optional<RecipeHolder<CobblestoneDissolutionChamberRecipe>> getCurrentRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty() || this.storedInputFluidAmount <= 0L || this.storedInputFluid.isEmpty()) {
            return Optional.empty();
        }

        DissolutionChamberRecipeInput input = new DissolutionChamberRecipeInput(inputStack, this.getDisplayedInputFluid());
        return currentLevel.getRecipeManager().getRecipeFor(ModRecipeTypes.COBBLESTONE_DISSOLUTION_CHAMBER.get(), input, currentLevel);
    }

    private boolean canProcess(CobblestoneDissolutionChamberRecipe recipe) {
        if (this.getProgressStep(recipe.getCobblestonePowerPerTick()) <= 0) {
            return false;
        }

        if (this.storedInputFluidAmount < recipe.getFluidInput().getAmount()) {
            return false;
        }

        FluidStack fluidOutput = recipe.getFluidOutput();
        if (fluidOutput.isEmpty()) {
            return false;
        }

        if (!this.storedOutputFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(this.storedOutputFluid, fluidOutput)) {
            return false;
        }

        return this.storedOutputFluidAmount + fluidOutput.getAmount() <= MAX_OUTPUT_FLUID_AMOUNT;
    }

    private void craft(CobblestoneDissolutionChamberRecipe recipe) {
        this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX).shrink(1);
        this.drainInputInternal(recipe.getFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        this.fillOutputInternal(recipe.getFluidOutput(), IFluidHandler.FluidAction.EXECUTE);
    }

    private int getProgressStep(long cobblestonePowerPerTick) {
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

    private int fillInputInternal(FluidStack resource, IFluidHandler.FluidAction action) {
        return this.fillInternal(resource, action, true);
    }

    private int fillOutputInternal(FluidStack resource, IFluidHandler.FluidAction action) {
        return this.fillInternal(resource, action, false);
    }

    private int fillInternal(FluidStack resource, IFluidHandler.FluidAction action, boolean inputTank) {
        if (resource.isEmpty()) {
            return 0;
        }

        FluidStack storedFluid = inputTank ? this.storedInputFluid : this.storedOutputFluid;
        long storedAmount = inputTank ? this.storedInputFluidAmount : this.storedOutputFluidAmount;
        long maxAmount = inputTank ? MAX_INPUT_FLUID_AMOUNT : MAX_OUTPUT_FLUID_AMOUNT;
        if (!storedFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(storedFluid, resource)) {
            return 0;
        }

        long remainingCapacity = maxAmount - storedAmount;
        if (remainingCapacity <= 0L) {
            return 0;
        }

        int filledAmount = (int) Math.min(remainingCapacity, resource.getAmount());
        if (filledAmount <= 0) {
            return 0;
        }

        if (action.execute()) {
            if (storedFluid.isEmpty()) {
                storedFluid = resource.copyWithAmount(filledAmount);
            }

            if (inputTank) {
                this.storedInputFluid = storedFluid.copyWithAmount(filledAmount);
                this.storedInputFluidAmount += filledAmount;
            } else {
                this.storedOutputFluid = storedFluid.copyWithAmount(filledAmount);
                this.storedOutputFluidAmount += filledAmount;
            }
            this.setChanged();
        }

        return filledAmount;
    }

    private FluidStack drainInputInternal(int amount, IFluidHandler.FluidAction action) {
        return this.drainInternal(amount, action, true);
    }

    private FluidStack drainOutputInternal(int amount, IFluidHandler.FluidAction action) {
        return this.drainInternal(amount, action, false);
    }

    private FluidStack drainInternal(int amount, IFluidHandler.FluidAction action, boolean inputTank) {
        if (amount <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack storedFluid = inputTank ? this.storedInputFluid : this.storedOutputFluid;
        long storedAmount = inputTank ? this.storedInputFluidAmount : this.storedOutputFluidAmount;
        if (storedFluid.isEmpty() || storedAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        int drainedAmount = (int) Math.min(storedAmount, amount);
        FluidStack drained = storedFluid.copyWithAmount(drainedAmount);
        if (action.execute()) {
            long remainingAmount = storedAmount - drainedAmount;
            if (inputTank) {
                this.storedInputFluidAmount = remainingAmount;
                if (remainingAmount <= 0L) {
                    this.storedInputFluid = FluidStack.EMPTY;
                    this.storedInputFluidAmount = 0L;
                }
            } else {
                this.storedOutputFluidAmount = remainingAmount;
                if (remainingAmount <= 0L) {
                    this.storedOutputFluid = FluidStack.EMPTY;
                    this.storedOutputFluidAmount = 0L;
                }
            }
            this.setChanged();
        }

        return drained;
    }

    private FluidStack drainSpecificInternal(FluidStack resource, IFluidHandler.FluidAction action, boolean inputTank) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack storedFluid = inputTank ? this.storedInputFluid : this.storedOutputFluid;
        if (storedFluid.isEmpty() || !FluidStack.isSameFluidSameComponents(storedFluid, resource)) {
            return FluidStack.EMPTY;
        }

        return this.drainInternal(resource.getAmount(), action, inputTank);
    }

    private boolean tryProcessPlayerFluidContainer(Player player, ItemStack sourceStack, boolean carriedStack, boolean processAll, boolean inputTank) {
        if (sourceStack.isEmpty() || FluidUtil.getFluidHandler(sourceStack).isEmpty()) {
            return false;
        }

        ItemStack singleContainer = sourceStack.copy();
        singleContainer.setCount(1);

        Optional<IFluidHandlerItem> optionalHandler = FluidUtil.getFluidHandler(singleContainer);
        if (optionalHandler.isEmpty()) {
            return false;
        }

        IFluidHandlerItem fluidHandlerItem = optionalHandler.get();
        ItemStack processedContainer = this.tryDrainPlayerContainer(fluidHandlerItem, processAll, inputTank);
        if (processedContainer.isEmpty()) {
            processedContainer = this.tryFillPlayerContainer(fluidHandlerItem, processAll, inputTank);
        }

        if (processedContainer.isEmpty()) {
            return false;
        }

        this.replaceProcessedPlayerContainer(player, sourceStack, processedContainer, carriedStack);
        return true;
    }

    private ItemStack tryDrainPlayerContainer(IFluidHandlerItem fluidHandlerItem, boolean processAll, boolean inputTank) {
        int transferredAmount = 0;
        while (true) {
            int stepTransferredAmount = this.drainFluidHandlerIntoTank(fluidHandlerItem, inputTank);
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

    private ItemStack tryFillPlayerContainer(IFluidHandlerItem fluidHandlerItem, boolean processAll, boolean inputTank) {
        int transferredAmount = 0;
        while (true) {
            int stepTransferredAmount = this.fillFluidHandlerFromTank(fluidHandlerItem, inputTank);
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

    private int drainFluidHandlerIntoTank(IFluidHandlerItem fluidHandlerItem, boolean inputTank) {
        FluidStack simulatedDrain = fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedDrain.isEmpty()) {
            return 0;
        }

        int fillableAmount = inputTank
            ? this.fillInputInternal(simulatedDrain, IFluidHandler.FluidAction.SIMULATE)
            : this.fillOutputInternal(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
        if (fillableAmount <= 0) {
            return 0;
        }

        FluidStack executedDrain = fluidHandlerItem.drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);
        if (executedDrain.isEmpty()) {
            return 0;
        }

        return inputTank
            ? this.fillInputInternal(executedDrain, IFluidHandler.FluidAction.EXECUTE)
            : this.fillOutputInternal(executedDrain, IFluidHandler.FluidAction.EXECUTE);
    }

    private int fillFluidHandlerFromTank(IFluidHandlerItem fluidHandlerItem, boolean inputTank) {
        FluidStack availableFluid = inputTank ? this.getDisplayedInputFluid() : this.getDisplayedOutputFluid();
        if (availableFluid.isEmpty()) {
            return 0;
        }

        FluidStack simulatedFluid = availableFluid.copy();
        int fillAmount = fluidHandlerItem.fill(simulatedFluid, IFluidHandler.FluidAction.SIMULATE);
        if (fillAmount <= 0) {
            return 0;
        }

        FluidStack drainedFluid = inputTank
            ? this.drainInputInternal(fillAmount, IFluidHandler.FluidAction.EXECUTE)
            : this.drainOutputInternal(fillAmount, IFluidHandler.FluidAction.EXECUTE);
        if (drainedFluid.isEmpty()) {
            return 0;
        }

        int executedFillAmount = fluidHandlerItem.fill(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
        if (executedFillAmount <= 0) {
            if (inputTank) {
                this.fillInputInternal(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
            } else {
                this.fillOutputInternal(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
            }
            return 0;
        }

        if (executedFillAmount < drainedFluid.getAmount()) {
            FluidStack remainingFluid = drainedFluid.copyWithAmount(drainedFluid.getAmount() - executedFillAmount);
            if (inputTank) {
                this.fillInputInternal(remainingFluid, IFluidHandler.FluidAction.EXECUTE);
            } else {
                this.fillOutputInternal(remainingFluid, IFluidHandler.FluidAction.EXECUTE);
            }
        }

        return executedFillAmount;
    }

    private void saveStoredFluid(CompoundTag tag, HolderLookup.Provider registries, String key, FluidStack fluidStack, long amount) {
        tag.putLong(key + "Amount", amount);
        if (fluidStack.isEmpty() || amount <= 0L) {
            return;
        }

        tag.put(key, fluidStack.save(registries));
    }

    private void loadStoredFluid(CompoundTag tag, HolderLookup.Provider registries, String key, boolean inputTank) {
        long amount = tag.getLong(key + "Amount");
        FluidStack fluidStack = FluidStack.EMPTY;
        if (tag.contains(key, Tag.TAG_COMPOUND)) {
            fluidStack = FluidStack.parseOptional(registries, tag.getCompound(key));
        }

        if (amount <= 0L || fluidStack.isEmpty()) {
            if (inputTank) {
                this.storedInputFluidAmount = 0L;
                this.storedInputFluid = FluidStack.EMPTY;
            } else {
                this.storedOutputFluidAmount = 0L;
                this.storedOutputFluid = FluidStack.EMPTY;
            }
            return;
        }

        if (inputTank) {
            this.storedInputFluidAmount = amount;
            this.storedInputFluid = fluidStack.copyWithAmount((int) Math.min(amount, Integer.MAX_VALUE));
        } else {
            this.storedOutputFluidAmount = amount;
            this.storedOutputFluid = fluidStack.copyWithAmount((int) Math.min(amount, Integer.MAX_VALUE));
        }
    }

    private class DissolutionChamberFluidHandler implements IFluidHandler {
        private final boolean canFill;
        private final boolean canDrain;
        private final boolean combinedAccess;

        private DissolutionChamberFluidHandler(boolean canFill, boolean canDrain, boolean combinedAccess) {
            this.canFill = canFill;
            this.canDrain = canDrain;
            this.combinedAccess = combinedAccess;
        }

        @Override
        public int getTanks() {
            return this.combinedAccess ? 2 : 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            if (this.combinedAccess) {
                if (tank == 0) {
                    return CobblestoneDissolutionChamberBlockEntity.this.getDisplayedInputFluid();
                }

                if (tank == 1) {
                    return CobblestoneDissolutionChamberBlockEntity.this.getDisplayedOutputFluid();
                }

                return FluidStack.EMPTY;
            }

            return this.canFill
                ? CobblestoneDissolutionChamberBlockEntity.this.getDisplayedInputFluid()
                : CobblestoneDissolutionChamberBlockEntity.this.getDisplayedOutputFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            if (this.combinedAccess) {
                return tank == 0 ? (int) Math.min(Integer.MAX_VALUE, MAX_INPUT_FLUID_AMOUNT) : (int) Math.min(Integer.MAX_VALUE, MAX_OUTPUT_FLUID_AMOUNT);
            }

            return this.canFill ? (int) Math.min(Integer.MAX_VALUE, MAX_INPUT_FLUID_AMOUNT) : (int) Math.min(Integer.MAX_VALUE, MAX_OUTPUT_FLUID_AMOUNT);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            if (!this.canFill || stack.isEmpty()) {
                return false;
            }

            if (this.combinedAccess && tank != 0) {
                return false;
            }

            return CobblestoneDissolutionChamberBlockEntity.this.storedInputFluid.isEmpty()
                || FluidStack.isSameFluidSameComponents(CobblestoneDissolutionChamberBlockEntity.this.storedInputFluid, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!this.canFill) {
                return 0;
            }

            return CobblestoneDissolutionChamberBlockEntity.this.fillInputInternal(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (!this.canDrain) {
                return FluidStack.EMPTY;
            }

            return CobblestoneDissolutionChamberBlockEntity.this.drainSpecificInternal(resource, action, false);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            if (!this.canDrain) {
                return FluidStack.EMPTY;
            }

            return CobblestoneDissolutionChamberBlockEntity.this.drainOutputInternal(maxDrain, action);
        }
    }

    private class SingleSlotAutomationHandler implements IItemHandler {
        private final int slotIndex;
        private final boolean canInsert;

        private SingleSlotAutomationHandler(int slotIndex, boolean canInsert) {
            this.slotIndex = slotIndex;
            this.canInsert = canInsert;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneDissolutionChamberBlockEntity.this.itemStackHandler.getStackInSlot(this.slotIndex);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!this.canInsert || slot != 0) {
                return stack;
            }

            return CobblestoneDissolutionChamberBlockEntity.this.itemStackHandler.insertItem(this.slotIndex, stack, simulate);
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

            return CobblestoneDissolutionChamberBlockEntity.this.itemStackHandler.getSlotLimit(this.slotIndex);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return this.canInsert && slot == 0 && CobblestoneDissolutionChamberBlockEntity.this.itemStackHandler.isItemValid(this.slotIndex, stack);
        }
    }
}