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
import net.minecraft.world.level.Level;

public class CobblestoneMixerRecipe implements Recipe<DoubleItemRecipeInput> {
    public static final MapCodec<CobblestoneMixerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient_1").forGetter(CobblestoneMixerRecipe::getFirstIngredient),
        Ingredient.CODEC.fieldOf("ingredient_2").forGetter(CobblestoneMixerRecipe::getSecondIngredient),
        ItemStack.CODEC.fieldOf("result").forGetter(CobblestoneMixerRecipe::getResult),
        Codec.INT.fieldOf("total_cobblestone_power").forGetter(CobblestoneMixerRecipe::getTotalCobblestonePower),
        Codec.INT.optionalFieldOf("cobblestone_power_per_tick", 1).forGetter(CobblestoneMixerRecipe::getCobblestonePowerPerTick)
    ).apply(instance, CobblestoneMixerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneMixerRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        CobblestoneMixerRecipe::getFirstIngredient,
        Ingredient.CONTENTS_STREAM_CODEC,
        CobblestoneMixerRecipe::getSecondIngredient,
        ItemStack.STREAM_CODEC,
        CobblestoneMixerRecipe::getResult,
        ByteBufCodecs.INT,
        CobblestoneMixerRecipe::getTotalCobblestonePower,
        ByteBufCodecs.INT,
        CobblestoneMixerRecipe::getCobblestonePowerPerTick,
        CobblestoneMixerRecipe::new
    );

    private final Ingredient firstIngredient;
    private final Ingredient secondIngredient;
    private final ItemStack result;
    private final int totalCobblestonePower;
    private final int cobblestonePowerPerTick;

    public CobblestoneMixerRecipe(
        Ingredient firstIngredient,
        Ingredient secondIngredient,
        ItemStack result,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        this.firstIngredient = firstIngredient;
        this.secondIngredient = secondIngredient;
        this.result = result;
        this.cobblestonePowerPerTick = Math.max(1, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public Ingredient getFirstIngredient() {
        return this.firstIngredient;
    }

    public Ingredient getSecondIngredient() {
        return this.secondIngredient;
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
    public boolean matches(@NotNull DoubleItemRecipeInput input, @NotNull Level level) {
        return this.firstIngredient.test(input.getItem(0))
            && this.secondIngredient.test(input.getItem(1));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull DoubleItemRecipeInput input, @NotNull HolderLookup.Provider registries) {
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
    public RecipeSerializer<? extends Recipe<DoubleItemRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_MIXER.get();
    }

    @Override
    public RecipeType<? extends Recipe<DoubleItemRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_MIXER.get();
    }
}