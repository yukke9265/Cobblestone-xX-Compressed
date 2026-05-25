package com.yukke9265.cobblestone_xx_compressed.jei.category;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.jei.CompressedStoneLootJeiRecipe;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("null")
public class CompressedStoneLootRecipeCategory implements IRecipeCategory<CompressedStoneLootJeiRecipe> {
    private static final int BACKGROUND_WIDTH = 162;
    private static final int BACKGROUND_HEIGHT = 124;
    private static final int INPUT_SLOT_X = 6;
    private static final int INPUT_SLOT_Y = 42;
    private static final int LIST_SLOT_X = 74;
    private static final int LIST_TEXT_X = 96;
    private static final int[] LIST_SLOT_Y = { 8, 30, 52, 74, 96 };
    private static final int[] LIST_TEXT_Y = { 13, 35, 57, 79, 101 };

    private final IDrawable background;
    private final IDrawable icon;

    public CompressedStoneLootRecipeCategory(IGuiHelper guiHelper) {
        // loot 用の専用 GUI テクスチャはまだ無いので、
        // まずは JEI の blank drawable 上に必要な情報だけを明示します。
        this.background = guiHelper.createBlankDrawable(BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COMPRESSED_STONE.get()));
    }

    @Override
    public RecipeType<CompressedStoneLootJeiRecipe> getRecipeType() {
        return ModJeiPlugin.COMPRESSED_STONE_LOOT_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.cobblestonexxcompressed.compressed_stone_loot");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull CompressedStoneLootJeiRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, INPUT_SLOT_Y)
            .addItemStack(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, LIST_SLOT_X, LIST_SLOT_Y[0])
            .addItemStack(recipe.getNormalDrop());

        for (int index = 0; index < recipe.getBonusDrops().size(); index++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, LIST_SLOT_X, LIST_SLOT_Y[index + 1])
                .addItemStack(recipe.getBonusDrops().get(index).getDrop());
        }
    }

    @Override
    public void draw(@Nonnull CompressedStoneLootJeiRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.drawString(
            minecraft.font,
            Component.translatable("jei.cobblestonexxcompressed.chance", "100%"),
            LIST_TEXT_X,
            LIST_TEXT_Y[0],
            0x404040,
            false
        );

        for (int index = 0; index < recipe.getBonusDrops().size(); index++) {
            CompressedStoneLootJeiRecipe.BonusDrop bonusDrop = recipe.getBonusDrops().get(index);

            guiGraphics.drawString(
                minecraft.font,
                Component.translatable("jei.cobblestonexxcompressed.chance", bonusDrop.getChanceText()),
                LIST_TEXT_X,
                LIST_TEXT_Y[index + 1],
                0x404040,
                false
            );
        }
    }
}