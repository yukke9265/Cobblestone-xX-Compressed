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
import net.neoforged.neoforge.fluids.FluidStack;

public class CobblestoneDissolutionChamberRecipe implements Recipe<DissolutionChamberRecipeInput> {
    public static final MapCodec<CobblestoneDissolutionChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(CobblestoneDissolutionChamberRecipe::getIngredient),
        FluidStack.CODEC.fieldOf("fluid_input").forGetter(CobblestoneDissolutionChamberRecipe::getFluidInput),
        FluidStack.CODEC.fieldOf("fluid_output").forGetter(CobblestoneDissolutionChamberRecipe::getFluidOutput),
        Codec.INT.fieldOf("total_cobblestone_power").forGetter(CobblestoneDissolutionChamberRecipe::getTotalCobblestonePower),
        Codec.INT.fieldOf("cobblestone_power_per_tick").forGetter(CobblestoneDissolutionChamberRecipe::getCobblestonePowerPerTick)
    ).apply(instance, CobblestoneDissolutionChamberRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneDissolutionChamberRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC,
        CobblestoneDissolutionChamberRecipe::getIngredient,
        FluidStack.STREAM_CODEC,
        CobblestoneDissolutionChamberRecipe::getFluidInput,
        FluidStack.STREAM_CODEC,
        CobblestoneDissolutionChamberRecipe::getFluidOutput,
        ByteBufCodecs.INT,
        CobblestoneDissolutionChamberRecipe::getTotalCobblestonePower,
        ByteBufCodecs.INT,
        CobblestoneDissolutionChamberRecipe::getCobblestonePowerPerTick,
        CobblestoneDissolutionChamberRecipe::new
    );

    private final Ingredient ingredient;
    private final FluidStack fluidInput;
    private final FluidStack fluidOutput;
    private final int totalCobblestonePower;
    private final int cobblestonePowerPerTick;

    public CobblestoneDissolutionChamberRecipe(
        Ingredient ingredient,
        FluidStack fluidInput,
        FluidStack fluidOutput,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        this.ingredient = ingredient;
        this.fluidInput = fluidInput.copy();
        this.fluidOutput = fluidOutput.copy();
        this.cobblestonePowerPerTick = Math.max(1, cobblestonePowerPerTick);
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