package com.yukke9265.cobblestone_xx_compressed.jei.category;

import java.util.ArrayList;
import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@SuppressWarnings("null")
public final class JeiCobblestonePowerItems {
    private JeiCobblestonePowerItems() {
    }

    public static List<ItemStack> getCatalystItems() {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(new ItemStack(Items.COBBLESTONE));
        stacks.add(new ItemStack(ModBlocks.COMPRESSED_COBBLESTONE.get()));

        for (ModBlocks.TierCompressedCobblestone tier : ModBlocks.TierCompressedCobblestone.values()) {
            stacks.add(new ItemStack(tier.getBlock().get()));
        }

        return List.copyOf(stacks);
    }

    // CP 燃料はレシピ画面には出すが、JEI の検索対象にはしない。
    // CATALYST だと圧縮丸石の使用レシピに全機械が並んでしまう。
    public static void addPowerSlot(IRecipeLayoutBuilder builder, int x, int y) {
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x, y)
            .addItemStacks(getCatalystItems());
    }
}
