package com.yukke9265.cobblestone_xx_compressed.multiblock;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.block.RotatingBlock;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

/**
 * マルチブロック CP 加工機の骨格です。
 * 前提: 処理・バッファ・CP はコアのみ。ポートは capability 委譲だけ。
 * 結果: formed のときだけレシピ処理が進む。
 */
public abstract class MultiblockPoweredMachineBlockEntityBase<R> extends BlockEntity {
    protected static final int DATA_INDEX_PROGRESS = 0;
    protected static final int DATA_INDEX_MAX_PROGRESS = 1;
    protected static final int DATA_INDEX_STORED_POWER = 2;
    protected static final int DATA_INDEX_STORED_POWER_UPPER = 3;
    protected static final int DATA_INDEX_MAX_STORED_POWER = 4;
    protected static final int DATA_INDEX_MAX_STORED_POWER_UPPER = 5;
    protected static final int DATA_INDEX_FORMED = 6;
    protected static final int DATA_INDEX_CURRENT_POWER_RATE = 7;
    protected static final int DATA_INDEX_CURRENT_POWER_RATE_UPPER = 8;
    protected static final int DATA_INDEX_INPUT_COUNT = 9;
    protected static final int DATA_INDEX_INPUT_COUNT_UPPER = 10;
    protected static final int DATA_INDEX_OUTPUT_COUNT = 11;
    protected static final int DATA_INDEX_OUTPUT_COUNT_UPPER = 12;
    protected static final int DATA_INDEX_STRUCTURE_MATCH_MASK = 13;
    protected static final int DATA_INDEX_STRUCTURE_MATCHED_COUNT = 14;
    protected static final int DATA_INDEX_STRUCTURE_REQUIRED_COUNT = 15;
    protected static final int MULTIBLOCK_DATA_COUNT = 16;

    private int progress;
    private int maxProgress;
    private long storedCobblestonePower;
    private boolean isAvailable = true;
    private boolean formed;
    private boolean structureDirty = true;
    private List<BlockPos> memberPositions = List.of();
    private Map<MultiblockCellType, List<BlockPos>> portsByType = Map.of();
    private int accelerationMultiplier = 1;
    private int energizedMultiplier = 1;
    private int parallelExtraCraftCount;
    private int structureMatchMask;
    private int structureMatchedCount;
    private int structureRequiredCount;

    protected MultiblockPoweredMachineBlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract MultiblockPattern getPattern();

    protected abstract long getBaseMaxCobblestonePower();

    protected abstract Optional<R> findMatchingRecipe();

    protected abstract boolean canProcessRecipe(R recipe);

    protected abstract boolean shouldResetProgress(R recipe);

    protected abstract int getRecipeProcessingTime(R recipe);

    protected abstract long getRecipeCobblestonePowerPerTick(R recipe);

    protected abstract void finishProcessing(R recipe);

    protected abstract VirtualItemBuffer getInputBuffer();

    protected abstract VirtualItemBuffer getOutputBuffer();

    public boolean isFormed() {
        return this.formed;
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
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

    public void setStoredCobblestonePower(long storedCobblestonePower) {
        this.storedCobblestonePower = Math.max(0L, storedCobblestonePower);
    }

    public long getMaxCobblestonePower() {
        return this.getBaseMaxCobblestonePower() * this.energizedMultiplier;
    }

    public void markStructureDirty() {
        this.structureDirty = true;
    }

    public int getStructureMatchMask() {
        return this.structureMatchMask;
    }

    public int getStructureMatchedCount() {
        return this.structureMatchedCount;
    }

    public int getStructureRequiredCount() {
        return this.structureRequiredCount;
    }

    public boolean isStructureCellMatched(int index) {
        return MultiblockStructureStatus.isMatched(this.structureMatchMask, index);
    }

    public void reverseIsAvailable() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        this.isAvailable = !this.isAvailable;
        if (!this.isAvailable) {
            this.resetProcessingState();
        }
        this.setChanged();
    }

    public IItemHandler getPortItemHandler(MultiblockCellType portType) {
        if (!this.formed) {
            return EmptyItemHandler.INSTANCE;
        }

        return switch (portType) {
            case ITEM_IN -> new VirtualItemHandler(this.getInputBuffer(), true, false, this::setChanged);
            case ITEM_OUT -> new VirtualItemHandler(this.getOutputBuffer(), false, true, this::setChanged);
            case COBBLE_IN -> new CobblePowerPortHandler(
                this::getStoredCobblestonePower,
                this::getMaxCobblestonePower,
                this::setStoredCobblestonePower,
                this::setChanged
            );
            default -> EmptyItemHandler.INSTANCE;
        };
    }

    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        if (this.structureDirty) {
            this.revalidateStructure();
        } else if (!this.formed && this.level.getGameTime() % 5L == 0L) {
            // 未完成時は短周期で再検証し、GUI の不足表示を追従させる。
            this.revalidateStructure();
        }

