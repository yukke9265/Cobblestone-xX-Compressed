package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneAssemblyMachineMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.AssemblyMachineRecipeInput;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneAssemblyMachineRecipe;
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

@SuppressWarnings("null")
public class CobblestoneAssemblyMachineBlockEntity extends PoweredMachineBlockEntityBase<CobblestoneAssemblyMachineRecipe> implements MenuProvider {
    public static final int INPUT_SLOT_1_INDEX = 0;
    public static final int INPUT_SLOT_2_INDEX = 1;
    public static final int INPUT_SLOT_3_INDEX = 2;
    public static final int INPUT_SLOT_4_INDEX = 3;
    public static final int INPUT_SLOT_5_INDEX = 4;
    public static final int INPUT_SLOT_6_INDEX = 5;
    public static final int POWER_SLOT_INDEX = 6;
    public static final int OUTPUT_SLOT_INDEX = 7;
    public static final int ACCELERATION_SLOT_INDEX = 8;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 9;
    public static final long MAX_COBBLESTONE_POWER = 4194304000L;
    public static final long MAX_INPUT_FLUID_AMOUNT = 64_000L;

    private static final int MACHINE_SPECIFIC_DATA_COUNT = 5;
    private static final int DATA_INDEX_INPUT_FLUID = 6;
    private static final int DATA_INDEX_INPUT_FLUID_UPPER = 7;
    private static final int DATA_INDEX_MAX_INPUT_FLUID = 8;
    private static final int DATA_INDEX_MAX_INPUT_FLUID_UPPER = 9;
    private static final int DATA_INDEX_INPUT_FLUID_ID = 10;
    private long storedInputFluidAmount;
    private FluidStack storedInputFluid = FluidStack.EMPTY;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(10) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
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

