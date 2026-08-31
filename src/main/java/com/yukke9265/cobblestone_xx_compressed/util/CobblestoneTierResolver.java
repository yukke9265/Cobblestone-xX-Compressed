package com.yukke9265.cobblestone_xx_compressed.util;

import java.util.List;
import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * 登録名から tier を推定し、tooltip へ Tier 数値を出す共通処理です。
 */
public final class CobblestoneTierResolver {
    private CobblestoneTierResolver() {
    }

    public static Optional<CobblestoneTier> findTier(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        return findTierByRegistryPath(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath());
    }

    public static Optional<CobblestoneTier> findTierByRegistryPath(String registryPath) {
        Optional<CobblestoneTier> machineTier = CobblestoneMachineTiers.findByRegistryName(registryPath);
        if (machineTier.isPresent()) {
            return machineTier;
        }

        if (registryPath.startsWith("molten_dirty_tier_")) {
            return parseMaterialPrefix(registryPath.substring("molten_dirty_tier_".length()));
        }
        if (registryPath.startsWith("molten_tier_")) {
            return parseMaterialPrefix(registryPath.substring("molten_tier_".length()));
        }
        if (registryPath.startsWith("tier_")) {
            return parseMaterialPrefix(registryPath.substring("tier_".length()));
        }

        return Optional.empty();
    }

    public static void appendTierTooltip(List<Component> tooltipComponents, CobblestoneTier tier) {
        tooltipComponents.add(
            1,
            Component.translatable(TooltipTranslationKeys.tier(), tier.getLevel()).withStyle(ChatFormatting.GRAY)
        );
    }

    private static Optional<CobblestoneTier> parseMaterialPrefix(String remainder) {
        int underscoreIndex = remainder.indexOf('_');
        if (underscoreIndex <= 0) {
            return Optional.empty();
        }

        String materialName = remainder.substring(0, underscoreIndex).toUpperCase();
        try {
            return Optional.of(CobblestoneTier.fromMaterialName(materialName));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
