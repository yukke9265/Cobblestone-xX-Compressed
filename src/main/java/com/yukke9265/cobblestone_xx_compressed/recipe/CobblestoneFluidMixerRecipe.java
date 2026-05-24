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
        Codec.LONG.fieldOf("total_cobblestone_power").forGetter(CobblestoneFluidMixerRecipe::getTotalCobblestonePower),
        Codec.LONG.fieldOf("cobblestone_power_per_tick").forGetter(CobblestoneFluidMixerRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (firstFluidInput, secondFluidInput, fluidOutput, totalCobblestonePower, cobblestonePowerPerTick) -> new CobblestoneFluidMixerRecipe(
        firstFluidInput,
        secondFluidInput,
        fluidOutput,
        totalCobblestonePower.longValue(),
        cobblestonePowerPerTick.longValue()
    )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneFluidMixerRecipe> STREAM_CODEC = StreamCodec.of(
        (buf, recipe) -> {
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFirstFluidInput());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getSecondFluidInput());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFluidOutput());
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
        },
        buf -> new CobblestoneFluidMixerRecipe(
            FluidStack.STREAM_CODEC.decode(buf),
            FluidStack.STREAM_CODEC.decode(buf),
            FluidStack.STREAM_CODEC.decode(buf),
            buf.readLong(),
            buf.readLong()
        )
    );

    private final FluidStack firstFluidInput;
    private final FluidStack secondFluidInput;
    private final FluidStack fluidOutput;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneFluidMixerRecipe(
        FluidStack firstFluidInput,
        FluidStack secondFluidInput,
        FluidStack fluidOutput,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.firstFluidInput = firstFluidInput.copy();
        this.secondFluidInput = secondFluidInput.copy();
        this.fluidOutput = fluidOutput.copy();
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
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