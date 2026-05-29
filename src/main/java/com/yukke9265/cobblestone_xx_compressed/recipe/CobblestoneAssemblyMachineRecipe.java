package com.yukke9265.cobblestone_xx_compressed.recipe;

import java.util.Optional;

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
import net.neoforged.neoforge.fluids.FluidStack;

@SuppressWarnings("null")
public class CobblestoneAssemblyMachineRecipe implements Recipe<AssemblyMachineRecipeInput> {
    public static final MapCodec<CobblestoneAssemblyMachineRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ItemStack.CODEC.optionalFieldOf("item_input_1", ItemStack.EMPTY).forGetter(CobblestoneAssemblyMachineRecipe::getFirstItemInput),
        ItemStack.CODEC.optionalFieldOf("item_input_2", ItemStack.EMPTY).forGetter(CobblestoneAssemblyMachineRecipe::getSecondItemInput),
        ItemStack.CODEC.optionalFieldOf("item_input_3", ItemStack.EMPTY).forGetter(CobblestoneAssemblyMachineRecipe::getThirdItemInput),
        ItemStack.CODEC.optionalFieldOf("item_input_4", ItemStack.EMPTY).forGetter(CobblestoneAssemblyMachineRecipe::getFourthItemInput),
        ItemStack.CODEC.optionalFieldOf("item_input_5", ItemStack.EMPTY).forGetter(CobblestoneAssemblyMachineRecipe::getFifthItemInput),
        ItemStack.CODEC.optionalFieldOf("item_input_6", ItemStack.EMPTY).forGetter(CobblestoneAssemblyMachineRecipe::getSixthItemInput),
        FluidStack.CODEC.optionalFieldOf("fluid_input", FluidStack.EMPTY).forGetter(CobblestoneAssemblyMachineRecipe::getFluidInput),
        ItemStack.CODEC.fieldOf("result_item").forGetter(CobblestoneAssemblyMachineRecipe::getResultItemStack),
        Codec.LONG.fieldOf("total_cobblestone_power").forGetter(CobblestoneAssemblyMachineRecipe::getTotalCobblestonePower),
        Codec.LONG.optionalFieldOf("cobblestone_power_per_tick", 1L).forGetter(CobblestoneAssemblyMachineRecipe::getCobblestonePowerPerTick)
    ).apply(instance, (firstItemInput, secondItemInput, thirdItemInput, fourthItemInput, fifthItemInput, sixthItemInput, fluidInput, resultItem, totalPower, powerPerTick) ->
        new CobblestoneAssemblyMachineRecipe(
            firstItemInput,
            secondItemInput,
            thirdItemInput,
            fourthItemInput,
            fifthItemInput,
            sixthItemInput,
            fluidInput,
            resultItem,
            totalPower.longValue(),
            powerPerTick.longValue()
        )));

    public static final StreamCodec<RegistryFriendlyByteBuf, CobblestoneAssemblyMachineRecipe> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public CobblestoneAssemblyMachineRecipe decode(@Nonnull RegistryFriendlyByteBuf buf) {
            ItemStack firstItemInput = decodeOptionalItemStack(buf);
            ItemStack secondItemInput = decodeOptionalItemStack(buf);
            ItemStack thirdItemInput = decodeOptionalItemStack(buf);
            ItemStack fourthItemInput = decodeOptionalItemStack(buf);
            ItemStack fifthItemInput = decodeOptionalItemStack(buf);
            ItemStack sixthItemInput = decodeOptionalItemStack(buf);
            FluidStack fluidInput = FluidStack.STREAM_CODEC.decode(buf);
            ItemStack resultItem = ItemStack.STREAM_CODEC.decode(buf);
            long totalPower = buf.readLong();
            long powerPerTick = buf.readLong();
            return new CobblestoneAssemblyMachineRecipe(
                firstItemInput,
                secondItemInput,
                thirdItemInput,
                fourthItemInput,
                fifthItemInput,
                sixthItemInput,
                fluidInput,
                resultItem,
                totalPower,
                powerPerTick
            );
        }

        @Override
        public void encode(@Nonnull RegistryFriendlyByteBuf buf, @Nonnull CobblestoneAssemblyMachineRecipe recipe) {
            encodeOptionalItemStack(buf, recipe.getFirstItemInput());
            encodeOptionalItemStack(buf, recipe.getSecondItemInput());
            encodeOptionalItemStack(buf, recipe.getThirdItemInput());
            encodeOptionalItemStack(buf, recipe.getFourthItemInput());
            encodeOptionalItemStack(buf, recipe.getFifthItemInput());
            encodeOptionalItemStack(buf, recipe.getSixthItemInput());
            FluidStack.STREAM_CODEC.encode(buf, recipe.getFluidInput());
            ItemStack.STREAM_CODEC.encode(buf, recipe.getResultItemStack());
            buf.writeLong(recipe.getTotalCobblestonePower());
            buf.writeLong(recipe.getCobblestonePowerPerTick());
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
    private final ItemStack thirdItemInput;
    private final ItemStack fourthItemInput;
    private final ItemStack fifthItemInput;
    private final ItemStack sixthItemInput;
    private final FluidStack fluidInput;
    private final ItemStack resultItem;
    private final long totalCobblestonePower;
    private final long cobblestonePowerPerTick;

    public CobblestoneAssemblyMachineRecipe(
        ItemStack firstItemInput,
        ItemStack secondItemInput,
        ItemStack thirdItemInput,
        ItemStack fourthItemInput,
        ItemStack fifthItemInput,
        ItemStack sixthItemInput,
        FluidStack fluidInput,
        ItemStack resultItem,
        long totalCobblestonePower,
        long cobblestonePowerPerTick
    ) {
        this.firstItemInput = firstItemInput.copy();
        this.secondItemInput = secondItemInput.copy();
        this.thirdItemInput = thirdItemInput.copy();
        this.fourthItemInput = fourthItemInput.copy();
        this.fifthItemInput = fifthItemInput.copy();
        this.sixthItemInput = sixthItemInput.copy();
        this.fluidInput = fluidInput.copy();
        this.resultItem = resultItem.copy();
        this.cobblestonePowerPerTick = Math.max(1L, cobblestonePowerPerTick);
        this.totalCobblestonePower = Math.max(this.cobblestonePowerPerTick, totalCobblestonePower);
    }

    public ItemStack getFirstItemInput() {
        return this.firstItemInput.copy();
    }

    public ItemStack getSecondItemInput() {
        return this.secondItemInput.copy();
    }

    public ItemStack getThirdItemInput() {
        return this.thirdItemInput.copy();
    }

    public ItemStack getFourthItemInput() {
        return this.fourthItemInput.copy();
    }

    public ItemStack getFifthItemInput() {
        return this.fifthItemInput.copy();
    }

    public ItemStack getSixthItemInput() {
        return this.sixthItemInput.copy();
    }

    public FluidStack getFluidInput() {
        return this.fluidInput.copy();
    }

    public ItemStack getResultItemStack() {
        return this.resultItem.copy();
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

    public boolean hasFirstItemInput() {
        return !this.firstItemInput.isEmpty();
    }

    public boolean hasSecondItemInput() {
        return !this.secondItemInput.isEmpty();
    }

    public boolean hasThirdItemInput() {
        return !this.thirdItemInput.isEmpty();
    }

    public boolean hasFourthItemInput() {
        return !this.fourthItemInput.isEmpty();
    }

    public boolean hasFifthItemInput() {
        return !this.fifthItemInput.isEmpty();
    }

    public boolean hasSixthItemInput() {
        return !this.sixthItemInput.isEmpty();
    }

    public boolean hasFluidInput() {
        return !this.fluidInput.isEmpty();
    }

    @Override
    public boolean matches(@Nonnull AssemblyMachineRecipeInput input, @Nonnull Level level) {
        if (!this.hasAnyInput()) {
            return false;
        }

        if (this.findMatchingItemSlots(input).isEmpty()) {
            return false;
        }

        return this.matchesFluid(this.fluidInput, input.getFluidInput());
    }

    public Optional<int[]> findMatchingItemSlots(AssemblyMachineRecipeInput input) {
        ItemStack[] requiredItems = this.getRequiredItemInputs();
        ItemStack[] actualItems = new ItemStack[] {
            input.getItem(0),
            input.getItem(1),
            input.getItem(2),
            input.getItem(3),
            input.getItem(4),
            input.getItem(5)
        };
        int[] matchedSlots = new int[] { -1, -1, -1, -1, -1, -1 };
        boolean[] usedSlots = new boolean[actualItems.length];

        if (!this.matchRequiredItems(requiredItems, actualItems, 0, usedSlots, matchedSlots)) {
            return Optional.empty();
        }

        return Optional.of(matchedSlots);
    }

    @Override
    public @Nonnull ItemStack assemble(@Nonnull AssemblyMachineRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        return this.resultItem.copy();
    }

    @Override
    public @Nonnull ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return this.resultItem.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @Nonnull RecipeSerializer<? extends Recipe<AssemblyMachineRecipeInput>> getSerializer() {
        return ModRecipeSerializers.COBBLESTONE_ASSEMBLY_MACHINE.get();
    }

    @Override
    public @Nonnull RecipeType<? extends Recipe<AssemblyMachineRecipeInput>> getType() {
        return ModRecipeTypes.COBBLESTONE_ASSEMBLY_MACHINE.get();
    }

    private boolean hasAnyInput() {
        return this.hasFirstItemInput()
            || this.hasSecondItemInput()
            || this.hasThirdItemInput()
            || this.hasFourthItemInput()
            || this.hasFifthItemInput()
            || this.hasSixthItemInput()
            || this.hasFluidInput();
    }

    private boolean matchesItem(ItemStack expected, ItemStack actual) {
        if (expected.isEmpty()) {
            return true;
        }

        if (actual.isEmpty()) {
            return false;
        }

        if (!ItemStack.isSameItemSameComponents(expected, actual)) {
            return false;
        }

        return actual.getCount() >= expected.getCount();
    }

    private ItemStack[] getRequiredItemInputs() {
        return new ItemStack[] {
            this.firstItemInput,
            this.secondItemInput,
            this.thirdItemInput,
            this.fourthItemInput,
            this.fifthItemInput,
            this.sixthItemInput
        };
    }

    private boolean matchRequiredItems(ItemStack[] requiredItems, ItemStack[] actualItems, int requiredIndex, boolean[] usedSlots, int[] matchedSlots) {
        if (requiredIndex >= requiredItems.length) {
            return true;
        }

        ItemStack requiredStack = requiredItems[requiredIndex];
        if (requiredStack.isEmpty()) {
            matchedSlots[requiredIndex] = -1;
            return this.matchRequiredItems(requiredItems, actualItems, requiredIndex + 1, usedSlots, matchedSlots);
        }

        // 同じ材料が複数個ある recipe でも正しく対応付けられるよう、
        // 1 スロットずつ仮に割り当てながら最後まで成立する組み合わせを探します。
        for (int actualIndex = 0; actualIndex < actualItems.length; actualIndex++) {
            if (usedSlots[actualIndex]) {
                continue;
            }
            if (!this.matchesItem(requiredStack, actualItems[actualIndex])) {
                continue;
            }

            usedSlots[actualIndex] = true;
            matchedSlots[requiredIndex] = actualIndex;
            if (this.matchRequiredItems(requiredItems, actualItems, requiredIndex + 1, usedSlots, matchedSlots)) {
                return true;
            }

            usedSlots[actualIndex] = false;
            matchedSlots[requiredIndex] = -1;
        }

        return false;
    }

    private boolean matchesFluid(FluidStack expected, FluidStack actual) {
        if (expected.isEmpty()) {
            return true;
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