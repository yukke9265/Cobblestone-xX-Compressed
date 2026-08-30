package com.yukke9265.cobblestone_xx_compressed.recipe;

import org.jetbrains.annotations.NotNull;

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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * シールドプロジェクター用レシピ。
 * 入力アイテムは無く、CP を消費して完了時にシールド量を加算します。
 */
public class ShieldProjectorRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<ShieldProjectorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.LONG.fieldOf("total_cobblestone_power").forGetter(ShieldProjectorRecipe::getTotalCobblestonePower),
        Codec.LONG.optionalFieldOf("cobblestone_power_per_tick", 10L).forGetter(ShieldProjectorRecipe::getCobblestonePowerPerTick),
        Codec.LONG.fieldOf("shield_output").forGetter(ShieldProjectorRecipe::getShieldOutput)
    ).apply(instance, ShieldProjectorRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldProjectorRecipe> STREAM_CODEC = StreamCodec.of(
        (buf, recipe) -> {
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
            buf.writeLong(recipe.getShieldOutput());
        },
        buf -> new ShieldProjectorRecipe(buf.readLong(), buf.readLong(), buf.readLong())
    );

    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;
    private final long shieldOutput;

    public ShieldProjectorRecipe(long totalCobblestonePower, long cobblestonePowerPerTick, long shieldOutput) {
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
        this.shieldOutput = Math.max(1L, shieldOutput);
    }

    public long getTotalCobblestonePower() {
        return this.totalCobblestonePower;
    }

    public long getCobblestonePowerPerTick() {
        return this.cobblestonePowerPerTick;
    }

    public long getShieldOutput() {
        return this.shieldOutput;
    }

    public int getProcessingTime() {
        long processingTime = (this.totalCobblestonePower + this.cobblestonePowerPerTick - 1L) / this.cobblestonePowerPerTick;
        return Math.max(1, (int) Math.min(Integer.MAX_VALUE, processingTime));
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        // 入力アイテムは使わないため、常に一致扱いにします。
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider registries) {
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
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return ModRecipeSerializers.SHIELD_PROJECTOR.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipeTypes.SHIELD_PROJECTOR.get();
    }
}
