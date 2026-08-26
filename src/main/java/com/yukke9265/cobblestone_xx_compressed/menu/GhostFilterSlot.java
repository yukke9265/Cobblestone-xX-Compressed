package com.yukke9265.cobblestone_xx_compressed.menu;

import java.util.function.BooleanSupplier;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/*
 * 方針:
 * フィルタ用 ghost スロットは見た目だけアイテムを置き、手持ちを消費しません。
 * パネル閉鎖時は isActive=false で描画・クリックの両方を止めます。
 */
public class GhostFilterSlot extends SlotItemHandler {
    private final BooleanSupplier activeSupplier;

    public GhostFilterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, BooleanSupplier activeSupplier) {
        super(itemHandler, index, xPosition, yPosition);
        this.activeSupplier = activeSupplier;
    }

    @Override
    public boolean isActive() {
        return this.activeSupplier.getAsBoolean();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
