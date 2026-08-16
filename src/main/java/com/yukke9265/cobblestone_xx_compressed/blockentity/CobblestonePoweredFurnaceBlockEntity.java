package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.menu.CobblestonePoweredFurnaceMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestonePoweredFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestonePoweredFurnaceRecipeHelper;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CobblestonePoweredFurnaceBlockEntity extends PoweredMachineBlockEntityBase<CobblestonePoweredFurnaceRecipe> implements MenuProvider {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int POWER_SLOT_INDEX = 1;
    public static final int OUTPUT_SLOT_INDEX = 2;
    public static final int ACCELERATION_SLOT_INDEX = 3;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 4;
    public static final int PARALLEL_SLOT_INDEX = 5;
    public static final long MAX_COBBLESTONE_POWER = 1000L;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(6) {
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

            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestonePoweredFurnaceBlockEntity.this.setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == ACCELERATION_SLOT_INDEX || slot == ENERGIZED_CUBE_SLOT_INDEX || slot == PARALLEL_SLOT_INDEX) {
                return 1;
            }

            return super.getSlotLimit(slot);
        }
    };

    // 各面の mode が公開する入出力ルールだけを、この機械で明示します。
    // handler の定型処理は helper 側へ集約し、従来と同じ inventory へ委譲します。
    private final IItemHandler inputAutomationHandler = AutomationItemHandlerHelper.createInsertOnlyHandler(
        this.itemStackHandler,
        INPUT_SLOT_INDEX
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
        INPUT_SLOT_INDEX,
        OUTPUT_SLOT_INDEX
    );

    public CobblestonePoweredFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_POWERED_FURNACE_BLOCK_ENTITY.get(), pos, state);
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

    @SuppressWarnings("null")
    public boolean canQuickMoveToInput(ItemStack stack) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return false;
        }

        return CobblestonePoweredFurnaceRecipeHelper.isValidInput(currentLevel, stack);
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
    protected Optional<CobblestonePoweredFurnaceRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        return CobblestonePoweredFurnaceRecipeHelper.findMatchingRecipe(currentLevel, inputStack);
    }

    @Override
    protected boolean canProcessRecipe(CobblestonePoweredFurnaceRecipe recipe) {
        return this.canOutput(recipe);
    }

    @Override
    protected boolean shouldResetProgress(CobblestonePoweredFurnaceRecipe recipe) {
        return !this.canOutput(recipe);
    }

    @Override
    protected int getRecipeProcessingTime(CobblestonePoweredFurnaceRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestonePoweredFurnaceRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(CobblestonePoweredFurnaceRecipe recipe) {
        this.craft(recipe);
    }

    private boolean canOutput(CobblestonePoweredFurnaceRecipe recipe) {
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

    private void craft(CobblestonePoweredFurnaceRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);

        inputStack.shrink(1);

        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_powered_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData poweredFurnaceData = new ContainerData() {
            @Override
            public int get(int index) {
                return CobblestonePoweredFurnaceBlockEntity.this.getPoweredMachineCommonData(index, 0);
            }

            @Override
            public void set(int index, int value) {
                CobblestonePoweredFurnaceBlockEntity.this.setPoweredMachineCommonData(index, value, 0);
            }

            @Override
            public int getCount() {
                return CobblestonePoweredFurnaceBlockEntity.this.getPoweredMachineDataCount(0);
            }
        };

        return new CobblestonePoweredFurnaceMenu(containerId, playerInventory, this, poweredFurnaceData);
    }
}
