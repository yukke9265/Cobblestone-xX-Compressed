package com.yukke9265.cobblestone_xx_compressed.blockentity;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 機械の面ごとに公開する item handler を、小さな共通部品として生成します。
 *
 * この helper は、実際の inventory を持つ {@link ItemStackHandler} へ処理を委譲します。
 * そのため、各スロットで受け入れられる item の判定は、従来どおり機械側の
 * {@code ItemStackHandler#isItemValid} が担当します。
 *
 * Phase 1 では既存の automation の挙動を変えないことを優先します。
 * そのため、公開するスロット、挿入可否、抽出可否は呼び出し側で明示します。
 */
@SuppressWarnings("null")
public final class AutomationItemHandlerHelper {
    private AutomationItemHandlerHelper() {
    }

    /**
     * 指定した inventory スロットだけを、挿入専用の 1 スロット handler として公開します。
     */
    public static IItemHandler createInsertOnlyHandler(ItemStackHandler itemStackHandler, int itemStackSlotIndex) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public @Nonnull ItemStack getStackInSlot(int slot) {
                if (slot != 0) {
                    return ItemStack.EMPTY;
                }

                return itemStackHandler.getStackInSlot(itemStackSlotIndex);
            }

            @Override
            public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (slot != 0) {
                    return stack;
                }

                return itemStackHandler.insertItem(itemStackSlotIndex, stack, simulate);
            }

            @Override
            public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot != 0) {
                    return 0;
                }

                return itemStackHandler.getSlotLimit(itemStackSlotIndex);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return slot == 0 && itemStackHandler.isItemValid(itemStackSlotIndex, stack);
            }
        };
    }

    /**
     * 指定した inventory スロットだけを、抽出専用の 1 スロット handler として公開します。
     */
    public static IItemHandler createExtractOnlyHandler(ItemStackHandler itemStackHandler, int itemStackSlotIndex) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public @Nonnull ItemStack getStackInSlot(int slot) {
                if (slot != 0) {
                    return ItemStack.EMPTY;
                }

                return itemStackHandler.getStackInSlot(itemStackSlotIndex);
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

                return itemStackHandler.extractItem(itemStackSlotIndex, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot != 0) {
                    return 0;
                }

                return itemStackHandler.getSlotLimit(itemStackSlotIndex);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }
        };
    }

    /**
     * 指定した複数の inventory スロットを、抽出専用 handler として指定順に公開します。
     * handler 側の slot 0 は配列の先頭、slot 1 は次の inventory スロットに対応します。
     */
    public static IItemHandler createMultipleExtractOnlyHandler(ItemStackHandler itemStackHandler, int... itemStackSlotIndices) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return itemStackSlotIndices.length;
            }

            @Override
            public @Nonnull ItemStack getStackInSlot(int slot) {
                int itemStackSlotIndex = getItemStackSlotIndex(slot);
                if (itemStackSlotIndex < 0) {
                    return ItemStack.EMPTY;
                }

                return itemStackHandler.getStackInSlot(itemStackSlotIndex);
            }

            @Override
            public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return stack;
            }

            @Override
            public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
                int itemStackSlotIndex = getItemStackSlotIndex(slot);
                if (itemStackSlotIndex < 0) {
                    return ItemStack.EMPTY;
                }

                return itemStackHandler.extractItem(itemStackSlotIndex, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                int itemStackSlotIndex = getItemStackSlotIndex(slot);
                if (itemStackSlotIndex < 0) {
                    return 0;
                }

                return itemStackHandler.getSlotLimit(itemStackSlotIndex);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }

            private int getItemStackSlotIndex(int slot) {
                if (slot < 0 || slot >= itemStackSlotIndices.length) {
                    return -1;
                }

                return itemStackSlotIndices[slot];
            }
        };
    }

    /**
     * 指定した複数の inventory スロットへ、配列の順番で投入する handler を作成します。
     *
     * 外部からは各入力枠を確認でき、どの外部 slot へ投入しても、最初の入力枠から
     * 順番に空き容量へ入れます。これは Mixer の INPUT mode の既存仕様です。
     */
    public static IItemHandler createSequentialInsertOnlyHandler(ItemStackHandler itemStackHandler, int... itemStackSlotIndices) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return itemStackSlotIndices.length;
            }

            @Override
            public @Nonnull ItemStack getStackInSlot(int slot) {
                int itemStackSlotIndex = getItemStackSlotIndex(slot);
                if (itemStackSlotIndex < 0) {
                    return ItemStack.EMPTY;
                }

                return itemStackHandler.getStackInSlot(itemStackSlotIndex);
            }

            @Override
            public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (getItemStackSlotIndex(slot) < 0) {
                    return stack;
                }

                ItemStack remainingStack = stack;
                for (int itemStackSlotIndex : itemStackSlotIndices) {
                    if (remainingStack.isEmpty()) {
                        break;
                    }

                    remainingStack = itemStackHandler.insertItem(itemStackSlotIndex, remainingStack, simulate);
                }

                return remainingStack;
            }

            @Override
            public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                int itemStackSlotIndex = getItemStackSlotIndex(slot);
                if (itemStackSlotIndex < 0) {
                    return 0;
                }

                return itemStackHandler.getSlotLimit(itemStackSlotIndex);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return getItemStackSlotIndex(slot) >= 0;
            }

            private int getItemStackSlotIndex(int slot) {
                if (slot < 0 || slot >= itemStackSlotIndices.length) {
                    return -1;
                }

                return itemStackSlotIndices[slot];
            }
        };
    }

    /**
     * inventory 全体を見せつつ、挿入先と抽出元をそれぞれ 1 スロットに制限した handler を作成します。
     *
     * {@code getStackInSlot}、{@code getSlotLimit}、{@code isItemValid} は全スロットを元の inventory へ
     * 委譲します。ただし、実際に item を移動できるのは指定した挿入先または抽出元だけです。
     */
    public static IItemHandler createRestrictedAccessHandler(
        ItemStackHandler itemStackHandler,
        int insertSlotIndex,
        int extractSlotIndex
    ) {
        return createRestrictedAccessHandler(itemStackHandler, new int[] {insertSlotIndex}, new int[] {extractSlotIndex});
    }

    /**
     * inventory 全体を見せつつ、指定した複数の挿入先と抽出元だけで item の移動を許可します。
     */
    public static IItemHandler createRestrictedAccessHandler(
        ItemStackHandler itemStackHandler,
        int[] insertSlotIndices,
        int[] extractSlotIndices
    ) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return itemStackHandler.getSlots();
            }

            @Override
            public @Nonnull ItemStack getStackInSlot(int slot) {
                if (!isValidSlot(slot)) {
                    return ItemStack.EMPTY;
                }

                return itemStackHandler.getStackInSlot(slot);
            }

            @Override
            public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!containsSlot(insertSlotIndices, slot)) {
                    return stack;
                }

                return itemStackHandler.insertItem(slot, stack, simulate);
            }

            @Override
            public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!containsSlot(extractSlotIndices, slot)) {
                    return ItemStack.EMPTY;
                }

                return itemStackHandler.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                if (!isValidSlot(slot)) {
                    return 0;
                }

                return itemStackHandler.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return isValidSlot(slot) && itemStackHandler.isItemValid(slot, stack);
            }

            private boolean isValidSlot(int slot) {
                return slot >= 0 && slot < itemStackHandler.getSlots();
            }

            private boolean containsSlot(int[] slotIndices, int slot) {
                for (int slotIndex : slotIndices) {
                    if (slotIndex == slot) {
                        return true;
                    }
                }

                return false;
            }
        };
    }
}