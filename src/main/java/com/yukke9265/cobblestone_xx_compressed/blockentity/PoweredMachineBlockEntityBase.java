package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

/*
 * PoweredMachineBlockEntityBase は、丸石電力を消費して加工する機械のうち、
 * 処理の流れが近い機械で共有できる骨格だけを持つ基底です。
 *
 * ここでは「充電する」「レシピを確認する」「進捗を進める」「停止中やレシピ不一致時を整理する」
 * という流れだけを共通化し、実際に何を加工するか、どの条件で進捗を捨てるかは継承先へ残します。
 */
public abstract class PoweredMachineBlockEntityBase<R> extends BaseBlockEntity {
    protected static final int DATA_INDEX_PROGRESS = 0;
    protected static final int DATA_INDEX_MAX_PROGRESS = 1;
    protected static final int DATA_INDEX_STORED_POWER = 2;
    protected static final int DATA_INDEX_STORED_POWER_UPPER = 3;
    protected static final int DATA_INDEX_MAX_STORED_POWER = 4;
    protected static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    protected static final int DATA_INDEX_MACHINE_SPECIFIC_START = 6;

    private int progress;
    private int maxProgress;
    private long storedCobblestonePower;
    private boolean isAvailable = true;

    protected PoweredMachineBlockEntityBase(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
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
        return this.getBaseMaxCobblestonePower() * this.getEnergizedCubeMultiplier();
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
    }

    public long getCurrentCobblestonePowerConsumption() {
        if (!this.isAvailable) {
            return 0L;
        }

        Optional<R> recipeOptional = this.findMatchingRecipe();
        if (recipeOptional.isEmpty()) {
            return 0L;
        }

        R recipe = recipeOptional.get();
        if (!this.canProcessRecipe(recipe)) {
            return 0L;
        }

        long cobblestonePowerPerTick = this.getRecipeCobblestonePowerPerTick(recipe);
        int progressStep = this.getProgressStep(cobblestonePowerPerTick);
        if (progressStep <= 0) {
            return 0L;
        }

        return cobblestonePowerPerTick * progressStep;
    }

