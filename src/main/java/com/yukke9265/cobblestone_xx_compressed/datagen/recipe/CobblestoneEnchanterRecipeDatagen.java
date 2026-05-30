package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.util.CobblestoneEnchanterHelper;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public final class CobblestoneEnchanterRecipeDatagen {
    private CobblestoneEnchanterRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        // Enchanter は結果が入力内容で動的に決まるため、
        // JSON には受け付ける本の条件と JEI 用のプレビューだけを持たせます。
        MachineRecipeOutputHelper.saveCobblestoneEnchanterRecipe(
            output,
            "generic_enchanting",
            Ingredient.of(Items.ENCHANTED_BOOK),
            new ItemStack(Items.DIAMOND_PICKAXE),
            new ItemStack(Items.ENCHANTED_BOOK),
            new ItemStack(Items.DIAMOND_PICKAXE),
            CobblestoneEnchanterHelper.DEFAULT_PROCESSING_TICKS
        );
    }
}