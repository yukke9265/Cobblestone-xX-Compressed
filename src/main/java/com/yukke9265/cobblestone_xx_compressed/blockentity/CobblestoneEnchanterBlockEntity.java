package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneEnchanterMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneEnchanterRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneEnchanterRecipeInput;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.util.CobblestoneEnchanterHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

/**
 * tool と enchanted book を消費してエンチャント済みの tool を作る powered machine です。
 *
 * CP、progress、保存、同期、停止処理は PoweredMachineBlockEntityBase に統一し、
 * エンチャント評価、2 入力の振り分け、結果の作成だけをこのクラスに残します。
 */
@SuppressWarnings("null")
public class CobblestoneEnchanterBlockEntity extends PoweredMachineBlockEntityBase<CobblestoneEnchanterRecipe> implements MenuProvider {
    public static final int TOOL_INPUT_SLOT_INDEX = 0;
    public static final int BOOK_INPUT_SLOT_INDEX = 1;
    public static final int POWER_SLOT_INDEX = 2;
    public static final int OUTPUT_SLOT_INDEX = 3;
    public static final int ACCELERATION_SLOT_INDEX = 4;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 5;
    public static final long MAX_COBBLESTONE_POWER = 16384L;

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
                return isCobblestonePowerItem(stack);
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

    /**
     * INPUT 面では item の種類から投入先を決めます。
     * enchanted book は book slot、それ以外の有効な tool は tool slot に入れます。
     */
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

