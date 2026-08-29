package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTarget;
import com.yukke9265.cobblestone_xx_compressed.machine.filter.FilterTargetIds;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestonePoweredCrafterMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestonePoweredCrafterRecipeHelper;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 3x3 に実アイテムを置き、バニラ作業台レシピを固定 CP で自動クラフトする機械です。
 *
 * 既定で最大 4 回分まで連続完了し、Parallel Chip はその回数へ乗算します。
 */
public class CobblestonePoweredCrafterBlockEntity extends PoweredMachineBlockEntityBase<CraftingRecipe> implements MenuProvider {
    public static final int INPUT_SLOT_1_INDEX = 0;
    public static final int INPUT_SLOT_2_INDEX = 1;
    public static final int INPUT_SLOT_3_INDEX = 2;
    public static final int INPUT_SLOT_4_INDEX = 3;
    public static final int INPUT_SLOT_5_INDEX = 4;
    public static final int INPUT_SLOT_6_INDEX = 5;
    public static final int INPUT_SLOT_7_INDEX = 6;
    public static final int INPUT_SLOT_8_INDEX = 7;
    public static final int INPUT_SLOT_9_INDEX = 8;
    public static final int POWER_SLOT_INDEX = 9;
    public static final int OUTPUT_SLOT_INDEX = 10;
    public static final int ACCELERATION_SLOT_INDEX = 11;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 12;
    public static final int PARALLEL_SLOT_INDEX = 13;
    public static final int FIRST_GRID_SLOT_INDEX = INPUT_SLOT_1_INDEX;
    public static final long MAX_COBBLESTONE_POWER = 16000L;
    private static final int BASE_CRAFT_COUNT = 4;
    private static final int[] GRID_SLOT_INDICES = new int[] {
        INPUT_SLOT_1_INDEX,
        INPUT_SLOT_2_INDEX,
        INPUT_SLOT_3_INDEX,
        INPUT_SLOT_4_INDEX,
        INPUT_SLOT_5_INDEX,
        INPUT_SLOT_6_INDEX,
        INPUT_SLOT_7_INDEX,
        INPUT_SLOT_8_INDEX,
        INPUT_SLOT_9_INDEX
    };
    private static final String[] GRID_FILTER_IDS = new String[] {
        FilterTargetIds.ITEM_INPUT_1,
        FilterTargetIds.ITEM_INPUT_2,
        FilterTargetIds.ITEM_INPUT_3,
        FilterTargetIds.ITEM_INPUT_4,
        FilterTargetIds.ITEM_INPUT_5,
        FilterTargetIds.ITEM_INPUT_6,
        FilterTargetIds.ITEM_INPUT_7,
        FilterTargetIds.ITEM_INPUT_8,
        FilterTargetIds.ITEM_INPUT_9
    };

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(14) {
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

            for (int gridIndex = 0; gridIndex < GRID_SLOT_INDICES.length; gridIndex++) {
                if (slot == GRID_SLOT_INDICES[gridIndex]) {
                    return CobblestonePoweredCrafterBlockEntity.this.getSlotFilters()
                        .allowsItem(GRID_FILTER_IDS[gridIndex], stack);
                }
            }

            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestonePoweredCrafterBlockEntity.this.setChanged();
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
        GRID_SLOT_INDICES
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
        GRID_SLOT_INDICES,
        new int[] {OUTPUT_SLOT_INDEX}
    );

