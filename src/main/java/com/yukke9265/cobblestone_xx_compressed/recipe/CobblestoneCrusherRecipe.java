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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class CobblestoneCrusherRecipe implements Recipe<SingleRecipeInput> {
    // datagen 時に外部 mod の Item 実体が無くても JSON を出せるよう、
    // 結果は id + count で保持し、実行時に ItemStack へ解決します。
    private static final Codec<CrushResult> RESULT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("id").forGetter(CrushResult::id),
        Codec.INT.optionalFieldOf("count", 1).forGetter(CrushResult::count)
    ).apply(instance, CrushResult::new));

    public static final MapCodec<CobblestoneCrusherRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(CobblestoneCrusherRecipe::getIngredient),
        RESULT_CODEC.fieldOf("result").forGetter(CobblestoneCrusherRecipe::getCrushResult),
        Codec.LONG.fieldOf("total_cobblestone_power").forGetter(CobblestoneCrusherRecipe::getTotalCobblestonePower),
        Codec.LONG.optionalFieldOf("cobblestone_power_per_tick", 1L).forGetter(CobblestoneCrusherRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (ingredient, result, totalCobblestonePower, cobblestonePowerPerTick) -> new CobblestoneCrusherRecipe(
        ingredient,
        result.id(),
        result.count(),
        totalCobblestonePower.longValue(),
        cobblestonePowerPerTick.longValue()
    )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneCrusherRecipe> STREAM_CODEC = StreamCodec.of(
        (buf, recipe) -> {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient());
            ResourceLocation.STREAM_CODEC.encode(buf, recipe.resultId);
            ByteBufCodecs.VAR_INT.encode(buf, recipe.resultCount);
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
        },
        buf -> new CobblestoneCrusherRecipe(
            Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
            ResourceLocation.STREAM_CODEC.decode(buf),
            ByteBufCodecs.VAR_INT.decode(buf),
            buf.readLong(),
            buf.readLong()
        )
    );

    private final Ingredient ingredient;
    private final ResourceLocation resultId;
    private final int resultCount;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneCrusherRecipe(Ingredient ingredient, ItemStack result, long totalCobblestonePower, long cobblestonePowerPerTick) {
        this(
            ingredient,
            BuiltInRegistries.ITEM.getKey(result.getItem()),
            Math.max(1, result.getCount()),
            totalCobblestonePower,
            cobblestonePowerPerTick
        );
    }

    public CobblestoneCrusherRecipe(
        Ingredient ingredient,
        ResourceLocation resultId,
        int resultCount,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.ingredient = ingredient;
        this.resultId = resultId;
        this.resultCount = Math.max(1, resultCount);
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getResult() {
        Item item = BuiltInRegistries.ITEM.get(this.resultId);
        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item, this.resultCount);
    }

    private CrushResult getCrushResult() {
        return new CrushResult(this.resultId, this.resultCount);
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
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return this.ingredient.test(input.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider registries) {
        return this.getResult();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return this.getResult();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_CRUSHER.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_CRUSHER.get();
    }

    private record CrushResult(ResourceLocation id, int count) {
    }
}
