package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneCrusherMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;
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

public class CobblestoneCrusherBlockEntity extends PoweredMachineBlockEntityBase<CobblestoneCrusherRecipe> implements MenuProvider {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int POWER_SLOT_INDEX = 1;
    public static final int OUTPUT_SLOT_INDEX = 2;
    public static final int ACCELERATION_SLOT_INDEX = 3;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 4;
    public static final long MAX_COBBLESTONE_POWER = 4000L;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(5) {
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

            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneCrusherBlockEntity.this.setChanged();
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
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot == 0) {
                return CobblestoneCrusherBlockEntity.this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == 0) {
                return CobblestoneCrusherBlockEntity.this.itemStackHandler.insertItem(INPUT_SLOT_INDEX, stack, simulate);
            }

            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == 0) {
                return CobblestoneCrusherBlockEntity.this.itemStackHandler.getSlotLimit(INPUT_SLOT_INDEX);
            }

            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == 0) {
                return CobblestoneCrusherBlockEntity.this.itemStackHandler.isItemValid(INPUT_SLOT_INDEX, stack);
            }

            return false;
        }
    };

    private final IItemHandler cobblestoneInputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.getStackInSlot(POWER_SLOT_INDEX);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0) {
                return stack;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.insertItem(POWER_SLOT_INDEX, stack, simulate);
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot != 0) {
                return 0;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.getSlotLimit(POWER_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot != 0) {
                return false;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.isItemValid(POWER_SLOT_INDEX, stack);
        }
    };

    private final IItemHandler outputAutomationHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot != 0) {
                return 0;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.getSlotLimit(OUTPUT_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };

    private final IItemHandler automationAccessHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 5;
        }

        @Override
        public @Nonnull ItemStack getStackInSlot(int slot) {
            if (slot < 0 || slot >= 5) {
                return ItemStack.EMPTY;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == INPUT_SLOT_INDEX) {
                return CobblestoneCrusherBlockEntity.this.itemStackHandler.insertItem(slot, stack, simulate);
            }

            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != OUTPUT_SLOT_INDEX) {
                return ItemStack.EMPTY;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 0 || slot >= 5) {
                return 0;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot < 0 || slot >= 5) {
                return false;
            }

            return CobblestoneCrusherBlockEntity.this.itemStackHandler.isItemValid(slot, stack);
        }
    };

    public CobblestoneCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_CRUSHER_BLOCK_ENTITY.get(), pos, state);
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
        if (stack.isEmpty()) {
            return false;
        }

        Level currentLevel = this.level;
        if (currentLevel == null) {
            return false;
        }

        for (RecipeHolder<CobblestoneCrusherRecipe> recipeHolder : currentLevel.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_CRUSHER.get())) {
            if (recipeHolder.value().getIngredient().test(stack)) {
                return true;
            }
        }

        return false;
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
    protected Optional<CobblestoneCrusherRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        Optional<RecipeHolder<CobblestoneCrusherRecipe>> recipeHolder = currentLevel.getRecipeManager().getRecipeFor(
            ModRecipeTypes.COBBLESTONE_CRUSHER.get(),
            input,
            currentLevel
        );
        return recipeHolder.map(RecipeHolder::value);
    }

    @Override
    protected boolean canProcessRecipe(CobblestoneCrusherRecipe recipe) {
        return this.canOutput(recipe);
    }

    @Override
    protected boolean shouldResetProgress(CobblestoneCrusherRecipe recipe) {
        return !this.canOutput(recipe);
    }

    @Override
    protected int getRecipeProcessingTime(CobblestoneCrusherRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestoneCrusherRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(CobblestoneCrusherRecipe recipe) {
        this.craft(recipe);
    }

    private boolean canOutput(CobblestoneCrusherRecipe recipe) {
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

    private void craft(CobblestoneCrusherRecipe recipe) {
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
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_crusher");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData crusherData = new ContainerData() {
            @Override
            public int get(int index) {
                return CobblestoneCrusherBlockEntity.this.getPoweredMachineCommonData(index, 0);
            }

            @Override
            public void set(int index, int value) {
                CobblestoneCrusherBlockEntity.this.setPoweredMachineCommonData(index, value, 0);
            }

            @Override
            public int getCount() {
                return CobblestoneCrusherBlockEntity.this.getPoweredMachineDataCount(0);
            }
        };

        return new CobblestoneCrusherMenu(containerId, playerInventory, this, crusherData);
    }
}