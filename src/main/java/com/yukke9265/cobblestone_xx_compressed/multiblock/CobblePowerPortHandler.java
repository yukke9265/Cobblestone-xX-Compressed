package com.yukke9265.cobblestone_xx_compressed.multiblock;

import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import org.jetbrains.annotations.NotNull;

import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestonePowerHelper;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * 丸石入力ポート用 handler です。
 * 前提: 挿入された燃料／触媒をその場で CP へ変換する。
 * 結果: ポート側に実体スロットを持たない。
 */
public final class CobblePowerPortHandler implements IItemHandler {
    private final LongSupplier storedPowerSupplier;
    private final LongSupplier maxPowerSupplier;
    private final LongConsumer storedPowerSetter;
    private final Runnable onChanged;

    public CobblePowerPortHandler(
        LongSupplier storedPowerSupplier,
        LongSupplier maxPowerSupplier,
        LongConsumer storedPowerSetter,
        Runnable onChanged
    ) {
        this.storedPowerSupplier = storedPowerSupplier;
        this.maxPowerSupplier = maxPowerSupplier;
        this.storedPowerSetter = storedPowerSetter;
        this.onChanged = onChanged;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot != 0 || stack.isEmpty()) {
            return stack;
        }

        // ポートは一時保管スロットを持たないため、1 個消費する燃料だけを受け付ける。
        if (CobblestonePowerHelper.getFuelValue(stack) <= 0L) {
            return stack;
        }

        long stored = this.storedPowerSupplier.getAsLong();
        long max = this.maxPowerSupplier.getAsLong();
        ItemStack working = stack.copy();
        long updated = CobblestonePowerHelper.absorbPowerFromSlot(working, stored, max);
        if (updated == stored && working.getCount() == stack.getCount()) {
            return stack;
        }

        if (!simulate) {
            this.storedPowerSetter.accept(updated);
            this.onChanged.run();
        }

        return working;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return slot == 0 && CobblestonePowerHelper.getFuelValue(stack) > 0L;
    }
}
