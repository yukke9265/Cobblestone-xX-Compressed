package com.yukke9265.cobblestone_xx_compressed.machine.filter;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

/*
 * 方針:
 * フィルタの1枠は「アイテム種類」または「流体種類」だけを保持します。
 * ghost 表示用に ItemStack を返し、照合では種類一致だけを見ます。
 */
public final class SlotFilterEntry {
    private static final String TYPE_TAG = "Type";
    private static final String ITEM_TAG = "Item";
    private static final String FLUID_TAG = "Fluid";
    private static final String TYPE_ITEM = "item";
    private static final String TYPE_FLUID = "fluid";

    private final FilterTargetType type;
    private final ItemStack itemStack;
    private final FluidStack fluidStack;

    private SlotFilterEntry(FilterTargetType type, ItemStack itemStack, FluidStack fluidStack) {
        this.type = type;
        this.itemStack = itemStack;
        this.fluidStack = fluidStack;
    }

    public static SlotFilterEntry ofItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return new SlotFilterEntry(FilterTargetType.ITEM, stack.copyWithCount(1), FluidStack.EMPTY);
    }

    public static SlotFilterEntry ofFluid(FluidStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return new SlotFilterEntry(FilterTargetType.FLUID, ItemStack.EMPTY, new FluidStack(stack.getFluidHolder(), 1));
    }

    public static SlotFilterEntry fromCarried(ItemStack carried, FilterTargetType expectedType) {
        if (carried.isEmpty()) {
            return null;
        }

        if (expectedType == FilterTargetType.FLUID) {
            return FluidUtil.getFluidContained(carried)
                .filter(fluidStack -> !fluidStack.isEmpty())
                .map(SlotFilterEntry::ofFluid)
                .orElse(null);
        }

        return ofItem(carried);
    }

    public FilterTargetType getType() {
        return this.type;
    }

    public boolean matchesItem(ItemStack stack) {
        if (this.type != FilterTargetType.ITEM || stack.isEmpty() || this.itemStack.isEmpty()) {
            return false;
        }
        // Components は見ず、アイテム種類だけを照合します。
        return this.itemStack.getItem() == stack.getItem();
    }

    public boolean matchesFluid(FluidStack stack) {
        if (this.type != FilterTargetType.FLUID || stack.isEmpty() || this.fluidStack.isEmpty()) {
            return false;
        }
        return this.fluidStack.getFluid() == stack.getFluid();
    }

    public ItemStack createDisplayStack() {
        if (this.type == FilterTargetType.ITEM) {
            return this.itemStack.copy();
        }

        if (this.fluidStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        Item bucketItem = this.fluidStack.getFluid().getBucket();
        if (bucketItem == null || bucketItem == Items.AIR) {
            return new ItemStack(Items.BUCKET);
        }
        return new ItemStack(bucketItem);
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        if (this.type == FilterTargetType.ITEM) {
            tag.putString(TYPE_TAG, TYPE_ITEM);
            tag.put(ITEM_TAG, this.itemStack.save(registries));
            return tag;
        }

        tag.putString(TYPE_TAG, TYPE_FLUID);
        ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(this.fluidStack.getFluid());
        tag.putString(FLUID_TAG, fluidId.toString());
        return tag;
    }

    public static SlotFilterEntry load(CompoundTag tag, HolderLookup.Provider registries) {
        String typeName = tag.getString(TYPE_TAG);
        if (TYPE_FLUID.equals(typeName)) {
            Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(tag.getString(FLUID_TAG)));
            if (fluid == null || fluid == Fluids.EMPTY) {
                return null;
            }
            return ofFluid(new FluidStack(fluid, 1));
        }

        if (!tag.contains(ITEM_TAG, Tag.TAG_COMPOUND)) {
            return null;
        }
        ItemStack stack = ItemStack.parseOptional(registries, tag.getCompound(ITEM_TAG));
        return ofItem(stack);
    }
}
