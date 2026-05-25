package com.yukke9265.cobblestone_xx_compressed.jei.category;

import java.util.List;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMelterRecipe;
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

public class CobblestoneMelterRecipeCategory implements IRecipeCategory<CobblestoneMelterRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_melter.png");

    private static final int BACKGROUND_U = 3;
    private static final int BACKGROUND_V = 6;
    private static final int BACKGROUND_WIDTH = 176 - BACKGROUND_U * 2;
    private static final int BACKGROUND_HEIGHT = 78 - BACKGROUND_V * 2;
    private static final int INPUT_SLOT_X = MachineGuiLayouts.PoweredMachine.INPUT_SLOT_X - BACKGROUND_U;
    private static final int INPUT_SLOT_Y = MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y - BACKGROUND_V;
    private static final int FLUID_SLOT_X = MachineGuiLayouts.PoweredMachine.OUTPUT_SLOT_X - BACKGROUND_U;
    private static final int FLUID_SLOT_Y = MachineGuiLayouts.PoweredMachine.MACHINE_SLOT_Y - BACKGROUND_V;
    private static final int POWER_SLOT_X = MachineGuiLayouts.PoweredMachine.POWER_SLOT_X - BACKGROUND_U;
    private static final int POWER_SLOT_Y = MachineGuiLayouts.PoweredMachine.POWER_SLOT_Y - BACKGROUND_V;
    private static final int PROGRESS_FRAME_X = MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_X - BACKGROUND_U;
    private static final int PROGRESS_FRAME_Y = MachineGuiLayouts.PoweredMachine.PROGRESS_BAR_Y - BACKGROUND_V;
    private static final int CPPT_LABEL_X = 8;
    private static final int CPPT_LABEL_Y = 2;
    private static final int TOTAL_CP_LABEL_X = 8;
    private static final int TOTAL_CP_LABEL_Y = 12;

    private final IDrawable background;
    private final IDrawable icon;

    public CobblestoneMelterRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COBBLESTONE_MELTER.get()));
    }

    @Override
    public RecipeType<CobblestoneMelterRecipe> getRecipeType() {
        return ModJeiPlugin.COBBLESTONE_MELTER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_melter");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CobblestoneMelterRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_X, INPUT_SLOT_Y)
            .addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.CATALYST, POWER_SLOT_X, POWER_SLOT_Y)
            .addItemStacks(JeiCobblestonePowerItems.getCatalystItems());

        builder.addSlot(RecipeIngredientRole.OUTPUT, FLUID_SLOT_X, FLUID_SLOT_Y)
            .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(recipe.getFluidResult()))
            .setFluidRenderer(Math.max(recipe.getFluidResult().getAmount(), FluidType.BUCKET_VOLUME), false, 16, 16);
    }

    @Override
    public void draw(CobblestoneMelterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GuiPartRenderer.renderCobblestoneSlot(guiGraphics, POWER_SLOT_X, POWER_SLOT_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, INPUT_SLOT_X, INPUT_SLOT_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, FLUID_SLOT_X, FLUID_SLOT_Y);
        GuiPartRenderer.renderProgressFrame(guiGraphics, PROGRESS_FRAME_X, PROGRESS_FRAME_Y);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getCobblestonePowerPerTick() + " CP/t", CPPT_LABEL_X, CPPT_LABEL_Y, 0x404040, false);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getTotalCobblestonePower() + " total CP", TOTAL_CP_LABEL_X, TOTAL_CP_LABEL_Y, 0x404040, false);
    }
}