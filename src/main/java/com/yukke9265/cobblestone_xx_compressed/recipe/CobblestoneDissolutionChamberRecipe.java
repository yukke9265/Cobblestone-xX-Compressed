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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class CobblestoneDissolutionChamberRecipe implements Recipe<DissolutionChamberRecipeInput> {
    public static final MapCodec<CobblestoneDissolutionChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(CobblestoneDissolutionChamberRecipe::getIngredient),
        FluidStack.CODEC.fieldOf("fluid_input").forGetter(CobblestoneDissolutionChamberRecipe::getFluidInput),
        FluidStack.CODEC.fieldOf("fluid_output").forGetter(CobblestoneDissolutionChamberRecipe::getFluidOutput),
        Codec.LONG.fieldOf("total_cobblestone_power").forGetter(CobblestoneDissolutionChamberRecipe::getTotalCobblestonePower),
        Codec.LONG.fieldOf("cobblestone_power_per_tick").forGetter(CobblestoneDissolutionChamberRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (ingredient, fluidInput, fluidOutput, totalCobblestonePower, cobblestonePowerPerTick) -> new CobblestoneDissolutionChamberRecipe(
        ingredient,
        fluidInput,
        fluidOutput,
        totalCobblestonePower.longValue(),
        cobblestonePowerPerTick.longValue()
    )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneDissolutionChamberRecipe> STREAM_CODEC = StreamCodec.of(
        (buf, recipe) -> {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFluidInput());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFluidOutput());
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
        },
        buf -> new CobblestoneDissolutionChamberRecipe(
            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
            FluidStack.STREAM_CODEC.decode(buf),
            FluidStack.STREAM_CODEC.decode(buf),
            buf.readLong(),
            buf.readLong()
        )
    );

    private final Ingredient ingredient;
    private final FluidStack fluidInput;
    private final FluidStack fluidOutput;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneDissolutionChamberRecipe(
        Ingredient ingredient,
        FluidStack fluidInput,
        FluidStack fluidOutput,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.ingredient = ingredient;
        this.fluidInput = fluidInput.copy();
        this.fluidOutput = fluidOutput.copy();
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public FluidStack getFluidInput() {
        return this.fluidInput.copy();
    }

    public FluidStack getFluidOutput() {
        return this.fluidOutput.copy();
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
    public boolean matches(@NotNull DissolutionChamberRecipeInput input, @NotNull Level level) {
        if (!this.ingredient.test(input.getItem(0))) {
            return false;
        }

        FluidStack inputFluid = input.getFluidInput();
        if (inputFluid.isEmpty()) {
            return false;
        }

        if (!FluidStack.isSameFluidSameComponents(inputFluid, this.fluidInput)) {
            return false;
        }

        return inputFluid.getAmount() >= this.fluidInput.getAmount();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull DissolutionChamberRecipeInput input, @NotNull HolderLookup.Provider registries) {
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
    public RecipeSerializer<? extends Recipe<DissolutionChamberRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_DISSOLUTION_CHAMBER.get();
    }

    @Override
    public RecipeType<? extends Recipe<DissolutionChamberRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_DISSOLUTION_CHAMBER.get();
    }
}