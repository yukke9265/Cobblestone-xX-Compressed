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

public class CobblestoneCrystallizationChamberRecipe implements Recipe<CrystallizationChamberRecipeInput> {
    public static final MapCodec<CobblestoneCrystallizationChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidStack.CODEC.fieldOf("fluid_input").forGetter(CobblestoneCrystallizationChamberRecipe::getFluidInput),
        ItemStack.CODEC.fieldOf("result").forGetter(CobblestoneCrystallizationChamberRecipe::getResult),
        Codec.LONG.fieldOf("total_cobblestone_power").forGetter(CobblestoneCrystallizationChamberRecipe::getTotalCobblestonePower),
        Codec.LONG.optionalFieldOf("cobblestone_power_per_tick", 1L).forGetter(CobblestoneCrystallizationChamberRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (fluidInput, result, totalCobblestonePower, cobblestonePowerPerTick) -> new CobblestoneCrystallizationChamberRecipe(
        fluidInput,
        result,
        totalCobblestonePower.longValue(),
        cobblestonePowerPerTick.longValue()
    )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneCrystallizationChamberRecipe> STREAM_CODEC = StreamCodec.of(
        (buf, recipe) -> {
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFluidInput());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getResult());
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
        },
        buf -> new CobblestoneCrystallizationChamberRecipe(
            FluidStack.STREAM_CODEC.decode(buf),
            ItemStack.STREAM_CODEC.decode(buf),
            buf.readLong(),
            buf.readLong()
        )
    );

    private final FluidStack fluidInput;
    private final ItemStack result;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneCrystallizationChamberRecipe(
        FluidStack fluidInput,
        ItemStack result,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.fluidInput = fluidInput.copy();
        this.result = result.copy();
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public FluidStack getFluidInput() {
        return this.fluidInput.copy();
    }

    public ItemStack getResult() {
        return this.result.copy();
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
