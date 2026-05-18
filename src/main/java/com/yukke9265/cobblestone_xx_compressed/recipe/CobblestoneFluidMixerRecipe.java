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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class CobblestoneFluidMixerRecipe implements Recipe<FluidMixerRecipeInput> {
    public static final MapCodec<CobblestoneFluidMixerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidStack.CODEC.fieldOf("fluid_input_1").forGetter(CobblestoneFluidMixerRecipe::getFirstFluidInput),
        FluidStack.CODEC.fieldOf("fluid_input_2").forGetter(CobblestoneFluidMixerRecipe::getSecondFluidInput),
        FluidStack.CODEC.fieldOf("fluid_output").forGetter(CobblestoneFluidMixerRecipe::getFluidOutput),
        Codec.INT.fieldOf("total_cobblestone_power").forGetter(CobblestoneFluidMixerRecipe::getTotalCobblestonePower),
        Codec.INT.fieldOf("cobblestone_power_per_tick").forGetter(CobblestoneFluidMixerRecipe::getCobblestonePowerPerTick)
    ).apply(instance, CobblestoneFluidMixerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneFluidMixerRecipe> STREAM_CODEC = StreamCodec.composite(
        FluidStack.STREAM_CODEC,
        CobblestoneFluidMixerRecipe::getFirstFluidInput,
        FluidStack.STREAM_CODEC,
        CobblestoneFluidMixerRecipe::getSecondFluidInput,
        FluidStack.STREAM_CODEC,
        CobblestoneFluidMixerRecipe::getFluidOutput,
        ByteBufCodecs.INT,
        CobblestoneFluidMixerRecipe::getTotalCobblestonePower,
        ByteBufCodecs.INT,
        CobblestoneFluidMixerRecipe::getCobblestonePowerPerTick,
        CobblestoneFluidMixerRecipe::new
    );

    private final FluidStack firstFluidInput;
    private final FluidStack secondFluidInput;
    private final FluidStack fluidOutput;
    private final int totalCobblestonePower;
    private final int cobblestonePowerPerTick;

    public CobblestoneFluidMixerRecipe(
        FluidStack firstFluidInput,
        FluidStack secondFluidInput,
        FluidStack fluidOutput,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        this.firstFluidInput = firstFluidInput.copy();
        this.secondFluidInput = secondFluidInput.copy();
        this.fluidOutput = fluidOutput.copy();
        this.cobblestonePowerPerTick = Math.max(1, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public FluidStack getFirstFluidInput() {
        return this.firstFluidInput.copy();
    }

    public FluidStack getSecondFluidInput() {
        return this.secondFluidInput.copy();
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
    public boolean matches(@NotNull FluidMixerRecipeInput input, @NotNull Level level) {
        FluidStack firstFluid = input.getFirstFluidInput();
        if (firstFluid.isEmpty() || !FluidStack.isSameFluidSameComponents(firstFluid, this.firstFluidInput)) {
            return false;
        }

        if (firstFluid.getAmount() < this.firstFluidInput.getAmount()) {
            return false;
        }

        FluidStack secondFluid = input.getSecondFluidInput();
        if (secondFluid.isEmpty() || !FluidStack.isSameFluidSameComponents(secondFluid, this.secondFluidInput)) {
            return false;
        }

        return secondFluid.getAmount() >= this.secondFluidInput.getAmount();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull FluidMixerRecipeInput input, @NotNull HolderLookup.Provider registries) {
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
    public RecipeSerializer<? extends Recipe<FluidMixerRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_FLUID_MIXER.get();
    }

    @Override
    public RecipeType<? extends Recipe<FluidMixerRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_FLUID_MIXER.get();
    }
}