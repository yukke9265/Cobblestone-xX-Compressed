package com.yukke9265.cobblestone_xx_compressed.jei.category;

import java.util.List;
import java.util.Objects;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMixerRecipe;
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

public class CobblestoneMixerRecipeCategory implements IRecipeCategory<CobblestoneMixerRecipe> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_mixer.png");

    private static final int BACKGROUND_U = 3;// 0, 0 から始まるテクスチャの位置
    private static final int BACKGROUND_V = 6;// 0, 0 から始まるテクスチャの位置
    private static final int BACKGROUND_WIDTH = 176 - BACKGROUND_U * 2;// テクスチャの幅
    private static final int BACKGROUND_HEIGHT = 78 - BACKGROUND_V * 2;// テクスチャの高さ
    private static final int SLOT_SIZE = 18;// スロットのサイズ
    private static final int POWER_SLOT_X = 10 - BACKGROUND_U;
    private static final int POWER_SLOT_Y = 51 - BACKGROUND_V;
    private static final int INPUT_SLOT_1_X = 36 - BACKGROUND_U;
    private static final int INPUT_SLOT_1_Y = 31 - BACKGROUND_V;
    private static final int INPUT_SLOT_2_X = INPUT_SLOT_1_X + SLOT_SIZE + 2;
    private static final int INPUT_SLOT_2_Y = INPUT_SLOT_1_Y;
    private static final int OUTPUT_SLOT_X = INPUT_SLOT_2_X + SLOT_SIZE + 35;
    private static final int OUTPUT_SLOT_Y = INPUT_SLOT_1_Y;
    private static final int PROGRESS_FRAME_X = MachineGuiLayouts.Mixer.PROGRESS_BAR_X - BACKGROUND_U;
    private static final int PROGRESS_FRAME_Y = MachineGuiLayouts.Mixer.PROGRESS_BAR_Y - BACKGROUND_V;

    private static final int CPPt_LABEL_X = 8;
    private static final int CPPt_LABEL_Y = 2;
    private static final int TOTAL_CP_LABEL_X = 8;
    private static final int TOTAL_CP_LABEL_Y = 12;


    private final IDrawable background;
    private final IDrawable icon;

    public CobblestoneMixerRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, BACKGROUND_U, BACKGROUND_V, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COBBLESTONE_MIXER.get()));
    }

    @Override
    public RecipeType<CobblestoneMixerRecipe> getRecipeType() {
        return ModJeiPlugin.COBBLESTONE_MIXER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.cobblestonexxcompressed.cobblestone_mixer");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CobblestoneMixerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, POWER_SLOT_X, POWER_SLOT_Y)
            .addItemStacks(List.of(
                new ItemStack(Items.COBBLESTONE),
                new ItemStack(ModBlocks.COMPRESSED_COBBLESTONE.get()),
                new ItemStack(ModBlocks.TierCompressedCobblestone.COPPER.getBlock().get())
            ));

        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_1_X, INPUT_SLOT_1_Y)
            .addItemStack(Objects.requireNonNull(recipe.getFirstInput()));

        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOT_2_X, INPUT_SLOT_2_Y)
            .addItemStack(Objects.requireNonNull(recipe.getSecondInput()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_SLOT_X, OUTPUT_SLOT_Y)
            .addItemStack(recipe.getResult().copy());
    }

    @Override
    public void draw(CobblestoneMixerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        GuiPartRenderer.renderCobblestoneSlot(guiGraphics, POWER_SLOT_X, POWER_SLOT_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, INPUT_SLOT_1_X, INPUT_SLOT_1_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, INPUT_SLOT_2_X, INPUT_SLOT_2_Y);
        GuiPartRenderer.renderNormalSlot(guiGraphics, OUTPUT_SLOT_X, OUTPUT_SLOT_Y);
        GuiPartRenderer.renderProgressFrame(guiGraphics, PROGRESS_FRAME_X, PROGRESS_FRAME_Y);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getCobblestonePowerPerTick() + " CP/t", CPPt_LABEL_X, CPPt_LABEL_Y, 0x404040, false);
        guiGraphics.drawString(Minecraft.getInstance().font, recipe.getTotalCobblestonePower() + " total CP", TOTAL_CP_LABEL_X, TOTAL_CP_LABEL_Y, 0x404040, false);
    }
}