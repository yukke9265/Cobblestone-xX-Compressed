package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneAssemblyMachineRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCentrifugeRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrystallizationChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneChemicalReactorRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneDissolutionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneCrusherRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneExtremeCompressorRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFluidMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneLaserDrillRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestonePoweredFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneReactionChamberRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMelterRecipe;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneMixerRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;

@SuppressWarnings("null")
public class ModRecipeProvider extends RecipeProvider {
    // gem のレシピは tier ごとに個別変更しやすいよう、
    // 「出力先」と「その tier で使う素材」を 1 行ずつ定義しておきます。
    // ここを見れば、どの gem が何から作られるかをすぐ追えます。
    // gemのレシピは削除します(入手経路をcompressed stoneからのドロップに絞るため)
    //private static final GemRecipeDefinition[] GEM_RECIPES = new GemRecipeDefinition[] {
    //    new GemRecipeDefinition("cobblestone_gem", Items.FLINT, ModItems.COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_copper_cobblestone_gem", Items.COPPER_INGOT, ModItems.TIER_COPPER_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_iron_cobblestone_gem", Items.IRON_INGOT, ModItems.TIER_IRON_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_gold_cobblestone_gem", Items.GOLD_INGOT, ModItems.TIER_GOLD_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_amethyst_cobblestone_gem", Items.AMETHYST_SHARD, ModItems.TIER_AMETHYST_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_aquamarine_cobblestone_gem", Items.PRISMARINE_CRYSTALS, ModItems.TIER_AQUAMARINE_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_topaz_cobblestone_gem", Items.QUARTZ, ModItems.TIER_TOPAZ_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_ruby_cobblestone_gem", Items.REDSTONE, ModItems.TIER_RUBY_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_sapphire_cobblestone_gem", Items.LAPIS_LAZULI, ModItems.TIER_SAPPHIRE_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_diamond_cobblestone_gem", Items.DIAMOND, ModItems.TIER_DIAMOND_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_emerald_cobblestone_gem", Items.EMERALD, ModItems.TIER_EMERALD_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_netherite_cobblestone_gem", Items.NETHERITE_INGOT, ModItems.TIER_NETHERITE_COBBLESTONE_GEM.get()),
    //    new GemRecipeDefinition("tier_obsidian_cobblestone_gem", Items.OBSIDIAN, ModItems.TIER_OBSIDIAN_COBBLESTONE_GEM.get())
    //};

