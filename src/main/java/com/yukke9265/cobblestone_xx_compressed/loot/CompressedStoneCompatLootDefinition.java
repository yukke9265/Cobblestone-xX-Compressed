package com.yukke9265.cobblestone_xx_compressed.loot;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class CompressedStoneCompatLootDefinition {
    private static final List<CompressedStoneCompatLootDefinition> DEFINITIONS = List.of(
        new CompressedStoneCompatLootDefinition(ModBlocks.TierCompressedStone.COPPER.getBlock(), "mekanism", "raw_tin", 0.45d),
        new CompressedStoneCompatLootDefinition(ModBlocks.TierCompressedStone.IRON.getBlock(), "mekanism", "raw_osmium", 0.45d),
        new CompressedStoneCompatLootDefinition(ModBlocks.TierCompressedStone.GOLD.getBlock(), "mekanism", "fluorite_ore", 0.45d),
        new CompressedStoneCompatLootDefinition(ModBlocks.TierCompressedStone.AMETHYST.getBlock(), "mekanism", "raw_lead", 0.45d),
        new CompressedStoneCompatLootDefinition(ModBlocks.TierCompressedStone.AQUAMARINE.getBlock(), "mekanism", "raw_uranium", 0.45d),
        new CompressedStoneCompatLootDefinition(ModBlocks.TierCompressedStone.DIAMOND.getBlock(), "mekanism_extras", "raw_naquadah", 0.2d)
    );

    private final Supplier<? extends Block> stoneBlock;
    private final String requiredModId;
    private final ResourceLocation itemId;
    private final double chance;

    public CompressedStoneCompatLootDefinition(
        Supplier<? extends Block> stoneBlock,
        String requiredModId,
        String itemPath,
        double chance
    ) {
        String safeRequiredModId = Objects.requireNonNull(requiredModId);
        String safeItemPath = Objects.requireNonNull(itemPath);
        this.stoneBlock = stoneBlock;
        this.requiredModId = safeRequiredModId;
        this.itemId = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(safeRequiredModId, safeItemPath));
        this.chance = chance;
    }

    public Supplier<? extends Block> getStoneBlock() {
        return this.stoneBlock;
    }

    public String getRequiredModId() {
        return this.requiredModId;
    }

    public ResourceLocation getItemId() {
        return this.itemId;
    }

    public double getChance() {
        return this.chance;
    }

    public ResourceLocation getTargetLootTableId() {
        return Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "blocks/" + getStoneBlockPath()));
    }

    public ResourceLocation getCompatLootTableId() {
        return Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(
            CobblestonexXCompressed.MODID,
            "compat/compressed_stone/" + getStoneBlockPath() + "/" + this.itemId.getPath()
        ));
    }

    public String getStoneBlockPath() {
        Block block = Objects.requireNonNull(this.stoneBlock.get());
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    public static List<CompressedStoneCompatLootDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}