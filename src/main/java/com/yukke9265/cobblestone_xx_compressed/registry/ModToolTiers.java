package com.yukke9265.cobblestone_xx_compressed.registry;

import java.util.function.Supplier;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;

/**
 * ツール性能の定義置き場です。
 * 耐久・採掘速度・採掘レベルを変えたいときは、基本的にここだけを直します。
 */
public final class ModToolTiers {
    private ModToolTiers() {
    }

    /**
     * 1.21 では採掘レベルは数値ではなく、
     * 「このツールではドロップしないブロック」タグの切り替えで表現します。
     * STONE / IRON / DIAMOND / NETHERITE の 4 段階を、調整しやすい名前で持たせます。
     */
    public enum MiningLevel {
        STONE(BlockTags.INCORRECT_FOR_STONE_TOOL),
        IRON(BlockTags.INCORRECT_FOR_IRON_TOOL),
        DIAMOND(BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
        NETHERITE(BlockTags.INCORRECT_FOR_NETHERITE_TOOL);

        private final TagKey<Block> incorrectBlocksForDrops;

        MiningLevel(TagKey<Block> incorrectBlocksForDrops) {
            this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        }

        public TagKey<Block> getIncorrectBlocksForDrops() {
            return this.incorrectBlocksForDrops;
        }
    }

    /**
     * 丸石ピッケル用の素材性能です。
     * 引数の意味と、バニラ素材の参考値:
     * 1. 耐久 … 木59 / 石131 / 鉄250 / 金32 / ダイヤ1561 / ネザライト2031
     * 2. 採掘速度 … 木2 / 石4 / 鉄6 / 金12 / ダイヤ8 / ネザライト9
     * 3. 採掘レベル … STONE / IRON / DIAMOND / NETHERITE（石・鉄・ダイヤ・ネザライト相当）
     * 4. 攻撃力ボーナス … 木0 / 石1 / 鉄2 / 金0 / ダイヤ3 / ネザライト4
     * 5. エンチャント適性 … 木15 / 石5 / 鉄14 / 金22 / ダイヤ10 / ネザライト15
     * 6. 修理素材 … そのツールを金床で直すときに使うアイテム
     *
     * 初期値は仮置きなので、プレイ感を見ながらここだけ変えれば十分です。
     */
    public enum CobblestonePickaxeMaterial {
        BASE(
            200,
            4.0f,
            MiningLevel.STONE,
            4.0f,
            15,
            () -> Ingredient.of(ModItems.COBBLESTONE_GEM.get())
        ),
        COPPER(
            400,
            5.0f,
            MiningLevel.STONE,
            5.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.COPPER.getItem().get())
        ),
        IRON(
            800,
            6.0f,
            MiningLevel.IRON,
            6.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.IRON.getItem().get())
        ),
        GOLD(
            1200,
            7.0f,
            MiningLevel.IRON,
            7.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.GOLD.getItem().get())
        ),
        AMETHYST(
            1600,
            8.0f,
            MiningLevel.DIAMOND,
            8.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.AMETHYST.getItem().get())
        ),
        AQUAMARINE(
            2000,
            9.0f,
            MiningLevel.DIAMOND,
            9.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.AQUAMARINE.getItem().get())
        ),
        TOPAZ(
            2400,
            10.0f,
            MiningLevel.DIAMOND,
            10.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.TOPAZ.getItem().get())
        ),
        RUBY(
            2800,
            11.0f,
            MiningLevel.NETHERITE,
            11.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.RUBY.getItem().get())
        ),
        SAPPHIRE(
            3200,
            12.0f,
            MiningLevel.NETHERITE,
            12.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.SAPPHIRE.getItem().get())
        ),
        DIAMOND(
            3600,
            13.0f,
            MiningLevel.NETHERITE,
            13.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.DIAMOND.getItem().get())
        ),
        EMERALD(
            4000,
            14.0f,
            MiningLevel.NETHERITE,
            14.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.EMERALD.getItem().get())
        ),
        NETHERITE(
            4400,
            15.0f,
            MiningLevel.NETHERITE,
            15.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.NETHERITE.getItem().get())
        ),
        OBSIDIAN(
            4800,
            16.0f,
            MiningLevel.NETHERITE,
            16.0f,
            15,
            () -> Ingredient.of(ModItems.TierCobblestoneGem.OBSIDIAN.getItem().get())
        );

        private final Tier tier;

        CobblestonePickaxeMaterial(
            int uses,
            float speed,
            MiningLevel miningLevel,
            float attackDamageBonus,
            int enchantmentValue,
            Supplier<Ingredient> repairIngredient
        ) {
            // SimpleTier に渡すと、PickaxeItem 側が耐久や採掘判定を自動で読んでくれます。
            this.tier = new SimpleTier(
                miningLevel.getIncorrectBlocksForDrops(),
                uses,
                speed,
                attackDamageBonus,
                enchantmentValue,
                repairIngredient
            );
        }

        public Tier getTier() {
            return this.tier;
        }

        /**
         * Stone Break Simulator で耐久エンチャント相当として加算するボーナスです。
         * BASE から +1, +2, +3 ... と段階的に増えます。
         */
        public int getStoneBreakSimulatorUnbreakingBonus() {
            return this.ordinal() + 1;
        }
    }
}
