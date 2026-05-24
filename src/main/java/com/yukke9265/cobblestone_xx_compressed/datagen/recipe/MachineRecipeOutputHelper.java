package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneAssemblyMachineRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCentrifugeRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneChemicalReactorRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrystallizationChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneDissolutionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneExtremeCompressorRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFluidMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneLaserDrillRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMelterRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestonePoweredFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneReactionChamberRecipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

final class MachineRecipeOutputHelper {
    private MachineRecipeOutputHelper() {
    }

    public static void saveCobblestoneFurnaceRecipe(RecipeOutput output, String recipeName, ItemLike ingredient, ItemLike result, int processingTime) {
        CobblestoneFurnaceRecipe recipe = new CobblestoneFurnaceRecipe(
            Ingredient.of(ingredient),
            new ItemStack(result),
            processingTime
        );

        output.accept(modRecipeId("cobblestone_furnace/" + recipeName), recipe, null);
    }

    public static void saveCobblestonePoweredFurnaceRecipe(
        RecipeOutput output,
        String recipeName,
        ItemLike ingredient,
        ItemLike result,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestonePoweredFurnaceRecipe recipe = new CobblestonePoweredFurnaceRecipe(
            Ingredient.of(ingredient),
            new ItemStack(result),
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_powered_furnace/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneCrusherRecipe(
        RecipeOutput output,
        String recipeName,
        ItemLike ingredient,
        ItemStack result,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneCrusherRecipe recipe = new CobblestoneCrusherRecipe(
            Ingredient.of(ingredient),
            result.copy(),
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_crusher/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneCentrifugeRecipe(
        RecipeOutput output,
        String recipeName,
        ItemLike ingredient,
        ItemStack firstResult,
        float firstResultChance,
        ItemStack secondResult,
        float secondResultChance,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneCentrifugeRecipe recipe = new CobblestoneCentrifugeRecipe(
            Ingredient.of(ingredient),
            firstResult,
            firstResultChance,
            secondResult,
            secondResultChance,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_centrifuge/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneLaserDrillRecipe(
        RecipeOutput output,
        String recipeName,
        ItemLike ingredient,
        ItemStack firstResult,
        float firstResultChance,
        ItemStack secondResult,
        float secondResultChance,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneLaserDrillRecipe recipe = new CobblestoneLaserDrillRecipe(
            Ingredient.of(ingredient),
            firstResult,
            firstResultChance,
            secondResult,
            secondResultChance,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_laser_drill/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneExtremeCompressorRecipe(
        RecipeOutput output,
        String recipeName,
        ItemLike ingredient,
        ItemLike result,
        int requiredItemCount,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneExtremeCompressorRecipe recipe = new CobblestoneExtremeCompressorRecipe(
            Ingredient.of(ingredient),
            new ItemStack(result),
            requiredItemCount,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_extreme_compressor/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneMixerRecipe(
        RecipeOutput output,
        String recipeName,
        ItemStack firstIngredient,
        ItemStack secondIngredient,
        ItemStack result,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneMixerRecipe recipe = new CobblestoneMixerRecipe(
            firstIngredient,
            secondIngredient,
            result,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_mixer/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneMelterRecipe(
        RecipeOutput output,
        String recipeName,
        ItemLike ingredient,
        FluidStack fluidResult,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneMelterRecipe recipe = new CobblestoneMelterRecipe(
            Ingredient.of(ingredient),
            fluidResult,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_melter/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneChemicalReactorRecipe(
        RecipeOutput output,
        String recipeName,
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
        CobblestoneChemicalReactorRecipe recipe = new CobblestoneChemicalReactorRecipe(
            firstItemInput,
            secondItemInput,
            firstFluidInput,
            secondFluidInput,
            firstResultItem,
            secondResultItem,
            firstResultFluid,
            secondResultFluid,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_chemical_reactor/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneReactionChamberRecipe(
        RecipeOutput output,
        String recipeName,
        FluidStack fluidInput,
        ItemLike firstIngredient,
        ItemLike secondIngredient,
        ItemStack result,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneReactionChamberRecipe recipe = new CobblestoneReactionChamberRecipe(
            fluidInput,
            Ingredient.of(firstIngredient),
            Ingredient.of(secondIngredient),
            result,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_reaction_chamber/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneAssemblyMachineRecipe(
        RecipeOutput output,
        String recipeName,
        ItemStack firstItemInput,
        ItemStack secondItemInput,
        ItemStack thirdItemInput,
        ItemStack fourthItemInput,
        ItemStack fifthItemInput,
        ItemStack sixthItemInput,
        FluidStack fluidInput,
        ItemStack resultItem,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneAssemblyMachineRecipe recipe = new CobblestoneAssemblyMachineRecipe(
            firstItemInput,
            secondItemInput,
            thirdItemInput,
            fourthItemInput,
            fifthItemInput,
            sixthItemInput,
            fluidInput,
            resultItem,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_assembly_machine/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneDissolutionChamberRecipe(
        RecipeOutput output,
        String recipeName,
        ItemLike ingredient,
        FluidStack fluidInput,
        FluidStack fluidOutput,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneDissolutionChamberRecipe recipe = new CobblestoneDissolutionChamberRecipe(
            Ingredient.of(ingredient),
            fluidInput,
            fluidOutput,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_dissolution_chamber/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneCrystallizationChamberRecipe(
        RecipeOutput output,
        String recipeName,
        FluidStack fluidInput,
        ItemLike result,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneCrystallizationChamberRecipe recipe = new CobblestoneCrystallizationChamberRecipe(
            fluidInput,
            new ItemStack(result),
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_crystallization_chamber/" + recipeName), recipe, null);
    }

    public static void saveCobblestoneFluidMixerRecipe(
        RecipeOutput output,
        String recipeName,
        FluidStack firstFluidInput,
        FluidStack secondFluidInput,
        FluidStack fluidOutput,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneFluidMixerRecipe recipe = new CobblestoneFluidMixerRecipe(
            firstFluidInput,
            secondFluidInput,
            fluidOutput,
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(modRecipeId("cobblestone_fluid_mixer/" + recipeName), recipe, null);
    }

    private static ResourceLocation modRecipeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, path);
    }
}