    public void reverseIsAvailable() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        this.isAvailable = !this.isAvailable;
        if (!this.isAvailable) {
            // 停止操作は、今処理中だった 1 件分の進捗を明示的に捨てます。
            // ただし機械固有の一時保持データは継承先で必要に応じて回収します。
            this.onStopped();
            this.resetProcessingState();
        }
        this.setChanged();
    }

    protected void onStopped() {
    }

    protected abstract long getBaseMaxCobblestonePower();

    protected abstract int getPowerSlotIndex();

    protected abstract int getOutputSlotIndex();

    protected abstract Optional<R> findMatchingRecipe();

    protected void onRecipeStateChanged(Optional<R> recipeOptional) {
    }

    protected void onUnavailableTick() {
        this.onRecipeStateChanged(Optional.empty());
    }

    protected abstract boolean canProcessRecipe(R recipe);

    protected abstract boolean shouldResetProgress(R recipe);

    protected abstract int getRecipeProcessingTime(R recipe);

    protected abstract long getRecipeCobblestonePowerPerTick(R recipe);

    protected abstract void finishProcessing(R recipe);

    protected final IItemHandler getConfiguredAutomationItemHandler(
        Direction side,
        IItemHandler inputAutomationHandler,
        IItemHandler cobblestoneInputAutomationHandler,
        IItemHandler outputAutomationHandler,
        IItemHandler automationAccessHandler
    ) {
        if (side == null) {
            return automationAccessHandler;
        }

        BlockState currentState = this.getBlockState();
        AutomationSide automationSide = AutomationSide.fromWorldSide(side, currentState);
        AutomationMode automationMode = this.getAutomationMode(automationSide);
        if (automationMode == AutomationMode.INPUT) {
            return inputAutomationHandler;
        }

        if (automationMode == AutomationMode.COBBLESTONE_INPUT) {
            return cobblestoneInputAutomationHandler;
        }

        if (automationMode == AutomationMode.OUTPUT) {
            return outputAutomationHandler;
        }

        if (automationMode == AutomationMode.IN_OUT) {
            return automationAccessHandler;
        }

        return EmptyItemHandler.INSTANCE;
    }

    protected final void pushOutputToConfiguredSides() {
        ItemStackHandler itemStackHandler = this.getItemStackHandler();
        if (itemStackHandler == null) {
            return;
        }

        ItemStack outputStack = itemStackHandler.getStackInSlot(this.getOutputSlotIndex());
        if (outputStack.isEmpty()) {
            return;
        }

        ItemStack remainingOutput = this.pushItemStackToConfiguredSides(outputStack.copy(), AutomationMode.OUTPUT, AutomationMode.IN_OUT);
        itemStackHandler.setStackInSlot(this.getOutputSlotIndex(), remainingOutput);
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

        if (!this.isAvailable) {
            this.onUnavailableTick();
        } else {
            Optional<R> recipeOptional = this.findMatchingRecipe();
            this.onRecipeStateChanged(recipeOptional);

            if (recipeOptional.isPresent()) {
                R recipe = recipeOptional.get();
                this.updateMaxProgress(this.getRecipeProcessingTime(recipe));

                if (this.canProcessRecipe(recipe)) {
                    long cobblestonePowerPerTick = this.getRecipeCobblestonePowerPerTick(recipe);
                    int progressStep = this.getProgressStep(cobblestonePowerPerTick);

                    // 材料や出力が正しくても、電力が足りないだけなら待機扱いにします。
                    // この時は進捗を捨てず、電力が貯まったら続きから再開します。
                    if (progressStep > 0) {
                        this.progress += progressStep;
                        this.storedCobblestonePower -= cobblestonePowerPerTick * progressStep;
                        shouldTurnOn = true;
                        this.setChanged();

                        if (this.progress >= this.maxProgress) {
                            this.finishProcessing(recipe);
                            this.resetProgress();
                        }
                    }
                } else if (this.shouldResetProgress(recipe)) {
                    // ここで進捗を捨てるのは、材料が変わった、出力が詰まった、停止した、のように
                    // 続きから再開すると整合性が崩れるケースだけに限定します。
                    this.resetProgress();
                }
            } else {
                this.resetProcessingState();
            }
        }

        BlockState updatedState = currentState.setValue(OnOffBlock.ON, shouldTurnOn);
        if (updatedState != currentState) {
            currentLevel.setBlock(this.worldPosition, updatedState, 3);
        }

        this.pushOutputToConfiguredSides();
    }

    protected final void resetProgress() {
        if (this.progress != 0) {
            this.progress = 0;
            this.setChanged();
        }
    }

    protected final void resetProcessingState() {
        boolean changed = false;

        if (this.progress != 0) {
            this.progress = 0;
            changed = true;
        }

        if (this.maxProgress != 0) {
            this.maxProgress = 0;
            changed = true;
        }

        if (changed) {
            this.setChanged();
        }
    }

    protected final void updateMaxProgress(int newMaxProgress) {
        if (this.maxProgress != newMaxProgress) {
            this.maxProgress = newMaxProgress;
            this.setChanged();
        }
    }

    protected final int getProgressStep(long cobblestonePowerPerTick) {
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
        ItemStackHandler itemStackHandler = this.getItemStackHandler();
        if (itemStackHandler == null) {
            return 1;
        }

        int accelerationSlotIndex = this.getAccelerationUpgradeSlotIndex();
        if (accelerationSlotIndex < 0 || itemStackHandler.getSlots() <= accelerationSlotIndex) {
            return 1;
        }

        ItemStack accelerationStack = itemStackHandler.getStackInSlot(accelerationSlotIndex);
        int multiplier = MachineUpgradeHelper.getAccelerationMultiplier(accelerationStack);
        if (multiplier <= 0) {
            return 1;
        }

        return multiplier;
    }

    private int getEnergizedCubeMultiplier() {
        ItemStackHandler itemStackHandler = this.getItemStackHandler();
        if (itemStackHandler == null) {
            return 1;
        }

        int energizedCubeSlotIndex = this.getEnergizedCubeUpgradeSlotIndex();
        if (energizedCubeSlotIndex < 0 || itemStackHandler.getSlots() <= energizedCubeSlotIndex) {
            return 1;
        }

        ItemStack energizedCubeStack = itemStackHandler.getStackInSlot(energizedCubeSlotIndex);
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

    private void tryAbsorbCobblestonePower() {
        ItemStackHandler itemStackHandler = this.getItemStackHandler();
        if (itemStackHandler == null) {
            return;
        }

        ItemStack powerStack = itemStackHandler.getStackInSlot(this.getPowerSlotIndex());
        long convertedPower = getCobblestonePowerValueForAutomation(powerStack);
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

    public static boolean isCobblestonePowerItem(ItemStack stack) {
        return getCobblestonePowerValue(stack) > 0;
    }

    public static long getCobblestonePowerValueForAutomation(ItemStack stack) {
        return getCobblestonePowerValue(stack);
    }

    private static long getCobblestonePowerValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        if (stack.is(Items.COBBLESTONE)) {
            return 1;
        }

        if (stack.is(ModBlocks.COMPRESSED_COBBLESTONE.get().asItem())) {
            return 4;
        }

        long currentPower = 32L;
        for (ModBlocks.TierCompressedCobblestone tier : ModBlocks.TierCompressedCobblestone.values()) {
            if (stack.is(tier.getBlock().get().asItem())) {
                return currentPower;
            }

            currentPower *= 8;
        }

        return 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", this.progress);
        tag.putInt("maxProgress", this.maxProgress);
        tag.putLong("storedCobblestonePower", this.storedCobblestonePower);
        tag.putBoolean("isAvailable", this.isAvailable);
        this.saveAutomationModes(tag);

        ItemStackHandler itemStackHandler = this.getItemStackHandler();
        if (itemStackHandler != null) {
            tag.put("inventory", itemStackHandler.serializeNBT(registries));
        }

        this.saveAdditionalPoweredMachineData(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("maxProgress");
        this.storedCobblestonePower = tag.getLong("storedCobblestonePower");
        this.isAvailable = !tag.contains("isAvailable", Tag.TAG_BYTE) || tag.getBoolean("isAvailable");
        this.loadAutomationModes(tag);

        ItemStackHandler itemStackHandler = this.getItemStackHandler();
        if (itemStackHandler instanceof FixedSizeItemStackHandler fixedSizeItemStackHandler && tag.contains("inventory", Tag.TAG_COMPOUND)) {
            fixedSizeItemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        } else if (itemStackHandler != null && tag.contains("inventory", Tag.TAG_COMPOUND)) {
            itemStackHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }

        this.loadAdditionalPoweredMachineData(tag, registries);
    }

    protected void saveAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
    }

    protected void loadAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
    }

    protected final int getAutomationStartDataIndex(int machineSpecificDataCount) {
        return DATA_INDEX_MACHINE_SPECIFIC_START + machineSpecificDataCount;
    }

    protected final int getCurrentPowerRateDataIndex(int machineSpecificDataCount) {
        return this.getAutomationStartDataIndex(machineSpecificDataCount) + AUTOMATION_FACE_COUNT;
    }

    protected final int getCurrentPowerRateUpperDataIndex(int machineSpecificDataCount) {
        return this.getCurrentPowerRateDataIndex(machineSpecificDataCount) + 1;
    }

    protected final int getAutoExportDataIndex(int machineSpecificDataCount) {
        return this.getCurrentPowerRateUpperDataIndex(machineSpecificDataCount) + 1;
    }

    protected final int getPoweredMachineDataCount(int machineSpecificDataCount) {
        return this.getAutoExportDataIndex(machineSpecificDataCount) + 1;
    }

    protected final int getPoweredMachineCommonData(int index, int machineSpecificDataCount) {
        if (index == DATA_INDEX_PROGRESS) {
            return this.progress;
        }

        if (index == DATA_INDEX_MAX_PROGRESS) {
            return this.maxProgress;
        }

        if (index == DATA_INDEX_STORED_POWER) {
            return LongDataHelper.lowerInt(this.storedCobblestonePower);
        }

        if (index == DATA_INDEX_STORED_POWER_UPPER) {
            return LongDataHelper.upperInt(this.storedCobblestonePower);
        }

        if (index == DATA_INDEX_MAX_STORED_POWER) {
            return LongDataHelper.lowerInt(this.getMaxCobblestonePower());
        }

        if (index == DATA_INDEX_MAX_STORED_POWER_UPPER) {
            return LongDataHelper.upperInt(this.getMaxCobblestonePower());
        }

        int automationIndex = index - this.getAutomationStartDataIndex(machineSpecificDataCount);
        if (automationIndex >= 0 && automationIndex < AUTOMATION_FACE_COUNT) {
            return this.getAutomationModeId(automationIndex);
        }

        if (index == this.getCurrentPowerRateDataIndex(machineSpecificDataCount)) {
            return LongDataHelper.lowerInt(this.getCurrentCobblestonePowerConsumption());
        }

        if (index == this.getCurrentPowerRateUpperDataIndex(machineSpecificDataCount)) {
            return LongDataHelper.upperInt(this.getCurrentCobblestonePowerConsumption());
        }

        if (index == this.getAutoExportDataIndex(machineSpecificDataCount)) {
            return this.getAutoExportEnabledId();
        }

        return 0;
    }

    protected final boolean setPoweredMachineCommonData(int index, int value, int machineSpecificDataCount) {
        if (index == DATA_INDEX_PROGRESS) {
            this.progress = value;
            return true;
        }

        if (index == DATA_INDEX_MAX_PROGRESS) {
            this.maxProgress = value;
            return true;
        }

        if (index == DATA_INDEX_STORED_POWER) {
            this.storedCobblestonePower = LongDataHelper.toLong(value, LongDataHelper.upperInt(this.storedCobblestonePower));
            return true;
        }

        if (index == DATA_INDEX_STORED_POWER_UPPER) {
            this.storedCobblestonePower = LongDataHelper.toLong(LongDataHelper.lowerInt(this.storedCobblestonePower), value);
            return true;
        }

        int automationIndex = index - this.getAutomationStartDataIndex(machineSpecificDataCount);
        if (automationIndex >= 0 && automationIndex < AUTOMATION_FACE_COUNT) {
            this.setAutomationMode(automationIndex, AutomationMode.fromId(value));
            return true;
        }

        if (index == this.getAutoExportDataIndex(machineSpecificDataCount)) {
            this.setAutoExportEnabled(value != 0);
            return true;
        }

        return false;
    }
}