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

public class CobblestoneMixerRecipe implements Recipe<DoubleItemRecipeInput> {
    public static final MapCodec<CobblestoneMixerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ItemStack.CODEC.fieldOf("item_input_1").forGetter(CobblestoneMixerRecipe::getFirstInput),
        ItemStack.CODEC.fieldOf("item_input_2").forGetter(CobblestoneMixerRecipe::getSecondInput),
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
            ItemStack.STREAM_CODEC.encode(buf, recipe.getFirstInput());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getSecondInput());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getResult());
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
        },
        buf -> new CobblestoneMixerRecipe(
            ItemStack.STREAM_CODEC.decode(buf),
            ItemStack.STREAM_CODEC.decode(buf),
            ItemStack.STREAM_CODEC.decode(buf),
            buf.readLong(),
            buf.readLong()
        )
    );

    private final ItemStack firstInput;
    private final ItemStack secondInput;
    private final ItemStack result;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneMixerRecipe(
        ItemStack firstInput,
        ItemStack secondInput,
        ItemStack result,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.firstInput = firstInput.copy();
        this.secondInput = secondInput.copy();
        this.result = result.copy();
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public ItemStack getFirstInput() {
        return this.firstInput.copy();
    }

    public ItemStack getSecondInput() {
        return this.secondInput.copy();
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
    public boolean matches(@NotNull DoubleItemRecipeInput input, @NotNull Level level) {
        return matchesInput(this.firstInput, input.getItem(0))
            && matchesInput(this.secondInput, input.getItem(1));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull DoubleItemRecipeInput input, @NotNull HolderLookup.Provider registries) {
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
    public RecipeSerializer<? extends Recipe<DoubleItemRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_MIXER.get();
    }

    @Override
    public RecipeType<? extends Recipe<DoubleItemRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_MIXER.get();
    }

    private static boolean matchesInput(ItemStack expected, ItemStack actual) {
        if (expected.isEmpty()) {
            return actual.isEmpty();
        }

        if (actual.isEmpty()) {
            return false;
        }

        if (!ItemStack.isSameItemSameComponents(expected, actual)) {
            return false;
        }

        return actual.getCount() >= expected.getCount();
    }
}