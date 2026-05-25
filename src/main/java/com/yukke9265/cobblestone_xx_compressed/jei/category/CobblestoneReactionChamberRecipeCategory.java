package com.yukke9265.cobblestone_xx_compressed.jei.category;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneReactionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.util.GuiPartRenderer;
import com.yukke9265.cobblestone_xx_compressed.util.MachineGuiLayouts;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
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
import net.neoforged.neoforge.fluids.FluidType;

public class CobblestoneReactionChamberRecipeCategory implements IRecipeCategory<CobblestoneReactionChamberRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_reaction_chamber.png");

    private static final int BACKGROUND_U = 3;
    private static final int BACKGROUND_V = 6;
    private static final int BACKGROUND_WIDTH = 176 - BACKGROUND_U * 2;
    private static final int BACKGROUND_HEIGHT = 78 - BACKGROUND_V * 2;
    private static final int FLUID_SLOT_X = MachineGuiLayouts.ReactionChamber.FLUID_SLOT_X - BACKGROUND_U;
    private static final int FLUID_SLOT_Y = MachineGuiLayouts.ReactionChamber.FLUID_SLOT_Y - BACKGROUND_V;
    private static final int INPUT_SLOT_1_X = MachineGuiLayouts.ReactionChamber.INPUT_SLOT_1_X - BACKGROUND_U;
    private static final int INPUT_SLOT_1_Y = MachineGuiLayouts.ReactionChamber.INPUT_SLOT_1_Y - BACKGROUND_V;
    private static final int INPUT_SLOT_2_X = MachineGuiLayouts.ReactionChamber.INPUT_SLOT_2_X - BACKGROUND_U;
    private static final int INPUT_SLOT_2_Y = MachineGuiLayouts.ReactionChamber.INPUT_SLOT_2_Y - BACKGROUND_V;
    private static final int OUTPUT_SLOT_X = MachineGuiLayouts.ReactionChamber.OUTPUT_SLOT_X - BACKGROUND_U;
    private static final int OUTPUT_SLOT_Y = MachineGuiLayouts.ReactionChamber.OUTPUT_SLOT_Y - BACKGROUND_V;
    private static final int POWER_SLOT_X = MachineGuiLayouts.ReactionChamber.POWER_SLOT_X - BACKGROUND_U;
    private static final int POWER_SLOT_Y = MachineGuiLayouts.ReactionChamber.POWER_SLOT_Y - BACKGROUND_V;
    private static final int PROGRESS_FRAME_X = MachineGuiLayouts.ReactionChamber.PROGRESS_BAR_X - BACKGROUND_U;
    private static final int PROGRESS_FRAME_Y = MachineGuiLayouts.ReactionChamber.PROGRESS_BAR_Y - BACKGROUND_V;
    private static final int CPPT_LABEL_X = 36;
    private static final int CPPT_LABEL_Y = 50;//スロットと被らないように、下部に移動
    private static final int TOTAL_CP_LABEL_X = 36;
    private static final int TOTAL_CP_LABEL_Y = CPPT_LABEL_Y + 10;

    private final IDrawable background;
    private final IDrawable icon;

    public CobblestoneReactionChamberRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COBBLESTONE_REACTION_CHAMBER.get()));
    }

    @Override
    public RecipeType<CobblestoneReactionChamberRecipe> getRecipeType() {
        return ModJeiPlugin.COBBLESTONE_REACTION_CHAMBER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_reaction_chamber");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CobblestoneReactionChamberRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, POWER_SLOT_X, POWER_SLOT_Y)
            .addItemStacks(JeiCobblestonePowerItems.getCatalystItems());

        builder.addSlot(RecipeIngredientRole.INPUT, FLUID_SLOT_X, FLUID_SLOT_Y)
            .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(recipe.getFluidInput()))
            .setFluidRenderer(Math.max(recipe.getFluidInput().getAmount(), FluidType.BUCKET_VOLUME), false, 16, 16);

        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_1_X, INPUT_SLOT_1_Y)
            .addIngredients(recipe.getFirstIngredient());

        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_2_X, INPUT_SLOT_2_Y)
            .addIngredients(recipe.getSecondIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT_X, OUTPUT_SLOT_Y)
            .addItemStack(recipe.getResult().copy());
    }

    @Override
    public void draw(CobblestoneReactionChamberRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.ReactionChamber.FLUID_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ReactionChamber.FLUID_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, INPUT_SLOT_1_X, INPUT_SLOT_1_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, INPUT_SLOT_2_X, INPUT_SLOT_2_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, OUTPUT_SLOT_X, OUTPUT_SLOT_Y);
        GuiPartRenderer.renderCobblestoneSlot(guiGraphics, POWER_SLOT_X, POWER_SLOT_Y);
        GuiPartRenderer.renderProgressFrame(guiGraphics, PROGRESS_FRAME_X, PROGRESS_FRAME_Y);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getCobblestonePowerPerTick() + " CP/t", CPPT_LABEL_X, CPPT_LABEL_Y, 0x404040, false);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getTotalCobblestonePower() + " total CP", TOTAL_CP_LABEL_X, TOTAL_CP_LABEL_Y, 0x404040, false);
    }
}