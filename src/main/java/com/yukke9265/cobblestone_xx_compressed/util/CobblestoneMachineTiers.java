package com.yukke9265.cobblestone_xx_compressed.util;

import java.util.Map;
import java.util.Optional;

/**
 * 機械本体の tier は、クラフトで使う machine casing の素材 tier に合わせます。
 * 対応表の正は ModRecipeProvider.MACHINE_BLOCK_RECIPES です。
 */
public final class CobblestoneMachineTiers {
    private static final Map<String, CobblestoneTier> TIERS_BY_REGISTRY_NAME = Map.ofEntries(
        Map.entry("cobblestone_crusher", CobblestoneTier.COPPER),
        Map.entry("cobblestone_mixer", CobblestoneTier.IRON),
        Map.entry("cobblestone_powered_crafter", CobblestoneTier.IRON),
        Map.entry("cobblestone_centrifuge", CobblestoneTier.GOLD),
        Map.entry("cobblestone_laser_drill", CobblestoneTier.AMETHYST),
        Map.entry("cobblestone_reaction_chamber", CobblestoneTier.AQUAMARINE),
        Map.entry("cobblestone_melter", CobblestoneTier.TOPAZ),
        Map.entry("cobblestone_dissolution_chamber", CobblestoneTier.RUBY),
        Map.entry("cobblestone_fluid_mixer", CobblestoneTier.SAPPHIRE),
        Map.entry("cobblestone_crystallization_chamber", CobblestoneTier.DIAMOND),
        Map.entry("cobblestone_chemical_reactor", CobblestoneTier.EMERALD),
        Map.entry("cobblestone_assembly_machine", CobblestoneTier.NETHERITE),
        Map.entry("cobblestone_extreme_compressor", CobblestoneTier.OBSIDIAN),
        Map.entry("cobblestone_multiblock_crusher", CobblestoneTier.IRON),
        // 共通レシピ形と違う個別クラフトは、主素材の tier を手で合わせます。
        Map.entry("cobblestone_enchanter", CobblestoneTier.IRON),
        Map.entry("stone_break_simulator", CobblestoneTier.GOLD),
        Map.entry("cobblestone_water_generator", CobblestoneTier.AMETHYST),
        Map.entry("cobblestone_shield_projector", CobblestoneTier.AQUAMARINE)
    );

    private CobblestoneMachineTiers() {
    }

    public static Optional<CobblestoneTier> findByRegistryName(String registryName) {
        return Optional.ofNullable(TIERS_BY_REGISTRY_NAME.get(registryName));
    }
}
