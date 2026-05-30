package com.yukke9265.cobblestone_xx_compressed.jei.category;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.jei.StoneBreakSimulatorJeiRecipe;
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
public class StoneBreakSimulatorRecipeCategory implements IRecipeCategory<StoneBreakSimulatorJeiRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_assembly_machine.png");

    private static final int BACKGROUND_U = 3;
    private static final int BACKGROUND_V = 6;
    private static final int BACKGROUND_WIDTH = 176 - BACKGROUND_U * 2;
    private static final int BACKGROUND_HEIGHT = 78 - BACKGROUND_V * 2;
    private static final int CPPT_LABEL_X = 36;
    private static final int CPPT_LABEL_Y = 50;
    private static final int TOTAL_CP_LABEL_X = 36;
    private static final int TOTAL_CP_LABEL_Y = 60;

    private final IDrawable background;
    private final IDrawable icon;

    public StoneBreakSimulatorRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.STONE_BREAK_SIMULATOR.get()));
    }

    @Override
    public RecipeType<StoneBreakSimulatorJeiRecipe> getRecipeType() {
        return ModJeiPlugin.STONE_BREAK_SIMULATOR_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.cobblestonexxcompressed.stone_break_simulator");
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
    public void setRecipe(IRecipeLayoutBuilder builder, StoneBreakSimulatorJeiRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, MachineGuiLayouts.StoneBreakSimulator.POWER_SLOT_X - BACKGROUND_U, MachineGuiLayouts.StoneBreakSimulator.POWER_SLOT_Y - BACKGROUND_V)
            .addItemStacks(JeiCobblestonePowerItems.getCatalystItems());

        builder.addSlot(RecipeIngredientRole.INPUT, MachineGuiLayouts.StoneBreakSimulator.INPUT_SLOT_1_X - BACKGROUND_U, MachineGuiLayouts.StoneBreakSimulator.INPUT_SLOT_1_Y - BACKGROUND_V)
            .addItemStack(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.INPUT, MachineGuiLayouts.StoneBreakSimulator.INPUT_SLOT_2_X - BACKGROUND_U, MachineGuiLayouts.StoneBreakSimulator.INPUT_SLOT_2_Y - BACKGROUND_V)
            .addIngredients(recipe.getPickaxeIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, MachineGuiLayouts.StoneBreakSimulator.OUTPUT_SLOT_X[0] - BACKGROUND_U, MachineGuiLayouts.StoneBreakSimulator.OUTPUT_SLOT_Y[0] - BACKGROUND_V)
            .addItemStack(recipe.getMainOutput().copy());

        for (int index = 0; index < recipe.getSubOutputs().size() && index + 1 < MachineGuiLayouts.StoneBreakSimulator.OUTPUT_SLOT_X.length; index++) {
            StoneBreakSimulatorJeiRecipe.BonusDrop bonusDrop = recipe.getSubOutputs().get(index);
            builder.addSlot(RecipeIngredientRole.OUTPUT, MachineGuiLayouts.StoneBreakSimulator.OUTPUT_SLOT_X[index + 1] - BACKGROUND_U, MachineGuiLayouts.StoneBreakSimulator.OUTPUT_SLOT_Y[index + 1] - BACKGROUND_V)
                .addItemStack(bonusDrop.stack().copy())
                .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.translatable("jei.cobblestonexxcompressed.chance", bonusDrop.chanceText())));
        }
    }

    @Override
    public void draw(StoneBreakSimulatorJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GuiPartRenderer.renderCobblestoneSlot(guiGraphics, MachineGuiLayouts.StoneBreakSimulator.POWER_SLOT_X - BACKGROUND_U, MachineGuiLayouts.StoneBreakSimulator.POWER_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.StoneBreakSimulator.INPUT_SLOT_1_X - BACKGROUND_U, MachineGuiLayouts.StoneBreakSimulator.INPUT_SLOT_1_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.StoneBreakSimulator.INPUT_SLOT_2_X - BACKGROUND_U, MachineGuiLayouts.StoneBreakSimulator.INPUT_SLOT_2_Y - BACKGROUND_V);
        GuiPartRenderer.renderProgressFrame(guiGraphics, MachineGuiLayouts.StoneBreakSimulator.PROGRESS_BAR_X - BACKGROUND_U, MachineGuiLayouts.StoneBreakSimulator.PROGRESS_BAR_Y - BACKGROUND_V);

        for (int index = 0; index < MachineGuiLayouts.StoneBreakSimulator.OUTPUT_SLOT_X.length; index++) {
            GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.StoneBreakSimulator.OUTPUT_SLOT_X[index] - BACKGROUND_U, MachineGuiLayouts.StoneBreakSimulator.OUTPUT_SLOT_Y[index] - BACKGROUND_V);
        }

        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getCobblestonePowerPerTick() + " CP/t", CPPT_LABEL_X, CPPT_LABEL_Y, 0x000000, false);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getTotalCobblestonePower() + " total CP", TOTAL_CP_LABEL_X, TOTAL_CP_LABEL_Y, 0x000000, false);
    }
}