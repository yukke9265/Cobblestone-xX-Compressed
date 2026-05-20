package com.yukke9265.cobblestone_xx_compressed.compat.jei;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;

import net.minecraft.resources.ResourceLocation;

// JEI 連携で使うカテゴリ ID を 1 か所へまとめます。
// ここは JEI API を参照しないので、通常起動やサーバー側でも安全に使えます。
public final class ModJeiIds {
    public static final ResourceLocation COBBLESTONE_FURNACE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_furnace");

    public static final ResourceLocation COBBLESTONE_POWERED_FURNACE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_powered_furnace");

    public static final ResourceLocation COBBLESTONE_EXTREME_COMPRESSOR =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_extreme_compressor");

    public static final ResourceLocation COBBLESTONE_CRUSHER =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_crusher");

    public static final ResourceLocation COBBLESTONE_CENTRIFUGE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_centrifuge");

    public static final ResourceLocation COBBLESTONE_LASER_DRILL =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_laser_drill");

    public static final ResourceLocation COBBLESTONE_MIXER =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_mixer");

    public static final ResourceLocation COBBLESTONE_MELTER =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_melter");

    public static final ResourceLocation COBBLESTONE_ASSEMBLY_MACHINE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_assembly_machine");

    public static final ResourceLocation COBBLESTONE_CHEMICAL_REACTOR =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_chemical_reactor");

    public static final ResourceLocation COBBLESTONE_REACTION_CHAMBER =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_reaction_chamber");

    public static final ResourceLocation COBBLESTONE_CRYSTALLIZATION_CHAMBER =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_crystallization_chamber");

    public static final ResourceLocation COBBLESTONE_DISSOLUTION_CHAMBER =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_dissolution_chamber");

    public static final ResourceLocation COBBLESTONE_FLUID_MIXER =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_fluid_mixer");

    public static final ResourceLocation COMPRESSED_STONE_LOOT =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "compressed_stone_loot");

    private ModJeiIds() {
    }
}