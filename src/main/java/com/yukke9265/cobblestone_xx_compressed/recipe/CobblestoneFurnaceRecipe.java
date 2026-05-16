package com.yukke9265.cobblestone_xx_compressed.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeSerializers;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class CobblestoneFurnaceRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<CobblestoneFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(CobblestoneFurnaceRecipe::getIngredient),
        ItemStack.CODEC.fieldOf("result").forGetter(CobblestoneFurnaceRecipe::getResult),
        Codec.INT.optionalFieldOf("processing_time", 200).forGetter(CobblestoneFurnaceRecipe::getProcessingTime)
    ).apply(instance, (ingredient, result, processingTime) -> new CobblestoneFurnaceRecipe(ingredient, result, processingTime)));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneFurnaceRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        CobblestoneFurnaceRecipe::getIngredient,
        ItemStack.STREAM_CODEC,
        CobblestoneFurnaceRecipe::getResult,
        ByteBufCodecs.INT,
        CobblestoneFurnaceRecipe::getProcessingTime,
        (ingredient, result, processingTime) -> new CobblestoneFurnaceRecipe(ingredient, result, processingTime)
    );

    private final Ingredient ingredient;
    private final ItemStack result;
    private final int processingTime;

    public CobblestoneFurnaceRecipe(Ingredient ingredient, ItemStack result, int processingTime) {
        this.ingredient = ingredient;
        this.result = result;
        this.processingTime = processingTime;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return this.ingredient.test(input.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_FURNACE.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_FURNACE.get();
    }
}