package com.yukke9265.cobblestone_xx_compressed.util;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

public final class TooltipTranslationKeys {
    private static final String TOOLTIP_PREFIX = "tooltip.cobblestonexxcompressed.";
    private static final String DESCRIPTION_SUFFIX = ".description";

    private TooltipTranslationKeys() {
    }

    public static String machineDescription(String registryName) {
        return TOOLTIP_PREFIX + registryName + DESCRIPTION_SUFFIX;
    }

    public static String cobblestoneGeneratorDescription() {
        return machineDescription("cobblestone_generator");
    }

    public static String cobblestoneGeneratorSizeDescription(ModBlocks.CobblestoneGeneratorSize size) {
        return TOOLTIP_PREFIX + "cobblestone_generator_" + size.getRegistrySuffix() + DESCRIPTION_SUFFIX;
    }
}