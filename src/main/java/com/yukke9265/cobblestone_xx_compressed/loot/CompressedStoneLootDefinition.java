package com.yukke9265.cobblestone_xx_compressed.loot;

import java.util.List;
import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class CompressedStoneLootDefinition {
    // compressed stone のドロップ条件は datagen と JEI の両方で使うため、
    // このクラスに 1 か所だけ定義して、表示と実際の loot table がずれないようにします。
    private static final List<CompressedStoneLootDefinition> DEFINITIONS = List.of(
        new CompressedStoneLootDefinition(ModBlocks.COMPRESSED_STONE, ModBlocks.COMPRESSED_COBBLESTONE, ModItems.COBBLESTONE_GEM, 0.12d, () -> Items.FLINT, 0.35d, 1.0d, 2.0d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.COPPER.getBlock(), ModBlocks.TierCompressedCobblestone.COPPER.getBlock(), ModItems.TierCobblestoneGem.COPPER.getItem(), 0.12d, () -> Items.COPPER_INGOT, 0.45d, 1.0d, 3.0d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.IRON.getBlock(), ModBlocks.TierCompressedCobblestone.IRON.getBlock(), ModItems.TierCobblestoneGem.IRON.getItem(), 0.12d, () -> Items.IRON_NUGGET, 0.45d, 1.0d, 3.0d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.GOLD.getBlock(), ModBlocks.TierCompressedCobblestone.GOLD.getBlock(), ModItems.TierCobblestoneGem.GOLD.getItem(), 0.12d, () -> Items.GOLD_NUGGET, 0.45d, 1.0d, 3.0d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.AMETHYST.getBlock(), ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock(), ModItems.TierCobblestoneGem.AMETHYST.getItem(), 0.12d, () -> Items.AMETHYST_SHARD, 0.45d, 1.0d, 3.0d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.AQUAMARINE.getBlock(), ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock(), ModItems.TierCobblestoneGem.AQUAMARINE.getItem(), 0.12d, ModItems.AQUAMARINE_SHARD, 0.45d, 1.0d, 3.0d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.TOPAZ.getBlock(), ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock(), ModItems.TierCobblestoneGem.TOPAZ.getItem(), 0.12d, ModItems.TOPAZ_SHARD, 0.4d, 1.0d, 2.0d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.RUBY.getBlock(), ModBlocks.TierCompressedCobblestone.RUBY.getBlock(), ModItems.TierCobblestoneGem.RUBY.getItem(), 0.12d, ModItems.RUBY_SHARD, 0.45d, 1.0d, 4.0d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.SAPPHIRE.getBlock(), ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock(), ModItems.TierCobblestoneGem.SAPPHIRE.getItem(), 0.12d, ModItems.SAPPHIRE_SHARD, 0.45d, 2.0d, 5.0d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.DIAMOND.getBlock(), ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock(), ModItems.TierCobblestoneGem.DIAMOND.getItem(), 0.1d, () -> Items.DIAMOND, 0.2d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.EMERALD.getBlock(), ModBlocks.TierCompressedCobblestone.EMERALD.getBlock(), ModItems.TierCobblestoneGem.EMERALD.getItem(), 0.1d, () -> Items.EMERALD, 0.18d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.NETHERITE.getBlock(), ModBlocks.TierCompressedCobblestone.NETHERITE.getBlock(), ModItems.TierCobblestoneGem.NETHERITE.getItem(), 0.08d, () -> Items.NETHERITE_SCRAP, 0.08d),
        new CompressedStoneLootDefinition(ModBlocks.TierCompressedStone.OBSIDIAN.getBlock(), ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock(), ModItems.TierCobblestoneGem.OBSIDIAN.getItem(), 0.1d, () -> Items.OBSIDIAN, 0.2d)
    );

    private final Supplier<? extends Block> stoneBlock;
    private final Supplier<? extends Block> cobblestoneBlock;
    private final Supplier<? extends ItemLike> gemItem;
    private final double gemChance;
    private final Supplier<? extends ItemLike> resourceItem;
    private final double resourceChance;
    private final boolean hasCountRange;
    private final double minCount;
    private final double maxCount;

    public CompressedStoneLootDefinition(
        Supplier<? extends Block> stoneBlock,
        Supplier<? extends Block> cobblestoneBlock,
        Supplier<? extends ItemLike> gemItem,
        double gemChance,
        Supplier<? extends ItemLike> resourceItem,
        double resourceChance
    ) {
        this(stoneBlock, cobblestoneBlock, gemItem, gemChance, resourceItem, resourceChance, false, 0.0d, 0.0d);
    }

    public CompressedStoneLootDefinition(
        Supplier<? extends Block> stoneBlock,
        Supplier<? extends Block> cobblestoneBlock,
        Supplier<? extends ItemLike> gemItem,
        double gemChance,
        Supplier<? extends ItemLike> resourceItem,
        double resourceChance,
        double minCount,
        double maxCount
    ) {
        this(stoneBlock, cobblestoneBlock, gemItem, gemChance, resourceItem, resourceChance, true, minCount, maxCount);
    }

    public CompressedStoneLootDefinition(
        Supplier<? extends Block> stoneBlock,
        Supplier<? extends Block> cobblestoneBlock,
        Supplier<? extends ItemLike> gemItem,
        double gemChance,
        Supplier<? extends ItemLike> resourceItem,
        double resourceChance,
        boolean hasCountRange,
        double minCount,
        double maxCount
    ) {
        this.stoneBlock = stoneBlock;
        this.cobblestoneBlock = cobblestoneBlock;
        this.gemItem = gemItem;
        this.gemChance = gemChance;
        this.resourceItem = resourceItem;
        this.resourceChance = resourceChance;
        this.hasCountRange = hasCountRange;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    public Supplier<? extends Block> getStoneBlock() {
        return this.stoneBlock;
    }

    public Supplier<? extends Block> getCobblestoneBlock() {
        return this.cobblestoneBlock;
    }

    public Supplier<? extends ItemLike> getGemItem() {
        return this.gemItem;
    }

    public double getGemChance() {
        return this.gemChance;
    }

    public Supplier<? extends ItemLike> getResourceItem() {
        return this.resourceItem;
    }

    public double getResourceChance() {
        return this.resourceChance;
    }

    public boolean hasCountRange() {
        return this.hasCountRange;
    }

    public double getMinCount() {
        return this.minCount;
    }

    public double getMaxCount() {
        return this.maxCount;
    }

    public static List<CompressedStoneLootDefinition> getDefinitions() {
        return DEFINITIONS;
    }
}