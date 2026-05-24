package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFluidMixerMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFluidMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.FluidMixerRecipeInput;
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

public class CobblestoneFluidMixerBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int POWER_SLOT_INDEX = 0;
    public static final int ACCELERATION_SLOT_INDEX = 1;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 2;
    public static final long MAX_COBBLESTONE_POWER = CobblestoneCrusherBlockEntity.MAX_COBBLESTONE_POWER;
    public static final long MAX_INPUT_FLUID_1_AMOUNT = 64_000L;
    public static final long MAX_INPUT_FLUID_2_AMOUNT = 64_000L;
    public static final long MAX_OUTPUT_FLUID_AMOUNT = 64_000L;

    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_POWER_UPPER = 3;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    private static final int DATA_INDEX_INPUT_FLUID_1 = 6;
    private static final int DATA_INDEX_INPUT_FLUID_1_UPPER = 7;
    private static final int DATA_INDEX_MAX_INPUT_FLUID_1 = 8;
    private static final int DATA_INDEX_MAX_INPUT_FLUID_1_UPPER = 9;
    private static final int DATA_INDEX_INPUT_FLUID_1_ID = 10;
    private static final int DATA_INDEX_INPUT_FLUID_2 = 11;
    private static final int DATA_INDEX_INPUT_FLUID_2_UPPER = 12;
    private static final int DATA_INDEX_MAX_INPUT_FLUID_2 = 13;
    private static final int DATA_INDEX_MAX_INPUT_FLUID_2_UPPER = 14;
    private static final int DATA_INDEX_INPUT_FLUID_2_ID = 15;
    private static final int DATA_INDEX_OUTPUT_FLUID = 16;
    private static final int DATA_INDEX_OUTPUT_FLUID_UPPER = 17;
    private static final int DATA_INDEX_MAX_OUTPUT_FLUID = 18;
    private static final int DATA_INDEX_MAX_OUTPUT_FLUID_UPPER = 19;
    private static final int DATA_INDEX_OUTPUT_FLUID_ID = 20;
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 21;
    private static final int DATA_INDEX_FLUID_AUTOMATION_START = DATA_INDEX_ITEM_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_FLUID_AUTOMATION_START + AUTOMATION_FACE_COUNT;

    private int progress;
    private int maxProgress;
    private long storedCobblestonePower;
    private long storedInputFluid1Amount;
    private long storedInputFluid2Amount;
    private long storedOutputFluidAmount;
    private FluidStack storedInputFluid1 = FluidStack.EMPTY;
    private FluidStack storedInputFluid2 = FluidStack.EMPTY;
    private FluidStack storedOutputFluid = FluidStack.EMPTY;
    private boolean isAvailable = true;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(3) {
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

            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneFluidMixerBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler cobblestoneInputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneFluidMixerBlockEntity.this.itemStackHandler.getStackInSlot(POWER_SLOT_INDEX);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0) {
                return stack;
            }

            return CobblestoneFluidMixerBlockEntity.this.itemStackHandler.insertItem(POWER_SLOT_INDEX, stack, simulate);
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

            return CobblestoneFluidMixerBlockEntity.this.itemStackHandler.getSlotLimit(POWER_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == 0 && CobblestoneFluidMixerBlockEntity.this.itemStackHandler.isItemValid(POWER_SLOT_INDEX, stack);
        }
    };

    private final IItemHandler automationAccessHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 3;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot < 0 || slot >= 3) {
                return ItemStack.EMPTY;
            }

            return CobblestoneFluidMixerBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 0 || slot >= 3) {
                return 0;
            }

            return CobblestoneFluidMixerBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot < 0 || slot >= 3) {
                return false;
            }

            return CobblestoneFluidMixerBlockEntity.this.itemStackHandler.isItemValid(slot, stack);
        }
    };

    private final IFluidHandler internalFluidHandler = new FluidMixerFluidHandler(true, true);
    private final IFluidHandler inputFluidHandler = new FluidMixerFluidHandler(true, false);
    private final IFluidHandler inputFluid1Handler = new SingleTankInputFluidHandler(0);
    private final IFluidHandler inputFluid2Handler = new SingleTankInputFluidHandler(1);
    private final IFluidHandler outputFluidHandler = new FluidMixerFluidHandler(false, true);

    public CobblestoneFluidMixerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_FLUID_MIXER_BLOCK_ENTITY.get(), pos, state);

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

    public long getStoredInputFluid1Amount() {
        return this.storedInputFluid1Amount;
    }

    public long getStoredInputFluid2Amount() {
        return this.storedInputFluid2Amount;
    }

    public long getStoredOutputFluidAmount() {
        return this.storedOutputFluidAmount;
    }

    public long getMaxInputFluid1Amount() {
        return MAX_INPUT_FLUID_1_AMOUNT;
    }

    public long getMaxInputFluid2Amount() {
        return MAX_INPUT_FLUID_2_AMOUNT;
    }

    public long getMaxOutputFluidAmount() {
        return MAX_OUTPUT_FLUID_AMOUNT;
    }

    public FluidStack getDisplayedInputFluid1() {
        return this.createDisplayedFluid(this.storedInputFluid1, this.storedInputFluid1Amount);
    }

    public FluidStack getDisplayedInputFluid2() {
        return this.createDisplayedFluid(this.storedInputFluid2, this.storedInputFluid2Amount);
    }

    public FluidStack getDisplayedOutputFluid() {
        return this.createDisplayedFluid(this.storedOutputFluid, this.storedOutputFluidAmount);
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
            return this.internalFluidHandler;
        }

        AutomationMode automationMode = this.getFluidAutomationMode(AutomationSide.fromWorldSide(side, this.getBlockState()));
        if (automationMode == AutomationMode.INPUT) {
            return this.inputFluidHandler;
        }

        if (automationMode == AutomationMode.INPUT_1) {
            return this.inputFluid1Handler;
        }

        if (automationMode == AutomationMode.INPUT_2) {
            return this.inputFluid2Handler;
        }

        if (automationMode == AutomationMode.OUTPUT) {
            return this.outputFluidHandler;
        }

        if (automationMode == AutomationMode.IN_OUT) {
            return this.internalFluidHandler;
        }

        return null;
    }

    public boolean handleInputFluid1IndicatorClick(Player player, boolean processAll) {
        return this.handleFluidIndicatorClick(player, processAll, 0);
    }

    public boolean handleInputFluid2IndicatorClick(Player player, boolean processAll) {
        return this.handleFluidIndicatorClick(player, processAll, 1);
    }

    public boolean handleOutputFluidIndicatorClick(Player player, boolean processAll) {
        return this.handleFluidIndicatorClick(player, processAll, 2);
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

        Optional<RecipeHolder<CobblestoneFluidMixerRecipe>> recipeHolder = this.getCurrentRecipe();
        if (this.isAvailable && recipeHolder.isPresent()) {
            CobblestoneFluidMixerRecipe recipe = recipeHolder.get().value();
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

        this.loadStoredFluid(tag, registries, "inputFluid1", 0);
        this.loadStoredFluid(tag, registries, "inputFluid2", 1);
        this.loadStoredFluid(tag, registries, "outputFluid", 2);
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

        this.saveStoredFluid(tag, registries, "inputFluid1", this.storedInputFluid1, this.storedInputFluid1Amount);
        this.saveStoredFluid(tag, registries, "inputFluid2", this.storedInputFluid2, this.storedInputFluid2Amount);
        this.saveStoredFluid(tag, registries, "outputFluid", this.storedOutputFluid, this.storedOutputFluidAmount);
        this.saveAutomationModes(tag);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_fluid_mixer");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CobblestoneFluidMixerMenu(containerId, playerInventory, this, new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_PROGRESS) {
                    return CobblestoneFluidMixerBlockEntity.this.progress;
                }
                if (index == DATA_INDEX_MAX_PROGRESS) {
                    return CobblestoneFluidMixerBlockEntity.this.maxProgress;
                }
                if (index == DATA_INDEX_STORED_POWER) {
                    return LongDataHelper.lowerInt(CobblestoneFluidMixerBlockEntity.this.storedCobblestonePower);
                }
                if (index == DATA_INDEX_STORED_POWER_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneFluidMixerBlockEntity.this.storedCobblestonePower);
                }
                if (index == DATA_INDEX_MAX_STORED_POWER) {
                    return LongDataHelper.lowerInt(CobblestoneFluidMixerBlockEntity.this.getMaxCobblestonePower());
                }
                if (index == DATA_INDEX_MAX_STORED_POWER_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneFluidMixerBlockEntity.this.getMaxCobblestonePower());
                }
                if (index == DATA_INDEX_INPUT_FLUID_1) {
                    return LongDataHelper.lowerInt(CobblestoneFluidMixerBlockEntity.this.storedInputFluid1Amount);
                }
                if (index == DATA_INDEX_INPUT_FLUID_1_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneFluidMixerBlockEntity.this.storedInputFluid1Amount);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID_1) {
                    return LongDataHelper.lowerInt(MAX_INPUT_FLUID_1_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID_1_UPPER) {
                    return LongDataHelper.upperInt(MAX_INPUT_FLUID_1_AMOUNT);
                }
                if (index == DATA_INDEX_INPUT_FLUID_1_ID) {
                    return CobblestoneFluidMixerBlockEntity.this.getFluidId(0);
                }
                if (index == DATA_INDEX_INPUT_FLUID_2) {
                    return LongDataHelper.lowerInt(CobblestoneFluidMixerBlockEntity.this.storedInputFluid2Amount);
                }
                if (index == DATA_INDEX_INPUT_FLUID_2_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneFluidMixerBlockEntity.this.storedInputFluid2Amount);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID_2) {
                    return LongDataHelper.lowerInt(MAX_INPUT_FLUID_2_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID_2_UPPER) {
                    return LongDataHelper.upperInt(MAX_INPUT_FLUID_2_AMOUNT);
                }
                if (index == DATA_INDEX_INPUT_FLUID_2_ID) {
                    return CobblestoneFluidMixerBlockEntity.this.getFluidId(1);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID) {
                    return LongDataHelper.lowerInt(CobblestoneFluidMixerBlockEntity.this.storedOutputFluidAmount);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneFluidMixerBlockEntity.this.storedOutputFluidAmount);
                }
                if (index == DATA_INDEX_MAX_OUTPUT_FLUID) {
                    return LongDataHelper.lowerInt(MAX_OUTPUT_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_OUTPUT_FLUID_UPPER) {
                    return LongDataHelper.upperInt(MAX_OUTPUT_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID_ID) {
                    return CobblestoneFluidMixerBlockEntity.this.getFluidId(2);
                }
                if (index >= DATA_INDEX_ITEM_AUTOMATION_START && index < DATA_INDEX_FLUID_AUTOMATION_START) {
                    return CobblestoneFluidMixerBlockEntity.this.getAutomationModeId(index - DATA_INDEX_ITEM_AUTOMATION_START);
                }
                if (index >= DATA_INDEX_FLUID_AUTOMATION_START && index < DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneFluidMixerBlockEntity.this.getFluidAutomationModeId(index - DATA_INDEX_FLUID_AUTOMATION_START);
                }
                if (index == DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneFluidMixerBlockEntity.this.getAutoExportEnabledId();
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

    private FluidStack createDisplayedFluid(FluidStack storedFluid, long storedAmount) {
        if (storedFluid.isEmpty() || storedAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        return storedFluid.copyWithAmount((int) Math.min(storedAmount, Integer.MAX_VALUE));
    }

    private int getFluidId(int tankIndex) {
        FluidStack fluidStack = switch (tankIndex) {
            case 0 -> this.storedInputFluid1;
            case 1 -> this.storedInputFluid2;
            case 2 -> this.storedOutputFluid;
            default -> FluidStack.EMPTY;
        };
        long amount = switch (tankIndex) {
            case 0 -> this.storedInputFluid1Amount;
            case 1 -> this.storedInputFluid2Amount;
            case 2 -> this.storedOutputFluidAmount;
            default -> 0L;
        };

        if (fluidStack.isEmpty() || amount <= 0L) {
            return -1;
        }

        return BuiltInRegistries.FLUID.getId(fluidStack.getFluid());
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
            this.drainTankInternal(2, exportedAmount, IFluidHandler.FluidAction.EXECUTE);
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

    private Optional<RecipeHolder<CobblestoneFluidMixerRecipe>> getCurrentRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        if (this.storedInputFluid1Amount <= 0L || this.storedInputFluid2Amount <= 0L) {
            return Optional.empty();
        }

        FluidMixerRecipeInput input = new FluidMixerRecipeInput(this.getDisplayedInputFluid1(), this.getDisplayedInputFluid2());
        return currentLevel.getRecipeManager().getRecipeFor(ModRecipeTypes.COBBLESTONE_FLUID_MIXER.get(), input, currentLevel);
    }

    private boolean canProcess(CobblestoneFluidMixerRecipe recipe) {
        if (this.getProgressStep(recipe.getCobblestonePowerPerTick()) <= 0) {
            return false;
        }

        if (this.storedInputFluid1Amount < recipe.getFirstFluidInput().getAmount()) {
            return false;
        }

        if (this.storedInputFluid2Amount < recipe.getSecondFluidInput().getAmount()) {
            return false;
        }

        FluidStack outputFluid = recipe.getFluidOutput();
        if (outputFluid.isEmpty()) {
            return false;
        }

        if (!this.storedOutputFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(this.storedOutputFluid, outputFluid)) {
            return false;
        }

        return this.storedOutputFluidAmount + outputFluid.getAmount() <= MAX_OUTPUT_FLUID_AMOUNT;
    }

    private void craft(CobblestoneFluidMixerRecipe recipe) {
        this.drainTankInternal(0, recipe.getFirstFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        this.drainTankInternal(1, recipe.getSecondFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        this.fillTankInternal(2, recipe.getFluidOutput(), IFluidHandler.FluidAction.EXECUTE);
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

    private boolean handleFluidIndicatorClick(Player player, boolean processAll, int tankIndex) {
        ItemStack carriedStack = player.containerMenu.getCarried();
        if (this.tryProcessPlayerFluidContainer(player, carriedStack, true, processAll, tankIndex)) {
            return true;
        }

        int selectedSlot = player.getInventory().selected;
        ItemStack selectedStack = player.getInventory().getItem(selectedSlot);
        return this.tryProcessPlayerFluidContainer(player, selectedStack, false, processAll, tankIndex);
    }

    private boolean tryProcessPlayerFluidContainer(Player player, ItemStack sourceStack, boolean carriedStack, boolean processAll, int tankIndex) {
        if (sourceStack.isEmpty()) {
            return false;
        }

        Optional<IFluidHandlerItem> optionalHandler = FluidUtil.getFluidHandler(sourceStack.copyWithCount(1));
        if (optionalHandler.isEmpty()) {
            return false;
        }

        IFluidHandlerItem fluidHandlerItem = optionalHandler.get();
        ItemStack processedContainer = this.tryDrainPlayerContainer(fluidHandlerItem, processAll, tankIndex);
        if (processedContainer.isEmpty()) {
            processedContainer = this.tryFillPlayerContainer(fluidHandlerItem, processAll, tankIndex);
        }

        if (processedContainer.isEmpty()) {
            return false;
        }

        this.replaceProcessedPlayerContainer(player, sourceStack, processedContainer, carriedStack);
        return true;
    }

    private ItemStack tryDrainPlayerContainer(IFluidHandlerItem fluidHandlerItem, boolean processAll, int tankIndex) {
        int transferredAmount = 0;
        while (true) {
            int stepTransferredAmount = this.drainFluidHandlerIntoTank(fluidHandlerItem, tankIndex);
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

    private ItemStack tryFillPlayerContainer(IFluidHandlerItem fluidHandlerItem, boolean processAll, int tankIndex) {
        int transferredAmount = 0;
        while (true) {
            int stepTransferredAmount = this.fillFluidHandlerFromTank(fluidHandlerItem, tankIndex);
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

    private int drainFluidHandlerIntoTank(IFluidHandlerItem fluidHandlerItem, int tankIndex) {
        FluidStack simulatedDrain = fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedDrain.isEmpty()) {
            return 0;
        }

        int fillableAmount = this.fillTankInternal(tankIndex, simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
        if (fillableAmount <= 0) {
            return 0;
        }

        FluidStack executedDrain = fluidHandlerItem.drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);
        if (executedDrain.isEmpty()) {
            return 0;
        }

        return this.fillTankInternal(tankIndex, executedDrain, IFluidHandler.FluidAction.EXECUTE);
    }

    private int fillFluidHandlerFromTank(IFluidHandlerItem fluidHandlerItem, int tankIndex) {
        FluidStack availableFluid = this.getDisplayedFluidByTank(tankIndex);
        if (availableFluid.isEmpty()) {
            return 0;
        }

        int fillAmount = fluidHandlerItem.fill(availableFluid.copy(), IFluidHandler.FluidAction.SIMULATE);
        if (fillAmount <= 0) {
            return 0;
        }

        FluidStack drainedFluid = this.drainTankInternal(tankIndex, fillAmount, IFluidHandler.FluidAction.EXECUTE);
        if (drainedFluid.isEmpty()) {
            return 0;
        }

        int executedFillAmount = fluidHandlerItem.fill(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
        if (executedFillAmount <= 0) {
            this.fillTankInternal(tankIndex, drainedFluid, IFluidHandler.FluidAction.EXECUTE);
            return 0;
        }

        if (executedFillAmount < drainedFluid.getAmount()) {
            FluidStack remainingFluid = drainedFluid.copyWithAmount(drainedFluid.getAmount() - executedFillAmount);
            this.fillTankInternal(tankIndex, remainingFluid, IFluidHandler.FluidAction.EXECUTE);
        }

        return executedFillAmount;
    }

    private FluidStack getDisplayedFluidByTank(int tankIndex) {
        return switch (tankIndex) {
            case 0 -> this.getDisplayedInputFluid1();
            case 1 -> this.getDisplayedInputFluid2();
            case 2 -> this.getDisplayedOutputFluid();
            default -> FluidStack.EMPTY;
        };
    }

    private int fillTankInternal(int tankIndex, FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }

        FluidStack storedFluid = this.getStoredFluidByTank(tankIndex);
        long storedAmount = this.getStoredAmountByTank(tankIndex);
        long maxAmount = this.getMaxAmountByTank(tankIndex);
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
            FluidStack newStoredFluid = storedFluid.isEmpty() ? resource.copyWithAmount(filledAmount) : storedFluid.copy();
            this.setStoredFluidByTank(tankIndex, newStoredFluid, storedAmount + filledAmount);
            this.setChanged();
        }

        return filledAmount;
    }

    private FluidStack drainTankInternal(int tankIndex, int amount, IFluidHandler.FluidAction action) {
        if (amount <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack storedFluid = this.getStoredFluidByTank(tankIndex);
        long storedAmount = this.getStoredAmountByTank(tankIndex);
        if (storedFluid.isEmpty() || storedAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        int drainedAmount = (int) Math.min(storedAmount, amount);
        FluidStack drained = storedFluid.copyWithAmount(drainedAmount);
        if (action.execute()) {
            long remainingAmount = storedAmount - drainedAmount;
            if (remainingAmount <= 0L) {
                this.setStoredFluidByTank(tankIndex, FluidStack.EMPTY, 0L);
            } else {
                this.setStoredFluidByTank(tankIndex, storedFluid, remainingAmount);
            }
            this.setChanged();
        }

        return drained;
    }

    private int fillIntoInputTanks(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!this.storedInputFluid1.isEmpty() && FluidStack.isSameFluidSameComponents(this.storedInputFluid1, resource)) {
            return this.fillTankInternal(0, resource, action);
        }

        if (!this.storedInputFluid2.isEmpty() && FluidStack.isSameFluidSameComponents(this.storedInputFluid2, resource)) {
            return this.fillTankInternal(1, resource, action);
        }

        int filledAmount = this.fillTankInternal(0, resource, action);
        if (filledAmount > 0) {
            return filledAmount;
        }

        return this.fillTankInternal(1, resource, action);
    }

    private int fillSpecificInputTank(int tankIndex, FluidStack resource, IFluidHandler.FluidAction action) {
        if (tankIndex != 0 && tankIndex != 1) {
            return 0;
        }

        return this.fillTankInternal(tankIndex, resource, action);
    }

    private FluidStack getStoredFluidByTank(int tankIndex) {
        return switch (tankIndex) {
            case 0 -> this.storedInputFluid1;
            case 1 -> this.storedInputFluid2;
            case 2 -> this.storedOutputFluid;
            default -> FluidStack.EMPTY;
        };
    }

    private long getStoredAmountByTank(int tankIndex) {
        return switch (tankIndex) {
            case 0 -> this.storedInputFluid1Amount;
            case 1 -> this.storedInputFluid2Amount;
            case 2 -> this.storedOutputFluidAmount;
            default -> 0L;
        };
    }

    private long getMaxAmountByTank(int tankIndex) {
        return switch (tankIndex) {
            case 0 -> MAX_INPUT_FLUID_1_AMOUNT;
            case 1 -> MAX_INPUT_FLUID_2_AMOUNT;
            case 2 -> MAX_OUTPUT_FLUID_AMOUNT;
            default -> 0L;
        };
    }

    private void setStoredFluidByTank(int tankIndex, FluidStack storedFluid, long amount) {
        FluidStack copiedFluid = storedFluid.isEmpty() || amount <= 0L ? FluidStack.EMPTY : storedFluid.copy();
        switch (tankIndex) {
            case 0 -> {
                this.storedInputFluid1 = copiedFluid;
                this.storedInputFluid1Amount = copiedFluid.isEmpty() ? 0L : amount;
            }
            case 1 -> {
                this.storedInputFluid2 = copiedFluid;
                this.storedInputFluid2Amount = copiedFluid.isEmpty() ? 0L : amount;
            }
            case 2 -> {
                this.storedOutputFluid = copiedFluid;
                this.storedOutputFluidAmount = copiedFluid.isEmpty() ? 0L : amount;
            }
            default -> {
            }
        }
    }

    private void saveStoredFluid(CompoundTag tag, HolderLookup.Provider registries, String key, FluidStack fluidStack, long amount) {
        tag.putLong(key + "Amount", amount);
        if (fluidStack.isEmpty() || amount <= 0L) {
            return;
        }

        tag.put(key, fluidStack.save(registries));
    }

    private void loadStoredFluid(CompoundTag tag, HolderLookup.Provider registries, String key, int tankIndex) {
        long amount = tag.getLong(key + "Amount");
        FluidStack fluidStack = FluidStack.EMPTY;
        if (tag.contains(key, Tag.TAG_COMPOUND)) {
            fluidStack = FluidStack.parseOptional(registries, tag.getCompound(key));
        }

        if (fluidStack.isEmpty() || amount <= 0L) {
            this.setStoredFluidByTank(tankIndex, FluidStack.EMPTY, 0L);
            return;
        }

        this.setStoredFluidByTank(tankIndex, fluidStack, amount);
    }

    private class FluidMixerFluidHandler implements IFluidHandler {
        private final boolean allowInput;
        private final boolean allowOutput;

        private FluidMixerFluidHandler(boolean allowInput, boolean allowOutput) {
            this.allowInput = allowInput;
            this.allowOutput = allowOutput;
        }

        @Override
        public int getTanks() {
            return 3;
        }

        @Override
        public @Nonnull FluidStack getFluidInTank(int tank) {
            return CobblestoneFluidMixerBlockEntity.this.getDisplayedFluidByTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return (int) Math.min(Integer.MAX_VALUE, CobblestoneFluidMixerBlockEntity.this.getMaxAmountByTank(tank));
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return tank == 0 || tank == 1 || tank == 2;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!this.allowInput) {
                return 0;
            }

            return CobblestoneFluidMixerBlockEntity.this.fillIntoInputTanks(resource, action);
        }

        @Override
        public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) {
            if (!this.allowOutput || resource.isEmpty()) {
                return FluidStack.EMPTY;
            }

            if (CobblestoneFluidMixerBlockEntity.this.storedOutputFluid.isEmpty()) {
                return FluidStack.EMPTY;
            }

            if (!FluidStack.isSameFluidSameComponents(CobblestoneFluidMixerBlockEntity.this.storedOutputFluid, resource)) {
                return FluidStack.EMPTY;
            }

            return CobblestoneFluidMixerBlockEntity.this.drainTankInternal(2, resource.getAmount(), action);
        }

        @Override
        public @Nonnull FluidStack drain(int maxDrain, FluidAction action) {
            if (!this.allowOutput) {
                return FluidStack.EMPTY;
            }

            return CobblestoneFluidMixerBlockEntity.this.drainTankInternal(2, maxDrain, action);
        }
    }

    private class SingleTankInputFluidHandler implements IFluidHandler {
        private final int tankIndex;

        private SingleTankInputFluidHandler(int tankIndex) {
            this.tankIndex = tankIndex;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @Nonnull FluidStack getFluidInTank(int tank) {
            if (tank != 0) {
                return FluidStack.EMPTY;
            }

            return CobblestoneFluidMixerBlockEntity.this.getDisplayedFluidByTank(this.tankIndex);
        }

        @Override
        public int getTankCapacity(int tank) {
            if (tank != 0) {
                return 0;
            }

            return (int) Math.min(Integer.MAX_VALUE, CobblestoneFluidMixerBlockEntity.this.getMaxAmountByTank(this.tankIndex));
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return tank == 0;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return CobblestoneFluidMixerBlockEntity.this.fillSpecificInputTank(this.tankIndex, resource, action);
        }

        @Override
        public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public @Nonnull FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
    }
}