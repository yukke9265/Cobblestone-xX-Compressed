package com.yukke9265.cobblestone_xx_compressed.blockentity;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class CobblestoneGeneratorBlockEntity extends BaseBlockEntity {
    private static final int OUTPUT_SLOT_INDEX = 0;

    private final ModBlocks.TierCobblestoneGenerator generatorVariant;

    // この機械は出力専用 1 スロットだけを持ちます。
    // 中身が変わったら保存対象が変わるため、必ず setChanged() を呼びます。
    private final FixedSizeItemStackHandler itemStackHandler = new FixedSizeItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            CobblestoneGeneratorBlockEntity.this.setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };

    // 全面出力なので、外部からは 1 スロットの搬出専用ハンドラだけを返します。
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

            return CobblestoneGeneratorBlockEntity.this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
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

            return CobblestoneGeneratorBlockEntity.this.itemStackHandler.extractItem(OUTPUT_SLOT_INDEX, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot != 0) {
                return 0;
            }

            return CobblestoneGeneratorBlockEntity.this.itemStackHandler.getSlotLimit(OUTPUT_SLOT_INDEX);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };

    public CobblestoneGeneratorBlockEntity(BlockPos pos, BlockState state, ModBlocks.TierCobblestoneGenerator generatorVariant) {
        super(ModBlockEntities.COBBLESTONE_GENERATOR_BLOCK_ENTITY.get(), pos, state);
        this.generatorVariant = generatorVariant;

        // 全面出力専用の機械なので、6 面すべて OUTPUT に固定します。
        for (int faceIndex = 0; faceIndex < AUTOMATION_FACE_COUNT; faceIndex++) {
            this.setAutomationMode(faceIndex, AutomationMode.OUTPUT);
        }
    }

    public ModBlocks.TierCobblestoneGenerator getGeneratorVariant() {
        return this.generatorVariant;
    }

    public IItemHandler getAutomationItemHandler(Direction side) {
        return this.outputAutomationHandler;
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        this.generateOutput();
        this.pushOutputToAdjacentInventories();
    }

    private void generateOutput() {
        ItemStack currentOutputStack = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        ItemStack producedStack = new ItemStack(this.generatorVariant.getProducedBlock().get(), this.generatorVariant.getProductionPerTick());

        if (currentOutputStack.isEmpty()) {
            this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, producedStack);
            return;
        }

        if (!ItemStack.isSameItemSameComponents(currentOutputStack, producedStack)) {
            return;
        }

        int maxStackSize = currentOutputStack.getMaxStackSize();
        int availableSpace = maxStackSize - currentOutputStack.getCount();
        if (availableSpace <= 0) {
            return;
        }

        int addCount = Math.min(availableSpace, this.generatorVariant.getProductionPerTick());
        currentOutputStack.grow(addCount);
        this.setChanged();
    }

    private void pushOutputToAdjacentInventories() {
        Level currentLevel = this.level;
        if (currentLevel == null) {
            return;
        }

        ItemStack remainingOutput = this.itemStackHandler.getStackInSlot(OUTPUT_SLOT_INDEX);
        if (remainingOutput.isEmpty()) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (remainingOutput.isEmpty()) {
                break;
            }

            BlockPos targetPos = this.worldPosition.relative(direction);
            IItemHandler targetHandler = currentLevel.getCapability(
                Capabilities.ItemHandler.BLOCK,
                targetPos,
                direction.getOpposite()
            );
            if (targetHandler == null) {
                continue;
            }

            remainingOutput = ItemHandlerHelper.insertItem(targetHandler, remainingOutput, false);
        }

        this.itemStackHandler.setStackInSlot(OUTPUT_SLOT_INDEX, remainingOutput);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.saveAutomationModes(tag);
        tag.put("inventory", this.itemStackHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.loadAutomationModes(tag);

        if (tag.contains("inventory", CompoundTag.TAG_COMPOUND)) {
            this.itemStackHandler.deserializeNBTKeepingSize(registries, tag.getCompound("inventory"));
        }
    }
}