    // 圧縮丸石の upgrade レシピも 1 行ずつ並べます。
    // 「前段階の圧縮丸石」と「中央に置く gem」と「出力ブロック」を分けておくと、
    // 後から 1 tier だけ差し替える作業がかなり楽になります。
    private static final CompressionRecipeDefinition[] COMPRESSED_COBBLESTONE_RECIPES = new CompressionRecipeDefinition[] {
        new CompressionRecipeDefinition("tier_copper_compressed_cobblestone", ModBlocks.COMPRESSED_COBBLESTONE.get(), ModItems.COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.COPPER.getBlock().get()),
        new CompressionRecipeDefinition("tier_iron_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.COPPER.getBlock().get(), ModItems.TIER_COPPER_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.IRON.getBlock().get()),
        new CompressionRecipeDefinition("tier_gold_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.IRON.getBlock().get(), ModItems.TIER_IRON_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.GOLD.getBlock().get()),
        new CompressionRecipeDefinition("tier_amethyst_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.GOLD.getBlock().get(), ModItems.TIER_GOLD_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get()),
        new CompressionRecipeDefinition("tier_aquamarine_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get(), ModItems.TIER_AMETHYST_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock().get()),
        new CompressionRecipeDefinition("tier_topaz_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock().get(), ModItems.TIER_AQUAMARINE_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock().get()),
        new CompressionRecipeDefinition("tier_ruby_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock().get(), ModItems.TIER_TOPAZ_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.RUBY.getBlock().get()),
        new CompressionRecipeDefinition("tier_sapphire_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.RUBY.getBlock().get(), ModItems.TIER_RUBY_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock().get()),
        new CompressionRecipeDefinition("tier_diamond_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock().get(), ModItems.TIER_SAPPHIRE_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock().get()),
        new CompressionRecipeDefinition("tier_emerald_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock().get(), ModItems.TIER_DIAMOND_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.EMERALD.getBlock().get()),
        new CompressionRecipeDefinition("tier_netherite_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.EMERALD.getBlock().get(), ModItems.TIER_EMERALD_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.NETHERITE.getBlock().get()),
        new CompressionRecipeDefinition("tier_obsidian_compressed_cobblestone", ModBlocks.TierCompressedCobblestone.NETHERITE.getBlock().get(), ModItems.TIER_NETHERITE_COBBLESTONE_GEM.get(), ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock().get())
    };

    // 圧縮石への変換は、将来 tier ごとに個別時間や個別素材へ変えることを想定して、
    // こちらも 1 レシピずつ独立した定義にします。
    private static final MachineRecipeDefinition[] COMPRESSED_STONE_MACHINE_RECIPES = new MachineRecipeDefinition[] {
        /*
        丸石かまど加工
        純粋な圧縮丸石粉→圧縮石
        丸石ティア 丸石 → 圧縮石
        銅ティア 汚い粉→圧縮石
        鉄 金 アメジスト ティア 混合物→圧縮石
         */
        
        //丸石ティア 丸石 → 石
        new MachineRecipeDefinition("compressed_cobblestone_to_compressed_stone", ModBlocks.COMPRESSED_COBBLESTONE.get(), ModBlocks.COMPRESSED_STONE.get(), 100),
        
        //銅ティア 汚い粉→圧縮石
        new MachineRecipeDefinition("tier_copper_dirty_compressed_cobblestone_dust_to_tier_copper_compressed_stone", ModItems.TIER_COPPER_COBBLESTONE_DIRTY_DUST.get(), ModBlocks.TierCompressedStone.COPPER.getBlock().get(), 100),
        
        //鉄 金 アメジスト ティア 混合物→圧縮石
        new MachineRecipeDefinition("tier_iron_compressed_cobblestone_mixed_dust_to_tier_iron_compressed_stone", ModItems.TIER_IRON_COBBLESTONE_MIXED_DUST.get(), ModBlocks.TierCompressedStone.IRON.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_gold_compressed_cobblestone_mixed_dust_to_tier_gold_compressed_stone", ModItems.TIER_GOLD_COBBLESTONE_MIXED_DUST.get(), ModBlocks.TierCompressedStone.GOLD.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_amethyst_compressed_cobblestone_mixed_dust_to_tier_amethyst_compressed_stone", ModItems.TIER_AMETHYST_COBBLESTONE_MIXED_DUST.get(), ModBlocks.TierCompressedStone.AMETHYST.getBlock().get(), 100),

        //すべてのティア ピュアな圧縮丸石粉→圧縮石
        new MachineRecipeDefinition("compressed_cobblestone_pure_dust_to_compressed_stone",ModItems.COBBLESTONE_PURE_DUST.get(), ModBlocks.COMPRESSED_STONE.get(), 100),
        new MachineRecipeDefinition("tier_copper_compressed_cobblestone_pure_dust_to_tier_copper_compressed_stone", ModItems.TIER_COPPER_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.COPPER.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_iron_compressed_cobblestone_pure_dust_to_tier_iron_compressed_stone", ModItems.TIER_IRON_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.IRON.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_gold_compressed_cobblestone_pure_dust_to_tier_gold_compressed_stone", ModItems.TIER_GOLD_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.GOLD.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_amethyst_compressed_cobblestone_pure_dust_to_tier_amethyst_compressed_stone", ModItems.TIER_AMETHYST_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.AMETHYST.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_aquamarine_compressed_cobblestone_pure_dust_to_tier_aquamarine_compressed_stone", ModItems.TIER_AQUAMARINE_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.AQUAMARINE.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_topaz_compressed_cobblestone_pure_dust_to_tier_topaz_compressed_stone", ModItems.TIER_TOPAZ_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.TOPAZ.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_ruby_compressed_cobblestone_pure_dust_to_tier_ruby_compressed_stone", ModItems.TIER_RUBY_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.RUBY.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_sapphire_compressed_cobblestone_pure_dust_to_tier_sapphire_compressed_stone", ModItems.TIER_SAPPHIRE_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.SAPPHIRE.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_diamond_compressed_cobblestone_pure_dust_to_tier_diamond_compressed_stone", ModItems.TIER_DIAMOND_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.DIAMOND.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_emerald_compressed_cobblestone_pure_dust_to_tier_emerald_compressed_stone", ModItems.TIER_EMERALD_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.EMERALD.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_netherite_compressed_cobblestone_pure_dust_to_tier_netherite_compressed_stone", ModItems.TIER_NETHERITE_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.NETHERITE.getBlock().get(), 100),
        new MachineRecipeDefinition("tier_obsidian_compressed_cobblestone_pure_dust_to_tier_obsidian_compressed_stone", ModItems.TIER_OBSIDIAN_COBBLESTONE_PURE_DUST.get(), ModBlocks.TierCompressedStone.OBSIDIAN.getBlock().get(), 100)
    };

        private static final CrusherRecipeDefinition[] COBBLESTONE_CRUSHER_RECIPES = new CrusherRecipeDefinition[] {
        new CrusherRecipeDefinition(
            "tier_copper_compressed_cobblestone_to_tier_copper_cobblestone_dust",
            ModBlocks.TierCompressedCobblestone.COPPER.getBlock().get(),
            ModItems.TIER_COPPER_COBBLESTONE_DUST.get(),
            200,
            2
        ),
        new CrusherRecipeDefinition(
            "amethyst_shard_to_amethyst_dust",
            Items.AMETHYST_SHARD,
            ModItems.AMETHYST_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "aquamarine_shard_to_aquamarine_dust",
            ModItems.AQUAMARINE_SHARD.get(),
            ModItems.AQUAMARINE_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "ancient_debris_to_ancient_debris_dust",
            Items.ANCIENT_DEBRIS,
            ModItems.ANCIENT_DEBRIS_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "copper_ingot_to_copper_dust",
            Items.COPPER_INGOT,
            ModItems.COPPER_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "diamond_to_diamond_dust",
            Items.DIAMOND,
            ModItems.DIAMOND_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "ender_pearl_to_ender_dust",
            Items.ENDER_PEARL,
            ModItems.ENDER_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "emerald_to_emerald_dust",
            Items.EMERALD,
            ModItems.EMERALD_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "gold_ingot_to_gold_dust",
            Items.GOLD_INGOT,
            ModItems.GOLD_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "iron_ingot_to_iron_dust",
            Items.IRON_INGOT,
            ModItems.IRON_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "lapis_lazuli_to_lapis_dust",
            Items.LAPIS_LAZULI,
            ModItems.LAPIS_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "topaz_shard_to_topaz_dust",
            ModItems.TOPAZ_SHARD.get(),
            ModItems.TOPAZ_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "ruby_shard_to_ruby_dust",
            ModItems.RUBY_SHARD.get(),
            ModItems.RUBY_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "sapphire_shard_to_sapphire_dust",
            ModItems.SAPPHIRE_SHARD.get(),
            ModItems.SAPPHIRE_DUST.get(),
            100,
            1
        ),
        new CrusherRecipeDefinition(
            "coal_to_coal_dust",
            Items.COAL,
            ModItems.COAL_DUST.get(),
            100,
            1
        )
    };

    private static final CentrifugeRecipeDefinition[] COBBLESTONE_CENTRIFUGE_RECIPES = new CentrifugeRecipeDefinition[] {
        new CentrifugeRecipeDefinition(
            "tier_gold_cobblestone_dust_to_tier_gold_cobblestone_dirty_dust_and_dirty_mixture",
            ModItems.TIER_GOLD_COBBLESTONE_DUST.get(),
            new ItemStack(ModItems.TIER_GOLD_COBBLESTONE_DIRTY_DUST.get()),
            1.0F,
            new ItemStack(ModItems.DIRTY_MIXTURE.get()),
            0.3F,
            400,
            4
        )
    };

    private static final LaserDrillRecipeDefinition[] COBBLESTONE_LASER_DRILL_RECIPES = new LaserDrillRecipeDefinition[] {
        new LaserDrillRecipeDefinition(
            "amethyst_compressed_cobblestone_to_amethyst_shard_and_diamond",
            ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get(),
            new ItemStack(Items.AMETHYST_SHARD),
            0.5F,
            new ItemStack(Items.DIAMOND),
            0.05F,
            3200,
            16
        )
    };



    private static final ExtremeCompressorRecipeDefinition[] COBBLESTONE_EXTREME_COMPRESSOR_RECIPES = new ExtremeCompressorRecipeDefinition[] {
        new ExtremeCompressorRecipeDefinition(
            "obsidian_compressed_cobblestone_to_bedrock",
            ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock().get(),
            Items.BEDROCK,
            5000,
            409600,
            4096
        )
    };

    private static final MixerRecipeDefinition[] COBBLESTONE_MIXER_RECIPES = new MixerRecipeDefinition[] {
        new MixerRecipeDefinition(
            "tier_iron_cobblestone_dust_and_coal_dust_to_tier_iron_cobblestone_mixed_dust",
            new ItemStack(ModItems.TIER_IRON_COBBLESTONE_DUST.get()),
            new ItemStack(ModItems.COAL_DUST.get()),
            new ItemStack(ModItems.TIER_IRON_COBBLESTONE_MIXED_DUST.get()),
            200,
            2
        ),
        new MixerRecipeDefinition(
            "ancient_debris_dust_and_gold_dust_to_ancient_mixtures",
            new ItemStack(ModItems.ANCIENT_DEBRIS_DUST.get()),
            new ItemStack(ModItems.GOLD_DUST.get()),
            new ItemStack(ModItems.ANCIENT_MIXTURES.get(), 2),
            200,
            2
        ),
        new MixerRecipeDefinition(
            "aquamarine_dust_and_lapis_dust_to_aquamarine_mixture",
            new ItemStack(ModItems.AQUAMARINE_DUST.get()),
            new ItemStack(ModItems.LAPIS_DUST.get()),
            new ItemStack(ModItems.AQUAMARINE_MIXTURE.get(), 2),
            200,
            2
        ),
        new MixerRecipeDefinition(
            "blaze_powder_and_diamond_dust_to_blaze_mixtures",
            new ItemStack(Items.BLAZE_POWDER, 2),
            new ItemStack(ModItems.DIAMOND_DUST.get()),
            new ItemStack(ModItems.BLAZE_MIXTURES.get(), 3),
            200,
            2
        ),
        new MixerRecipeDefinition(
            "emerald_dust_and_ender_dust_to_emerald_mixtures",
            new ItemStack(ModItems.EMERALD_DUST.get()),
            new ItemStack(ModItems.ENDER_DUST.get()),
            new ItemStack(ModItems.EMERALD_MIXTURES.get(), 2),
            200,
            2
        ),
        new MixerRecipeDefinition(
            "ruby_dust_and_redstone_to_ruby_mixtures",
            new ItemStack(ModItems.RUBY_DUST.get()),
            new ItemStack(Items.REDSTONE),
            new ItemStack(ModItems.RUBY_MIXTURES.get(), 2),
            200,
            2
        ),
        new MixerRecipeDefinition(
            "sapphire_dust_and_diamond_dust_to_sapphire_mixture",
            new ItemStack(ModItems.SAPPHIRE_DUST.get()),
            new ItemStack(ModItems.DIAMOND_DUST.get()),
            new ItemStack(ModItems.SAPPHIRE_MIXTURE.get(), 2),
            200,
            2
        ),
        new MixerRecipeDefinition(
            "topaz_dust_and_glowstone_dust_to_topaz_mixtures",
            new ItemStack(ModItems.TOPAZ_DUST.get()),
            new ItemStack(Items.GLOWSTONE_DUST),
            new ItemStack(ModItems.TOPAZ_MIXTURES.get(), 2),
            200,
            2
        )
    };

    private static final MelterRecipeDefinition[] COBBLESTONE_MELTER_RECIPES = new MelterRecipeDefinition[] {
        new MelterRecipeDefinition(
            "tier_topaz_compressed_cobblestone_to_molten_tier_topaz_compressed_cobblestone",
            ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock().get(),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.TOPAZ.getFluidEntry().getStillFluid().get(), 1000),
            6400,
            64
        ),
        new MelterRecipeDefinition(
            "tier_diamond_compressed_cobblestone_to_molten_tier_diamond_compressed_cobblestone",
            ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock().get(),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            51200,
            512
        )
    };

    private static final DissolutionChamberRecipeDefinition[] COBBLESTONE_DISSOLUTION_CHAMBER_RECIPES = new DissolutionChamberRecipeDefinition[] {
        new DissolutionChamberRecipeDefinition(
            "tier_ruby_compressed_cobblestone_to_molten_tier_ruby_dirty_compressed_cobblestone",
            ModBlocks.TierCompressedCobblestone.RUBY.getBlock().get(),
            new FluidStack(net.minecraft.world.level.material.Fluids.LAVA, 100),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.RUBY.getFluidEntry().getStillFluid().get(), 1000),
            12800,
            128
        )
    };

    private static final ReactionChamberRecipeDefinition[] COBBLESTONE_REACTION_CHAMBER_RECIPES = new ReactionChamberRecipeDefinition[] {
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


    private static final CrystallizationChamberRecipeDefinition[] COBBLESTONE_CRYSTALLIZATION_CHAMBER_RECIPES = new CrystallizationChamberRecipeDefinition[] {
        new CrystallizationChamberRecipeDefinition(
            "molten_diamond_compressed_cobblestone_to_diamond_cobblestone_dust",
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            ModItems.TIER_DIAMOND_COBBLESTONE_DUST.get(),
            51200,
            512
        )
    };

    private static final ChemicalReactorRecipeDefinition[] COBBLESTONE_CHEMICAL_REACTOR_RECIPES = new ChemicalReactorRecipeDefinition[] {
        new ChemicalReactorRecipeDefinition(
            "redstone_and_emerald_diamond_fluids_to_obsidian",
            new ItemStack(Items.REDSTONE, 16),
            ItemStack.EMPTY,
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            new ItemStack(Items.OBSIDIAN),
            new ItemStack(ModItems.DIRTY_MIXTURE.get()),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.EMERALD.getFluidEntry().getStillFluid().get(), 1000),
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.DIAMOND.getFluidEntry().getStillFluid().get(), 1000),
            102400,
            1024
        )
    };

    private static final FluidMixerRecipeDefinition[] COBBLESTONE_FLUID_MIXER_RECIPES = new FluidMixerRecipeDefinition[] {
        new FluidMixerRecipeDefinition(
            "dirty_sapphire_and_water_to_sapphire",
            new FluidStack(ModFluids.TierMoltenDirtyCompressedCobblestone.SAPPHIRE.getFluidEntry().getStillFluid().get(), 100),
            new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 10000),
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.SAPPHIRE.getFluidEntry().getStillFluid().get(), 100),
            25600,
            256
        )
    };

    private static final AssemblyMachineRecipeDefinition[] COBBLESTONE_ASSEMBLY_MACHINE_RECIPES = new AssemblyMachineRecipeDefinition[] {
        new AssemblyMachineRecipeDefinition(
            "netherite_processor",
            new ItemStack(ModItems.TierCobblestoneCircuit.NETHERITE.getItem().get(), 4),
            new ItemStack(ModItems.TierCobblestoneCircuit.EMERALD.getItem().get(), 4),
            new ItemStack(ModItems.TierCobblestoneCircuitPackage.NETHERITE.getItem().get(), 1),
            new ItemStack(ModItems.TierCobblestoneWire.NETHERITE.getItem().get(), 16),
            new ItemStack(ModItems.TIER_NETHERITE_COBBLESTONE_GEM.get(), 16),
            ItemStack.EMPTY,
            new FluidStack(ModFluids.TierMoltenCompressedCobblestone.NETHERITE.getFluidEntry().getStillFluid().get(), 1000),
            new ItemStack(ModItems.TierCobblestoneProcessor.NETHERITE.getItem().get(), 1),
            204800,
            2048
        )
    };

    private static final MachineCasingRecipeDefinition[] MACHINE_CASING_RECIPES = new MachineCasingRecipeDefinition[] {
        new MachineCasingRecipeDefinition(
            "cobblestone_machine_casing",
            ModBlocks.COMPRESSED_COBBLESTONE.get(),
            ModBlocks.COMPRESSED_STONE.get(),
            ModItems.COBBLESTONE_GEM.get(),
            ModBlocks.COBBLESTONE_MACHINE_CASING.get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_copper_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.COPPER.getBlock().get(),
            ModBlocks.TierCompressedStone.COPPER.getBlock().get(),
            ModItems.TIER_COPPER_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.COPPER.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_iron_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.IRON.getBlock().get(),
            ModBlocks.TierCompressedStone.IRON.getBlock().get(),
            ModItems.TIER_IRON_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.IRON.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_gold_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.GOLD.getBlock().get(),
            ModBlocks.TierCompressedStone.GOLD.getBlock().get(),
            ModItems.TIER_GOLD_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.GOLD.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_amethyst_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get(),
            ModBlocks.TierCompressedStone.AMETHYST.getBlock().get(),
            ModItems.TIER_AMETHYST_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.AMETHYST.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_aquamarine_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock().get(),
            ModBlocks.TierCompressedStone.AQUAMARINE.getBlock().get(),
            ModItems.TIER_AQUAMARINE_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.AQUAMARINE.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_topaz_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock().get(),
            ModBlocks.TierCompressedStone.TOPAZ.getBlock().get(),
            ModItems.TIER_TOPAZ_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.TOPAZ.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_ruby_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.RUBY.getBlock().get(),
            ModBlocks.TierCompressedStone.RUBY.getBlock().get(),
            ModItems.TIER_RUBY_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.RUBY.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_sapphire_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock().get(),
            ModBlocks.TierCompressedStone.SAPPHIRE.getBlock().get(),
            ModItems.TIER_SAPPHIRE_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.SAPPHIRE.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_diamond_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock().get(),
            ModBlocks.TierCompressedStone.DIAMOND.getBlock().get(),
            ModItems.TIER_DIAMOND_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.DIAMOND.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_emerald_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.EMERALD.getBlock().get(),
            ModBlocks.TierCompressedStone.EMERALD.getBlock().get(),
            ModItems.TIER_EMERALD_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.EMERALD.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_netherite_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.NETHERITE.getBlock().get(),
            ModBlocks.TierCompressedStone.NETHERITE.getBlock().get(),
            ModItems.TIER_NETHERITE_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.NETHERITE.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_obsidian_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock().get(),
            ModBlocks.TierCompressedStone.OBSIDIAN.getBlock().get(),
            ModItems.TIER_OBSIDIAN_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.OBSIDIAN.getBlock().get()
        )
    };

    public ModRecipeProvider(PackOutput output, CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(@Nonnull RecipeOutput output) {
        // ここがレシピ datagen の実際の登録順です。
        // 前半は作業台で使う通常クラフト、後半は各機械が読む独自レシピです。
        // 追加先を迷ったら、まず「作業台でクラフトする物か」「機械内部で読む物か」をここで切り分けます。
        buildCompressedCobblestoneRecipes(output);
        buildCompressedCobblestoneSingularityRecipes(output);
        buildCobblestoneGeneratorRecipes(output);
        buildCobblestoneMachineCasingRecipes(output);
        buildCobblestoneTankRecipes(output);
        buildCobblestoneBreadRecipe(output);
        // buildGemRecipes(output); //gemはドロップによる獲得に変更するため、レシピを削除します。
        buildCobblestoneRodRecipes(output);
        buildCobblestoneMotorRecipes(output);
        buildCobblestoneAccelerationChipRecipes(output);
        buildCobblestoneEnergizedCubeRecipes(output);

        // ここから下は独自 RecipeType / RecipeSerializer を使う機械レシピです。
        // JSON の出力先は data/<modid>/recipe/<machine_name>/... になります。
        // 現在は登録済みの機械レシピをすべて datagen しています。
        // 新しい機械を追加したときは、この並びへ buildXxxRecipes(...) を足します。
        buildCobblestoneFurnaceRecipes(output);
        buildCobblestonePoweredFurnaceRecipes(output);
        buildCobblestoneExtremeCompressorRecipes(output);
        buildCobblestoneCrusherRecipes(output);
        buildCobblestoneCentrifugeRecipes(output);
        buildCobblestoneLaserDrillRecipes(output);
        buildCobblestoneMelterRecipes(output);
        buildCobblestoneAssemblyMachineRecipes(output);
        buildCobblestoneChemicalReactorRecipes(output);
        buildCobblestoneMixerRecipes(output);
        buildCobblestoneReactionChamberRecipes(output);
        buildCobblestoneDissolutionChamberRecipes(output);
        buildCobblestoneCrystallizationChamberRecipes(output);
        buildCobblestoneFluidMixerRecipes(output);
    }

    private void buildCobblestoneBreadRecipe(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_BREAD.get())
            .pattern("   ")
            .pattern("CCC")
            .pattern("SSS")
            .define('C', Items.COBBLESTONE)
            .define('S', Items.STONE)
            .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
            .save(output, modRecipeId("cobblestone_bread"));
    }

    private void buildCompressedCobblestoneRecipes(RecipeOutput output) {
        // 一番下の圧縮丸石だけは、ユーザー要望どおり 2x2 の丸石で作ります。
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COMPRESSED_COBBLESTONE.get())
            .pattern("CC")
            .pattern("CC")
            .define('C', Items.COBBLESTONE)
            .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
            .save(output, modRecipeId("compressed_cobblestone"));

        // 逆変換は単体クラフトで戻せるようにします。
        // 無印だけは元レシピが 2x2 なので、4 個の丸石へ戻します。
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.COBBLESTONE, 4)
            .requires(ModBlocks.COMPRESSED_COBBLESTONE.get())
            .unlockedBy("has_compressed_cobblestone", has(ModBlocks.COMPRESSED_COBBLESTONE.get()))
            .save(output, modRecipeId("compressed_cobblestone_to_cobblestone"));

        for (CompressionRecipeDefinition recipe : COMPRESSED_COBBLESTONE_RECIPES) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, recipe.result)
                .pattern("CCC")
                .pattern("CGC")
                .pattern("CCC")
                .define('C', recipe.outerBlock)
                .define('G', recipe.centerGem)
                .unlockedBy("has_" + recipe.recipeName, has(recipe.centerGem))
                .save(output, modRecipeId(recipe.recipeName));

            // 各 tier の圧縮丸石は、1 個から前の tier 8 個へ戻せるようにします。
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, recipe.outerBlock, 8)
                .requires(recipe.result)
                .unlockedBy("has_" + recipe.recipeName, has(recipe.result))
                .save(output, modRecipeId(recipe.recipeName + "_to_previous_tier"));
        }
    }

