package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCentrifugeMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCentrifugeRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import org.jetbrains.annotations.NotNull;

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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

public class CobblestoneCentrifugeBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int POWER_SLOT_INDEX = 1;
    public static final int OUTPUT_SLOT_1_INDEX = 2;
    public static final int OUTPUT_SLOT_2_INDEX = 3;
    public static final int ACCELERATION_SLOT_INDEX = 4;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 5;
    public static final long MAX_COBBLESTONE_POWER = 64000L;

    private static final int DATA_INDEX_PROGRESS = 0;
    private static final int DATA_INDEX_MAX_PROGRESS = 1;
    private static final int DATA_INDEX_STORED_POWER = 2;
    private static final int DATA_INDEX_STORED_POWER_UPPER = 3;
    private static final int DATA_INDEX_MAX_STORED_POWER = 4;
    private static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    private static final int DATA_INDEX_AUTOMATION_START = 6;
    private static final int DATA_INDEX_CURRENT_POWER_RATE = DATA_INDEX_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_CURRENT_POWER_RATE_UPPER = DATA_INDEX_CURRENT_POWER_RATE + 1;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_CURRENT_POWER_RATE_UPPER + 1;

    private int progress = 0;
    private int maxProgress = 0;
    private long storedCobblestonePower = 0L;
    private boolean isAvailable = true;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(6) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == OUTPUT_SLOT_1_INDEX || slot == OUTPUT_SLOT_2_INDEX) {
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
            CobblestoneCentrifugeBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler inputAutomationHandler = new SingleSlotInsertHandler(INPUT_SLOT_INDEX);
    private final IItemHandler cobblestoneInputAutomationHandler = new SingleSlotInsertHandler(POWER_SLOT_INDEX);
    private final IItemHandler outputSlot1AutomationHandler = new SingleSlotExtractHandler(OUTPUT_SLOT_1_INDEX);
    private final IItemHandler outputSlot2AutomationHandler = new SingleSlotExtractHandler(OUTPUT_SLOT_2_INDEX);

    private final IItemHandler outputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot == 0) {
                return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_1_INDEX);
            }

            if (slot == 1) {
                return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_2_INDEX);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0) {
                return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_1_INDEX, amount, simulate);
            }

            if (slot == 1) {
                return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_2_INDEX, amount, simulate);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == 0) {
                return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.getSlotLimit(OUTPUT_SLOT_1_INDEX);
            }

            if (slot == 1) {
                return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.getSlotLimit(OUTPUT_SLOT_2_INDEX);
            }

            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };

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

            return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == INPUT_SLOT_INDEX) {
                return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.insertItem(slot, stack, simulate);
            }

            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == OUTPUT_SLOT_1_INDEX || slot == OUTPUT_SLOT_2_INDEX) {
                return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.extractItem(slot, amount, simulate);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 0 || slot >= 6) {
                return 0;
            }

            return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot < 0 || slot >= 6) {
                return false;
            }

            return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.isItemValid(slot, stack);
        }
    };

    public CobblestoneCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_CENTRIFUGE_BLOCK_ENTITY.get(), pos, state);
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

    public long getCurrentCobblestonePowerConsumption() {
        if (!this.isAvailable) {
            return 0L;
        }

        var recipeHolder = this.getCurrentRecipe();
        if (recipeHolder.isEmpty()) {
            return 0L;
        }

        var recipe = recipeHolder.get().value();
        if (!this.canProcess(recipe)) {
            return 0L;
        }

        long cobblestonePowerPerTick = recipe.getCobblestonePowerPerTick();
        return cobblestonePowerPerTick * this.getProgressStep(cobblestonePowerPerTick);
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
    }

    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public IItemHandler getAutomationItemHandler(Direction side) {
        if (side == null) {
            return this.automationAccessHandler;
        }

        BlockState currentState = this.getBlockState();
        AutomationSide automationSide = AutomationSide.fromWorldSide(side, currentState);
        AutomationMode automationMode = this.getAutomationMode(automationSide);
        if (automationMode == AutomationMode.INPUT) {
            return this.inputAutomationHandler;
        }

        if (automationMode == AutomationMode.COBBLESTONE_INPUT) {
            return this.cobblestoneInputAutomationHandler;
        }

        if (automationMode == AutomationMode.OUTPUT) {
            return this.outputAutomationHandler;
        }

        if (automationMode == AutomationMode.OUTPUT_1) {
            return this.outputSlot1AutomationHandler;
        }

        if (automationMode == AutomationMode.OUTPUT_2) {
            return this.outputSlot2AutomationHandler;
        }

        if (automationMode == AutomationMode.IN_OUT) {
            return this.automationAccessHandler;
        }

        return EmptyItemHandler.INSTANCE;
    }

    public void reverseIsAvailable() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        this.isAvailable = !this.isAvailable;
        this.setChanged();
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

        Optional<RecipeHolder<CobblestoneCentrifugeRecipe>> recipeHolder = this.getCurrentRecipe();
        if (this.isAvailable && recipeHolder.isPresent()) {
            CobblestoneCentrifugeRecipe recipe = recipeHolder.get().value();
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

        this.pushOutputsToConfiguredSides();
    }

    private void pushOutputsToConfiguredSides() {
        ItemStack outputStack1 = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_1_INDEX);
        if (!outputStack1.isEmpty()) {
            ItemStack remainingOutput1 = this.pushItemStackToConfiguredSides(outputStack1.copy(), AutomationMode.OUTPUT, AutomationMode.OUTPUT_1, AutomationMode.IN_OUT);
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_1_INDEX, remainingOutput1);
        }

        ItemStack outputStack2 = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_2_INDEX);
        if (!outputStack2.isEmpty()) {
            ItemStack remainingOutput2 = this.pushItemStackToConfiguredSides(outputStack2.copy(), AutomationMode.OUTPUT, AutomationMode.OUTPUT_2, AutomationMode.IN_OUT);
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_2_INDEX, remainingOutput2);
        }
    }

    private void tryAbsorbCobblestonePower() {
        ItemStack powerStack = this.itemStackHandler.getStackInSlot(POWER_SLOT_INDEX);
        long convertedPower = CobblestoneCrusherBlockEntity.getCobblestonePowerValueForAutomation(powerStack);
        if (convertedPower <= 0) {
            return;
        }

        if (this.storedCobblestonePower + convertedPower > this.getMaxCobblestonePower()) {
            return;
        }

        powerStack.shrink(1);
        this.storedCobblestonePower += convertedPower;
        this.setChanged();
    }

    private Optional<RecipeHolder<CobblestoneCentrifugeRecipe>> getCurrentRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        return currentLevel.getRecipeManager().getRecipeFor(ModRecipeTypes.COBBLESTONE_CENTRIFUGE.get(), input, currentLevel);
    }

    private boolean canProcess(CobblestoneCentrifugeRecipe recipe) {
        int progressStep = this.getProgressStep(recipe.getCobblestonePowerPerTick());
        if (progressStep <= 0) {
            return false;
        }

        if (!this.canAcceptResult(OUTPUT_SLOT_1_INDEX, recipe.getFirstResult())) {
            return false;
        }

        return this.canAcceptResult(OUTPUT_SLOT_2_INDEX, recipe.getSecondResult());
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
        if (this.itemStackHandler.getSlots() <= ACCELERATION_SLOT_INDEX) {
            return 1;
        }

        ItemStack accelerationStack = this.itemStackHandler.getStackInSlot(ACCELERATION_SLOT_INDEX);
        int multiplier = MachineUpgradeHelper.getAccelerationMultiplier(accelerationStack);
        if (multiplier <= 0) {
            return 1;
        }

        return multiplier;
    }

    private int getEnergizedCubeMultiplier() {
        if (this.itemStackHandler.getSlots() <= ENERGIZED_CUBE_SLOT_INDEX) {
            return 1;
        }

        ItemStack energizedCubeStack = this.itemStackHandler.getStackInSlot(ENERGIZED_CUBE_SLOT_INDEX);
        int multiplier = MachineUpgradeHelper.getEnergizedCubeMultiplier(energizedCubeStack);
        if (multiplier <= 0) {
            return 1;
        }

        return multiplier;
    }

    private void clampStoredCobblestonePower() {
        long maxCobblestonePower = this.getMaxCobblestonePower();
        if (this.storedCobblestonePower > maxCobblestonePower) {
            this.storedCobblestonePower = maxCobblestonePower;
            this.setChanged();
        }
    }

    private boolean canAcceptResult(int slotIndex, ItemStack resultStack) {
        if (resultStack.isEmpty()) {
            return true;
        }

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(slotIndex);
        if (outputStack.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
            return false;
        }

        return outputStack.getCount() + resultStack.getCount() <= outputStack.getMaxStackSize();
    }

    private void craft(CobblestoneCentrifugeRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        inputStack.shrink(1);

        this.insertResult(OUTPUT_SLOT_1_INDEX, recipe.rollFirstResult(currentLevel.random));
        this.insertResult(OUTPUT_SLOT_2_INDEX, recipe.rollSecondResult(currentLevel.random));
    }

    private void insertResult(int slotIndex, ItemStack resultStack) {
        if (resultStack.isEmpty()) {
            return;
        }

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(slotIndex);
        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(slotIndex, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", this.progress);
        tag.putInt("maxProgress", this.maxProgress);
        tag.putLong("storedCobblestonePower", this.storedCobblestonePower);
        tag.putBoolean("isAvailable", this.isAvailable);
        this.saveAutomationModes(tag);
        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("maxProgress");
        this.storedCobblestonePower = tag.getLong("storedCobblestonePower");
        this.isAvailable = tag.getBoolean("isAvailable");
        this.loadAutomationModes(tag);
        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_centrifuge");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData centrifugeData = new ContainerData() {
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

                if (index == DATA_INDEX_CURRENT_POWER_RATE) {
                    return LongDataHelper.lowerInt(getCurrentCobblestonePowerConsumption());
                }

                if (index == DATA_INDEX_CURRENT_POWER_RATE_UPPER) {
                    return LongDataHelper.upperInt(getCurrentCobblestonePowerConsumption());
                }

                int automationIndex = index - DATA_INDEX_AUTOMATION_START;
                if (automationIndex >= 0 && automationIndex < AUTOMATION_FACE_COUNT) {
                    return getAutomationModeId(automationIndex);
                }

                if (index == DATA_INDEX_AUTO_EXPORT) {
                    return getAutoExportEnabledId();
                }

                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == DATA_INDEX_PROGRESS) {
                    progress = value;
                }

                if (index == DATA_INDEX_MAX_PROGRESS) {
                    maxProgress = value;
                }

                if (index == DATA_INDEX_STORED_POWER) {
                    storedCobblestonePower = LongDataHelper.toLong(value, LongDataHelper.upperInt(storedCobblestonePower));
                }

                if (index == DATA_INDEX_STORED_POWER_UPPER) {
                    storedCobblestonePower = LongDataHelper.toLong(LongDataHelper.lowerInt(storedCobblestonePower), value);
                }

                int automationIndex = index - DATA_INDEX_AUTOMATION_START;
                if (automationIndex >= 0 && automationIndex < AUTOMATION_FACE_COUNT) {
                    setAutomationMode(automationIndex, AutomationMode.fromId(value));
                }

                if (index == DATA_INDEX_AUTO_EXPORT) {
                    setAutoExportEnabled(value != 0);
                }
            }

            @Override
            public int getCount() {
                return DATA_INDEX_AUTO_EXPORT + 1;
            }
        };

        return new CobblestoneCentrifugeMenu(containerId, playerInventory, this, centrifugeData);
    }

    private class SingleSlotInsertHandler implements IItemHandler {
        private final int slotIndex;

        private SingleSlotInsertHandler(int slotIndex) {
            this.slotIndex = slotIndex;
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

            return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.getStackInSlot(this.slotIndex);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0) {
                return stack;
            }

            return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.insertItem(this.slotIndex, stack, simulate);
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

            return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.getSlotLimit(this.slotIndex);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot != 0) {
                return false;
            }

            return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.isItemValid(this.slotIndex, stack);
        }
    }

    private class SingleSlotExtractHandler implements IItemHandler {
        private final int slotIndex;

        private SingleSlotExtractHandler(int slotIndex) {
            this.slotIndex = slotIndex;
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

            return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.getStackInSlot(this.slotIndex);
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

            return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.extractItem(this.slotIndex, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot != 0) {
                return 0;
            }

            return CobblestoneCentrifugeBlockEntity.this.itemStackHandler.getSlotLimit(this.slotIndex);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    }
}