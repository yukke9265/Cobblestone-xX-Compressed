package com.yukke9265.cobblestone_xx_compressed.jei.category;

import java.util.List;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneAssemblyMachineRecipe;
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

@SuppressWarnings("null")
public class CobblestoneAssemblyMachineRecipeCategory implements IRecipeCategory<CobblestoneAssemblyMachineRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_assembly_machine.png");

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

    public CobblestoneAssemblyMachineRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COBBLESTONE_ASSEMBLY_MACHINE.get()));
    }

    @Override
    public RecipeType<CobblestoneAssemblyMachineRecipe> getRecipeType() {
        return ModJeiPlugin.COBBLESTONE_ASSEMBLY_MACHINE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_assembly_machine");
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
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull CobblestoneAssemblyMachineRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, MachineGuiLayouts.AssemblyMachine.POWER_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.POWER_SLOT_Y - BACKGROUND_V)
            .addItemStacks(JeiCobblestonePowerItems.getCatalystItems());

        this.addInputItemSlot(builder, recipe.getFirstItemInput(), MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_1_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_1_SLOT_Y);
        this.addInputItemSlot(builder, recipe.getSecondItemInput(), MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_2_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_2_SLOT_Y);
        this.addInputItemSlot(builder, recipe.getThirdItemInput(), MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_3_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_3_SLOT_Y);
        this.addInputItemSlot(builder, recipe.getFourthItemInput(), MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_4_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_4_SLOT_Y);
        this.addInputItemSlot(builder, recipe.getFifthItemInput(), MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_5_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_5_SLOT_Y);
        this.addInputItemSlot(builder, recipe.getSixthItemInput(), MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_6_SLOT_X, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_6_SLOT_Y);

        if (recipe.hasFluidInput()) {
            builder.addSlot(RecipeIngredientRole.INPUT, MachineGuiLayouts.AssemblyMachine.INPUT_FLUID_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.INPUT_FLUID_SLOT_Y - BACKGROUND_V)
                .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(recipe.getFluidInput()))
                .setFluidRenderer(Math.max(recipe.getFluidInput().getAmount(), FluidType.BUCKET_VOLUME), false, 16, 16);
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, MachineGuiLayouts.AssemblyMachine.OUTPUT_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.OUTPUT_SLOT_Y - BACKGROUND_V)
            .addItemStack(recipe.getResultItemStack());
    }

    @Override
    public void draw(@Nonnull CobblestoneAssemblyMachineRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_1_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_1_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_2_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_2_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_3_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_3_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_4_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_4_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_5_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_5_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_6_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.INPUT_ITEM_6_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.AssemblyMachine.INPUT_FLUID_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.INPUT_FLUID_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderCobblestoneSlot(guiGraphics, MachineGuiLayouts.AssemblyMachine.POWER_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.POWER_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderNormalSlot(guiGraphics, MachineGuiLayouts.AssemblyMachine.OUTPUT_SLOT_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.OUTPUT_SLOT_Y - BACKGROUND_V);
        GuiPartRenderer.renderProgressFrame(guiGraphics, MachineGuiLayouts.AssemblyMachine.PROGRESS_BAR_X - BACKGROUND_U, MachineGuiLayouts.AssemblyMachine.PROGRESS_BAR_Y - BACKGROUND_V);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getCobblestonePowerPerTick() + " CP/t", CPPT_LABEL_X, CPPT_LABEL_Y, 0x404040, false);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getTotalCobblestonePower() + " total CP", TOTAL_CP_LABEL_X, TOTAL_CP_LABEL_Y, 0x404040, false);
    }

    private void addInputItemSlot(IRecipeLayoutBuilder builder, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) {
            return;
        }

        builder.addSlot(RecipeIngredientRole.INPUT, x - BACKGROUND_U, y - BACKGROUND_V)
            .addItemStack(stack);
    }
}