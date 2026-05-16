package com.yukke9265.cobblestone_xx_compressed.jei.category;

import java.util.List;

import org.lwjgl.system.windows.INPUT;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CobblestoneCrusherRecipeCategory implements IRecipeCategory<CobblestoneCrusherRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_crusher.png");

    private static final int BACKGROUND_U = 3;// 0, 0 から始まるテクスチャの位置
    private static final int BACKGROUND_V = 6;// 0, 0 から始まるテクスチャの位置
    private static final int BACKGROUND_WIDTH = 176 - BACKGROUND_U * 2;// テクスチャの幅
    private static final int BACKGROUND_HEIGHT = 78 - BACKGROUND_V * 2;// テクスチャの高さ
    private static final int SLOT_SIZE = 18;// スロットのサイズ
    private static final int INPUT_SLOT_X = 56 - BACKGROUND_U;
    private static final int INPUT_SLOT_Y = 31 - BACKGROUND_V;
    private static final int POWER_SLOT_X = 10 - BACKGROUND_U;
    private static final int POWER_SLOT_Y = 51 - BACKGROUND_V;
    private static final int OUTPUT_SLOT_X = INPUT_SLOT_X + SLOT_SIZE + 35;
    private static final int OUTPUT_SLOT_Y = INPUT_SLOT_Y;

    private static final int CPPt_LABEL_X = 8;
    private static final int CPPt_LABEL_Y = 2;
    private static final int TOTAL_CP_LABEL_X = 8;
    private static final int TOTAL_CP_LABEL_Y = 12;

    private final IDrawable background;
    private final IDrawable icon;

    public CobblestoneCrusherRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COBBLESTONE_CRUSHER.get()));
    }

    @Override
    public RecipeType<CobblestoneCrusherRecipe> getRecipeType() {
        return ModJeiPlugin.COBBLESTONE_CRUSHER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_crusher");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CobblestoneCrusherRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, INPUT_SLOT_Y)
            .addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.CATALYST, POWER_SLOT_X, POWER_SLOT_Y)
            .addItemStacks(List.of(
                new ItemStack(Items.COBBLESTONE),
                new ItemStack(ModBlocks.COMPRESSED_COBBLESTONE.get()),
                new ItemStack(ModBlocks.TierCompressedCobblestone.COPPER.getBlock().get())
            ));

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT_X, OUTPUT_SLOT_Y)
            .addItemStack(recipe.getResult().copy());
    }

    @Override
    public void draw(CobblestoneCrusherRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getCobblestonePowerPerTick() + " CP/t", CPPt_LABEL_X, CPPt_LABEL_Y, 0x000000, false);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getTotalCobblestonePower() + " total CP", TOTAL_CP_LABEL_X, TOTAL_CP_LABEL_Y, 0x000000, false);
    }
}