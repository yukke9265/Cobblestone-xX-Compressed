package com.yukke9265.cobblestone_xx_compressed.multiblock;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * アイテム種別 + long 個数の仮想バッファです。
 * 前提: 外部 IItemHandler は通常スタック単位で出し入れする。
 * 結果: コア内では大量個数を軽量に保持できる。
 */
public final class VirtualItemBuffer {
    private static final String TAG_STACK = "Stack";
    private static final String TAG_COUNT = "Count";

    private ItemStack templateStack = ItemStack.EMPTY;
    private long count;
    private final long capacity;

    public VirtualItemBuffer(long capacity) {
        this.capacity = Math.max(1L, capacity);
    }

    public long getCapacity() {
        return this.capacity;
    }

    public long getCount() {
        return this.count;
    }

    public boolean isEmpty() {
        return this.count <= 0L || this.templateStack.isEmpty();
    }

    public ItemStack getTemplateStack() {
        return this.templateStack;
    }

    /**
     * GUI / capability 表示用。表示個数はスタック上限で丸める。
     */
    public ItemStack createDisplayStack() {
        if (this.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack display = this.templateStack.copy();
        int shown = (int) Math.min(display.getMaxStackSize(), this.count);
        display.setCount(Math.max(1, shown));
        return display;
    }

    public boolean canAccept(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (this.isEmpty()) {
            return true;
        }

        return ItemStack.isSameItemSameComponents(this.templateStack, stack);
    }

    public long getFreeSpace() {
        return Math.max(0L, this.capacity - this.count);
    }

    /**
     * 可能な限り挿入し、残りスタックを返す。
     */
    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !this.canAccept(stack)) {
            return stack;
        }

        long freeSpace = this.getFreeSpace();
        if (freeSpace <= 0L) {
            return stack;
        }

        long toInsert = Math.min(freeSpace, stack.getCount());
        if (toInsert <= 0L) {
            return stack;
        }

        if (!simulate) {
            if (this.isEmpty()) {
                this.templateStack = stack.copyWithCount(1);
            }
            this.count += toInsert;
        }

        ItemStack remainder = stack.copy();
        remainder.shrink((int) toInsert);
        return remainder;
    }

    /**
     * 指定個数まで取り出し、取り出せたスタックを返す。
     */
    public ItemStack extract(int maxAmount, boolean simulate) {
        if (this.isEmpty() || maxAmount <= 0) {
            return ItemStack.EMPTY;
        }

        int extractAmount = (int) Math.min(maxAmount, Math.min(this.templateStack.getMaxStackSize(), this.count));
        if (extractAmount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack extracted = this.templateStack.copyWithCount(extractAmount);
        if (!simulate) {
            this.count -= extractAmount;
            if (this.count <= 0L) {
                this.count = 0L;
                this.templateStack = ItemStack.EMPTY;
            }
        }

        return extracted;
    }

    /**
     * レシピ消費用。足りなければ false。
     */
    public boolean consume(long amount) {
        if (amount <= 0L) {
            return true;
        }

        if (this.count < amount) {
            return false;
        }

        this.count -= amount;
        if (this.count <= 0L) {
            this.count = 0L;
            this.templateStack = ItemStack.EMPTY;
        }

        return true;
    }

    /**
     * レシピ出力用。空きが足りなければ false。
     */
    public boolean tryProduce(ItemStack stack, long amount) {
        if (stack.isEmpty() || amount <= 0L) {
            return false;
        }

        if (this.isEmpty()) {
            if (amount > this.capacity) {
                return false;
            }

            this.templateStack = stack.copyWithCount(1);
            this.count = amount;
            return true;
        }

        if (!ItemStack.isSameItemSameComponents(this.templateStack, stack)) {
            return false;
        }

        if (this.count + amount > this.capacity) {
            return false;
        }

        this.count += amount;
        return true;
    }

    public boolean canProduce(ItemStack stack, long amount) {
        if (stack.isEmpty() || amount <= 0L) {
            return false;
        }

        if (this.isEmpty()) {
            return amount <= this.capacity;
        }

        if (!ItemStack.isSameItemSameComponents(this.templateStack, stack)) {
            return false;
        }

        return this.count + amount <= this.capacity;
    }

    public void save(CompoundTag tag, HolderLookup.Provider registries) {
        if (!this.templateStack.isEmpty()) {
            tag.put(TAG_STACK, this.templateStack.save(registries));
        }
        tag.putLong(TAG_COUNT, this.count);
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        this.templateStack = ItemStack.EMPTY;
        this.count = 0L;

        if (tag.contains(TAG_STACK)) {
            this.templateStack = ItemStack.parseOptional(registries, tag.getCompound(TAG_STACK));
        }

        this.count = Math.max(0L, tag.getLong(TAG_COUNT));
        if (this.templateStack.isEmpty() || this.count <= 0L) {
            this.templateStack = ItemStack.EMPTY;
            this.count = 0L;
        }
    }
}
