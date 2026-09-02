package com.yukke9265.cobblestone_xx_compressed.datagen.recipe;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;
import com.yukke9265.cobblestone_xx_compressed.util.EfficiencyEnchantedBookHelper;
import com.yukke9265.cobblestone_xx_compressed.util.FortuneEnchantedBookHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

@SuppressWarnings("null")
public class ModRecipeProvider extends RecipeProvider {
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

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
    // 機械固有の datagen は、機械ごとの専用クラスへ移しました。
    // 入口である ModRecipeProvider には、作業台レシピと共通の進行順だけを残します。

    private static final MachineCasingRecipeDefinition[] MACHINE_CASING_RECIPES = new MachineCasingRecipeDefinition[] {
        new MachineCasingRecipeDefinition(
            "cobblestone_machine_casing",
            ModBlocks.COMPRESSED_COBBLESTONE.get(),
            Items.FLINT,
            ModBlocks.COBBLESTONE_MACHINE_CASING.get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_copper_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.COPPER.getBlock().get(),
            ModItems.COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.COPPER.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_iron_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.IRON.getBlock().get(),
            ModItems.TIER_COPPER_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.IRON.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_gold_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.GOLD.getBlock().get(),
            ModItems.TIER_IRON_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.GOLD.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_amethyst_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get(),
            ModItems.TIER_GOLD_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.AMETHYST.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_aquamarine_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock().get(),
            ModItems.TIER_AMETHYST_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.AQUAMARINE.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_topaz_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock().get(),
            ModItems.TIER_AQUAMARINE_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.TOPAZ.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_ruby_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.RUBY.getBlock().get(),
            ModItems.TIER_TOPAZ_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.RUBY.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_sapphire_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock().get(),
            ModItems.TIER_RUBY_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.SAPPHIRE.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_diamond_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock().get(),
            ModItems.TIER_SAPPHIRE_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.DIAMOND.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_emerald_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.EMERALD.getBlock().get(),
            ModItems.TIER_DIAMOND_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.EMERALD.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_netherite_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.NETHERITE.getBlock().get(),
            ModItems.TIER_EMERALD_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.NETHERITE.getBlock().get()
        ),
        new MachineCasingRecipeDefinition(
            "tier_obsidian_cobblestone_machine_casing",
            ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock().get(),
            ModItems.TIER_NETHERITE_COBBLESTONE_GEM.get(),
            ModBlocks.TierCobblestoneMachineCasing.OBSIDIAN.getBlock().get()
        )
    };

    // 各 tier の機械本体クラフト対応表です。
    // ユーザー指定の tier 順と既存の登録名をここで 1 対 1 に固定しておくと、
    // 後から対象機械を差し替える時もこの配列だけ見れば追えます。
    private static final MachineBlockRecipeDefinition[] MACHINE_BLOCK_RECIPES = new MachineBlockRecipeDefinition[] {
        new MachineBlockRecipeDefinition(
            "cobblestone_powered_furnace",
            ModBlocks.COMPRESSED_COBBLESTONE.get(),
            ModItems.COBBLESTONE_MOTOR.get(),
            ModBlocks.COBBLESTONE_MACHINE_CASING.get(),
            ModItems.COBBLESTONE_PROCESSOR.get(),
            ModBlocks.COBBLESTONE_POWERED_FURNACE.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_crusher",
            ModBlocks.TierCompressedCobblestone.COPPER.getBlock().get(),
            ModItems.TIER_COPPER_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.COPPER.getBlock().get(),
            ModItems.TierCobblestoneProcessor.COPPER.getItem().get(),
            ModBlocks.COBBLESTONE_CRUSHER.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_mixer",
            ModBlocks.TierCompressedCobblestone.IRON.getBlock().get(),
            ModItems.TIER_IRON_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.IRON.getBlock().get(),
            ModItems.TierCobblestoneProcessor.IRON.getItem().get(),
            ModBlocks.COBBLESTONE_MIXER.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_powered_crafter",
            ModBlocks.TierCompressedCobblestone.IRON.getBlock().get(),
            ModItems.TIER_IRON_COBBLESTONE_MOTOR.get(),
            Blocks.CRAFTING_TABLE,
            ModItems.TierCobblestoneProcessor.IRON.getItem().get(),
            ModBlocks.COBBLESTONE_POWERED_CRAFTER.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_centrifuge",
            ModBlocks.TierCompressedCobblestone.GOLD.getBlock().get(),
            ModItems.TIER_GOLD_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.GOLD.getBlock().get(),
            ModItems.TierCobblestoneProcessor.GOLD.getItem().get(),
            ModBlocks.COBBLESTONE_CENTRIFUGE.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_laser_drill",
            ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get(),
            ModItems.TIER_AMETHYST_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.AMETHYST.getBlock().get(),
            ModItems.TierCobblestoneProcessor.AMETHYST.getItem().get(),
            ModBlocks.COBBLESTONE_LASER_DRILL.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_reaction_chamber",
            ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock().get(),
            ModItems.TIER_AQUAMARINE_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.AQUAMARINE.getBlock().get(),
            ModItems.TierCobblestoneProcessor.AQUAMARINE.getItem().get(),
            ModBlocks.COBBLESTONE_REACTION_CHAMBER.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_melter",
            ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock().get(),
            ModItems.TIER_TOPAZ_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.TOPAZ.getBlock().get(),
            ModItems.TierCobblestoneProcessor.TOPAZ.getItem().get(),
            ModBlocks.COBBLESTONE_MELTER.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_dissolution_chamber",
            ModBlocks.TierCompressedCobblestone.RUBY.getBlock().get(),
            ModItems.TIER_RUBY_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.RUBY.getBlock().get(),
            ModItems.TierCobblestoneProcessor.RUBY.getItem().get(),
            ModBlocks.COBBLESTONE_DISSOLUTION_CHAMBER.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_fluid_mixer",
            ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock().get(),
            ModItems.TIER_SAPPHIRE_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.SAPPHIRE.getBlock().get(),
            ModItems.TierCobblestoneProcessor.SAPPHIRE.getItem().get(),
            ModBlocks.COBBLESTONE_FLUID_MIXER.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_crystallization_chamber",
            ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock().get(),
            ModItems.TIER_DIAMOND_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.DIAMOND.getBlock().get(),
            ModItems.TierCobblestoneProcessor.DIAMOND.getItem().get(),
            ModBlocks.COBBLESTONE_CRYSTALLIZATION_CHAMBER.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_chemical_reactor",
            ModBlocks.TierCompressedCobblestone.EMERALD.getBlock().get(),
            ModItems.TIER_EMERALD_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.EMERALD.getBlock().get(),
            ModItems.TierCobblestoneProcessor.EMERALD.getItem().get(),
            ModBlocks.COBBLESTONE_CHEMICAL_REACTOR.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_assembly_machine",
            ModBlocks.TierCompressedCobblestone.NETHERITE.getBlock().get(),
            ModItems.TIER_NETHERITE_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.NETHERITE.getBlock().get(),
            ModItems.TierCobblestoneProcessor.NETHERITE.getItem().get(),
            ModBlocks.COBBLESTONE_ASSEMBLY_MACHINE.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_extreme_compressor",
            ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock().get(),
            ModItems.TIER_OBSIDIAN_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.OBSIDIAN.getBlock().get(),
            ModItems.TierCobblestoneProcessor.OBSIDIAN.getItem().get(),
            ModBlocks.COBBLESTONE_EXTREME_COMPRESSOR.get()
        ),
        new MachineBlockRecipeDefinition(
            "cobblestone_multiblock_crusher",
            ModBlocks.TierCompressedCobblestone.IRON.getBlock().get(),
            ModItems.TIER_IRON_COBBLESTONE_MOTOR.get(),
            ModBlocks.TierCobblestoneMachineCasing.IRON.getBlock().get(),
            ModItems.COBBLESTONE_CRUSHER_ITEM.get(),
            ModBlocks.COBBLESTONE_MULTIBLOCK_CRUSHER.get()
        )
    };

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
        this.lookupProvider = lookupProvider;
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
        buildCobblestoneDrawerRecipes(output);
        buildCobblestoneFeCubeRecipes(output);
        buildCobblestoneBreadRecipe(output);
        buildFortuneEnchantedBookRecipes(output);
        buildEfficiencyEnchantedBookRecipes(output);
        // buildGemRecipes(output); //gemはドロップによる獲得に変更するため、レシピを削除します。
        buildCobblestoneRodRecipes(output);
        buildCobblestonePickaxeRecipes(output);
        buildCobblestoneArmorRecipes(output);
        buildCobblestoneWireRecipes(output);
        buildCobblestoneCircuitRecipes(output);
        buildCobblestoneCircuitPackageRecipes(output);
        buildCobblestoneProcessorRecipes(output);
        buildCobblestoneMotorRecipes(output);
        buildCobblestoneAccelerationChipRecipes(output);
        buildCobblestoneEnergizedCubeRecipes(output);
        buildCobblestoneParallelChipRecipes(output);
        buildShieldProjectorModuleRecipes(output);
        buildCobblestoneMachineBlockRecipes(output);
        buildCustomMachineRecipes(output);
        buildConfigurationCardRecipe(output);
        buildFlyingStoneRecipe(output);
        buildMultiblockPortAndUpgradeRecipes(output);

