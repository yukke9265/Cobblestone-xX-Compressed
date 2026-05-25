package com.yukke9265.cobblestone_xx_compressed.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {
    public static final TagKey<Item> DUSTS_AMETHYST = commonTag("dusts/amethyst");
    public static final TagKey<Item> DUSTS_ANCIENT_DEBRIS = commonTag("dusts/ancient_debris");
    public static final TagKey<Item> DUSTS_AQUAMARINE = commonTag("dusts/aquamarine");
    public static final TagKey<Item> DUSTS_COAL = commonTag("dusts/coal");
    public static final TagKey<Item> DUSTS_DIAMOND = commonTag("dusts/diamond");
    public static final TagKey<Item> DUSTS_EMERALD = commonTag("dusts/emerald");
    public static final TagKey<Item> DUSTS_ENDER = commonTag("dusts/ender");
    public static final TagKey<Item> DUSTS_GOLD = commonTag("dusts/gold");
    public static final TagKey<Item> DUSTS_LAPIS = commonTag("dusts/lapis");
    public static final TagKey<Item> DUSTS_OBSIDIAN = commonTag("dusts/obsidian");
    public static final TagKey<Item> DUSTS_RUBY = commonTag("dusts/ruby");
    public static final TagKey<Item> DUSTS_SAPPHIRE = commonTag("dusts/sapphire");
    public static final TagKey<Item> DUSTS_TOPAZ = commonTag("dusts/topaz");

    private ModItemTags() {
    }

    private static TagKey<Item> commonTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
    }
}