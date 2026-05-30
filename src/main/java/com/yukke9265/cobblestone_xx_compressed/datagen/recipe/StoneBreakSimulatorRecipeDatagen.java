package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.loot.CompressedStoneLootDefinition;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class StoneBreakSimulatorRecipeDatagen {
    private static final long DEFAULT_TOTAL_CP = 100L;
    private static final long DEFAULT_CP_PER_TICK = 1L;

    private StoneBreakSimulatorRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (CompressedStoneLootDefinition definition : CompressedStoneLootDefinition.getDefinitions()) {
            Block stoneBlock = definition.getStoneBlock().get();
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(stoneBlock);

            MachineRecipeOutputHelper.saveStoneBreakSimulatorRecipe(
                output,
                blockId.getPath(),
                stoneBlock,
                DEFAULT_TOTAL_CP,
                DEFAULT_CP_PER_TICK
            );
        }
    }
}