        // ここから下は独自 RecipeType / RecipeSerializer を使う機械レシピです。
        // JSON の出力先は data/<modid>/recipe/<machine_name>/... になります。
        // 現在は登録済みの機械レシピをすべて datagen しています。
        // 新しい機械を追加したときは、この並びへ buildXxxRecipes(...) を足します。
        CobblestoneFurnaceRecipeDatagen.register(output);
        CobblestonePoweredFurnaceRecipeDatagen.register(output);
        CobblestoneExtremeCompressorRecipeDatagen.register(output);
        CobblestoneCrusherRecipeDatagen.register(output);
        ShieldProjectorRecipeDatagen.register(output);
        CobblestoneCentrifugeRecipeDatagen.register(output);
        CobblestoneLaserDrillRecipeDatagen.register(output);
        CobblestoneMelterRecipeDatagen.register(output);
        CobblestoneAssemblyMachineRecipeDatagen.register(output);
        CobblestoneEnchanterRecipeDatagen.register(output);
        CobblestoneChemicalReactorRecipeDatagen.register(output);
        CobblestoneMixerRecipeDatagen.register(output);
        CobblestoneReactionChamberRecipeDatagen.register(output);
        CobblestoneDissolutionChamberRecipeDatagen.register(output);
        CobblestoneCrystallizationChamberRecipeDatagen.register(output);
        CobblestoneFluidMixerRecipeDatagen.register(output);
        StoneBreakSimulatorRecipeDatagen.register(output);
    }

    private void buildCobblestoneBreadRecipe(RecipeOutput output) {
        // 通常版はバニラの丸石 3 個と石 3 個を、tier 版は同じ tier の圧縮丸石と圧縮石で作ります。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_BREAD.get())
            .pattern("   ")
            .pattern("CCC")
            .pattern("SSS")
            .define('C', Items.COBBLESTONE)
            .define('S', Items.STONE)
            .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
            .save(output, modRecipeId("cobblestone_bread"));

        for (ModItems.TierCobblestoneBread tier : ModItems.TierCobblestoneBread.values()) {
            ModBlocks.TierCompressedCobblestone compressedCobblestoneTier =
                ModBlocks.TierCompressedCobblestone.valueOf(tier.name());
            ModBlocks.TierCompressedStone compressedStoneTier =
                ModBlocks.TierCompressedStone.valueOf(tier.name());
            ItemLike compressedCobblestone = compressedCobblestoneTier.getBlock().get();
            ItemLike compressedStone = compressedStoneTier.getBlock().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("   ")
                .pattern("CCC")
                .pattern("SSS")
                .define('C', compressedCobblestone)
                .define('S', compressedStone)
                .unlockedBy("has_" + compressedCobblestoneTier.getRegistryName(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildFortuneEnchantedBookRecipes(RecipeOutput output) {
        HolderLookup.Provider holderLookup = this.lookupProvider.join();

        // 幸運 4 以降は、同じ形の recipe を段階的に積み上げます。
        // B は前レベルの本に component まで厳密一致させ、
        // C は次段階に対応する丸石シンギュラリティ 8 個を外周へ置きます。
        for (int fortuneLevel = FortuneEnchantedBookHelper.FIRST_FORTUNE_BOOK_LEVEL;
             fortuneLevel <= FortuneEnchantedBookHelper.LAST_FORTUNE_BOOK_LEVEL;
             fortuneLevel++) {
            ItemLike singularity = FortuneEnchantedBookHelper.getRequiredSingularityTier(fortuneLevel).getItem().get();
            ItemStack resultBook = FortuneEnchantedBookHelper.createFortuneEnchantedBook(holderLookup, fortuneLevel);

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultBook)
                .pattern("CCC")
                .pattern("CBC")
                .pattern("CCC")
                .define('C', singularity)
                .define('B', createFortuneBookBaseIngredient(holderLookup, fortuneLevel))
                .unlockedBy("has_" + FortuneEnchantedBookHelper.getRequiredSingularityTier(fortuneLevel).getRegistryName(), has(singularity))
                .save(output, modRecipeId("fortune_" + fortuneLevel + "_enchanted_book"));
        }
    }

    private Ingredient createFortuneBookBaseIngredient(HolderLookup.Provider holderLookup, int fortuneLevel) {
        if (fortuneLevel == FortuneEnchantedBookHelper.FIRST_FORTUNE_BOOK_LEVEL) {
            return Ingredient.of(Items.BOOK);
        }

        // ここは「前レベルの幸運本だけ」を素材にしたいので、
        // enchanted_book というアイテム種別だけでなく保存エンチャントの中身まで一致させます。
        return DataComponentIngredient.of(true, FortuneEnchantedBookHelper.createFortuneEnchantedBook(holderLookup, fortuneLevel - 1));
    }

    private void buildEfficiencyEnchantedBookRecipes(RecipeOutput output) {
        HolderLookup.Provider holderLookup = this.lookupProvider.join();

        // 効率 4 は本をレッドストーンブロックで囲み、
        // 効率 5 以降は幸運本と同じ形でシンギュラリティ外周へ置き換えます。
        for (int efficiencyLevel = EfficiencyEnchantedBookHelper.FIRST_EFFICIENCY_BOOK_LEVEL;
             efficiencyLevel <= EfficiencyEnchantedBookHelper.LAST_EFFICIENCY_BOOK_LEVEL;
             efficiencyLevel++) {
            ItemStack resultBook = EfficiencyEnchantedBookHelper.createEfficiencyEnchantedBook(holderLookup, efficiencyLevel);

            if (efficiencyLevel == EfficiencyEnchantedBookHelper.FIRST_EFFICIENCY_BOOK_LEVEL) {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultBook)
                    .pattern("CCC")
                    .pattern("CBC")
                    .pattern("CCC")
                    .define('C', Items.REDSTONE_BLOCK)
                    .define('B', Items.BOOK)
                    .unlockedBy("has_redstone_block", has(Items.REDSTONE_BLOCK))
                    .save(output, modRecipeId("efficiency_" + efficiencyLevel + "_enchanted_book"));
                continue;
            }

            ItemLike singularity = EfficiencyEnchantedBookHelper.getRequiredSingularityTier(efficiencyLevel).getItem().get();
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, resultBook)
                .pattern("CCC")
                .pattern("CBC")
                .pattern("CCC")
                .define('C', singularity)
                .define('B', createEfficiencyBookBaseIngredient(holderLookup, efficiencyLevel))
                .unlockedBy("has_" + EfficiencyEnchantedBookHelper.getRequiredSingularityTier(efficiencyLevel).getRegistryName(), has(singularity))
                .save(output, modRecipeId("efficiency_" + efficiencyLevel + "_enchanted_book"));
        }
    }

    private Ingredient createEfficiencyBookBaseIngredient(HolderLookup.Provider holderLookup, int efficiencyLevel) {
        // 効率 5 以降は「前レベルの効率本だけ」を素材にします。
        return DataComponentIngredient.of(true, EfficiencyEnchantedBookHelper.createEfficiencyEnchantedBook(holderLookup, efficiencyLevel - 1));
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
        // 各段階は圧縮丸石と同じく、1 個の単体クラフトで前段階 8 個へ戻せます。
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
        // 逆変換は単体クラフトで、8 個の gem へ戻します。
        saveSingleItemUnpackRecipe(
            output,
            ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_BIT.get(),
            ModItems.COBBLESTONE_GEM.get(),
            8,
            "compressed_cobblestone_singularity_bit",
            "cobblestone_gem"
        );

        for (ModItems.TierCompressedCobblestoneSingularityBit tier : ModItems.TierCompressedCobblestoneSingularityBit.values()) {
            ItemLike cobblestoneGem = ModItems.TierCobblestoneGem.valueOf(tier.name()).getItem().get();
            String gemRegistryName = ModItems.TierCobblestoneGem.valueOf(tier.name()).getRegistryName();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("CCC")
                .pattern("C C")
                .pattern("CCC")
                .define('C', cobblestoneGem)
                .unlockedBy("has_" + gemRegistryName, has(cobblestoneGem))
                .save(output, modRecipeId(tier.getRegistryName()));
            saveSingleItemUnpackRecipe(
                output,
                tier.getItem().get(),
                cobblestoneGem,
                8,
                tier.getRegistryName(),
                gemRegistryName
            );
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
        saveSingleItemUnpackRecipe(
            output,
            ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT.get(),
            ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_BIT.get(),
            8,
            "compressed_cobblestone_singularity_fragment",
            "compressed_cobblestone_singularity_bit"
        );

        for (ModItems.TierCompressedCobblestoneSingularityFragment tier : ModItems.TierCompressedCobblestoneSingularityFragment.values()) {
            ItemLike singularityBit = ModItems.TierCompressedCobblestoneSingularityBit.valueOf(tier.name()).getItem().get();
            String bitRegistryName = ModItems.TierCompressedCobblestoneSingularityBit.valueOf(tier.name()).getRegistryName();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("BBB")
                .pattern("B B")
                .pattern("BBB")
                .define('B', singularityBit)
                .unlockedBy("has_" + bitRegistryName, has(singularityBit))
                .save(output, modRecipeId(tier.getRegistryName()));
            saveSingleItemUnpackRecipe(
                output,
                tier.getItem().get(),
                singularityBit,
                8,
                tier.getRegistryName(),
                bitRegistryName
            );
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
        saveSingleItemUnpackRecipe(
            output,
            ModItems.COMPRESSED_COBBLESTONE_SINGULARITY.get(),
            ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT.get(),
            8,
            "compressed_cobblestone_singularity",
            "compressed_cobblestone_singularity_fragment"
        );

        for (ModItems.TierCompressedCobblestoneSingularity tier : ModItems.TierCompressedCobblestoneSingularity.values()) {
            ItemLike singularityFragment = ModItems.TierCompressedCobblestoneSingularityFragment.valueOf(tier.name()).getItem().get();
            String fragmentRegistryName = ModItems.TierCompressedCobblestoneSingularityFragment.valueOf(tier.name()).getRegistryName();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("FFF")
                .pattern("F F")
                .pattern("FFF")
                .define('F', singularityFragment)
                .unlockedBy("has_" + fragmentRegistryName, has(singularityFragment))
                .save(output, modRecipeId(tier.getRegistryName()));
            saveSingleItemUnpackRecipe(
                output,
                tier.getItem().get(),
                singularityFragment,
                8,
                tier.getRegistryName(),
                fragmentRegistryName
            );
        }
    }

    // 圧縮丸石と同じく、1 個を作業台へ置くだけで前段階へ戻します。
    private void saveSingleItemUnpackRecipe(
        RecipeOutput output,
        ItemLike packedItem,
        ItemLike unpackedItem,
        int unpackedCount,
        String packedRegistryName,
        String unpackedRegistryName
    ) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, unpackedItem, unpackedCount)
            .requires(packedItem)
            .unlockedBy("has_" + packedRegistryName, has(packedItem))
            .save(output, modRecipeId(packedRegistryName + "_to_" + unpackedRegistryName));
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

    private void buildCobblestonePickaxeRecipes(RecipeOutput output) {
        // バニラ石ピッケルと同じ形です。
        // 頭 3 個 = その tier の圧縮丸石、持ち手 2 本 = その tier の丸石ロッド。
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COMPRESSED_COBBLESTONE_PICKAXE.get())
            .pattern("CCC")
            .pattern(" R ")
            .pattern(" R ")
            .define('C', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .define('R', ModItems.COBBLESTONE_ROD.get())
            .unlockedBy("has_compressed_cobblestone", has(ModItems.COMPRESSED_COBBLESTONE_ITEM.get()))
            .save(output, modRecipeId("compressed_cobblestone_pickaxe"));

        for (ModItems.TierCompressedCobblestonePickaxe tier : ModItems.TierCompressedCobblestonePickaxe.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();
            ItemLike rod = ModItems.TierCobblestoneRod.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, tier.getItem().get())
                .pattern("CCC")
                .pattern(" R ")
                .pattern(" R ")
                .define('C', compressedCobblestone)
                .define('R', rod)
                .unlockedBy("has_" + tier.getRegistryName() + "_head", has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneArmorRecipes(RecipeOutput output) {
        // バニラ鉄防具と同じ形です。素材だけ圧縮丸石（tier 版は対応 tier の圧縮丸石）に差し替えます。
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.COMPRESSED_COBBLESTONE_HELMET.get())
            .pattern("CCC")
            .pattern("C C")
            .define('C', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .unlockedBy("has_compressed_cobblestone", has(ModItems.COMPRESSED_COBBLESTONE_ITEM.get()))
            .save(output, modRecipeId("compressed_cobblestone_helmet"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.COMPRESSED_COBBLESTONE_CHESTPLATE.get())
            .pattern("C C")
            .pattern("CCC")
            .pattern("CCC")
            .define('C', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .unlockedBy("has_compressed_cobblestone", has(ModItems.COMPRESSED_COBBLESTONE_ITEM.get()))
            .save(output, modRecipeId("compressed_cobblestone_chestplate"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.COMPRESSED_COBBLESTONE_LEGGINGS.get())
            .pattern("CCC")
            .pattern("C C")
            .pattern("C C")
            .define('C', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .unlockedBy("has_compressed_cobblestone", has(ModItems.COMPRESSED_COBBLESTONE_ITEM.get()))
            .save(output, modRecipeId("compressed_cobblestone_leggings"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.COMPRESSED_COBBLESTONE_BOOTS.get())
            .pattern("C C")
            .pattern("C C")
            .define('C', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .unlockedBy("has_compressed_cobblestone", has(ModItems.COMPRESSED_COBBLESTONE_ITEM.get()))
            .save(output, modRecipeId("compressed_cobblestone_boots"));

        for (ModItems.TierCompressedCobblestoneArmor tier : ModItems.TierCompressedCobblestoneArmor.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, tier.getItem(ModItems.CompressedCobblestoneArmorPiece.HELMET).get())
                .pattern("CCC")
                .pattern("C C")
                .define('C', compressedCobblestone)
                .unlockedBy("has_" + tier.getRegistryPrefix(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName(ModItems.CompressedCobblestoneArmorPiece.HELMET)));

            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, tier.getItem(ModItems.CompressedCobblestoneArmorPiece.CHESTPLATE).get())
                .pattern("C C")
                .pattern("CCC")
                .pattern("CCC")
                .define('C', compressedCobblestone)
                .unlockedBy("has_" + tier.getRegistryPrefix(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName(ModItems.CompressedCobblestoneArmorPiece.CHESTPLATE)));

            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, tier.getItem(ModItems.CompressedCobblestoneArmorPiece.LEGGINGS).get())
                .pattern("CCC")
                .pattern("C C")
                .pattern("C C")
                .define('C', compressedCobblestone)
                .unlockedBy("has_" + tier.getRegistryPrefix(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName(ModItems.CompressedCobblestoneArmorPiece.LEGGINGS)));

            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, tier.getItem(ModItems.CompressedCobblestoneArmorPiece.BOOTS).get())
                .pattern("C C")
                .pattern("C C")
                .define('C', compressedCobblestone)
                .unlockedBy("has_" + tier.getRegistryPrefix(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName(ModItems.CompressedCobblestoneArmorPiece.BOOTS)));
        }
    }

    private void buildCobblestoneWireRecipes(RecipeOutput output) {
        // ワイヤは上下を糸、中央列を対応する圧縮丸石でそろえ、9 個ずつ出力します。
        // tier 版は enum 名をそのまま使って、同じ tier の圧縮丸石へ差し替えます。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_WIRE.get(), 9)
            .pattern("SSS")
            .pattern("CCC")
            .pattern("SSS")
            .define('S', Items.STRING)
            .define('C', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .unlockedBy("has_compressed_cobblestone", has(ModItems.COMPRESSED_COBBLESTONE_ITEM.get()))
            .save(output, modRecipeId("cobblestone_wire"));

        for (ModItems.TierCobblestoneWire tier : ModItems.TierCobblestoneWire.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get(), 9)
                .pattern("SSS")
                .pattern("CCC")
                .pattern("SSS")
                .define('S', Items.STRING)
                .define('C', compressedCobblestone)
                .unlockedBy("has_" + tier.getRegistryName(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneCircuitRecipes(RecipeOutput output) {
        // サーキットは外周 8 マスをレッドストーン、中央を対応 tier の圧縮丸石で作ります。
        // 出力数はユーザー指定どおり 1 個です。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_CIRCUIT.get())
            .pattern("RRR")
            .pattern("RCR")
            .pattern("RRR")
            .define('R', Items.REDSTONE)
            .define('C', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .unlockedBy("has_compressed_cobblestone", has(ModItems.COMPRESSED_COBBLESTONE_ITEM.get()))
            .save(output, modRecipeId("cobblestone_circuit"));

        for (ModItems.TierCobblestoneCircuit tier : ModItems.TierCobblestoneCircuit.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("RRR")
                .pattern("RCR")
                .pattern("RRR")
                .define('R', Items.REDSTONE)
                .define('C', compressedCobblestone)
                .unlockedBy("has_" + tier.getRegistryName(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneProcessorRecipes(RecipeOutput output) {
        // processor は wire と circuit を外周へ置き、右下に package を置く形で統一します。
        // tier 版も同じ tier の部品を引けばよいので、名前対応だけで安全に回せます。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_PROCESSOR.get())
            .pattern("WCW")
            .pattern("WPW")
            .pattern("WCW")
            .define('W', ModItems.COBBLESTONE_WIRE.get())
            .define('C', ModItems.COBBLESTONE_CIRCUIT.get())
            .define('P', ModItems.COBBLESTONE_CIRCUIT_PACKAGE.get())
            .unlockedBy("has_cobblestone_circuit_package", has(ModItems.COBBLESTONE_CIRCUIT_PACKAGE.get()))
            .save(output, modRecipeId("cobblestone_processor"));

        for (ModItems.TierCobblestoneProcessor tier : ModItems.TierCobblestoneProcessor.values()) {
            ItemLike cobblestoneWire = ModItems.TierCobblestoneWire.valueOf(tier.name()).getItem().get();
            ItemLike cobblestoneCircuit = ModItems.TierCobblestoneCircuit.valueOf(tier.name()).getItem().get();
            ItemLike cobblestoneCircuitPackage = ModItems.TierCobblestoneCircuitPackage.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("WCW")
                .pattern("WPW")
                .pattern("WCW")
                .define('W', cobblestoneWire)
                .define('C', cobblestoneCircuit)
                .define('P', cobblestoneCircuitPackage)
                .unlockedBy("has_" + tier.getRegistryName(), has(cobblestoneCircuitPackage))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneCircuitPackageRecipes(RecipeOutput output) {
        // circuit package は 2 列 3 段で圧縮丸石を並べ、6 個出力します。
        // tier 版も同じ形で、対応する tier の圧縮丸石へ置き換えます。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_CIRCUIT_PACKAGE.get(), 6)
            .pattern("CC ")
            .pattern("CC ")
            .pattern("CC ")
            .define('C', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .unlockedBy("has_compressed_cobblestone", has(ModItems.COMPRESSED_COBBLESTONE_ITEM.get()))
            .save(output, modRecipeId("cobblestone_circuit_package"));

        for (ModItems.TierCobblestoneCircuitPackage tier : ModItems.TierCobblestoneCircuitPackage.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get(), 6)
                .pattern("CC ")
                .pattern("CC ")
                .pattern("CC ")
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

    private void buildCobblestoneParallelChipRecipes(RecipeOutput output) {
        // Parallel Chip は、外周を圧縮丸石で囲み、中央へ singularity fragment を置く 3x3 レシピです。
        // tier 版も同じ見た目のまま、対応する tier の圧縮丸石と fragment に差し替えます。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COBBLESTONE_PARALLEL_CHIP.get())
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .define('A', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .define('B', ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT.get())
            .unlockedBy("has_compressed_cobblestone_singularity_fragment", has(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT.get()))
            .save(output, modRecipeId("cobblestone_parallel_chip"));

        for (ModItems.TierCobblestoneParallelChip tier : ModItems.TierCobblestoneParallelChip.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();
            ItemLike singularityFragment = ModItems.TierCompressedCobblestoneSingularityFragment.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', compressedCobblestone)
                .define('B', singularityFragment)
                .unlockedBy("has_" + tier.getRegistryName(), has(singularityFragment))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildShieldProjectorModuleRecipes(RecipeOutput output) {
        // ベースは外周を圧縮丸石、中央を回路にします。
        // 加速チップ（motor）や Energized Cube（processor）と同じ材料にならないようにします。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SHIELD_BASE_MODULE.get())
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .define('A', ModItems.COMPRESSED_COBBLESTONE_ITEM.get())
            .define('B', ModItems.COBBLESTONE_CIRCUIT.get())
            .unlockedBy("has_cobblestone_circuit", has(ModItems.COBBLESTONE_CIRCUIT.get()))
            .save(output, modRecipeId("shield_base_module"));

        for (ModItems.TierShieldBaseModule tier : ModItems.TierShieldBaseModule.values()) {
            ItemLike compressedCobblestone = ModItems.TierCompressedCobblestoneItem.valueOf(tier.name()).getItem().get();
            ItemLike cobblestoneCircuit = ModItems.TierCobblestoneCircuit.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tier.getItem().get())
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', compressedCobblestone)
                .define('B', cobblestoneCircuit)
                .unlockedBy("has_" + tier.getRegistryName(), has(cobblestoneCircuit))
                .save(output, modRecipeId(tier.getRegistryName()));
        }

        // 範囲・変換量・容量は、同じ tier のベースへ専用部品を足します。
        buildShieldModuleFromBaseRecipes(
            output,
            ModItems.SHIELD_RANGE_MODULE.get(),
            ModItems.SHIELD_BASE_MODULE.get(),
            ModItems.COBBLESTONE_ACCELERATION_CHIP.get(),
            "shield_range_module",
            "cobblestone_acceleration_chip"
        );
        for (ModItems.TierShieldRangeModule tier : ModItems.TierShieldRangeModule.values()) {
            ItemLike baseModule = ModItems.TierShieldBaseModule.valueOf(tier.name()).getItem().get();
            ItemLike cobblestoneCircuit = ModItems.TierCobblestoneCircuit.valueOf(tier.name()).getItem().get();
            buildShieldModuleFromBaseRecipes(
                output,
                tier.getItem().get(),
                baseModule,
                cobblestoneCircuit,
                tier.getRegistryName(),
                tier.getRegistryName()
            );
        }

        buildShieldModuleFromBaseRecipes(
            output,
            ModItems.SHIELD_RATE_MODULE.get(),
            ModItems.SHIELD_BASE_MODULE.get(),
            ModItems.COBBLESTONE_PARALLEL_CHIP.get(),
            "shield_rate_module",
            "cobblestone_parallel_chip"
        );
        for (ModItems.TierShieldRateModule tier : ModItems.TierShieldRateModule.values()) {
            ItemLike baseModule = ModItems.TierShieldBaseModule.valueOf(tier.name()).getItem().get();
            ItemLike cobblestoneProcessor = ModItems.TierCobblestoneProcessor.valueOf(tier.name()).getItem().get();
            buildShieldModuleFromBaseRecipes(
                output,
                tier.getItem().get(),
                baseModule,
                cobblestoneProcessor,
                tier.getRegistryName(),
                tier.getRegistryName()
            );
        }

        buildShieldModuleFromBaseRecipes(
            output,
            ModItems.SHIELD_CAPACITY_MODULE.get(),
            ModItems.SHIELD_BASE_MODULE.get(),
            ModItems.COBBLESTONE_ENERGIZED_CUBE.get(),
            "shield_capacity_module",
            "cobblestone_energized_cube"
        );
        for (ModItems.TierShieldCapacityModule tier : ModItems.TierShieldCapacityModule.values()) {
            ItemLike baseModule = ModItems.TierShieldBaseModule.valueOf(tier.name()).getItem().get();
            ItemLike energizedCube = ModItems.TierCobblestoneEnergizedCube.valueOf(tier.name()).getItem().get();
            buildShieldModuleFromBaseRecipes(
                output,
                tier.getItem().get(),
                baseModule,
                energizedCube,
                tier.getRegistryName(),
                tier.getRegistryName()
            );
        }
    }

    private void buildShieldModuleFromBaseRecipes(
        RecipeOutput output,
        ItemLike result,
        ItemLike baseModule,
        ItemLike extraIngredient,
        String recipeId,
        String unlockName
    ) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result)
            .requires(baseModule)
            .requires(extraIngredient)
            .unlockedBy("has_" + unlockName, has(extraIngredient))
            .save(output, modRecipeId(recipeId));
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

            // M は 1 段上、L は 2 段上の singularity bit を中央へ置きます。
            // 上の tier が無い obsidian などは、今までどおり中央を空けたままにします。
            int centerBitTierOffset = generatorVariant.getSize() == ModBlocks.CobblestoneGeneratorSize.M ? 1 : 2;
            ItemLike centerSingularityBit = findHigherTierSingularityBit(generatorVariant.getTier(), centerBitTierOffset);

            if (centerSingularityBit == null) {
                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                    .pattern("GGG")
                    .pattern("G G")
                    .pattern("GGG")
                    .define('G', previousGenerator)
                    .unlockedBy("has_" + previousVariant.getRegistryName(), has(previousGenerator))
                    .save(output, modRecipeId(generatorVariant.getRegistryName()));
                continue;
            }

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .pattern("GGG")
                .pattern("GBG")
                .pattern("GGG")
                .define('G', previousGenerator)
                .define('B', centerSingularityBit)
                .unlockedBy("has_" + previousVariant.getRegistryName(), has(previousGenerator))
                .save(output, modRecipeId(generatorVariant.getRegistryName()));
        }
    }

    /**
     * 無印圧縮丸石を copper の一段下として数え、offset 段上の singularity bit を返します。
     * 上の tier が無ければ null です。
     */
    private ItemLike findHigherTierSingularityBit(ModBlocks.TierCompressedCobblestone currentTier, int tierOffset) {
        ModBlocks.TierCompressedCobblestone[] tiers = ModBlocks.TierCompressedCobblestone.values();
        int currentIndex = -1;
        if (currentTier != null) {
            currentIndex = currentTier.ordinal();
        }

        int targetIndex = currentIndex + tierOffset;
        if (targetIndex < 0 || targetIndex >= tiers.length) {
            return null;
        }

        return ModItems.TierCompressedCobblestoneSingularityBit.valueOf(tiers[targetIndex].name()).getItem().get();
    }

    private void buildCobblestoneMachineCasingRecipes(RecipeOutput output) {
        for (MachineCasingRecipeDefinition recipe : MACHINE_CASING_RECIPES) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, recipe.result)
                .pattern("CGC")
                .pattern("GCG")
                .pattern("CGC")
                .define('C', recipe.compressedCobblestone)
                .define('G', recipe.centerGem)
                .unlockedBy("has_" + recipe.recipeName, has(recipe.centerGem))
                .save(output, modRecipeId(recipe.recipeName));
        }
    }

    private void buildCobblestoneMachineBlockRecipes(RecipeOutput output) {
        for (MachineBlockRecipeDefinition recipe : MACHINE_BLOCK_RECIPES) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, recipe.result)
                .pattern("CCC")
                .pattern("MAP")
                .pattern("CCC")
                .define('C', recipe.compressedCobblestone)
                .define('M', recipe.motor)
                .define('A', recipe.machineCasing)
                .define('P', recipe.processor)
                .unlockedBy("has_" + recipe.recipeName, has(recipe.processor))
                .save(output, modRecipeId(recipe.recipeName));
        }

        // シールドプロジェクターは四隅ダイヤモンド、中央アクアマリン casing、残りアメジスト casing です。
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_SHIELD_PROJECTOR.get())
            .pattern("DAD")
            .pattern("ACA")
            .pattern("DAD")
            .define('D', Items.DIAMOND_BLOCK)
            .define('A', ModBlocks.TierCobblestoneMachineCasing.AMETHYST.getBlock().get())
            .define('C', ModBlocks.TierCobblestoneMachineCasing.AQUAMARINE.getBlock().get())
            .unlockedBy(
                "has_tier_aquamarine_cobblestone_machine_casing",
                has(ModBlocks.TierCobblestoneMachineCasing.AQUAMARINE.getBlock().get())
            )
            .save(output, modRecipeId("cobblestone_shield_projector"));

        // Cobblestone Furnace は最序盤で作れる基本機械なので、
        // 圧縮丸石ではなくバニラの丸石とかまどだけで作れるようにします。
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_FURNACE.get())
            .pattern("CCC")
            .pattern("CFC")
            .pattern("CCC")
            .define('C', Items.COBBLESTONE)
            .define('F', Items.FURNACE)
            .unlockedBy("has_cobblestone_furnace_material", has(Items.FURNACE))
            .save(output, modRecipeId("cobblestone_furnace"));

        // Cobblestone FE Generator は Cobblestone Furnace を土台に、
        // 鉄ブロックとレッドストーンブロックで発電機へ強化する流れにします。
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_FE_GENERATOR.get())
            .pattern("IRI")
            .pattern("RFR")
            .pattern("IRI")
            .define('I', Items.IRON_BLOCK)
            .define('R', Items.REDSTONE_BLOCK)
            .define('F', ModBlocks.COBBLESTONE_FURNACE.get())
            .unlockedBy("has_cobblestone_fe_generator_material", has(ModBlocks.COBBLESTONE_FURNACE.get()))
            .save(output, modRecipeId("cobblestone_fe_generator"));
    }

    private void buildMultiblockPortAndUpgradeRecipes(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.MULTIBLOCK_ITEM_INPUT_PORT.get())
            .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
            .requires(Items.HOPPER)
            .unlockedBy("has_cobblestone_machine_casing", has(ModBlocks.COBBLESTONE_MACHINE_CASING.get()))
            .save(output, modRecipeId("multiblock_item_input_port"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.MULTIBLOCK_ITEM_OUTPUT_PORT.get())
            .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
            .requires(Items.DROPPER)
            .unlockedBy("has_cobblestone_machine_casing", has(ModBlocks.COBBLESTONE_MACHINE_CASING.get()))
            .save(output, modRecipeId("multiblock_item_output_port"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.MULTIBLOCK_FLUID_INPUT_PORT.get())
            .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
            .requires(Items.BUCKET)
            .unlockedBy("has_cobblestone_machine_casing", has(ModBlocks.COBBLESTONE_MACHINE_CASING.get()))
            .save(output, modRecipeId("multiblock_fluid_input_port"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.MULTIBLOCK_FLUID_OUTPUT_PORT.get())
            .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
            .requires(Items.GLASS_BOTTLE)
            .unlockedBy("has_cobblestone_machine_casing", has(ModBlocks.COBBLESTONE_MACHINE_CASING.get()))
            .save(output, modRecipeId("multiblock_fluid_output_port"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.MULTIBLOCK_COBBLE_INPUT_PORT.get())
            .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
            .requires(Items.COBBLESTONE)
            .unlockedBy("has_cobblestone_machine_casing", has(ModBlocks.COBBLESTONE_MACHINE_CASING.get()))
            .save(output, modRecipeId("multiblock_cobble_input_port"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.MULTIBLOCK_ACCELERATION_UPGRADE.get())
            .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
            .requires(ModItems.COBBLESTONE_ACCELERATION_CHIP.get())
            .unlockedBy("has_cobblestone_acceleration_chip", has(ModItems.COBBLESTONE_ACCELERATION_CHIP.get()))
            .save(output, modRecipeId("multiblock_acceleration_upgrade"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.MULTIBLOCK_ENERGIZED_UPGRADE.get())
            .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
            .requires(ModItems.COBBLESTONE_ENERGIZED_CUBE.get())
            .unlockedBy("has_cobblestone_energized_cube", has(ModItems.COBBLESTONE_ENERGIZED_CUBE.get()))
            .save(output, modRecipeId("multiblock_energized_upgrade"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.MULTIBLOCK_PARALLEL_UPGRADE.get())
            .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
            .requires(ModItems.COBBLESTONE_PARALLEL_CHIP.get())
            .unlockedBy("has_cobblestone_parallel_chip", has(ModItems.COBBLESTONE_PARALLEL_CHIP.get()))
            .save(output, modRecipeId("multiblock_parallel_upgrade"));

        for (ModBlocks.TierMultiblockAccelerationUpgrade tier : ModBlocks.TierMultiblockAccelerationUpgrade.values()) {
            ModItems.TierCobblestoneAccelerationChip chipTier =
                ModItems.TierCobblestoneAccelerationChip.valueOf(tier.name());
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, tier.getBlock().get())
                .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
                .requires(chipTier.getItem().get())
                .unlockedBy("has_" + chipTier.getRegistryName(), has(chipTier.getItem().get()))
                .save(output, modRecipeId(tier.getRegistryName()));
        }

        for (ModBlocks.TierMultiblockEnergizedUpgrade tier : ModBlocks.TierMultiblockEnergizedUpgrade.values()) {
            ModItems.TierCobblestoneEnergizedCube cubeTier =
                ModItems.TierCobblestoneEnergizedCube.valueOf(tier.name());
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, tier.getBlock().get())
                .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
                .requires(cubeTier.getItem().get())
                .unlockedBy("has_" + cubeTier.getRegistryName(), has(cubeTier.getItem().get()))
                .save(output, modRecipeId(tier.getRegistryName()));
        }

        for (ModBlocks.TierMultiblockParallelUpgrade tier : ModBlocks.TierMultiblockParallelUpgrade.values()) {
            ModItems.TierCobblestoneParallelChip chipTier =
                ModItems.TierCobblestoneParallelChip.valueOf(tier.name());
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, tier.getBlock().get())
                .requires(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
                .requires(chipTier.getItem().get())
                .unlockedBy("has_" + chipTier.getRegistryName(), has(chipTier.getItem().get()))
                .save(output, modRecipeId(tier.getRegistryName()));
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

    private void buildCobblestoneDrawerRecipes(RecipeOutput output) {
        // Cobblestone Drawer は中央にチェスト、外周に同じ tier の圧縮丸石を 8 個並べるレシピです。
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_DRAWER.get())
            .pattern("CCC")
            .pattern("CTC")
            .pattern("CCC")
            .define('C', ModBlocks.COMPRESSED_COBBLESTONE.get())
            .define('T', Items.CHEST)
            .unlockedBy("has_cobblestone_drawer_material", has(ModBlocks.COMPRESSED_COBBLESTONE.get()))
            .save(output, modRecipeId("cobblestone_drawer"));

        for (ModBlocks.TierCobblestoneDrawer tier : ModBlocks.TierCobblestoneDrawer.values()) {
            ItemLike compressedCobblestone = ModBlocks.TierCompressedCobblestone.valueOf(tier.name()).getBlock().get();
            ItemLike result = ModItems.TierCobblestoneDrawerItem.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .pattern("CCC")
                .pattern("CTC")
                .pattern("CCC")
                .define('C', compressedCobblestone)
                .define('T', Items.CHEST)
                .unlockedBy("has_" + tier.getRegistryName(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCobblestoneFeCubeRecipes(RecipeOutput output) {
        // FE キューブは中央のレッドストーンブロックを、各 tier の圧縮丸石 8 個で囲みます。
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_FE_CUBE.get())
            .pattern("CCC")
            .pattern("CRC")
            .pattern("CCC")
            .define('C', ModBlocks.COMPRESSED_COBBLESTONE.get())
            .define('R', Items.REDSTONE_BLOCK)
            .unlockedBy("has_cobblestone_fe_cube_material", has(ModBlocks.COMPRESSED_COBBLESTONE.get()))
            .save(output, modRecipeId("cobblestone_fe_cube"));

        for (ModBlocks.TierCobblestoneFeCube tier : ModBlocks.TierCobblestoneFeCube.values()) {
            ItemLike compressedCobblestone = ModBlocks.TierCompressedCobblestone.valueOf(tier.name()).getBlock().get();
            ItemLike result = ModItems.TierCobblestoneFeCubeItem.valueOf(tier.name()).getItem().get();

            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .pattern("CCC")
                .pattern("CRC")
                .pattern("CCC")
                .define('C', compressedCobblestone)
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy("has_" + tier.getRegistryName(), has(compressedCobblestone))
                .save(output, modRecipeId(tier.getRegistryName()));
        }
    }

    private void buildCustomMachineRecipes(RecipeOutput output) {
        // 丸石エンチャンターは、鉄 tier の圧縮丸石と gem、中央のエンチャントテーブルで組みます。
        // 通常の機械共通レシピとは形が違うため、個別レシピとして分けておきます。
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_ENCHANTER.get())
            .pattern("CGC")
            .pattern("GEG")
            .pattern("CGC")
            .define('C', ModBlocks.TierCompressedCobblestone.IRON.getBlock().get())
            .define('G', ModItems.TIER_IRON_COBBLESTONE_GEM.get())
            .define('E', Items.ENCHANTING_TABLE)
            .unlockedBy("has_cobblestone_enchanter_material", has(Items.ENCHANTING_TABLE))
            .save(output, modRecipeId("cobblestone_enchanter"));

        // Stone Break Simulator は、金 tier の圧縮丸石を外周に置き、
        // 鉄 tier の machine casing とダイヤのツルハシを組み合わせて作ります。
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STONE_BREAK_SIMULATOR.get())
            .pattern("CIC")
            .pattern("IPI")
            .pattern("CIC")
            .define('C', ModBlocks.TierCompressedCobblestone.GOLD.getBlock().get())
            .define('I', ModBlocks.TierCobblestoneMachineCasing.IRON.getBlock().get())
            .define('P', Items.DIAMOND_PICKAXE)
            .unlockedBy("has_stone_break_simulator_material", has(Items.DIAMOND_PICKAXE))
            .save(output, modRecipeId("stone_break_simulator"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_WATER_GENERATOR.get())
            .pattern("ACA")
            .pattern("CBC")
            .pattern("ACA")
            .define('A', ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock().get())
            .define('C', ModBlocks.TierCobblestoneMachineCasing.GOLD.getBlock().get())
            .define('B', Items.WATER_BUCKET)
            .unlockedBy("has_cobblestone_water_generator_material", has(Items.WATER_BUCKET))
            .save(output, modRecipeId("cobblestone_water_generator"));
    }

    private void buildConfigurationCardRecipe(RecipeOutput output) {
        // Configuration Card は初期 machine casing とレッドストーンだけで作れるようにします。
        // 1x2 の簡単なレシピにして、序盤の設定変更ツールとして取り回しやすくします。
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CONFIGURATION_CARD.get())
            .pattern("CR")
            .define('C', ModBlocks.COBBLESTONE_MACHINE_CASING.get())
            .define('R', Items.REDSTONE)
            .unlockedBy("has_configuration_card_material", has(ModBlocks.COBBLESTONE_MACHINE_CASING.get()))
            .save(output, modRecipeId("configuration_card"));
    }

    private void buildFlyingStoneRecipe(RecipeOutput output) {
        // 飛行石はサファイア tier の圧縮丸石・ワイヤとグロウストーンで組み立てます。
        ItemLike compressedSapphireCobblestone = ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock().get();
        ItemLike sapphireWire = ModItems.TierCobblestoneWire.SAPPHIRE.getItem().get();

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FLYING_STONE.get())
            .pattern(" CS")
            .pattern("CGC")
            .pattern(" C ")
            .define('C', compressedSapphireCobblestone)
            .define('G', Items.GLOWSTONE)
            .define('S', sapphireWire)
            .unlockedBy("has_tier_sapphire_compressed_cobblestone", has(compressedSapphireCobblestone))
            .save(output, modRecipeId("flying_stone"));
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

    private static class MachineCasingRecipeDefinition {
        private final String recipeName;
        private final ItemLike compressedCobblestone;
        private final ItemLike centerGem;
        private final ItemLike result;

        private MachineCasingRecipeDefinition(
            String recipeName,
            ItemLike compressedCobblestone,
            ItemLike centerGem,
            ItemLike result
        ) {
            this.recipeName = recipeName;
            this.compressedCobblestone = compressedCobblestone;
            this.centerGem = centerGem;
            this.result = result;
        }
    }

    private static class MachineBlockRecipeDefinition {
        private final String recipeName;
        private final ItemLike compressedCobblestone;
        private final ItemLike motor;
        private final ItemLike machineCasing;
        private final ItemLike processor;
        private final ItemLike result;

        private MachineBlockRecipeDefinition(
            String recipeName,
            ItemLike compressedCobblestone,
            ItemLike motor,
            ItemLike machineCasing,
            ItemLike processor,
            ItemLike result
        ) {
            this.recipeName = recipeName;
            this.compressedCobblestone = compressedCobblestone;
            this.motor = motor;
            this.machineCasing = machineCasing;
            this.processor = processor;
            this.result = result;
        }
    }

}