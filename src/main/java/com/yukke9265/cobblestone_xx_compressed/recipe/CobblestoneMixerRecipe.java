package com.yukke9265.cobblestone_xx_compressed.recipe;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeSerializers;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

@SuppressWarnings("null")
public class CobblestoneMixerRecipe implements Recipe<DoubleItemRecipeInput> {
    public static final MapCodec<CobblestoneMixerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        SizedIngredient.FLAT_CODEC.fieldOf("item_input_1").forGetter(CobblestoneMixerRecipe::getFirstInput),
        SizedIngredient.FLAT_CODEC.fieldOf("item_input_2").forGetter(CobblestoneMixerRecipe::getSecondInput),
        ItemStack.CODEC.fieldOf("result").forGetter(CobblestoneMixerRecipe::getResult),
        Codec.LONG.fieldOf("total_cobblestone_power").forGetter(CobblestoneMixerRecipe::getTotalCobblestonePower),
        Codec.LONG.optionalFieldOf("cobblestone_power_per_tick", 1L).forGetter(CobblestoneMixerRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (firstInput, secondInput, result, totalCobblestonePower, cobblestonePowerPerTick) -> new CobblestoneMixerRecipe(
        firstInput,
        secondInput,
        result,
        totalCobblestonePower.longValue(),
        cobblestonePowerPerTick.longValue()
    )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneMixerRecipe> STREAM_CODEC = StreamCodec.of(
        (buf, recipe) -> {
            SizedIngredient.STREAM_CODEC.encode(buf, recipe.getFirstInput());
            SizedIngredient.STREAM_CODEC.encode(buf, recipe.getSecondInput());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getResult());
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
        },
        buf -> new CobblestoneMixerRecipe(
            SizedIngredient.STREAM_CODEC.decode(buf),
            SizedIngredient.STREAM_CODEC.decode(buf),
            ItemStack.STREAM_CODEC.decode(buf),
            buf.readLong(),
            buf.readLong()
        )
    );

    private final SizedIngredient firstInput;
    private final SizedIngredient secondInput;
    private final ItemStack result;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneMixerRecipe(
        SizedIngredient firstInput,
        SizedIngredient secondInput,
        ItemStack result,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.firstInput = firstInput;
        this.secondInput = secondInput;
        this.result = result.copy();
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public SizedIngredient getFirstInput() {
        return this.firstInput;
    }

    public SizedIngredient getSecondInput() {
        return this.secondInput;
    }

    public ItemStack getResult() {
        return this.result.copy();
    }

    public long getTotalCobblestonePower() {
        return this.totalCobblestonePower;
    }

    public int getProcessingTime() {
        long processingTime = (this.totalCobblestonePower + this.cobblestonePowerPerTick - 1L) / this.cobblestonePowerPerTick;
        return Math.max(1, (int) Math.min(Integer.MAX_VALUE, processingTime));
    }

    public long getCobblestonePowerPerTick() {
        return this.cobblestonePowerPerTick;
    }

    @Override
    public boolean matches(@Nonnull DoubleItemRecipeInput input, @Nonnull Level level) {
        return this.firstInput.test(input.getItem(0))
            && this.secondInput.test(input.getItem(1));
    }

    @Override
    public @Nonnull ItemStack assemble(@Nonnull DoubleItemRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public @Nonnull ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<DoubleItemRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_MIXER.get();
    }

    @Override
    public RecipeType<? extends Recipe<DoubleItemRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_MIXER.get();
    }
}