            return slot >= INPUT_SLOT_1_INDEX && slot <= INPUT_SLOT_6_INDEX;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneAssemblyMachineBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler inputAutomationHandler = AutomationItemHandlerHelper.createSequentialInsertOnlyHandler(
        this.itemStackHandler,
        INPUT_SLOT_1_INDEX,
        INPUT_SLOT_2_INDEX,
        INPUT_SLOT_3_INDEX,
        INPUT_SLOT_4_INDEX,
        INPUT_SLOT_5_INDEX,
        INPUT_SLOT_6_INDEX
    );
    private final IItemHandler cobblestoneInputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        POWER_SLOT_INDEX
    );
    private final IItemHandler outputAutomationHandler = AutomationItemHandlerHelper.createExtractOnlyHandler(
        this.itemStackHandler,
        OUTPUT_SLOT_INDEX
    );
    private final IItemHandler inputGroup1AutomationHandler = AutomationItemHandlerHelper.createSequentialInsertOnlyHandler(
        this.itemStackHandler,
        INPUT_SLOT_1_INDEX,
        INPUT_SLOT_2_INDEX,
        INPUT_SLOT_3_INDEX
    );
    private final IItemHandler inputGroup2AutomationHandler = AutomationItemHandlerHelper.createSequentialInsertOnlyHandler(
        this.itemStackHandler,
        INPUT_SLOT_4_INDEX,
        INPUT_SLOT_5_INDEX,
        INPUT_SLOT_6_INDEX
    );
    private final IItemHandler automationAccessHandler = AutomationItemHandlerHelper.createRestrictedAccessHandler(
        this.itemStackHandler,
        new int[] {
            INPUT_SLOT_1_INDEX,
            INPUT_SLOT_2_INDEX,
            INPUT_SLOT_3_INDEX,
            INPUT_SLOT_4_INDEX,
            INPUT_SLOT_5_INDEX,
            INPUT_SLOT_6_INDEX
        },
        new int[] {OUTPUT_SLOT_INDEX}
    );

    private final IFluidHandler internalFluidHandler = new AssemblyMachineFluidHandler(true, true);
    private final IFluidHandler inputFluidHandler = new AssemblyMachineFluidHandler(true, false);

    public CobblestoneAssemblyMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_ASSEMBLY_MACHINE_BLOCK_ENTITY.get(), pos, state);

        for (int index = 0; index < AUTOMATION_FACE_COUNT; index++) {
            this.setFluidAutomationMode(index, AutomationMode.DISABLED);
        }
    }

    public long getStoredInputFluidAmount() {
        return this.storedInputFluidAmount;
    }

    public long getMaxInputFluidAmount() {
        return MAX_INPUT_FLUID_AMOUNT;
    }

    public FluidStack getDisplayedInputFluid() {
        if (this.storedInputFluid.isEmpty() || this.storedInputFluidAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        return this.storedInputFluid.copyWithAmount((int) Math.min(this.storedInputFluidAmount, Integer.MAX_VALUE));
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
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
        return OUTPUT_SLOT_INDEX;
    }

    @Override
    protected Optional<CobblestoneAssemblyMachineRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        AssemblyMachineRecipeInput input = this.createRecipeInput();
        if (input.isEmpty()) {
            return Optional.empty();
        }

        return currentLevel.getRecipeManager()
            .getRecipeFor(ModRecipeTypes.COBBLESTONE_ASSEMBLY_MACHINE.get(), input, currentLevel)
            .map(RecipeHolder::value);
    }

    @Override
    protected boolean canProcessRecipe(CobblestoneAssemblyMachineRecipe recipe) {
        return this.hasRequiredInputs(recipe) && this.canOutputItem(recipe);
    }

    @Override
    protected boolean shouldResetProgress(CobblestoneAssemblyMachineRecipe recipe) {
        return !this.hasRequiredInputs(recipe) || !this.canOutputItem(recipe);
    }

    @Override
    protected int getRecipeProcessingTime(CobblestoneAssemblyMachineRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestoneAssemblyMachineRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(CobblestoneAssemblyMachineRecipe recipe) {
        this.craft(recipe);
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
            return this.inputGroup1AutomationHandler;
        }
        if (automationMode == AutomationMode.INPUT_2) {
            return this.inputGroup2AutomationHandler;
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
        if (automationMode == AutomationMode.INPUT) {
            return this.inputFluidHandler;
        }
        if (automationMode == AutomationMode.IN_OUT) {
            return this.internalFluidHandler;
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
    protected void saveAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
        this.saveStoredFluid(tag, registries, "inputFluid", this.storedInputFluid, this.storedInputFluidAmount);
    }

    @Override
    protected void loadAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
        this.loadStoredFluid(tag, registries, "inputFluid");
    }

    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        this.loadAdditional(tag, registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_assembly_machine");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData assemblyMachineData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_INPUT_FLUID) {
                    return LongDataHelper.lowerInt(CobblestoneAssemblyMachineBlockEntity.this.storedInputFluidAmount);
                }
                if (index == DATA_INDEX_INPUT_FLUID_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneAssemblyMachineBlockEntity.this.storedInputFluidAmount);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID) {
                    return LongDataHelper.lowerInt(MAX_INPUT_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID_UPPER) {
                    return LongDataHelper.upperInt(MAX_INPUT_FLUID_AMOUNT);
                }
                if (index == DATA_INDEX_INPUT_FLUID_ID) {
                    return CobblestoneAssemblyMachineBlockEntity.this.getDisplayedFluidId();
                }
                return CobblestoneAssemblyMachineBlockEntity.this.getPoweredMachineCommonData(
                    index,
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }

            @Override
            public void set(int index, int value) {
                CobblestoneAssemblyMachineBlockEntity.this.setPoweredMachineCommonData(
                    index,
                    value,
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }

            @Override
            public int getCount() {
                return CobblestoneAssemblyMachineBlockEntity.this.getPoweredMachineDataCount(
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }
        };

        return new CobblestoneAssemblyMachineMenu(containerId, playerInventory, this, assemblyMachineData);
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

        for (RecipeHolder<CobblestoneAssemblyMachineRecipe> recipeHolder : currentLevel.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_ASSEMBLY_MACHINE.get())) {
            CobblestoneAssemblyMachineRecipe recipe = recipeHolder.value();
            if (this.matchesAssemblyInput(recipe.getFirstItemInput(), stack)
                || this.matchesAssemblyInput(recipe.getSecondItemInput(), stack)
                || this.matchesAssemblyInput(recipe.getThirdItemInput(), stack)
                || this.matchesAssemblyInput(recipe.getFourthItemInput(), stack)
                || this.matchesAssemblyInput(recipe.getFifthItemInput(), stack)
                || this.matchesAssemblyInput(recipe.getSixthItemInput(), stack)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesAssemblyInput(ItemStack requiredStack, ItemStack actualStack) {
        if (requiredStack.isEmpty() || actualStack.isEmpty()) {
            return false;
        }

        return ItemStack.isSameItemSameComponents(requiredStack, actualStack);
    }

    private boolean hasRequiredInputs(CobblestoneAssemblyMachineRecipe recipe) {
        if (recipe.findMatchingItemSlots(this.createRecipeInput()).isEmpty()) {
            return false;
        }
        if (recipe.hasFluidInput() && this.storedInputFluidAmount < recipe.getFluidInput().getAmount()) {
            return false;
        }

        return true;
    }

    private boolean canOutputItem(CobblestoneAssemblyMachineRecipe recipe) {
        ItemStack resultStack = recipe.getResultItemStack();
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
            return false;
        }

        return outputStack.getCount() + resultStack.getCount() <= outputStack.getMaxStackSize();
    }

    private void craft(CobblestoneAssemblyMachineRecipe recipe) {
        Optional<int[]> matchedSlots = recipe.findMatchingItemSlots(this.createRecipeInput());
        if (matchedSlots.isEmpty()) {
            return;
        }

        int[] itemSlots = matchedSlots.get();
        if (recipe.hasFirstItemInput()) {
            this.itemStackHandler.getStackInSlot(itemSlots[0]).shrink(recipe.getFirstItemInput().getCount());
        }
        if (recipe.hasSecondItemInput()) {
            this.itemStackHandler.getStackInSlot(itemSlots[1]).shrink(recipe.getSecondItemInput().getCount());
        }
        if (recipe.hasThirdItemInput()) {
            this.itemStackHandler.getStackInSlot(itemSlots[2]).shrink(recipe.getThirdItemInput().getCount());
        }
        if (recipe.hasFourthItemInput()) {
            this.itemStackHandler.getStackInSlot(itemSlots[3]).shrink(recipe.getFourthItemInput().getCount());
        }
        if (recipe.hasFifthItemInput()) {
            this.itemStackHandler.getStackInSlot(itemSlots[4]).shrink(recipe.getFifthItemInput().getCount());
        }
        if (recipe.hasSixthItemInput()) {
            this.itemStackHandler.getStackInSlot(itemSlots[5]).shrink(recipe.getSixthItemInput().getCount());
        }
        if (recipe.hasFluidInput()) {
            this.drainTankInternal(recipe.getFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }

        this.insertItemIntoOutput(recipe.getResultItemStack());
    }

    private AssemblyMachineRecipeInput createRecipeInput() {
        return new AssemblyMachineRecipeInput(
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_3_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_4_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_5_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_6_INDEX),
            this.getDisplayedInputFluid()
        );
    }

    private void insertItemIntoOutput(ItemStack resultStack) {
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
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
        FluidStack simulatedDrain = fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedDrain.isEmpty()) {
            return 0;
        }

        int fillableAmount = this.fillTankInternal(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
        if (fillableAmount <= 0) {
            return 0;
        }

        FluidStack executedDrain = fluidHandlerItem.drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);
        if (executedDrain.isEmpty()) {
            return 0;
        }

        return this.fillTankInternal(executedDrain, IFluidHandler.FluidAction.EXECUTE);
    }

    private int fillFluidHandlerFromTank(IFluidHandlerItem fluidHandlerItem) {
        FluidStack availableFluid = this.getDisplayedInputFluid();
        if (availableFluid.isEmpty()) {
            return 0;
        }

        int fillAmount = fluidHandlerItem.fill(availableFluid.copy(), IFluidHandler.FluidAction.SIMULATE);
        if (fillAmount <= 0) {
            return 0;
        }

        FluidStack drainedFluid = this.drainTankInternal(fillAmount, IFluidHandler.FluidAction.EXECUTE);
        if (drainedFluid.isEmpty()) {
            return 0;
        }

        int executedFillAmount = fluidHandlerItem.fill(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
        if (executedFillAmount <= 0) {
            this.fillTankInternal(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
            return 0;
        }

        if (executedFillAmount < drainedFluid.getAmount()) {
            FluidStack remainingFluid = drainedFluid.copyWithAmount(drainedFluid.getAmount() - executedFillAmount);
            this.fillTankInternal(remainingFluid, IFluidHandler.FluidAction.EXECUTE);
        }

        return executedFillAmount;
    }

    private int getDisplayedFluidId() {
        FluidStack fluidStack = this.getDisplayedInputFluid();
        if (fluidStack.isEmpty()) {
            return -1;
        }

        return BuiltInRegistries.FLUID.getId(fluidStack.getFluid());
    }

    private int fillTankInternal(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        if (!this.storedInputFluid.isEmpty() && !FluidStack.isSameFluidSameComponents(this.storedInputFluid, resource)) {
            return 0;
        }

        long remainingCapacity = MAX_INPUT_FLUID_AMOUNT - this.storedInputFluidAmount;
        if (remainingCapacity <= 0L) {
            return 0;
        }

        int filledAmount = (int) Math.min(remainingCapacity, resource.getAmount());
        if (filledAmount <= 0) {
            return 0;
        }

        if (action.execute()) {
            this.storedInputFluid = this.storedInputFluid.isEmpty() ? resource.copy() : this.storedInputFluid.copy();
            this.storedInputFluidAmount += filledAmount;
            this.syncToClient();
        }

        return filledAmount;
    }

    private FluidStack drainTankInternal(int amount, IFluidHandler.FluidAction action) {
        if (amount <= 0 || this.storedInputFluid.isEmpty() || this.storedInputFluidAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        int drainedAmount = (int) Math.min(this.storedInputFluidAmount, amount);
        FluidStack drained = this.storedInputFluid.copyWithAmount(drainedAmount);
        if (action.execute()) {
            this.storedInputFluidAmount -= drainedAmount;
            if (this.storedInputFluidAmount <= 0L) {
                this.storedInputFluid = FluidStack.EMPTY;
                this.storedInputFluidAmount = 0L;
            }
            this.syncToClient();
        }

        return drained;
    }

    private void saveStoredFluid(CompoundTag tag, HolderLookup.Provider registries, String key, FluidStack fluidStack, long amount) {
        tag.putLong(key + "Amount", amount);
        if (fluidStack.isEmpty() || amount <= 0L) {
            return;
        }

        tag.put(key, fluidStack.save(registries));
    }

    private void loadStoredFluid(CompoundTag tag, HolderLookup.Provider registries, String key) {
        long amount = tag.getLong(key + "Amount");
        FluidStack fluidStack = FluidStack.EMPTY;
        if (tag.contains(key, Tag.TAG_COMPOUND)) {
            fluidStack = FluidStack.parseOptional(registries, tag.getCompound(key));
        }

        if (fluidStack.isEmpty() || amount <= 0L) {
            this.storedInputFluid = FluidStack.EMPTY;
            this.storedInputFluidAmount = 0L;
            return;
        }

        this.storedInputFluid = fluidStack;
        this.storedInputFluidAmount = amount;
    }

    private void syncToClient() {
        this.setChanged();

        Level currentLevel = this.level;
        if (currentLevel != null) {
            BlockState currentState = this.getBlockState();
            currentLevel.sendBlockUpdated(this.worldPosition, currentState, currentState, 3);
        }
    }

    private class AssemblyMachineFluidHandler implements IFluidHandler {
        private final boolean canFill;
        private final boolean canDrain;

        private AssemblyMachineFluidHandler(boolean canFill, boolean canDrain) {
            this.canFill = canFill;
            this.canDrain = canDrain;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @Nonnull FluidStack getFluidInTank(int tank) {
            return tank == 0 ? CobblestoneAssemblyMachineBlockEntity.this.getDisplayedInputFluid() : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank == 0 ? (int) Math.min(MAX_INPUT_FLUID_AMOUNT, Integer.MAX_VALUE) : 0;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return tank == 0;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!this.canFill) {
                return 0;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.fillTankInternal(resource, action);
        }

        @Override
        public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) {
            if (!this.canDrain || resource.isEmpty()) {
                return FluidStack.EMPTY;
            }

            FluidStack storedFluid = CobblestoneAssemblyMachineBlockEntity.this.getDisplayedInputFluid();
            if (storedFluid.isEmpty() || !FluidStack.isSameFluidSameComponents(storedFluid, resource)) {
                return FluidStack.EMPTY;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.drainTankInternal(resource.getAmount(), action);
        }

        @Override
        public @Nonnull FluidStack drain(int maxDrain, FluidAction action) {
            if (!this.canDrain) {
                return FluidStack.EMPTY;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.drainTankInternal(maxDrain, action);
        }
    }
}