    public CobblestonePoweredCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_POWERED_CRAFTER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public List<FilterTarget> getFilterTargets() {
        List<FilterTarget> targets = new ArrayList<>(GRID_SLOT_INDICES.length);
        for (int gridIndex = 0; gridIndex < GRID_SLOT_INDICES.length; gridIndex++) {
            targets.add(FilterTarget.item(
                GRID_FILTER_IDS[gridIndex],
                MachineGuiLayouts.PoweredCrafter.getInputSlotX(gridIndex),
                MachineGuiLayouts.PoweredCrafter.getInputSlotY(gridIndex)
            ));
        }

        return targets;
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public IItemHandler getAutomationItemHandler(Direction side) {
        return this.getConfiguredAutomationItemHandler(
            side,
            this.inputAutomationHandler,
            this.cobblestoneInputAutomationHandler,
            this.outputAutomationHandler,
            this.automationAccessHandler
        );
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

    /**
     * 既定 4 回 × (1 + Parallel 追加)。石破壊シミュレータの効率×Parallel と同じ乗算形です。
     */
    @Override
    protected int getCraftsPerCompletion() {
        ItemStack parallelStack = this.itemStackHandler.getStackInSlot(PARALLEL_SLOT_INDEX);
        int chipExtra = MachineUpgradeHelper.getParallelExtraCraftCount(parallelStack);
        return BASE_CRAFT_COUNT * (1 + chipExtra);
    }

    public boolean canQuickMoveToInput(ItemStack stack) {
        return !stack.isEmpty()
            && !isCobblestonePowerItem(stack)
            && !MachineUpgradeHelper.isAccelerationChip(stack)
            && !MachineUpgradeHelper.isEnergizedCube(stack)
            && !MachineUpgradeHelper.isParallelChip(stack);
    }

    @Override
    protected Optional<CraftingRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        CraftingInput craftingInput = CobblestonePoweredCrafterRecipeHelper.createCraftingInput(
            this.itemStackHandler,
            FIRST_GRID_SLOT_INDEX
        );
        return CobblestonePoweredCrafterRecipeHelper.findMatchingRecipe(currentLevel, craftingInput);
    }

    @Override
    protected boolean canProcessRecipe(CraftingRecipe recipe) {
        return this.canCraftOnce(recipe);
    }

    @Override
    protected boolean shouldResetProgress(CraftingRecipe recipe) {
        return !this.canCraftOnce(recipe);
    }

    @Override
    protected int getRecipeProcessingTime(CraftingRecipe recipe) {
        return CobblestonePoweredCrafterRecipeHelper.PROCESSING_TIME;
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CraftingRecipe recipe) {
        return CobblestonePoweredCrafterRecipeHelper.COBBLESTONE_POWER_PER_TICK;
    }

    @Override
    protected void finishProcessing(CraftingRecipe recipe) {
        this.craft(recipe);
    }

    private boolean canCraftOnce(CraftingRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return false;
        }

        CraftingInput.Positioned positioned = CobblestonePoweredCrafterRecipeHelper.createPositionedCraftingInput(
            this.itemStackHandler,
            FIRST_GRID_SLOT_INDEX
        );
        CraftingInput craftingInput = positioned.input();
        ItemStack resultStack = recipe.assemble(craftingInput, currentLevel.registryAccess());
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (!CobblestonePoweredCrafterRecipeHelper.canFitResult(outputStack, resultStack)) {
            return false;
        }

        NonNullList<ItemStack> remainingOnGrid = CobblestonePoweredCrafterRecipeHelper.expandRemainingItemsToGrid(
            positioned,
            recipe.getRemainingItems(craftingInput)
        );
        return CobblestonePoweredCrafterRecipeHelper.canApplyRemainingItems(
            this.itemStackHandler,
            FIRST_GRID_SLOT_INDEX,
            remainingOnGrid
        );
    }

    private void craft(CraftingRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        CraftingInput.Positioned positioned = CobblestonePoweredCrafterRecipeHelper.createPositionedCraftingInput(
            this.itemStackHandler,
            FIRST_GRID_SLOT_INDEX
        );
        CraftingInput craftingInput = positioned.input();
        ItemStack resultStack = recipe.assemble(craftingInput, currentLevel.registryAccess());
        NonNullList<ItemStack> remainingOnGrid = CobblestonePoweredCrafterRecipeHelper.expandRemainingItemsToGrid(
            positioned,
            recipe.getRemainingItems(craftingInput)
        );

        CobblestonePoweredCrafterRecipeHelper.consumeIngredientsAndApplyRemaining(
            this.itemStackHandler,
            FIRST_GRID_SLOT_INDEX,
            remainingOnGrid
        );

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_powered_crafter");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData poweredCrafterData = new ContainerData() {
            @Override
            public int get(int index) {
                return CobblestonePoweredCrafterBlockEntity.this.getPoweredMachineCommonData(index, 0);
            }

            @Override
            public void set(int index, int value) {
                CobblestonePoweredCrafterBlockEntity.this.setPoweredMachineCommonData(index, value, 0);
            }

            @Override
            public int getCount() {
                return CobblestonePoweredCrafterBlockEntity.this.getPoweredMachineDataCount(0);
            }
        };

        return new CobblestonePoweredCrafterMenu(containerId, playerInventory, this, poweredCrafterData);
    }
}