    private final IItemHandler toolInputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        TOOL_INPUT_SLOT_INDEX
    );
    private final IItemHandler bookInputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        BOOK_INPUT_SLOT_INDEX
    );
    private final IItemHandler cobblestoneInputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        POWER_SLOT_INDEX
    );
    private final IItemHandler outputAutomationHandler = AutomationItemHandlerHelper.createExtractOnlyHandler(
        this.itemStackHandler,
        OUTPUT_SLOT_INDEX
    );
    private final IItemHandler automationAccessHandler = AutomationItemHandlerHelper.createRestrictedAccessHandler(
        this.itemStackHandler,
        new int[] {TOOL_INPUT_SLOT_INDEX, BOOK_INPUT_SLOT_INDEX, POWER_SLOT_INDEX},
        new int[] {OUTPUT_SLOT_INDEX}
    );

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

        if (isCobblestonePowerItem(stack)) {
            return false;
        }

        if (MachineUpgradeHelper.isAccelerationChip(stack) || MachineUpgradeHelper.isEnergizedCube(stack)) {
            return false;
        }

        return true;
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
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
    public long getMaxCobblestonePower() {
        long baseCapacity = this.multiplySaturating(MAX_COBBLESTONE_POWER, this.getEnergizedCubeMultiplier());
        long requiredCapacity = this.getRequiredCobblestonePowerCapacity();
        return Math.max(baseCapacity, requiredCapacity);
    }

    @Override
    public long getCurrentCobblestonePowerConsumption() {
        if (!this.getIsAvailable()) {
            return 0L;
        }

        Optional<CobblestoneEnchanterRecipe> recipeOptional = this.findMatchingRecipe();
        if (recipeOptional.isEmpty() || !this.canProcessRecipe(recipeOptional.get())) {
            return 0L;
        }

        long cobblestonePowerPerTick = this.getRecipeCobblestonePowerPerTick(recipeOptional.get());
        int progressStep = this.getProgressStep(cobblestonePowerPerTick);
        if (progressStep <= 0) {
            // CP が不足して処理できない tick は、共通 powered machine と同じく 0 CP/t と表示します。
            return 0L;
        }

        return this.multiplySaturating(cobblestonePowerPerTick, progressStep);
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
    protected Optional<CobblestoneEnchanterRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        CobblestoneEnchanterRecipeInput input = this.createRecipeInput();
        if (input.isEmpty()) {
            return Optional.empty();
        }

        Optional<RecipeHolder<CobblestoneEnchanterRecipe>> recipeHolder = currentLevel.getRecipeManager().getRecipeFor(
            ModRecipeTypes.COBBLESTONE_ENCHANTER.get(),
            input,
            currentLevel
        );
        return recipeHolder.map(RecipeHolder::value);
    }

    @Override
    protected boolean canProcessRecipe(CobblestoneEnchanterRecipe recipe) {
        CobblestoneEnchanterHelper.EvaluationResult evaluation = this.evaluateRecipe(recipe);
        return evaluation.isValid() && this.canOutput(evaluation.resultStack());
    }

    @Override
    protected boolean shouldResetProgress(CobblestoneEnchanterRecipe recipe) {
        return !this.canProcessRecipe(recipe);
    }

    @Override
    protected int getRecipeProcessingTime(CobblestoneEnchanterRecipe recipe) {
        return recipe.getProcessingTicks();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestoneEnchanterRecipe recipe) {
        return this.evaluateRecipe(recipe).cobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(CobblestoneEnchanterRecipe recipe) {
        CobblestoneEnchanterHelper.EvaluationResult evaluation = this.evaluateRecipe(recipe);
        if (!evaluation.isValid()) {
            return;
        }

        ItemStack toolStack = this.itemStackHandler.getStackInSlot(TOOL_INPUT_SLOT_INDEX);
        ItemStack bookStack = this.itemStackHandler.getStackInSlot(BOOK_INPUT_SLOT_INDEX);
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);

        toolStack.shrink(1);
        bookStack.shrink(1);

        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, evaluation.resultStack().copy());
            return;
        }

        outputStack.grow(evaluation.resultStack().getCount());
    }

    private CobblestoneEnchanterRecipeInput createRecipeInput() {
        return new CobblestoneEnchanterRecipeInput(
            this.itemStackHandler.getStackInSlot(TOOL_INPUT_SLOT_INDEX),
            this.itemStackHandler.getStackInSlot(BOOK_INPUT_SLOT_INDEX)
        );
    }

    private CobblestoneEnchanterHelper.EvaluationResult evaluateRecipe(CobblestoneEnchanterRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return CobblestoneEnchanterHelper.EvaluationResult.invalid();
        }

        return recipe.evaluate(this.createRecipeInput(), currentLevel.registryAccess());
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

    private long getRequiredCobblestonePowerCapacity() {
        Optional<CobblestoneEnchanterRecipe> recipeOptional = this.findMatchingRecipe();
        if (recipeOptional.isEmpty()) {
            return 0L;
        }

        CobblestoneEnchanterHelper.EvaluationResult evaluation = this.evaluateRecipe(recipeOptional.get());
        if (!evaluation.isValid()) {
            return 0L;
        }

        // 高レベルエンチャントでも 1 tick 分以上は蓄えられるようにして、
        // acceleration chip があるときはその分の同時消費も受け止めます。
        return this.multiplySaturating(evaluation.cobblestonePowerPerTick(), this.getAccelerationMultiplier());
    }

    private int getAccelerationMultiplier() {
        ItemStack accelerationStack = this.itemStackHandler.getStackInSlot(ACCELERATION_SLOT_INDEX);
        int multiplier = MachineUpgradeHelper.getAccelerationMultiplier(accelerationStack);
        return Math.max(1, multiplier);
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
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_enchanter");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData enchanterData = new ContainerData() {
            @Override
            public int get(int index) {
                return CobblestoneEnchanterBlockEntity.this.getPoweredMachineCommonData(index, 0);
            }

            @Override
            public void set(int index, int value) {
                CobblestoneEnchanterBlockEntity.this.setPoweredMachineCommonData(index, value, 0);
            }

            @Override
            public int getCount() {
                return CobblestoneEnchanterBlockEntity.this.getPoweredMachineDataCount(0);
            }
        };

        return new CobblestoneEnchanterMenu(containerId, playerInventory, this, enchanterData);
    }
}
