package com.yukke9265.cobblestone_xx_compressed.recipe;

import java.util.Optional;

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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

public class CobblestoneReactionChamberRecipe implements Recipe<ReactionChamberRecipeInput> {
    // datagen 時に外部 mod の Item 実体が無くても JSON を出せるよう、
    // 結果は id + count で保持し、実行時に ItemStack へ解決します。
    private static final Codec<ItemResult> RESULT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("id").forGetter(ItemResult::id),
        Codec.INT.optionalFieldOf("count", 1).forGetter(ItemResult::count)
    ).apply(instance, ItemResult::new));

    public static final MapCodec<CobblestoneReactionChamberRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        FluidStack.CODEC.fieldOf("fluid_input").forGetter(CobblestoneReactionChamberRecipe::getFluidInput),
        SizedIngredient.FLAT_CODEC.fieldOf("ingredient_1").forGetter(CobblestoneReactionChamberRecipe::getFirstInput),
        SizedIngredient.FLAT_CODEC.fieldOf("ingredient_2").forGetter(CobblestoneReactionChamberRecipe::getSecondInput),
        RESULT_CODEC.fieldOf("result").forGetter(CobblestoneReactionChamberRecipe::getItemResult),
        Codec.LONG.fieldOf("total_cobblestone_power").forGetter(CobblestoneReactionChamberRecipe::getTotalCobblestonePower),
        Codec.LONG.optionalFieldOf("cobblestone_power_per_tick", 1L).forGetter(CobblestoneReactionChamberRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (fluidInput, firstInput, secondInput, result, totalCobblestonePower, cobblestonePowerPerTick) -> new CobblestoneReactionChamberRecipe(
        fluidInput,
        firstInput,
        secondInput,
        result.id(),
        result.count(),
        totalCobblestonePower.longValue(),
        cobblestonePowerPerTick.longValue()
    )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneReactionChamberRecipe> STREAM_CODEC = StreamCodec.of(
        (buf, recipe) -> {
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFluidInput());
            SizedIngredient.STREAM_CODEC.encode(buf, recipe.getFirstInput());
            SizedIngredient.STREAM_CODEC.encode(buf, recipe.getSecondInput());
            ResourceLocation.STREAM_CODEC.encode(buf, recipe.resultId);
            ByteBufCodecs.VAR_INT.encode(buf, recipe.resultCount);
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
        },
        buf -> new CobblestoneReactionChamberRecipe(
            FluidStack.STREAM_CODEC.decode(buf),
            SizedIngredient.STREAM_CODEC.decode(buf),
            SizedIngredient.STREAM_CODEC.decode(buf),
            ResourceLocation.STREAM_CODEC.decode(buf),
            ByteBufCodecs.VAR_INT.decode(buf),
            buf.readLong(),
            buf.readLong()
        )
    );

    private final FluidStack fluidInput;
    private final SizedIngredient firstInput;
    private final SizedIngredient secondInput;
    private final ResourceLocation resultId;
    private final int resultCount;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneReactionChamberRecipe(
        FluidStack fluidInput,
        Ingredient firstIngredient,
        Ingredient secondIngredient,
        ItemStack result,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this(
            fluidInput,
            new SizedIngredient(firstIngredient, 1),
            new SizedIngredient(secondIngredient, 1),
            result,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );
    }

    public CobblestoneReactionChamberRecipe(
        FluidStack fluidInput,
        SizedIngredient firstInput,
        SizedIngredient secondInput,
        ItemStack result,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this(
            fluidInput,
            firstInput,
            secondInput,
            BuiltInRegistries.ITEM.getKey(result.getItem()),
            Math.max(1, result.getCount()),
            totalCobblestonePower,
            cobblestonePowerPerTick
        );
    }

    public CobblestoneReactionChamberRecipe(
        FluidStack fluidInput,
        SizedIngredient firstInput,
        SizedIngredient secondInput,
        ResourceLocation resultId,
        int resultCount,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.fluidInput = fluidInput.copy();
        this.firstInput = firstInput;
        this.secondInput = secondInput;
        this.resultId = resultId;
        this.resultCount = Math.max(1, resultCount);
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public FluidStack getFluidInput() {
        return this.fluidInput.copy();
    }

    public SizedIngredient getFirstInput() {
        return this.firstInput;
    }

    public SizedIngredient getSecondInput() {
        return this.secondInput;
    }

    public Ingredient getFirstIngredient() {
        return this.firstInput.ingredient();
    }

    public Ingredient getSecondIngredient() {
        return this.secondInput.ingredient();
    }

    public ItemStack getResult() {
        return this.createResultStack();
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

    public Optional<int[]> findMatchingItemSlots(ReactionChamberRecipeInput input) {
        ItemStack firstSlot = input.getItem(0);
        ItemStack secondSlot = input.getItem(1);

        if (this.firstInput.test(firstSlot) && this.secondInput.test(secondSlot)) {
            return Optional.of(new int[] { 0, 1 });
        }

        if (this.firstInput.test(secondSlot) && this.secondInput.test(firstSlot)) {
            return Optional.of(new int[] { 1, 0 });
        }

        return Optional.empty();
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

        return this.findMatchingItemSlots(input).isPresent();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull ReactionChamberRecipeInput input, @NotNull HolderLookup.Provider registries) {
        return this.createResultStack();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return this.createResultStack();
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

    private ItemResult getItemResult() {
        return new ItemResult(this.resultId, this.resultCount);
    }

    private ItemStack createResultStack() {
        Item item = BuiltInRegistries.ITEM.get(this.resultId);
        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item, this.resultCount);
    }

    private record ItemResult(ResourceLocation id, int count) {
    }
}
