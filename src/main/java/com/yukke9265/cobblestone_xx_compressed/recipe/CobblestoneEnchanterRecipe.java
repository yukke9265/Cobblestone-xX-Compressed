package com.yukke9265.cobblestone_xx_compressed.recipe;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeSerializers;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.util.CobblestoneEnchanterHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@SuppressWarnings("null")
public class CobblestoneEnchanterRecipe implements Recipe<CobblestoneEnchanterRecipeInput> {
    public static final MapCodec<CobblestoneEnchanterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("book_ingredient").forGetter(CobblestoneEnchanterRecipe::getBookIngredient),
        ItemStack.CODEC.fieldOf("jei_preview_tool").forGetter(CobblestoneEnchanterRecipe::getJeiPreviewTool),
        ItemStack.CODEC.fieldOf("jei_preview_book").forGetter(CobblestoneEnchanterRecipe::getJeiPreviewBook),
        ItemStack.CODEC.fieldOf("jei_preview_result").forGetter(CobblestoneEnchanterRecipe::getJeiPreviewResult),
        Codec.INT.optionalFieldOf("processing_ticks", CobblestoneEnchanterHelper.DEFAULT_PROCESSING_TICKS).forGetter(CobblestoneEnchanterRecipe::getProcessingTicks)
    ).apply(instance, (bookIngredient, jeiPreviewTool, jeiPreviewBook, jeiPreviewResult, processingTicks) ->
        new CobblestoneEnchanterRecipe(bookIngredient, jeiPreviewTool, jeiPreviewBook, jeiPreviewResult, processingTicks.intValue())));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneEnchanterRecipe> STREAM_CODEC = StreamCodec.of(
        (buf, recipe) -> {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getBookIngredient());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getJeiPreviewTool());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getJeiPreviewBook());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getJeiPreviewResult());
            buf.writeInt(recipe.getProcessingTicks());
        },
        buf -> new CobblestoneEnchanterRecipe(
            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
            ItemStack.STREAM_CODEC.decode(buf),
            ItemStack.STREAM_CODEC.decode(buf),
            ItemStack.STREAM_CODEC.decode(buf),
            buf.readInt()
        )
    );

    private final Ingredient bookIngredient;
    private final ItemStack jeiPreviewTool;
    private final ItemStack jeiPreviewBook;
    private final ItemStack jeiPreviewResult;
    private final int processingTicks;

    public CobblestoneEnchanterRecipe(
        Ingredient bookIngredient,
        ItemStack jeiPreviewTool,
        ItemStack jeiPreviewBook,
        ItemStack jeiPreviewResult,
        int processingTicks
    ) {
        this.bookIngredient = bookIngredient;
        this.jeiPreviewTool = jeiPreviewTool.copy();
        this.jeiPreviewBook = jeiPreviewBook.copy();
        this.jeiPreviewResult = jeiPreviewResult.copy();
        this.processingTicks = Math.max(1, processingTicks);
    }

    public Ingredient getBookIngredient() {
        return this.bookIngredient;
    }

    public ItemStack getJeiPreviewTool() {
        return this.jeiPreviewTool.copy();
    }

    public ItemStack getJeiPreviewBook() {
        return this.jeiPreviewBook.copy();
    }

    public ItemStack getJeiPreviewResult() {
        return this.jeiPreviewResult.copy();
    }

    public int getProcessingTicks() {
        return this.processingTicks;
    }

    public CobblestoneEnchanterHelper.EvaluationResult evaluate(CobblestoneEnchanterRecipeInput input, HolderLookup.Provider holderLookup) {
        return CobblestoneEnchanterHelper.evaluate(input.getToolInput(), input.getBookInput(), holderLookup);
    }

    @Override
    public boolean matches(@Nonnull CobblestoneEnchanterRecipeInput input, @Nonnull Level level) {
        if (!this.bookIngredient.test(input.getBookInput())) {
            return false;
        }

        return this.evaluate(input, level.registryAccess()).isValid();
    }

    @Override
    public @Nonnull ItemStack assemble(@Nonnull CobblestoneEnchanterRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        return this.evaluate(input, registries).resultStack().copy();
    }

    @Override
    public @Nonnull ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return this.jeiPreviewResult.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<CobblestoneEnchanterRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_ENCHANTER.get();
    }

    @Override
    public RecipeType<? extends Recipe<CobblestoneEnchanterRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_ENCHANTER.get();
    }
}