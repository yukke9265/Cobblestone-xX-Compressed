package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTarget;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTargetIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneChemicalReactorMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.ChemicalReactorRecipeInput;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneChemicalReactorRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.util.LongDataHelper;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

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
public class CobblestoneChemicalReactorBlockEntity extends PoweredMachineBlockEntityBase<CobblestoneChemicalReactorRecipe> implements MenuProvider {
    public static final int INPUT_SLOT_1_INDEX = 0;
    public static final int INPUT_SLOT_2_INDEX = 1;
    public static final int POWER_SLOT_INDEX = 2;
    public static final int OUTPUT_SLOT_1_INDEX = 3;
    public static final int OUTPUT_SLOT_2_INDEX = 4;
    public static final int ACCELERATION_SLOT_INDEX = 5;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 6;
    public static final int PARALLEL_SLOT_INDEX = 7;
    public static final long MAX_COBBLESTONE_POWER = 1048576000L;
    public static final long MAX_INPUT_FLUID_1_AMOUNT = 64_000L;
    public static final long MAX_INPUT_FLUID_2_AMOUNT = 64_000L;
    public static final long MAX_OUTPUT_FLUID_1_AMOUNT = 64_000L;
    public static final long MAX_OUTPUT_FLUID_2_AMOUNT = 64_000L;

