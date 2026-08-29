package com.yukke9265.cobblestone_xx_compressed.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Powered Crafter 用のバニラ作業台レシピ照合ヘルパー。
 * CraftingInput.of は空枠を削るため、残アイテムは Positioned の left/top で 3x3 へ戻します。
 */
public final class CobblestonePoweredCrafterRecipeHelper {
    public static final long TOTAL_COBBLESTONE_POWER = 1600L;
    public static final long COBBLESTONE_POWER_PER_TICK = 4L;
    public static final int PROCESSING_TIME = (int) (TOTAL_COBBLESTONE_POWER / COBBLESTONE_POWER_PER_TICK);
    public static final int GRID_WIDTH = 3;
    public static final int GRID_HEIGHT = 3;
    public static final int GRID_SIZE = GRID_WIDTH * GRID_HEIGHT;

    private CobblestonePoweredCrafterRecipeHelper() {
    }

    public static CraftingInput.Positioned createPositionedCraftingInput(
        ItemStackHandler itemStackHandler,
        int firstGridSlotIndex
    ) {
        List<ItemStack> stacks = new ArrayList<>(GRID_SIZE);
        for (int offset = 0; offset < GRID_SIZE; offset++) {
            stacks.add(itemStackHandler.getStackInSlot(firstGridSlotIndex + offset));
        }

        return CraftingInput.ofPositioned(GRID_WIDTH, GRID_HEIGHT, stacks);
    }

    public static CraftingInput createCraftingInput(ItemStackHandler itemStackHandler, int firstGridSlotIndex) {
        return createPositionedCraftingInput(itemStackHandler, firstGridSlotIndex).input();
    }

    public static Optional<CraftingRecipe> findMatchingRecipe(Level level, CraftingInput craftingInput) {
        if (craftingInput.isEmpty()) {
            return Optional.empty();
        }

        Optional<RecipeHolder<CraftingRecipe>> recipeHolder = level.getRecipeManager().getRecipeFor(
            RecipeType.CRAFTING,
            craftingInput,
            level
        );
        return recipeHolder.map(RecipeHolder::value);
    }

    public static boolean canFitResult(ItemStack outputStack, ItemStack resultStack) {
        if (resultStack.isEmpty()) {
            return false;
        }

        if (outputStack.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
            return false;
        }

        return outputStack.getCount() + resultStack.getCount() <= outputStack.getMaxStackSize();
    }

    /**
     * getRemainingItems の並びを 3x3 グリッドの各マスへ展開します。
     * 空のマスは EMPTY のままです。
     */
    public static NonNullList<ItemStack> expandRemainingItemsToGrid(
        CraftingInput.Positioned positioned,
        NonNullList<ItemStack> remainingItems
    ) {
        NonNullList<ItemStack> expanded = NonNullList.withSize(GRID_SIZE, ItemStack.EMPTY);
        CraftingInput craftingInput = positioned.input();
        int width = craftingInput.width();
        int height = craftingInput.height();
        int left = positioned.left();
        int top = positioned.top();

        for (int remainingIndex = 0; remainingIndex < remainingItems.size(); remainingIndex++) {
            int localX = remainingIndex % width;
            int localY = remainingIndex / width;
            if (localY >= height) {
                break;
            }

            int gridX = left + localX;
            int gridY = top + localY;
            if (gridX < 0 || gridX >= GRID_WIDTH || gridY < 0 || gridY >= GRID_HEIGHT) {
                continue;
            }

            expanded.set(gridY * GRID_WIDTH + gridX, remainingItems.get(remainingIndex));
        }

        return expanded;
    }

    /**
     * 1 回クラフトしたあとに、残アイテム（空きバケツ等）を同じマスへ戻せるかを調べます。
     * remainingItems は 3x3 展開済みを渡します。
     */
    public static boolean canApplyRemainingItems(
        ItemStackHandler itemStackHandler,
        int firstGridSlotIndex,
        NonNullList<ItemStack> remainingItemsOnGrid
    ) {
        for (int offset = 0; offset < GRID_SIZE; offset++) {
            ItemStack currentStack = itemStackHandler.getStackInSlot(firstGridSlotIndex + offset).copy();
            if (!currentStack.isEmpty()) {
                currentStack.shrink(1);
            }

            ItemStack remainingStack = remainingItemsOnGrid.get(offset);
            if (remainingStack.isEmpty()) {
                continue;
            }

            if (currentStack.isEmpty()) {
                continue;
            }

            if (!ItemStack.isSameItemSameComponents(currentStack, remainingStack)) {
                return false;
            }

            if (currentStack.getCount() + remainingStack.getCount() > currentStack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    public static void consumeIngredientsAndApplyRemaining(
        ItemStackHandler itemStackHandler,
        int firstGridSlotIndex,
        NonNullList<ItemStack> remainingItemsOnGrid
    ) {
        for (int offset = 0; offset < GRID_SIZE; offset++) {
            int slotIndex = firstGridSlotIndex + offset;
            ItemStack currentStack = itemStackHandler.getStackInSlot(slotIndex);
            if (!currentStack.isEmpty()) {
                currentStack.shrink(1);
                if (currentStack.isEmpty()) {
                    itemStackHandler.setStackInSlot(slotIndex, ItemStack.EMPTY);
                }
            }

            ItemStack remainingStack = remainingItemsOnGrid.get(offset);
            if (remainingStack.isEmpty()) {
                continue;
            }

            ItemStack afterShrink = itemStackHandler.getStackInSlot(slotIndex);
            if (afterShrink.isEmpty()) {
                itemStackHandler.setStackInSlot(slotIndex, remainingStack.copy());
                continue;
            }

            afterShrink.grow(remainingStack.getCount());
        }
    }
}
