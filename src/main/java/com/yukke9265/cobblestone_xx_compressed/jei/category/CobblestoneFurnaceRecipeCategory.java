package com.yukke9265.cobblestone_xx_compressed.jei.category;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.util.GuiPartRenderer;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CobblestoneFurnaceRecipeCategory implements IRecipeCategory<CobblestoneFurnaceRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_furnace.png");

    private static final int BACKGROUND_U = 26;
    private static final int BACKGROUND_V = 16;
    private static final int BACKGROUND_WIDTH = 124;
    private static final int BACKGROUND_HEIGHT = 54;
    private static final int INPUT_SLOT_X = 30;
    private static final int INPUT_SLOT_Y = 19;
    private static final int OUTPUT_SLOT_X = 83;
    private static final int OUTPUT_SLOT_Y = INPUT_SLOT_Y;
    private static final int PROGRESS_FRAME_X = 54;
    private static final int PROGRESS_FRAME_Y = 19;

    private final IDrawable background;
    private final IDrawable icon;

    public CobblestoneFurnaceRecipeCategory(IGuiHelper guiHelper) {
        // 既存 GUI の中央部分を切り出すと、実機画面と JEI の見た目を揃えやすくなります。
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COBBLESTONE_FURNACE.get()));
    }

    @Override
    public RecipeType<CobblestoneFurnaceRecipe> getRecipeType() {
        return ModJeiPlugin.COBBLESTONE_FURNACE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_furnace");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CobblestoneFurnaceRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, INPUT_SLOT_Y)
            .addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT_X, OUTPUT_SLOT_Y)
            .addItemStack(recipe.getResult().copy());
    }

    @Override
    public void draw(CobblestoneFurnaceRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GuiPartRenderer.renderNormalSlot(guiGraphics, INPUT_SLOT_X, INPUT_SLOT_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, OUTPUT_SLOT_X, OUTPUT_SLOT_Y);
        GuiPartRenderer.renderProgressFrame(guiGraphics, PROGRESS_FRAME_X, PROGRESS_FRAME_Y);
    }
}