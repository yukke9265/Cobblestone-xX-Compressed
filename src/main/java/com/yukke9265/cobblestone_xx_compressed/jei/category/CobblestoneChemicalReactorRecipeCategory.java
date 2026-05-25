package com.yukke9265.cobblestone_xx_compressed.jei.category;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneChemicalReactorRecipe;
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
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidType;

@SuppressWarnings("null")
public class CobblestoneChemicalReactorRecipeCategory implements IRecipeCategory<CobblestoneChemicalReactorRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_chemical_reactor.png");

    private static final int BACKGROUND_U = 3;
    private static final int BACKGROUND_V = 6;
    private static final int BACKGROUND_WIDTH = 170;
    private static final int BACKGROUND_HEIGHT = 66;

    private static final int CPPT_LABEL_X = 36;
    private static final int CPPT_LABEL_Y = 50;//スロットと被らないように、下部に移動
    private static final int TOTAL_CP_LABEL_X = 36;
    private static final int TOTAL_CP_LABEL_Y = CPPT_LABEL_Y + 10;

    private final IDrawable background;
    private final IDrawable icon;

    public CobblestoneChemicalReactorRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COBBLESTONE_CHEMICAL_REACTOR.get()));
    }

    @Override
    public RecipeType<CobblestoneChemicalReactorRecipe> getRecipeType() {
        return ModJeiPlugin.COBBLESTONE_CHEMICAL_REACTOR_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_chemical_reactor");
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
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull CobblestoneChemicalReactorRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, MachineGuiLayouts.ChemicalReactor.POWER_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.POWER_SLOT_Y - BACKGROUND_V)
            .addItemStacks(JeiCobblestonePowerItems.getCatalystItems());

        if (recipe.hasFirstItemInput()) {
            builder.addSlot(RecipeIngredientRole.INPUT, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_1_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_1_SLOT_Y - BACKGROUND_V)
                .addItemStacks(getDisplayStacks(recipe.getFirstItemInput()));
        }
        if (recipe.hasSecondItemInput()) {
            builder.addSlot(RecipeIngredientRole.INPUT, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_2_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_2_SLOT_Y - BACKGROUND_V)
                .addItemStacks(getDisplayStacks(recipe.getSecondItemInput()));
        }
        if (recipe.hasFirstFluidInput()) {
            builder.addSlot(RecipeIngredientRole.INPUT, MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_1_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_1_SLOT_Y - BACKGROUND_V)
                .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(recipe.getFirstFluidInput()))
                .setFluidRenderer(Math.max(recipe.getFirstFluidInput().getAmount(), FluidType.BUCKET_VOLUME), false, 16, 16);
        }
        if (recipe.hasSecondFluidInput()) {
            builder.addSlot(RecipeIngredientRole.INPUT, MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_2_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_2_SLOT_Y - BACKGROUND_V)
                .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(recipe.getSecondFluidInput()))
                .setFluidRenderer(Math.max(recipe.getSecondFluidInput().getAmount(), FluidType.BUCKET_VOLUME), false, 16, 16);
        }
        if (recipe.hasFirstItemOutput()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_1_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_1_SLOT_Y - BACKGROUND_V)
                .addItemStack(recipe.getFirstResultItem());
        }
        if (recipe.hasSecondItemOutput()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_2_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_2_SLOT_Y - BACKGROUND_V)
                .addItemStack(recipe.getSecondResultItem());
        }
        if (recipe.hasFirstFluidOutput()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, MachineGuiLayouts.ChemicalReactor.OUTPUT_FLUID_1_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.OUTPUT_FLUID_1_SLOT_Y - BACKGROUND_V)
                .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(recipe.getFirstResultFluid()))
                .setFluidRenderer(Math.max(recipe.getFirstResultFluid().getAmount(), FluidType.BUCKET_VOLUME), false, 16, 16);
        }
        if (recipe.hasSecondFluidOutput()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, MachineGuiLayouts.ChemicalReactor.OUTPUT_FLUID_2_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.OUTPUT_FLUID_2_SLOT_Y - BACKGROUND_V)
                .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(recipe.getSecondResultFluid()))
                .setFluidRenderer(Math.max(recipe.getSecondResultFluid().getAmount(), FluidType.BUCKET_VOLUME), false, 16, 16);
        }
    }

    @Override
    public void draw(@Nonnull CobblestoneChemicalReactorRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_1_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_1_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_2_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.INPUT_ITEM_2_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_1_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_1_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_2_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.INPUT_FLUID_2_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_1_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_1_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_2_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.OUTPUT_ITEM_2_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.ChemicalReactor.OUTPUT_FLUID_1_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.OUTPUT_FLUID_1_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.ChemicalReactor.OUTPUT_FLUID_2_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.OUTPUT_FLUID_2_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderCobblestoneSlot(guiGraphics, MachineGuiLayouts.ChemicalReactor.POWER_SLOT_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.POWER_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderProgressFrame(guiGraphics, MachineGuiLayouts.ChemicalReactor.PROGRESS_BAR_X - BACKGROUND_U, MachineGuiLayouts.ChemicalReactor.PROGRESS_BAR_Y - BACKGROUND_V);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getCobblestonePowerPerTick() + " CP/t", CPPT_LABEL_X, CPPT_LABEL_Y, 0x404040, false);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getTotalCobblestonePower() + " total CP", TOTAL_CP_LABEL_X, TOTAL_CP_LABEL_Y, 0x404040, false);
    }

    private static List<ItemStack> getDisplayStacks(SizedIngredient ingredient) {
        return Arrays.stream(ingredient.getItems())
            .map(stack -> stack.copyWithCount(ingredient.count()))
            .toList();
    }
}