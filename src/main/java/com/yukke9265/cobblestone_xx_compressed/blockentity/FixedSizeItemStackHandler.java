package com.yukke9265.cobblestone_xx_compressed.blockentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class FixedSizeItemStackHandler extends ItemStackHandler {
    private final int fixedSize;

    public FixedSizeItemStackHandler(int fixedSize) {
        super(fixedSize);
        this.fixedSize = fixedSize;
    }

    public void deserializeNBTKeepingSize(HolderLookup.Provider registries, CompoundTag tag) {
        super.deserializeNBT(registries, tag);
        this.ensureFixedSize();
    }

    public void ensureFixedSize() {
        if (this.getSlots() == this.fixedSize) {
            return;
        }

        List<ItemStack> existingStacks = new ArrayList<>();
        int copyCount = Math.min(this.getSlots(), this.fixedSize);
        for (int slotIndex = 0; slotIndex < copyCount; slotIndex++) {
            existingStacks.add(this.getStackInSlot(slotIndex).copy());
        }

        this.setSize(this.fixedSize);

        for (int slotIndex = 0; slotIndex < existingStacks.size(); slotIndex++) {
            this.setStackInSlot(slotIndex, existingStacks.get(slotIndex));
        }
    }
}