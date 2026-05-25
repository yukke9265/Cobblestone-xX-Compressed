package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItemTags;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

@SuppressWarnings("null")
public final class CobblestoneChemicalReactorRecipeDatagen {
    private static final ChemicalReactorRecipeDefinition[] RECIPES = new ChemicalReactorRecipeDefinition[] {
        new ChemicalReactorRecipeDefinition(
            "amethyst_and_dirty_emerald_diamond_fluids_to_emerald_fluid",
            Optional.of(sizedTag(ModItemTags.DUSTS_AMETHYST, 16)),
            Optional.empty(),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            107374182400L,
            1048576
        ),
        new ChemicalReactorRecipeDefinition(
            "emerald_cobblestone_dust_and_dirty_netherite_diamond_fluids_and_canned_enderite_to_netherite_fluid",
            Optional.of(sizedItem(ModItems.TierCobblestoneDust.EMERALD.getItem().get(), 4)),
            Optional.of(sizedItem(ModItems.CANNED_STABLE_ENDERITE.get())),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.NETHERITE.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 4000),
            new ItemStack(ModItems.TierCobblestoneDirtyDust.EMERALD.getItem().get(), 4),
            ItemStack.EMPTY,
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.NETHERITE.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            107374182400L,
            1048576
        ),
        new ChemicalReactorRecipeDefinition(
            "emerald_cobblestone_dust_and_dirty_netherite_diamond_fluids_and_compressed_enderite_to_netherite_fluid",
            Optional.of(sizedItem(ModItems.TierCobblestoneDust.EMERALD.getItem().get(), 4)),
            Optional.of(sizedItem(ModItems.EXTREME_COMPRESSED_CANNED_ENDERITE.get())),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.OBSIDIAN.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 4000),
            new ItemStack(ModItems.TierCobblestoneDirtyDust.EMERALD.getItem().get(), 4),
            ItemStack.EMPTY,
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.OBSIDIAN.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            107374182400L,
            1048576
        )
    };

    private CobblestoneChemicalReactorRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (ChemicalReactorRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneChemicalReactorRecipe(
                output,
                recipe.recipeName,
                recipe.firstItemInput,
                recipe.secondItemInput,
                recipe.firstFluidInput,
                recipe.secondFluidInput,
                recipe.firstResultItem,
                recipe.secondResultItem,
                recipe.firstResultFluid,
                recipe.secondResultFluid,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static SizedIngredient sizedItem(ItemLike item) {
        return sizedItem(item, 1);
    }

    private static SizedIngredient sizedItem(ItemLike item, int count) {
        return new SizedIngredient(Ingredient.of(item), count);
    }

    private static SizedIngredient sizedTag(TagKey<Item> tag, int count) {
        return new SizedIngredient(Ingredient.of(tag), count);
    }

    private static class ChemicalReactorRecipeDefinition {
        private final String recipeName;
        private final Optional<SizedIngredient> firstItemInput;
        private final Optional<SizedIngredient> secondItemInput;
        private final FluidStack firstFluidInput;
        private final FluidStack secondFluidInput;
        private final ItemStack firstResultItem;
        private final ItemStack secondResultItem;
        private final FluidStack firstResultFluid;
        private final FluidStack secondResultFluid;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private ChemicalReactorRecipeDefinition(
            String recipeName,
            Optional<SizedIngredient> firstItemInput,
            Optional<SizedIngredient> secondItemInput,
            FluidStack firstFluidInput,
            FluidStack secondFluidInput,
            ItemStack firstResultItem,
            ItemStack secondResultItem,
            FluidStack firstResultFluid,
            FluidStack secondResultFluid,
            long totalCobblestonePower,
            long cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.firstItemInput = firstItemInput;
            this.secondItemInput = secondItemInput;
            this.firstFluidInput = firstFluidInput.copy();
            this.secondFluidInput = secondFluidInput.copy();
            this.firstResultItem = firstResultItem.copy();
            this.secondResultItem = secondResultItem.copy();
            this.firstResultFluid = firstResultFluid.copy();
            this.secondResultFluid = secondResultFluid.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}