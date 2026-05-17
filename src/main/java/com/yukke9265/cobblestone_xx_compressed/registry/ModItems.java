package com.yukke9265.cobblestone_xx_compressed.registry;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = 
        DeferredRegister.createItems(CobblestonexXCompressed.MODID);

    private static final int DEFAULT_STACK_SIZE = 64;
    private static final int COBBLESTONE_BREAD_STACK_SIZE = 64;
    private static final int COBBLESTONE_BREAD_NUTRITION = 4;
    private static final float COBBLESTONE_BREAD_SATURATION = 0.3f;
    private static final int COBBLESTONE_BREAD_CONFUSION_DURATION = 200;
    private static final int COBBLESTONE_BREAD_CONFUSION_AMPLIFIER = 100;
    private static final int COBBLESTONE_BREAD_RESISTANCE_DURATION = 1200;
    private static final int COBBLESTONE_BREAD_RESISTANCE_AMPLIFIER = 1;

    // tier 一覧は今後増える可能性が高いため、
    // 1 か所に集約して「登録名」「英語表示名」「登録された Item」をまとめて持たせます。
    // tier を増やすときは、基本的にこの enum へ 1 行足すのが起点になります。
    public enum TierCobblestoneBread {
        COPPER("tier_copper_cobblestone_bread", "Copper Cobblestone Bread"),
        IRON("tier_iron_cobblestone_bread", "Iron Cobblestone Bread"),
        GOLD("tier_gold_cobblestone_bread", "Gold Cobblestone Bread"),
        AMETHYST("tier_amethyst_cobblestone_bread", "Amethyst Cobblestone Bread"),
        AQUAMARINE("tier_aquamarine_cobblestone_bread", "Aquamarine Cobblestone Bread"),
        TOPAZ("tier_topaz_cobblestone_bread", "Topaz Cobblestone Bread"),
        RUBY("tier_ruby_cobblestone_bread", "Ruby Cobblestone Bread"),
        SAPPHIRE("tier_sapphire_cobblestone_bread", "Sapphire Cobblestone Bread"),
        DIAMOND("tier_diamond_cobblestone_bread", "Diamond Cobblestone Bread"),
        EMERALD("tier_emerald_cobblestone_bread", "Emerald Cobblestone Bread"),
        NETHERITE("tier_netherite_cobblestone_bread", "Netherite Cobblestone Bread"),
        OBSIDIAN("tier_obsidian_cobblestone_bread", "Obsidian Cobblestone Bread");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneBread(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    // 丸石ジェムも tier 名と登録名の規則が同じなので、
    // パンと同じ考え方で enum にまとめておくと追加箇所が追いやすくなります。
    public enum TierCobblestoneGem {
        COPPER("tier_copper_cobblestone_gem", "Copper Cobblestone Gem"),
        IRON("tier_iron_cobblestone_gem", "Iron Cobblestone Gem"),
        GOLD("tier_gold_cobblestone_gem", "Gold Cobblestone Gem"),
        AMETHYST("tier_amethyst_cobblestone_gem", "Amethyst Cobblestone Gem"),
        AQUAMARINE("tier_aquamarine_cobblestone_gem", "Aquamarine Cobblestone Gem"),
        TOPAZ("tier_topaz_cobblestone_gem", "Topaz Cobblestone Gem"),
        RUBY("tier_ruby_cobblestone_gem", "Ruby Cobblestone Gem"),
        SAPPHIRE("tier_sapphire_cobblestone_gem", "Sapphire Cobblestone Gem"),
        DIAMOND("tier_diamond_cobblestone_gem", "Diamond Cobblestone Gem"),
        EMERALD("tier_emerald_cobblestone_gem", "Emerald Cobblestone Gem"),
        NETHERITE("tier_netherite_cobblestone_gem", "Netherite Cobblestone Gem"),
        OBSIDIAN("tier_obsidian_cobblestone_gem", "Obsidian Cobblestone Gem");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneGem(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    // 丸石ダストも tier ごとの登録名と表示名の規則が揃っているので、
    // enum に集約しておくと追加先が散らばりません。
    public enum TierCobblestoneDust {
        COPPER("tier_copper_cobblestone_dust", "Copper Cobblestone Dust"),
        IRON("tier_iron_cobblestone_dust", "Iron Cobblestone Dust"),
        GOLD("tier_gold_cobblestone_dust", "Gold Cobblestone Dust"),
        AMETHYST("tier_amethyst_cobblestone_dust", "Amethyst Cobblestone Dust"),
        AQUAMARINE("tier_aquamarine_cobblestone_dust", "Aquamarine Cobblestone Dust"),
        TOPAZ("tier_topaz_cobblestone_dust", "Topaz Cobblestone Dust"),
        RUBY("tier_ruby_cobblestone_dust", "Ruby Cobblestone Dust"),
        SAPPHIRE("tier_sapphire_cobblestone_dust", "Sapphire Cobblestone Dust"),
        DIAMOND("tier_diamond_cobblestone_dust", "Diamond Cobblestone Dust"),
        EMERALD("tier_emerald_cobblestone_dust", "Emerald Cobblestone Dust"),
        NETHERITE("tier_netherite_cobblestone_dust", "Netherite Cobblestone Dust"),
        OBSIDIAN("tier_obsidian_cobblestone_dust", "Obsidian Cobblestone Dust");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneDust(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    // 汚れた丸石ダストも tier ごとの規則がそろっているので、
    // 通常の丸石ダストと同じ形で enum に集約しておきます。
    public enum TierCobblestoneDirtyDust {
        COPPER("tier_copper_cobblestone_dirty_dust", "Copper Cobblestone Dirty Dust"),
        IRON("tier_iron_cobblestone_dirty_dust", "Iron Cobblestone Dirty Dust"),
        GOLD("tier_gold_cobblestone_dirty_dust", "Gold Cobblestone Dirty Dust"),
        AMETHYST("tier_amethyst_cobblestone_dirty_dust", "Amethyst Cobblestone Dirty Dust"),
        AQUAMARINE("tier_aquamarine_cobblestone_dirty_dust", "Aquamarine Cobblestone Dirty Dust"),
        TOPAZ("tier_topaz_cobblestone_dirty_dust", "Topaz Cobblestone Dirty Dust"),
        RUBY("tier_ruby_cobblestone_dirty_dust", "Ruby Cobblestone Dirty Dust"),
        SAPPHIRE("tier_sapphire_cobblestone_dirty_dust", "Sapphire Cobblestone Dirty Dust"),
        DIAMOND("tier_diamond_cobblestone_dirty_dust", "Diamond Cobblestone Dirty Dust"),
        EMERALD("tier_emerald_cobblestone_dirty_dust", "Emerald Cobblestone Dirty Dust"),
        NETHERITE("tier_netherite_cobblestone_dirty_dust", "Netherite Cobblestone Dirty Dust"),
        OBSIDIAN("tier_obsidian_cobblestone_dirty_dust", "Obsidian Cobblestone Dirty Dust");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneDirtyDust(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    // 混合丸石ダストも tier ごとの命名規則がそろっているため、
    // 通常の丸石ダストと同じ考え方で enum へ集約しておきます。
    public enum TierCobblestoneMixedDust {
        COPPER("tier_copper_cobblestone_mixed_dust", "Copper Cobblestone Mixed Dust"),
        IRON("tier_iron_cobblestone_mixed_dust", "Iron Cobblestone Mixed Dust"),
        GOLD("tier_gold_cobblestone_mixed_dust", "Gold Cobblestone Mixed Dust"),
        AMETHYST("tier_amethyst_cobblestone_mixed_dust", "Amethyst Cobblestone Mixed Dust"),
        AQUAMARINE("tier_aquamarine_cobblestone_mixed_dust", "Aquamarine Cobblestone Mixed Dust"),
        TOPAZ("tier_topaz_cobblestone_mixed_dust", "Topaz Cobblestone Mixed Dust"),
        RUBY("tier_ruby_cobblestone_mixed_dust", "Ruby Cobblestone Mixed Dust"),
        SAPPHIRE("tier_sapphire_cobblestone_mixed_dust", "Sapphire Cobblestone Mixed Dust"),
        DIAMOND("tier_diamond_cobblestone_mixed_dust", "Diamond Cobblestone Mixed Dust"),
        EMERALD("tier_emerald_cobblestone_mixed_dust", "Emerald Cobblestone Mixed Dust"),
        NETHERITE("tier_netherite_cobblestone_mixed_dust", "Netherite Cobblestone Mixed Dust"),
        OBSIDIAN("tier_obsidian_cobblestone_mixed_dust", "Obsidian Cobblestone Mixed Dust");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneMixedDust(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    // 宝石系ダスト 5 種は、登録名と英語表示名の対応が固定なので、
    // enum にまとめておくと creative tab・lang・model で同じ一覧を使い回せます。
    public enum GemDust {
        AMETHYST("amethyst_dust", "Amethyst Dust"),
        AQUAMARINE("aquamarine_dust", "Aquamarine Dust"),
        TOPAZ("topaz_dust", "Topaz Dust"),
        RUBY("ruby_dust", "Ruby Dust"),
        SAPPHIRE("sapphire_dust", "Sapphire Dust");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        GemDust(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    // tier 違いの丸石パンは、見た目だけでなく登録名も規則的なので、
    // 共通の Item.Properties を使う小さな補助メソッドを用意しておくと追加手順が追いやすくなります。
    private static DeferredItem<Item> registerTierCobblestoneBread(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneBreadProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneGem(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneGemProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneDust(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneDustProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneDirtyDust(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneDustProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneMixedDust(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneDustProperties());
    }

    private static DeferredItem<Item> registerGemDust(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneDustProperties());
    }

    private static DeferredItem<Item> registerTierCompressedCobblestoneSingularityBit(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneSingularityProperties());
    }

    private static DeferredItem<Item> registerTierCompressedCobblestoneSingularityFragment(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneSingularityProperties());
    }

    private static DeferredItem<Item> registerTierCompressedCobblestoneSingularity(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneSingularityProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneWire(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneComponentProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneRod(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneComponentProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneMotor(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneComponentProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneCircuit(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneComponentProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneProcessor(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneComponentProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneCircuitPackage(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneComponentProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneAccelerationChip(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneComponentProperties());
    }

    private static DeferredItem<Item> registerTierCobblestoneEnergizedCube(String name) {
        return ITEMS.registerSimpleItem(name, createCobblestoneComponentProperties());
    }

    private static DeferredItem<BlockItem> registerCompressedCobblestoneBlockItem(String name, DeferredBlock<Block> block) {
        return ITEMS.registerSimpleBlockItem(name, block);
    }

    // 丸石パン系は、今の段階では通常版も tier 版も同じ食べ物設定を使います。
    // 後から栄養値や効果を調整したくなったとき、この 1 か所を直せば全 tier に反映されます。
    private static Item.Properties createCobblestoneBreadProperties() {
        return new Item.Properties()
            .stacksTo(COBBLESTONE_BREAD_STACK_SIZE)
            .food(new FoodProperties.Builder()
                .nutrition(COBBLESTONE_BREAD_NUTRITION)
                .saturationModifier(COBBLESTONE_BREAD_SATURATION)
                .alwaysEdible()
                // 食べた直後に少し酔い、その代わり一定時間だけ耐性が付く共通設定です。
                .effect(
                    () -> new MobEffectInstance(
                        MobEffects.CONFUSION,
                        COBBLESTONE_BREAD_CONFUSION_DURATION,
                        COBBLESTONE_BREAD_CONFUSION_AMPLIFIER
                    ),
                    1.0f
                )
                .effect(
                    () -> new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        COBBLESTONE_BREAD_RESISTANCE_DURATION,
                        COBBLESTONE_BREAD_RESISTANCE_AMPLIFIER
                    ),
                    1.0f
                )
                .build());
    }

    // 丸石ジェムは今の段階では特殊効果のない通常アイテムとして扱います。
    // 後から耐火や希少素材向けの設定を加えたい場合も、この 1 か所を直せば済みます。
    private static Item.Properties createCobblestoneGemProperties() {
        return new Item.Properties().stacksTo(DEFAULT_STACK_SIZE);
    }

    // 丸石ダストも現状では通常素材アイテムなので、
    // ジェムと同じくシンプルな stack 設定だけを持たせます。
    private static Item.Properties createCobblestoneDustProperties() {
        return new Item.Properties().stacksTo(DEFAULT_STACK_SIZE);
    }

    // singularity 系 3 種も現状では通常素材アイテムとして扱います。
    // bit -> fragment -> singularity と段階は分かれていますが、
    // まだ固有挙動は持たせないため、共通の Item.Properties にまとめておきます。
    private static Item.Properties createCobblestoneSingularityProperties() {
        return new Item.Properties().stacksTo(DEFAULT_STACK_SIZE);
    }

    // 回路部品 4 種は現状ではすべて通常素材アイテムとして扱うため、
    // 同じ stack 設定を共通メソッドにまとめておきます。
    // 後から耐火や希少度に応じた設定を入れたい場合も、この 1 か所を見れば済みます。
    private static Item.Properties createCobblestoneComponentProperties() {
        return new Item.Properties().stacksTo(DEFAULT_STACK_SIZE);
    }

    // cobblestone_bread アイテムを登録します。
    public static final DeferredItem<Item> COBBLESTONE_BREAD = 
        ITEMS.registerSimpleItem("cobblestone_bread", createCobblestoneBreadProperties());

    public static final DeferredItem<Item> COBBLESTONE_GEM =
        ITEMS.registerSimpleItem("cobblestone_gem", createCobblestoneGemProperties());

    public static final DeferredItem<Item> AQUAMARINE_SHARD =
        ITEMS.registerSimpleItem("aquamarine_shard", createCobblestoneGemProperties());

    public static final DeferredItem<Item> TOPAZ_SHARD =
        ITEMS.registerSimpleItem("topaz_shard", createCobblestoneGemProperties());

    public static final DeferredItem<Item> RUBY_SHARD =
        ITEMS.registerSimpleItem("ruby_shard", createCobblestoneGemProperties());

    public static final DeferredItem<Item> SAPPHIRE_SHARD =
        ITEMS.registerSimpleItem("sapphire_shard", createCobblestoneGemProperties());

    public static final DeferredItem<Item> COBBLESTONE_DUST =
        ITEMS.registerSimpleItem("cobblestone_dust", createCobblestoneDustProperties());

    public static final DeferredItem<Item> COBBLESTONE_DIRTY_DUST =
        ITEMS.registerSimpleItem("cobblestone_dirty_dust", createCobblestoneDustProperties());

    public static final DeferredItem<Item> COBBLESTONE_MIXED_DUST =
        ITEMS.registerSimpleItem("cobblestone_mixed_dust", createCobblestoneDustProperties());

    public static final DeferredItem<Item> DIRTY_MIXTURE =
        ITEMS.registerSimpleItem("dirty_mixture", createCobblestoneDustProperties());

    public static final DeferredItem<Item> COAL_DUST =
        ITEMS.registerSimpleItem("coal_dust", createCobblestoneDustProperties());

    public static final DeferredItem<Item> COMPRESSED_COBBLESTONE_SINGULARITY_BIT =
        ITEMS.registerSimpleItem("compressed_cobblestone_singularity_bit", createCobblestoneSingularityProperties());

    public static final DeferredItem<Item> COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT =
        ITEMS.registerSimpleItem("compressed_cobblestone_singularity_fragment", createCobblestoneSingularityProperties());

    public static final DeferredItem<Item> COMPRESSED_COBBLESTONE_SINGULARITY =
        ITEMS.registerSimpleItem("compressed_cobblestone_singularity", createCobblestoneSingularityProperties());

    public static final DeferredItem<Item> COBBLESTONE_WIRE =
        ITEMS.registerSimpleItem("cobblestone_wire", createCobblestoneComponentProperties());

    public static final DeferredItem<Item> COBBLESTONE_ROD =
        ITEMS.registerSimpleItem("cobblestone_rod", createCobblestoneComponentProperties());

    public static final DeferredItem<Item> COBBLESTONE_MOTOR =
        ITEMS.registerSimpleItem("cobblestone_motor", createCobblestoneComponentProperties());

    public static final DeferredItem<Item> COBBLESTONE_CIRCUIT =
        ITEMS.registerSimpleItem("cobblestone_circuit", createCobblestoneComponentProperties());

    public static final DeferredItem<Item> COBBLESTONE_PROCESSOR =
        ITEMS.registerSimpleItem("cobblestone_processor", createCobblestoneComponentProperties());

    public static final DeferredItem<Item> COBBLESTONE_CIRCUIT_PACKAGE =
        ITEMS.registerSimpleItem("cobblestone_circuit_package", createCobblestoneComponentProperties());

    public static final DeferredItem<Item> COBBLESTONE_ACCELERATION_CHIP =
        ITEMS.registerSimpleItem("cobblestone_acceleration_chip", createCobblestoneComponentProperties());

    public static final DeferredItem<Item> COBBLESTONE_ENERGIZED_CUBE =
        ITEMS.registerSimpleItem("cobblestone_energized_cube", createCobblestoneComponentProperties());

    // 丸石ワイヤー系は tier ごとに登録名と英語名の規則がそろっているため、
    // enum にまとめておくと lang・model・creative tab 側で同じ順番を再利用できます。
    public enum TierCobblestoneWire {
        COPPER("tier_copper_cobblestone_wire", "Copper Cobblestone Wire"),
        IRON("tier_iron_cobblestone_wire", "Iron Cobblestone Wire"),
        GOLD("tier_gold_cobblestone_wire", "Gold Cobblestone Wire"),
        AMETHYST("tier_amethyst_cobblestone_wire", "Amethyst Cobblestone Wire"),
        AQUAMARINE("tier_aquamarine_cobblestone_wire", "Aquamarine Cobblestone Wire"),
        TOPAZ("tier_topaz_cobblestone_wire", "Topaz Cobblestone Wire"),
        RUBY("tier_ruby_cobblestone_wire", "Ruby Cobblestone Wire"),
        SAPPHIRE("tier_sapphire_cobblestone_wire", "Sapphire Cobblestone Wire"),
        DIAMOND("tier_diamond_cobblestone_wire", "Diamond Cobblestone Wire"),
        EMERALD("tier_emerald_cobblestone_wire", "Emerald Cobblestone Wire"),
        NETHERITE("tier_netherite_cobblestone_wire", "Netherite Cobblestone Wire"),
        OBSIDIAN("tier_obsidian_cobblestone_wire", "Obsidian Cobblestone Wire");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneWire(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    // 丸石ロッドも wire と同じ tier 一覧を使う系列アイテムです。
    // enum へ集約しておくと、lang・model・recipe で同じ順番をそのまま再利用できます。
    public enum TierCobblestoneRod {
        COPPER("tier_copper_cobblestone_rod", "Copper Cobblestone Rod"),
        IRON("tier_iron_cobblestone_rod", "Iron Cobblestone Rod"),
        GOLD("tier_gold_cobblestone_rod", "Gold Cobblestone Rod"),
        AMETHYST("tier_amethyst_cobblestone_rod", "Amethyst Cobblestone Rod"),
        AQUAMARINE("tier_aquamarine_cobblestone_rod", "Aquamarine Cobblestone Rod"),
        TOPAZ("tier_topaz_cobblestone_rod", "Topaz Cobblestone Rod"),
        RUBY("tier_ruby_cobblestone_rod", "Ruby Cobblestone Rod"),
        SAPPHIRE("tier_sapphire_cobblestone_rod", "Sapphire Cobblestone Rod"),
        DIAMOND("tier_diamond_cobblestone_rod", "Diamond Cobblestone Rod"),
        EMERALD("tier_emerald_cobblestone_rod", "Emerald Cobblestone Rod"),
        NETHERITE("tier_netherite_cobblestone_rod", "Netherite Cobblestone Rod"),
        OBSIDIAN("tier_obsidian_cobblestone_rod", "Obsidian Cobblestone Rod");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneRod(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    public enum TierCobblestoneMotor {
        COPPER("tier_copper_cobblestone_motor", "Copper Cobblestone Motor"),
        IRON("tier_iron_cobblestone_motor", "Iron Cobblestone Motor"),
        GOLD("tier_gold_cobblestone_motor", "Gold Cobblestone Motor"),
        AMETHYST("tier_amethyst_cobblestone_motor", "Amethyst Cobblestone Motor"),
        AQUAMARINE("tier_aquamarine_cobblestone_motor", "Aquamarine Cobblestone Motor"),
        TOPAZ("tier_topaz_cobblestone_motor", "Topaz Cobblestone Motor"),
        RUBY("tier_ruby_cobblestone_motor", "Ruby Cobblestone Motor"),
        SAPPHIRE("tier_sapphire_cobblestone_motor", "Sapphire Cobblestone Motor"),
        DIAMOND("tier_diamond_cobblestone_motor", "Diamond Cobblestone Motor"),
        EMERALD("tier_emerald_cobblestone_motor", "Emerald Cobblestone Motor"),
        NETHERITE("tier_netherite_cobblestone_motor", "Netherite Cobblestone Motor"),
        OBSIDIAN("tier_obsidian_cobblestone_motor", "Obsidian Cobblestone Motor");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneMotor(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    public enum TierCobblestoneCircuit {
        COPPER("tier_copper_cobblestone_circuit", "Copper Cobblestone Circuit"),
        IRON("tier_iron_cobblestone_circuit", "Iron Cobblestone Circuit"),
        GOLD("tier_gold_cobblestone_circuit", "Gold Cobblestone Circuit"),
        AMETHYST("tier_amethyst_cobblestone_circuit", "Amethyst Cobblestone Circuit"),
        AQUAMARINE("tier_aquamarine_cobblestone_circuit", "Aquamarine Cobblestone Circuit"),
        TOPAZ("tier_topaz_cobblestone_circuit", "Topaz Cobblestone Circuit"),
        RUBY("tier_ruby_cobblestone_circuit", "Ruby Cobblestone Circuit"),
        SAPPHIRE("tier_sapphire_cobblestone_circuit", "Sapphire Cobblestone Circuit"),
        DIAMOND("tier_diamond_cobblestone_circuit", "Diamond Cobblestone Circuit"),
        EMERALD("tier_emerald_cobblestone_circuit", "Emerald Cobblestone Circuit"),
        NETHERITE("tier_netherite_cobblestone_circuit", "Netherite Cobblestone Circuit"),
        OBSIDIAN("tier_obsidian_cobblestone_circuit", "Obsidian Cobblestone Circuit");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneCircuit(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    public enum TierCobblestoneProcessor {
        COPPER("tier_copper_cobblestone_processor", "Copper Cobblestone Processor"),
        IRON("tier_iron_cobblestone_processor", "Iron Cobblestone Processor"),
        GOLD("tier_gold_cobblestone_processor", "Gold Cobblestone Processor"),
        AMETHYST("tier_amethyst_cobblestone_processor", "Amethyst Cobblestone Processor"),
        AQUAMARINE("tier_aquamarine_cobblestone_processor", "Aquamarine Cobblestone Processor"),
        TOPAZ("tier_topaz_cobblestone_processor", "Topaz Cobblestone Processor"),
        RUBY("tier_ruby_cobblestone_processor", "Ruby Cobblestone Processor"),
        SAPPHIRE("tier_sapphire_cobblestone_processor", "Sapphire Cobblestone Processor"),
        DIAMOND("tier_diamond_cobblestone_processor", "Diamond Cobblestone Processor"),
        EMERALD("tier_emerald_cobblestone_processor", "Emerald Cobblestone Processor"),
        NETHERITE("tier_netherite_cobblestone_processor", "Netherite Cobblestone Processor"),
        OBSIDIAN("tier_obsidian_cobblestone_processor", "Obsidian Cobblestone Processor");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneProcessor(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    public enum TierCobblestoneCircuitPackage {
        COPPER("tier_copper_cobblestone_circuit_package", "Copper Cobblestone Circuit Package"),
        IRON("tier_iron_cobblestone_circuit_package", "Iron Cobblestone Circuit Package"),
        GOLD("tier_gold_cobblestone_circuit_package", "Gold Cobblestone Circuit Package"),
        AMETHYST("tier_amethyst_cobblestone_circuit_package", "Amethyst Cobblestone Circuit Package"),
        AQUAMARINE("tier_aquamarine_cobblestone_circuit_package", "Aquamarine Cobblestone Circuit Package"),
        TOPAZ("tier_topaz_cobblestone_circuit_package", "Topaz Cobblestone Circuit Package"),
        RUBY("tier_ruby_cobblestone_circuit_package", "Ruby Cobblestone Circuit Package"),
        SAPPHIRE("tier_sapphire_cobblestone_circuit_package", "Sapphire Cobblestone Circuit Package"),
        DIAMOND("tier_diamond_cobblestone_circuit_package", "Diamond Cobblestone Circuit Package"),
        EMERALD("tier_emerald_cobblestone_circuit_package", "Emerald Cobblestone Circuit Package"),
        NETHERITE("tier_netherite_cobblestone_circuit_package", "Netherite Cobblestone Circuit Package"),
        OBSIDIAN("tier_obsidian_cobblestone_circuit_package", "Obsidian Cobblestone Circuit Package");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneCircuitPackage(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    public enum TierCobblestoneAccelerationChip {
        COPPER("tier_copper_cobblestone_acceleration_chip", "Copper Cobblestone Acceleration Chip"),
        IRON("tier_iron_cobblestone_acceleration_chip", "Iron Cobblestone Acceleration Chip"),
        GOLD("tier_gold_cobblestone_acceleration_chip", "Gold Cobblestone Acceleration Chip"),
        AMETHYST("tier_amethyst_cobblestone_acceleration_chip", "Amethyst Cobblestone Acceleration Chip"),
        AQUAMARINE("tier_aquamarine_cobblestone_acceleration_chip", "Aquamarine Cobblestone Acceleration Chip"),
        TOPAZ("tier_topaz_cobblestone_acceleration_chip", "Topaz Cobblestone Acceleration Chip"),
        RUBY("tier_ruby_cobblestone_acceleration_chip", "Ruby Cobblestone Acceleration Chip"),
        SAPPHIRE("tier_sapphire_cobblestone_acceleration_chip", "Sapphire Cobblestone Acceleration Chip"),
        DIAMOND("tier_diamond_cobblestone_acceleration_chip", "Diamond Cobblestone Acceleration Chip"),
        EMERALD("tier_emerald_cobblestone_acceleration_chip", "Emerald Cobblestone Acceleration Chip"),
        NETHERITE("tier_netherite_cobblestone_acceleration_chip", "Netherite Cobblestone Acceleration Chip"),
        OBSIDIAN("tier_obsidian_cobblestone_acceleration_chip", "Obsidian Cobblestone Acceleration Chip");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneAccelerationChip(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    public enum TierCobblestoneEnergizedCube {
        COPPER("tier_copper_cobblestone_energized_cube", "Copper Cobblestone Energized Cube"),
        IRON("tier_iron_cobblestone_energized_cube", "Iron Cobblestone Energized Cube"),
        GOLD("tier_gold_cobblestone_energized_cube", "Gold Cobblestone Energized Cube"),
        AMETHYST("tier_amethyst_cobblestone_energized_cube", "Amethyst Cobblestone Energized Cube"),
        AQUAMARINE("tier_aquamarine_cobblestone_energized_cube", "Aquamarine Cobblestone Energized Cube"),
        TOPAZ("tier_topaz_cobblestone_energized_cube", "Topaz Cobblestone Energized Cube"),
        RUBY("tier_ruby_cobblestone_energized_cube", "Ruby Cobblestone Energized Cube"),
        SAPPHIRE("tier_sapphire_cobblestone_energized_cube", "Sapphire Cobblestone Energized Cube"),
        DIAMOND("tier_diamond_cobblestone_energized_cube", "Diamond Cobblestone Energized Cube"),
        EMERALD("tier_emerald_cobblestone_energized_cube", "Emerald Cobblestone Energized Cube"),
        NETHERITE("tier_netherite_cobblestone_energized_cube", "Netherite Cobblestone Energized Cube"),
        OBSIDIAN("tier_obsidian_cobblestone_energized_cube", "Obsidian Cobblestone Energized Cube");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCobblestoneEnergizedCube(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    // singularity bit は tier ごとに名前と表示名の規則が完全にそろっているため、
    // まず enum へ集約しておくと登録・lang・model・recipe を同じ順番で回せます。
    public enum TierCompressedCobblestoneSingularityBit {
        COPPER("tier_copper_compressed_cobblestone_singularity_bit", "Copper Compressed Cobblestone Singularity Bit"),
        IRON("tier_iron_compressed_cobblestone_singularity_bit", "Iron Compressed Cobblestone Singularity Bit"),
        GOLD("tier_gold_compressed_cobblestone_singularity_bit", "Gold Compressed Cobblestone Singularity Bit"),
        AMETHYST("tier_amethyst_compressed_cobblestone_singularity_bit", "Amethyst Compressed Cobblestone Singularity Bit"),
        AQUAMARINE("tier_aquamarine_compressed_cobblestone_singularity_bit", "Aquamarine Compressed Cobblestone Singularity Bit"),
        TOPAZ("tier_topaz_compressed_cobblestone_singularity_bit", "Topaz Compressed Cobblestone Singularity Bit"),
        RUBY("tier_ruby_compressed_cobblestone_singularity_bit", "Ruby Compressed Cobblestone Singularity Bit"),
        SAPPHIRE("tier_sapphire_compressed_cobblestone_singularity_bit", "Sapphire Compressed Cobblestone Singularity Bit"),
        DIAMOND("tier_diamond_compressed_cobblestone_singularity_bit", "Diamond Compressed Cobblestone Singularity Bit"),
        EMERALD("tier_emerald_compressed_cobblestone_singularity_bit", "Emerald Compressed Cobblestone Singularity Bit"),
        NETHERITE("tier_netherite_compressed_cobblestone_singularity_bit", "Netherite Compressed Cobblestone Singularity Bit"),
        OBSIDIAN("tier_obsidian_compressed_cobblestone_singularity_bit", "Obsidian Compressed Cobblestone Singularity Bit");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCompressedCobblestoneSingularityBit(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    // fragment も bit と同じ tier 並びを使うため、
    // enum 化しておくと 1 つ上の bit からのクラフトレシピを追いやすくなります。
    public enum TierCompressedCobblestoneSingularityFragment {
        COPPER("tier_copper_compressed_cobblestone_singularity_fragment", "Copper Compressed Cobblestone Singularity Fragment"),
        IRON("tier_iron_compressed_cobblestone_singularity_fragment", "Iron Compressed Cobblestone Singularity Fragment"),
        GOLD("tier_gold_compressed_cobblestone_singularity_fragment", "Gold Compressed Cobblestone Singularity Fragment"),
        AMETHYST("tier_amethyst_compressed_cobblestone_singularity_fragment", "Amethyst Compressed Cobblestone Singularity Fragment"),
        AQUAMARINE("tier_aquamarine_compressed_cobblestone_singularity_fragment", "Aquamarine Compressed Cobblestone Singularity Fragment"),
        TOPAZ("tier_topaz_compressed_cobblestone_singularity_fragment", "Topaz Compressed Cobblestone Singularity Fragment"),
        RUBY("tier_ruby_compressed_cobblestone_singularity_fragment", "Ruby Compressed Cobblestone Singularity Fragment"),
        SAPPHIRE("tier_sapphire_compressed_cobblestone_singularity_fragment", "Sapphire Compressed Cobblestone Singularity Fragment"),
        DIAMOND("tier_diamond_compressed_cobblestone_singularity_fragment", "Diamond Compressed Cobblestone Singularity Fragment"),
        EMERALD("tier_emerald_compressed_cobblestone_singularity_fragment", "Emerald Compressed Cobblestone Singularity Fragment"),
        NETHERITE("tier_netherite_compressed_cobblestone_singularity_fragment", "Netherite Compressed Cobblestone Singularity Fragment"),
        OBSIDIAN("tier_obsidian_compressed_cobblestone_singularity_fragment", "Obsidian Compressed Cobblestone Singularity Fragment");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCompressedCobblestoneSingularityFragment(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    // 最終段の singularity も同じ tier 規則なので、
    // 下位素材と同じ名前順で並べておくと valueOf(...) で対応 tier を引けます。
    public enum TierCompressedCobblestoneSingularity {
        COPPER("tier_copper_compressed_cobblestone_singularity", "Copper Compressed Cobblestone Singularity"),
        IRON("tier_iron_compressed_cobblestone_singularity", "Iron Compressed Cobblestone Singularity"),
        GOLD("tier_gold_compressed_cobblestone_singularity", "Gold Compressed Cobblestone Singularity"),
        AMETHYST("tier_amethyst_compressed_cobblestone_singularity", "Amethyst Compressed Cobblestone Singularity"),
        AQUAMARINE("tier_aquamarine_compressed_cobblestone_singularity", "Aquamarine Compressed Cobblestone Singularity"),
        TOPAZ("tier_topaz_compressed_cobblestone_singularity", "Topaz Compressed Cobblestone Singularity"),
        RUBY("tier_ruby_compressed_cobblestone_singularity", "Ruby Compressed Cobblestone Singularity"),
        SAPPHIRE("tier_sapphire_compressed_cobblestone_singularity", "Sapphire Compressed Cobblestone Singularity"),
        DIAMOND("tier_diamond_compressed_cobblestone_singularity", "Diamond Compressed Cobblestone Singularity"),
        EMERALD("tier_emerald_compressed_cobblestone_singularity", "Emerald Compressed Cobblestone Singularity"),
        NETHERITE("tier_netherite_compressed_cobblestone_singularity", "Netherite Compressed Cobblestone Singularity"),
        OBSIDIAN("tier_obsidian_compressed_cobblestone_singularity", "Obsidian Compressed Cobblestone Singularity");

        private final String registryName;
        private final String englishDisplayName;
        private DeferredItem<Item> item;

        TierCompressedCobblestoneSingularity(String registryName, String englishDisplayName) {
            this.registryName = registryName;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public DeferredItem<Item> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<Item> item) {
            this.item = item;
        }
    }

    static {
        // enum に並んだ順番で登録しておくと、
        // creative tab や datagen でも同じ順番をそのまま再利用できます。
        for (TierCobblestoneBread tier : TierCobblestoneBread.values()) {
            tier.setItem(registerTierCobblestoneBread(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneGem tier : TierCobblestoneGem.values()) {
            tier.setItem(registerTierCobblestoneGem(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneDust tier : TierCobblestoneDust.values()) {
            tier.setItem(registerTierCobblestoneDust(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneDirtyDust tier : TierCobblestoneDirtyDust.values()) {
            tier.setItem(registerTierCobblestoneDirtyDust(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneMixedDust tier : TierCobblestoneMixedDust.values()) {
            tier.setItem(registerTierCobblestoneMixedDust(tier.getRegistryName()));
        }
    }

    static {
        for (GemDust dust : GemDust.values()) {
            dust.setItem(registerGemDust(dust.getRegistryName()));
        }
    }

    public static final DeferredItem<Item> AMETHYST_DUST =
        GemDust.AMETHYST.getItem();

    public static final DeferredItem<Item> AQUAMARINE_DUST =
        GemDust.AQUAMARINE.getItem();

    public static final DeferredItem<Item> TOPAZ_DUST =
        GemDust.TOPAZ.getItem();

    public static final DeferredItem<Item> RUBY_DUST =
        GemDust.RUBY.getItem();

    public static final DeferredItem<Item> SAPPHIRE_DUST =
        GemDust.SAPPHIRE.getItem();

    static {
        for (TierCompressedCobblestoneSingularityBit tier : TierCompressedCobblestoneSingularityBit.values()) {
            tier.setItem(registerTierCompressedCobblestoneSingularityBit(tier.getRegistryName()));
        }
    }

    static {
        for (TierCompressedCobblestoneSingularityFragment tier : TierCompressedCobblestoneSingularityFragment.values()) {
            tier.setItem(registerTierCompressedCobblestoneSingularityFragment(tier.getRegistryName()));
        }
    }

    static {
        for (TierCompressedCobblestoneSingularity tier : TierCompressedCobblestoneSingularity.values()) {
            tier.setItem(registerTierCompressedCobblestoneSingularity(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneWire tier : TierCobblestoneWire.values()) {
            tier.setItem(registerTierCobblestoneWire(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneRod tier : TierCobblestoneRod.values()) {
            tier.setItem(registerTierCobblestoneRod(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneMotor tier : TierCobblestoneMotor.values()) {
            tier.setItem(registerTierCobblestoneMotor(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneCircuit tier : TierCobblestoneCircuit.values()) {
            tier.setItem(registerTierCobblestoneCircuit(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneProcessor tier : TierCobblestoneProcessor.values()) {
            tier.setItem(registerTierCobblestoneProcessor(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneCircuitPackage tier : TierCobblestoneCircuitPackage.values()) {
            tier.setItem(registerTierCobblestoneCircuitPackage(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneAccelerationChip tier : TierCobblestoneAccelerationChip.values()) {
            tier.setItem(registerTierCobblestoneAccelerationChip(tier.getRegistryName()));
        }
    }

    static {
        for (TierCobblestoneEnergizedCube tier : TierCobblestoneEnergizedCube.values()) {
            tier.setItem(registerTierCobblestoneEnergizedCube(tier.getRegistryName()));
        }
    }

    // ここから下は tier 違いの丸石パンです。
    // 既存コード側から参照しやすいように、各 tier の公開定数は残します。
    // 実際の元データは上の enum 側なので、将来 tier を増やすときはまず enum を編集します。
    public static final DeferredItem<Item> TIER_COPPER_COBBLESTONE_BREAD =
        TierCobblestoneBread.COPPER.getItem();
    public static final DeferredItem<Item> TIER_IRON_COBBLESTONE_BREAD =
        TierCobblestoneBread.IRON.getItem();
    public static final DeferredItem<Item> TIER_GOLD_COBBLESTONE_BREAD =
        TierCobblestoneBread.GOLD.getItem();
    public static final DeferredItem<Item> TIER_AMETHYST_COBBLESTONE_BREAD =
        TierCobblestoneBread.AMETHYST.getItem();
    public static final DeferredItem<Item> TIER_AQUAMARINE_COBBLESTONE_BREAD =
        TierCobblestoneBread.AQUAMARINE.getItem();
    public static final DeferredItem<Item> TIER_TOPAZ_COBBLESTONE_BREAD =
        TierCobblestoneBread.TOPAZ.getItem();
    public static final DeferredItem<Item> TIER_RUBY_COBBLESTONE_BREAD =
        TierCobblestoneBread.RUBY.getItem();
    public static final DeferredItem<Item> TIER_SAPPHIRE_COBBLESTONE_BREAD =
        TierCobblestoneBread.SAPPHIRE.getItem();
    public static final DeferredItem<Item> TIER_DIAMOND_COBBLESTONE_BREAD =
        TierCobblestoneBread.DIAMOND.getItem();
    public static final DeferredItem<Item> TIER_EMERALD_COBBLESTONE_BREAD =
        TierCobblestoneBread.EMERALD.getItem();
    public static final DeferredItem<Item> TIER_NETHERITE_COBBLESTONE_BREAD =
        TierCobblestoneBread.NETHERITE.getItem();
    public static final DeferredItem<Item> TIER_OBSIDIAN_COBBLESTONE_BREAD =
        TierCobblestoneBread.OBSIDIAN.getItem();

    public static final DeferredItem<Item> TIER_COPPER_COBBLESTONE_GEM =
        TierCobblestoneGem.COPPER.getItem();
    public static final DeferredItem<Item> TIER_IRON_COBBLESTONE_GEM =
        TierCobblestoneGem.IRON.getItem();
    public static final DeferredItem<Item> TIER_GOLD_COBBLESTONE_GEM =
        TierCobblestoneGem.GOLD.getItem();
    public static final DeferredItem<Item> TIER_AMETHYST_COBBLESTONE_GEM =
        TierCobblestoneGem.AMETHYST.getItem();
    public static final DeferredItem<Item> TIER_AQUAMARINE_COBBLESTONE_GEM =
        TierCobblestoneGem.AQUAMARINE.getItem();
    public static final DeferredItem<Item> TIER_TOPAZ_COBBLESTONE_GEM =
        TierCobblestoneGem.TOPAZ.getItem();
    public static final DeferredItem<Item> TIER_RUBY_COBBLESTONE_GEM =
        TierCobblestoneGem.RUBY.getItem();
    public static final DeferredItem<Item> TIER_SAPPHIRE_COBBLESTONE_GEM =
        TierCobblestoneGem.SAPPHIRE.getItem();
    public static final DeferredItem<Item> TIER_DIAMOND_COBBLESTONE_GEM =
        TierCobblestoneGem.DIAMOND.getItem();
    public static final DeferredItem<Item> TIER_EMERALD_COBBLESTONE_GEM =
        TierCobblestoneGem.EMERALD.getItem();
    public static final DeferredItem<Item> TIER_NETHERITE_COBBLESTONE_GEM =
        TierCobblestoneGem.NETHERITE.getItem();
    public static final DeferredItem<Item> TIER_OBSIDIAN_COBBLESTONE_GEM =
        TierCobblestoneGem.OBSIDIAN.getItem();

    public static final DeferredItem<Item> TIER_COPPER_COBBLESTONE_DUST =
        TierCobblestoneDust.COPPER.getItem();
    public static final DeferredItem<Item> TIER_IRON_COBBLESTONE_DUST =
        TierCobblestoneDust.IRON.getItem();
    public static final DeferredItem<Item> TIER_GOLD_COBBLESTONE_DUST =
        TierCobblestoneDust.GOLD.getItem();
    public static final DeferredItem<Item> TIER_AMETHYST_COBBLESTONE_DUST =
        TierCobblestoneDust.AMETHYST.getItem();
    public static final DeferredItem<Item> TIER_AQUAMARINE_COBBLESTONE_DUST =
        TierCobblestoneDust.AQUAMARINE.getItem();
    public static final DeferredItem<Item> TIER_TOPAZ_COBBLESTONE_DUST =
        TierCobblestoneDust.TOPAZ.getItem();
    public static final DeferredItem<Item> TIER_RUBY_COBBLESTONE_DUST =
        TierCobblestoneDust.RUBY.getItem();
    public static final DeferredItem<Item> TIER_SAPPHIRE_COBBLESTONE_DUST =
        TierCobblestoneDust.SAPPHIRE.getItem();
    public static final DeferredItem<Item> TIER_DIAMOND_COBBLESTONE_DUST =
        TierCobblestoneDust.DIAMOND.getItem();
    public static final DeferredItem<Item> TIER_EMERALD_COBBLESTONE_DUST =
        TierCobblestoneDust.EMERALD.getItem();
    public static final DeferredItem<Item> TIER_NETHERITE_COBBLESTONE_DUST =
        TierCobblestoneDust.NETHERITE.getItem();
    public static final DeferredItem<Item> TIER_OBSIDIAN_COBBLESTONE_DUST =
        TierCobblestoneDust.OBSIDIAN.getItem();

    public static final DeferredItem<Item> TIER_COPPER_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.COPPER.getItem();
    public static final DeferredItem<Item> TIER_IRON_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.IRON.getItem();
    public static final DeferredItem<Item> TIER_GOLD_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.GOLD.getItem();
    public static final DeferredItem<Item> TIER_AMETHYST_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.AMETHYST.getItem();
    public static final DeferredItem<Item> TIER_AQUAMARINE_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.AQUAMARINE.getItem();
    public static final DeferredItem<Item> TIER_TOPAZ_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.TOPAZ.getItem();
    public static final DeferredItem<Item> TIER_RUBY_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.RUBY.getItem();
    public static final DeferredItem<Item> TIER_SAPPHIRE_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.SAPPHIRE.getItem();
    public static final DeferredItem<Item> TIER_DIAMOND_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.DIAMOND.getItem();
    public static final DeferredItem<Item> TIER_EMERALD_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.EMERALD.getItem();
    public static final DeferredItem<Item> TIER_NETHERITE_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.NETHERITE.getItem();
    public static final DeferredItem<Item> TIER_OBSIDIAN_COBBLESTONE_DIRTY_DUST =
        TierCobblestoneDirtyDust.OBSIDIAN.getItem();

    public static final DeferredItem<Item> TIER_COPPER_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.COPPER.getItem();
    public static final DeferredItem<Item> TIER_IRON_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.IRON.getItem();
    public static final DeferredItem<Item> TIER_GOLD_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.GOLD.getItem();
    public static final DeferredItem<Item> TIER_AMETHYST_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.AMETHYST.getItem();
    public static final DeferredItem<Item> TIER_AQUAMARINE_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.AQUAMARINE.getItem();
    public static final DeferredItem<Item> TIER_TOPAZ_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.TOPAZ.getItem();
    public static final DeferredItem<Item> TIER_RUBY_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.RUBY.getItem();
    public static final DeferredItem<Item> TIER_SAPPHIRE_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.SAPPHIRE.getItem();
    public static final DeferredItem<Item> TIER_DIAMOND_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.DIAMOND.getItem();
    public static final DeferredItem<Item> TIER_EMERALD_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.EMERALD.getItem();
    public static final DeferredItem<Item> TIER_NETHERITE_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.NETHERITE.getItem();
    public static final DeferredItem<Item> TIER_OBSIDIAN_COBBLESTONE_MIXED_DUST =
        TierCobblestoneMixedDust.OBSIDIAN.getItem();

    public static final DeferredItem<Item> TIER_COPPER_COBBLESTONE_ROD =
        TierCobblestoneRod.COPPER.getItem();
    public static final DeferredItem<Item> TIER_IRON_COBBLESTONE_ROD =
        TierCobblestoneRod.IRON.getItem();
    public static final DeferredItem<Item> TIER_GOLD_COBBLESTONE_ROD =
        TierCobblestoneRod.GOLD.getItem();
    public static final DeferredItem<Item> TIER_AMETHYST_COBBLESTONE_ROD =
        TierCobblestoneRod.AMETHYST.getItem();
    public static final DeferredItem<Item> TIER_AQUAMARINE_COBBLESTONE_ROD =
        TierCobblestoneRod.AQUAMARINE.getItem();
    public static final DeferredItem<Item> TIER_TOPAZ_COBBLESTONE_ROD =
        TierCobblestoneRod.TOPAZ.getItem();
    public static final DeferredItem<Item> TIER_RUBY_COBBLESTONE_ROD =
        TierCobblestoneRod.RUBY.getItem();
    public static final DeferredItem<Item> TIER_SAPPHIRE_COBBLESTONE_ROD =
        TierCobblestoneRod.SAPPHIRE.getItem();
    public static final DeferredItem<Item> TIER_DIAMOND_COBBLESTONE_ROD =
        TierCobblestoneRod.DIAMOND.getItem();
    public static final DeferredItem<Item> TIER_EMERALD_COBBLESTONE_ROD =
        TierCobblestoneRod.EMERALD.getItem();
    public static final DeferredItem<Item> TIER_NETHERITE_COBBLESTONE_ROD =
        TierCobblestoneRod.NETHERITE.getItem();
    public static final DeferredItem<Item> TIER_OBSIDIAN_COBBLESTONE_ROD =
        TierCobblestoneRod.OBSIDIAN.getItem();

    public static final DeferredItem<Item> TIER_COPPER_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.COPPER.getItem();
    public static final DeferredItem<Item> TIER_IRON_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.IRON.getItem();
    public static final DeferredItem<Item> TIER_GOLD_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.GOLD.getItem();
    public static final DeferredItem<Item> TIER_AMETHYST_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.AMETHYST.getItem();
    public static final DeferredItem<Item> TIER_AQUAMARINE_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.AQUAMARINE.getItem();
    public static final DeferredItem<Item> TIER_TOPAZ_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.TOPAZ.getItem();
    public static final DeferredItem<Item> TIER_RUBY_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.RUBY.getItem();
    public static final DeferredItem<Item> TIER_SAPPHIRE_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.SAPPHIRE.getItem();
    public static final DeferredItem<Item> TIER_DIAMOND_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.DIAMOND.getItem();
    public static final DeferredItem<Item> TIER_EMERALD_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.EMERALD.getItem();
    public static final DeferredItem<Item> TIER_NETHERITE_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.NETHERITE.getItem();
    public static final DeferredItem<Item> TIER_OBSIDIAN_COBBLESTONE_MOTOR =
        TierCobblestoneMotor.OBSIDIAN.getItem();

    public static final DeferredItem<BlockItem> COMPRESSED_COBBLESTONE_ITEM =
        registerCompressedCobblestoneBlockItem("compressed_cobblestone", ModBlocks.COMPRESSED_COBBLESTONE);

    public static final DeferredItem<BlockItem> COMPRESSED_STONE_ITEM =
        registerCompressedCobblestoneBlockItem("compressed_stone", ModBlocks.COMPRESSED_STONE);

    public static final DeferredItem<BlockItem> COBBLESTONE_MACHINE_CASING_ITEM =
        registerCompressedCobblestoneBlockItem("cobblestone_machine_casing", ModBlocks.COBBLESTONE_MACHINE_CASING);

    public static final DeferredItem<BlockItem> COBBLESTONE_TANK_ITEM =
        registerCompressedCobblestoneBlockItem("cobblestone_tank", ModBlocks.COBBLESTONE_TANK);

    public enum TierCompressedCobblestoneItem {
        COPPER(ModBlocks.TierCompressedCobblestone.COPPER),
        IRON(ModBlocks.TierCompressedCobblestone.IRON),
        GOLD(ModBlocks.TierCompressedCobblestone.GOLD),
        AMETHYST(ModBlocks.TierCompressedCobblestone.AMETHYST),
        AQUAMARINE(ModBlocks.TierCompressedCobblestone.AQUAMARINE),
        TOPAZ(ModBlocks.TierCompressedCobblestone.TOPAZ),
        RUBY(ModBlocks.TierCompressedCobblestone.RUBY),
        SAPPHIRE(ModBlocks.TierCompressedCobblestone.SAPPHIRE),
        DIAMOND(ModBlocks.TierCompressedCobblestone.DIAMOND),
        EMERALD(ModBlocks.TierCompressedCobblestone.EMERALD),
        NETHERITE(ModBlocks.TierCompressedCobblestone.NETHERITE),
        OBSIDIAN(ModBlocks.TierCompressedCobblestone.OBSIDIAN);

        private final ModBlocks.TierCompressedCobblestone blockTier;
        private DeferredItem<BlockItem> item;

        TierCompressedCobblestoneItem(ModBlocks.TierCompressedCobblestone blockTier) {
            this.blockTier = blockTier;
        }

        public DeferredItem<BlockItem> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<BlockItem> item) {
            this.item = item;
        }

        public ModBlocks.TierCompressedCobblestone getBlockTier() {
            return this.blockTier;
        }
    }

    // generator は block 側の 36 バリエーションと同じ並びをそのまま使います。
    // block enum と名前をそろえておくと、recipe や creative tab から valueOf(...) で対応を取りやすくなります。
    public enum TierCobblestoneGeneratorItem {
        S(ModBlocks.TierCobblestoneGenerator.S),
        M(ModBlocks.TierCobblestoneGenerator.M),
        L(ModBlocks.TierCobblestoneGenerator.L),
        COPPER_S(ModBlocks.TierCobblestoneGenerator.COPPER_S),
        COPPER_M(ModBlocks.TierCobblestoneGenerator.COPPER_M),
        COPPER_L(ModBlocks.TierCobblestoneGenerator.COPPER_L),
        IRON_S(ModBlocks.TierCobblestoneGenerator.IRON_S),
        IRON_M(ModBlocks.TierCobblestoneGenerator.IRON_M),
        IRON_L(ModBlocks.TierCobblestoneGenerator.IRON_L),
        GOLD_S(ModBlocks.TierCobblestoneGenerator.GOLD_S),
        GOLD_M(ModBlocks.TierCobblestoneGenerator.GOLD_M),
        GOLD_L(ModBlocks.TierCobblestoneGenerator.GOLD_L),
        AMETHYST_S(ModBlocks.TierCobblestoneGenerator.AMETHYST_S),
        AMETHYST_M(ModBlocks.TierCobblestoneGenerator.AMETHYST_M),
        AMETHYST_L(ModBlocks.TierCobblestoneGenerator.AMETHYST_L),
        AQUAMARINE_S(ModBlocks.TierCobblestoneGenerator.AQUAMARINE_S),
        AQUAMARINE_M(ModBlocks.TierCobblestoneGenerator.AQUAMARINE_M),
        AQUAMARINE_L(ModBlocks.TierCobblestoneGenerator.AQUAMARINE_L),
        TOPAZ_S(ModBlocks.TierCobblestoneGenerator.TOPAZ_S),
        TOPAZ_M(ModBlocks.TierCobblestoneGenerator.TOPAZ_M),
        TOPAZ_L(ModBlocks.TierCobblestoneGenerator.TOPAZ_L),
        RUBY_S(ModBlocks.TierCobblestoneGenerator.RUBY_S),
        RUBY_M(ModBlocks.TierCobblestoneGenerator.RUBY_M),
        RUBY_L(ModBlocks.TierCobblestoneGenerator.RUBY_L),
        SAPPHIRE_S(ModBlocks.TierCobblestoneGenerator.SAPPHIRE_S),
        SAPPHIRE_M(ModBlocks.TierCobblestoneGenerator.SAPPHIRE_M),
        SAPPHIRE_L(ModBlocks.TierCobblestoneGenerator.SAPPHIRE_L),
        DIAMOND_S(ModBlocks.TierCobblestoneGenerator.DIAMOND_S),
        DIAMOND_M(ModBlocks.TierCobblestoneGenerator.DIAMOND_M),
        DIAMOND_L(ModBlocks.TierCobblestoneGenerator.DIAMOND_L),
        EMERALD_S(ModBlocks.TierCobblestoneGenerator.EMERALD_S),
        EMERALD_M(ModBlocks.TierCobblestoneGenerator.EMERALD_M),
        EMERALD_L(ModBlocks.TierCobblestoneGenerator.EMERALD_L),
        NETHERITE_S(ModBlocks.TierCobblestoneGenerator.NETHERITE_S),
        NETHERITE_M(ModBlocks.TierCobblestoneGenerator.NETHERITE_M),
        NETHERITE_L(ModBlocks.TierCobblestoneGenerator.NETHERITE_L),
        OBSIDIAN_S(ModBlocks.TierCobblestoneGenerator.OBSIDIAN_S),
        OBSIDIAN_M(ModBlocks.TierCobblestoneGenerator.OBSIDIAN_M),
        OBSIDIAN_L(ModBlocks.TierCobblestoneGenerator.OBSIDIAN_L);

        private final ModBlocks.TierCobblestoneGenerator blockVariant;
        private DeferredItem<BlockItem> item;

        TierCobblestoneGeneratorItem(ModBlocks.TierCobblestoneGenerator blockVariant) {
            this.blockVariant = blockVariant;
        }

        public ModBlocks.TierCobblestoneGenerator getBlockVariant() {
            return this.blockVariant;
        }

        public DeferredItem<BlockItem> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<BlockItem> item) {
            this.item = item;
        }

        public static TierCobblestoneGeneratorItem from(ModBlocks.TierCobblestoneGenerator blockVariant) {
            return valueOf(blockVariant.name());
        }
    }

    public enum TierCompressedStoneItem {
        COPPER(ModBlocks.TierCompressedStone.COPPER),
        IRON(ModBlocks.TierCompressedStone.IRON),
        GOLD(ModBlocks.TierCompressedStone.GOLD),
        AMETHYST(ModBlocks.TierCompressedStone.AMETHYST),
        AQUAMARINE(ModBlocks.TierCompressedStone.AQUAMARINE),
        TOPAZ(ModBlocks.TierCompressedStone.TOPAZ),
        RUBY(ModBlocks.TierCompressedStone.RUBY),
        SAPPHIRE(ModBlocks.TierCompressedStone.SAPPHIRE),
        DIAMOND(ModBlocks.TierCompressedStone.DIAMOND),
        EMERALD(ModBlocks.TierCompressedStone.EMERALD),
        NETHERITE(ModBlocks.TierCompressedStone.NETHERITE),
        OBSIDIAN(ModBlocks.TierCompressedStone.OBSIDIAN);

        private final ModBlocks.TierCompressedStone blockTier;
        private DeferredItem<BlockItem> item;

        TierCompressedStoneItem(ModBlocks.TierCompressedStone blockTier) {
            this.blockTier = blockTier;
        }

        public DeferredItem<BlockItem> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<BlockItem> item) {
            this.item = item;
        }

        public ModBlocks.TierCompressedStone getBlockTier() {
            return this.blockTier;
        }
    }

    public enum TierCobblestoneMachineCasingItem {
        COPPER(ModBlocks.TierCobblestoneMachineCasing.COPPER),
        IRON(ModBlocks.TierCobblestoneMachineCasing.IRON),
        GOLD(ModBlocks.TierCobblestoneMachineCasing.GOLD),
        AMETHYST(ModBlocks.TierCobblestoneMachineCasing.AMETHYST),
        AQUAMARINE(ModBlocks.TierCobblestoneMachineCasing.AQUAMARINE),
        TOPAZ(ModBlocks.TierCobblestoneMachineCasing.TOPAZ),
        RUBY(ModBlocks.TierCobblestoneMachineCasing.RUBY),
        SAPPHIRE(ModBlocks.TierCobblestoneMachineCasing.SAPPHIRE),
        DIAMOND(ModBlocks.TierCobblestoneMachineCasing.DIAMOND),
        EMERALD(ModBlocks.TierCobblestoneMachineCasing.EMERALD),
        NETHERITE(ModBlocks.TierCobblestoneMachineCasing.NETHERITE),
        OBSIDIAN(ModBlocks.TierCobblestoneMachineCasing.OBSIDIAN);

        private final ModBlocks.TierCobblestoneMachineCasing blockTier;
        private DeferredItem<BlockItem> item;

        TierCobblestoneMachineCasingItem(ModBlocks.TierCobblestoneMachineCasing blockTier) {
            this.blockTier = blockTier;
        }

        public DeferredItem<BlockItem> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<BlockItem> item) {
            this.item = item;
        }

        public ModBlocks.TierCobblestoneMachineCasing getBlockTier() {
            return this.blockTier;
        }
    }

    public enum TierCobblestoneTankItem {
        COPPER(ModBlocks.TierCobblestoneTank.COPPER),
        IRON(ModBlocks.TierCobblestoneTank.IRON),
        GOLD(ModBlocks.TierCobblestoneTank.GOLD),
        AMETHYST(ModBlocks.TierCobblestoneTank.AMETHYST),
        AQUAMARINE(ModBlocks.TierCobblestoneTank.AQUAMARINE),
        TOPAZ(ModBlocks.TierCobblestoneTank.TOPAZ),
        RUBY(ModBlocks.TierCobblestoneTank.RUBY),
        SAPPHIRE(ModBlocks.TierCobblestoneTank.SAPPHIRE),
        DIAMOND(ModBlocks.TierCobblestoneTank.DIAMOND),
        EMERALD(ModBlocks.TierCobblestoneTank.EMERALD),
        NETHERITE(ModBlocks.TierCobblestoneTank.NETHERITE),
        OBSIDIAN(ModBlocks.TierCobblestoneTank.OBSIDIAN);

        private final ModBlocks.TierCobblestoneTank blockTier;
        private DeferredItem<BlockItem> item;

        TierCobblestoneTankItem(ModBlocks.TierCobblestoneTank blockTier) {
            this.blockTier = blockTier;
        }

        public DeferredItem<BlockItem> getItem() {
            return this.item;
        }

        private void setItem(DeferredItem<BlockItem> item) {
            this.item = item;
        }

        public ModBlocks.TierCobblestoneTank getBlockTier() {
            return this.blockTier;
        }
    }

    static {
        for (TierCompressedCobblestoneItem tier : TierCompressedCobblestoneItem.values()) {
            tier.setItem(registerCompressedCobblestoneBlockItem(
                tier.getBlockTier().getRegistryName(),
                tier.getBlockTier().getBlock()
            ));
        }
    }

    static {
        for (TierCompressedStoneItem tier : TierCompressedStoneItem.values()) {
            tier.setItem(registerCompressedCobblestoneBlockItem(
                tier.getBlockTier().getRegistryName(),
                tier.getBlockTier().getBlock()
            ));
        }
    }

    static {
        for (TierCobblestoneMachineCasingItem tier : TierCobblestoneMachineCasingItem.values()) {
            tier.setItem(registerCompressedCobblestoneBlockItem(
                tier.getBlockTier().getRegistryName(),
                tier.getBlockTier().getBlock()
            ));
        }
    }

    static {
        for (TierCobblestoneTankItem tier : TierCobblestoneTankItem.values()) {
            tier.setItem(registerCompressedCobblestoneBlockItem(
                tier.getBlockTier().getRegistryName(),
                tier.getBlockTier().getBlock()
            ));
        }
    }

    static {
        for (TierCobblestoneGeneratorItem generatorItem : TierCobblestoneGeneratorItem.values()) {
            generatorItem.setItem(registerCompressedCobblestoneBlockItem(
                generatorItem.getBlockVariant().getRegistryName(),
                generatorItem.getBlockVariant().getBlock()
            ));
        }
    }

    // cobblestone_furnace ブロックアイテムとして登録するためのDeferredItemも追加します。    
    public static final DeferredItem<BlockItem> COBBLESTONE_FURNACE_ITEM =
        ModItems.ITEMS.registerSimpleBlockItem(
            "cobblestone_furnace", 
            ModBlocks.COBBLESTONE_FURNACE);

    public static final DeferredItem<BlockItem> COBBLESTONE_POWERED_FURNACE_ITEM =
        ModItems.ITEMS.registerSimpleBlockItem(
            "cobblestone_powered_furnace",
            ModBlocks.COBBLESTONE_POWERED_FURNACE);

    // cobblestone_crusher も設置できるように BlockItem を登録します。
    public static final DeferredItem<BlockItem> COBBLESTONE_CRUSHER_ITEM =
        ModItems.ITEMS.registerSimpleBlockItem(
            "cobblestone_crusher",
            ModBlocks.COBBLESTONE_CRUSHER);

    public static final DeferredItem<BlockItem> COBBLESTONE_FE_GENERATOR_ITEM =
        ModItems.ITEMS.registerSimpleBlockItem(
            "cobblestone_fe_generator",
            ModBlocks.COBBLESTONE_FE_GENERATOR);

    // cobblestone_mixer も設置できるように BlockItem を登録します。
    public static final DeferredItem<BlockItem> COBBLESTONE_MIXER_ITEM =
        ModItems.ITEMS.registerSimpleBlockItem(
            "cobblestone_mixer",
            ModBlocks.COBBLESTONE_MIXER);

    public static final DeferredItem<BlockItem> COBBLESTONE_MELTER_ITEM =
        ModItems.ITEMS.registerSimpleBlockItem(
            "cobblestone_melter",
            ModBlocks.COBBLESTONE_MELTER);

    public static final DeferredItem<BlockItem> COBBLESTONE_REACTION_CHAMBER_ITEM =
        ModItems.ITEMS.registerSimpleBlockItem(
            "cobblestone_reaction_chamber",
            ModBlocks.COBBLESTONE_REACTION_CHAMBER);

    public static final DeferredItem<BlockItem> COBBLESTONE_DISSOLUTION_CHAMBER_ITEM =
        ModItems.ITEMS.registerSimpleBlockItem(
            "cobblestone_dissolution_chamber",
            ModBlocks.COBBLESTONE_DISSOLUTION_CHAMBER);

    // cobblestone_centrifuge も設置できるように BlockItem を登録します。
    public static final DeferredItem<BlockItem> COBBLESTONE_CENTRIFUGE_ITEM =
        ModItems.ITEMS.registerSimpleBlockItem(
            "cobblestone_centrifuge",
            ModBlocks.COBBLESTONE_CENTRIFUGE);

    public static final DeferredItem<BlockItem> COBBLESTONE_LASER_DRILL_ITEM =
        ModItems.ITEMS.registerSimpleBlockItem(
            "cobblestone_laser_drill",
            ModBlocks.COBBLESTONE_LASER_DRILL);
}