    private static final int MACHINE_SPECIFIC_DATA_COUNT = 20;
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
    private static final int DATA_INDEX_OUTPUT_FLUID_1 = 16;
    private static final int DATA_INDEX_OUTPUT_FLUID_1_UPPER = 17;
    private static final int DATA_INDEX_MAX_OUTPUT_FLUID_1 = 18;
    private static final int DATA_INDEX_MAX_OUTPUT_FLUID_1_UPPER = 19;
    private static final int DATA_INDEX_OUTPUT_FLUID_1_ID = 20;
    private static final int DATA_INDEX_OUTPUT_FLUID_2 = 21;
    private static final int DATA_INDEX_OUTPUT_FLUID_2_UPPER = 22;
    private static final int DATA_INDEX_MAX_OUTPUT_FLUID_2 = 23;
    private static final int DATA_INDEX_MAX_OUTPUT_FLUID_2_UPPER = 24;
    private static final int DATA_INDEX_OUTPUT_FLUID_2_ID = 25;
    private long storedInputFluid1Amount;
    private long storedInputFluid2Amount;
    private long storedOutputFluid1Amount;
    private long storedOutputFluid2Amount;
    private FluidStack storedInputFluid1 = FluidStack.EMPTY;
    private FluidStack storedInputFluid2 = FluidStack.EMPTY;
    private FluidStack storedOutputFluid1 = FluidStack.EMPTY;
    private FluidStack storedOutputFluid2 = FluidStack.EMPTY;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(8) {
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

            if (slot == PARALLEL_SLOT_INDEX) {
                return MachineUpgradeHelper.isParallelChip(stack);
            }

            if (slot == INPUT_SLOT_1_INDEX) {
                return CobblestoneChemicalReactorBlockEntity.this.getSlotFilters().allowsItem(FilterTargetIds.ITEM_INPUT_1, stack);
            }

            if (slot == INPUT_SLOT_2_INDEX) {
                return CobblestoneChemicalReactorBlockEntity.this.getSlotFilters().allowsItem(FilterTargetIds.ITEM_INPUT_2, stack);
            }

            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneChemicalReactorBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX || slot == PARALLEL_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler inputAutomationHandler = AutomationItemHandlerHelper.createSequentialInsertOnlyHandler(
        this.itemStackHandler,
        INPUT_SLOT_1_INDEX,
        INPUT_SLOT_2_INDEX
    );
    private final IItemHandler outputAutomationHandler = AutomationItemHandlerHelper.createMultipleExtractOnlyHandler(
        this.itemStackHandler,
        OUTPUT_SLOT_1_INDEX,
        OUTPUT_SLOT_2_INDEX
    );
    private final IItemHandler inputSlot1AutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        INPUT_SLOT_1_INDEX
    );
    private final IItemHandler inputSlot2AutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        INPUT_SLOT_2_INDEX
    );
    private final IItemHandler cobblestoneInputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        POWER_SLOT_INDEX
    );
    private final IItemHandler outputSlot1AutomationHandler = AutomationItemHandlerHelper.createExtractOnlyHandler(
        this.itemStackHandler,
        OUTPUT_SLOT_1_INDEX
    );
    private final IItemHandler outputSlot2AutomationHandler = AutomationItemHandlerHelper.createExtractOnlyHandler(
        this.itemStackHandler,
        OUTPUT_SLOT_2_INDEX
    );
    private final IItemHandler automationAccessHandler = AutomationItemHandlerHelper.createRestrictedAccessHandler(
        this.itemStackHandler,
        new int[] {INPUT_SLOT_1_INDEX, INPUT_SLOT_2_INDEX},
        new int[] {OUTPUT_SLOT_1_INDEX, OUTPUT_SLOT_2_INDEX}
    );

    private final IFluidHandler internalFluidHandler = new ChemicalReactorFluidHandler(true, true);
    private final IFluidHandler inputFluidHandler = new ChemicalReactorFluidHandler(true, false);
    private final IFluidHandler inputFluid1Handler = new SingleTankInputFluidHandler(0);
    private final IFluidHandler inputFluid2Handler = new SingleTankInputFluidHandler(1);
    private final IFluidHandler outputFluidHandler = new ChemicalReactorFluidHandler(false, true);
    private final IFluidHandler outputFluid1Handler = new SingleTankOutputFluidHandler(2);
    private final IFluidHandler outputFluid2Handler = new SingleTankOutputFluidHandler(3);

    public CobblestoneChemicalReactorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_CHEMICAL_REACTOR_BLOCK_ENTITY.get(), pos, state);

        for (int index = 0; index < AUTOMATION_FACE_COUNT; index++) {
            this.setFluidAutomationMode(index, AutomationMode.DISABLED);
        }
    }

    @Override
    public List<FilterTarget> getFilterTargets() {
        return List.of(
            FilterTarget.item(
                FilterTargetIds.ITEM_INPUT_1,
                MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_1_SLOT_X,
                MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_1_SLOT_Y
            ),
            FilterTarget.item(
                FilterTargetIds.ITEM_INPUT_2,
                MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_2_SLOT_X,
                MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_2_SLOT_Y
            ),
            FilterTarget.fluid(
                FilterTargetIds.FLUID_INPUT_1,
                MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_1_SLOT_X,
                MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_1_SLOT_Y
            ),
            FilterTarget.fluid(
                FilterTargetIds.FLUID_INPUT_2,
                MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_2_SLOT_X,
                MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_2_SLOT_Y
            )
        );
    }

    public long getStoredInputFluid1Amount() {
        return this.storedInputFluid1Amount;
    }

    public long getStoredInputFluid2Amount() {
        return this.storedInputFluid2Amount;
    }

    public long getStoredOutputFluid1Amount() {
        return this.storedOutputFluid1Amount;
    }

    public long getStoredOutputFluid2Amount() {
        return this.storedOutputFluid2Amount;
    }

    public long getMaxInputFluid1Amount() {
        return MAX_INPUT_FLUID_1_AMOUNT;
    }

    public long getMaxInputFluid2Amount() {
        return MAX_INPUT_FLUID_2_AMOUNT;
    }

    public long getMaxOutputFluid1Amount() {
        return MAX_OUTPUT_FLUID_1_AMOUNT;
    }

    public long getMaxOutputFluid2Amount() {
        return MAX_OUTPUT_FLUID_2_AMOUNT;
    }

    public FluidStack getDisplayedInputFluid1() {
        return this.createDisplayedFluid(this.storedInputFluid1, this.storedInputFluid1Amount);
    }

    public FluidStack getDisplayedInputFluid2() {
        return this.createDisplayedFluid(this.storedInputFluid2, this.storedInputFluid2Amount);
    }

    public FluidStack getDisplayedOutputFluid1() {
        return this.createDisplayedFluid(this.storedOutputFluid1, this.storedOutputFluid1Amount);
    }

    public FluidStack getDisplayedOutputFluid2() {
        return this.createDisplayedFluid(this.storedOutputFluid2, this.storedOutputFluid2Amount);
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
        return OUTPUT_SLOT_1_INDEX;
    }

    @Override
    protected Optional<CobblestoneChemicalReactorRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ChemicalReactorRecipeInput input = this.createRecipeInput();
        if (input.isEmpty()) {
            return Optional.empty();
        }

        return currentLevel.getRecipeManager()
            .getRecipeFor(ModRecipeTypes.COBBLESTONE_CHEMICAL_REACTOR.get(), input, currentLevel)
            .map(RecipeHolder::value);
    }

    @Override
    protected boolean canProcessRecipe(CobblestoneChemicalReactorRecipe recipe) {
        return this.hasRequiredInputs(recipe)
            && this.canOutputItems(recipe)
            && this.canOutputFluids(recipe);
    }

    @Override
    protected boolean shouldResetProgress(CobblestoneChemicalReactorRecipe recipe) {
        return !this.canProcessRecipe(recipe);
    }

    @Override
    protected int getRecipeProcessingTime(CobblestoneChemicalReactorRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestoneChemicalReactorRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(CobblestoneChemicalReactorRecipe recipe) {
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
            return this.inputSlot1AutomationHandler;
        }
        if (automationMode == AutomationMode.INPUT_2) {
            return this.inputSlot2AutomationHandler;
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
        if (automationMode == AutomationMode.OUTPUT_1) {
            return this.outputFluid1Handler;
        }
        if (automationMode == AutomationMode.OUTPUT_2) {
            return this.outputFluid2Handler;
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

    public boolean handleOutputFluid1IndicatorClick(Player player, boolean processAll) {
        return this.handleFluidIndicatorClick(player, processAll, 2);
    }

    public boolean handleOutputFluid2IndicatorClick(Player player, boolean processAll) {
        return this.handleFluidIndicatorClick(player, processAll, 3);
    }

    @Override
    protected void saveAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
        this.saveStoredFluid(tag, registries, "inputFluid1", this.storedInputFluid1, this.storedInputFluid1Amount);
        this.saveStoredFluid(tag, registries, "inputFluid2", this.storedInputFluid2, this.storedInputFluid2Amount);
        this.saveStoredFluid(tag, registries, "outputFluid1", this.storedOutputFluid1, this.storedOutputFluid1Amount);
        this.saveStoredFluid(tag, registries, "outputFluid2", this.storedOutputFluid2, this.storedOutputFluid2Amount);
    }

    @Override
    protected void loadAdditionalPoweredMachineData(CompoundTag tag, HolderLookup.Provider registries) {
        this.loadStoredFluid(tag, registries, "inputFluid1", 0);
        this.loadStoredFluid(tag, registries, "inputFluid2", 1);
        this.loadStoredFluid(tag, registries, "outputFluid1", 2);
        this.loadStoredFluid(tag, registries, "outputFluid2", 3);
    }

    @Override
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        this.loadAdditional(tag, registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_chemical_reactor");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData chemicalReactorData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_INPUT_FLUID_1) {
                    return LongDataHelper.lowerInt(CobblestoneChemicalReactorBlockEntity.this.storedInputFluid1Amount);
                }
                if (index == DATA_INDEX_INPUT_FLUID_1_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneChemicalReactorBlockEntity.this.storedInputFluid1Amount);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID_1) {
                    return LongDataHelper.lowerInt(MAX_INPUT_FLUID_1_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID_1_UPPER) {
                    return LongDataHelper.upperInt(MAX_INPUT_FLUID_1_AMOUNT);
                }
                if (index == DATA_INDEX_INPUT_FLUID_1_ID) {
                    return CobblestoneChemicalReactorBlockEntity.this.getDisplayedFluidId(0);
                }
                if (index == DATA_INDEX_INPUT_FLUID_2) {
                    return LongDataHelper.lowerInt(CobblestoneChemicalReactorBlockEntity.this.storedInputFluid2Amount);
                }
                if (index == DATA_INDEX_INPUT_FLUID_2_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneChemicalReactorBlockEntity.this.storedInputFluid2Amount);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID_2) {
                    return LongDataHelper.lowerInt(MAX_INPUT_FLUID_2_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_INPUT_FLUID_2_UPPER) {
                    return LongDataHelper.upperInt(MAX_INPUT_FLUID_2_AMOUNT);
                }
                if (index == DATA_INDEX_INPUT_FLUID_2_ID) {
                    return CobblestoneChemicalReactorBlockEntity.this.getDisplayedFluidId(1);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID_1) {
                    return LongDataHelper.lowerInt(CobblestoneChemicalReactorBlockEntity.this.storedOutputFluid1Amount);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID_1_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneChemicalReactorBlockEntity.this.storedOutputFluid1Amount);
                }
                if (index == DATA_INDEX_MAX_OUTPUT_FLUID_1) {
                    return LongDataHelper.lowerInt(MAX_OUTPUT_FLUID_1_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_OUTPUT_FLUID_1_UPPER) {
                    return LongDataHelper.upperInt(MAX_OUTPUT_FLUID_1_AMOUNT);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID_1_ID) {
                    return CobblestoneChemicalReactorBlockEntity.this.getDisplayedFluidId(2);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID_2) {
                    return LongDataHelper.lowerInt(CobblestoneChemicalReactorBlockEntity.this.storedOutputFluid2Amount);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID_2_UPPER) {
                    return LongDataHelper.upperInt(CobblestoneChemicalReactorBlockEntity.this.storedOutputFluid2Amount);
                }
                if (index == DATA_INDEX_MAX_OUTPUT_FLUID_2) {
                    return LongDataHelper.lowerInt(MAX_OUTPUT_FLUID_2_AMOUNT);
                }
                if (index == DATA_INDEX_MAX_OUTPUT_FLUID_2_UPPER) {
                    return LongDataHelper.upperInt(MAX_OUTPUT_FLUID_2_AMOUNT);
                }
                if (index == DATA_INDEX_OUTPUT_FLUID_2_ID) {
                    return CobblestoneChemicalReactorBlockEntity.this.getDisplayedFluidId(3);
                }
                return CobblestoneChemicalReactorBlockEntity.this.getPoweredMachineCommonData(
                    index,
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }

            @Override
            public void set(int index, int value) {
                CobblestoneChemicalReactorBlockEntity.this.setPoweredMachineCommonData(
                    index,
                    value,
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }

            @Override
            public int getCount() {
                return CobblestoneChemicalReactorBlockEntity.this.getPoweredMachineDataCount(
                    MACHINE_SPECIFIC_DATA_COUNT,
                    true
                );
            }
        };

        return new CobblestoneChemicalReactorMenu(containerId, playerInventory, this, chemicalReactorData);
    }

    private FluidStack createDisplayedFluid(FluidStack storedFluid, long storedAmount) {
        if (storedFluid.isEmpty() || storedAmount <= 0L) {
            return FluidStack.EMPTY;
        }

        return storedFluid.copyWithAmount((int) Math.min(storedAmount, Integer.MAX_VALUE));
    }

    @Override
    protected void pushOutputsToConfiguredSides() {
        this.pushOutputSlotToConfiguredSides(
            OUTPUT_SLOT_1_INDEX,
            AutomationMode.OUTPUT,
            AutomationMode.OUTPUT_1,
            AutomationMode.IN_OUT
        );
        this.pushOutputSlotToConfiguredSides(
            OUTPUT_SLOT_2_INDEX,
            AutomationMode.OUTPUT,
            AutomationMode.OUTPUT_2,
            AutomationMode.IN_OUT
        );
    }

    @Override
    protected void onAutoExportFluid() {
        if (!this.isAutoExportEnabled()) {
            return;
        }

        this.autoExportFluidTank(2);
        this.autoExportFluidTank(3);
    }

    private void autoExportFluidTank(int tankIndex) {
        FluidStack exportStack = this.getDisplayedFluidByTank(tankIndex);
        if (exportStack.isEmpty()) {
            return;
        }

        int originalAmount = exportStack.getAmount();
        AutomationMode specificOutputMode = tankIndex == 2 ? AutomationMode.OUTPUT_1 : AutomationMode.OUTPUT_2;
        FluidStack remainingFluid = this.pushFluidToConfiguredSides(exportStack, AutomationMode.OUTPUT, specificOutputMode, AutomationMode.IN_OUT);
        int exportedAmount = originalAmount - remainingFluid.getAmount();
        if (exportedAmount > 0) {
            this.drainTankInternal(tankIndex, exportedAmount, IFluidHandler.FluidAction.EXECUTE);
        }
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

        for (RecipeHolder<CobblestoneChemicalReactorRecipe> recipeHolder : currentLevel.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CHEMICAL_REACTOR.get())) {
            CobblestoneChemicalReactorRecipe recipe = recipeHolder.value();
            if (recipe.hasFirstItemInput() && recipe.getFirstItemInput().test(stack)) {
                return true;
            }

            if (recipe.hasSecondItemInput() && recipe.getSecondItemInput().test(stack)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasRequiredInputs(CobblestoneChemicalReactorRecipe recipe) {
        ChemicalReactorRecipeInput input = this.createRecipeInput();
        if (recipe.findMatchingItemSlots(input).isEmpty()) {
            return false;
        }
        if (recipe.findMatchingFluidTanks(input).isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean canOutputItems(CobblestoneChemicalReactorRecipe recipe) {
        if (recipe.hasFirstItemOutput() && !this.canInsertItemIntoOutputSlot(OUTPUT_SLOT_1_INDEX, recipe.getFirstResultItem())) {
            return false;
        }
        if (recipe.hasSecondItemOutput() && !this.canInsertItemIntoOutputSlot(OUTPUT_SLOT_2_INDEX, recipe.getSecondResultItem())) {
            return false;
        }
        return true;
    }

    private boolean canInsertItemIntoOutputSlot(int slotIndex, ItemStack resultStack) {
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

    private boolean canOutputFluids(CobblestoneChemicalReactorRecipe recipe) {
        if (recipe.hasFirstFluidOutput() && this.fillTankInternal(2, recipe.getFirstResultFluid(), IFluidHandler.FluidAction.SIMULATE) < recipe.getFirstResultFluid().getAmount()) {
            return false;
        }
        if (recipe.hasSecondFluidOutput() && this.fillTankInternal(3, recipe.getSecondResultFluid(), IFluidHandler.FluidAction.SIMULATE) < recipe.getSecondResultFluid().getAmount()) {
            return false;
        }
        return true;
    }

    private void craft(CobblestoneChemicalReactorRecipe recipe) {
        ChemicalReactorRecipeInput input = this.createRecipeInput();
        Optional<int[]> matchedItemSlots = recipe.findMatchingItemSlots(input);
        Optional<int[]> matchedFluidTanks = recipe.findMatchingFluidTanks(input);
        if (matchedItemSlots.isEmpty() || matchedFluidTanks.isEmpty()) {
            return;
        }

        int[] itemSlots = matchedItemSlots.get();
        int[] fluidTanks = matchedFluidTanks.get();
        if (recipe.hasFirstItemInput()) {
            this.itemStackHandler.getStackInSlot(itemSlots[0]).shrink(recipe.getFirstItemInput().count());
        }
        if (recipe.hasSecondItemInput()) {
            this.itemStackHandler.getStackInSlot(itemSlots[1]).shrink(recipe.getSecondItemInput().count());
        }
        if (recipe.hasFirstFluidInput()) {
            this.drainTankInternal(fluidTanks[0], recipe.getFirstFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }
        if (recipe.hasSecondFluidInput()) {
            this.drainTankInternal(fluidTanks[1], recipe.getSecondFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }
        if (recipe.hasFirstItemOutput()) {
            this.insertItemIntoOutputSlot(OUTPUT_SLOT_1_INDEX, recipe.getFirstResultItem());
        }
        if (recipe.hasSecondItemOutput()) {
            this.insertItemIntoOutputSlot(OUTPUT_SLOT_2_INDEX, recipe.getSecondResultItem());
        }
        if (recipe.hasFirstFluidOutput()) {
            this.fillTankInternal(2, recipe.getFirstResultFluid(), IFluidHandler.FluidAction.EXECUTE);
        }
        if (recipe.hasSecondFluidOutput()) {
            this.fillTankInternal(3, recipe.getSecondResultFluid(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private ChemicalReactorRecipeInput createRecipeInput() {
        return new ChemicalReactorRecipeInput(
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX),
            this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX),
            this.getDisplayedInputFluid1(),
            this.getDisplayedInputFluid2()
        );
    }

    private void insertItemIntoOutputSlot(int slotIndex, ItemStack resultStack) {
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(slotIndex);
        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(slotIndex, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
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

        return transferredAmount > 0 ? fluidHandlerItem.getContainer().copy() : ItemStack.EMPTY;
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

    private int drainFluidHandlerIntoTank(IFluidHandlerItem fluidHandlerItem, int tankIndex) {
        if (tankIndex != 0 && tankIndex != 1) {
            return 0;
        }

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
        // GUI の fluid indicator からは入力タンクも回収対象にしたいので、
        // 存在する 4 つのタンクすべてを対象にします。
        if (tankIndex < 0 || tankIndex > 3) {
            return 0;
        }

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
            case 2 -> this.getDisplayedOutputFluid1();
            case 3 -> this.getDisplayedOutputFluid2();
            default -> FluidStack.EMPTY;
        };
    }

    private int getDisplayedFluidId(int tankIndex) {
        FluidStack fluidStack = this.getDisplayedFluidByTank(tankIndex);
        if (fluidStack.isEmpty()) {
            return -1;
        }

        return BuiltInRegistries.FLUID.getId(fluidStack.getFluid());
    }

    private int fillTankInternal(int tankIndex, FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }

        if (tankIndex == 0 && !this.getSlotFilters().allowsFluid(FilterTargetIds.FLUID_INPUT_1, resource)) {
            return 0;
        }
        if (tankIndex == 1 && !this.getSlotFilters().allowsFluid(FilterTargetIds.FLUID_INPUT_2, resource)) {
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
            FluidStack newStoredFluid = storedFluid.isEmpty() ? resource.copy() : storedFluid.copy();
            this.setStoredFluidByTank(tankIndex, newStoredFluid, storedAmount + filledAmount);
            this.syncToClient();
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
            this.syncToClient();
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

    private FluidStack drainFromOutputTanks(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        if (!this.storedOutputFluid1.isEmpty() && FluidStack.isSameFluidSameComponents(this.storedOutputFluid1, resource)) {
            return this.drainTankInternal(2, resource.getAmount(), action);
        }

        if (!this.storedOutputFluid2.isEmpty() && FluidStack.isSameFluidSameComponents(this.storedOutputFluid2, resource)) {
            return this.drainTankInternal(3, resource.getAmount(), action);
        }

        return FluidStack.EMPTY;
    }

    private FluidStack drainFromOutputTanks(int maxDrain, IFluidHandler.FluidAction action) {
        FluidStack drainedFluid = this.drainTankInternal(2, maxDrain, action);
        if (!drainedFluid.isEmpty()) {
            return drainedFluid;
        }

        return this.drainTankInternal(3, maxDrain, action);
    }

    private FluidStack drainSpecificOutputTank(int tankIndex, FluidStack resource, IFluidHandler.FluidAction action) {
        if ((tankIndex != 2 && tankIndex != 3) || resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack storedFluid = this.getStoredFluidByTank(tankIndex);
        if (storedFluid.isEmpty() || !FluidStack.isSameFluidSameComponents(storedFluid, resource)) {
            return FluidStack.EMPTY;
        }

        return this.drainTankInternal(tankIndex, resource.getAmount(), action);
    }

    private FluidStack drainSpecificOutputTank(int tankIndex, int maxDrain, IFluidHandler.FluidAction action) {
        if (tankIndex != 2 && tankIndex != 3) {
            return FluidStack.EMPTY;
        }

        return this.drainTankInternal(tankIndex, maxDrain, action);
    }

    private FluidStack getStoredFluidByTank(int tankIndex) {
        return switch (tankIndex) {
            case 0 -> this.storedInputFluid1;
            case 1 -> this.storedInputFluid2;
            case 2 -> this.storedOutputFluid1;
            case 3 -> this.storedOutputFluid2;
            default -> FluidStack.EMPTY;
        };
    }

    private long getStoredAmountByTank(int tankIndex) {
        return switch (tankIndex) {
            case 0 -> this.storedInputFluid1Amount;
            case 1 -> this.storedInputFluid2Amount;
            case 2 -> this.storedOutputFluid1Amount;
            case 3 -> this.storedOutputFluid2Amount;
            default -> 0L;
        };
    }

    private long getMaxAmountByTank(int tankIndex) {
        return switch (tankIndex) {
            case 0 -> MAX_INPUT_FLUID_1_AMOUNT;
            case 1 -> MAX_INPUT_FLUID_2_AMOUNT;
            case 2 -> MAX_OUTPUT_FLUID_1_AMOUNT;
            case 3 -> MAX_OUTPUT_FLUID_2_AMOUNT;
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
                this.storedOutputFluid1 = copiedFluid;
                this.storedOutputFluid1Amount = copiedFluid.isEmpty() ? 0L : amount;
            }
            case 3 -> {
                this.storedOutputFluid2 = copiedFluid;
                this.storedOutputFluid2Amount = copiedFluid.isEmpty() ? 0L : amount;
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

    private void syncToClient() {
        this.setChanged();

        Level currentLevel = this.level;
        if (currentLevel != null) {
            BlockState currentState = this.getBlockState();
            currentLevel.sendBlockUpdated(this.worldPosition, currentState, currentState, 3);
        }
    }

    private class ChemicalReactorFluidHandler implements IFluidHandler {
        private final boolean allowInput;
        private final boolean allowOutput;

        private ChemicalReactorFluidHandler(boolean allowInput, boolean allowOutput) {
            this.allowInput = allowInput;
            this.allowOutput = allowOutput;
        }

        @Override
        public int getTanks() {
            return 4;
        }

        @Override
        public @Nonnull FluidStack getFluidInTank(int tank) {
            return CobblestoneChemicalReactorBlockEntity.this.getDisplayedFluidByTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return (int) Math.min(Integer.MAX_VALUE, CobblestoneChemicalReactorBlockEntity.this.getMaxAmountByTank(tank));
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return tank >= 0 && tank < 4;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!this.allowInput) {
                return 0;
            }

            return CobblestoneChemicalReactorBlockEntity.this.fillIntoInputTanks(resource, action);
        }

        @Override
        public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) {
            if (!this.allowOutput || resource.isEmpty()) {
                return FluidStack.EMPTY;
            }

            return CobblestoneChemicalReactorBlockEntity.this.drainFromOutputTanks(resource, action);
        }

        @Override
        public @Nonnull FluidStack drain(int maxDrain, FluidAction action) {
            if (!this.allowOutput) {
                return FluidStack.EMPTY;
            }

            return CobblestoneChemicalReactorBlockEntity.this.drainFromOutputTanks(maxDrain, action);
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

            return CobblestoneChemicalReactorBlockEntity.this.getDisplayedFluidByTank(this.tankIndex);
        }

        @Override
        public int getTankCapacity(int tank) {
            if (tank != 0) {
                return 0;
            }

            return (int) Math.min(Integer.MAX_VALUE, CobblestoneChemicalReactorBlockEntity.this.getMaxAmountByTank(this.tankIndex));
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return tank == 0;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return CobblestoneChemicalReactorBlockEntity.this.fillSpecificInputTank(this.tankIndex, resource, action);
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

    private class SingleTankOutputFluidHandler implements IFluidHandler {
        private final int tankIndex;

        private SingleTankOutputFluidHandler(int tankIndex) {
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

            return CobblestoneChemicalReactorBlockEntity.this.getDisplayedFluidByTank(this.tankIndex);
        }

        @Override
        public int getTankCapacity(int tank) {
            if (tank != 0) {
                return 0;
            }

            return (int) Math.min(Integer.MAX_VALUE, CobblestoneChemicalReactorBlockEntity.this.getMaxAmountByTank(this.tankIndex));
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) {
            return CobblestoneChemicalReactorBlockEntity.this.drainSpecificOutputTank(this.tankIndex, resource, action);
        }

        @Override
        public @Nonnull FluidStack drain(int maxDrain, FluidAction action) {
            return CobblestoneChemicalReactorBlockEntity.this.drainSpecificOutputTank(this.tankIndex, maxDrain, action);
        }
    }
}