package com.yukke9265.cobblestone_xx_compressed.machine.filter;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/*
 * 方針:
 * 機械全体のスロット別フィルタ辞書です。
 * キーは FilterTarget.id で、未登録キーは常に制限なしとして扱います。
 */
public final class MachineSlotFilters {
    private static final String ROOT_TAG = "SlotFilters";

    private final Map<String, SlotFilter> filtersByTargetId = new HashMap<>();

    public SlotFilter getOrCreate(String targetId) {
        return this.filtersByTargetId.computeIfAbsent(targetId, ignored -> new SlotFilter());
    }

    public SlotFilter get(String targetId) {
        return this.filtersByTargetId.get(targetId);
    }

    public boolean allowsItem(String targetId, ItemStack stack) {
        SlotFilter filter = this.filtersByTargetId.get(targetId);
        if (filter == null) {
            return true;
        }
        return filter.allowsItem(stack);
    }

    public boolean allowsFluid(String targetId, FluidStack stack) {
        SlotFilter filter = this.filtersByTargetId.get(targetId);
        if (filter == null) {
            return true;
        }
        return filter.allowsFluid(stack);
    }

    public void clear() {
        this.filtersByTargetId.clear();
    }

    public void copyFrom(MachineSlotFilters other) {
        this.filtersByTargetId.clear();
        for (Map.Entry<String, SlotFilter> entry : other.filtersByTargetId.entrySet()) {
            this.filtersByTargetId.put(entry.getKey(), entry.getValue().copy());
        }
    }

    public void save(CompoundTag parent, HolderLookup.Provider registries) {
        CompoundTag root = new CompoundTag();
        for (Map.Entry<String, SlotFilter> entry : this.filtersByTargetId.entrySet()) {
            if (!entry.getValue().isEmpty() || entry.getValue().getMode() == SlotFilterMode.BLACKLIST) {
                root.put(entry.getKey(), entry.getValue().save(registries));
            }
        }
        parent.put(ROOT_TAG, root);
    }

    public void load(CompoundTag parent, HolderLookup.Provider registries) {
        this.filtersByTargetId.clear();
        if (!parent.contains(ROOT_TAG, Tag.TAG_COMPOUND)) {
            return;
        }

        CompoundTag root = parent.getCompound(ROOT_TAG);
        for (String key : root.getAllKeys()) {
            SlotFilter filter = new SlotFilter();
            filter.load(root.getCompound(key), registries);
            this.filtersByTargetId.put(key, filter);
        }
    }
}
