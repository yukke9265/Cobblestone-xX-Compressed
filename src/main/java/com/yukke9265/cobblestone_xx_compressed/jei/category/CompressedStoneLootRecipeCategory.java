package com.yukke9265.cobblestone_xx_compressed.jei.category;

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

public class CompressedStoneLootRecipeCategory implements IRecipeCategory<CompressedStoneLootJeiRecipe> {
    private static final int BACKGROUND_WIDTH = 162;
    private static final int BACKGROUND_HEIGHT = 76;
    private static final int INPUT_SLOT_X = 6;
    private static final int INPUT_SLOT_Y = 29;
    private static final int SILK_TOUCH_SLOT_X = 58;
    private static final int SILK_TOUCH_SLOT_Y = 11;
    private static final int NORMAL_DROP_SLOT_X = 58;
    private static final int NORMAL_DROP_SLOT_Y = 47;
    private static final int GEM_SLOT_X = 116;
    private static final int GEM_SLOT_Y = 11;
    private static final int RESOURCE_SLOT_X = 116;
    private static final int RESOURCE_SLOT_Y = 47;

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
    public void setRecipe(IRecipeLayoutBuilder builder, CompressedStoneLootJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, INPUT_SLOT_Y)
            .addItemStack(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, SILK_TOUCH_SLOT_X, SILK_TOUCH_SLOT_Y)
            .addItemStack(recipe.getSilkTouchDrop());

        builder.addSlot(RecipeIngredientRole.OUTPUT, NORMAL_DROP_SLOT_X, NORMAL_DROP_SLOT_Y)
            .addItemStack(recipe.getNormalDrop());

        builder.addSlot(RecipeIngredientRole.OUTPUT, GEM_SLOT_X, GEM_SLOT_Y)
            .addItemStack(recipe.getGemDrop());

        builder.addSlot(RecipeIngredientRole.OUTPUT, RESOURCE_SLOT_X, RESOURCE_SLOT_Y)
            .addItemStack(recipe.getResourceDrop());
    }

    @Override
    public void draw(CompressedStoneLootJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.drawString(minecraft.font, Component.translatable("jei.cobblestonexxcompressed.silk_touch"), 44, 2, 0x404040, false);
        guiGraphics.drawString(minecraft.font, Component.translatable("jei.cobblestonexxcompressed.no_silk_touch"), 34, 38, 0x404040, false);
        guiGraphics.drawString(
            minecraft.font,
            Component.translatable("jei.cobblestonexxcompressed.chance", recipe.getGemChanceText()),
            85,
            2,
            0x404040,
            false
        );
        guiGraphics.drawString(
            minecraft.font,
            Component.translatable("jei.cobblestonexxcompressed.chance", recipe.getResourceChanceText()),
            85,
            38,
            0x404040,
            false
        );

        if (recipe.hasCountRange()) {
            guiGraphics.drawString(
                minecraft.font,
                Component.translatable(
                    "jei.cobblestonexxcompressed.count_range_fortune",
                    recipe.getMinCountText(),
                    recipe.getMaxCountText()
                ),
                85,
                58,
                0x404040,
                false
            );
        }
    }
}