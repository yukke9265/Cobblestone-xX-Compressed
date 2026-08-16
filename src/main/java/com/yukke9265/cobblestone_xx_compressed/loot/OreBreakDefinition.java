package com.yukke9265.cobblestone_xx_compressed.loot;

import java.util.List;
import java.util.Objects;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class OreBreakDefinition {
    private static final String MINECRAFT_MOD_ID = "minecraft";
    private static final String MEKANISM_MOD_ID = "mekanism";
    private static final String MEKANISM_EXTRAS_MOD_ID = "mekanism_extras";

    // JEI の代表ドロップと入力判定用。実際の破壊結果は loot table を使う。
    private static final List<OreBreakDefinition> DEFINITIONS = List.of(
        vanilla("coal_ore", "coal"),
        vanilla("deepslate_coal_ore", "coal"),
        vanilla("iron_ore", "raw_iron"),
        vanilla("deepslate_iron_ore", "raw_iron"),
        vanilla("copper_ore", "raw_copper"),
        vanilla("deepslate_copper_ore", "raw_copper"),
        vanilla("gold_ore", "raw_gold"),
        vanilla("deepslate_gold_ore", "raw_gold"),
        vanilla("redstone_ore", "redstone"),
        vanilla("deepslate_redstone_ore", "redstone"),
        vanilla("emerald_ore", "emerald"),
        vanilla("deepslate_emerald_ore", "emerald"),
        vanilla("lapis_ore", "lapis_lazuli"),
        vanilla("deepslate_lapis_ore", "lapis_lazuli"),
        vanilla("diamond_ore", "diamond"),
        vanilla("deepslate_diamond_ore", "diamond"),
        vanilla("nether_gold_ore", "gold_nugget"),
        vanilla("nether_quartz_ore", "quartz"),
        vanilla("ancient_debris", "ancient_debris"),
        optionalMod(MEKANISM_MOD_ID, "tin_ore", "raw_tin"),
        optionalMod(MEKANISM_MOD_ID, "deepslate_tin_ore", "raw_tin"),
        optionalMod(MEKANISM_MOD_ID, "osmium_ore", "raw_osmium"),
        optionalMod(MEKANISM_MOD_ID, "deepslate_osmium_ore", "raw_osmium"),
        optionalMod(MEKANISM_MOD_ID, "lead_ore", "raw_lead"),
        optionalMod(MEKANISM_MOD_ID, "deepslate_lead_ore", "raw_lead"),
        optionalMod(MEKANISM_MOD_ID, "uranium_ore", "raw_uranium"),
        optionalMod(MEKANISM_MOD_ID, "deepslate_uranium_ore", "raw_uranium"),
        optionalMod(MEKANISM_MOD_ID, "fluorite_ore", "fluorite_gem"),
        optionalMod(MEKANISM_MOD_ID, "deepslate_fluorite_ore", "fluorite_gem"),
        optionalMod(MEKANISM_EXTRAS_MOD_ID, "naquadah_ore", "raw_naquadah"),
        optionalMod(MEKANISM_EXTRAS_MOD_ID, "end_naquadah_ore", "raw_naquadah")
    );

    private final ResourceLocation inputItemId;
    private final ResourceLocation jeiDropItemId;

    private OreBreakDefinition(ResourceLocation inputItemId, ResourceLocation jeiDropItemId) {
        this.inputItemId = inputItemId;
        this.jeiDropItemId = jeiDropItemId;
    }

    public ResourceLocation getInputItemId() {
        return this.inputItemId;
    }

    public ResourceLocation getJeiDropItemId() {
        return this.jeiDropItemId;
    }

    public boolean dropsSelfWithoutSilkTouch() {
        return this.inputItemId.equals(this.jeiDropItemId);
    }

    public boolean hasResolvedInput() {
        return resolveItem(this.inputItemId) != Items.AIR;
    }

    public boolean hasResolvedJeiDrop() {
        return resolveItem(this.jeiDropItemId) != Items.AIR;
    }

    public ItemStack createInputStack() {
        Item item = resolveItem(this.inputItemId);
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item);
    }

    public ItemStack createJeiDropStack() {
        Item item = resolveItem(this.jeiDropItemId);
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item);
    }

    public static List<OreBreakDefinition> getDefinitions() {
        return DEFINITIONS;
    }

    public static OreBreakDefinition findByInput(ItemStack inputStack) {
        if (inputStack.isEmpty()) {
            return null;
        }

        ResourceLocation inputId = BuiltInRegistries.ITEM.getKey(inputStack.getItem());
        for (OreBreakDefinition definition : DEFINITIONS) {
            if (definition.inputItemId.equals(inputId) && definition.hasResolvedInput()) {
                return definition;
            }
        }

        return null;
    }

    private static OreBreakDefinition vanilla(String inputPath, String dropPath) {
        return new OreBreakDefinition(
            itemId(MINECRAFT_MOD_ID, inputPath),
            itemId(MINECRAFT_MOD_ID, dropPath)
        );
    }

    private static OreBreakDefinition optionalMod(String modId, String inputPath, String dropPath) {
        String safeModId = Objects.requireNonNull(modId);
        return new OreBreakDefinition(
            itemId(safeModId, inputPath),
            itemId(safeModId, dropPath)
        );
    }

    private static ResourceLocation itemId(String modId, String path) {
        return Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    private static Item resolveItem(ResourceLocation itemId) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null) {
            return Items.AIR;
        }

        return item;
    }
}
