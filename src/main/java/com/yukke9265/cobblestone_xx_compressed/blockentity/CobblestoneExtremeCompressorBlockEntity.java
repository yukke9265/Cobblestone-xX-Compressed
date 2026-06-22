package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneExtremeCompressorMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneExtremeCompressorRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import org.jetbrains.annotations.NotNull;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CobblestoneExtremeCompressorBlockEntity extends PoweredMachineBlockEntityBase<CobblestoneExtremeCompressorRecipe> implements MenuProvider {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int POWER_SLOT_INDEX = 1;
    public static final int OUTPUT_SLOT_INDEX = 2;
    public static final int ACCELERATION_SLOT_INDEX = 3;
    public static final int ENERGIZED_CUBE_SLOT_INDEX = 4;
    public static final long MAX_COBBLESTONE_POWER = 33554432000L;

    private static final int MACHINE_SPECIFIC_DATA_COUNT = 3;
    private static final int DATA_INDEX_STORED_ITEM_COUNT = DATA_INDEX_MACHINE_SPECIFIC_START;
    private static final int DATA_INDEX_REQUIRED_ITEM_COUNT = DATA_INDEX_MACHINE_SPECIFIC_START + 1;
    private static final int DATA_INDEX_STORED_ITEM_ID = DATA_INDEX_MACHINE_SPECIFIC_START + 2;

    private int storedInputItemCount;
    private int currentRequiredItemCount;
    private ItemStack storedInputTemplate = ItemStack.EMPTY;

    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(5) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
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

            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneExtremeCompressorBlockEntity.this.setChanged();
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
                return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == 0) {
                return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.insertItem(INPUT_SLOT_INDEX, stack, simulate);
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
                return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.getSlotLimit(INPUT_SLOT_INDEX);
            }

            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == 0) {
                return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.isItemValid(INPUT_SLOT_INDEX, stack);
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

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.getStackInSlot(POWER_SLOT_INDEX);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0) {
                return stack;
            }

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.insertItem(POWER_SLOT_INDEX, stack, simulate);
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

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.getSlotLimit(POWER_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot != 0) {
                return false;
            }

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.isItemValid(POWER_SLOT_INDEX, stack);
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

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
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

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot != 0) {
                return 0;
            }

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.getSlotLimit(OUTPUT_SLOT_INDEX);
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

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == INPUT_SLOT_INDEX) {
                return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.insertItem(slot, stack, simulate);
            }

            return stack;
        }

        @Override
        public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != OUTPUT_SLOT_INDEX) {
                return ItemStack.EMPTY;
            }

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < 0 || slot >= 5) {
                return 0;
            }

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot < 0 || slot >= 5) {
                return false;
            }

            return CobblestoneExtremeCompressorBlockEntity.this.itemStackHandler.isItemValid(slot, stack);
        }
    };

    public CobblestoneExtremeCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_EXTREME_COMPRESSOR_BLOCK_ENTITY.get(), pos, state);
    }

    public int getStoredInputItemCount() {
        return this.storedInputItemCount;
    }

    public int getCurrentRequiredItemCount() {
        return this.currentRequiredItemCount;
    }

    public ItemStack getStoredInputTemplate() {
        if (this.storedInputTemplate.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return this.storedInputTemplate.copy();
    }

    public int getStoredInputItemId() {
        if (this.storedInputTemplate.isEmpty()) {
            return -1;
        }

        return BuiltInRegistries.ITEM.getId(this.storedInputTemplate.getItem());
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
    protected void onStopped() {
        // 停止直後は内部に貯め込んだ途中材料の回収だけ継承先で続けます。
        // 共通基底側の reverseIsAvailable で進捗自体は既に破棄しています。
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
    protected void onRecipeStateChanged(Optional<CobblestoneExtremeCompressorRecipe> recipeOptional) {
        this.updateDisplayedRecipeState(recipeOptional);
    }

    @Override
    protected void onUnavailableTick() {
        this.tryReturnStoredItemsToOutput();
        this.updateDisplayedRecipeState(Optional.empty());
    }

    private void updateDisplayedRecipeState(Optional<CobblestoneExtremeCompressorRecipe> recipeOptional) {
        if (recipeOptional.isPresent()) {
            int requiredItemCount = recipeOptional.get().getRequiredItemCount();
            if (this.currentRequiredItemCount != requiredItemCount) {
                this.currentRequiredItemCount = requiredItemCount;
                this.setChanged();
            }
            return;
        }

        if (this.storedInputItemCount <= 0 && this.currentRequiredItemCount != 0) {
            this.currentRequiredItemCount = 0;
            this.setChanged();
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

        for (RecipeHolder<CobblestoneExtremeCompressorRecipe> recipeHolder : currentLevel.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COBBLESTONE_EXTREME_COMPRESSOR.get())) {
            if (recipeHolder.value().getIngredient().test(stack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Optional<CobblestoneExtremeCompressorRecipe> findMatchingRecipe() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return Optional.empty();
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }

        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        Optional<RecipeHolder<CobblestoneExtremeCompressorRecipe>> recipeHolder = currentLevel.getRecipeManager().getRecipeFor(
            ModRecipeTypes.COBBLESTONE_EXTREME_COMPRESSOR.get(),
            input,
            currentLevel
        );
        return recipeHolder.map(RecipeHolder::value);
    }

    private boolean canContinueRecipe(CobblestoneExtremeCompressorRecipe recipe) {
        if (!this.canAcceptCurrentInput(recipe)) {
            return false;
        }

        if (this.storedInputItemCount >= recipe.getRequiredItemCount()) {
            return this.canOutputResult(recipe);
        }

        return true;
    }

    @Override
    protected boolean canProcessRecipe(CobblestoneExtremeCompressorRecipe recipe) {
        return this.canContinueRecipe(recipe);
    }

    @Override
    protected boolean shouldResetProgress(CobblestoneExtremeCompressorRecipe recipe) {
        if (!this.canAcceptCurrentInput(recipe)) {
            return true;
        }

        if (this.storedInputItemCount >= recipe.getRequiredItemCount() && !this.canOutputResult(recipe)) {
            return true;
        }

        return false;
    }

    private boolean canAcceptCurrentInput(CobblestoneExtremeCompressorRecipe recipe) {
        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty()) {
            return false;
        }

        if (!recipe.getIngredient().test(inputStack)) {
            return false;
        }

        if (this.storedInputTemplate.isEmpty() || this.storedInputItemCount <= 0) {
            return true;
        }

        return ItemStack.isSameItemSameComponents(this.storedInputTemplate, inputStack);
    }

    @Override
    protected int getRecipeProcessingTime(CobblestoneExtremeCompressorRecipe recipe) {
        return recipe.getProcessingTime();
    }

    @Override
    protected long getRecipeCobblestonePowerPerTick(CobblestoneExtremeCompressorRecipe recipe) {
        return recipe.getCobblestonePowerPerTick();
    }

    @Override
    protected void finishProcessing(CobblestoneExtremeCompressorRecipe recipe) {
        this.finishSingleInput(recipe);
    }

    private void finishSingleInput(CobblestoneExtremeCompressorRecipe recipe) {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        ItemStack inputStack = this.itemStackHandler.getStackInSlot(INPUT_SLOT_INDEX);
        if (inputStack.isEmpty()) {
            return;
        }

        if (this.storedInputTemplate.isEmpty() || this.storedInputItemCount <= 0) {
            this.storedInputTemplate = inputStack.copyWithCount(1);
        }

        inputStack.shrink(1);
        this.storedInputItemCount++;
        this.currentRequiredItemCount = recipe.getRequiredItemCount();

        if (this.storedInputItemCount >= recipe.getRequiredItemCount() && this.canOutputResult(recipe)) {
            this.outputCraftedResult(recipe, currentLevel);
            this.storedInputItemCount = 0;
            this.currentRequiredItemCount = 0;
            this.storedInputTemplate = ItemStack.EMPTY;
        }
    }

    private boolean canOutputResult(CobblestoneExtremeCompressorRecipe recipe) {
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

    private void outputCraftedResult(CobblestoneExtremeCompressorRecipe recipe, Level currentLevel) {
        ItemStack resultStack = recipe.getResultItem(currentLevel.registryAccess());
        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);

        if (outputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, resultStack.copy());
            return;
        }

        outputStack.grow(resultStack.getCount());
    }

    private void tryReturnStoredItemsToOutput() {
        if (this.storedInputItemCount <= 0 || this.storedInputTemplate.isEmpty()) {
            return;
        }

        ItemStack outputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (!outputStack.isEmpty() && !ItemStack.isSameItemSameComponents(outputStack, this.storedInputTemplate)) {
            return;
        }

        int movableCount;
        if (outputStack.isEmpty()) {
            movableCount = Math.min(this.storedInputItemCount, this.storedInputTemplate.getMaxStackSize());
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, this.storedInputTemplate.copyWithCount(movableCount));
        } else {
            int freeSpace = outputStack.getMaxStackSize() - outputStack.getCount();
            movableCount = Math.min(this.storedInputItemCount, freeSpace);
            if (movableCount <= 0) {
                return;
            }

            outputStack.grow(movableCount);
        }

        this.storedInputItemCount -= movableCount;
        if (this.storedInputItemCount <= 0) {
            this.storedInputItemCount = 0;
            this.currentRequiredItemCount = 0;
            this.storedInputTemplate = ItemStack.EMPTY;
        }
        this.setChanged();
    }

    @Override
    protected void saveAdditionalPoweredMachineData(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        tag.putInt("storedInputItemCount", this.storedInputItemCount);
        tag.putInt("currentRequiredItemCount", this.currentRequiredItemCount);
        if (!this.storedInputTemplate.isEmpty()) {
            tag.put("storedInputTemplate", this.storedInputTemplate.save(registries));
        }
    }

    @Override
    protected void loadAdditionalPoweredMachineData(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        this.storedInputItemCount = tag.getInt("storedInputItemCount");
        this.currentRequiredItemCount = tag.getInt("currentRequiredItemCount");
        if (tag.contains("storedInputTemplate", Tag.TAG_COMPOUND)) {
            this.storedInputTemplate = ItemStack.parseOptional(registries, tag.getCompound("storedInputTemplate"));
        } else {
            this.storedInputTemplate = ItemStack.EMPTY;
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_extreme_compressor");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData extremeCompressorData = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == DATA_INDEX_STORED_ITEM_COUNT) {
                    return CobblestoneExtremeCompressorBlockEntity.this.storedInputItemCount;
                }

                if (index == DATA_INDEX_REQUIRED_ITEM_COUNT) {
                    return CobblestoneExtremeCompressorBlockEntity.this.currentRequiredItemCount;
                }

                if (index == DATA_INDEX_STORED_ITEM_ID) {
                    return CobblestoneExtremeCompressorBlockEntity.this.getStoredInputItemId();
                }

                return CobblestoneExtremeCompressorBlockEntity.this.getPoweredMachineCommonData(index, MACHINE_SPECIFIC_DATA_COUNT);
            }

            @Override
            public void set(int index, int value) {
                if (index == DATA_INDEX_STORED_ITEM_COUNT) {
                    CobblestoneExtremeCompressorBlockEntity.this.storedInputItemCount = value;
                    return;
                }

                if (index == DATA_INDEX_REQUIRED_ITEM_COUNT) {
                    CobblestoneExtremeCompressorBlockEntity.this.currentRequiredItemCount = value;
                    return;
                }

                CobblestoneExtremeCompressorBlockEntity.this.setPoweredMachineCommonData(index, value, MACHINE_SPECIFIC_DATA_COUNT);
            }

            @Override
            public int getCount() {
                return CobblestoneExtremeCompressorBlockEntity.this.getPoweredMachineDataCount(MACHINE_SPECIFIC_DATA_COUNT);
            }
        };

        return new CobblestoneExtremeCompressorMenu(containerId, playerInventory, this, extremeCompressorData);
    }
}