package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
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
public class CobblestoneAssemblyMachineBlockEntity extends BaseBlockEntity implements MenuProvider {
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
    private static final int DATA_INDEX_ITEM_AUTOMATION_START = 11;
    private static final int DATA_INDEX_FLUID_AUTOMATION_START = DATA_INDEX_ITEM_AUTOMATION_START + AUTOMATION_FACE_COUNT;
    private static final int DATA_INDEX_AUTO_EXPORT = DATA_INDEX_FLUID_AUTOMATION_START + AUTOMATION_FACE_COUNT;

    private int progress;
    private int maxProgress;
    private long storedCobblestonePower;
    private long storedInputFluidAmount;
    private FluidStack storedInputFluid = FluidStack.EMPTY;
    private boolean isAvailable = true;

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

    private final IItemHandler inputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 6;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot < 0 || slot >= 6) {
                return ItemStack.EMPTY;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot < 0 || slot >= 6) {
                return stack;
            }

            ItemStack remainingStack = stack;
            for (int index = INPUT_SLOT_1_INDEX; index <= INPUT_SLOT_6_INDEX; index++) {
                remainingStack = CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.insertItem(index, remainingStack, simulate);
                if (remainingStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }

            return remainingStack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot >= 0 && slot < 6 ? CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.getSlotLimit(INPUT_SLOT_1_INDEX) : 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot >= 0 && slot < 6;
        }
    };

    private final IItemHandler cobblestoneInputAutomationHandler = new SingleSlotAutomationHandler(POWER_SLOT_INDEX, true, false);
    private final IItemHandler outputAutomationHandler = new SingleSlotAutomationHandler(OUTPUT_SLOT_INDEX, false, true);
    private final IItemHandler inputGroup1AutomationHandler = new MultiSlotAutomationHandler(INPUT_SLOT_1_INDEX, INPUT_SLOT_3_INDEX, true, false);
    private final IItemHandler inputGroup2AutomationHandler = new MultiSlotAutomationHandler(INPUT_SLOT_4_INDEX, INPUT_SLOT_6_INDEX, true, false);

    private final IItemHandler automationAccessHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 10;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot < 0 || slot >= 10) {
                return ItemStack.EMPTY;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot >= INPUT_SLOT_1_INDEX && slot <= INPUT_SLOT_6_INDEX) {
                return CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.insertItem(slot, stack, simulate);
            }

            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == OUTPUT_SLOT_INDEX) {
                return CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.extractItem(slot, amount, simulate);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 0 || slot >= 10) {
                return 0;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot < 0 || slot >= 10) {
                return false;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.isItemValid(slot, stack);
        }
    };

    private final IFluidHandler internalFluidHandler = new AssemblyMachineFluidHandler(true, true);
    private final IFluidHandler inputFluidHandler = new AssemblyMachineFluidHandler(true, false);

    public CobblestoneAssemblyMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_ASSEMBLY_MACHINE_BLOCK_ENTITY.get(), pos, state);

        for (int index = 0; index < AUTOMATION_FACE_COUNT; index++) {
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

    public FluidStack getDisplayedInputFluid() {
        if (this.storedInputFluid.isEmpty() || this.storedInputFluidAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        return this.storedInputFluid.copyWithAmount((int) Math.min(this.storedInputFluidAmount, Integer.MAX_VALUE));
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
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        Level currentLevel = this.level;
        BlockState currentState = this.getBlockState();
        boolean shouldTurnOn = false;

        this.clampStoredCobblestonePower();
        this.tryAbsorbCobblestonePower();

        Optional<RecipeHolder<CobblestoneAssemblyMachineRecipe>> recipeHolder = this.getCurrentRecipe();
        if (this.isAvailable && recipeHolder.isPresent()) {
            CobblestoneAssemblyMachineRecipe recipe = recipeHolder.get().value();
            if (this.maxProgress != recipe.getProcessingTime()) {
                this.maxProgress = recipe.getProcessingTime();
                this.setChanged();
            }

            boolean hasInputs = this.hasRequiredInputs(recipe);
            boolean canOutput = this.canOutputItem(recipe);
            if (hasInputs && canOutput) {
                int progressStep = this.getProgressStep(recipe.getCobblestonePowerPerTick());
                if (progressStep > 0) {
                    this.progress += progressStep;
                    this.storedCobblestonePower -= recipe.getCobblestonePowerPerTick() * progressStep;
                    shouldTurnOn = true;
                    this.setChanged();

                    if (this.progress >= this.maxProgress) {
                        this.craft(recipe);
                        this.progress = 0;
                        this.setChanged();
                    }
                }
            } else if (this.progress != 0) {
                // 材料不足や出力詰まりのときだけ進捗をリセットし、
                // CP 不足の一時停止では進捗を保持して再開しやすくします。
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

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", this.progress);
        tag.putInt("maxProgress", this.maxProgress);
        tag.putLong("storedCobblestonePower", this.storedCobblestonePower);
        tag.putBoolean("isAvailable", this.isAvailable);
        this.saveAutomationModes(tag);
        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
        this.saveStoredFluid(tag, registries, "inputFluid", this.storedInputFluid, this.storedInputFluidAmount);
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("maxProgress");
        this.storedCobblestonePower = tag.getLong("storedCobblestonePower");
        this.isAvailable = !tag.contains("isAvailable", Tag.TAG_BYTE) || tag.getBoolean("isAvailable");
        this.loadAutomationModes(tag);
        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        }

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
                if (index == DATA_INDEX_INPUT_FLUID) {
                    return LongDataHelper.lowerInt(storedInputFluidAmount);
                }
                if (index == DATA_INDEX_INPUT_FLUID_UPPER) {
                    return LongDataHelper.upperInt(storedInputFluidAmount);
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
                if (index >= DATA_INDEX_ITEM_AUTOMATION_START && index < DATA_INDEX_FLUID_AUTOMATION_START) {
                    return CobblestoneAssemblyMachineBlockEntity.this.getAutomationModeId(index - DATA_INDEX_ITEM_AUTOMATION_START);
                }
                if (index >= DATA_INDEX_FLUID_AUTOMATION_START && index < DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneAssemblyMachineBlockEntity.this.getFluidAutomationModeId(index - DATA_INDEX_FLUID_AUTOMATION_START);
                }
                if (index == DATA_INDEX_AUTO_EXPORT) {
                    return CobblestoneAssemblyMachineBlockEntity.this.getAutoExportEnabledId();
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
        };

        return new CobblestoneAssemblyMachineMenu(containerId, playerInventory, this, assemblyMachineData);
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
        if (convertedPower <= 0 || this.storedCobblestonePower + convertedPower > this.getMaxCobblestonePower()) {
            return;
        }

        powerStack.shrink(1);
        this.storedCobblestonePower += convertedPower;
        this.setChanged();
    }

    private Optional<RecipeHolder<CobblestoneAssemblyMachineRecipe>> getCurrentRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        AssemblyMachineRecipeInput input = new AssemblyMachineRecipeInput(
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_3_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_4_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_5_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_6_INDEX),
            this.getDisplayedInputFluid()
        );
        if (input.isEmpty()) {
            return Optional.empty();
        }

        return currentLevel.getRecipeManager().getRecipeFor(ModRecipeTypes.COBBLESTONE_ASSEMBLY_MACHINE.get(), input, currentLevel);
    }

    private boolean hasRequiredInputs(CobblestoneAssemblyMachineRecipe recipe) {
        if (recipe.hasFirstItemInput() && !this.hasEnoughItems(INPUT_SLOT_1_INDEX, recipe.getFirstItemInput())) {
            return false;
        }
        if (recipe.hasSecondItemInput() && !this.hasEnoughItems(INPUT_SLOT_2_INDEX, recipe.getSecondItemInput())) {
            return false;
        }
        if (recipe.hasThirdItemInput() && !this.hasEnoughItems(INPUT_SLOT_3_INDEX, recipe.getThirdItemInput())) {
            return false;
        }
        if (recipe.hasFourthItemInput() && !this.hasEnoughItems(INPUT_SLOT_4_INDEX, recipe.getFourthItemInput())) {
            return false;
        }
        if (recipe.hasFifthItemInput() && !this.hasEnoughItems(INPUT_SLOT_5_INDEX, recipe.getFifthItemInput())) {
            return false;
        }
        if (recipe.hasSixthItemInput() && !this.hasEnoughItems(INPUT_SLOT_6_INDEX, recipe.getSixthItemInput())) {
            return false;
        }
        if (recipe.hasFluidInput() && this.storedInputFluidAmount < recipe.getFluidInput().getAmount()) {
            return false;
        }

        return true;
    }

    private boolean hasEnoughItems(int slotIndex, ItemStack requiredStack) {
        ItemStack slotStack = this.itemStackHandler.getStackInSlot(slotIndex);
        if (slotStack.isEmpty()) {
            return false;
        }
        if (!ItemStack.isSameItemSameComponents(slotStack, requiredStack)) {
            return false;
        }

        return slotStack.getCount() >= requiredStack.getCount();
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
        if (recipe.hasFirstItemInput()) {
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX).shrink(recipe.getFirstItemInput().getCount());
        }
        if (recipe.hasSecondItemInput()) {
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX).shrink(recipe.getSecondItemInput().getCount());
        }
        if (recipe.hasThirdItemInput()) {
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_3_INDEX).shrink(recipe.getThirdItemInput().getCount());
        }
        if (recipe.hasFourthItemInput()) {
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_4_INDEX).shrink(recipe.getFourthItemInput().getCount());
        }
        if (recipe.hasFifthItemInput()) {
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_5_INDEX).shrink(recipe.getFifthItemInput().getCount());
        }
        if (recipe.hasSixthItemInput()) {
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_6_INDEX).shrink(recipe.getSixthItemInput().getCount());
        }
        if (recipe.hasFluidInput()) {
            this.drainTankInternal(recipe.getFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }

        this.insertItemIntoOutput(recipe.getResultItemStack());
    }

    private void insertItemIntoOutput(ItemStack resultStack) {
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
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
            return slot == 0 ? CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.getStackInSlot(this.slotIndex) : ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!this.canInsert || slot != 0) {
                return stack;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.insertItem(this.slotIndex, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!this.canExtract || slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.extractItem(this.slotIndex, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.getSlotLimit(this.slotIndex) : 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == 0 && CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.isItemValid(this.slotIndex, stack);
        }
    }

    private class MultiSlotAutomationHandler implements IItemHandler {
        private final int startSlotIndex;
        private final int endSlotIndex;
        private final boolean canInsert;
        private final boolean canExtract;

        private MultiSlotAutomationHandler(int startSlotIndex, int endSlotIndex, boolean canInsert, boolean canExtract) {
            this.startSlotIndex = startSlotIndex;
            this.endSlotIndex = endSlotIndex;
            this.canInsert = canInsert;
            this.canExtract = canExtract;
        }

        @Override
        public int getSlots() {
            return this.endSlotIndex - this.startSlotIndex + 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            int actualSlot = this.startSlotIndex + slot;
            if (actualSlot < this.startSlotIndex || actualSlot > this.endSlotIndex) {
                return ItemStack.EMPTY;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.getStackInSlot(actualSlot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!this.canInsert) {
                return stack;
            }

            ItemStack remainingStack = stack;
            for (int actualSlot = this.startSlotIndex; actualSlot <= this.endSlotIndex; actualSlot++) {
                remainingStack = CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.insertItem(actualSlot, remainingStack, simulate);
                if (remainingStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }

            return remainingStack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!this.canExtract) {
                return ItemStack.EMPTY;
            }

            int actualSlot = this.startSlotIndex + slot;
            if (actualSlot < this.startSlotIndex || actualSlot > this.endSlotIndex) {
                return ItemStack.EMPTY;
            }

            return CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.extractItem(actualSlot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot >= 0 && slot < this.getSlots() ? CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.getSlotLimit(this.startSlotIndex) : 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            int actualSlot = this.startSlotIndex + slot;
            return actualSlot >= this.startSlotIndex
                && actualSlot <= this.endSlotIndex
                && CobblestoneAssemblyMachineBlockEntity.this.itemStackHandler.isItemValid(actualSlot, stack);
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