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

public class CobblestoneCrystallizationChamberRecipe implements Recipe<CrystallizationChamberRecipeInput> {
    public static final MapCodec<CobblestoneCrystallizationChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidStack.CODEC.fieldOf("fluid_input").forGetter(CobblestoneCrystallizationChamberRecipe::getFluidInput),
        ItemStack.CODEC.fieldOf("result").forGetter(CobblestoneCrystallizationChamberRecipe::getResult),
        Codec.INT.fieldOf("total_cobblestone_power").forGetter(CobblestoneCrystallizationChamberRecipe::getTotalCobblestonePower),
        Codec.INT.optionalFieldOf("cobblestone_power_per_tick", 1).forGetter(CobblestoneCrystallizationChamberRecipe::getCobblestonePowerPerTick)
    ).apply(instance, CobblestoneCrystallizationChamberRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneCrystallizationChamberRecipe> STREAM_CODEC = StreamCodec.composite(
        FluidStack.STREAM_CODEC,
        CobblestoneCrystallizationChamberRecipe::getFluidInput,
        ItemStack.STREAM_CODEC,
        CobblestoneCrystallizationChamberRecipe::getResult,
        ByteBufCodecs.INT,
        CobblestoneCrystallizationChamberRecipe::getTotalCobblestonePower,
        ByteBufCodecs.INT,
        CobblestoneCrystallizationChamberRecipe::getCobblestonePowerPerTick,
        CobblestoneCrystallizationChamberRecipe::new
    );

    private final FluidStack fluidInput;
    private final ItemStack result;
    private final int totalCobblestonePower;
    private final int cobblestonePowerPerTick;

    public CobblestoneCrystallizationChamberRecipe(
        FluidStack fluidInput,
        ItemStack result,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        this.fluidInput = fluidInput.copy();
        this.result = result.copy();
        this.cobblestonePowerPerTick = Math.max(1, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public FluidStack getFluidInput() {
        return this.fluidInput.copy();
    }

    public ItemStack getResult() {
        return this.result.copy();
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
    public boolean matches(@NotNull CrystallizationChamberRecipeInput input, @NotNull Level level) {
        FluidStack fluidStack = input.getFluidInput();
        if (fluidStack.isEmpty()) {
            return false;
        }

        if (!FluidStack.isSameFluidSameComponents(fluidStack, this.fluidInput)) {
            return false;
        }

        return fluidStack.getAmount() >= this.fluidInput.getAmount();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CrystallizationChamberRecipeInput input, @NotNull HolderLookup.Provider registries) {
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
    public RecipeSerializer<? extends Recipe<CrystallizationChamberRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_CRYSTALLIZATION_CHAMBER.get();
    }

    @Override
    public RecipeType<? extends Recipe<CrystallizationChamberRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_CRYSTALLIZATION_CHAMBER.get();
    }
}
