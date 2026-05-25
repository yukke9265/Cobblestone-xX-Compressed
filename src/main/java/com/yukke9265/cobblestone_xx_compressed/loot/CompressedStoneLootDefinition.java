package com.yukke9265.cobblestone_xx_compressed.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

public class CompressedStoneLootDefinition {
    private static final String MEKANISM_MOD_ID = "mekanism";
    private static final String MEKANISM_EXTRAS_MOD_ID = "mekanism_extras";

    // compressed stone のドロップ条件は datagen と JEI の両方で使うため、
    // このクラスに 1 か所だけ定義して、表示と実際の loot table がずれないようにします。
    private static final List<CompressedStoneLootDefinition> DEFINITIONS = List.of(
        new CompressedStoneLootDefinition(
            ModBlocks.COMPRESSED_STONE,
            ModBlocks.COMPRESSED_COBBLESTONE,
            bonusDrop(ModItems.COBBLESTONE_GEM, 0.8d),
            bonusDrop(() -> Items.BONE, 0.01d),
            bonusDrop(() -> Items.FLINT, 0.05d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.COPPER.getBlock(),
            ModBlocks.TierCompressedCobblestone.COPPER.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.COPPER.getItem(), 0.4d),
            bonusDrop(() -> Items.COAL_ORE, 0.45d),
            bonusDrop(() -> Items.RAW_COPPER, 0.45d),
            optionalModBonusDrop(MEKANISM_MOD_ID, "raw_tin", 0.45d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.IRON.getBlock(),
            ModBlocks.TierCompressedCobblestone.IRON.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.IRON.getItem(), 0.2d),
            bonusDrop(() -> Items.REDSTONE_ORE, 0.45d),
            optionalModBonusDrop(MEKANISM_MOD_ID, "raw_osmium", 0.45d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.GOLD.getBlock(),
            ModBlocks.TierCompressedCobblestone.GOLD.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.GOLD.getItem(), 0.2d),
            bonusDrop(() -> Items.RAW_IRON, 0.45d),
            bonusDrop(() -> Items.NETHER_QUARTZ_ORE, 0.45d),
            optionalModBonusDrop(MEKANISM_MOD_ID, "fluorite_ore", 0.45d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.AMETHYST.getBlock(),
            ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.AMETHYST.getItem(), 0.1d),
            bonusDrop(() -> Items.LAPIS_ORE, 0.45d),
            optionalModBonusDrop(MEKANISM_MOD_ID, "raw_lead", 0.45d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.AQUAMARINE.getBlock(),
            ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.AQUAMARINE.getItem(), 0.1d),
            bonusDrop(() -> Items.GLOWSTONE, 0.45d),
            optionalModBonusDrop(MEKANISM_MOD_ID, "raw_uranium", 0.45d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.TOPAZ.getBlock(),
            ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.TOPAZ.getItem(), 0.04d),
            bonusDrop(() -> Items.RAW_GOLD, 0.4d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.RUBY.getBlock(),
            ModBlocks.TierCompressedCobblestone.RUBY.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.RUBY.getItem(), 0.04d),
            bonusDrop(() -> Items.BLAZE_ROD, 0.45d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.SAPPHIRE.getBlock(),
            ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.SAPPHIRE.getItem(), 0.02d),
            bonusDrop(() -> Items.ENDER_PEARL, 0.45d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.DIAMOND.getBlock(),
            ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.DIAMOND.getItem(), 0.02d),
            bonusDrop(() -> Items.DIAMOND_ORE, 0.2d),
            optionalModBonusDrop(MEKANISM_EXTRAS_MOD_ID, "raw_naquadah", 0.2d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.EMERALD.getBlock(),
            ModBlocks.TierCompressedCobblestone.EMERALD.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.EMERALD.getItem(), 0.01d),
            bonusDrop(() -> Items.EMERALD_ORE, 0.18d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.NETHERITE.getBlock(),
            ModBlocks.TierCompressedCobblestone.NETHERITE.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.NETHERITE.getItem(), 0.01d),
            bonusDrop(() -> Items.ANCIENT_DEBRIS, 0.08d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.OBSIDIAN.getBlock(),
            ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.OBSIDIAN.getItem(), 0.01d),
            bonusDrop(() -> Items.NETHER_STAR, 0.2d)
        )
    );

    private final Supplier<? extends Block> stoneBlock;
    private final Supplier<? extends Block> cobblestoneBlock;
    private final List<BonusLootEntry> bonusDrops;

    public CompressedStoneLootDefinition(
        Supplier<? extends Block> stoneBlock,
        Supplier<? extends Block> cobblestoneBlock,
        BonusLootEntry... bonusDrops
    ) {
        this.stoneBlock = stoneBlock;
        this.cobblestoneBlock = cobblestoneBlock;
        this.bonusDrops = createBonusDrops(bonusDrops);
    }

    public Supplier<? extends Block> getStoneBlock() {
        return this.stoneBlock;
    }

    public Supplier<? extends Block> getCobblestoneBlock() {
        return this.cobblestoneBlock;
    }

    public List<BonusLootEntry> getBonusDrops() {
        return this.bonusDrops;
    }

    public static List<CompressedStoneLootDefinition> getDefinitions() {
        return DEFINITIONS;
    }

    public static BonusLootEntry bonusDrop(Supplier<? extends ItemLike> item, double chance) {
        return new BonusLootEntry(item, chance, false, 0.0d, 0.0d);
    }

    private static BonusLootEntry optionalModBonusDrop(String modId, String itemPath, double chance) {
        // 外部 mod を compileOnly 依存にしていないため、
        // レジストリから文字列 ID で引いて、未導入環境でも安全に読み込めるようにします。
        if (!ModList.get().isLoaded(modId)) {
            return null;
        }

        String safeModId = Objects.requireNonNull(modId);
        String safeItemPath = Objects.requireNonNull(itemPath);
        ResourceLocation itemId = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(safeModId, safeItemPath));
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == Items.AIR) {
            return null;
        }

        return bonusDrop(() -> item, chance);
    }

    private static List<BonusLootEntry> createBonusDrops(BonusLootEntry... bonusDrops) {
        List<BonusLootEntry> entries = new ArrayList<>();
        for (BonusLootEntry bonusDrop : bonusDrops) {
            if (bonusDrop == null) {
                continue;
            }
            entries.add(bonusDrop);
        }

        if (entries.isEmpty()) {
            throw new IllegalArgumentException("Compressed stone loot must define at least one bonus drop.");
        }
        if (entries.size() > 4) {
            throw new IllegalArgumentException("Compressed stone loot supports up to four bonus drops.");
        }

        return List.copyOf(entries);
    }

    public static class BonusLootEntry {
        private final Supplier<? extends ItemLike> item;
        private final double chance;

        private BonusLootEntry(Supplier<? extends ItemLike> item, double chance, boolean hasCountRange, double minCount, double maxCount) {
            this.item = item;
            this.chance = chance;
        }

        public Supplier<? extends ItemLike> getItem() {
            return this.item;
        }

        public double getChance() {
            return this.chance;
        }

        public boolean hasCountRange() {
            return false;
        }

        public double getMinCount() {
            return 0.0d;
        }

        public double getMaxCount() {
            return 0.0d;
        }
    }
}