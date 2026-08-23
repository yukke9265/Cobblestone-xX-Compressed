package com.yukke9265.cobblestone_xx_compressed.jei.category;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.jei.WaterGeneratorConversionJeiRecipe;

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
import net.minecraft.world.item.Items;

@SuppressWarnings("null")
public class WaterGeneratorConversionRecipeCategory implements IRecipeCategory<WaterGeneratorConversionJeiRecipe> {
    private static final int BACKGROUND_WIDTH = 120;
    private static final int BACKGROUND_HEIGHT = 54;
    private static final int CP_SLOT_X = 6;
    private static final int CP_SLOT_Y = 18;
    private static final int WATER_SLOT_X = 72;
    private static final int WATER_SLOT_Y = 18;
    private static final int RATE_TEXT_X = 36;
    private static final int RATE_TEXT_Y = 44;

    private final IDrawable background;
    private final IDrawable icon;

    public WaterGeneratorConversionRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(Items.WATER_BUCKET.getDefaultInstance());
    }

    @Override
    public RecipeType<WaterGeneratorConversionJeiRecipe> getRecipeType() {
        return ModJeiPlugin.WATER_GENERATOR_CONVERSION_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.cobblestonexxcompressed.water_generator_conversion");
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
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull WaterGeneratorConversionJeiRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, CP_SLOT_X, CP_SLOT_Y)
            .addItemStack(recipe.getCpExample());

        builder.addSlot(RecipeIngredientRole.OUTPUT, WATER_SLOT_X, WATER_SLOT_Y)
            .addFluidStack(recipe.getWaterOutput().getFluid(), recipe.getWaterOutput().getAmount());
    }

    @Override
    public void draw(
        @Nonnull WaterGeneratorConversionJeiRecipe recipe,
        @Nonnull IRecipeSlotsView recipeSlotsView,
        @Nonnull GuiGraphics guiGraphics,
        double mouseX,
        double mouseY
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.drawString(
            minecraft.font,
            Component.translatable("jei.cobblestonexxcompressed.water_generator_conversion.rate"),
            RATE_TEXT_X,
            RATE_TEXT_Y,
            0x404040,
            false
        );
    }
}