        boolean shouldTurnOn = false;
        this.clampStoredCobblestonePower();

        if (!this.formed || !this.isAvailable) {
            if (!this.formed || !this.isAvailable) {
                this.resetProcessingState();
            }
        } else {
            Optional<R> recipeOptional = this.findMatchingRecipe();
            if (recipeOptional.isPresent()) {
                R recipe = recipeOptional.get();
                this.updateMaxProgress(this.getRecipeProcessingTime(recipe));

                if (this.canProcessRecipe(recipe)) {
                    long cobblestonePowerPerTick = this.getRecipeCobblestonePowerPerTick(recipe);
                    int progressStep = this.getProgressStep(cobblestonePowerPerTick);
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
                    this.resetProgress();
                }
            } else {
                this.resetProcessingState();
            }
        }

        BlockState currentState = this.getBlockState();
        if (currentState.hasProperty(OnOffBlock.ON)) {
            BlockState updatedState = currentState.setValue(OnOffBlock.ON, shouldTurnOn);
            if (updatedState != currentState) {
                this.level.setBlock(this.worldPosition, updatedState, 3);
            }
        }
    }

    private void revalidateStructure() {
        this.structureDirty = false;
        Level level = this.level;
        if (level == null) {
            return;
        }

        Direction facing = Direction.NORTH;
        BlockState state = this.getBlockState();
        if (state.hasProperty(RotatingBlock.FACING)) {
            facing = state.getValue(RotatingBlock.FACING);
        }

        MultiblockFormIndex index = MultiblockFormIndex.get(level);
        // 旧メンバの完成見た目を先に戻してからインデックスを外す。
        MultiblockFormedHelper.setFormed(level, this.memberPositions, false);
        index.unregisterMembers(this.memberPositions);

        // GUI 用に、完成可否とは別にセルごとの一致状況を必ず更新する。
        MultiblockStructureStatus.InspectionResult inspection =
            MultiblockStructureStatus.inspect(this.getPattern(), level, this.worldPosition, facing);
        this.structureMatchMask = inspection.matchMask();
        this.structureMatchedCount = inspection.matchedCount();
        this.structureRequiredCount = inspection.requiredCount();

        Optional<MultiblockPattern.ValidationResult> validation =
            this.getPattern().tryValidate(level, this.worldPosition, facing);

        if (validation.isEmpty()) {
            this.formed = false;
            this.memberPositions = List.of();
            this.portsByType = Map.of();
            this.accelerationMultiplier = 1;
            this.energizedMultiplier = 1;
            this.parallelExtraCraftCount = 0;
            this.resetProcessingState();
            this.setChanged();
            return;
        }

        MultiblockPattern.ValidationResult result = validation.get();
        this.formed = true;
        this.memberPositions = result.memberPositions();
        this.portsByType = result.portsByType();
        this.accelerationMultiplier = result.accelerationMultiplier();
        this.energizedMultiplier = result.energizedMultiplier();
        this.parallelExtraCraftCount = result.parallelExtraCraftCount();
        index.registerMembers(this.worldPosition, this.memberPositions);
        MultiblockFormedHelper.setFormed(level, this.memberPositions, true);
        this.setChanged();
    }

    private void clampStoredCobblestonePower() {
        long max = this.getMaxCobblestonePower();
        if (this.storedCobblestonePower > max) {
            this.storedCobblestonePower = max;
            this.setChanged();
        }
    }

    private int getProgressStep(long cobblestonePowerPerTick) {
        if (cobblestonePowerPerTick <= 0L) {
            return 0;
        }

        int remainingProgress = this.maxProgress - this.progress;
        if (remainingProgress <= 0) {
            return 0;
        }

        int maxProgressStep = Math.min(this.accelerationMultiplier, remainingProgress);
        long maxPowerStep = this.storedCobblestonePower / cobblestonePowerPerTick;
        return Math.min(maxProgressStep, (int) Math.min(Integer.MAX_VALUE, maxPowerStep));
    }

    private void processParallelExtraCrafts() {
        int extraLimit = this.parallelExtraCraftCount;
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

    public long getCurrentCobblestonePowerConsumption() {
        if (!this.formed || !this.isAvailable) {
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

    public int getMultiblockCommonData(int index) {
        return switch (index) {
            case DATA_INDEX_PROGRESS -> this.progress;
            case DATA_INDEX_MAX_PROGRESS -> this.maxProgress;
            case DATA_INDEX_STORED_POWER -> LongDataHelper.lowerInt(this.storedCobblestonePower);
            case DATA_INDEX_STORED_POWER_UPPER -> LongDataHelper.upperInt(this.storedCobblestonePower);
            case DATA_INDEX_MAX_STORED_POWER -> LongDataHelper.lowerInt(this.getMaxCobblestonePower());
            case DATA_INDEX_MAX_STORED_POWER_UPPER -> LongDataHelper.upperInt(this.getMaxCobblestonePower());
            case DATA_INDEX_FORMED -> this.formed ? 1 : 0;
            case DATA_INDEX_CURRENT_POWER_RATE -> LongDataHelper.lowerInt(this.getCurrentCobblestonePowerConsumption());
            case DATA_INDEX_CURRENT_POWER_RATE_UPPER -> LongDataHelper.upperInt(this.getCurrentCobblestonePowerConsumption());
            case DATA_INDEX_INPUT_COUNT -> LongDataHelper.lowerInt(this.getInputBuffer().getCount());
            case DATA_INDEX_INPUT_COUNT_UPPER -> LongDataHelper.upperInt(this.getInputBuffer().getCount());
            case DATA_INDEX_OUTPUT_COUNT -> LongDataHelper.lowerInt(this.getOutputBuffer().getCount());
            case DATA_INDEX_OUTPUT_COUNT_UPPER -> LongDataHelper.upperInt(this.getOutputBuffer().getCount());
            case DATA_INDEX_STRUCTURE_MATCH_MASK -> this.structureMatchMask;
            case DATA_INDEX_STRUCTURE_MATCHED_COUNT -> this.structureMatchedCount;
            case DATA_INDEX_STRUCTURE_REQUIRED_COUNT -> this.structureRequiredCount;
            default -> 0;
        };
    }

    public void setMultiblockCommonData(int index, int value) {
        switch (index) {
            case DATA_INDEX_PROGRESS -> this.progress = value;
            case DATA_INDEX_MAX_PROGRESS -> this.maxProgress = value;
            case DATA_INDEX_STORED_POWER -> this.storedCobblestonePower =
                LongDataHelper.toLong(value, LongDataHelper.upperInt(this.storedCobblestonePower));
            case DATA_INDEX_STORED_POWER_UPPER -> this.storedCobblestonePower =
                LongDataHelper.toLong(LongDataHelper.lowerInt(this.storedCobblestonePower), value);
            default -> {
            }
        }
    }

    public int getMultiblockDataCount() {
        return MULTIBLOCK_DATA_COUNT;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", this.progress);
        tag.putInt("maxProgress", this.maxProgress);
        tag.putLong("storedCobblestonePower", this.storedCobblestonePower);
        tag.putBoolean("isAvailable", this.isAvailable);
        tag.putBoolean("formed", this.formed);
        tag.putInt("accelerationMultiplier", this.accelerationMultiplier);
        tag.putInt("energizedMultiplier", this.energizedMultiplier);
        tag.putInt("parallelExtraCraftCount", this.parallelExtraCraftCount);

        // メンバ座標はロード後に再検証するため保存しない。
        CompoundTag inputTag = new CompoundTag();
        this.getInputBuffer().save(inputTag, registries);
        tag.put("inputBuffer", inputTag);

        CompoundTag outputTag = new CompoundTag();
        this.getOutputBuffer().save(outputTag, registries);
        tag.put("outputBuffer", outputTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("maxProgress");
        this.storedCobblestonePower = tag.getLong("storedCobblestonePower");
        this.isAvailable = !tag.contains("isAvailable", Tag.TAG_BYTE) || tag.getBoolean("isAvailable");
        this.formed = tag.getBoolean("formed");
        this.accelerationMultiplier = Math.max(1, tag.getInt("accelerationMultiplier"));
        this.energizedMultiplier = Math.max(1, tag.getInt("energizedMultiplier"));
        this.parallelExtraCraftCount = Math.max(0, tag.getInt("parallelExtraCraftCount"));
        this.memberPositions = List.of();
        this.portsByType = Map.of();
        this.structureDirty = true;

        if (tag.contains("inputBuffer", Tag.TAG_COMPOUND)) {
            this.getInputBuffer().load(tag.getCompound("inputBuffer"), registries);
        }
        if (tag.contains("outputBuffer", Tag.TAG_COMPOUND)) {
            this.getOutputBuffer().load(tag.getCompound("outputBuffer"), registries);
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && !this.level.isClientSide) {
            MultiblockFormIndex.get(this.level).unregisterMembers(this.memberPositions);
        }
        super.setRemoved();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.structureDirty = true;
    }
}
