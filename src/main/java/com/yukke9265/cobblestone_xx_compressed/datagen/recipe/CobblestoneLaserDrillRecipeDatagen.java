package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

/*
アメジスト丸石 → アメジスト + 副産物 ダイヤ
アクアマリン丸石 → アクアマリン + 副産物 ダイヤ
トパーズ丸石 →トパーズ + 副産物 ダイヤ
ルビー丸石 → ルビー + 副産物 ダイヤ
ダイヤモンド丸石 → ダイヤモンド + 副産物 古代の残骸
エメラルド丸石 → エメラルド + 副産物 古代の残骸
ネザライト丸石 → 古代の残骸 + 副産物 残響のかけら
黒曜石丸石 → 黒曜石 + 副産物 残響のかけら
*/

public final class CobblestoneLaserDrillRecipeDatagen {
    private static final LaserDrillRecipeDefinition[] RECIPES = new LaserDrillRecipeDefinition[] {
        new LaserDrillRecipeDefinition(
            "amethyst_compressed_cobblestone_to_amethyst_shard_and_diamond",
            ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get(),
            new ItemStack(Items.AMETHYST_SHARD),
            0.5F,
            new ItemStack(Items.DIAMOND),
            0.01F,
            4096000,
            256
        ),
        new LaserDrillRecipeDefinition(
            "aquamarine_compressed_cobblestone_to_aquamarine_shard_and_diamond",
            ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock().get(),
            new ItemStack(ModItems.AQUAMARINE_SHARD.get()),
            0.5F,
            new ItemStack(Items.DIAMOND),
            0.01F,
            4096000,
            256
        ),
        new LaserDrillRecipeDefinition(
            "topaz_compressed_cobblestone_to_topaz_shard_and_diamond",
            ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock().get(),
            new ItemStack(ModItems.TOPAZ_SHARD.get()),
            0.5F,
            new ItemStack(Items.DIAMOND),
            0.01F,
            4096000,
            256
        ),
        new LaserDrillRecipeDefinition(
            "ruby_compressed_cobblestone_to_ruby_shard_and_diamond",
            ModBlocks.TierCompressedCobblestone.RUBY.getBlock().get(),
            new ItemStack(ModItems.RUBY_SHARD.get()),
            0.5F,
            new ItemStack(Items.DIAMOND),
            0.01F,
            4096000,
            256
        ),
        new LaserDrillRecipeDefinition(
            "sapphire_compressed_cobblestone_to_sapphire_shard_and_diamond",
            ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock().get(),
            new ItemStack(ModItems.SAPPHIRE_SHARD.get()),
            0.5F,
            new ItemStack(Items.DIAMOND),
            0.01F,
            4096000,
            256
        ),
        new LaserDrillRecipeDefinition(
            "diamond_compressed_cobblestone_to_diamond_and_ancient_debris",
            ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock().get(),
            new ItemStack(Items.DIAMOND),
            0.5F,
            new ItemStack(Items.ANCIENT_DEBRIS),
            0.01F,
            4096000,
            256
        ),
        new LaserDrillRecipeDefinition(
            "emerald_compressed_cobblestone_to_emerald_and_ancient_debris",
            ModBlocks.TierCompressedCobblestone.EMERALD.getBlock().get(),
            new ItemStack(Items.EMERALD),
            0.5F,
            new ItemStack(Items.ANCIENT_DEBRIS),
            0.01F,
            4096000,
            256
        ),
        new LaserDrillRecipeDefinition(
            "netherite_compressed_cobblestone_to_ancient_debris_and_echo_shard",
            ModBlocks.TierCompressedCobblestone.NETHERITE.getBlock().get(),
            new ItemStack(Items.ANCIENT_DEBRIS),
            0.5F,
            new ItemStack(Items.ECHO_SHARD),
            0.01F,
            4096000,
            256
        ),
        new LaserDrillRecipeDefinition(
            "obsidian_compressed_cobblestone_to_obsidian_and_echo_shard",
            ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock().get(),
            new ItemStack(Items.OBSIDIAN,64),
            0.5F,
            new ItemStack(Items.ECHO_SHARD),
            0.01F,
            4096000,
            256
        )
    };

    private CobblestoneLaserDrillRecipeDatagen() {
    }

    public static void register(RecipeOutput output) {
        for (LaserDrillRecipeDefinition recipe : RECIPES) {
            MachineRecipeOutputHelper.saveCobblestoneLaserDrillRecipe(
                output,
                recipe.recipeName,
                recipe.ingredient,
                recipe.firstResult,
                recipe.firstResultChance,
                recipe.secondResult,
                recipe.secondResultChance,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private static class LaserDrillRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemStack firstResult;
        private final float firstResultChance;
        private final ItemStack secondResult;
        private final float secondResultChance;
        private final long totalCobblestonePower;
        private final long cobblestonePowerPerTick;

        private LaserDrillRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            ItemStack firstResult,
            float firstResultChance,
            ItemStack secondResult,
            float secondResultChance,
            long totalCobblestonePower,
            long cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.firstResult = firstResult.copy();
            this.firstResultChance = firstResultChance;
            this.secondResult = secondResult.copy();
            this.secondResultChance = secondResultChance;
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}