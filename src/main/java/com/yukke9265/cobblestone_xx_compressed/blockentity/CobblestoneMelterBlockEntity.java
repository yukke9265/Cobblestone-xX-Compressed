package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMelterMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMelterRecipe;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
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
 * item を fluid に変換する powered machine です。
 *
 * CP、progress、保存、同期、停止処理は PoweredMachineBlockEntityBase に統一し、
 * 流体タンク、fluid capability、container 操作、流体搬出だけをこのクラスに残します。
 */
@SuppressWarnings("null")
public class CobblestoneMelterBlockEntity extends PoweredMachineBlockEntityBase<CobblestoneMelterRecipe> implements MenuProvider {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int POWER_SLOT_INDEX = 1;
    public static final int ACCELERATION_SLOT_INDEX = 2;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 3;
    public static final int PARALLEL_SLOT_INDEX = 4;
    public static final long MAX_COBBLESTONE_POWER = 4096000L;
    public static final long MAX_FLUID_AMOUNT = 64_000L;

    private static final int MACHINE_SPECIFIC_DATA_COUNT = 5;
    private static final int DATA_INDEX_STORED_FLUID = 6;
    private static final int DATA_INDEX_STORED_FLUID_UPPER = 7;
    private static final int DATA_INDEX_MAX_FLUID = 8;
    private static final int DATA_INDEX_MAX_FLUID_UPPER = 9;
    private static final int DATA_INDEX_FLUID_ID = 10;

