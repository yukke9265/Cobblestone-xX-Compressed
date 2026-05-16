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

public class CobblestonePoweredFurnaceRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<CobblestonePoweredFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(CobblestonePoweredFurnaceRecipe::getIngredient),
        ItemStack.CODEC.fieldOf("result").forGetter(CobblestonePoweredFurnaceRecipe::getResult),
        Codec.INT.fieldOf("total_cobblestone_power").forGetter(CobblestonePoweredFurnaceRecipe::getTotalCobblestonePower),
        Codec.INT.optionalFieldOf("cobblestone_power_per_tick", 1).forGetter(CobblestonePoweredFurnaceRecipe::getCobblestonePowerPerTick)
    ).apply(instance, CobblestonePoweredFurnaceRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestonePoweredFurnaceRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        CobblestonePoweredFurnaceRecipe::getIngredient,
        ItemStack.STREAM_CODEC,
        CobblestonePoweredFurnaceRecipe::getResult,
        ByteBufCodecs.INT,
        CobblestonePoweredFurnaceRecipe::getTotalCobblestonePower,
        ByteBufCodecs.INT,
        CobblestonePoweredFurnaceRecipe::getCobblestonePowerPerTick,
        CobblestonePoweredFurnaceRecipe::new
    );

    private final Ingredient ingredient;
    private final ItemStack result;
    private final int totalCobblestonePower;
    private final int cobblestonePowerPerTick;

    public CobblestonePoweredFurnaceRecipe(Ingredient ingredient, ItemStack result, int totalCobblestonePower, int cobblestonePowerPerTick) {
        this.ingredient = ingredient;
        this.result = result;
        this.cobblestonePowerPerTick = Math.max(1, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public int getTotalCobblestonePower() {
        return this.totalCobblestonePower;
    }

    public int getProcessingTime() {
        return Math.max(1, (this.totalCobblestonePower + this.cobblestonePowerPerTick - 1) / this.cobblestonePowerPerTick);
    }

    public int getCobblestonePowerPerTick() {
        return this.cobblestonePowerPerTick;
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
        return ModRecipeSerializers.COBBLESTONE_POWERED_FURNACE.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_POWERED_FURNACE.get();
    }
}