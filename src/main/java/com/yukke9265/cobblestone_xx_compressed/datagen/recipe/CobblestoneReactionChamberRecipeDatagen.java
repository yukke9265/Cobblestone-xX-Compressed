package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItemTags;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

@SuppressWarnings("null")
public final class CobblestoneReactionChamberRecipeDatagen {
    private static final String AE2_MOD_ID = "ae2";
    private static final long REACTION_CHAMBER_TOTAL_CP = 3276800L;
    private static final long REACTION_CHAMBER_CP_PER_TICK = 1024L;
    private static final int AE2_PRINT_MATERIAL_COUNT = 4;
    private static final int AE2_SKY_DUST_COUNT = 1;
    private static final int AE2_PROCESSOR_INPUT_COUNT = 4;
    private static final int AE2_RESULT_COUNT = 4;
    private static final int AE2_FLUID_AMOUNT = 1000;
    private static final ReactionChamberRecipeDefinition[] RECIPES = new ReactionChamberRecipeDefinition[] {
        new ReactionChamberRecipeDefinition(
            "water_and_aquamarine_mixture_and_aquamarine_cobblestone_to_aquamarine_cobblestone_dust",
            new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 1000),
            ingredientOf(ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock().get()),
            ingredientOf(ModItems.AQUAMARINE_MIXTURE.get()),
            new ItemStack(ModItems.TIER_AQUAMARINE_COBBLESTONE_DUST.get()),
            REACTION_CHAMBER_TOTAL_CP,
            REACTION_CHAMBER_CP_PER_TICK
        ),
        new ReactionChamberRecipeDefinition(
            "glowtopaz_and_topaz_cobblestone_and_amethyst_dust_to_topaz_cobblestone_dust",
            new FluidStack(ModFluids.WaterBasedFluid.GLOW_TOPAZ.getFluidEntry().getStillFluid(), 1000),
            ingredientOf(ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock().get()),
            ingredientOf(ModItemTags.DUSTS_AMETHYST),
            new ItemStack(ModItems.TIER_TOPAZ_COBBLESTONE_DUST.get()),
            REACTION_CHAMBER_TOTAL_CP,
            REACTION_CHAMBER_CP_PER_TICK
        ),
        new ReactionChamberRecipeDefinition(
            "molten_dirty_ruby_cobblestone_and_obsidian_dust_and_amethyst_dust_to_ruby_cobblestone_dust",
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.RUBY.getFluidEntry().getStillFluid(), 1000),
            ingredientOf(ModItemTags.DUSTS_OBSIDIAN),
            ingredientOf(ModItemTags.DUSTS_AMETHYST),
            new ItemStack(ModItems.TIER_RUBY_COBBLESTONE_DUST.get()),
            REACTION_CHAMBER_TOTAL_CP,
            REACTION_CHAMBER_CP_PER_TICK
        ),
        new ReactionChamberRecipeDefinition(
            "molten_dirty_sapphire_cobblestone_and_obsidian_dust_and_amethyst_dust_to_sapphire_cobblestone_dust",
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.SAPPHIRE.getFluidEntry().getStillFluid(), 1000),
            ingredientOf(ModItemTags.DUSTS_OBSIDIAN),
            ingredientOf(ModItemTags.DUSTS_AMETHYST),
            new ItemStack(ModItems.TIER_SAPPHIRE_COBBLESTONE_DUST.get()),
            REACTION_CHAMBER_TOTAL_CP,
            REACTION_CHAMBER_CP_PER_TICK
        ),
        new ReactionChamberRecipeDefinition(
            "shiny_water_and_dirty_diamond_cobblestone_and_redstone_to_diamond_cobblestone_dust",
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid(), 1000),
            ingredientOf(ModItems.TIER_DIAMOND_COBBLESTONE_DIRTY_DUST.get()),
            ingredientOf(Items.REDSTONE),
            new ItemStack(ModItems.TIER_DIAMOND_COBBLESTONE_DUST.get()),
            REACTION_CHAMBER_TOTAL_CP,
            REACTION_CHAMBER_CP_PER_TICK
        ),
        new ReactionChamberRecipeDefinition(
            "shiny_water_and_dirty_emerald_cobblestone_and_redstone_to_emerald_cobblestone_dust",
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid(), 1000),
            ingredientOf(ModItems.TIER_EMERALD_COBBLESTONE_DIRTY_DUST.get()),
            ingredientOf(Items.REDSTONE),
            new ItemStack(ModItems.TIER_EMERALD_COBBLESTONE_DUST.get()),
            REACTION_CHAMBER_TOTAL_CP,
            REACTION_CHAMBER_CP_PER_TICK
        ),
        new ReactionChamberRecipeDefinition(
            "shiny_water_and_dirty_netherite_cobblestone_and_redstone_to_netherite_cobblestone_dust",
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid(), 1000),
            ingredientOf(ModItems.TIER_NETHERITE_COBBLESTONE_DIRTY_DUST.get()),
            ingredientOf(Items.REDSTONE),
            new ItemStack(ModItems.TIER_NETHERITE_COBBLESTONE_DUST.get()),
            REACTION_CHAMBER_TOTAL_CP,
            REACTION_CHAMBER_CP_PER_TICK
        ),
        new ReactionChamberRecipeDefinition(
            "shiny_water_and_dirty_obsidian_cobblestone_and_redstone_to_obsidian_cobblestone_dust",
            new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid(), 1000),
            ingredientOf(ModItems.TIER_OBSIDIAN_COBBLESTONE_DIRTY_DUST.get()),
            ingredientOf(Items.REDSTONE),
            new ItemStack(ModItems.TIER_OBSIDIAN_COBBLESTONE_DUST.get()),
            REACTION_CHAMBER_TOTAL_CP,
            REACTION_CHAMBER_CP_PER_TICK
        ),
        new ReactionChamberRecipeDefinition(
            "lava_amd_lapis_dust_and_redstone_to_obsidian",
            new FluidStack(net.minecraft.world.level.material.Fluids.LAVA, 4000),
            ingredientOf(ModItemTags.DUSTS_LAPIS),
            ingredientOf(Items.REDSTONE),
            new ItemStack(Items.OBSIDIAN,16),
            REACTION_CHAMBER_TOTAL_CP,
            REACTION_CHAMBER_CP_PER_TICK
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

        registerAe2Recipes(output);
    }

    private static void registerAe2Recipes(RecipeOutput output) {
        // 基板は素材 4 + スカイストーンダスト 1、回路は基板 4 + シリコン基板 4 です。
        RecipeOutput ae2Output = output.withConditions(new ModLoadedCondition(AE2_MOD_ID));
        FluidStack shinyWater = new FluidStack(ModFluids.WaterBasedFluid.SHINY_WATER.getFluidEntry().getStillFluid(), AE2_FLUID_AMOUNT);
        FluidStack redRuby = new FluidStack(ModFluids.WaterBasedFluid.RED_RUBY.getFluidEntry().getStillFluid(), AE2_FLUID_AMOUNT);
        SizedIngredient skyDust = sizedTag(ModItemTags.AE2_SKY_DUST, AE2_SKY_DUST_COUNT);
        SizedIngredient printedSilicon = sizedTag(ModItemTags.AE2_PRINTED_SILICON, AE2_PROCESSOR_INPUT_COUNT);

        saveAe2ReactionChamberRecipe(
            ae2Output,
            "ae2_gold_and_sky_dust_to_printed_logic_processor",
            shinyWater,
            sizedItem(Items.GOLD_INGOT, AE2_PRINT_MATERIAL_COUNT),
            skyDust,
            "printed_logic_processor"
        );
        saveAe2ReactionChamberRecipe(
            ae2Output,
            "ae2_certus_quartz_and_sky_dust_to_printed_calculation_processor",
            shinyWater,
            sizedTag(ModItemTags.GEMS_CERTUS_QUARTZ, AE2_PRINT_MATERIAL_COUNT),
            skyDust,
            "printed_calculation_processor"
        );
        saveAe2ReactionChamberRecipe(
            ae2Output,
            "ae2_diamond_and_sky_dust_to_printed_engineering_processor",
            shinyWater,
            sizedItem(Items.DIAMOND, AE2_PRINT_MATERIAL_COUNT),
            skyDust,
            "printed_engineering_processor"
        );
        saveAe2ReactionChamberRecipe(
            ae2Output,
            "ae2_silicon_and_sky_dust_to_printed_silicon",
            shinyWater,
            sizedTag(ModItemTags.SILICON, AE2_PRINT_MATERIAL_COUNT),
            skyDust,
            "printed_silicon"
        );
        saveAe2ReactionChamberRecipe(
            ae2Output,
            "ae2_printed_logic_processor_and_printed_silicon_to_logic_processor",
            redRuby,
            sizedTag(ModItemTags.AE2_PRINTED_LOGIC_PROCESSOR, AE2_PROCESSOR_INPUT_COUNT),
            printedSilicon,
            "logic_processor"
        );
        saveAe2ReactionChamberRecipe(
            ae2Output,
            "ae2_printed_calculation_processor_and_printed_silicon_to_calculation_processor",
            redRuby,
            sizedTag(ModItemTags.AE2_PRINTED_CALCULATION_PROCESSOR, AE2_PROCESSOR_INPUT_COUNT),
            printedSilicon,
            "calculation_processor"
        );
        saveAe2ReactionChamberRecipe(
            ae2Output,
            "ae2_printed_engineering_processor_and_printed_silicon_to_engineering_processor",
            redRuby,
            sizedTag(ModItemTags.AE2_PRINTED_ENGINEERING_PROCESSOR, AE2_PROCESSOR_INPUT_COUNT),
            printedSilicon,
            "engineering_processor"
        );
        saveAe2ReactionChamberRecipe(
            ae2Output,
            "ae2_amethyst_compressed_cobblestone_and_singularity_bit_and_lava_to_singularity",
            new FluidStack(net.minecraft.world.level.material.Fluids.LAVA, AE2_FLUID_AMOUNT),
            sizedItem(ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get(), 16),
            sizedItem(ModItems.TierCompressedCobblestoneSingularityBit.AMETHYST.getItem().get(), 1),
            "singularity",
            1
        );
    }

    private static void saveAe2ReactionChamberRecipe(
        RecipeOutput output,
        String recipeName,
        FluidStack fluidInput,
        SizedIngredient firstIngredient,
        SizedIngredient secondIngredient,
        String resultItemPath
    ) {
        saveAe2ReactionChamberRecipe(
            output,
            recipeName,
            fluidInput,
            firstIngredient,
            secondIngredient,
            resultItemPath,
            AE2_RESULT_COUNT
        );
    }

    private static void saveAe2ReactionChamberRecipe(
        RecipeOutput output,
        String recipeName,
        FluidStack fluidInput,
        SizedIngredient firstIngredient,
        SizedIngredient secondIngredient,
        String resultItemPath,
        int resultCount
    ) {
        MachineRecipeOutputHelper.saveCobblestoneReactionChamberRecipe(
            output,
            recipeName,
            fluidInput,
            firstIngredient,
            secondIngredient,
            ResourceLocation.fromNamespaceAndPath(AE2_MOD_ID, resultItemPath),
            resultCount,
            REACTION_CHAMBER_TOTAL_CP,
            REACTION_CHAMBER_CP_PER_TICK
        );
    }

    private static Ingredient ingredientOf(ItemLike item) {
        return Ingredient.of(item);
    }

    private static Ingredient ingredientOf(TagKey<Item> tag) {
        return Ingredient.of(tag);
    }

    private static SizedIngredient sizedItem(ItemLike item, int count) {
        return new SizedIngredient(Ingredient.of(item), count);
    }

    private static SizedIngredient sizedTag(TagKey<Item> tag, int count) {
        return new SizedIngredient(Ingredient.of(tag), count);
    }

    private static class ReactionChamberRecipeDefinition {
        private final String recipeName;
        private final FluidStack fluidInput;
        private final Ingredient firstIngredient;
        private final Ingredient secondIngredient;
        private final ItemStack result;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private ReactionChamberRecipeDefinition(String recipeName, FluidStack fluidInput, Ingredient firstIngredient, Ingredient secondIngredient, ItemStack result, long totalCobblestonePower, long cobblestonePowerPerTick) {
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