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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class CobblestoneLaserDrillRecipe implements Recipe<SingleRecipeInput> {
    // datagen 時に外部 mod の Item 実体が無くても JSON を出せるよう、
    // 結果は id + count で保持し、実行時に ItemStack へ解決します。
    private static final Codec<ItemResult> RESULT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("id").forGetter(ItemResult::id),
        Codec.INT.optionalFieldOf("count", 1).forGetter(ItemResult::count)
    ).apply(instance, ItemResult::new));

    public static final MapCodec<CobblestoneLaserDrillRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(CobblestoneLaserDrillRecipe::getIngredient),
        RESULT_CODEC.fieldOf("result_1").forGetter(CobblestoneLaserDrillRecipe::getFirstItemResult),
        Codec.floatRange(0.0F, 1.0F).optionalFieldOf("result_1_chance", 1.0F).forGetter(CobblestoneLaserDrillRecipe::getFirstResultChance),
        RESULT_CODEC.fieldOf("result_2").forGetter(CobblestoneLaserDrillRecipe::getSecondItemResult),
        Codec.floatRange(0.0F, 1.0F).optionalFieldOf("result_2_chance", 1.0F).forGetter(CobblestoneLaserDrillRecipe::getSecondResultChance),
        Codec.LONG.optionalFieldOf("total_cobblestone_power", 200L).forGetter(CobblestoneLaserDrillRecipe::getTotalCobblestonePower),
        Codec.LONG.optionalFieldOf("cobblestone_power_per_tick", 1L).forGetter(CobblestoneLaserDrillRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (ingredient, firstResult, firstChance, secondResult, secondChance, totalPower, powerPerTick) ->
        new CobblestoneLaserDrillRecipe(
            ingredient,
            firstResult.id(),
            firstResult.count(),
            firstChance.floatValue(),
            secondResult.id(),
            secondResult.count(),
            secondChance.floatValue(),
            totalPower.longValue(),
            powerPerTick.longValue()
        )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneLaserDrillRecipe> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CobblestoneLaserDrillRecipe decode(RegistryFriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ResourceLocation firstResultId = ResourceLocation.STREAM_CODEC.decode(buf);
            int firstResultCount = ByteBufCodecs.VAR_INT.decode(buf);
            float firstChance = ByteBufCodecs.FLOAT.decode(buf);
            ResourceLocation secondResultId = ResourceLocation.STREAM_CODEC.decode(buf);
            int secondResultCount = ByteBufCodecs.VAR_INT.decode(buf);
            float secondChance = ByteBufCodecs.FLOAT.decode(buf);
            long totalPower = buf.readLong();
            long powerPerTick = buf.readLong();
            return new CobblestoneLaserDrillRecipe(
                ingredient,
                firstResultId,
                firstResultCount,
                firstChance,
                secondResultId,
                secondResultCount,
                secondChance,
                totalPower,
                powerPerTick
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, CobblestoneLaserDrillRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient());
            ResourceLocation.STREAM_CODEC.encode(buf, recipe.firstResultId);
            ByteBufCodecs.VAR_INT.encode(buf, recipe.firstResultCount);
            ByteBufCodecs.FLOAT.encode(buf, recipe.getFirstResultChance());
            ResourceLocation.STREAM_CODEC.encode(buf, recipe.secondResultId);
            ByteBufCodecs.VAR_INT.encode(buf, recipe.secondResultCount);
            ByteBufCodecs.FLOAT.encode(buf, recipe.getSecondResultChance());
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
        }
    };

    private final Ingredient ingredient;
    private final ResourceLocation firstResultId;
    private final int firstResultCount;
    private final float firstResultChance;
    private final ResourceLocation secondResultId;
    private final int secondResultCount;
    private final float secondResultChance;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneLaserDrillRecipe(
        Ingredient ingredient,
        ItemStack firstResult,
        float firstResultChance,
        ItemStack secondResult,
        float secondResultChance,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this(
            ingredient,
            BuiltInRegistries.ITEM.getKey(firstResult.getItem()),
            Math.max(1, firstResult.getCount()),
            firstResultChance,
            BuiltInRegistries.ITEM.getKey(secondResult.getItem()),
            Math.max(1, secondResult.getCount()),
            secondResultChance,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );
    }

    public CobblestoneLaserDrillRecipe(
        Ingredient ingredient,
        ResourceLocation firstResultId,
        int firstResultCount,
        float firstResultChance,
        ResourceLocation secondResultId,
        int secondResultCount,
        float secondResultChance,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.ingredient = ingredient;
        this.firstResultId = firstResultId;
        this.firstResultCount = Math.max(1, firstResultCount);
        this.firstResultChance = firstResultChance;
        this.secondResultId = secondResultId;
        this.secondResultCount = Math.max(1, secondResultCount);
        this.secondResultChance = secondResultChance;
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getFirstResult() {
        return createResultStack(this.firstResultId, this.firstResultCount);
    }

    public float getFirstResultChance() {
        return this.firstResultChance;
    }

    public ItemStack getSecondResult() {
        return createResultStack(this.secondResultId, this.secondResultCount);
    }

    public float getSecondResultChance() {
        return this.secondResultChance;
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

    public ItemStack rollFirstResult(RandomSource random) {
        return rollResult(this.getFirstResult(), this.firstResultChance, random);
    }

    public ItemStack rollSecondResult(RandomSource random) {
        return rollResult(this.getSecondResult(), this.secondResultChance, random);
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return this.ingredient.test(input.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SingleRecipeInput input, @NotNull HolderLookup.Provider registries) {
        return this.getFirstResult();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return this.getFirstResult();
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

    private ItemResult getFirstItemResult() {
        return new ItemResult(this.firstResultId, this.firstResultCount);
    }

    private ItemResult getSecondItemResult() {
        return new ItemResult(this.secondResultId, this.secondResultCount);
    }

    private static ItemStack rollResult(ItemStack result, float chance, RandomSource random) {
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (random.nextFloat() > chance) {
            return ItemStack.EMPTY;
        }

        return result.copy();
    }

    private static ItemStack createResultStack(ResourceLocation itemId, int count) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item, count);
    }

    private record ItemResult(ResourceLocation id, int count) {
    }
}
