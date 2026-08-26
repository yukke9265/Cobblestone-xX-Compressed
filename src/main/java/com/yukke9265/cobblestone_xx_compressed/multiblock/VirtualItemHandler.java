package com.yukke9265.cobblestone_xx_compressed.multiblock;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * VirtualItemBuffer を 1 スロット IItemHandlerModifiable として公開します。
 * insertOnly / extractOnly でポート種別と GUI 用を分けます。
 */
public final class VirtualItemHandler implements IItemHandlerModifiable {
    private final VirtualItemBuffer buffer;
    private final boolean allowInsert;
    private final boolean allowExtract;
    private final Runnable onChanged;

    public VirtualItemHandler(
        VirtualItemBuffer buffer,
        boolean allowInsert,
        boolean allowExtract,
        Runnable onChanged
    ) {
        this.buffer = buffer;
        this.allowInsert = allowInsert;
        this.allowExtract = allowExtract;
        this.onChanged = onChanged;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }

        return this.buffer.createDisplayStack();
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (slot != 0) {
            return;
        }

        // スロット同期用: 表示スタックをバッファへ反映する。
        if (stack.isEmpty()) {
            this.buffer.extract(Integer.MAX_VALUE, false);
            this.onChanged.run();
            return;
        }

        if (!this.buffer.isEmpty() && !ItemStack.isSameItemSameComponents(this.buffer.getTemplateStack(), stack)) {
            this.buffer.extract(Integer.MAX_VALUE, false);
        }

        long current = this.buffer.getCount();
        long target = stack.getCount();
        if (this.buffer.isEmpty()) {
            this.buffer.insert(stack.copy(), false);
        } else if (target > current) {
            ItemStack toAdd = stack.copyWithCount((int) Math.min(Integer.MAX_VALUE, target - current));
            this.buffer.insert(toAdd, false);
        } else if (target < current) {
            this.buffer.extract((int) Math.min(Integer.MAX_VALUE, current - target), false);
        }

        this.onChanged.run();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot != 0 || !this.allowInsert || stack.isEmpty()) {
            return stack;
        }

        ItemStack remainder = this.buffer.insert(stack, simulate);
        if (!simulate && remainder.getCount() != stack.getCount()) {
            this.onChanged.run();
        }

        return remainder;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != 0 || !this.allowExtract || amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack extracted = this.buffer.extract(amount, simulate);
        if (!simulate && !extracted.isEmpty()) {
            this.onChanged.run();
        }

        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.allowInsert && slot == 0 && this.buffer.canAccept(stack);
    }
}