    /* gemはドロップによる獲得に変更します
    private void buildGemRecipes(RecipeOutput output) {
        // 通常 gem は flint を核にして作る仮レシピです。
        // tier 側はすべて同じ見た目の recipe ですが、素材行は個別に分かれているので、
        // 1 tier だけ別の材料や pattern へ変えたい時もここから追えます。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_GEM.get())
            .pattern(" C ")
            .pattern("CFC")
            .pattern(" C ")
            .define('C', Items.COBBLESTONE)
            .define('F', Items.FLINT)
            .unlockedBy("has_flint", has(Items.FLINT))
            .save(output, modRecipeId("cobblestone_gem"));

        for (GemRecipeDefinition recipe : GEM_RECIPES) {
            if ("cobblestone_gem".equals(recipe.recipeName)) {
                continue;
            }

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, recipe.result)
                .pattern(" M ")
                .pattern("MGM")
                .pattern(" M ")
                .define('M', recipe.material)
                .define('G', ModItems.COBBLESTONE_GEM.get())
                .unlockedBy("has_" + recipe.recipeName, has(recipe.material))
                .save(output, modRecipeId(recipe.recipeName));
        }
    }*/

    private void buildCompressedCobblestoneSingularityRecipes(RecipeOutput output) {
        // bit は通常の丸石 gem、tier 版は同じ tier の丸石 gem を 8 個外周へ並べて作ります。
        // fragment と singularity も同じ「8 個を外周へ置く」見た目でそろえ、
        // bit -> fragment -> singularity の段階がレシピ形状から追いやすいようにします。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_BIT.get())
            .pattern("CCC")
            .pattern("C C")
            .pattern("CCC")
            .define('C', ModItems.COBBLESTONE_GEM.get())
            .unlockedBy(
                "has_cobblestone_gem",
                has(ModItems.COBBLESTONE_GEM.get())
            )
            .save(output, modRecipeId("compressed_cobblestone_singularity_bit"));

        for (ModItems.TierCompressedCobblestoneSingularityBit tier : ModItems.TierCompressedCobblestoneSingularityBit.values()) {
            ItemLike cobblestoneGem = ModItems.TierCobblestoneGem.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("CCC")
                .pattern("C C")
                .pattern("CCC")
                .define('C', cobblestoneGem)
                .unlockedBy("has_" + ModItems.TierCobblestoneGem.valueOf(tier.name()).getRegistryName(), has(cobblestoneGem))
                .save(output, modRecipeId(tier.getRegistryName()));
        }

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT.get())
            .pattern("BBB")
            .pattern("B B")
            .pattern("BBB")
            .define('B', ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_BIT.get())
            .unlockedBy(
                "has_compressed_cobblestone_singularity_bit",
                has(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_BIT.get())
            )
            .save(output, modRecipeId("compressed_cobblestone_singularity_fragment"));

