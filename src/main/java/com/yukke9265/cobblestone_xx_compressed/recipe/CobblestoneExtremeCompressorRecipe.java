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

public class CobblestoneExtremeCompressorRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<CobblestoneExtremeCompressorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(CobblestoneExtremeCompressorRecipe::getIngredient),
        ItemStack.CODEC.fieldOf("result").forGetter(CobblestoneExtremeCompressorRecipe::getResult),
        Codec.INT.fieldOf("required_item_count").forGetter(CobblestoneExtremeCompressorRecipe::getRequiredItemCount),
        Codec.INT.fieldOf("total_cobblestone_power").forGetter(CobblestoneExtremeCompressorRecipe::getTotalCobblestonePower),
        Codec.INT.optionalFieldOf("cobblestone_power_per_tick", 1).forGetter(CobblestoneExtremeCompressorRecipe::getCobblestonePowerPerTick)
    ).apply(instance, CobblestoneExtremeCompressorRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneExtremeCompressorRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        CobblestoneExtremeCompressorRecipe::getIngredient,
        ItemStack.STREAM_CODEC,
        CobblestoneExtremeCompressorRecipe::getResult,
        ByteBufCodecs.INT,
        CobblestoneExtremeCompressorRecipe::getRequiredItemCount,
        ByteBufCodecs.INT,
        CobblestoneExtremeCompressorRecipe::getTotalCobblestonePower,
        ByteBufCodecs.INT,
        CobblestoneExtremeCompressorRecipe::getCobblestonePowerPerTick,
        CobblestoneExtremeCompressorRecipe::new
    );

    private final Ingredient ingredient;
    private final ItemStack result;
    private final int requiredItemCount;
    private final int totalCobblestonePower;
    private final int cobblestonePowerPerTick;

    public CobblestoneExtremeCompressorRecipe(
        Ingredient ingredient,
        ItemStack result,
        int requiredItemCount,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        this.ingredient = ingredient;
        this.result = result;
        this.requiredItemCount = Math.max(1, requiredItemCount);
        this.cobblestonePowerPerTick = Math.max(1, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public int getRequiredItemCount() {
        return this.requiredItemCount;
    }

    public int getTotalCobblestonePower() {
        return this.totalCobblestonePower;
    }

    public int getCobblestonePowerPerTick() {
        return this.cobblestonePowerPerTick;
    }

    public int getProcessingTime() {
        return Math.max(1, (this.totalCobblestonePower + this.cobblestonePowerPerTick - 1) / this.cobblestonePowerPerTick);
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
        return ModRecipeSerializers.COBBLESTONE_EXTREME_COMPRESSOR.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_EXTREME_COMPRESSOR.get();
    }
}