package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CobblestoneReactionChamberRecipeDatagen {
    private static final ReactionChamberRecipeDefinition[] RECIPES = new ReactionChamberRecipeDefinition[] {
        new ReactionChamberRecipeDefinition(
            "water_and_amethyst_dust_and_redstone_dust_to_amethyst_shard",
            new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 1000),
            ModItems.AMETHYST_DUST.get(),
            Items.REDSTONE,
            new ItemStack(Items.AMETHYST_SHARD),
            3200,
            32
        )
    };

    private CobblestoneReactionChamberRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (ReactionChamberRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneReactionChamberRecipe(
                output,
                recipe.recipeName,
                recipe.fluidInput,
                recipe.firstIngredient,
                recipe.secondIngredient,
                recipe.result,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static class ReactionChamberRecipeDefinition {
        private final String recipeName;
        private final FluidStack fluidInput;
        private final ItemLike firstIngredient;
        private final ItemLike secondIngredient;
        private final ItemStack result;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private ReactionChamberRecipeDefinition(String recipeName, FluidStack fluidInput, ItemLike firstIngredient, ItemLike secondIngredient, ItemStack result, int totalCobblestonePower, int cobblestonePowerPerTick) {
            this.recipeName = recipeName;
            this.fluidInput = fluidInput.copy();
            this.firstIngredient = firstIngredient;
            this.secondIngredient = secondIngredient;
            this.result = result.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}