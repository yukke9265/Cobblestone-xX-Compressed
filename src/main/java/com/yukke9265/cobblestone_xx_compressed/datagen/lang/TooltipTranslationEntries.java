package com.yukke9265.cobblestone_xx_compressed.datagen.lang;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.util.TooltipTranslationKeys;

import net.neoforged.neoforge.common.data.LanguageProvider;

public final class TooltipTranslationEntries {
    private TooltipTranslationEntries() {
    }

    private record TooltipEntry(String key, String japanese, String english) {
    }

    // 機械の説明文はこの配列へ集約します。
    // 文面を調整したいときは、まずこのまとまりを見る前提です。
    private static final TooltipEntry[] MACHINE_ENTRIES = new TooltipEntry[] {
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_furnace"), "CP を消費して、かまどレシピを処理します。", "Uses Cobblestone Power to process furnace recipes."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_powered_furnace"), "CP を使って精錬を進める強化版のかまどです。", "An upgraded furnace that smelts items with Cobblestone Power."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_extreme_compressor"), "大量の素材を圧縮して、上位の丸石系素材を作ります。", "Compresses large amounts of material into higher tier cobblestone resources."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_crusher"), "素材を砕いて、ダストや副産物を作ります。", "Crushes materials into dusts and byproducts."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_fe_generator"), "CP を FE に変換して、外部の機械へ電力を送ります。", "Converts Cobblestone Power into FE for external machines."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_mixer"), "複数のアイテムを混ぜて、新しい素材を作ります。", "Mixes multiple items into new materials."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("stone_break_simulator"), "圧縮石を疑似的に破壊して、エンチャントの効果付きでドロップを生成します。", "Simulates breaking compressed stone and generates drops with the inserted pickaxe enchants."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_melter"), "アイテムを溶かして、液体素材へ変換します。", "Melts items into fluid materials."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_assembly_machine"), "部品を組み合わせて、機械用のアイテムを作ります。", "Assembles components into machine-related items."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_chemical_reactor"), "アイテムと液体を反応させて、化学系素材を作ります。", "Reacts items and fluids to create chemical materials."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_reaction_chamber"), "複数の材料を反応させて、中間素材や完成品を作ります。", "Combines multiple ingredients to produce intermediates and finished items."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_crystallization_chamber"), "液体や素材から結晶系アイテムを生成します。", "Creates crystal-based items from fluids and materials."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_dissolution_chamber"), "アイテムを液体へ溶かし込み、後工程の材料を作ります。", "Dissolves items into fluids for later processing."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_fluid_mixer"), "複数の液体や材料を混ぜて、新しい液体を作ります。", "Mixes fluids and ingredients into new fluids."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_centrifuge"), "材料を遠心分離して、複数の生成物に分けます。", "Separates materials into multiple outputs with centrifuging."),
        new TooltipEntry(TooltipTranslationKeys.machineDescription("cobblestone_laser_drill"), "CP を使って採掘系の素材を自動で取り出します。", "Uses Cobblestone Power to extract mining-related resources automatically.")
    };

    // 丸石製造機は全 tier で役割が共通なので、共通文とサイズ差分に分けて管理します。
    private static final TooltipEntry[] GENERATOR_ENTRIES = new TooltipEntry[] {
        new TooltipEntry(TooltipTranslationKeys.cobblestoneGeneratorDescription(), "対応する圧縮丸石を自動で生成します。", "Automatically produces the corresponding compressed cobblestone."),
        new TooltipEntry(TooltipTranslationKeys.cobblestoneGeneratorSizeDescription(ModBlocks.CobblestoneGeneratorSize.S), "S サイズは 1 tick ごとに 1 個生成します。", "Size S produces 1 block every tick."),
        new TooltipEntry(TooltipTranslationKeys.cobblestoneGeneratorSizeDescription(ModBlocks.CobblestoneGeneratorSize.M), "M サイズは 1 tick ごとに 8 個生成します。", "Size M produces 8 blocks every tick."),
        new TooltipEntry(TooltipTranslationKeys.cobblestoneGeneratorSizeDescription(ModBlocks.CobblestoneGeneratorSize.L), "L サイズは 1 tick ごとに 64 個生成します。", "Size L produces 64 blocks every tick.")
    };

    public static void addJapanese(LanguageProvider provider) {
        addEntries(provider, MACHINE_ENTRIES, true);
        addEntries(provider, GENERATOR_ENTRIES, true);
    }

    public static void addEnglish(LanguageProvider provider) {
        addEntries(provider, MACHINE_ENTRIES, false);
        addEntries(provider, GENERATOR_ENTRIES, false);
    }

    private static void addEntries(LanguageProvider provider, TooltipEntry[] entries, boolean japanese) {
        for (TooltipEntry entry : entries) {
            provider.add(entry.key(), japanese ? entry.japanese() : entry.english());
        }
    }
}