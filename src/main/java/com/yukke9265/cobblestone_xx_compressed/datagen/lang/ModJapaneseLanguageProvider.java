package com.yukke9265.cobblestone_xx_compressed.datagen.lang;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

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
        addBlock(ModBlocks.COBBLESTONE_POWERED_FURNACE, "丸石動力かまど");
        addBlock(ModBlocks.COBBLESTONE_EXTREME_COMPRESSOR, "丸石エクストリームコンプレッサー");
        addBlock(ModBlocks.COBBLESTONE_CRUSHER, "丸石クラッシャー");
        addBlock(ModBlocks.COBBLESTONE_CENTRIFUGE, "丸石遠心分離機");
        addBlock(ModBlocks.COBBLESTONE_LASER_DRILL, "丸石レーザードリル");
        addBlock(ModBlocks.COBBLESTONE_MELTER, "丸石溶解機");
        addBlock(ModBlocks.COBBLESTONE_ASSEMBLY_MACHINE, "丸石組立機");
        addBlock(ModBlocks.COBBLESTONE_ENCHANTER, "丸石エンチャンター");
        addBlock(ModBlocks.COBBLESTONE_CHEMICAL_REACTOR, "丸石化学反応機");
        addBlock(ModBlocks.COBBLESTONE_MIXER, "丸石ミキサー");
        addBlock(ModBlocks.STONE_BREAK_SIMULATOR, "石破壊シミュレーター");
        addBlock(ModBlocks.COBBLESTONE_REACTION_CHAMBER, "丸石反応槽");
        addBlock(ModBlocks.COBBLESTONE_CRYSTALLIZATION_CHAMBER, "丸石結晶化槽");
        addBlock(ModBlocks.COBBLESTONE_DISSOLUTION_CHAMBER, "丸石溶解槽");
        addBlock(ModBlocks.COBBLESTONE_FLUID_MIXER, "丸石流体ミキサー");

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
        add("gui.cobblestonexxcompressed.fe_energy", "FE エネルギー");
        add("gui.cobblestonexxcompressed.convert_fe_rate", "FE 変換量");
        add("gui.cobblestonexxcompressed.output_fe_rate", "FE 出力量");
        add("gui.cobblestonexxcompressed.fluid", "液体");
        add("gui.cobblestonexxcompressed.item", "アイテム");
        add("gui.cobblestonexxcompressed.fluid_amount", "液体量");
        add("gui.cobblestonexxcompressed.empty", "空");
        add("gui.cobblestonexxcompressed.auto_export", "自動搬出");
        add("gui.cobblestonexxcompressed.start_stop", "開始/停止");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone.compression", "x%s 圧縮");
        add("tooltip.cobblestonexxcompressed.cobblestone_energized_cube.capacity", "x%s CP容量");
        add("tooltip.cobblestonexxcompressed.cobblestone_acceleration_chip.rate", "x%s CP/t");

        TooltipTranslationEntries.addJapanese(this);
    }
}