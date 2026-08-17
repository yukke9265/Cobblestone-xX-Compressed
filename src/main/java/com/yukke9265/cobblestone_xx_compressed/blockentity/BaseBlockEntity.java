package com.yukke9265.cobblestone_xx_compressed.blockentity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

@SuppressWarnings("null")
public class BaseBlockEntity extends BlockEntity {
    public static final int AUTOMATION_FACE_COUNT = AutomationSide.values().length;

    private final AutomationMode[] automationModes = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.DISABLED,
        AutomationMode.DISABLED,
        AutomationMode.DISABLED,
        AutomationMode.DISABLED,
        AutomationMode.DISABLED
    };
    private final AutomationMode[] fluidAutomationModes = new AutomationMode[] {
        AutomationMode.DISABLED,
        AutomationMode.DISABLED,
        AutomationMode.DISABLED,
        AutomationMode.DISABLED,
        AutomationMode.DISABLED,
        AutomationMode.DISABLED
    };
    private boolean autoExportEnabled;
    private boolean autoInsertEnabled;

    public BaseBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public boolean isAutoExportEnabled() {
        return this.autoExportEnabled;
    }

    public int getAutoExportEnabledId() {
        return this.autoExportEnabled ? 1 : 0;
    }

    public void setAutoExportEnabled(boolean autoExportEnabled) {
        this.autoExportEnabled = autoExportEnabled;
        this.setChanged();
    }

    public void toggleAutoExportEnabled() {
        this.setAutoExportEnabled(!this.autoExportEnabled);
    }

    public boolean isAutoInsertEnabled() {
        return this.autoInsertEnabled;
    }

    public int getAutoInsertEnabledId() {
        return this.autoInsertEnabled ? 1 : 0;
    }

    public void setAutoInsertEnabled(boolean autoInsertEnabled) {
        this.autoInsertEnabled = autoInsertEnabled;
        this.setChanged();
    }

    public void toggleAutoInsertEnabled() {
        this.setAutoInsertEnabled(!this.autoInsertEnabled);
    }

    public AutomationMode getAutomationMode(int index) {
        if (index < 0 || index >= AUTOMATION_FACE_COUNT) {
            throw new IllegalArgumentException("不明な自動搬出入設定 index です: " + index);
        }

        return this.automationModes[index];
    }

    public int getAutomationModeId(int index) {
        return this.getAutomationMode(index).getId();
    }

    public AutomationMode getAutomationMode(AutomationSide automationSide) {
        return this.getAutomationMode(automationSide.getIndex());
    }

    public int getAutomationModeId(AutomationSide automationSide) {
        return this.getAutomationMode(automationSide).getId();
    }

    public AutomationMode getFluidAutomationMode(int index) {
        if (index < 0 || index >= AUTOMATION_FACE_COUNT) {
            throw new IllegalArgumentException("不明な液体自動搬出入設定 index です: " + index);
        }

        return this.fluidAutomationModes[index];
    }

    public int getFluidAutomationModeId(int index) {
        return this.getFluidAutomationMode(index).getId();
    }

    public AutomationMode getFluidAutomationMode(AutomationSide automationSide) {
        return this.getFluidAutomationMode(automationSide.getIndex());
    }

    public int getFluidAutomationModeId(AutomationSide automationSide) {
        return this.getFluidAutomationMode(automationSide).getId();
    }

    public void cycleAutomationMode(int index) {
        AutomationMode nextMode = this.getAutomationMode(index).next();
        this.setAutomationMode(index, nextMode);
    }

    public void cycleReverseAutomationMode(int index) {
        AutomationMode previousMode = this.getAutomationMode(index).previous();
        this.setAutomationMode(index, previousMode);
    }

    public void cycleAutomationMode(int index, AutomationMode[] allowedModes) {
        this.setAutomationMode(index, this.getShiftedAllowedAutomationMode(this.getAutomationMode(index), allowedModes, 1));
    }

    public void cycleReverseAutomationMode(int index, AutomationMode[] allowedModes) {
        this.setAutomationMode(index, this.getShiftedAllowedAutomationMode(this.getAutomationMode(index), allowedModes, -1));
    }

    public void cycleFluidAutomationMode(int index) {
        AutomationMode nextMode = this.getFluidAutomationMode(index).next();
        this.setFluidAutomationMode(index, nextMode);
    }

    public void cycleReverseFluidAutomationMode(int index) {
        AutomationMode previousMode = this.getFluidAutomationMode(index).previous();
        this.setFluidAutomationMode(index, previousMode);
    }

    public void cycleFluidAutomationMode(int index, AutomationMode[] allowedModes) {
        this.setFluidAutomationMode(index, this.getShiftedAllowedAutomationMode(this.getFluidAutomationMode(index), allowedModes, 1));
    }

    public void cycleReverseFluidAutomationMode(int index, AutomationMode[] allowedModes) {
        this.setFluidAutomationMode(index, this.getShiftedAllowedAutomationMode(this.getFluidAutomationMode(index), allowedModes, -1));
    }

    private AutomationMode getShiftedAllowedAutomationMode(AutomationMode currentMode, AutomationMode[] allowedModes, int direction) {
        if (allowedModes == null || allowedModes.length == 0) {
            throw new IllegalArgumentException("allowedModes が空です");
        }

        int currentAllowedIndex = 0;

        for (int modeIndex = 0; modeIndex < allowedModes.length; modeIndex++) {
            if (allowedModes[modeIndex] == currentMode) {
                currentAllowedIndex = modeIndex;
                break;
            }
        }

        int nextAllowedIndex = (currentAllowedIndex + direction + allowedModes.length) % allowedModes.length;
        return allowedModes[nextAllowedIndex];
    }

    public void setAutomationMode(int index, AutomationMode mode) {
        if (index < 0 || index >= AUTOMATION_FACE_COUNT) {
            throw new IllegalArgumentException("不明な自動搬出入設定 index です: " + index);
        }

        this.automationModes[index] = mode;

        // 面設定を変えると外部へ公開する capability の中身も変わるため、
        // 既に隣接機械やパイプが握っているキャッシュを破棄して再取得させます。
        this.invalidateAutomationCapabilities();
        this.setChanged();
    }

    public void setFluidAutomationMode(int index, AutomationMode mode) {
        if (index < 0 || index >= AUTOMATION_FACE_COUNT) {
            throw new IllegalArgumentException("不明な液体自動搬出入設定 index です: " + index);
        }

        this.fluidAutomationModes[index] = mode;

        // 液体側も同様に、面設定の変更を外部 capability キャッシュへ即時反映します。
        this.invalidateAutomationCapabilities();
        this.setChanged();
    }

    private void invalidateAutomationCapabilities() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        currentLevel.invalidateCapabilities(this.worldPosition);
    }

    protected void saveAutomationModes(CompoundTag tag) {
        for (AutomationSide automationSide : AutomationSide.values()) {
            String key = automationSide.getSerializedName() + "AutomationMode";
            tag.putInt(key, this.getAutomationModeId(automationSide));

            String fluidKey = automationSide.getSerializedName() + "FluidAutomationMode";
            tag.putInt(fluidKey, this.getFluidAutomationModeId(automationSide));
        }

        tag.putBoolean("autoExportEnabled", this.autoExportEnabled);
        tag.putBoolean("autoInsertEnabled", this.autoInsertEnabled);
    }

    protected void loadAutomationModes(CompoundTag tag) {
        for (AutomationSide automationSide : AutomationSide.values()) {
            String key = automationSide.getSerializedName() + "AutomationMode";
            this.setAutomationMode(automationSide.getIndex(), AutomationMode.fromId(tag.getInt(key)));

            String fluidKey = automationSide.getSerializedName() + "FluidAutomationMode";
            this.setFluidAutomationMode(automationSide.getIndex(), AutomationMode.fromId(tag.getInt(fluidKey)));
        }

        this.setAutoExportEnabled(tag.getBoolean("autoExportEnabled"));
        this.setAutoInsertEnabled(tag.getBoolean("autoInsertEnabled"));
    }

    public CompoundTag createAutomationCopyData() {
        CompoundTag tag = new CompoundTag();
        this.saveAutomationModes(tag);
        return tag;
    }

    public void applyAutomationCopyData(CompoundTag tag) {
        this.loadAutomationModes(tag);
        this.setChanged();
    }

    public ItemStackHandler getItemStackHandler() {
        return null;
    }

    protected int getAccelerationUpgradeSlotIndex() {
        return this.getUpgradeSlotIndexByFieldName("ACCELERATION_SLOT_INDEX");
    }

    protected int getEnergizedCubeUpgradeSlotIndex() {
        return this.getUpgradeSlotIndexByFieldName("ENERGIZED_CUBE_SLOT_INDEX");
    }

    protected int getParallelUpgradeSlotIndex() {
        return this.getUpgradeSlotIndexByFieldName("PARALLEL_SLOT_INDEX");
    }

    private int getUpgradeSlotIndexByFieldName(String fieldName) {
        try {
            Object slotIndexValue = this.getClass().getField(fieldName).get(null);
            if (slotIndexValue instanceof Integer slotIndex) {
                return slotIndex.intValue();
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // upgrade slot を持たない機械もあるため、その場合は未対応として扱います。
        }

        return -1;
    }

    private boolean canInstallUpgradeItemInSlot(ItemStackHandler itemStackHandler, ItemStack stack, int slot) {
        if (slot < 0 || slot >= itemStackHandler.getSlots()) {
            return false;
        }

        if (!itemStackHandler.isItemValid(slot, stack)) {
            return false;
        }

        if (!itemStackHandler.getStackInSlot(slot).isEmpty()) {
            return false;
        }

        return itemStackHandler.getSlotLimit(slot) > 0;
    }

    private boolean tryInstallUpgradeItemInSlot(ItemStackHandler itemStackHandler, ItemStack stack, int slot, boolean simulate) {
        if (!this.canInstallUpgradeItemInSlot(itemStackHandler, stack, slot)) {
            return false;
        }

        ItemStack singleStack = stack.copyWithCount(1);
        ItemStack remainingStack = itemStackHandler.insertItem(slot, singleStack, simulate);
        if (!remainingStack.isEmpty()) {
            return false;
        }

        if (!simulate) {
            this.setChanged();
        }
        return true;
    }

    public boolean canInstallUpgradeItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (!MachineUpgradeHelper.isAccelerationChip(stack)
            && !MachineUpgradeHelper.isEnergizedCube(stack)
            && !MachineUpgradeHelper.isParallelChip(stack)) {
            return false;
        }

        ItemStackHandler itemStackHandler = this.getItemStackHandler();
        if (itemStackHandler == null) {
            return false;
        }

        if (MachineUpgradeHelper.isAccelerationChip(stack)
            && this.canInstallUpgradeItemInSlot(itemStackHandler, stack, this.getAccelerationUpgradeSlotIndex())) {
            return true;
        }

        if (MachineUpgradeHelper.isEnergizedCube(stack)
            && this.canInstallUpgradeItemInSlot(itemStackHandler, stack, this.getEnergizedCubeUpgradeSlotIndex())) {
            return true;
        }

        if (MachineUpgradeHelper.isParallelChip(stack)
            && this.canInstallUpgradeItemInSlot(itemStackHandler, stack, this.getParallelUpgradeSlotIndex())) {
            return true;
        }

        return false;
    }

    public boolean installUpgradeItem(ItemStack stack, boolean simulate) {
        if (!this.canInstallUpgradeItem(stack)) {
            return false;
        }

        ItemStackHandler itemStackHandler = this.getItemStackHandler();
        if (itemStackHandler == null) {
            return false;
        }

        if (MachineUpgradeHelper.isAccelerationChip(stack)
            && this.tryInstallUpgradeItemInSlot(itemStackHandler, stack, this.getAccelerationUpgradeSlotIndex(), simulate)) {
            return true;
        }

        if (MachineUpgradeHelper.isEnergizedCube(stack)
            && this.tryInstallUpgradeItemInSlot(itemStackHandler, stack, this.getEnergizedCubeUpgradeSlotIndex(), simulate)) {
            return true;
        }

        if (MachineUpgradeHelper.isParallelChip(stack)
            && this.tryInstallUpgradeItemInSlot(itemStackHandler, stack, this.getParallelUpgradeSlotIndex(), simulate)) {
            return true;
        }

        return false;
    }

    protected ItemStack pushItemStackToConfiguredSides(ItemStack stack, AutomationMode... targetModes) {
        if (!this.autoExportEnabled || stack.isEmpty()) {
            return stack;
        }

        Level currentLevel = this.level;
        if (currentLevel == null) {
            return stack;
        }

        BlockState currentState = this.getBlockState();
        ItemStack remainingStack = stack;

        for (Direction direction : Direction.values()) {
            if (remainingStack.isEmpty()) {
                break;
            }

            AutomationSide automationSide = AutomationSide.fromWorldSide(direction, currentState);
            AutomationMode currentMode = this.getAutomationMode(automationSide);
            if (!this.matchesAnyAutomationMode(currentMode, targetModes)) {
                continue;
            }

            IItemHandler targetHandler = currentLevel.getCapability(
                Capabilities.ItemHandler.BLOCK,
                this.worldPosition.relative(direction),
                direction.getOpposite()
            );
            if (targetHandler == null) {
                continue;
            }

            remainingStack = ItemHandlerHelper.insertItem(targetHandler, remainingStack, false);
        }

        return remainingStack;
    }

    protected FluidStack pushFluidToConfiguredSides(FluidStack stack, AutomationMode... targetModes) {
        if (!this.autoExportEnabled || stack.isEmpty()) {
            return stack;
        }

        Level currentLevel = this.level;
        if (currentLevel == null) {
            return stack;
        }

        BlockState currentState = this.getBlockState();
        FluidStack remainingStack = stack.copy();

        for (Direction direction : Direction.values()) {
            if (remainingStack.isEmpty()) {
                break;
            }

            AutomationSide automationSide = AutomationSide.fromWorldSide(direction, currentState);
            AutomationMode currentMode = this.getFluidAutomationMode(automationSide);
            if (!this.matchesAnyAutomationMode(currentMode, targetModes)) {
                continue;
            }

            IFluidHandler targetHandler = currentLevel.getCapability(
                Capabilities.FluidHandler.BLOCK,
                this.worldPosition.relative(direction),
                direction.getOpposite()
            );
            if (targetHandler == null) {
                continue;
            }

            int filledAmount = targetHandler.fill(remainingStack, IFluidHandler.FluidAction.EXECUTE);
            if (filledAmount <= 0) {
                continue;
            }

            remainingStack.shrink(filledAmount);
        }

        return remainingStack;
    }

    /**
     * 面ごとの item 公開 handler です。
     * 未対応の機械は空 handler を返し、自動搬入は何もしません。
     */
    public IItemHandler getAutomationItemHandler(@Nullable Direction side) {
        return EmptyItemHandler.INSTANCE;
    }

    /**
     * 面ごとの fluid 公開 handler です。
     * 液体を持たない機械は null を返し、自動搬入は何もしません。
     */
    @Nullable
    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        return null;
    }

    /**
     * INPUT 系の面から、隣ブロックの item / fluid を取り込みます。
     * IN_OUT 面は吸い込み対象にしません。
     */
    protected final void pullInputsFromConfiguredSides() {
        if (!this.autoInsertEnabled) {
            return;
        }

        this.pullItemsFromConfiguredSides();
        this.pullFluidsFromConfiguredSides();
    }

    private void pullItemsFromConfiguredSides() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        BlockState currentState = this.getBlockState();
        for (Direction direction : Direction.values()) {
            AutomationSide automationSide = AutomationSide.fromWorldSide(direction, currentState);
            if (!this.getAutomationMode(automationSide).isInputMode()) {
                continue;
            }

            IItemHandler ownHandler = this.getAutomationItemHandler(direction);
            if (ownHandler == null || ownHandler.getSlots() <= 0) {
                continue;
            }

            IItemHandler neighborHandler = currentLevel.getCapability(
                Capabilities.ItemHandler.BLOCK,
                this.worldPosition.relative(direction),
                direction.getOpposite()
            );
            if (neighborHandler == null) {
                continue;
            }

            this.transferItemsFromNeighbor(neighborHandler, ownHandler);
        }
    }

    private void pullFluidsFromConfiguredSides() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        BlockState currentState = this.getBlockState();
        for (Direction direction : Direction.values()) {
            AutomationSide automationSide = AutomationSide.fromWorldSide(direction, currentState);
            if (!this.getFluidAutomationMode(automationSide).isInputMode()) {
                continue;
            }

            IFluidHandler ownHandler = this.getFluidHandler(direction);
            if (ownHandler == null) {
                continue;
            }

            IFluidHandler neighborHandler = currentLevel.getCapability(
                Capabilities.FluidHandler.BLOCK,
                this.worldPosition.relative(direction),
                direction.getOpposite()
            );
            if (neighborHandler == null) {
                continue;
            }

            this.transferFluidFromNeighbor(neighborHandler, ownHandler);
        }
    }

    private void transferItemsFromNeighbor(IItemHandler sourceHandler, IItemHandler destinationHandler) {
        for (int slot = 0; slot < sourceHandler.getSlots(); slot++) {
            ItemStack simulatedExtract = sourceHandler.extractItem(slot, Integer.MAX_VALUE, true);
            if (simulatedExtract.isEmpty()) {
                continue;
            }

            ItemStack simulatedRemaining = this.insertIntoOwnHandler(destinationHandler, simulatedExtract, true);
            int insertableCount = simulatedExtract.getCount() - simulatedRemaining.getCount();
            if (insertableCount <= 0) {
                continue;
            }

            // 他機械が同じ tick で先に吸っている場合は、ここで量を減らして取りすぎない。
            ItemStack stillAvailable = sourceHandler.extractItem(slot, insertableCount, true);
            int extractCount = Math.min(insertableCount, stillAvailable.getCount());
            if (extractCount <= 0) {
                continue;
            }

            ItemStack recheckRemaining = this.insertIntoOwnHandler(
                destinationHandler,
                stillAvailable.copyWithCount(extractCount),
                true
            );
            extractCount = extractCount - recheckRemaining.getCount();
            if (extractCount <= 0) {
                continue;
            }

            ItemStack extractedStack = sourceHandler.extractItem(slot, extractCount, false);
            if (extractedStack.isEmpty()) {
                continue;
            }

            ItemStack leftoverStack = this.insertIntoOwnHandler(destinationHandler, extractedStack, false);
            if (leftoverStack.isEmpty()) {
                continue;
            }

            leftoverStack = this.putItemBackToSource(sourceHandler, slot, leftoverStack);
            if (!leftoverStack.isEmpty()) {
                this.dropLeftoverItem(leftoverStack);
            }
        }
    }

    /**
     * 自分の入力 handler へ入れます。
     *
     * Mixer などの sequential handler は、どの slot へ insert しても全入力枠を順に埋めます。
     * ItemHandlerHelper で全 slot を回すと、空き容量を二重に見積もって取りすぎます。
     * そのため、1 回の投入で全体を埋める handler は slot 0 だけを使います。
     */
    private ItemStack insertIntoOwnHandler(IItemHandler destinationHandler, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || destinationHandler.getSlots() <= 0) {
            return stack;
        }

        ItemStack remainingAfterFirstSlot = destinationHandler.insertItem(0, stack, simulate);
        if (remainingAfterFirstSlot.isEmpty() || destinationHandler.getSlots() == 1) {
            return remainingAfterFirstSlot;
        }

        ItemStack remainingIfSecondSlot = destinationHandler.insertItem(1, stack, simulate);
        boolean firstSlotFillsAllInputs = remainingAfterFirstSlot.getCount() == remainingIfSecondSlot.getCount()
            && ItemStack.isSameItemSameComponents(remainingAfterFirstSlot, remainingIfSecondSlot);
        if (firstSlotFillsAllInputs) {
            return remainingAfterFirstSlot;
        }

        ItemStack remainingStack = remainingAfterFirstSlot;
        for (int slot = 1; slot < destinationHandler.getSlots(); slot++) {
            if (remainingStack.isEmpty()) {
                break;
            }

            remainingStack = destinationHandler.insertItem(slot, remainingStack, simulate);
        }

        return remainingStack;
    }

    private ItemStack putItemBackToSource(IItemHandler sourceHandler, int originalSlot, ItemStack leftoverStack) {
        ItemStack remainingStack = sourceHandler.insertItem(originalSlot, leftoverStack, false);
        if (remainingStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return ItemHandlerHelper.insertItem(sourceHandler, remainingStack, false);
    }

    private void dropLeftoverItem(ItemStack leftoverStack) {
        Level currentLevel = this.level;
        if (currentLevel == null || leftoverStack.isEmpty()) {
            return;
        }

        Containers.dropItemStack(
            currentLevel,
            this.worldPosition.getX() + 0.5D,
            this.worldPosition.getY() + 0.5D,
            this.worldPosition.getZ() + 0.5D,
            leftoverStack
        );
    }

    private void transferFluidFromNeighbor(IFluidHandler sourceHandler, IFluidHandler destinationHandler) {
        FluidStack simulatedDrain = sourceHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedDrain.isEmpty()) {
            return;
        }

        int simulatedFillAmount = destinationHandler.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedFillAmount <= 0) {
            return;
        }

        FluidStack stillAvailable = sourceHandler.drain(simulatedFillAmount, IFluidHandler.FluidAction.SIMULATE);
        int drainAmount = Math.min(simulatedFillAmount, stillAvailable.getAmount());
        if (drainAmount <= 0) {
            return;
        }

        drainAmount = destinationHandler.fill(stillAvailable.copyWithAmount(drainAmount), IFluidHandler.FluidAction.SIMULATE);
        if (drainAmount <= 0) {
            return;
        }

        FluidStack drainedFluid = sourceHandler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
        if (drainedFluid.isEmpty()) {
            return;
        }

        int filledAmount = destinationHandler.fill(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
        int leftoverAmount = drainedFluid.getAmount() - filledAmount;
        if (leftoverAmount <= 0) {
            return;
        }

        FluidStack leftoverFluid = drainedFluid.copyWithAmount(leftoverAmount);
        int restoredAmount = sourceHandler.fill(leftoverFluid, IFluidHandler.FluidAction.EXECUTE);
        leftoverFluid.shrink(restoredAmount);
        if (leftoverFluid.isEmpty()) {
            return;
        }

        int filledAgainAmount = destinationHandler.fill(leftoverFluid, IFluidHandler.FluidAction.EXECUTE);
        leftoverFluid.shrink(filledAgainAmount);
    }

    private boolean matchesAnyAutomationMode(AutomationMode currentMode, AutomationMode[] targetModes) {
        for (AutomationMode targetMode : targetModes) {
            if (currentMode == targetMode) {
                return true;
            }
        }

        return false;
    }

    public void tick() {
        // 共通の基底BlockEntityです。
        // デフォルトでは毎ティック何もしません。
    }
}