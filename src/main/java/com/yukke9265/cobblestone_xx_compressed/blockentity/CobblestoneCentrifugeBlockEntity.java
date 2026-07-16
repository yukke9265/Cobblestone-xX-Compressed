package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCentrifugeMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCentrifugeRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

/**
 * 2 種類の結果を作る powered machine です。
 *
 * CP、progress、保存、同期、停止時の処理は PoweredMachineBlockEntityBase に統一し、
 * 2 出力の作成、個別出力面、出力 1 から出力 2 への搬出順だけをこのクラスに残します。
 */
public class CobblestoneCentrifugeBlockEntity extends PoweredMachineBlockEntityBase<CobblestoneCentrifugeRecipe> implements MenuProvider {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int POWER_SLOT_INDEX = 1;
    public static final int OUTPUT_SLOT_1_INDEX = 2;
    public static final int OUTPUT_SLOT_2_INDEX = 3;
    public static final int ACCELERATION_SLOT_INDEX = 4;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 5;
    public static final long MAX_COBBLESTONE_POWER = 64000L;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(6) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == OUTPUT_SLOT_1_INDEX || slot == OUTPUT_SLOT_2_INDEX) {
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

            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneCentrifugeBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    // OUTPUT は二つの出力を順に公開し、IN_OUT は入力 1 枠と出力 2 枠だけを実際に移動できます。
    private final IItemHandler inputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        INPUT_SLOT_INDEX
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
    private final IItemHandler outputAutomationHandler = AutomationItemHandlerHelper.createMultipleExtractOnlyHandler(
        this.itemStackHandler,
        OUTPUT_SLOT_1_INDEX,
        OUTPUT_SLOT_2_INDEX
    );
    private final IItemHandler automationAccessHandler = AutomationItemHandlerHelper.createRestrictedAccessHandler(
        this.itemStackHandler,
        new int[] {INPUT_SLOT_INDEX},
        new int[] {OUTPUT_SLOT_1_INDEX, OUTPUT_SLOT_2_INDEX}
    );

    public CobblestoneCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_CENTRIFUGE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

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

    @Override
    protected long getBaseMaxCobblestonePower() {
        return MAX_COBBLESTONE_POWER;
    }

    @Override
    protected int getPowerSlotIndex() {
        return POWER_SLOT_INDEX;
    }

    /**
     * 標準の単一出力用の抽象メソッドに対しては、第 1 出力を返します。
     * 実際の搬出は pushOutputsToConfiguredSides() を override して 2 枠とも処理します。
     */
    @Override
    protected int getOutputSlotIndex() {
        return OUTPUT_SLOT_1_INDEX;
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

        for (RecipeHolder<CobblestoneCentrifugeRecipe> recipeHolder : currentLevel.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CENTRIFUGE.get())) {
            if (recipeHolder.value().getIngredient().test(stack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Optional<CobblestoneCentrifugeRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        Optional<RecipeHolder<CobblestoneCentrifugeRecipe>> recipeHolder = currentLevel.getRecipeManager().getRecipeFor(
            ModRecipeTypes.COBBLESTONE_CENTRIFUGE.get(),
            input,
            currentLevel
        );
        return recipeHolder.map(RecipeHolder::value);
    }

    @Override
    protected boolean canProcessRecipe(CobblestoneCentrifugeRecipe recipe) {
        return this.canAcceptResult(OUTPUT_SLOT_1_INDEX, recipe.getFirstResult())
            && this.canAcceptResult(OUTPUT_SLOT_2_INDEX, recipe.getSecondResult());
    }

    @Override
    protected boolean shouldResetProgress(CobblestoneCentrifugeRecipe recipe) {
        return !this.canProcessRecipe(recipe);
    }

    @Override
    protected int getRecipeProcessingTime(CobblestoneCentrifugeRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestoneCentrifugeRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(CobblestoneCentrifugeRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        inputStack.shrink(1);

        this.insertResult(OUTPUT_SLOT_1_INDEX, recipe.rollFirstResult(currentLevel.random));
        this.insertResult(OUTPUT_SLOT_2_INDEX, recipe.rollSecondResult(currentLevel.random));
    }

    private boolean canAcceptResult(int slotIndex, ItemStack resultStack) {
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

    private void insertResult(int slotIndex, ItemStack resultStack) {
        if (resultStack.isEmpty()) {
            return;
        }

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(slotIndex);
        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(slotIndex, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
    }

    /**
     * 第 1 出力を先に搬出してから、第 2 出力を搬出します。
     * 個別出力面と全出力面の両方を従来どおり対象にします。
     */
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
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_centrifuge");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData centrifugeData = new ContainerData() {
            @Override
            public int get(int index) {
                return CobblestoneCentrifugeBlockEntity.this.getPoweredMachineCommonData(index, 0);
            }

            @Override
            public void set(int index, int value) {
                CobblestoneCentrifugeBlockEntity.this.setPoweredMachineCommonData(index, value, 0);
            }

            @Override
            public int getCount() {
                return CobblestoneCentrifugeBlockEntity.this.getPoweredMachineDataCount(0);
            }
        };

        return new CobblestoneCentrifugeMenu(containerId, playerInventory, this, centrifugeData);
    }
}