        for (ModItems.TierCompressedCobblestoneSingularityFragment tier : ModItems.TierCompressedCobblestoneSingularityFragment.values()) {
            ItemLike singularityBit = ModItems.TierCompressedCobblestoneSingularityBit.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("BBB")
                .pattern("B B")
                .pattern("BBB")
                .define('B', singularityBit)
                .unlockedBy("has_" + ModItems.TierCompressedCobblestoneSingularityBit.valueOf(tier.name()).getRegistryName(), has(singularityBit))
                .save(output, modRecipeId(tier.getRegistryName()));
        }

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COMPRESSED_COBBLESTONE_SINGULARITY.get())
            .pattern("FFF")
            .pattern("F F")
            .pattern("FFF")
            .define('F', ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT.get())
            .unlockedBy(
                "has_compressed_cobblestone_singularity_fragment",
                has(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT.get())
            )
            .save(output, modRecipeId("compressed_cobblestone_singularity"));

        for (ModItems.TierCompressedCobblestoneSingularity tier : ModItems.TierCompressedCobblestoneSingularity.values()) {
            ItemLike singularityFragment = ModItems.TierCompressedCobblestoneSingularityFragment.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("FFF")
                .pattern("F F")
                .pattern("FFF")
                .define('F', singularityFragment)
                .unlockedBy("has_" + ModItems.TierCompressedCobblestoneSingularityFragment.valueOf(tier.name()).getRegistryName(), has(singularityFragment))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneRodRecipes(RecipeOutput output) {
        // 通常版ロッドは通常の圧縮丸石 2 個から 2 本作ります。
        // tier 版も同じ見た目で、対応する tier の圧縮丸石だけを差し替えます。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_ROD.get(), 2)
            .pattern("C")
            .pattern("C")
            .define('C', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .unlockedBy("has_compressed_cobblestone", has(ModItems.COMPRESSED_COBBLESTONE_ITEM.get()))
            .save(output, modRecipeId("cobblestone_rod"));

        for (ModItems.TierCobblestoneRod tier : ModItems.TierCobblestoneRod.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get(), 2)
                .pattern("C")
                .pattern("C")
                .define('C', compressedCobblestone)
                .unlockedBy("has_" + tier.getRegistryName(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneMotorRecipes(RecipeOutput output) {
        // モーターは対応する圧縮丸石・wire・rod を組み合わせて 2 個作ります。
        // tier 版は enum 名をそろえてあるので、その名前から同じ tier の素材を引けます。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_MOTOR.get(), 2)
            .pattern("CWR")
            .pattern("WRW")
            .pattern("RWC")
            .define('C', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .define('W', ModItems.COBBLESTONE_WIRE.get())
            .define('R', ModItems.COBBLESTONE_ROD.get())
            .unlockedBy("has_cobblestone_rod", has(ModItems.COBBLESTONE_ROD.get()))
            .save(output, modRecipeId("cobblestone_motor"));

        for (ModItems.TierCobblestoneMotor tier : ModItems.TierCobblestoneMotor.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();
            ItemLike cobblestoneWire = ModItems.TierCobblestoneWire.valueOf(tier.name()).getItem().get();
            ItemLike cobblestoneRod = ModItems.TierCobblestoneRod.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get(), 2)
                .pattern("CWR")
                .pattern("WRW")
                .pattern("RWC")
                .define('C', compressedCobblestone)
                .define('W', cobblestoneWire)
                .define('R', cobblestoneRod)
                .unlockedBy("has_" + tier.getRegistryName(), has(cobblestoneRod))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneAccelerationChipRecipes(RecipeOutput output) {
        // Acceleration Chip は、外周を圧縮丸石で囲み、中央へ motor を置く 3x3 レシピです。
        // tier 版も同じ見た目のまま、対応する tier の圧縮丸石と motor に差し替えます。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_ACCELERATION_CHIP.get())
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .define('A', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .define('B', ModItems.COBBLESTONE_MOTOR.get())
            .unlockedBy("has_cobblestone_motor", has(ModItems.COBBLESTONE_MOTOR.get()))
            .save(output, modRecipeId("cobblestone_acceleration_chip"));

        for (ModItems.TierCobblestoneAccelerationChip tier : ModItems.TierCobblestoneAccelerationChip.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();
            ItemLike cobblestoneMotor = ModItems.TierCobblestoneMotor.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', compressedCobblestone)
                .define('B', cobblestoneMotor)
                .unlockedBy("has_" + tier.getRegistryName(), has(cobblestoneMotor))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneEnergizedCubeRecipes(RecipeOutput output) {
        // Energized Cube は、外周を圧縮丸石で囲み、中央へ processor を置く 3x3 レシピです。
        // tier 版は enum 名から同じ tier の圧縮丸石と processor を引いて、そのまま回します。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_ENERGIZED_CUBE.get())
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .define('A', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .define('B', ModItems.COBBLESTONE_PROCESSOR.get())
            .unlockedBy("has_cobblestone_processor", has(ModItems.COBBLESTONE_PROCESSOR.get()))
            .save(output, modRecipeId("cobblestone_energized_cube"));

        for (ModItems.TierCobblestoneEnergizedCube tier : ModItems.TierCobblestoneEnergizedCube.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();
            ItemLike cobblestoneProcessor = ModItems.TierCobblestoneProcessor.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', compressedCobblestone)
                .define('B', cobblestoneProcessor)
                .unlockedBy("has_" + tier.getRegistryName(), has(cobblestoneProcessor))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneGeneratorRecipes(RecipeOutput output) {
        for (ModBlocks.TierCobblestoneGenerator generatorVariant : ModBlocks.TierCobblestoneGenerator.values()) {
            ItemLike result = ModItems.TierCobblestoneGeneratorItem.from(generatorVariant).getItem().get();

            if (generatorVariant.getSize() == ModBlocks.CobblestoneGeneratorSize.S) {
                ItemLike compressedCobblestone;
                ItemLike singularity;

                if (generatorVariant.hasTier()) {
                    compressedCobblestone = generatorVariant.getTier().getBlock().get();
                    singularity = ModItems.TierCompressedCobblestoneSingularity.valueOf(generatorVariant.getTier().name()).getItem().get();
                } else {
                    compressedCobblestone = ModBlocks.COMPRESSED_COBBLESTONE.get();
                    singularity = ModItems.COMPRESSED_COBBLESTONE_SINGULARITY.get();
                }

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                    .pattern("CCC")
                    .pattern("CSC")
                    .pattern("CCC")
                    .define('C', compressedCobblestone)
                    .define('S', singularity)
                    .unlockedBy("has_" + generatorVariant.getRegistryName(), has(singularity))
                    .save(output, modRecipeId(generatorVariant.getRegistryName()));

                continue;
            }

            ModBlocks.CobblestoneGeneratorSize previousSize = generatorVariant.getSize() == ModBlocks.CobblestoneGeneratorSize.M
                ? ModBlocks.CobblestoneGeneratorSize.S
                : ModBlocks.CobblestoneGeneratorSize.M;
            ModBlocks.TierCobblestoneGenerator previousVariant = ModBlocks.TierCobblestoneGenerator.from(generatorVariant.getTier(), previousSize);
            ItemLike previousGenerator = ModItems.TierCobblestoneGeneratorItem.from(previousVariant).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .pattern("GGG")
                .pattern("G G")
                .pattern("GGG")
                .define('G', previousGenerator)
                .unlockedBy("has_" + previousVariant.getRegistryName(), has(previousGenerator))
                .save(output, modRecipeId(generatorVariant.getRegistryName()));
        }
    }

    private void buildCobblestoneMachineCasingRecipes(RecipeOutput output) {
        for (MachineCasingRecipeDefinition recipe : MACHINE_CASING_RECIPES) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, recipe.result)
                .pattern("CSC")
                .pattern("SGS")
                .pattern("CSC")
                .define('C', recipe.compressedCobblestone)
                .define('S', recipe.compressedStone)
                .define('G', recipe.centerGem)
                .unlockedBy("has_" + recipe.recipeName, has(recipe.centerGem))
                .save(output, modRecipeId(recipe.recipeName));
        }
    }

    private void buildCobblestoneTankRecipes(RecipeOutput output) {
        // Cobblestone Tank は中央にガラス、外周に同じ tier の圧縮丸石を 8 個並べるだけのシンプルなレシピです。
        // tier 版も enum 名をそろえてあるので、対応する圧縮丸石とタンクをそのまま引き当てられます。
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_TANK.get())
            .pattern("CCC")
            .pattern("CGC")
            .pattern("CCC")
            .define('C', ModBlocks.COMPRESSED_COBBLESTONE.get())
            .define('G', Items.GLASS)
            .unlockedBy("has_cobblestone_tank_material", has(ModBlocks.COMPRESSED_COBBLESTONE.get()))
            .save(output, modRecipeId("cobblestone_tank"));

        for (ModBlocks.TierCobblestoneTank tier : ModBlocks.TierCobblestoneTank.values()) {
            ItemLike compressedCobblestone = ModBlocks.TierCompressedCobblestone.valueOf(tier.name()).getBlock().get();
            ItemLike result = ModItems.TierCobblestoneTankItem.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .pattern("CCC")
                .pattern("CGC")
                .pattern("CCC")
                .define('C', compressedCobblestone)
                .define('G', Items.GLASS)
                .unlockedBy("has_" + tier.getRegistryName(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneFurnaceRecipes(RecipeOutput output) {
        // Furnace 系は ItemLike 1 個入力 + ItemStack 1 個出力 + 処理時間、という
        // 一番単純な構成なので、最初に機械レシピを増やす時の見本として使いやすいです。
        // 既存の cobblestone -> stone 例も datagen 管理へ寄せます。
        saveCobblestoneFurnaceRecipe(output, "cobblestone_to_stone", Items.COBBLESTONE, Items.STONE, 100);

        for (MachineRecipeDefinition recipe : COMPRESSED_STONE_MACHINE_RECIPES) {
            saveCobblestoneFurnaceRecipe(output, recipe.recipeName, recipe.ingredient, recipe.result, recipe.processingTime);
        }
    }

    private void buildCobblestonePoweredFurnaceRecipes(RecipeOutput output) {
        // Powered Furnace も Furnace と同じ 1 入力 1 出力ですが、
        // 処理時間の代わりに総消費 CP と tick ごとの消費 CP を持ちます。
        // 既存の Furnace と並べて見比べやすいよう、まずは同じ材料・出力を流し、
        // 処理時間 100 tick は total CP 100 / 1 tick あたり 1 CP として扱います。
        saveCobblestonePoweredFurnaceRecipe(output, "cobblestone_to_stone", Items.COBBLESTONE, Items.STONE, 100, 1);

        for (MachineRecipeDefinition recipe : COMPRESSED_STONE_MACHINE_RECIPES) {
            saveCobblestonePoweredFurnaceRecipe(
                output,
                recipe.recipeName,
                recipe.ingredient,
                recipe.result,
                recipe.processingTime,
                1
            );
        }
    }

    private void buildCobblestoneExtremeCompressorRecipes(RecipeOutput output) {
        for (ExtremeCompressorRecipeDefinition recipe : COBBLESTONE_EXTREME_COMPRESSOR_RECIPES) {
            saveCobblestoneExtremeCompressorRecipe(
                output,
                recipe.recipeName,
                recipe.ingredient,
                recipe.result,
                recipe.requiredItemCount,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private void buildCobblestoneCrusherRecipes(RecipeOutput output) {
        for (CrusherRecipeDefinition recipe : COBBLESTONE_CRUSHER_RECIPES) {
            saveCobblestoneCrusherRecipe(
                output,
                recipe.recipeName,
                recipe.ingredient,
                recipe.result,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private void buildCobblestoneCentrifugeRecipes(RecipeOutput output) {
        for (CentrifugeRecipeDefinition recipe : COBBLESTONE_CENTRIFUGE_RECIPES) {
            saveCobblestoneCentrifugeRecipe(
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

    private void buildCobblestoneLaserDrillRecipes(RecipeOutput output) {
        for (LaserDrillRecipeDefinition recipe : COBBLESTONE_LASER_DRILL_RECIPES) {
            saveCobblestoneLaserDrillRecipe(
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

    private void buildCobblestoneMixerRecipes(RecipeOutput output) {
        for (MixerRecipeDefinition recipe : COBBLESTONE_MIXER_RECIPES) {
            saveCobblestoneMixerRecipe(
                output,
                recipe.recipeName,
                recipe.firstIngredient,
                recipe.secondIngredient,
                recipe.result,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private void buildCobblestoneMelterRecipes(RecipeOutput output) {
        for (MelterRecipeDefinition recipe : COBBLESTONE_MELTER_RECIPES) {
            saveCobblestoneMelterRecipe(
                output,
                recipe.recipeName,
                recipe.ingredient,
                recipe.fluidResult,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private void buildCobblestoneChemicalReactorRecipes(RecipeOutput output) {
        for (ChemicalReactorRecipeDefinition recipe : COBBLESTONE_CHEMICAL_REACTOR_RECIPES) {
            saveCobblestoneChemicalReactorRecipe(
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

    private void buildCobblestoneReactionChamberRecipes(RecipeOutput output) {
        for (ReactionChamberRecipeDefinition recipe : COBBLESTONE_REACTION_CHAMBER_RECIPES) {
            saveCobblestoneReactionChamberRecipe(
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

    private void buildCobblestoneAssemblyMachineRecipes(RecipeOutput output) {
        for (AssemblyMachineRecipeDefinition recipe : COBBLESTONE_ASSEMBLY_MACHINE_RECIPES) {
            saveCobblestoneAssemblyMachineRecipe(
                output,
                recipe.recipeName,
                recipe.firstItemInput,
                recipe.secondItemInput,
                recipe.thirdItemInput,
                recipe.fourthItemInput,
                recipe.fifthItemInput,
                recipe.sixthItemInput,
                recipe.fluidInput,
                recipe.resultItem,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private void buildCobblestoneDissolutionChamberRecipes(RecipeOutput output) {
        for (DissolutionChamberRecipeDefinition recipe : COBBLESTONE_DISSOLUTION_CHAMBER_RECIPES) {
            saveCobblestoneDissolutionChamberRecipe(
                output,
                recipe.recipeName,
                recipe.ingredient,
                recipe.fluidInput,
                recipe.fluidOutput,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private void buildCobblestoneCrystallizationChamberRecipes(RecipeOutput output) {
        for (CrystallizationChamberRecipeDefinition recipe : COBBLESTONE_CRYSTALLIZATION_CHAMBER_RECIPES) {
            saveCobblestoneCrystallizationChamberRecipe(
                output,
                recipe.recipeName,
                recipe.fluidInput,
                recipe.result,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private void buildCobblestoneFluidMixerRecipes(RecipeOutput output) {
        for (FluidMixerRecipeDefinition recipe : COBBLESTONE_FLUID_MIXER_RECIPES) {
            saveCobblestoneFluidMixerRecipe(
                output,
                recipe.recipeName,
                recipe.firstFluidInput,
                recipe.secondFluidInput,
                recipe.fluidOutput,
                recipe.totalCobblestonePower,
                recipe.cobblestonePowerPerTick
            );
        }
    }

    private void saveCobblestoneFurnaceRecipe(RecipeOutput output, String recipeName, ItemLike ingredient, ItemLike result, int processingTime) {
        // output.accept(...) へ流した時点で、実体の JSON は generated/resources 側へ書き出されます。
        // 機械レシピは builder を使わず、Recipe インスタンスをそのまま渡すのがこのプロジェクトの流れです。
        CobblestoneFurnaceRecipe recipe = new CobblestoneFurnaceRecipe(
            Ingredient.of(ingredient),
            new ItemStack(result),
            processingTime
        );

        output.accept(
            modRecipeId("cobblestone_furnace/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestonePoweredFurnaceRecipe(
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

        output.accept(
            modRecipeId("cobblestone_powered_furnace/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneCrusherRecipe(
        RecipeOutput output,
        String recipeName,
        ItemLike ingredient,
        ItemLike result,
        int totalCobblestonePower,
        int cobblestonePowerPerTick
    ) {
        CobblestoneCrusherRecipe recipe = new CobblestoneCrusherRecipe(
            Ingredient.of(ingredient),
            new ItemStack(result),
            totalCobblestonePower,
            cobblestonePowerPerTick
        );

        output.accept(
            modRecipeId("cobblestone_crusher/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneCentrifugeRecipe(
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

        output.accept(
            modRecipeId("cobblestone_centrifuge/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneLaserDrillRecipe(
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

        output.accept(
            modRecipeId("cobblestone_laser_drill/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneExtremeCompressorRecipe(
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

        output.accept(
            modRecipeId("cobblestone_extreme_compressor/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneMixerRecipe(
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

        output.accept(
            modRecipeId("cobblestone_mixer/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneMelterRecipe(
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

        output.accept(
            modRecipeId("cobblestone_melter/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneChemicalReactorRecipe(
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

        output.accept(
            modRecipeId("cobblestone_chemical_reactor/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneReactionChamberRecipe(
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

        output.accept(
            modRecipeId("cobblestone_reaction_chamber/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneAssemblyMachineRecipe(
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

        output.accept(
            modRecipeId("cobblestone_assembly_machine/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneDissolutionChamberRecipe(
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

        output.accept(
            modRecipeId("cobblestone_dissolution_chamber/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneCrystallizationChamberRecipe(
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

        output.accept(
            modRecipeId("cobblestone_crystallization_chamber/" + recipeName),
            recipe,
            null
        );
    }

    private void saveCobblestoneFluidMixerRecipe(
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

        output.accept(
            modRecipeId("cobblestone_fluid_mixer/" + recipeName),
            recipe,
            null
        );
    }

    private ResourceLocation modRecipeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, path);
    }

    /* gemはドロップによる獲得に変更するため、レシピ定義を削除します。
    private static class GemRecipeDefinition {
        private final String recipeName;
        private final ItemLike material;
        private final ItemLike result;

        private GemRecipeDefinition(String recipeName, ItemLike material, ItemLike result) {
            this.recipeName = recipeName;
            this.material = material;
            this.result = result;
        }
    }*/

    private static class CompressionRecipeDefinition {
        private final String recipeName;
        private final ItemLike outerBlock;
        private final ItemLike centerGem;
        private final ItemLike result;

        private CompressionRecipeDefinition(String recipeName, ItemLike outerBlock, ItemLike centerGem, ItemLike result) {
            this.recipeName = recipeName;
            this.outerBlock = outerBlock;
            this.centerGem = centerGem;
            this.result = result;
        }
    }

    private static class MachineRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemLike result;
        private final int processingTime;

        private MachineRecipeDefinition(String recipeName, ItemLike ingredient, ItemLike result, int processingTime) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.result = result;
            this.processingTime = processingTime;
        }
    }

    private static class MachineCasingRecipeDefinition {
        private final String recipeName;
        private final ItemLike compressedCobblestone;
        private final ItemLike compressedStone;
        private final ItemLike centerGem;
        private final ItemLike result;

        private MachineCasingRecipeDefinition(
            String recipeName,
            ItemLike compressedCobblestone,
            ItemLike compressedStone,
            ItemLike centerGem,
            ItemLike result
        ) {
            this.recipeName = recipeName;
            this.compressedCobblestone = compressedCobblestone;
            this.compressedStone = compressedStone;
            this.centerGem = centerGem;
            this.result = result;
        }
    }

    private static class CrusherRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemLike result;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private CrusherRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            ItemLike result,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.result = result;
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }

    private static class CentrifugeRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemStack firstResult;
        private final float firstResultChance;
        private final ItemStack secondResult;
        private final float secondResultChance;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private CentrifugeRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            ItemStack firstResult,
            float firstResultChance,
            ItemStack secondResult,
            float secondResultChance,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
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

    private static class LaserDrillRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemStack firstResult;
        private final float firstResultChance;
        private final ItemStack secondResult;
        private final float secondResultChance;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private LaserDrillRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            ItemStack firstResult,
            float firstResultChance,
            ItemStack secondResult,
            float secondResultChance,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
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

    private static class ExtremeCompressorRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final ItemLike result;
        private final int requiredItemCount;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private ExtremeCompressorRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            ItemLike result,
            int requiredItemCount,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.result = result;
            this.requiredItemCount = requiredItemCount;
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }

    private static class MixerRecipeDefinition {
        private final String recipeName;
        private final ItemStack firstIngredient;
        private final ItemStack secondIngredient;
        private final ItemStack result;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private MixerRecipeDefinition(
            String recipeName,
            ItemStack firstIngredient,
            ItemStack secondIngredient,
            ItemStack result,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.firstIngredient = firstIngredient.copy();
            this.secondIngredient = secondIngredient.copy();
            this.result = result.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }

    private static class MelterRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final FluidStack fluidResult;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private MelterRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            FluidStack fluidResult,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.fluidResult = fluidResult.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }

    private static class DissolutionChamberRecipeDefinition {
        private final String recipeName;
        private final ItemLike ingredient;
        private final FluidStack fluidInput;
        private final FluidStack fluidOutput;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private DissolutionChamberRecipeDefinition(
            String recipeName,
            ItemLike ingredient,
            FluidStack fluidInput,
            FluidStack fluidOutput,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.ingredient = ingredient;
            this.fluidInput = fluidInput.copy();
            this.fluidOutput = fluidOutput.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
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

        private ReactionChamberRecipeDefinition(
            String recipeName,
            FluidStack fluidInput,
            ItemLike firstIngredient,
            ItemLike secondIngredient,
            ItemStack result,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.fluidInput = fluidInput.copy();
            this.firstIngredient = firstIngredient;
            this.secondIngredient = secondIngredient;
            this.result = result.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }

    private static class CrystallizationChamberRecipeDefinition {
        private final String recipeName;
        private final FluidStack fluidInput;
        private final ItemLike result;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private CrystallizationChamberRecipeDefinition(
            String recipeName,
            FluidStack fluidInput,
            ItemLike result,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.fluidInput = fluidInput.copy();
            this.result = result;
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }

    private static class ChemicalReactorRecipeDefinition {
        private final String recipeName;
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

        private ChemicalReactorRecipeDefinition(
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
            this.recipeName = recipeName;
            this.firstItemInput = firstItemInput.copy();
            this.secondItemInput = secondItemInput.copy();
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

    private static class AssemblyMachineRecipeDefinition {
        private final String recipeName;
        private final ItemStack firstItemInput;
        private final ItemStack secondItemInput;
        private final ItemStack thirdItemInput;
        private final ItemStack fourthItemInput;
        private final ItemStack fifthItemInput;
        private final ItemStack sixthItemInput;
        private final FluidStack fluidInput;
        private final ItemStack resultItem;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private AssemblyMachineRecipeDefinition(
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
            this.recipeName = recipeName;
            this.firstItemInput = firstItemInput.copy();
            this.secondItemInput = secondItemInput.copy();
            this.thirdItemInput = thirdItemInput.copy();
            this.fourthItemInput = fourthItemInput.copy();
            this.fifthItemInput = fifthItemInput.copy();
            this.sixthItemInput = sixthItemInput.copy();
            this.fluidInput = fluidInput.copy();
            this.resultItem = resultItem.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }

    private static class FluidMixerRecipeDefinition {
        private final String recipeName;
        private final FluidStack firstFluidInput;
        private final FluidStack secondFluidInput;
        private final FluidStack fluidOutput;
        private final int totalCobblestonePower;
        private final int cobblestonePowerPerTick;

        private FluidMixerRecipeDefinition(
            String recipeName,
            FluidStack firstFluidInput,
            FluidStack secondFluidInput,
            FluidStack fluidOutput,
            int totalCobblestonePower,
            int cobblestonePowerPerTick
        ) {
            this.recipeName = recipeName;
            this.firstFluidInput = firstFluidInput.copy();
            this.secondFluidInput = secondFluidInput.copy();
            this.fluidOutput = fluidOutput.copy();
            this.totalCobblestonePower = totalCobblestonePower;
            this.cobblestonePowerPerTick = cobblestonePowerPerTick;
        }
    }
}