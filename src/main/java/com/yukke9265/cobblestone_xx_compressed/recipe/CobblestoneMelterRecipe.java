package com.yukke9265.cobblestone_xx_compressed.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeSerializers;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class CobblestoneMelterRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<CobblestoneMelterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(CobblestoneMelterRecipe::getIngredient),
        FluidStack.CODEC.fieldOf("fluid_output").forGetter(CobblestoneMelterRecipe::getFluidResult),
        Codec.LONG.fieldOf("total_cobblestone_power").forGetter(CobblestoneMelterRecipe::getTotalCobblestonePower),
        Codec.LONG.optionalFieldOf("cobblestone_power_per_tick", 1L).forGetter(CobblestoneMelterRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (ingredient, fluidResult, totalCobblestonePower, cobblestonePowerPerTick) -> new CobblestoneMelterRecipe(
        ingredient,
        fluidResult,
        totalCobblestonePower.longValue(),
        cobblestonePowerPerTick.longValue()
    )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneMelterRecipe> STREAM_CODEC = StreamCodec.of(
        (buf, recipe) -> {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFluidResult());
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
        },
        buf -> new CobblestoneMelterRecipe(
            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
            FluidStack.STREAM_CODEC.decode(buf),
            buf.readLong(),
            buf.readLong()
        )
    );

    private final Ingredient ingredient;
    private final FluidStack fluidResult;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneMelterRecipe(Ingredient ingredient, FluidStack fluidResult, long totalCobblestonePower, long cobblestonePowerPerTick) {
        this.ingredient = ingredient;
        this.fluidResult = fluidResult.copy();
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public FluidStack getFluidResult() {
        return this.fluidResult.copy();
    }

    public long getTotalCobblestonePower() {
        return this.totalCobblestonePower;
    }

    public long getCobblestonePowerPerTick() {
        return this.cobblestonePowerPerTick;
    }

    public int getProcessingTime() {
        long processingTime = (this.totalCobblestonePower + this.cobblestonePowerPerTick - 1L) / this.cobblestonePowerPerTick;
        return Math.max(1, (int) Math.min(Integer.MAX_VALUE, processingTime));
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return this.ingredient.test(input.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_MELTER.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_MELTER.get();
    }
}