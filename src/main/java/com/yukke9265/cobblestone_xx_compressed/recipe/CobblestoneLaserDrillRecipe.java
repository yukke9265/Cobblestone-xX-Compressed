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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class CobblestoneLaserDrillRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<CobblestoneLaserDrillRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(CobblestoneLaserDrillRecipe::getIngredient),
        ItemStack.CODEC.fieldOf("result_1").forGetter(CobblestoneLaserDrillRecipe::getFirstResult),
        Codec.floatRange(0.0F, 1.0F).optionalFieldOf("result_1_chance", 1.0F).forGetter(CobblestoneLaserDrillRecipe::getFirstResultChance),
        ItemStack.CODEC.fieldOf("result_2").forGetter(CobblestoneLaserDrillRecipe::getSecondResult),
        Codec.floatRange(0.0F, 1.0F).optionalFieldOf("result_2_chance", 1.0F).forGetter(CobblestoneLaserDrillRecipe::getSecondResultChance),
        Codec.INT.optionalFieldOf("total_cobblestone_power", 200).forGetter(CobblestoneLaserDrillRecipe::getTotalCobblestonePower),
        Codec.INT.optionalFieldOf("cobblestone_power_per_tick", 1).forGetter(CobblestoneLaserDrillRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (ingredient, firstResult, firstChance, secondResult, secondChance, totalPower, powerPerTick) ->
        new CobblestoneLaserDrillRecipe(
            ingredient,
            firstResult,
            firstChance.floatValue(),
            secondResult,
            secondChance.floatValue(),
            totalPower.intValue(),
            powerPerTick.intValue()
        )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneLaserDrillRecipe> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CobblestoneLaserDrillRecipe decode(RegistryFriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ItemStack firstResult = ItemStack.STREAM_CODEC.decode(buf);
            float firstChance = ByteBufCodecs.FLOAT.decode(buf);
            ItemStack secondResult = ItemStack.STREAM_CODEC.decode(buf);
            float secondChance = ByteBufCodecs.FLOAT.decode(buf);
            int totalPower = ByteBufCodecs.INT.decode(buf);
            int powerPerTick = ByteBufCodecs.INT.decode(buf);
            return new CobblestoneLaserDrillRecipe(
                ingredient,
                firstResult,
                firstChance,
                secondResult,
                secondChance,
                totalPower,
                powerPerTick
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, CobblestoneLaserDrillRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getFirstResult());
            ByteBufCodecs.FLOAT.encode(buf, recipe.getFirstResultChance());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getSecondResult());
            ByteBufCodecs.FLOAT.encode(buf, recipe.getSecondResultChance());
            ByteBufCodecs.INT.encode(buf, recipe.getTotalCobblestonePower());
            ByteBufCodecs.INT.encode(buf, recipe.getCobblestonePowerPerTick());
        }
    };

    private final Ingredient ingredient;
    private final ItemStack firstResult;
    private final float firstResultChance;
    private final ItemStack secondResult;
    private final float secondResultChance;
    private final int totalCobblestonePower;
    private final int cobblestonePowerPerTick;

    public CobblestoneLaserDrillRecipe(
        Ingredient ingredient,
        ItemStack firstResult,
        float firstResultChance,
        ItemStack secondResult,
        float secondResultChance,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        this.ingredient = ingredient;
        this.firstResult = firstResult;
        this.firstResultChance = firstResultChance;
        this.secondResult = secondResult;
        this.secondResultChance = secondResultChance;
        this.cobblestonePowerPerTick = Math.max(1, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getFirstResult() {
        return this.firstResult;
    }

    public float getFirstResultChance() {
        return this.firstResultChance;
    }

    public ItemStack getSecondResult() {
        return this.secondResult;
    }

    public float getSecondResultChance() {
        return this.secondResultChance;
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

    public ItemStack rollFirstResult(RandomSource random) {
        if (this.firstResult.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (random.nextFloat() > this.firstResultChance) {
            return ItemStack.EMPTY;
        }

        return this.firstResult.copy();
    }

    public ItemStack rollSecondResult(RandomSource random) {
        if (this.secondResult.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (random.nextFloat() > this.secondResultChance) {
            return ItemStack.EMPTY;
        }

        return this.secondResult.copy();
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return this.ingredient.test(input.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider registries) {
        return this.firstResult.copy();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return this.firstResult.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_LASER_DRILL.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_LASER_DRILL.get();
    }
}
