package com.yukke9265.cobblestone_xx_compressed.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeSerializers;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

public class CobblestoneDissolutionChamberRecipe implements Recipe<DissolutionChamberRecipeInput> {
    // datagen 時に外部 mod の Fluid 実体が無くても JSON を出せるよう、
    // 出力流体は id + amount で保持し、実行時に FluidStack へ解決します。
    private static final Codec<FluidResult> FLUID_RESULT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("id").forGetter(FluidResult::id),
        Codec.INT.fieldOf("amount").forGetter(FluidResult::amount)
    ).apply(instance, FluidResult::new));

    public static final MapCodec<CobblestoneDissolutionChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        SizedIngredient.FLAT_CODEC.fieldOf("ingredient").forGetter(CobblestoneDissolutionChamberRecipe::getItemInput),
        FluidStack.CODEC.fieldOf("fluid_input").forGetter(CobblestoneDissolutionChamberRecipe::getFluidInput),
        FLUID_RESULT_CODEC.fieldOf("fluid_output").forGetter(CobblestoneDissolutionChamberRecipe::getFluidResult),
        Codec.LONG.fieldOf("total_cobblestone_power").forGetter(CobblestoneDissolutionChamberRecipe::getTotalCobblestonePower),
        Codec.LONG.fieldOf("cobblestone_power_per_tick").forGetter(CobblestoneDissolutionChamberRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (itemInput, fluidInput, fluidOutput, totalCobblestonePower, cobblestonePowerPerTick) -> new CobblestoneDissolutionChamberRecipe(
        itemInput,
        fluidInput,
        fluidOutput.id(),
        fluidOutput.amount(),
        totalCobblestonePower.longValue(),
        cobblestonePowerPerTick.longValue()
    )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneDissolutionChamberRecipe> STREAM_CODEC = StreamCodec.of(
        (buf, recipe) -> {
            SizedIngredient.STREAM_CODEC.encode(buf, recipe.getItemInput());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFluidInput());
            ResourceLocation.STREAM_CODEC.encode(buf, recipe.fluidOutputId);
            ByteBufCodecs.VAR_INT.encode(buf, recipe.fluidOutputAmount);
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
        },
        buf -> new CobblestoneDissolutionChamberRecipe(
            SizedIngredient.STREAM_CODEC.decode(buf),
            FluidStack.STREAM_CODEC.decode(buf),
            ResourceLocation.STREAM_CODEC.decode(buf),
            ByteBufCodecs.VAR_INT.decode(buf),
            buf.readLong(),
            buf.readLong()
        )
    );

    private final SizedIngredient itemInput;
    private final FluidStack fluidInput;
    private final ResourceLocation fluidOutputId;
    private final int fluidOutputAmount;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneDissolutionChamberRecipe(
        SizedIngredient itemInput,
        FluidStack fluidInput,
        FluidStack fluidOutput,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this(
            itemInput,
            fluidInput,
            BuiltInRegistries.FLUID.getKey(fluidOutput.getFluid()),
            fluidOutput.getAmount(),
            totalCobblestonePower,
            cobblestonePowerPerTick
        );
    }

    public CobblestoneDissolutionChamberRecipe(
        SizedIngredient itemInput,
        FluidStack fluidInput,
        ResourceLocation fluidOutputId,
        int fluidOutputAmount,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.itemInput = itemInput;
        this.fluidInput = fluidInput.copy();
        this.fluidOutputId = fluidOutputId;
        this.fluidOutputAmount = Math.max(1, fluidOutputAmount);
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public SizedIngredient getItemInput() {
        return this.itemInput;
    }

    public net.minecraft.world.item.crafting.Ingredient getIngredient() {
        return this.itemInput.ingredient();
    }

    public FluidStack getFluidInput() {
        return this.fluidInput.copy();
    }

    public FluidStack getFluidOutput() {
        return this.createFluidOutputStack();
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
        if (!this.itemInput.test(input.getItem(0))) {
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

    private FluidResult getFluidResult() {
        return new FluidResult(this.fluidOutputId, this.fluidOutputAmount);
    }

    private FluidStack createFluidOutputStack() {
        var fluid = BuiltInRegistries.FLUID.get(this.fluidOutputId);
        if (fluid == null || fluid == Fluids.EMPTY) {
            return FluidStack.EMPTY;
        }

        return new FluidStack(fluid, this.fluidOutputAmount);
    }

    private record FluidResult(ResourceLocation id, int amount) {
    }
}