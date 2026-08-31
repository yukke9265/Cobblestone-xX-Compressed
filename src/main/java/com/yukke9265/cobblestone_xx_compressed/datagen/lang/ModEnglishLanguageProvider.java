package com.yukke9265.cobblestone_xx_compressed.datagen.lang;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModEnglishLanguageProvider extends LanguageProvider {
    public ModEnglishLanguageProvider(PackOutput output) {
        super(output, CobblestonexXCompressed.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // まずは既存の en_us.json に入っていた内容を、そのまま datagen 側へ移します。
        // こうしておくと、英語名の追加や修正が Java 側 1 か所にまとまり、
        // 手書き JSON と generated JSON のどちらを見るべきか迷いにくくなります。
        add("itemGroup.cobblestonexxcompressed", "Cobblestone xXCompressed");
        add("block.cobblestonexxcompressed.example_block", "Example Block");
        add("item.cobblestonexxcompressed.example_item", "Example Item");

        add("cobblestonexxcompressed.configuration.title", "Cobblestone xXCompressed Configs");
        add("cobblestonexxcompressed.configuration.section.cobblestonexxcompressed.common.toml", "Cobblestone xXCompressed Configs");
        add("cobblestonexxcompressed.configuration.section.cobblestonexxcompressed.common.toml.title", "Cobblestone xXCompressed Configs");
        add("cobblestonexxcompressed.configuration.items", "Item List");
        add("cobblestonexxcompressed.configuration.logDirtBlock", "Log Dirt Block");
        add("cobblestonexxcompressed.configuration.magicNumberIntroduction", "Magic Number Text");
        add("cobblestonexxcompressed.configuration.magicNumber", "Magic Number");

        // 丸石パンは通常版から tier 版までまとめてここで管理します。
        // addItem(...) を使うと、登録済みアイテムと翻訳キーが自動で対応するため、
        // 文字列キーを手打ちする量を減らせます。
        addItem(ModItems.COBBLESTONE_BREAD, "Cobblestone Bread");

        for (ModItems.TierCobblestoneBread tier : ModItems.TierCobblestoneBread.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_GEM, "Cobblestone Gem");
        for (ModItems.TierCobblestoneGem tier : ModItems.TierCobblestoneGem.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }
        addItem(ModItems.AQUAMARINE_SHARD, "Aquamarine Shard");
        addItem(ModItems.TOPAZ_SHARD, "Topaz Shard");
        addItem(ModItems.RUBY_SHARD, "Ruby Shard");
        addItem(ModItems.SAPPHIRE_SHARD, "Sapphire Shard");
        for (ModItems.GemDust dust : ModItems.GemDust.values()) {
            addItem(dust.getItem(), dust.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_DUST, "Cobblestone Dust");
        for (ModItems.TierCobblestoneDust tier : ModItems.TierCobblestoneDust.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_DIRTY_DUST, "Cobblestone Dirty Dust");
        for (ModItems.TierCobblestoneDirtyDust tier : ModItems.TierCobblestoneDirtyDust.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_MIXED_DUST, "Cobblestone Mixed Dust");
        for (ModItems.TierCobblestoneMixedDust tier : ModItems.TierCobblestoneMixedDust.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_PURE_DUST, "Cobblestone Pure Dust");
        for (ModItems.TierCobblestonePureDust tier : ModItems.TierCobblestonePureDust.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_BIT, "Compressed Cobblestone Singularity Bit");
        for (ModItems.TierCompressedCobblestoneSingularityBit tier : ModItems.TierCompressedCobblestoneSingularityBit.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT, "Compressed Cobblestone Singularity Fragment");
        for (ModItems.TierCompressedCobblestoneSingularityFragment tier : ModItems.TierCompressedCobblestoneSingularityFragment.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY, "Compressed Cobblestone Singularity");
        for (ModItems.TierCompressedCobblestoneSingularity tier : ModItems.TierCompressedCobblestoneSingularity.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.DIRTY_MIXTURE, "Dirty Mixture");
        for (ModItems.MixtureItem mixture : ModItems.MixtureItem.values()) {
            addItem(mixture.getItem(), mixture.getEnglishDisplayName());
        }

        addItem(ModItems.COAL_DUST, "Coal Dust");
        addItem(ModItems.OBSIDIAN_DUST, "Obsidian Dust");
        addItem(ModItems.CANNED_STABLE_ENDERITE, "Canned Stable Enderite");
        addItem(ModItems.EXTREME_COMPRESSED_CANNED_ENDERITE, "Extreme Compressed Canned Enderite");
        addItem(ModItems.EXTREME_COMPRESSED_OBSIDIAN, "Extreme Compressed Obsidian");
        addItem(ModItems.CONFIGURATION_CARD, "Configuration Card");
        addItem(ModItems.FLYING_STONE, "Flying Stone");

        addItem(ModItems.COBBLESTONE_WIRE, "Cobblestone Wire");
        for (ModItems.TierCobblestoneWire tier : ModItems.TierCobblestoneWire.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_ROD, "Cobblestone Rod");
        for (ModItems.TierCobblestoneRod tier : ModItems.TierCobblestoneRod.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COMPRESSED_COBBLESTONE_PICKAXE, "Compressed Cobblestone Pickaxe");
        for (ModItems.TierCompressedCobblestonePickaxe tier : ModItems.TierCompressedCobblestonePickaxe.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        for (ModItems.CompressedCobblestoneArmorPiece piece : ModItems.CompressedCobblestoneArmorPiece.values()) {
            addItem(
                ModItems.getBaseCompressedCobblestoneArmor(piece),
                "Compressed Cobblestone " + piece.getEnglishDisplaySuffix()
            );
        }
        for (ModItems.TierCompressedCobblestoneArmor tier : ModItems.TierCompressedCobblestoneArmor.values()) {
            for (ModItems.CompressedCobblestoneArmorPiece piece : ModItems.CompressedCobblestoneArmorPiece.values()) {
                addItem(tier.getItem(piece), tier.getEnglishDisplayName(piece));
            }
        }

        addItem(ModItems.COBBLESTONE_MOTOR, "Cobblestone Motor");
        for (ModItems.TierCobblestoneMotor tier : ModItems.TierCobblestoneMotor.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_CIRCUIT, "Cobblestone Circuit");
        for (ModItems.TierCobblestoneCircuit tier : ModItems.TierCobblestoneCircuit.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_PROCESSOR, "Cobblestone Processor");
        for (ModItems.TierCobblestoneProcessor tier : ModItems.TierCobblestoneProcessor.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_CIRCUIT_PACKAGE, "Cobblestone Circuit Package");
        for (ModItems.TierCobblestoneCircuitPackage tier : ModItems.TierCobblestoneCircuitPackage.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_ACCELERATION_CHIP, "Cobblestone Acceleration Chip");
        for (ModItems.TierCobblestoneAccelerationChip tier : ModItems.TierCobblestoneAccelerationChip.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_ENERGIZED_CUBE, "Cobblestone Energized Cube");
        for (ModItems.TierCobblestoneEnergizedCube tier : ModItems.TierCobblestoneEnergizedCube.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.COBBLESTONE_PARALLEL_CHIP, "Cobblestone Parallel Chip");
        for (ModItems.TierCobblestoneParallelChip tier : ModItems.TierCobblestoneParallelChip.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.SHIELD_BASE_MODULE, "Shield Base Module");
        for (ModItems.TierShieldBaseModule tier : ModItems.TierShieldBaseModule.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.SHIELD_RANGE_MODULE, "Shield Range Module");
        for (ModItems.TierShieldRangeModule tier : ModItems.TierShieldRangeModule.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.SHIELD_RATE_MODULE, "Shield Conversion Module");
        for (ModItems.TierShieldRateModule tier : ModItems.TierShieldRateModule.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        addItem(ModItems.SHIELD_CAPACITY_MODULE, "Shield Capacity Module");
        for (ModItems.TierShieldCapacityModule tier : ModItems.TierShieldCapacityModule.values()) {
            addItem(tier.getItem(), tier.getEnglishDisplayName());
        }

        add("fluid_type.cobblestonexxcompressed.molten_compressed_cobblestone", "Molten Compressed Cobblestone");
        addBlock(ModFluids.MOLTEN_COMPRESSED_COBBLESTONE.getFluidBlock(), "Molten Compressed Cobblestone");
        addItem(ModFluids.MOLTEN_COMPRESSED_COBBLESTONE.getBucketItem(), "Molten Compressed Cobblestone Bucket");
        for (ModFluids.TierMoltenCompressedCobblestone tier : ModFluids.TierMoltenCompressedCobblestone.values()) {
            add("fluid_type.cobblestonexxcompressed." + tier.getRegistryName(), tier.getEnglishDisplayName());
            addBlock(tier.getFluidEntry().getFluidBlock(), tier.getEnglishDisplayName());
            addItem(tier.getFluidEntry().getBucketItem(), tier.getBucketEnglishDisplayName());
        }

        add("fluid_type.cobblestonexxcompressed.molten_dirty_compressed_cobblestone", "Molten Dirty Compressed Cobblestone");
        addBlock(ModFluids.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE.getFluidBlock(), "Molten Dirty Compressed Cobblestone");
        addItem(ModFluids.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE.getBucketItem(), "Molten Dirty Compressed Cobblestone Bucket");
        for (ModFluids.TierMoltenDirtyCompressedCobblestone tier : ModFluids.TierMoltenDirtyCompressedCobblestone.values()) {
            add("fluid_type.cobblestonexxcompressed." + tier.getRegistryName(), tier.getEnglishDisplayName());
            addBlock(tier.getFluidEntry().getFluidBlock(), tier.getEnglishDisplayName());
            addItem(tier.getFluidEntry().getBucketItem(), tier.getBucketEnglishDisplayName());
        }

        for (ModFluids.WaterBasedFluid fluid : ModFluids.WaterBasedFluid.values()) {
            add("fluid_type.cobblestonexxcompressed." + fluid.getRegistryName(), fluid.getEnglishDisplayName());
            addBlock(fluid.getFluidEntry().getFluidBlock(), fluid.getEnglishDisplayName());
            addItem(fluid.getFluidEntry().getBucketItem(), fluid.getBucketEnglishDisplayName());
        }

        addBlock(ModBlocks.COMPRESSED_COBBLESTONE, "Compressed Cobblestone");
        for (ModBlocks.TierCompressedCobblestone tier : ModBlocks.TierCompressedCobblestone.values()) {
            addBlock(tier.getBlock(), tier.getEnglishDisplayName());
        }

        addBlock(ModBlocks.COMPRESSED_STONE, "Compressed Stone");
        for (ModBlocks.TierCompressedStone tier : ModBlocks.TierCompressedStone.values()) {
            addBlock(tier.getBlock(), tier.getEnglishDisplayName());
        }

        addBlock(ModBlocks.COBBLESTONE_MACHINE_CASING, "Cobblestone Machine Casing");

        for (ModBlocks.TierCobblestoneMachineCasing tier : ModBlocks.TierCobblestoneMachineCasing.values()) {
            addBlock(tier.getBlock(), tier.getEnglishDisplayName());
        }

        addBlock(ModBlocks.COBBLESTONE_TANK, "Cobblestone Tank");
        for (ModBlocks.TierCobblestoneTank tier : ModBlocks.TierCobblestoneTank.values()) {
            addBlock(tier.getBlock(), tier.getEnglishDisplayName());
        }

        addBlock(ModBlocks.COBBLESTONE_DRAWER, "Cobblestone Drawer");
        for (ModBlocks.TierCobblestoneDrawer tier : ModBlocks.TierCobblestoneDrawer.values()) {
            addBlock(tier.getBlock(), tier.getEnglishDisplayName());
        }

        addBlock(ModBlocks.COBBLESTONE_FE_CUBE, "Cobblestone FE Cube");
        for (ModBlocks.TierCobblestoneFeCube tier : ModBlocks.TierCobblestoneFeCube.values()) {
            addBlock(tier.getBlock(), tier.getEnglishDisplayName());
        }

        addBlock(ModBlocks.COBBLESTONE_FURNACE, "Cobblestone Furnace");
        addBlock(ModBlocks.COBBLESTONE_FE_GENERATOR, "Cobblestone FE Generator");
        addBlock(ModBlocks.COBBLESTONE_POWERED_FURNACE, "Cobblestone Powered Furnace");
        addBlock(ModBlocks.COBBLESTONE_EXTREME_COMPRESSOR, "Cobblestone Extreme Compressor");
        addBlock(ModBlocks.COBBLESTONE_CRUSHER, "Cobblestone Crusher");
        addBlock(ModBlocks.COBBLESTONE_SHIELD_PROJECTOR, "Cobblestone Shield Projector");
        addBlock(ModBlocks.COBBLESTONE_CENTRIFUGE, "Cobblestone Centrifuge");
        addBlock(ModBlocks.COBBLESTONE_LASER_DRILL, "Cobblestone Laser Drill");
        addBlock(ModBlocks.COBBLESTONE_MELTER, "Cobblestone Melter");
        addBlock(ModBlocks.COBBLESTONE_ASSEMBLY_MACHINE, "Cobblestone Assembly Machine");
        addBlock(ModBlocks.COBBLESTONE_ENCHANTER, "Cobblestone Enchanter");
        addBlock(ModBlocks.COBBLESTONE_CHEMICAL_REACTOR, "Cobblestone Chemical Reactor");
        addBlock(ModBlocks.COBBLESTONE_MIXER, "Cobblestone Mixer");
        addBlock(ModBlocks.COBBLESTONE_POWERED_CRAFTER, "Cobblestone Powered Crafter");
        addBlock(ModBlocks.STONE_BREAK_SIMULATOR, "Stone Break Simulator");
        addBlock(ModBlocks.COBBLESTONE_REACTION_CHAMBER, "Cobblestone Reaction Chamber");
        addBlock(ModBlocks.COBBLESTONE_CRYSTALLIZATION_CHAMBER, "Cobblestone Crystallization Chamber");
        addBlock(ModBlocks.COBBLESTONE_DISSOLUTION_CHAMBER, "Cobblestone Dissolution Chamber");
        addBlock(ModBlocks.COBBLESTONE_FLUID_MIXER, "Cobblestone Fluid Mixer");
        addBlock(ModBlocks.COBBLESTONE_WATER_GENERATOR, "Cobblestone Water Generator");
        addBlock(ModBlocks.COBBLESTONE_MULTIBLOCK_CRUSHER, "Cobblestone Multiblock Crusher");
        addBlock(ModBlocks.MULTIBLOCK_ITEM_INPUT_PORT, "Multiblock Item Input Port");
        addBlock(ModBlocks.MULTIBLOCK_ITEM_OUTPUT_PORT, "Multiblock Item Output Port");
        addBlock(ModBlocks.MULTIBLOCK_FLUID_INPUT_PORT, "Multiblock Fluid Input Port");
        addBlock(ModBlocks.MULTIBLOCK_FLUID_OUTPUT_PORT, "Multiblock Fluid Output Port");
        addBlock(ModBlocks.MULTIBLOCK_COBBLE_INPUT_PORT, "Multiblock Cobble Input Port");
        addBlock(ModBlocks.MULTIBLOCK_ACCELERATION_UPGRADE, "Multiblock Acceleration Upgrade");
        addBlock(ModBlocks.MULTIBLOCK_ENERGIZED_UPGRADE, "Multiblock Energized Upgrade");
        addBlock(ModBlocks.MULTIBLOCK_PARALLEL_UPGRADE, "Multiblock Parallel Upgrade");
        for (ModBlocks.TierMultiblockAccelerationUpgrade tier : ModBlocks.TierMultiblockAccelerationUpgrade.values()) {
            addBlock(tier.getBlock(), tier.getEnglishDisplayName());
        }
        for (ModBlocks.TierMultiblockEnergizedUpgrade tier : ModBlocks.TierMultiblockEnergizedUpgrade.values()) {
            addBlock(tier.getBlock(), tier.getEnglishDisplayName());
        }
        for (ModBlocks.TierMultiblockParallelUpgrade tier : ModBlocks.TierMultiblockParallelUpgrade.values()) {
            addBlock(tier.getBlock(), tier.getEnglishDisplayName());
        }

        for (ModBlocks.TierCobblestoneGenerator generatorVariant : ModBlocks.TierCobblestoneGenerator.values()) {
            if (generatorVariant.hasTier()) {
                addBlock(generatorVariant.getBlock(), generatorVariant.getEnglishDisplayName());
            } else {
                addBlock(generatorVariant.getBlock(), generatorVariant.getEnglishDisplayName());
            }
        }

        add("automation_mode.cobblestonexxcompressed.disabled", "Off");
        add("automation_mode.cobblestonexxcompressed.input", "Input");
        add("automation_mode.cobblestonexxcompressed.input1", "Input1");
        add("automation_mode.cobblestonexxcompressed.input2", "Input2");
        add("automation_mode.cobblestonexxcompressed.output", "Output");
        add("automation_mode.cobblestonexxcompressed.output1", "Output1");
        add("automation_mode.cobblestonexxcompressed.output2", "Output2");
        add("automation_mode.cobblestonexxcompressed.in_out", "In/Out");
        add("automation_mode.cobblestonexxcompressed.cobblestone_input", "Cobblestone Input");
        add("gui.cobblestonexxcompressed.automation.up", "Up");
        add("gui.cobblestonexxcompressed.automation.down", "Down");
        add("gui.cobblestonexxcompressed.automation.front", "Front");
        add("gui.cobblestonexxcompressed.automation.back", "Back");
        add("gui.cobblestonexxcompressed.automation.left", "Left");
        add("gui.cobblestonexxcompressed.automation.right", "Right");
        add("gui.cobblestonexxcompressed.cobblestone_power", "CP");
        add("gui.cobblestonexxcompressed.shield", "Shield");
        add("gui.cobblestonexxcompressed.shield_conversion", "%s total CP / %s CP/t");
        add("gui.cobblestonexxcompressed.shield_generation", "Gen: %s/t");
        add("gui.cobblestonexxcompressed.fe_energy", "FE Energy");
        add("gui.cobblestonexxcompressed.convert_fe_rate", "Convert FE");
        add("gui.cobblestonexxcompressed.input_fe_rate", "Input FE");
        add("gui.cobblestonexxcompressed.output_fe_rate", "Output FE");
        add("gui.cobblestonexxcompressed.fluid", "Fluid");
        add("gui.cobblestonexxcompressed.item", "Item");
        add("gui.cobblestonexxcompressed.fluid_amount", "Fluid Amount");
        add("gui.cobblestonexxcompressed.stored_amount", "Stored Amount");
        add("gui.cobblestonexxcompressed.empty", "Empty");
        add("gui.cobblestonexxcompressed.auto_export", "Auto Export");
        add("gui.cobblestonexxcompressed.auto_insert", "Auto Insert");
        add("gui.cobblestonexxcompressed.void_overflow", "Void Overflow");
        add("gui.cobblestonexxcompressed.mute_sound", "Mute");
        add("gui.cobblestonexxcompressed.filter.whitelist", "WL");
        add("gui.cobblestonexxcompressed.filter.blacklist", "BL");
        add("gui.cobblestonexxcompressed.filter.open", "Filter");
        add("gui.cobblestonexxcompressed.filter.close", "Close");
        add("gui.cobblestonexxcompressed.start_stop", "Start/Stop");
        add("gui.cobblestonexxcompressed.multiblock.structure", "Structure");
        add("gui.cobblestonexxcompressed.multiblock.guide_orientation", "Down=Front");
        add("gui.cobblestonexxcompressed.multiblock.formed", "Formed");
        add("gui.cobblestonexxcompressed.multiblock.incomplete", "Incomplete");
        add("gui.cobblestonexxcompressed.multiblock.cell_matched", "OK  (%s,%s,%s)");
        add("gui.cobblestonexxcompressed.multiblock.cell_mismatch", "Missing/Mismatch  (%s,%s,%s)");
        add("multiblock_cell.cobblestonexxcompressed.core", "Core");
        add("multiblock_cell.cobblestonexxcompressed.air", "Air");
        add("multiblock_cell.cobblestonexxcompressed.casing", "Casing");
        add("multiblock_cell.cobblestonexxcompressed.inout", "I/O (Port/Casing)");
        add("multiblock_cell.cobblestonexxcompressed.item_in", "Item Input");
        add("multiblock_cell.cobblestonexxcompressed.item_out", "Item Output");
        add("multiblock_cell.cobblestonexxcompressed.fluid_in", "Fluid Input");
        add("multiblock_cell.cobblestonexxcompressed.fluid_out", "Fluid Output");
        add("multiblock_cell.cobblestonexxcompressed.cobble_in", "Cobble Input");
        add("multiblock_cell.cobblestonexxcompressed.upgrade", "Upgrade");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone.compression", "x%s compressed");
        add("tooltip.cobblestonexxcompressed.tier", "Tier %s");
        add("tooltip.cobblestonexxcompressed.cobblestone_energized_cube.capacity", "x%s CP capacity");
        add("tooltip.cobblestonexxcompressed.cobblestone_acceleration_chip.rate", "x%s CP/t");
        add("tooltip.cobblestonexxcompressed.shield_range_module.bonus", "Range +%s");
        add("tooltip.cobblestonexxcompressed.shield_rate_module.bonus", "Shield conversion +%s (total CP also increases)");
        add("tooltip.cobblestonexxcompressed.shield_capacity_module.bonus", "Max shield +%s");
        add("tooltip.cobblestonexxcompressed.shield_base_module.description", "Used to craft other shield modules");
        add("tooltip.cobblestonexxcompressed.cobblestone_generator.catalyst_rate", "Place in a machine cobblestone slot to supply %s CP/t without being consumed.");
        add("tooltip.cobblestonexxcompressed.cobblestone_parallel_chip.extra", "+%s extra crafts");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone_pickaxe.stone_break_simulator_bonus", "Stone Break Simulator: +%s Unbreaking");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone_armor.custom_protection.full_set", "Custom Protection (Full Set): %s%%");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone_armor.custom_protection.piece", "Custom Protection (This Piece): %s%%");
        add("tooltip.cobblestonexxcompressed.compressed_cobblestone_armor.knockback_immunity", "Knockback Immunity");
        add("tooltip.cobblestonexxcompressed.flying_stone.description", "Grants creative flight while in your inventory or an accessory slot. Balse!!!!!!");
        add("tooltip.cobblestonexxcompressed.configuration_card.empty", "No configuration stored");
        add("tooltip.cobblestonexxcompressed.configuration_card.stored", "Stored: %s");
        add("tooltip.cobblestonexxcompressed.configuration_card.upgrade", "Upgrade: %s");
        add("tooltip.cobblestonexxcompressed.configuration_card.power", "CP Input: %s");
        add("message.cobblestonexxcompressed.configuration_card.copied", "Copied configuration from %s");
        add("message.cobblestonexxcompressed.configuration_card.pasted", "Pasted configuration to %s");
        add("message.cobblestonexxcompressed.configuration_card.cleared", "Cleared stored configuration");
        add("message.cobblestonexxcompressed.configuration_card.incompatible", "Stored settings can only be pasted to the same machine type");
        add("commands.cobblestonexxcompressed.sethp.success.single", "Set %s health to %s via setHealth");
        add("commands.cobblestonexxcompressed.sethp.success.multiple", "Set health of %s players to %s via setHealth");
        TooltipTranslationEntries.addEnglish(this);
        add("gui.cobblestonexxcompressed.water_generator.convert_rate", "%s mB/t");
        add("gui.cobblestonexxcompressed.water_generator.cp_rate", "%s CP/t");
        add("jei.cobblestonexxcompressed.compressed_stone_loot", "Compressed Stone Loot");
        add("jei.cobblestonexxcompressed.water_generator_conversion", "Cobblestone Water Generator");
        add("jei.cobblestonexxcompressed.water_generator_conversion.rate", "1 CP = 1 mB");
        add("jei.cobblestonexxcompressed.silk_touch", "Silk Touch");
        add("jei.cobblestonexxcompressed.no_silk_touch", "No Silk Touch");
        add("jei.cobblestonexxcompressed.chance", "Chance: %s");
        add("jei.cobblestonexxcompressed.count_range_fortune", "Count: %s-%s (Fortune)");
        add("jei.cobblestonexxcompressed.cp_supply.fuel", "Cobblestone / Compressed Cobblestone: 1 cobblestone/tick → CP");
        add("jei.cobblestonexxcompressed.cp_supply.catalyst", "Cobblestone Generator: CP supply without consumption");
    }
}