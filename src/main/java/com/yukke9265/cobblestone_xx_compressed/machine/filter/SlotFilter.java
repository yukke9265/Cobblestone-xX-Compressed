package com.yukke9265.cobblestone_xx_compressed.machine.filter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/*
 * 方針:
 * 1つの入力経路に対するフィルタ本体です。
 * 最大9エントリと WL/BL モードを持ち、空なら制限なしとします。
 */
public final class SlotFilter {
    public static final int MAX_ENTRIES = 9;

    private static final String MODE_TAG = "Mode";
    private static final String ENTRIES_TAG = "Entries";

    private SlotFilterMode mode = SlotFilterMode.WHITELIST;
    private final List<SlotFilterEntry> entries = new ArrayList<>();

    public SlotFilterMode getMode() {
        return this.mode;
    }

    public void setMode(SlotFilterMode mode) {
        this.mode = mode == null ? SlotFilterMode.WHITELIST : mode;
    }

    public void cycleMode() {
        this.mode = this.mode.next();
    }

    public List<SlotFilterEntry> getEntries() {
        return List.copyOf(this.entries);
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public void clear() {
        this.entries.clear();
    }

    public void setEntriesFromDisplayStacks(List<ItemStack> displayStacks, FilterTargetType targetType) {
        this.entries.clear();
        int limit = Math.min(MAX_ENTRIES, displayStacks.size());
        for (int index = 0; index < limit; index++) {
            SlotFilterEntry entry = SlotFilterEntry.fromCarried(displayStacks.get(index), targetType);
            if (entry != null) {
                this.entries.add(entry);
            }
        }
    }

    public void setEntry(int index, SlotFilterEntry entry) {
        if (index < 0 || index >= MAX_ENTRIES) {
            return;
        }

        while (this.entries.size() <= index) {
            this.entries.add(null);
        }

        this.entries.set(index, entry);
        this.compactEntries();
    }

    public boolean allowsItem(ItemStack stack) {
        if (this.entries.isEmpty()) {
            return true;
        }

        boolean matched = false;
        for (SlotFilterEntry entry : this.entries) {
            if (entry != null && entry.matchesItem(stack)) {
                matched = true;
                break;
            }
        }

        return this.mode == SlotFilterMode.WHITELIST ? matched : !matched;
    }

    public boolean allowsFluid(FluidStack stack) {
        if (this.entries.isEmpty()) {
            return true;
        }

        boolean matched = false;
        for (SlotFilterEntry entry : this.entries) {
            if (entry != null && entry.matchesFluid(stack)) {
                matched = true;
                break;
            }
        }

        return this.mode == SlotFilterMode.WHITELIST ? matched : !matched;
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(MODE_TAG, this.mode.getId());

        ListTag entriesTag = new ListTag();
        for (SlotFilterEntry entry : this.entries) {
            if (entry != null) {
                entriesTag.add(entry.save(registries));
            }
        }
        tag.put(ENTRIES_TAG, entriesTag);
        return tag;
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        this.mode = SlotFilterMode.fromId(tag.getInt(MODE_TAG));
        this.entries.clear();

        ListTag entriesTag = tag.getList(ENTRIES_TAG, Tag.TAG_COMPOUND);
        for (int index = 0; index < entriesTag.size() && this.entries.size() < MAX_ENTRIES; index++) {
            SlotFilterEntry entry = SlotFilterEntry.load(entriesTag.getCompound(index), registries);
            if (entry != null) {
                this.entries.add(entry);
            }
        }
    }

    public SlotFilter copy() {
        SlotFilter copy = new SlotFilter();
        copy.mode = this.mode;
        copy.entries.addAll(this.entries);
        return copy;
    }

    private void compactEntries() {
        List<SlotFilterEntry> compacted = new ArrayList<>();
        for (SlotFilterEntry entry : this.entries) {
            if (entry != null) {
                compacted.add(entry);
            }
        }
        this.entries.clear();
        this.entries.addAll(compacted);
    }
}
