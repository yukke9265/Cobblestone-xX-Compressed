package com.yukke9265.cobblestone_xx_compressed.jei.category;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCentrifugeRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.util.GuiPartRenderer;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

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

public class CobblestoneCentrifugeRecipeCategory implements IRecipeCategory<CobblestoneCentrifugeRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_centrifuge.png");

    private static final int BACKGROUND_U = 3;
    private static final int BACKGROUND_V = 6;
    private static final int BACKGROUND_WIDTH = 176 - BACKGROUND_U * 2;
    private static final int BACKGROUND_HEIGHT = 78 - BACKGROUND_V * 2;
    private static final int SLOT_SIZE = 18;// スロットのサイズ
    private static final int INPUT_SLOT_X = 56 - BACKGROUND_U;
    private static final int INPUT_SLOT_Y = 31 - BACKGROUND_V;
    private static final int POWER_SLOT_X = 10 - BACKGROUND_U;
    private static final int POWER_SLOT_Y = 51 - BACKGROUND_V;
    private static final int OUTPUT_SLOT_1_X = INPUT_SLOT_X + SLOT_SIZE + 30;
    private static final int OUTPUT_SLOT_1_Y = INPUT_SLOT_Y;

    private static final int OUTPUT_SLOT_2_X = OUTPUT_SLOT_1_X + SLOT_SIZE;
    private static final int OUTPUT_SLOT_2_Y = INPUT_SLOT_Y;
    private static final int PROGRESS_FRAME_X = MachineGuiLayouts.Centrifuge.PROGRESS_BAR_X - BACKGROUND_U;
    private static final int PROGRESS_FRAME_Y = MachineGuiLayouts.Centrifuge.PROGRESS_BAR_Y - BACKGROUND_V;
    private static final int CPPt_LABEL_X = 8;
    private static final int CPPt_LABEL_Y = 2;
    private static final int TOTAL_CP_LABEL_X = 8;
    private static final int TOTAL_CP_LABEL_Y = 12;
    private static final int OUTPUT_CHANCE_Y_OFFSET = 20;

    private final IDrawable background;
    private final IDrawable icon;

    public CobblestoneCentrifugeRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COBBLESTONE_CENTRIFUGE.get()));
    }

    @Override
    public RecipeType<CobblestoneCentrifugeRecipe> getRecipeType() {
        return ModJeiPlugin.COBBLESTONE_CENTRIFUGE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_centrifuge");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CobblestoneCentrifugeRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, POWER_SLOT_X, POWER_SLOT_Y)
            .addItemStacks(JeiCobblestonePowerItems.getCatalystItems());

        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, INPUT_SLOT_Y)
            .addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT_1_X, OUTPUT_SLOT_1_Y)
            .addItemStack(recipe.getFirstResult().copy());

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT_2_X, OUTPUT_SLOT_2_Y)
            .addItemStack(recipe.getSecondResult().copy());
    }

    @Override
    public void draw(CobblestoneCentrifugeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GuiPartRenderer.renderCobblestoneSlot(guiGraphics, POWER_SLOT_X, POWER_SLOT_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, INPUT_SLOT_X, INPUT_SLOT_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, OUTPUT_SLOT_1_X, OUTPUT_SLOT_1_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, OUTPUT_SLOT_2_X, OUTPUT_SLOT_2_Y);
        GuiPartRenderer.renderProgressFrame(guiGraphics, PROGRESS_FRAME_X, PROGRESS_FRAME_Y);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getCobblestonePowerPerTick() + " CP/t", CPPt_LABEL_X, CPPt_LABEL_Y, 0x404040, false);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getTotalCobblestonePower() + " total CP", TOTAL_CP_LABEL_X, TOTAL_CP_LABEL_Y, 0x404040, false);
        guiGraphics.drawString(Minecraft.getInstance().font, toPercentLabel(recipe.getFirstResultChance()), OUTPUT_SLOT_1_X - 4, OUTPUT_SLOT_1_Y + OUTPUT_CHANCE_Y_OFFSET, 0x404040, false);
        guiGraphics.drawString(Minecraft.getInstance().font, toPercentLabel(recipe.getSecondResultChance()), OUTPUT_SLOT_2_X + 4, OUTPUT_SLOT_2_Y + OUTPUT_CHANCE_Y_OFFSET, 0x404040, false);
    }

    private static String toPercentLabel(float chance) {
        return Math.round(chance * 100.0F) + "%";
    }
}