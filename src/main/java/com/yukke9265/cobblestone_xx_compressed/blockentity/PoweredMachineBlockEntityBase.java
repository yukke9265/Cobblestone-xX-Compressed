package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
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

        long consumption = cobblestonePowerPerTick * progressStep;
        if (this.progress + progressStep < this.maxProgress) {
            return consumption;
        }

        return consumption + this.getParallelExtraCraftConsumption(recipe, this.storedCobblestonePower - consumption);
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

    /**
     * 完成品を automation 設定済みの面へ搬出します。
     *
     * 標準的な 1 出力機械は、この既定実装をそのまま使用します。
     * 複数出力の機械だけは、出力スロットの順番と公開 mode を明示するために override します。
     */
    protected void pushOutputsToConfiguredSides() {
        this.pushOutputSlotToConfiguredSides(
            this.getOutputSlotIndex(),
            AutomationMode.OUTPUT,
            AutomationMode.IN_OUT
        );
    }

    /**
     * 流体出力を持つ機械が、auto export を行うための hook です。
     *
     * item 出力だけを持つ標準機械では何もしません。流体タンクを持つ継承先だけが
     * override し、流体の種類や容量に応じた搬出処理を実装します。
     */
    protected void onAutoExportFluid() {
    }

    /**
     * 指定した出力スロットを、指定された automation mode の面へ搬出します。
     * 呼び出し順がそのまま搬出順になるため、複数出力機械でも優先順位を保てます。
     */
    protected final void pushOutputSlotToConfiguredSides(int outputSlotIndex, AutomationMode... targetModes) {
        ItemStackHandler itemStackHandler = this.getItemStackHandler();
        if (itemStackHandler == null) {
            return;
        }

        ItemStack outputStack = itemStackHandler.getStackInSlot(outputSlotIndex);
        if (outputStack.isEmpty()) {
            return;
        }

        ItemStack remainingOutput = this.pushItemStackToConfiguredSides(outputStack.copy(), targetModes);
        itemStackHandler.setStackInSlot(outputSlotIndex, remainingOutput);
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
        this.pullInputsFromConfiguredSides();
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
                            this.processParallelExtraCrafts();
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

        this.pushOutputsToConfiguredSides();
        this.onAutoExportFluid();
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

    /**
     * 1 回の progress 完了で行うクラフト回数です。
     *
     * 既定は「本処理 1 回 + Parallel Chip の追加回数」です。
     * Auto Crafter のように既定バッチ×乗算へ変えたい機械だけ override します。
     */
    protected int getCraftsPerCompletion() {
        ItemStackHandler itemStackHandler = this.getItemStackHandler();
        if (itemStackHandler == null) {
            return 1;
        }

        int parallelSlotIndex = this.getParallelUpgradeSlotIndex();
        if (parallelSlotIndex < 0 || itemStackHandler.getSlots() <= parallelSlotIndex) {
            return 1;
        }

        ItemStack parallelStack = itemStackHandler.getStackInSlot(parallelSlotIndex);
        return 1 + MachineUpgradeHelper.getParallelExtraCraftCount(parallelStack);
    }

    /**
     * 1 tick で今のレシピを完了したあと、余り CP で追加完了できる回数を返します。
     *
     * getCraftsPerCompletion() - 1 です。チップが無い通常機械では 0 になります。
     */
    protected final int getParallelExtraCraftCount() {
        return Math.max(0, this.getCraftsPerCompletion() - 1);
    }

    /**
     * 完了直後の余り CP で、同じレシピを追加完了します。
     *
     * 追加回数はチップ倍率、余り CP、入力残数、出力空きの最小です。
     * 1 回分すら入らなくなった時点で止め、使わなかった CP は残します。
     */
    private void processParallelExtraCrafts() {
        int extraLimit = this.getParallelExtraCraftCount();
        if (extraLimit <= 0) {
            return;
        }

        int extraDone = 0;
        while (extraDone < extraLimit) {
            Optional<R> extraRecipeOptional = this.findMatchingRecipe();
            if (extraRecipeOptional.isEmpty()) {
                return;
            }

            R extraRecipe = extraRecipeOptional.get();
            this.updateMaxProgress(this.getRecipeProcessingTime(extraRecipe));
            if (this.maxProgress <= 0 || !this.canProcessRecipe(extraRecipe)) {
                return;
            }

            long cobblestonePowerPerTick = this.getRecipeCobblestonePowerPerTick(extraRecipe);
            long totalCobblestonePower = cobblestonePowerPerTick * (long) this.maxProgress;
            if (totalCobblestonePower <= 0L || this.storedCobblestonePower < totalCobblestonePower) {
                return;
            }

            this.finishProcessing(extraRecipe);
            this.storedCobblestonePower -= totalCobblestonePower;
            extraDone++;
            this.setChanged();
        }
    }

    private long getParallelExtraCraftConsumption(R recipe, long leftoverPower) {
        int extraLimit = this.getParallelExtraCraftCount();
        if (extraLimit <= 0 || leftoverPower <= 0L || this.maxProgress <= 0) {
            return 0L;
        }

        long cobblestonePowerPerTick = this.getRecipeCobblestonePowerPerTick(recipe);
        long totalCobblestonePower = cobblestonePowerPerTick * (long) this.maxProgress;
        if (totalCobblestonePower <= 0L || leftoverPower < totalCobblestonePower) {
            return 0L;
        }

        long extraFromPower = leftoverPower / totalCobblestonePower;
        long extraCount = Math.min(extraLimit, extraFromPower);
        return extraCount * totalCobblestonePower;
    }

    /**
     * energized cube による容量倍率を返します。
     *
     * 通常は getMaxCobblestonePower() 内部だけで使用しますが、Enchanter のように
     * 機械固有の必要容量と比較する機械でも、同じ倍率規則を使えるようにします。
     */
    protected final int getEnergizedCubeMultiplier() {
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

    public static boolean isCobblestonePowerItem(ItemStack stack) {
        return CobblestonePowerHelper.isCobblestonePowerItem(stack);
    }

    public static long getCobblestonePowerValueForAutomation(ItemStack stack) {
        return CobblestonePowerHelper.getFuelValue(stack);
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

    protected final int getAutoInsertDataIndex(int machineSpecificDataCount) {
        return this.getAutoExportDataIndex(machineSpecificDataCount) + 1;
    }

    protected final int getSoundMutedDataIndex(int machineSpecificDataCount) {
        return this.getAutoInsertDataIndex(machineSpecificDataCount) + 1;
    }

    protected final int getPoweredMachineDataCount(int machineSpecificDataCount) {
        return this.getSoundMutedDataIndex(machineSpecificDataCount) + 1;
    }

    /**
     * item と fluid の両方で automation 設定を持つ powered machine 用の同期数です。
     *
     * 機械固有データ、item automation、fluid automation、CP/t、auto export、auto insert、消音 の順で
     * ContainerData を並べます。通常の item 専用機械は既存の overload を使用します。
     */
    protected final int getPoweredMachineDataCount(int machineSpecificDataCount, boolean includesFluidAutomation) {
        return this.getSoundMutedDataIndex(machineSpecificDataCount, includesFluidAutomation) + 1;
    }

    protected final int getPoweredMachineCommonData(int index, int machineSpecificDataCount) {
        return this.getPoweredMachineCommonData(index, machineSpecificDataCount, false);
    }

    /**
     * item と fluid の automation mode を両方同期する powered machine 用の共通データ取得です。
     */
    protected final int getPoweredMachineCommonData(int index, int machineSpecificDataCount, boolean includesFluidAutomation) {
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

        int fluidAutomationStartDataIndex = this.getFluidAutomationStartDataIndex(machineSpecificDataCount, includesFluidAutomation);
        if (includesFluidAutomation
            && index >= fluidAutomationStartDataIndex
            && index < fluidAutomationStartDataIndex + AUTOMATION_FACE_COUNT) {
            return this.getFluidAutomationModeId(index - fluidAutomationStartDataIndex);
        }

        if (index == this.getCurrentPowerRateDataIndex(machineSpecificDataCount, includesFluidAutomation)) {
            return LongDataHelper.lowerInt(this.getCurrentCobblestonePowerConsumption());
        }

        if (index == this.getCurrentPowerRateUpperDataIndex(machineSpecificDataCount, includesFluidAutomation)) {
            return LongDataHelper.upperInt(this.getCurrentCobblestonePowerConsumption());
        }

        if (index == this.getAutoExportDataIndex(machineSpecificDataCount, includesFluidAutomation)) {
            return this.getAutoExportEnabledId();
        }

        if (index == this.getAutoInsertDataIndex(machineSpecificDataCount, includesFluidAutomation)) {
            return this.getAutoInsertEnabledId();
        }

        if (index == this.getSoundMutedDataIndex(machineSpecificDataCount, includesFluidAutomation)) {
            return this.getSoundMutedId();
        }

        return 0;
    }

    protected final boolean setPoweredMachineCommonData(int index, int value, int machineSpecificDataCount) {
        return this.setPoweredMachineCommonData(index, value, machineSpecificDataCount, false);
    }

    /**
     * item と fluid の automation mode を両方同期する powered machine 用の共通データ設定です。
     */
    protected final boolean setPoweredMachineCommonData(
        int index,
        int value,
        int machineSpecificDataCount,
        boolean includesFluidAutomation
    ) {
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

        int fluidAutomationStartDataIndex = this.getFluidAutomationStartDataIndex(machineSpecificDataCount, includesFluidAutomation);
        if (includesFluidAutomation
            && index >= fluidAutomationStartDataIndex
            && index < fluidAutomationStartDataIndex + AUTOMATION_FACE_COUNT) {
            this.setFluidAutomationMode(index - fluidAutomationStartDataIndex, AutomationMode.fromId(value));
            return true;
        }

        if (index == this.getAutoExportDataIndex(machineSpecificDataCount, includesFluidAutomation)) {
            this.setAutoExportEnabled(value != 0);
            return true;
        }

        if (index == this.getAutoInsertDataIndex(machineSpecificDataCount, includesFluidAutomation)) {
            this.setAutoInsertEnabled(value != 0);
            return true;
        }

        if (index == this.getSoundMutedDataIndex(machineSpecificDataCount, includesFluidAutomation)) {
            this.setSoundMuted(value != 0);
            return true;
        }

        return false;
    }

    private int getFluidAutomationStartDataIndex(int machineSpecificDataCount, boolean includesFluidAutomation) {
        if (!includesFluidAutomation) {
            return -1;
        }

        return this.getAutomationStartDataIndex(machineSpecificDataCount) + AUTOMATION_FACE_COUNT;
    }

    private int getCurrentPowerRateDataIndex(int machineSpecificDataCount, boolean includesFluidAutomation) {
        if (!includesFluidAutomation) {
            return this.getCurrentPowerRateDataIndex(machineSpecificDataCount);
        }

        return this.getFluidAutomationStartDataIndex(machineSpecificDataCount, true) + AUTOMATION_FACE_COUNT;
    }

    private int getCurrentPowerRateUpperDataIndex(int machineSpecificDataCount, boolean includesFluidAutomation) {
        return this.getCurrentPowerRateDataIndex(machineSpecificDataCount, includesFluidAutomation) + 1;
    }

    private int getAutoExportDataIndex(int machineSpecificDataCount, boolean includesFluidAutomation) {
        return this.getCurrentPowerRateUpperDataIndex(machineSpecificDataCount, includesFluidAutomation) + 1;
    }

    private int getAutoInsertDataIndex(int machineSpecificDataCount, boolean includesFluidAutomation) {
        return this.getAutoExportDataIndex(machineSpecificDataCount, includesFluidAutomation) + 1;
    }

    private int getSoundMutedDataIndex(int machineSpecificDataCount, boolean includesFluidAutomation) {
        return this.getAutoInsertDataIndex(machineSpecificDataCount, includesFluidAutomation) + 1;
    }
}