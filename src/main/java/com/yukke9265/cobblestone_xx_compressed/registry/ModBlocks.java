package com.yukke9265.cobblestone_xx_compressed.registry;

import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneGeneratorBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneCrusherBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneCentrifugeBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneAssemblyMachineBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneChemicalReactorBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneDissolutionChamberBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneFEGeneratorBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneFluidMixerBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneFurnaceBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneLaserDrillBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneMelterBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneMixerBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestonePoweredFurnaceBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneReactionChamberBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneCrystallizationChamberBlock;
import com.yukke9265.cobblestone_xx_compressed.block.CobblestoneTankBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = 
        DeferredRegister.createBlocks(CobblestonexXCompressed.MODID);
    public static final long BASE_COBBLESTONE_TANK_CAPACITY = 8_000L;

    // 圧縮丸石の tier 一覧です。
    // 登録名と表示名をここにまとめておくと、block 登録、item 登録、datagen を同じ順番で回せます。
    public enum TierCompressedCobblestone {
        COPPER("tier_copper_compressed_cobblestone", "Copper Compressed Cobblestone"),
        IRON("tier_iron_compressed_cobblestone", "Iron Compressed Cobblestone"),
        GOLD("tier_gold_compressed_cobblestone", "Gold Compressed Cobblestone"),
        AMETHYST("tier_amethyst_compressed_cobblestone", "Amethyst Compressed Cobblestone"),
        AQUAMARINE("tier_aquamarine_compressed_cobblestone", "Aquamarine Compressed Cobblestone"),
        TOPAZ("tier_topaz_compressed_cobblestone", "Topaz Compressed Cobblestone"),
        RUBY("tier_ruby_compressed_cobblestone", "Ruby Compressed Cobblestone"),
        SAPPHIRE("tier_sapphire_compressed_cobblestone", "Sapphire Compressed Cobblestone"),
        DIAMOND("tier_diamond_compressed_cobblestone", "Diamond Compressed Cobblestone"),
        EMERALD("tier_emerald_compressed_cobblestone", "Emerald Compressed Cobblestone"),
        NETHERITE("tier_netherite_compressed_cobblestone", "Netherite Compressed Cobblestone"),
        OBSIDIAN("tier_obsidian_compressed_cobblestone", "Obsidian Compressed Cobblestone");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredBlock<Block> block;

        TierCompressedCobblestone(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredBlock<Block> getBlock() {
            return this.block;
        }

        private void setBlock(DeferredBlock<Block> block) {
            this.block = block;
        }
    }

    // 圧縮石も圧縮丸石と同じ tier 構成にしておくと、
    // script と datagen の対応関係が見比べやすくなります。
    public enum TierCompressedStone {
        COPPER("tier_copper_compressed_stone", "Copper Compressed Stone"),
        IRON("tier_iron_compressed_stone", "Iron Compressed Stone"),
        GOLD("tier_gold_compressed_stone", "Gold Compressed Stone"),
        AMETHYST("tier_amethyst_compressed_stone", "Amethyst Compressed Stone"),
        AQUAMARINE("tier_aquamarine_compressed_stone", "Aquamarine Compressed Stone"),
        TOPAZ("tier_topaz_compressed_stone", "Topaz Compressed Stone"),
        RUBY("tier_ruby_compressed_stone", "Ruby Compressed Stone"),
        SAPPHIRE("tier_sapphire_compressed_stone", "Sapphire Compressed Stone"),
        DIAMOND("tier_diamond_compressed_stone", "Diamond Compressed Stone"),
        EMERALD("tier_emerald_compressed_stone", "Emerald Compressed Stone"),
        NETHERITE("tier_netherite_compressed_stone", "Netherite Compressed Stone"),
        OBSIDIAN("tier_obsidian_compressed_stone", "Obsidian Compressed Stone");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredBlock<Block> block;

        TierCompressedStone(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredBlock<Block> getBlock() {
            return this.block;
        }

        private void setBlock(DeferredBlock<Block> block) {
            this.block = block;
        }
    }

    // machine casing も既存 tier と同じ順番で持たせておくと、
    // block 登録、item 登録、datagen、レシピ生成を 1 つの一覧でそろえられます。
    public enum TierCobblestoneMachineCasing {
        COPPER("tier_copper_cobblestone_machine_casing", "Copper Cobblestone Machine Casing"),
        IRON("tier_iron_cobblestone_machine_casing", "Iron Cobblestone Machine Casing"),
        GOLD("tier_gold_cobblestone_machine_casing", "Gold Cobblestone Machine Casing"),
        AMETHYST("tier_amethyst_cobblestone_machine_casing", "Amethyst Cobblestone Machine Casing"),
        AQUAMARINE("tier_aquamarine_cobblestone_machine_casing", "Aquamarine Cobblestone Machine Casing"),
        TOPAZ("tier_topaz_cobblestone_machine_casing", "Topaz Cobblestone Machine Casing"),
        RUBY("tier_ruby_cobblestone_machine_casing", "Ruby Cobblestone Machine Casing"),
        SAPPHIRE("tier_sapphire_cobblestone_machine_casing", "Sapphire Cobblestone Machine Casing"),
        DIAMOND("tier_diamond_cobblestone_machine_casing", "Diamond Cobblestone Machine Casing"),
        EMERALD("tier_emerald_cobblestone_machine_casing", "Emerald Cobblestone Machine Casing"),
        NETHERITE("tier_netherite_cobblestone_machine_casing", "Netherite Cobblestone Machine Casing"),
        OBSIDIAN("tier_obsidian_cobblestone_machine_casing", "Obsidian Cobblestone Machine Casing");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredBlock<Block> block;

        TierCobblestoneMachineCasing(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredBlock<Block> getBlock() {
            return this.block;
        }

        private void setBlock(DeferredBlock<Block> block) {
            this.block = block;
        }
    }

    // Cobblestone Tank は通常版 8,000 mB を起点に、
    // tier が 1 段上がるごとに容量を 8 倍へ増やします。
    public enum TierCobblestoneTank {
        COPPER("tier_copper_cobblestone_tank", "Copper Cobblestone Tank", 1),
        IRON("tier_iron_cobblestone_tank", "Iron Cobblestone Tank", 2),
        GOLD("tier_gold_cobblestone_tank", "Gold Cobblestone Tank", 3),
        AMETHYST("tier_amethyst_cobblestone_tank", "Amethyst Cobblestone Tank", 4),
        AQUAMARINE("tier_aquamarine_cobblestone_tank", "Aquamarine Cobblestone Tank", 5),
        TOPAZ("tier_topaz_cobblestone_tank", "Topaz Cobblestone Tank", 6),
        RUBY("tier_ruby_cobblestone_tank", "Ruby Cobblestone Tank", 7),
        SAPPHIRE("tier_sapphire_cobblestone_tank", "Sapphire Cobblestone Tank", 8),
        DIAMOND("tier_diamond_cobblestone_tank", "Diamond Cobblestone Tank", 9),
        EMERALD("tier_emerald_cobblestone_tank", "Emerald Cobblestone Tank", 10),
        NETHERITE("tier_netherite_cobblestone_tank", "Netherite Cobblestone Tank", 11),
        OBSIDIAN("tier_obsidian_cobblestone_tank", "Obsidian Cobblestone Tank", 12);

        private final String registryName;
        private final String englishDisplayName;
        private final long capacity;
        private DeferredBlock<Block> block;

        TierCobblestoneTank(String registryName, String englishDisplayName, int tierLevel) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
            this.capacity = calculateCobblestoneTankCapacity(tierLevel);
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public long getCapacity() {
            return this.capacity;
        }

        public DeferredBlock<Block> getBlock() {
            return this.block;
        }

        private void setBlock(DeferredBlock<Block> block) {
            this.block = block;
        }
    }

    // generator は S / M / L の 3 サイズだけを持ち、
    // どの tier でも生産量とテクスチャフォルダ規則を共通で使います。
    public enum CobblestoneGeneratorSize {
        S("s", "S", "cobblestone_generator_s", 1),
        M("m", "M", "cobblestone_generator_m", 8),
        L("l", "L", "cobblestone_generator_l", 64);

        private final String registrySuffix;
        private final String displayName;
        private final String textureFolderName;
        private final int productionPerTick;

        CobblestoneGeneratorSize(String registrySuffix, String displayName, String textureFolderName, int productionPerTick) {
            this.registrySuffix = registrySuffix;
            this.displayName = displayName;
            this.textureFolderName = textureFolderName;
            this.productionPerTick = productionPerTick;
        }

        public String getRegistrySuffix() {
            return this.registrySuffix;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getTextureFolderName() {
            return this.textureFolderName;
        }

        public int getProductionPerTick() {
            return this.productionPerTick;
        }
    }

    // 各 tier に対して S / M / L を 1 つずつ持たせます。
    // ここに登録名、表示名、生産量、生成対象ブロックをまとめておくと、
    // block / item / datagen / recipe の順番をそろえて回せます。
    public enum TierCobblestoneGenerator {
        S("cobblestone_generator_s", "Cobblestone Generator S", () -> ModBlocks.COMPRESSED_COBBLESTONE, CobblestoneGeneratorSize.S),
        M("cobblestone_generator_m", "Cobblestone Generator M", () -> ModBlocks.COMPRESSED_COBBLESTONE, CobblestoneGeneratorSize.M),
        L("cobblestone_generator_l", "Cobblestone Generator L", () -> ModBlocks.COMPRESSED_COBBLESTONE, CobblestoneGeneratorSize.L),
        COPPER_S(TierCompressedCobblestone.COPPER, CobblestoneGeneratorSize.S),
        COPPER_M(TierCompressedCobblestone.COPPER, CobblestoneGeneratorSize.M),
        COPPER_L(TierCompressedCobblestone.COPPER, CobblestoneGeneratorSize.L),
        IRON_S(TierCompressedCobblestone.IRON, CobblestoneGeneratorSize.S),
        IRON_M(TierCompressedCobblestone.IRON, CobblestoneGeneratorSize.M),
        IRON_L(TierCompressedCobblestone.IRON, CobblestoneGeneratorSize.L),
        GOLD_S(TierCompressedCobblestone.GOLD, CobblestoneGeneratorSize.S),
        GOLD_M(TierCompressedCobblestone.GOLD, CobblestoneGeneratorSize.M),
        GOLD_L(TierCompressedCobblestone.GOLD, CobblestoneGeneratorSize.L),
        AMETHYST_S(TierCompressedCobblestone.AMETHYST, CobblestoneGeneratorSize.S),
        AMETHYST_M(TierCompressedCobblestone.AMETHYST, CobblestoneGeneratorSize.M),
        AMETHYST_L(TierCompressedCobblestone.AMETHYST, CobblestoneGeneratorSize.L),
        AQUAMARINE_S(TierCompressedCobblestone.AQUAMARINE, CobblestoneGeneratorSize.S),
        AQUAMARINE_M(TierCompressedCobblestone.AQUAMARINE, CobblestoneGeneratorSize.M),
        AQUAMARINE_L(TierCompressedCobblestone.AQUAMARINE, CobblestoneGeneratorSize.L),
        TOPAZ_S(TierCompressedCobblestone.TOPAZ, CobblestoneGeneratorSize.S),
        TOPAZ_M(TierCompressedCobblestone.TOPAZ, CobblestoneGeneratorSize.M),
        TOPAZ_L(TierCompressedCobblestone.TOPAZ, CobblestoneGeneratorSize.L),
        RUBY_S(TierCompressedCobblestone.RUBY, CobblestoneGeneratorSize.S),
        RUBY_M(TierCompressedCobblestone.RUBY, CobblestoneGeneratorSize.M),
        RUBY_L(TierCompressedCobblestone.RUBY, CobblestoneGeneratorSize.L),
        SAPPHIRE_S(TierCompressedCobblestone.SAPPHIRE, CobblestoneGeneratorSize.S),
        SAPPHIRE_M(TierCompressedCobblestone.SAPPHIRE, CobblestoneGeneratorSize.M),
        SAPPHIRE_L(TierCompressedCobblestone.SAPPHIRE, CobblestoneGeneratorSize.L),
        DIAMOND_S(TierCompressedCobblestone.DIAMOND, CobblestoneGeneratorSize.S),
        DIAMOND_M(TierCompressedCobblestone.DIAMOND, CobblestoneGeneratorSize.M),
        DIAMOND_L(TierCompressedCobblestone.DIAMOND, CobblestoneGeneratorSize.L),
        EMERALD_S(TierCompressedCobblestone.EMERALD, CobblestoneGeneratorSize.S),
        EMERALD_M(TierCompressedCobblestone.EMERALD, CobblestoneGeneratorSize.M),
        EMERALD_L(TierCompressedCobblestone.EMERALD, CobblestoneGeneratorSize.L),
        NETHERITE_S(TierCompressedCobblestone.NETHERITE, CobblestoneGeneratorSize.S),
        NETHERITE_M(TierCompressedCobblestone.NETHERITE, CobblestoneGeneratorSize.M),
        NETHERITE_L(TierCompressedCobblestone.NETHERITE, CobblestoneGeneratorSize.L),
        OBSIDIAN_S(TierCompressedCobblestone.OBSIDIAN, CobblestoneGeneratorSize.S),
        OBSIDIAN_M(TierCompressedCobblestone.OBSIDIAN, CobblestoneGeneratorSize.M),
        OBSIDIAN_L(TierCompressedCobblestone.OBSIDIAN, CobblestoneGeneratorSize.L);

        private final TierCompressedCobblestone tier;
        private final CobblestoneGeneratorSize size;
        private final String registryName;
        private final String englishDisplayName;
        private final Supplier<DeferredBlock<Block>> producedBlockSupplier;
        private DeferredBlock<Block> block;

        TierCobblestoneGenerator(TierCompressedCobblestone tier, CobblestoneGeneratorSize size) {
            this.tier = tier;
            this.size = size;
            this.registryName = tier.getRegistryName().replace(
                "_compressed_cobblestone",
                "_cobblestone_generator_" + size.getRegistrySuffix()
            );
            this.englishDisplayName = tier.getEnglishDisplayName().replace(
                "Compressed Cobblestone",
                "Cobblestone Generator " + size.getDisplayName()
            );
            this.producedBlockSupplier = tier::getBlock;
        }

        TierCobblestoneGenerator(
            String registryName,
            String englishDisplayName,
            Supplier<DeferredBlock<Block>> producedBlockSupplier,
            CobblestoneGeneratorSize size
        ) {
            this.tier = null;
            this.size = size;
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
            this.producedBlockSupplier = producedBlockSupplier;
        }

        public TierCompressedCobblestone getTier() {
            return this.tier;
        }

        public CobblestoneGeneratorSize getSize() {
            return this.size;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public boolean hasTier() {
            return this.tier != null;
        }

        public int getProductionPerTick() {
            return this.size.getProductionPerTick();
        }

        public String getTextureFolderName() {
            return this.size.getTextureFolderName();
        }

        public DeferredBlock<Block> getProducedBlock() {
            return this.producedBlockSupplier.get();
        }

        public DeferredBlock<Block> getBlock() {
            return this.block;
        }

        private void setBlock(DeferredBlock<Block> block) {
            this.block = block;
        }

        public static TierCobblestoneGenerator from(TierCompressedCobblestone tier, CobblestoneGeneratorSize size) {
            for (TierCobblestoneGenerator generator : values()) {
                if (generator.getTier() == tier && generator.getSize() == size) {
                    return generator;
                }
            }

            throw new IllegalArgumentException("該当する generator が見つかりません: tier=" + tier + ", size=" + size);
        }
    }

    private static BlockBehaviour.Properties createCompressedCobblestoneProperties() {
        return BlockBehaviour.Properties
            .of()
            .mapColor(MapColor.STONE)
            .strength(2.0F, 6.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE);
    }

    private static DeferredBlock<Block> registerCompressedCobblestone(String name) {
        return BLOCKS.register(name, () -> new Block(createCompressedCobblestoneProperties()));
    }

    private static DeferredBlock<Block> registerCompressedStone(String name) {
        return BLOCKS.register(name, () -> new Block(createCompressedCobblestoneProperties()));
    }

    private static BlockBehaviour.Properties createCobblestoneMachineCasingProperties() {
        return BlockBehaviour.Properties
            .of()
            .mapColor(MapColor.STONE)
            .strength(2.0F, 6.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE);
    }

    private static DeferredBlock<Block> registerCobblestoneMachineCasing(String name) {
        return BLOCKS.register(name, () -> new Block(createCobblestoneMachineCasingProperties()));
    }

    private static long calculateCobblestoneTankCapacity(int tierLevel) {
        long capacity = BASE_COBBLESTONE_TANK_CAPACITY;
        for (int index = 0; index < tierLevel; index++) {
            capacity *= 8L;
        }

        return capacity;
    }

    private static BlockBehaviour.Properties createCobblestoneTankProperties() {
        return BlockBehaviour.Properties
            .of()
            .mapColor(MapColor.STONE)
            .strength(0.5F)
            .sound(SoundType.STONE);
    }

    private static DeferredBlock<Block> registerCobblestoneTank(String name, long capacity) {
        return BLOCKS.register(name, () -> new CobblestoneTankBlock(createCobblestoneTankProperties(), capacity));
    }

    private static BlockBehaviour.Properties createCobblestoneGeneratorProperties() {
        return BlockBehaviour.Properties
            .of()
            .mapColor(MapColor.STONE)
            .strength(0.5F)
            .sound(SoundType.STONE);
    }

    private static DeferredBlock<Block> registerCobblestoneGenerator(TierCobblestoneGenerator generatorVariant) {
        return BLOCKS.register(
            generatorVariant.getRegistryName(),
            () -> new CobblestoneGeneratorBlock(createCobblestoneGeneratorProperties(), generatorVariant)
        );
    }

    public static final DeferredBlock<Block> COMPRESSED_COBBLESTONE =
        registerCompressedCobblestone("compressed_cobblestone");

    public static final DeferredBlock<Block> COMPRESSED_STONE =
        registerCompressedStone("compressed_stone");

    public static final DeferredBlock<Block> COBBLESTONE_MACHINE_CASING =
        registerCobblestoneMachineCasing("cobblestone_machine_casing");

    public static final DeferredBlock<Block> COBBLESTONE_TANK =
        registerCobblestoneTank("cobblestone_tank", BASE_COBBLESTONE_TANK_CAPACITY);

    static {
        for (TierCompressedCobblestone tier : TierCompressedCobblestone.values()) {
            tier.setBlock(registerCompressedCobblestone(tier.getRegistryName()));
        }
    }

    static {
        for (TierCompressedStone tier : TierCompressedStone.values()) {
            tier.setBlock(registerCompressedStone(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneMachineCasing tier : TierCobblestoneMachineCasing.values()) {
            tier.setBlock(registerCobblestoneMachineCasing(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneTank tier : TierCobblestoneTank.values()) {
            tier.setBlock(registerCobblestoneTank(tier.getRegistryName(), tier.getCapacity()));
        }
    }

    static {
        for (TierCobblestoneGenerator generatorVariant : TierCobblestoneGenerator.values()) {
            generatorVariant.setBlock(registerCobblestoneGenerator(generatorVariant));
        }
    }

    public static final DeferredBlock<Block> COBBLESTONE_FURNACE =
        BLOCKS.register(
            "cobblestone_furnace", 
            () -> new CobblestoneFurnaceBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
            );

    public static final DeferredBlock<Block> COBBLESTONE_POWERED_FURNACE =
        BLOCKS.register(
            "cobblestone_powered_furnace",
            () -> new CobblestonePoweredFurnaceBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_CRUSHER =
        BLOCKS.register(
            "cobblestone_crusher",
            () -> new CobblestoneCrusherBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_FE_GENERATOR =
        BLOCKS.register(
            "cobblestone_fe_generator",
            () -> new CobblestoneFEGeneratorBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_CENTRIFUGE =
        BLOCKS.register(
            "cobblestone_centrifuge",
            () -> new CobblestoneCentrifugeBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_LASER_DRILL =
        BLOCKS.register(
            "cobblestone_laser_drill",
            () -> new CobblestoneLaserDrillBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_MIXER =
        BLOCKS.register(
            "cobblestone_mixer",
            () -> new CobblestoneMixerBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_MELTER =
        BLOCKS.register(
            "cobblestone_melter",
            () -> new CobblestoneMelterBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_ASSEMBLY_MACHINE =
        BLOCKS.register(
            "cobblestone_assembly_machine",
            () -> new CobblestoneAssemblyMachineBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_CHEMICAL_REACTOR =
        BLOCKS.register(
            "cobblestone_chemical_reactor",
            () -> new CobblestoneChemicalReactorBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_REACTION_CHAMBER =
        BLOCKS.register(
            "cobblestone_reaction_chamber",
            () -> new CobblestoneReactionChamberBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_CRYSTALLIZATION_CHAMBER =
        BLOCKS.register(
            "cobblestone_crystallization_chamber",
            () -> new CobblestoneCrystallizationChamberBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_DISSOLUTION_CHAMBER =
        BLOCKS.register(
            "cobblestone_dissolution_chamber",
            () -> new CobblestoneDissolutionChamberBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );

    public static final DeferredBlock<Block> COBBLESTONE_FLUID_MIXER =
        BLOCKS.register(
            "cobblestone_fluid_mixer",
            () -> new CobblestoneFluidMixerBlock(
                BlockBehaviour.Properties
                    .of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .sound(SoundType.STONE)
            )
        );
}