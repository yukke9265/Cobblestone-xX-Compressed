package com.yukke9265.cobblestone_xx_compressed.registry;

import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCentrifugeRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneDissolutionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneLaserDrillRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMelterRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestonePoweredFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneReactionChamberRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(Registries.RECIPE_TYPE, CobblestonexXCompressed.MODID);

    public static final Supplier<RecipeType<CobblestoneFurnaceRecipe>> COBBLESTONE_FURNACE =
        RECIPE_TYPES.register("cobblestone_furnace", RecipeType::simple);

    public static final Supplier<RecipeType<CobblestonePoweredFurnaceRecipe>> COBBLESTONE_POWERED_FURNACE =
        RECIPE_TYPES.register("cobblestone_powered_furnace", RecipeType::simple);

    public static final Supplier<RecipeType<CobblestoneCrusherRecipe>> COBBLESTONE_CRUSHER =
        RECIPE_TYPES.register("cobblestone_crusher", RecipeType::simple);

    public static final Supplier<RecipeType<CobblestoneCentrifugeRecipe>> COBBLESTONE_CENTRIFUGE =
        RECIPE_TYPES.register("cobblestone_centrifuge", RecipeType::simple);

    public static final Supplier<RecipeType<CobblestoneLaserDrillRecipe>> COBBLESTONE_LASER_DRILL =
        RECIPE_TYPES.register("cobblestone_laser_drill", RecipeType::simple);

    public static final Supplier<RecipeType<CobblestoneMixerRecipe>> COBBLESTONE_MIXER =
        RECIPE_TYPES.register("cobblestone_mixer", RecipeType::simple);

    public static final Supplier<RecipeType<CobblestoneMelterRecipe>> COBBLESTONE_MELTER =
        RECIPE_TYPES.register("cobblestone_melter", RecipeType::simple);

    public static final Supplier<RecipeType<CobblestoneReactionChamberRecipe>> COBBLESTONE_REACTION_CHAMBER =
        RECIPE_TYPES.register("cobblestone_reaction_chamber", RecipeType::simple);

    public static final Supplier<RecipeType<CobblestoneDissolutionChamberRecipe>> COBBLESTONE_DISSOLUTION_CHAMBER =
        RECIPE_TYPES.register("cobblestone_dissolution_chamber", RecipeType::simple);
}