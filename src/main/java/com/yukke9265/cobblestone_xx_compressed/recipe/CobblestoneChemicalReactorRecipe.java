package com.yukke9265.cobblestone_xx_compressed.recipe;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeSerializers;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class CobblestoneChemicalReactorRecipe implements Recipe<ChemicalReactorRecipeInput> {
    public static final MapCodec<CobblestoneChemicalReactorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ItemStack.CODEC.optionalFieldOf("item_input_1", ItemStack.EMPTY).forGetter(CobblestoneChemicalReactorRecipe::getFirstItemInput),
        ItemStack.CODEC.optionalFieldOf("item_input_2", ItemStack.EMPTY).forGetter(CobblestoneChemicalReactorRecipe::getSecondItemInput),
        FluidStack.CODEC.optionalFieldOf("fluid_input_1", FluidStack.EMPTY).forGetter(CobblestoneChemicalReactorRecipe::getFirstFluidInput),
        FluidStack.CODEC.optionalFieldOf("fluid_input_2", FluidStack.EMPTY).forGetter(CobblestoneChemicalReactorRecipe::getSecondFluidInput),
        ItemStack.CODEC.optionalFieldOf("result_item_1", ItemStack.EMPTY).forGetter(CobblestoneChemicalReactorRecipe::getFirstResultItem),
        ItemStack.CODEC.optionalFieldOf("result_item_2", ItemStack.EMPTY).forGetter(CobblestoneChemicalReactorRecipe::getSecondResultItem),
        FluidStack.CODEC.optionalFieldOf("result_fluid_1", FluidStack.EMPTY).forGetter(CobblestoneChemicalReactorRecipe::getFirstResultFluid),
        FluidStack.CODEC.optionalFieldOf("result_fluid_2", FluidStack.EMPTY).forGetter(CobblestoneChemicalReactorRecipe::getSecondResultFluid),
        Codec.INT.fieldOf("total_cobblestone_power").forGetter(CobblestoneChemicalReactorRecipe::getTotalCobblestonePower),
        Codec.INT.optionalFieldOf("cobblestone_power_per_tick", 1).forGetter(CobblestoneChemicalReactorRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (firstItemInput, secondItemInput, firstFluidInput, secondFluidInput, firstResultItem, secondResultItem, firstResultFluid, secondResultFluid, totalPower, powerPerTick) ->
        new CobblestoneChemicalReactorRecipe(
            firstItemInput,
            secondItemInput,
            firstFluidInput,
            secondFluidInput,
            firstResultItem,
            secondResultItem,
            firstResultFluid,
            secondResultFluid,
            totalPower.intValue(),
            powerPerTick.intValue()
        )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneChemicalReactorRecipe> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CobblestoneChemicalReactorRecipe decode(@Nonnull RegistryFriendlyByteBuf buf) {
            ItemStack firstItemInput = decodeOptionalItemStack(buf);
            ItemStack secondItemInput = decodeOptionalItemStack(buf);
            FluidStack firstFluidInput = FluidStack.STREAM_CODEC.decode(buf);
            FluidStack secondFluidInput = FluidStack.STREAM_CODEC.decode(buf);
            ItemStack firstResultItem = decodeOptionalItemStack(buf);
            ItemStack secondResultItem = decodeOptionalItemStack(buf);
            FluidStack firstResultFluid = FluidStack.STREAM_CODEC.decode(buf);
            FluidStack secondResultFluid = FluidStack.STREAM_CODEC.decode(buf);
            int totalPower = ByteBufCodecs.INT.decode(buf);
            int powerPerTick = ByteBufCodecs.INT.decode(buf);
            return new CobblestoneChemicalReactorRecipe(
                firstItemInput,
                secondItemInput,
                firstFluidInput,
                secondFluidInput,
                firstResultItem,
                secondResultItem,
                firstResultFluid,
                secondResultFluid,
                totalPower,
                powerPerTick
            );
        }

        @Override
        public void encode(@Nonnull RegistryFriendlyByteBuf buf, @Nonnull CobblestoneChemicalReactorRecipe recipe) {
            encodeOptionalItemStack(buf, recipe.getFirstItemInput());
            encodeOptionalItemStack(buf, recipe.getSecondItemInput());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFirstFluidInput());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getSecondFluidInput());
            encodeOptionalItemStack(buf, recipe.getFirstResultItem());
            encodeOptionalItemStack(buf, recipe.getSecondResultItem());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFirstResultFluid());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getSecondResultFluid());
            ByteBufCodecs.INT.encode(buf, recipe.getTotalCobblestonePower());
            ByteBufCodecs.INT.encode(buf, recipe.getCobblestonePowerPerTick());
        }

        private static ItemStack decodeOptionalItemStack(RegistryFriendlyByteBuf buf) {
            if (!buf.readBoolean()) {
                return ItemStack.EMPTY;
            }

            return ItemStack.STREAM_CODEC.decode(buf);
        }

        private static void encodeOptionalItemStack(RegistryFriendlyByteBuf buf, ItemStack stack) {
            boolean hasStack = !stack.isEmpty();
            buf.writeBoolean(hasStack);
            if (hasStack) {
                ItemStack.STREAM_CODEC.encode(buf, stack);
            }
        }
    };

    private final ItemStack firstItemInput;
    private final ItemStack secondItemInput;
    private final FluidStack firstFluidInput;
    private final FluidStack secondFluidInput;
    private final ItemStack firstResultItem;
    private final ItemStack secondResultItem;
    private final FluidStack firstResultFluid;
    private final FluidStack secondResultFluid;
    private final int totalCobblestonePower;
    private final int cobblestonePowerPerTick;

    public CobblestoneChemicalReactorRecipe(
        ItemStack firstItemInput,
        ItemStack secondItemInput,
        FluidStack firstFluidInput,
        FluidStack secondFluidInput,
        ItemStack firstResultItem,
        ItemStack secondResultItem,
        FluidStack firstResultFluid,
        FluidStack secondResultFluid,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        this.firstItemInput = firstItemInput.copy();
        this.secondItemInput = secondItemInput.copy();
        this.firstFluidInput = firstFluidInput.copy();
        this.secondFluidInput = secondFluidInput.copy();
        this.firstResultItem = firstResultItem.copy();
        this.secondResultItem = secondResultItem.copy();
        this.firstResultFluid = firstResultFluid.copy();
        this.secondResultFluid = secondResultFluid.copy();
        this.cobblestonePowerPerTick = Math.max(1, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public ItemStack getFirstItemInput() {
        return this.firstItemInput.copy();
    }

    public ItemStack getSecondItemInput() {
        return this.secondItemInput.copy();
    }

    public FluidStack getFirstFluidInput() {
        return this.firstFluidInput.copy();
    }

    public FluidStack getSecondFluidInput() {
        return this.secondFluidInput.copy();
    }

    public ItemStack getFirstResultItem() {
        return this.firstResultItem.copy();
    }

    public ItemStack getSecondResultItem() {
        return this.secondResultItem.copy();
    }

    public FluidStack getFirstResultFluid() {
        return this.firstResultFluid.copy();
    }

    public FluidStack getSecondResultFluid() {
        return this.secondResultFluid.copy();
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

    public boolean hasFirstItemInput() {
        return !this.firstItemInput.isEmpty();
    }

    public boolean hasSecondItemInput() {
        return !this.secondItemInput.isEmpty();
    }

    public boolean hasFirstFluidInput() {
        return !this.firstFluidInput.isEmpty();
    }

    public boolean hasSecondFluidInput() {
        return !this.secondFluidInput.isEmpty();
    }

    public boolean hasFirstItemOutput() {
        return !this.firstResultItem.isEmpty();
    }

    public boolean hasSecondItemOutput() {
        return !this.secondResultItem.isEmpty();
    }

    public boolean hasFirstFluidOutput() {
        return !this.firstResultFluid.isEmpty();
    }

    public boolean hasSecondFluidOutput() {
        return !this.secondResultFluid.isEmpty();
    }

    @Override
    public boolean matches(@Nonnull ChemicalReactorRecipeInput input, @Nonnull Level level) {
        if (!this.hasAnyInput()) {
            return false;
        }

        if (!this.matchesItem(this.firstItemInput, input.getItem(0))) {
            return false;
        }

        if (!this.matchesItem(this.secondItemInput, input.getItem(1))) {
            return false;
        }

        if (!this.matchesFluid(this.firstFluidInput, input.getFirstFluidInput())) {
            return false;
        }

        return this.matchesFluid(this.secondFluidInput, input.getSecondFluidInput());
    }

    @Override
    public @Nonnull ItemStack assemble(@Nonnull ChemicalReactorRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        if (!this.firstResultItem.isEmpty()) {
            return this.firstResultItem.copy();
        }

        if (!this.secondResultItem.isEmpty()) {
            return this.secondResultItem.copy();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public @Nonnull ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        if (!this.firstResultItem.isEmpty()) {
            return this.firstResultItem.copy();
        }

        if (!this.secondResultItem.isEmpty()) {
            return this.secondResultItem.copy();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<ChemicalReactorRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_CHEMICAL_REACTOR.get();
    }

    @Override
    public RecipeType<? extends Recipe<ChemicalReactorRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_CHEMICAL_REACTOR.get();
    }

    private boolean hasAnyInput() {
        return this.hasFirstItemInput()
            || this.hasSecondItemInput()
            || this.hasFirstFluidInput()
            || this.hasSecondFluidInput();
    }

    private boolean matchesItem(ItemStack expected, ItemStack actual) {
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

    private boolean matchesFluid(FluidStack expected, FluidStack actual) {
        if (expected.isEmpty()) {
            return actual.isEmpty();
        }

        if (actual.isEmpty()) {
            return false;
        }

        if (!FluidStack.isSameFluidSameComponents(expected, actual)) {
            return false;
        }

        return actual.getAmount() >= expected.getAmount();
    }
}