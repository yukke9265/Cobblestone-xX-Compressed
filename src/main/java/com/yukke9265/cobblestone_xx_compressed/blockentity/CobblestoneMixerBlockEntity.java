package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.List;
import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTarget;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTargetIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneMixerMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.DoubleItemRecipeInput;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

/**
 * 2 種類の入力を混合して 1 種類の結果を作る powered machine です。
 *
 * CP、progress、保存、同期、停止時の処理は PoweredMachineBlockEntityBase に統一し、
 * 2 入力の照合、投入順、消費量だけをこのクラスに残します。
 */
public class CobblestoneMixerBlockEntity extends PoweredMachineBlockEntityBase<CobblestoneMixerRecipe> implements MenuProvider {
    public static final int INPUT_SLOT_1_INDEX = 0;
    public static final int INPUT_SLOT_2_INDEX = 1;
    public static final int POWER_SLOT_INDEX = 2;
    public static final int OUTPUT_SLOT_INDEX = 3;
    public static final int ACCELERATION_SLOT_INDEX = 4;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 5;
    public static final int PARALLEL_SLOT_INDEX = 6;
    public static final long MAX_COBBLESTONE_POWER = 16000L;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(7) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == OUTPUT_SLOT_INDEX) {
                return false;
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

            if (slot == PARALLEL_SLOT_INDEX) {
                return MachineUpgradeHelper.isParallelChip(stack);
            }

            if (slot == INPUT_SLOT_1_INDEX) {
                return CobblestoneMixerBlockEntity.this.getSlotFilters().allowsItem(FilterTargetIds.ITEM_INPUT_1, stack);
            }

            if (slot == INPUT_SLOT_2_INDEX) {
                return CobblestoneMixerBlockEntity.this.getSlotFilters().allowsItem(FilterTargetIds.ITEM_INPUT_2, stack);
            }

            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneMixerBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX || slot == PARALLEL_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    // INPUT は入力 1、入力 2 の順に投入します。INPUT_1 と INPUT_2 は個別の入力面です。
    private final IItemHandler inputAutomationHandler = AutomationItemHandlerHelper.createSequentialInsertOnlyHandler(
        this.itemStackHandler,
        INPUT_SLOT_1_INDEX,
        INPUT_SLOT_2_INDEX
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
    private final IItemHandler outputAutomationHandler = AutomationItemHandlerHelper.createExtractOnlyHandler(
        this.itemStackHandler,
        OUTPUT_SLOT_INDEX
    );
    private final IItemHandler automationAccessHandler = AutomationItemHandlerHelper.createRestrictedAccessHandler(
        this.itemStackHandler,
        new int[] {INPUT_SLOT_1_INDEX, INPUT_SLOT_2_INDEX},
        new int[] {OUTPUT_SLOT_INDEX}
    );

    public CobblestoneMixerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_MIXER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public List<FilterTarget> getFilterTargets() {
        return List.of(
            FilterTarget.item(
                FilterTargetIds.ITEM_INPUT_1,
                MachineGuiLayouts.Mixer.INPUT_SLOT_1_X,
                MachineGuiLayouts.Mixer.INPUT_SLOT_1_Y
            ),
            FilterTarget.item(
                FilterTargetIds.ITEM_INPUT_2,
                MachineGuiLayouts.Mixer.INPUT_SLOT_2_X,
                MachineGuiLayouts.Mixer.INPUT_SLOT_2_Y
            )
        );
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    /**
     * Mixer は INPUT_1 と INPUT_2 を持つため、標準 powered machine の単一入力 routing ではなく
     * この機械固有の routing を使用します。
     */
    public IItemHandler getAutomationItemHandler(Direction side) {
        if (side == null) {
            return this.automationAccessHandler;
        }

        BlockState currentState = this.getBlockState();
        AutomationSide automationSide = AutomationSide.fromWorldSide(side, currentState);
        AutomationMode automationMode = this.getAutomationMode(automationSide);
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

        if (automationMode == AutomationMode.IN_OUT) {
            return this.automationAccessHandler;
        }

        return EmptyItemHandler.INSTANCE;
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

    @SuppressWarnings("null")
    public boolean canQuickMoveToInput(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Level currentLevel = this.level;
        if (currentLevel == null) {
            return false;
        }

        for (RecipeHolder<CobblestoneMixerRecipe> recipeHolder : currentLevel.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_MIXER.get())) {
            CobblestoneMixerRecipe recipe = recipeHolder.value();
            if (recipe.getFirstInput().test(stack) || recipe.getSecondInput().test(stack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Optional<CobblestoneMixerRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack firstInputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX);
        ItemStack secondInputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX);
        if (firstInputStack.isEmpty() || secondInputStack.isEmpty()) {
            return Optional.empty();
        }

        DoubleItemRecipeInput input = new DoubleItemRecipeInput(firstInputStack, secondInputStack);
        Optional<RecipeHolder<CobblestoneMixerRecipe>> recipeHolder = currentLevel.getRecipeManager().getRecipeFor(
            ModRecipeTypes.COBBLESTONE_MIXER.get(),
            input,
            currentLevel
        );
        return recipeHolder.map(RecipeHolder::value);
    }

    @Override
    protected boolean canProcessRecipe(CobblestoneMixerRecipe recipe) {
        return this.canOutput(recipe);
    }

    @Override
    protected boolean shouldResetProgress(CobblestoneMixerRecipe recipe) {
        return !this.canOutput(recipe);
    }

    @Override
    protected int getRecipeProcessingTime(CobblestoneMixerRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestoneMixerRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(CobblestoneMixerRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        ItemStack firstInputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_1_INDEX);
        ItemStack secondInputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_2_INDEX);
        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);

        Optional<int[]> matchedSlots = recipe.findMatchingItemSlots(new DoubleItemRecipeInput(firstInputStack, secondInputStack));
        if (matchedSlots.isEmpty()) {
            return;
        }

        int[] itemSlots = matchedSlots.get();
        this.itemStackHandler.getStackInSlot(itemSlots[0]).shrink(recipe.getFirstInput().count());
        this.itemStackHandler.getStackInSlot(itemSlots[1]).shrink(recipe.getSecondInput().count());

        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
    }

    private boolean canOutput(CobblestoneMixerRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return false;
        }

        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_mixer");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData mixerData = new ContainerData() {
            @Override
            public int get(int index) {
                return CobblestoneMixerBlockEntity.this.getPoweredMachineCommonData(index, 0);
            }

            @Override
            public void set(int index, int value) {
                CobblestoneMixerBlockEntity.this.setPoweredMachineCommonData(index, value, 0);
            }

            @Override
            public int getCount() {
                return CobblestoneMixerBlockEntity.this.getPoweredMachineDataCount(0);
            }
        };

        return new CobblestoneMixerMenu(containerId, playerInventory, this, mixerData);
    }
}