    private long storedFluidAmount;
    private FluidStack storedFluid = FluidStack.EMPTY;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(5) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == POWER_SLOT_INDEX) {
                return isCobblestonePowerItem(stack);
            }

            if (slot == ACCELERATION_SLOT_INDEX) {
                return MachineUpgradeHelper.isAccelerationChip(stack);
            }

            if (slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return MachineUpgradeHelper.isEnergizedCube(stack);
            }

            if (slot == PARALLEL_SLOT_INDEX) {
                return MachineUpgradeHelper.isParallelChip(stack);
            }

            return slot == INPUT_SLOT_INDEX;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneMelterBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX || slot == PARALLEL_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler inputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        INPUT_SLOT_INDEX
    );
    private final IItemHandler cobblestoneInputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        POWER_SLOT_INDEX
    );
    private final IItemHandler automationAccessHandler = AutomationItemHandlerHelper.createRestrictedAccessHandler(
        this.itemStackHandler,
        new int[] {INPUT_SLOT_INDEX, POWER_SLOT_INDEX},
        new int[0]
    );

    private final IFluidHandler internalFluidHandler = new MelterFluidHandler();
    private final IFluidHandler outputFluidHandler = new MelterFluidHandler();

    public CobblestoneMelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_MELTER_BLOCK_ENTITY.get(), pos, state);

        for (int index = 0; index < AUTOMATION_FACE_COUNT; index++) {
            this.setAutomationMode(index, AutomationMode.DISABLED);
            this.setFluidAutomationMode(index, AutomationMode.DISABLED);
        }
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
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

        return EmptyItemHandler.INSTANCE;
    }

    @Nullable
    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        if (side == null) {
            return this.internalFluidHandler;
        }

        AutomationMode automationMode = this.getFluidAutomationMode(AutomationSide.fromWorldSide(side, this.getBlockState()));
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

    @SuppressWarnings("null")
    public boolean canQuickMoveToInput(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Level currentLevel = this.level;
        if (currentLevel == null) {
            return false;
        }

        for (RecipeHolder<CobblestoneMelterRecipe> recipeHolder : currentLevel.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_MELTER.get())) {
            if (recipeHolder.value().getIngredient().test(stack)) {
                return true;
            }
        }

        return false;
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
        // Melter は item 出力を持たず、pushOutputsToConfiguredSides() も no-op にします。
        return -1;
    }

    @Override
    protected Optional<CobblestoneMelterRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        Optional<RecipeHolder<CobblestoneMelterRecipe>> recipeHolder = currentLevel.getRecipeManager().getRecipeFor(
            ModRecipeTypes.COBBLESTONE_MELTER.get(),
            input,
            currentLevel
        );
        return recipeHolder.map(RecipeHolder::value);
    }

    @Override
    protected boolean canProcessRecipe(CobblestoneMelterRecipe recipe) {
        return this.canOutputFluid(recipe);
    }

    @Override
    protected boolean shouldResetProgress(CobblestoneMelterRecipe recipe) {
        return !this.canProcessRecipe(recipe);
    }

    @Override
    protected int getRecipeProcessingTime(CobblestoneMelterRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestoneMelterRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(CobblestoneMelterRecipe recipe) {
        this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX).shrink(1);
        this.fillInternal(recipe.getFluidResult(), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    protected void pushOutputsToConfiguredSides() {
        // Melter は item を出力しないため、基底の item auto export を使用しません。
    }

    @Override
    protected void onAutoExportFluid() {
        if (!this.isAutoExportEnabled() || this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return;
        }

        FluidStack exportStack = this.storedFluid.copyWithAmount((int) Math.min(this.storedFluidAmount, Integer.MAX_VALUE));
        int originalAmount = exportStack.getAmount();
        FluidStack remainingFluid = this.pushFluidToConfiguredSides(exportStack, AutomationMode.OUTPUT);
        int exportedAmount = originalAmount - remainingFluid.getAmount();
        if (exportedAmount > 0) {
            this.drainInternal(exportedAmount, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private boolean canOutputFluid(CobblestoneMelterRecipe recipe) {
        FluidStack resultFluid = recipe.getFluidResult();
        if (resultFluid.isEmpty()) {
            return false;
        }

        if (!this.storedFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(this.storedFluid, resultFluid)) {
            return false;
        }

        return this.storedFluidAmount + resultFluid.getAmount() <= MAX_FLUID_AMOUNT;
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

    private boolean tryProcessPlayerFluidContainer(Player player, ItemStack sourceStack, boolean carriedStack, boolean processAll) {
        if (sourceStack.isEmpty()) {
            return false;
        }

        Optional<IFluidHandlerItem> optionalHandler = FluidUtil.getFluidHandler(sourceStack.copyWithCount(1));
        if (optionalHandler.isEmpty()) {
            return false;
        }

        IFluidHandlerItem fluidHandlerItem = optionalHandler.get();
        ItemStack processedContainer = this.tryFillPlayerContainer(fluidHandlerItem, processAll);
        if (processedContainer.isEmpty()) {
            return false;
        }

        this.replaceProcessedPlayerContainer(player, sourceStack, processedContainer, carriedStack);
        return true;
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

        if (transferredAmount <= 0) {
            return ItemStack.EMPTY;
        }

        return fluidHandlerItem.getContainer().copy();
    }

    private int fillFluidHandlerFromTank(IFluidHandlerItem fluidHandlerItem) {
        if (this.storedFluid.isEmpty() || this.storedFluidAmount <= 0L) {
            return 0;
        }

        FluidStack availableFluid = this.storedFluid.copyWithAmount((int) Math.min(this.storedFluidAmount, Integer.MAX_VALUE));
        int fillAmount = fluidHandlerItem.fill(availableFluid.copy(), IFluidHandler.FluidAction.SIMULATE);
        if (fillAmount <= 0) {
            return 0;
        }

        FluidStack drainedFluid = this.drainInternal(fillAmount, IFluidHandler.FluidAction.EXECUTE);
        if (drainedFluid.isEmpty()) {
            return 0;
        }

        int executedFillAmount = fluidHandlerItem.fill(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
        if (executedFillAmount <= 0) {
            this.fillInternal(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
            return 0;
        }

        if (executedFillAmount < drainedFluid.getAmount()) {
            FluidStack remainingFluid = drainedFluid.copyWithAmount(drainedFluid.getAmount() - executedFillAmount);
            this.fillInternal(remainingFluid, IFluidHandler.FluidAction.EXECUTE);
        }

        return executedFillAmount;
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
        if (tag.contains("storedFluid", Tag.TAG_COMPOUND)) {
            this.storedFluid = FluidStack.parseOptional(registries, tag.getCompound("storedFluid"));
            if (!this.storedFluid.isEmpty()) {
                this.storedFluid = this.storedFluid.copyWithAmount(1);
            }
        } else {
            this.storedFluid = FluidStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        this.loadAdditional(tag, registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_melter");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData melterData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_STORED_FLUID) {
                    return LongDataHelper.lowerInt(CobblestoneMelterBlockEntity.this.storedFluidAmount);
                }

                if (index == DATA_INDEX_STORED_FLUID_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneMelterBlockEntity.this.storedFluidAmount);
                }

                if (index == DATA_INDEX_MAX_FLUID) {
                    return LongDataHelper.lowerInt(MAX_FLUID_AMOUNT);
                }

                if (index == DATA_INDEX_MAX_FLUID_UPPER) {
                    return LongDataHelper.upperInt(MAX_FLUID_AMOUNT);
                }

                if (index == DATA_INDEX_FLUID_ID) {
                    return CobblestoneMelterBlockEntity.this.getDisplayedFluidId();
                }

                return CobblestoneMelterBlockEntity.this.getPoweredMachineCommonData(
                    index,
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }

            @Override
            public void set(int index, int value) {
                CobblestoneMelterBlockEntity.this.setPoweredMachineCommonData(
                    index,
                    value,
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }

            @Override
            public int getCount() {
                return CobblestoneMelterBlockEntity.this.getPoweredMachineDataCount(
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }
        };

        return new CobblestoneMelterMenu(containerId, playerInventory, this, melterData);
    }

    private class MelterFluidHandler implements IFluidHandler {
        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            if (tank != 0) {
                return FluidStack.EMPTY;
            }

            return CobblestoneMelterBlockEntity.this.getDisplayedFluid();
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
            return tank == 0 && (CobblestoneMelterBlockEntity.this.storedFluid.isEmpty()
                || FluidStack.isSameFluidSameComponents(CobblestoneMelterBlockEntity.this.storedFluid, stack));
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, CobblestoneMelterBlockEntity.this.storedFluid)) {
                return FluidStack.EMPTY;
            }

            return CobblestoneMelterBlockEntity.this.drainInternal(resource.getAmount(), action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return CobblestoneMelterBlockEntity.this.drainInternal(maxDrain, action);
        }
    }
}
