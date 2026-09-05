package com.yukke9265.cobblestone_xx_compressed.datagen.lang;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import com.yukke9265.cobblestone_xx_compressed.util.TooltipTranslationKeys;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModJapaneseLanguageProvider extends LanguageProvider {
    public ModJapaneseLanguageProvider(PackOutput output) {
        super(output, CobblestonexXCompressed.MODID, "ja_jp");
    }

    @Override
    protected void addTranslations() {
        addBlock(ModBlocks.COBBLESTONE_FURNACE, "丸石かまど");
        addBlock(ModBlocks.COBBLESTONE_FE_GENERATOR, "丸石 FE 発電機");
        addBlock(ModBlocks.COBBLESTONE_TANK, "丸石タンク");
        for (ModBlocks.TierCobblestoneTank tier : ModBlocks.TierCobblestoneTank.values()) {
            addBlock(tier.getBlock(), translateTierBlockName(tier.getEnglishDisplayName(), "Cobblestone Tank", "丸石タンク"));
        }
        addBlock(ModBlocks.COBBLESTONE_DRAWER, "丸石ドロワー");
        for (ModBlocks.TierCobblestoneDrawer tier : ModBlocks.TierCobblestoneDrawer.values()) {
            addBlock(tier.getBlock(), translateTierBlockName(tier.getEnglishDisplayName(), "Cobblestone Drawer", "丸石ドロワー"));
        }
        addBlock(ModBlocks.COBBLESTONE_FE_CUBE, "丸石 FE キューブ");
        for (ModBlocks.TierCobblestoneFeCube tier : ModBlocks.TierCobblestoneFeCube.values()) {
            addBlock(tier.getBlock(), translateTierBlockName(tier.getEnglishDisplayName(), "Cobblestone FE Cube", "丸石 FE キューブ"));
        }
        addBlock(ModBlocks.COBBLESTONE_POWERED_FURNACE, "丸石動力かまど");
        addBlock(ModBlocks.COBBLESTONE_EXTREME_COMPRESSOR, "丸石エクストリームコンプレッサー");
        addBlock(ModBlocks.COBBLESTONE_CRUSHER, "丸石クラッシャー");
        addBlock(ModBlocks.COBBLESTONE_SHIELD_PROJECTOR, "丸石シールドプロジェクター");
        addBlock(ModBlocks.COBBLESTONE_CENTRIFUGE, "丸石遠心分離機");
        addBlock(ModBlocks.COBBLESTONE_LASER_DRILL, "丸石レーザードリル");
        addBlock(ModBlocks.COBBLESTONE_MELTER, "丸石溶解機");
        addBlock(ModBlocks.COBBLESTONE_ASSEMBLY_MACHINE, "丸石組立機");
        addBlock(ModBlocks.COBBLESTONE_ENCHANTER, "丸石エンチャンター");
        addBlock(ModBlocks.COBBLESTONE_CHEMICAL_REACTOR, "丸石化学反応機");
        addBlock(ModBlocks.COBBLESTONE_MIXER, "丸石ミキサー");
        addBlock(ModBlocks.COBBLESTONE_POWERED_CRAFTER, "丸石パワードクラフター");
        addBlock(ModBlocks.STONE_BREAK_SIMULATOR, "石破壊シミュレーター");
        addBlock(ModBlocks.COBBLESTONE_REACTION_CHAMBER, "丸石反応槽");
        addBlock(ModBlocks.COBBLESTONE_CRYSTALLIZATION_CHAMBER, "丸石結晶化槽");
        addBlock(ModBlocks.COBBLESTONE_DISSOLUTION_CHAMBER, "丸石溶解槽");
        addBlock(ModBlocks.COBBLESTONE_FLUID_MIXER, "丸石流体ミキサー");
        addBlock(ModBlocks.COBBLESTONE_WATER_GENERATOR, "丸石水生成機");
        addBlock(ModBlocks.COBBLESTONE_MULTIBLOCK_CRUSHER, "丸石マルチブロッククラッシャー");
        addBlock(ModBlocks.MULTIBLOCK_ITEM_INPUT_PORT, "マルチブロックアイテム入力ポート");
        addBlock(ModBlocks.MULTIBLOCK_ITEM_OUTPUT_PORT, "マルチブロックアイテム出力ポート");
        addBlock(ModBlocks.MULTIBLOCK_FLUID_INPUT_PORT, "マルチブロック流体入力ポート");
        addBlock(ModBlocks.MULTIBLOCK_FLUID_OUTPUT_PORT, "マルチブロック流体出力ポート");
        addBlock(ModBlocks.MULTIBLOCK_COBBLE_INPUT_PORT, "マルチブロック丸石入力ポート");
        addBlock(ModBlocks.MULTIBLOCK_ACCELERATION_UPGRADE, "マルチブロック加速アップグレード");
        addBlock(ModBlocks.MULTIBLOCK_ENERGIZED_UPGRADE, "マルチブロック蓄電アップグレード");
        addBlock(ModBlocks.MULTIBLOCK_PARALLEL_UPGRADE, "マルチブロック並列アップグレード");
        for (ModBlocks.TierMultiblockAccelerationUpgrade tier : ModBlocks.TierMultiblockAccelerationUpgrade.values()) {
            addBlock(tier.getBlock(), translateMultiblockAccelerationUpgradeName(tier.getEnglishDisplayName()));
        }
        for (ModBlocks.TierMultiblockEnergizedUpgrade tier : ModBlocks.TierMultiblockEnergizedUpgrade.values()) {
            addBlock(tier.getBlock(), translateMultiblockEnergizedUpgradeName(tier.getEnglishDisplayName()));
        }
        for (ModBlocks.TierMultiblockParallelUpgrade tier : ModBlocks.TierMultiblockParallelUpgrade.values()) {
            addBlock(tier.getBlock(), translateMultiblockParallelUpgradeName(tier.getEnglishDisplayName()));
        }

        addBlock(ModBlocks.COBBLESTONE_MACHINE_CASING, "丸石機械筐体");
        for (ModBlocks.TierCobblestoneMachineCasing tier : ModBlocks.TierCobblestoneMachineCasing.values()) {
            addBlock(
                tier.getBlock(),
                translateTierBlockName(tier.getEnglishDisplayName(), "Cobblestone Machine Casing", "丸石機械筐体")
            );
        }

        for (ModBlocks.TierCobblestoneGenerator generatorVariant : ModBlocks.TierCobblestoneGenerator.values()) {
            if (generatorVariant.hasTier()) {
                addBlock(
                    generatorVariant.getBlock(),
                    translateTierBlockName(
                        generatorVariant.getEnglishDisplayName(),
                        "Cobblestone Generator " + generatorVariant.getSize().getDisplayName(),
                        "丸石ジェネレーター " + generatorVariant.getSize().getDisplayName()
                    )
                );
            } else {
                addBlock(
                    generatorVariant.getBlock(),
                    "丸石ジェネレーター " + generatorVariant.getSize().getDisplayName()
                );
            }
        }

        add("automation_mode.cobblestonexxcompressed.disabled", "OFF");
        add("automation_mode.cobblestonexxcompressed.input", "入力");
        add("automation_mode.cobblestonexxcompressed.input1", "入力1");
        add("automation_mode.cobblestonexxcompressed.input2", "入力2");
        add("automation_mode.cobblestonexxcompressed.output", "出力");
        add("automation_mode.cobblestonexxcompressed.output1", "出力1");
        add("automation_mode.cobblestonexxcompressed.output2", "出力2");
        add("automation_mode.cobblestonexxcompressed.in_out", "入出力");
        add("automation_mode.cobblestonexxcompressed.cobblestone_input", "丸石入力");
        add("gui.cobblestonexxcompressed.automation.up", "上");
        add("gui.cobblestonexxcompressed.automation.down", "下");
        add("gui.cobblestonexxcompressed.automation.front", "前");
        add("gui.cobblestonexxcompressed.automation.back", "後");
        add("gui.cobblestonexxcompressed.automation.left", "左");
        add("gui.cobblestonexxcompressed.automation.right", "右");
        add("gui.cobblestonexxcompressed.cobblestone_power", "CP");
        add("gui.cobblestonexxcompressed.shield", "シールド");
        add("gui.cobblestonexxcompressed.shield_conversion", "合計 %s CP / %s CP/t");
        add("gui.cobblestonexxcompressed.shield_generation", "生成: %s/t");
        add("gui.cobblestonexxcompressed.fe_energy", "FE エネルギー");
        add("gui.cobblestonexxcompressed.convert_fe_rate", "FE 変換量");
        add("gui.cobblestonexxcompressed.input_fe_rate", "FE 入力量");
        add("gui.cobblestonexxcompressed.output_fe_rate", "FE 出力量");
        add("gui.cobblestonexxcompressed.fluid", "液体");
        add("gui.cobblestonexxcompressed.item", "アイテム");
        add("gui.cobblestonexxcompressed.fluid_amount", "液体量");
        add("gui.cobblestonexxcompressed.stored_amount", "保管量");
        add("gui.cobblestonexxcompressed.empty", "空");
        add("gui.cobblestonexxcompressed.auto_export", "自動搬出");
        add("gui.cobblestonexxcompressed.auto_insert", "自動搬入");
        add("gui.cobblestonexxcompressed.void_overflow", "上限超過を消滅");
        add("gui.cobblestonexxcompressed.mute_sound", "消音");
        add("gui.cobblestonexxcompressed.filter.whitelist", "WL");
        add("gui.cobblestonexxcompressed.filter.blacklist", "BL");
        add("gui.cobblestonexxcompressed.filter.open", "フィルタ");
        add("gui.cobblestonexxcompressed.filter.close", "閉じる");
        add("gui.cobblestonexxcompressed.start_stop", "開始/停止");
        add("gui.cobblestonexxcompressed.multiblock.structure", "構造");
        add("gui.cobblestonexxcompressed.multiblock.guide_orientation", "下=正面");
        add("gui.cobblestonexxcompressed.multiblock.formed", "完成");
        add("gui.cobblestonexxcompressed.multiblock.incomplete", "未完成");
        add("gui.cobblestonexxcompressed.multiblock.cell_matched", "OK  (%s,%s,%s)");
        add("gui.cobblestonexxcompressed.multiblock.cell_mismatch", "不足/不一致  (%s,%s,%s)");
        add("multiblock_cell.cobblestonexxcompressed.core", "コア");
        add("multiblock_cell.cobblestonexxcompressed.air", "空気");
        add("multiblock_cell.cobblestonexxcompressed.casing", "筐体");
        add("multiblock_cell.cobblestonexxcompressed.inout", "IO(ポート/筐体)");
        add("multiblock_cell.cobblestonexxcompressed.item_in", "アイテム入力");
        add("multiblock_cell.cobblestonexxcompressed.item_out", "アイテム出力");
        add("multiblock_cell.cobblestonexxcompressed.fluid_in", "流体入力");
        add("multiblock_cell.cobblestonexxcompressed.fluid_out", "流体出力");
        add("multiblock_cell.cobblestonexxcompressed.cobble_in", "丸石入力");
        add("multiblock_cell.cobblestonexxcompressed.upgrade", "アップグレード");
        add("gui.cobblestonexxcompressed.water_generator.convert_rate", "%s mB/t");
        add("gui.cobblestonexxcompressed.water_generator.cp_rate", "%s CP/t");
        add("jei.cobblestonexxcompressed.compressed_stone_loot", "圧縮石ドロップ");
        add("jei.cobblestonexxcompressed.water_generator_conversion", "丸石水生成機");
        add("jei.cobblestonexxcompressed.water_generator_conversion.rate", "1 CP = 1 mB");
        add("jei.cobblestonexxcompressed.silk_touch", "シルクタッチ");
        add("jei.cobblestonexxcompressed.no_silk_touch", "シルクタッチなし");
        add("jei.cobblestonexxcompressed.chance", "確率: %s");
        add("jei.cobblestonexxcompressed.count_range_fortune", "個数: %s-%s (幸運)");
        add("jei.cobblestonexxcompressed.cp_supply.fuel", "丸石・圧縮丸石: 1丸石/t → CP");
        add("jei.cobblestonexxcompressed.cp_supply.catalyst", "丸石ジェネレータ: 消費なしでCP供給");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone.compression", "x%s 圧縮");
        add("tooltip.cobblestonexxcompressed.tier", "Tier %s");
        add("tooltip.cobblestonexxcompressed.cobblestone_energized_cube.capacity", "x%s CP容量");
        add("tooltip.cobblestonexxcompressed.cobblestone_acceleration_chip.rate", "x%s CP/t");
        add("tooltip.cobblestonexxcompressed.shield_range_module.bonus", "範囲 +%s");
        add("tooltip.cobblestonexxcompressed.shield_rate_module.bonus", "変換量 +%s（総消費CPも増加）");
        add("tooltip.cobblestonexxcompressed.shield_capacity_module.bonus", "シールド上限 +%s");
        add("tooltip.cobblestonexxcompressed.shield_base_module.description", "他のシールドモジュールの材料です");
        add("tooltip.cobblestonexxcompressed.cobblestone_generator.catalyst_rate", "機械の丸石スロットに置くと、消費せず %s CP/t を供給します。");
        add("tooltip.cobblestonexxcompressed.cobblestone_parallel_chip.extra", "追加処理 +%s");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone_pickaxe.stone_break_simulator_bonus", "石破壊シミュレーター: 耐久相当 +%s");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone_armor.custom_protection.full_set", "独自防御（4部位）: %s%%");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone_armor.custom_protection.piece", "独自防御（この部位）: %s%%");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone_armor.knockback_immunity", "ノックバック無効");

        for (ModItems.CompressedCobblestoneArmorPiece piece : ModItems.CompressedCobblestoneArmorPiece.values()) {
            addItem(
                ModItems.getBaseCompressedCobblestoneArmor(piece),
                translateBaseArmorName(piece.getEnglishDisplaySuffix())
            );
        }
        for (ModItems.TierCompressedCobblestoneArmor tier : ModItems.TierCompressedCobblestoneArmor.values()) {
            for (ModItems.CompressedCobblestoneArmorPiece piece : ModItems.CompressedCobblestoneArmorPiece.values()) {
                addItem(
                    tier.getItem(piece),
                    translateTierArmorName(tier.getEnglishDisplayName(piece), piece.getEnglishDisplaySuffix())
                );
            }
        }

        addItem(ModItems.FLYING_STONE, "飛行石");
        addItem(ModItems.SHIELD_BASE_MODULE, "シールドベースモジュール");
        for (ModItems.TierShieldBaseModule tier : ModItems.TierShieldBaseModule.values()) {
            addItem(tier.getItem(), translateTierBlockName(tier.getEnglishDisplayName(), "Shield Base Module", "シールドベースモジュール"));
        }
        addItem(ModItems.SHIELD_RANGE_MODULE, "シールド範囲モジュール");
        for (ModItems.TierShieldRangeModule tier : ModItems.TierShieldRangeModule.values()) {
            addItem(tier.getItem(), translateTierBlockName(tier.getEnglishDisplayName(), "Shield Range Module", "シールド範囲モジュール"));
        }
        addItem(ModItems.SHIELD_RATE_MODULE, "シールド変換量モジュール");
        for (ModItems.TierShieldRateModule tier : ModItems.TierShieldRateModule.values()) {
            addItem(tier.getItem(), translateTierBlockName(tier.getEnglishDisplayName(), "Shield Conversion Module", "シールド変換量モジュール"));
        }
        addItem(ModItems.SHIELD_CAPACITY_MODULE, "シールド容量モジュール");
        for (ModItems.TierShieldCapacityModule tier : ModItems.TierShieldCapacityModule.values()) {
            addItem(tier.getItem(), translateTierBlockName(tier.getEnglishDisplayName(), "Shield Capacity Module", "シールド容量モジュール"));
        }
        add("tooltip.cobblestonexxcompressed.flying_stone.description", "インベントリか装飾品スロットにあるあいだ、クリエイティブ飛行が使えます。バルス!!!!!!");
        add("tooltip.cobblestonexxcompressed.configuration_card.empty", "設定は保存されていません");
        add("tooltip.cobblestonexxcompressed.configuration_card.stored", "保存先: %s");
        add("tooltip.cobblestonexxcompressed.configuration_card.upgrade", "アップグレード: %s");
        add("tooltip.cobblestonexxcompressed.configuration_card.power", "CP入力: %s");
        add("config.jade.plugin_cobblestonexxcompressed.cobblestone_drawer_storage", "丸石ドロワーの収納");
        add("jade.cobblestonexxcompressed.cobblestone_drawer.capacity", "%s / %s");
        add("message.cobblestonexxcompressed.configuration_card.copied", "%s の設定をコピーしました");
        add("message.cobblestonexxcompressed.configuration_card.pasted", "%s に設定を貼り付けました");
        add("message.cobblestonexxcompressed.configuration_card.cleared", "保存した設定を消去しました");
        add("message.cobblestonexxcompressed.configuration_card.incompatible", "同じ機械にだけ貼り付けできます");
        add("commands.cobblestonexxcompressed.sethp.success.single", "%s の体力を setHealth で %s にしました");
        add("commands.cobblestonexxcompressed.sethp.success.multiple", "%s 人の体力を setHealth で %s にしました");

        addCobblestoneBreadFlavorTooltips();
        TooltipTranslationEntries.addJapanese(this);
    }

    private void addCobblestoneBreadFlavorTooltips() {
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("cobblestone_bread"),
            "丸石をそのまま焼いたような、いびつな工業食。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_copper_cobblestone_bread"),
            "初めての工業食。ちょっと硬い。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_iron_cobblestone_bread"),
            "体が石みたいに固くなる。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_gold_cobblestone_bread"),
            "幸運の香りがする黄金色のパン。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_amethyst_cobblestone_bread"),
            "洞窟の紫水晶を閉じ込めた。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_aquamarine_cobblestone_bread"),
            "海の丸石を練り込んだ。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_topaz_cobblestone_bread"),
            "軽く跳べる黄金色のパン。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_ruby_cobblestone_bread"),
            "かじるたびに力がみなぎる赤いパン。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_sapphire_cobblestone_bread"),
            "風のように走れる青いパン。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_diamond_cobblestone_bread"),
            "硬くて、とにかく硬い。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_emerald_cobblestone_bread"),
            "商人も喜ぶ幸運の結晶パン。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_netherite_cobblestone_bread"),
            "地獄の炎でも焼け残る。"
        );
        add(
            TooltipTranslationKeys.cobblestoneBreadDescription("tier_obsidian_cobblestone_bread"),
            "究極の石パン。噛むたびに世界が揺れる。"
        );
    }

    // tier 付きブロック名は英語表示名から機械名と素材名を分けて置換します。
    private static String translateTierBlockName(String englishDisplayName, String englishSuffix, String japaneseSuffix) {
        return translateTierMaterialPrefix(englishDisplayName.replace(englishSuffix, japaneseSuffix));
    }

    private static String translateMultiblockAccelerationUpgradeName(String englishDisplayName) {
        return translateTierMaterialPrefix(englishDisplayName.replace("Multiblock Acceleration Upgrade", "マルチブロック加速アップグレード"));
    }

    private static String translateMultiblockEnergizedUpgradeName(String englishDisplayName) {
        return translateTierMaterialPrefix(englishDisplayName.replace("Multiblock Energized Upgrade", "マルチブロック蓄電アップグレード"));
    }

    private static String translateMultiblockParallelUpgradeName(String englishDisplayName) {
        return translateTierMaterialPrefix(englishDisplayName.replace("Multiblock Parallel Upgrade", "マルチブロック並列アップグレード"));
    }

    private static String translateTierMaterialPrefix(String name) {
        return name
            .replace("Copper ", "銅")
            .replace("Iron ", "鉄")
            .replace("Gold ", "金")
            .replace("Amethyst ", "アメジスト")
            .replace("Aquamarine ", "アクアマリン")
            .replace("Topaz ", "トパーズ")
            .replace("Ruby ", "ルビー")
            .replace("Sapphire ", "サファイア")
            .replace("Diamond ", "ダイヤモンド")
            .replace("Emerald ", "エメラルド")
            .replace("Netherite ", "ネザライト")
            .replace("Obsidian ", "黒曜石");
    }

    private static String translateBaseArmorName(String englishPieceSuffix) {
        return "圧縮丸石" + translateArmorPieceSuffix(englishPieceSuffix);
    }

    private static String translateTierArmorName(String englishDisplayName, String englishPieceSuffix) {
        return translateTierMaterialPrefix(
            englishDisplayName.replace("Compressed Cobblestone " + englishPieceSuffix, "圧縮丸石" + translateArmorPieceSuffix(englishPieceSuffix))
        );
    }

    private static String translateArmorPieceSuffix(String englishPieceSuffix) {
        return switch (englishPieceSuffix) {
            case "Helmet" -> "ヘルメット";
            case "Chestplate" -> "チェストプレート";
            case "Leggings" -> "レギンス";
            case "Boots" -> "ブーツ";
            default -> englishPieceSuffix;
        };
    }
}
