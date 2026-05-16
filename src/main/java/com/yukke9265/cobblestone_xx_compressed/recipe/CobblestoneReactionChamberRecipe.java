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

public class CobblestoneReactionChamberRecipe implements Recipe<ReactionChamberRecipeInput> {
    public static final MapCodec<CobblestoneReactionChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidStack.CODEC.fieldOf("fluid_input").forGetter(CobblestoneReactionChamberRecipe::getFluidInput),
        Ingredient.CODEC.fieldOf("ingredient_1").forGetter(CobblestoneReactionChamberRecipe::getFirstIngredient),
        Ingredient.CODEC.fieldOf("ingredient_2").forGetter(CobblestoneReactionChamberRecipe::getSecondIngredient),
        ItemStack.CODEC.fieldOf("result").forGetter(CobblestoneReactionChamberRecipe::getResult),
        Codec.INT.fieldOf("total_cobblestone_power").forGetter(CobblestoneReactionChamberRecipe::getTotalCobblestonePower),
        Codec.INT.optionalFieldOf("cobblestone_power_per_tick", 1).forGetter(CobblestoneReactionChamberRecipe::getCobblestonePowerPerTick)
    ).apply(instance, CobblestoneReactionChamberRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneReactionChamberRecipe> STREAM_CODEC = StreamCodec.composite(
        FluidStack.STREAM_CODEC,
        CobblestoneReactionChamberRecipe::getFluidInput,
        Ingredient.CONTENTS_STREAM_CODEC,
        CobblestoneReactionChamberRecipe::getFirstIngredient,
        Ingredient.CONTENTS_STREAM_CODEC,
        CobblestoneReactionChamberRecipe::getSecondIngredient,
        ItemStack.STREAM_CODEC,
        CobblestoneReactionChamberRecipe::getResult,
        ByteBufCodecs.INT,
        CobblestoneReactionChamberRecipe::getTotalCobblestonePower,
        ByteBufCodecs.INT,
        CobblestoneReactionChamberRecipe::getCobblestonePowerPerTick,
        CobblestoneReactionChamberRecipe::new
    );

    private final FluidStack fluidInput;
    private final Ingredient firstIngredient;
    private final Ingredient secondIngredient;
    private final ItemStack result;
    private final int totalCobblestonePower;
    private final int cobblestonePowerPerTick;

    public CobblestoneReactionChamberRecipe(
        FluidStack fluidInput,
        Ingredient firstIngredient,
        Ingredient secondIngredient,
        ItemStack result,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        this.fluidInput = fluidInput.copy();
        this.firstIngredient = firstIngredient;
        this.secondIngredient = secondIngredient;
        this.result = result;
        this.cobblestonePowerPerTick = Math.max(1, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public FluidStack getFluidInput() {
        return this.fluidInput.copy();
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

    public int getCobblestonePowerPerTick() {
        return this.cobblestonePowerPerTick;
    }

    public int getProcessingTime() {
        return Math.max(1, (this.totalCobblestonePower + this.cobblestonePowerPerTick - 1) / this.cobblestonePowerPerTick);
    }

    @Override
    public boolean matches(@NotNull ReactionChamberRecipeInput input, @NotNull Level level) {
        FluidStack fluidStack = input.getFluidInput();
        if (fluidStack.isEmpty()) {
            return false;
        }

        if (!FluidStack.isSameFluidSameComponents(fluidStack, this.fluidInput)) {
            return false;
        }

        if (fluidStack.getAmount() < this.fluidInput.getAmount()) {
            return false;
        }

        ItemStack firstItem = input.getItem(0);
        ItemStack secondItem = input.getItem(1);
        boolean normalOrder = this.firstIngredient.test(firstItem) && this.secondIngredient.test(secondItem);
        boolean reversedOrder = this.firstIngredient.test(secondItem) && this.secondIngredient.test(firstItem);
        return normalOrder || reversedOrder;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull ReactionChamberRecipeInput input, @NotNull HolderLookup.Provider registries) {
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
    public RecipeSerializer<? extends Recipe<ReactionChamberRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_REACTION_CHAMBER.get();
    }

    @Override
    public RecipeType<? extends Recipe<ReactionChamberRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_REACTION_CHAMBER.get();
    }
}