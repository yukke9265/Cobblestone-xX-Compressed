package com.yukke9265.cobblestone_xx_compressed.jei.category;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneEnchanterRecipe;
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

@SuppressWarnings("null")
public class CobblestoneEnchanterRecipeCategory implements IRecipeCategory<CobblestoneEnchanterRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_mixer.png");

    private static final int BACKGROUND_U = 3;
    private static final int BACKGROUND_V = 6;
    private static final int BACKGROUND_WIDTH = 170;
    private static final int BACKGROUND_HEIGHT = 66;
    private static final int INFO_LABEL_X = 8;
    private static final int INFO_LABEL_Y = 2;
    private static final int FORMULA_LABEL_X = 8;
    private static final int FORMULA_LABEL_Y = 12;

    private final IDrawable background;
    private final IDrawable icon;

    public CobblestoneEnchanterRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COBBLESTONE_ENCHANTER.get()));
    }

    @Override
    public RecipeType<CobblestoneEnchanterRecipe> getRecipeType() {
        return ModJeiPlugin.COBBLESTONE_ENCHANTER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_enchanter");
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
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull CobblestoneEnchanterRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, MachineGuiLayouts.Enchanter.POWER_SLOT_X - BACKGROUND_U, MachineGuiLayouts.Enchanter.POWER_SLOT_Y - BACKGROUND_V)
            .addItemStacks(JeiCobblestonePowerItems.getCatalystItems());

        builder.addSlot(RecipeIngredientRole.INPUT, MachineGuiLayouts.Enchanter.TOOL_SLOT_X - BACKGROUND_U, MachineGuiLayouts.Enchanter.TOOL_SLOT_Y - BACKGROUND_V)
            .addItemStack(recipe.getJeiPreviewTool());

        builder.addSlot(RecipeIngredientRole.INPUT, MachineGuiLayouts.Enchanter.BOOK_SLOT_X - BACKGROUND_U, MachineGuiLayouts.Enchanter.BOOK_SLOT_Y - BACKGROUND_V)
            .addItemStack(recipe.getJeiPreviewBook());

        builder.addSlot(RecipeIngredientRole.OUTPUT, MachineGuiLayouts.Enchanter.OUTPUT_SLOT_X - BACKGROUND_U, MachineGuiLayouts.Enchanter.OUTPUT_SLOT_Y - BACKGROUND_V)
            .addItemStack(recipe.getJeiPreviewResult());
    }

    @Override
    public void draw(@Nonnull CobblestoneEnchanterRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GuiPartRenderer.renderCobblestoneSlot(guiGraphics, MachineGuiLayouts.Enchanter.POWER_SLOT_X - BACKGROUND_U, MachineGuiLayouts.Enchanter.POWER_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.Enchanter.TOOL_SLOT_X - BACKGROUND_U, MachineGuiLayouts.Enchanter.TOOL_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.Enchanter.BOOK_SLOT_X - BACKGROUND_U, MachineGuiLayouts.Enchanter.BOOK_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.Enchanter.OUTPUT_SLOT_X - BACKGROUND_U, MachineGuiLayouts.Enchanter.OUTPUT_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderProgressFrame(guiGraphics, MachineGuiLayouts.Enchanter.PROGRESS_BAR_X - BACKGROUND_U, MachineGuiLayouts.Enchanter.PROGRESS_BAR_Y - BACKGROUND_V);
        guiGraphics.drawString(Minecraft.getInstance().font, "Result follows tool + book", INFO_LABEL_X, INFO_LABEL_Y, 0x404040, false);
        guiGraphics.drawString(Minecraft.getInstance().font, "CP/t: 1, 8, 8^2...", FORMULA_LABEL_X, FORMULA_LABEL_Y, 0x404040, false);
    }
}