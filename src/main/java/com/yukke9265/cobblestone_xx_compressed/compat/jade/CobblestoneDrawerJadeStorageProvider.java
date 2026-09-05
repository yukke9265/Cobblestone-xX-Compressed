package com.yukke9265.cobblestone_xx_compressed.compat.jade;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneDrawerBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;

/**
 * ドロワー内部の storedItem / storedAmount を Jade のアイテム欄へ載せ替えます。
 * 既定の IItemHandler 表示（仮想スロット分だけ）を上書きするのが目的です。
 */
public enum CobblestoneDrawerJadeStorageProvider implements
    IServerExtensionProvider<ItemStack>,
    IClientExtensionProvider<ItemStack, ItemView> {
    INSTANCE;

    private static final String EXTRA_STORED_AMOUNT = "StoredAmount";
    private static final String EXTRA_MAX_AMOUNT = "MaxAmount";

    @Override
    public @Nullable List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        Object target = accessor.getTarget();
        if (!(target instanceof CobblestoneDrawerBlockEntity drawer)) {
            return null;
        }

        ItemStack storedItem = drawer.getStoredItem();
        long storedAmount = drawer.getStoredAmount();
        if (storedItem.isEmpty() || storedAmount <= 0L) {
            return List.of();
        }

        // count は見た目用のダミーです。実数は extraData に long で載せます。
        ItemStack displayStack = storedItem.copyWithCount(1);
        ViewGroup<ItemStack> group = new ViewGroup<>(List.of(displayStack));
        CompoundTag extraData = group.getExtraData();
        extraData.putLong(EXTRA_STORED_AMOUNT, storedAmount);
        extraData.putLong(EXTRA_MAX_AMOUNT, drawer.getMaxStoredAmount());
        if (drawer.getMaxStoredAmount() > 0L) {
            group.setProgress((float) Math.min(1.0D, (double) storedAmount / (double) drawer.getMaxStoredAmount()));
        }

        return List.of(group);
    }

    @Override
    public List<ClientViewGroup<ItemView>> getClientGroups(
        Accessor<?> accessor,
        List<ViewGroup<ItemStack>> groups
    ) {
        return ClientViewGroup.map(
            groups,
            ItemView::new,
            (serverGroup, clientGroup) -> {
                CompoundTag extraData = serverGroup.getExtraData();
                long storedAmount = extraData.getLong(EXTRA_STORED_AMOUNT);
                long maxAmount = extraData.getLong(EXTRA_MAX_AMOUNT);
                if (clientGroup.views.isEmpty()) {
                    return;
                }

                ItemView itemView = clientGroup.views.getFirst();
                itemView.amountText(formatAmount(storedAmount));
                clientGroup.title = Component.translatable(
                    "jade.cobblestonexxcompressed.cobblestone_drawer.capacity",
                    formatAmount(storedAmount),
                    formatAmount(maxAmount)
                );
            }
        );
    }

    @Override
    public ResourceLocation getUid() {
        return CobblestoneDrawerJadePlugin.DRAWER_STORAGE;
    }

    private static String formatAmount(long amount) {
        return Long.toString(amount);
    }
}
