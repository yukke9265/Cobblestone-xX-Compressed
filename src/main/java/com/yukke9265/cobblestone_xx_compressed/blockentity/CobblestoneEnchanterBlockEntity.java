package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneEnchanterMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneEnchanterRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneEnchanterRecipeInput;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.util.CobblestoneEnchanterHelper;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

@SuppressWarnings("null")
public class CobblestoneEnchanterBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int TOOL_INPUT_SLOT_INDEX = 0;
    public static final int BOOK_INPUT_SLOT_INDEX = 1;
    public static final int POWER_SLOT_INDEX = 2;
    public static final int OUTPUT_SLOT_INDEX = 3;
    public static final int ACCELERATION_SLOT_INDEX = 4;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 5;
    public static final long MAX_COBBLESTONE_POWER = 16384L;

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

    private int progress;
    private int maxProgress;
    private long storedCobblestonePower;
    private boolean isAvailable = true;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(6) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == OUTPUT_SLOT_INDEX) {
                return false;
            }

            if (slot == TOOL_INPUT_SLOT_INDEX) {
                return isValidToolCandidate(stack);
            }

            if (slot == BOOK_INPUT_SLOT_INDEX) {
                return stack.is(Items.ENCHANTED_BOOK);
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

            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneEnchanterBlockEntity.this.setChanged();
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
                return CobblestoneEnchanterBlockEntity.this.itemStackHandler.getStackInSlot(TOOL_INPUT_SLOT_INDEX);
            }
            if (slot == 1) {
                return CobblestoneEnchanterBlockEntity.this.itemStackHandler.getStackInSlot(BOOK_INPUT_SLOT_INDEX);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (stack.is(Items.ENCHANTED_BOOK)) {
                return CobblestoneEnchanterBlockEntity.this.itemStackHandler.insertItem(BOOK_INPUT_SLOT_INDEX, stack, simulate);
            }

            return CobblestoneEnchanterBlockEntity.this.itemStackHandler.insertItem(TOOL_INPUT_SLOT_INDEX, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == 0) {
                return CobblestoneEnchanterBlockEntity.this.itemStackHandler.getSlotLimit(TOOL_INPUT_SLOT_INDEX);
            }
            if (slot == 1) {
                return CobblestoneEnchanterBlockEntity.this.itemStackHandler.getSlotLimit(BOOK_INPUT_SLOT_INDEX);
            }

            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == 0) {
                return CobblestoneEnchanterBlockEntity.isValidToolCandidate(stack);
            }
            if (slot == 1) {
                return stack.is(Items.ENCHANTED_BOOK);
            }

            return false;
        }
    };

    private final IItemHandler toolInputAutomationHandler = new SingleSlotAutomationHandler(TOOL_INPUT_SLOT_INDEX, true, false);
    private final IItemHandler bookInputAutomationHandler = new SingleSlotAutomationHandler(BOOK_INPUT_SLOT_INDEX, true, false);
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

            return CobblestoneEnchanterBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == TOOL_INPUT_SLOT_INDEX || slot == BOOK_INPUT_SLOT_INDEX || slot == POWER_SLOT_INDEX) {
                return CobblestoneEnchanterBlockEntity.this.itemStackHandler.insertItem(slot, stack, simulate);
            }

            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == OUTPUT_SLOT_INDEX) {
                return CobblestoneEnchanterBlockEntity.this.itemStackHandler.extractItem(slot, amount, simulate);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 0 || slot >= 6) {
                return 0;
            }

            return CobblestoneEnchanterBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot < 0 || slot >= 6) {
                return false;
            }

            return CobblestoneEnchanterBlockEntity.this.itemStackHandler.isItemValid(slot, stack);
        }
    };

    public CobblestoneEnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_ENCHANTER_BLOCK_ENTITY.get(), pos, state);
    }

    public static boolean isValidToolCandidate(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.is(Items.ENCHANTED_BOOK)) {
            return false;
        }

        if (CobblestoneCrusherBlockEntity.isCobblestonePowerItem(stack)) {
            return false;
        }

        if (MachineUpgradeHelper.isAccelerationChip(stack) || MachineUpgradeHelper.isEnergizedCube(stack)) {
            return false;
        }

        return true;
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
        long baseCapacity = this.multiplySaturating(MAX_COBBLESTONE_POWER, this.getEnergizedCubeMultiplier());
        long requiredCapacity = this.getRequiredCobblestonePowerCapacity();
        return Math.max(baseCapacity, requiredCapacity);
    }

    public long getCurrentCobblestonePowerConsumption() {
        if (!this.isAvailable || this.level == null) {
            return 0L;
        }

        Optional<RecipeHolder<CobblestoneEnchanterRecipe>> recipeHolder = this.getCurrentRecipe();
        if (recipeHolder.isEmpty()) {
            return 0L;
        }

        CobblestoneEnchanterHelper.EvaluationResult evaluation = recipeHolder.get().value().evaluate(this.createRecipeInput(), this.level.registryAccess());
        if (!evaluation.isValid() || !this.canOutput(evaluation.resultStack())) {
            return 0L;
        }

        long cobblestonePowerPerTick = evaluation.cobblestonePowerPerTick();
        int progressStep = this.getProgressStep(cobblestonePowerPerTick);
        if (progressStep <= 0) {
            return cobblestonePowerPerTick;
        }

        return this.multiplySaturating(cobblestonePowerPerTick, progressStep);
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
        if (automationMode == AutomationMode.INPUT_1) {
            return this.toolInputAutomationHandler;
        }
        if (automationMode == AutomationMode.INPUT_2) {
            return this.bookInputAutomationHandler;
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

        Optional<RecipeHolder<CobblestoneEnchanterRecipe>> recipeHolder = this.getCurrentRecipe();
        if (this.isAvailable && recipeHolder.isPresent()) {
            CobblestoneEnchanterRecipe recipe = recipeHolder.get().value();
            if (this.maxProgress != recipe.getProcessingTicks()) {
                this.maxProgress = recipe.getProcessingTicks();
                this.setChanged();
            }

            CobblestoneEnchanterHelper.EvaluationResult evaluation = recipe.evaluate(this.createRecipeInput(), currentLevel.registryAccess());
            if (this.canProcess(evaluation)) {
                int progressStep = this.getProgressStep(evaluation.cobblestonePowerPerTick());
                this.progress += progressStep;
                this.storedCobblestonePower -= evaluation.cobblestonePowerPerTick() * progressStep;
                shouldTurnOn = true;
                this.setChanged();

                if (this.progress >= this.maxProgress) {
                    this.craft(evaluation.resultStack());
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
    }

    private void pushOutputToConfiguredSides() {
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            return;
        }

        ItemStack remainingOutput = this.pushItemStackToConfiguredSides(outputStack.copy(), AutomationMode.OUTPUT, AutomationMode.IN_OUT);
        this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, remainingOutput);
    }

    private void tryAbsorbCobblestonePower() {
        ItemStack powerStack = this.itemStackHandler.getStackInSlot(POWER_SLOT_INDEX);
        long convertedPower = CobblestoneCrusherBlockEntity.getCobblestonePowerValueForAutomation(powerStack);
        if (convertedPower <= 0L || this.storedCobblestonePower + convertedPower > this.getMaxCobblestonePower()) {
            return;
        }

        powerStack.shrink(1);
        this.storedCobblestonePower += convertedPower;
        this.setChanged();
    }

    private Optional<RecipeHolder<CobblestoneEnchanterRecipe>> getCurrentRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        CobblestoneEnchanterRecipeInput input = this.createRecipeInput();
        if (input.isEmpty()) {
            return Optional.empty();
        }

        return currentLevel.getRecipeManager().getRecipeFor(ModRecipeTypes.COBBLESTONE_ENCHANTER.get(), input, currentLevel);
    }

    private CobblestoneEnchanterRecipeInput createRecipeInput() {
        return new CobblestoneEnchanterRecipeInput(
            this.itemStackHandler.getStackInSlot(TOOL_INPUT_SLOT_INDEX),
            this.itemStackHandler.getStackInSlot(BOOK_INPUT_SLOT_INDEX)
        );
    }

    private boolean canProcess(CobblestoneEnchanterHelper.EvaluationResult evaluation) {
        if (!evaluation.isValid()) {
            return false;
        }

        int progressStep = this.getProgressStep(evaluation.cobblestonePowerPerTick());
        if (progressStep <= 0) {
            return false;
        }

        return this.canOutput(evaluation.resultStack());
    }

    private boolean canOutput(ItemStack resultStack) {
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

    private void craft(ItemStack resultStack) {
        ItemStack toolStack = this.itemStackHandler.getStackInSlot(TOOL_INPUT_SLOT_INDEX);
        ItemStack bookStack = this.itemStackHandler.getStackInSlot(BOOK_INPUT_SLOT_INDEX);
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);

        toolStack.shrink(1);
        bookStack.shrink(1);

        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
    }

    private int getProgressStep(long cobblestonePowerPerTick) {
        if (cobblestonePowerPerTick <= 0L) {
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

    private long getRequiredCobblestonePowerCapacity() {
        if (this.level == null) {
            return 0L;
        }

        Optional<RecipeHolder<CobblestoneEnchanterRecipe>> recipeHolder = this.getCurrentRecipe();
        if (recipeHolder.isEmpty()) {
            return 0L;
        }

        CobblestoneEnchanterHelper.EvaluationResult evaluation = recipeHolder.get().value().evaluate(this.createRecipeInput(), this.level.registryAccess());
        if (!evaluation.isValid()) {
            return 0L;
        }

        // 高レベルエンチャントでも 1 tick 分以上は蓄えられるようにして、
        // 加速アップグレードが入っているときはその分の同時消費も受け止めます。
        return this.multiplySaturating(evaluation.cobblestonePowerPerTick(), this.getAccelerationMultiplier());
    }

    private long multiplySaturating(long value, int multiplier) {
        if (value <= 0L || multiplier <= 0) {
            return 0L;
        }

        if (value > Long.MAX_VALUE / multiplier) {
            return Long.MAX_VALUE;
        }

        return value * multiplier;
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
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_enchanter");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData enchanterData = new ContainerData() {
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

        return new CobblestoneEnchanterMenu(containerId, playerInventory, this, enchanterData);
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
            return slot == 0 ? CobblestoneEnchanterBlockEntity.this.itemStackHandler.getStackInSlot(this.slotIndex) : ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!this.canInsert || slot != 0) {
                return stack;
            }

            return CobblestoneEnchanterBlockEntity.this.itemStackHandler.insertItem(this.slotIndex, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!this.canExtract || slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneEnchanterBlockEntity.this.itemStackHandler.extractItem(this.slotIndex, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? CobblestoneEnchanterBlockEntity.this.itemStackHandler.getSlotLimit(this.slotIndex) : 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == 0 && CobblestoneEnchanterBlockEntity.this.itemStackHandler.isItemValid(this.slotIndex, stack);
        }
    }
}