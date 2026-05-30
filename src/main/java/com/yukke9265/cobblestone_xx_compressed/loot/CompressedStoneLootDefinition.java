package com.yukke9265.cobblestone_xx_compressed.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

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
            bonusDrop(() -> Items.FLINT, 0.01d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.COPPER.getBlock(),
            ModBlocks.TierCompressedCobblestone.COPPER.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.COPPER.getItem(), 0.4d),
            bonusDrop(() -> Items.COAL_ORE, 0.10d),
            bonusDrop(() -> Items.RAW_COPPER, 0.10d),
            optionalModBonusDrop(MEKANISM_MOD_ID, "raw_tin", 0.10d, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.IRON.getBlock(),
            ModBlocks.TierCompressedCobblestone.IRON.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.IRON.getItem(), 0.2d),
            bonusDrop(() -> Items.REDSTONE_ORE, 0.10d),
            bonusDrop(() -> Items.RAW_IRON, 0.10d),
            optionalModBonusDrop(MEKANISM_MOD_ID, "raw_lead", 0.10d, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.GOLD.getBlock(),
            ModBlocks.TierCompressedCobblestone.GOLD.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.GOLD.getItem(), 0.2d),
            bonusDrop(() -> Items.NETHER_QUARTZ_ORE, 0.10d),
            optionalModBonusDrop(MEKANISM_MOD_ID, "fluorite_ore", 0.10d, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.AMETHYST.getBlock(),
            ModBlocks.TierCompressedCobblestone.AMETHYST.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.AMETHYST.getItem(), 0.1d),
            bonusDrop(() -> Items.LAPIS_ORE, 0.10d),
            optionalModBonusDrop(MEKANISM_MOD_ID, "raw_osmium", 0.10d, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.AQUAMARINE.getBlock(),
            ModBlocks.TierCompressedCobblestone.AQUAMARINE.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.AQUAMARINE.getItem(), 0.1d),
            bonusDrop(() -> Items.GLOWSTONE, 0.10d),
            optionalModBonusDrop(MEKANISM_MOD_ID, "raw_uranium", 0.10d, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.TOPAZ.getBlock(),
            ModBlocks.TierCompressedCobblestone.TOPAZ.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.TOPAZ.getItem(), 0.04d),
            bonusDrop(() -> Items.RAW_GOLD, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.RUBY.getBlock(),
            ModBlocks.TierCompressedCobblestone.RUBY.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.RUBY.getItem(), 0.04d),
            bonusDrop(() -> Items.BLAZE_ROD, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.SAPPHIRE.getBlock(),
            ModBlocks.TierCompressedCobblestone.SAPPHIRE.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.SAPPHIRE.getItem(), 0.02d),
            bonusDrop(() -> Items.ENDER_PEARL, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.DIAMOND.getBlock(),
            ModBlocks.TierCompressedCobblestone.DIAMOND.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.DIAMOND.getItem(), 0.02d),
            bonusDrop(() -> Items.DIAMOND_ORE, 0.10d),
            optionalModBonusDrop(MEKANISM_EXTRAS_MOD_ID, "raw_naquadah", 0.10d, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.EMERALD.getBlock(),
            ModBlocks.TierCompressedCobblestone.EMERALD.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.EMERALD.getItem(), 0.01d),
            bonusDrop(() -> Items.EMERALD_ORE, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.NETHERITE.getBlock(),
            ModBlocks.TierCompressedCobblestone.NETHERITE.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.NETHERITE.getItem(), 0.01d),
            bonusDrop(() -> Items.ANCIENT_DEBRIS, 0.10d)
        ),
        new CompressedStoneLootDefinition(
            ModBlocks.TierCompressedStone.OBSIDIAN.getBlock(),
            ModBlocks.TierCompressedCobblestone.OBSIDIAN.getBlock(),
            bonusDrop(ModItems.TierCobblestoneGem.OBSIDIAN.getItem(), 0.01d),
            bonusDrop(() -> Items.NETHER_STAR, 0.10d)
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

    public List<CompatLootEntry> getCompatBonusDrops() {
        List<CompatLootEntry> entries = new ArrayList<>();

        for (BonusLootEntry bonusDrop : this.bonusDrops) {
            if (!bonusDrop.hasCompatLoot()) {
                continue;
            }

            entries.add(new CompatLootEntry(this.stoneBlock, bonusDrop));
        }

        return List.copyOf(entries);
    }

    public static List<CompressedStoneLootDefinition> getDefinitions() {
        return DEFINITIONS;
    }

    public static CompressedStoneLootDefinition findByStoneInput(ItemStack inputStack) {
        if (inputStack.isEmpty()) {
            return null;
        }

        for (CompressedStoneLootDefinition definition : DEFINITIONS) {
            if (inputStack.is(definition.getStoneBlock().get().asItem())) {
                return definition;
            }
        }

        return null;
    }

    public static List<CompatLootEntry> getCompatLootEntries() {
        List<CompatLootEntry> entries = new ArrayList<>();

        for (CompressedStoneLootDefinition definition : DEFINITIONS) {
            entries.addAll(definition.getCompatBonusDrops());
        }

        return List.copyOf(entries);
    }

    public static BonusLootEntry bonusDrop(Supplier<? extends ItemLike> item, double chance) {
        return new BonusLootEntry(item, null, chance, null, 0.0d, false, 0.0d, 0.0d);
    }

    private static BonusLootEntry optionalModBonusDrop(String modId, String itemPath, double chance, double compatChance) {
        // datagen 環境では外部 mod の Item 実体が無くても compat JSON を出したいため、
        // item id は必ず保持し、実体が見つかったときだけ JEI 表示用の supplier を持たせます。
        String safeModId = Objects.requireNonNull(modId);
        String safeItemPath = Objects.requireNonNull(itemPath);
        ResourceLocation itemId = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(safeModId, safeItemPath));
        Item item = BuiltInRegistries.ITEM.get(itemId);
        Supplier<? extends ItemLike> itemSupplier = null;
        if (item != Items.AIR) {
            itemSupplier = () -> item;
        }

        return new BonusLootEntry(itemSupplier, itemId, chance, modId, compatChance, false, 0.0d, 0.0d);
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
        private final ResourceLocation explicitItemId;
        private final double chance;
        private final String compatModId;
        private final double compatChance;

        private BonusLootEntry(
            Supplier<? extends ItemLike> item,
            ResourceLocation explicitItemId,
            double chance,
            String compatModId,
            double compatChance,
            boolean hasCountRange,
            double minCount,
            double maxCount
        ) {
            this.item = item;
            this.explicitItemId = explicitItemId;
            this.chance = chance;
            this.compatModId = compatModId;
            this.compatChance = compatChance;
        }

        public Supplier<? extends ItemLike> getItem() {
            return Objects.requireNonNull(this.item);
        }

        public double getChance() {
            return this.chance;
        }

        public boolean hasCompatLoot() {
            return this.compatModId != null;
        }

        public boolean hasResolvedItem() {
            return this.item != null;
        }

        public String getCompatModId() {
            return this.compatModId;
        }

        public double getCompatChance() {
            return this.compatChance;
        }

        public ResourceLocation getItemId() {
            if (this.explicitItemId != null) {
                return this.explicitItemId;
            }

            ItemLike itemLike = Objects.requireNonNull(this.item.get());
            Item item = Objects.requireNonNull(itemLike.asItem());
            return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
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

    public static class CompatLootEntry {
        private final Supplier<? extends Block> stoneBlock;
        private final BonusLootEntry bonusDrop;

        private CompatLootEntry(Supplier<? extends Block> stoneBlock, BonusLootEntry bonusDrop) {
            this.stoneBlock = stoneBlock;
            this.bonusDrop = bonusDrop;
        }

        public String getRequiredModId() {
            return Objects.requireNonNull(this.bonusDrop.getCompatModId());
        }

        public ResourceLocation getItemId() {
            return this.bonusDrop.getItemId();
        }

        public double getChance() {
            return this.bonusDrop.getCompatChance();
        }

        public ResourceLocation getTargetLootTableId() {
            return Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "blocks/" + getStoneBlockPath()));
        }

        public ResourceLocation getCompatLootTableId() {
            return Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(
                CobblestonexXCompressed.MODID,
                "compat/compressed_stone/" + getStoneBlockPath() + "/" + getItemId().getPath()
            ));
        }

        public String getStoneBlockPath() {
            Block block = Objects.requireNonNull(this.stoneBlock.get());
            return BuiltInRegistries.BLOCK.getKey(block).getPath();
        }
    }
}