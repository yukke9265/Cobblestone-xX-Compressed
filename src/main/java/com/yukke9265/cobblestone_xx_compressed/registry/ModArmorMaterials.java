package com.yukke9265.cobblestone_xx_compressed.registry;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 防具性能の定義置き場です。
 * 防御力・エンチャント適性を変えたいときは、基本的にここだけを直します。
 * Item 耐久は持たせず、ModItems 側で不可壊として登録します。
 */
public final class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
        DeferredRegister.create(Registries.ARMOR_MATERIAL, CobblestonexXCompressed.MODID);

    private ModArmorMaterials() {
    }

    /**
     * 圧縮丸石防具用の素材性能です。
     *
     * BASE から SAPPHIRE まで:
     * - 下の「ベース値」「上限値」を ordinal 比例で補間し、バニラ防具として機能します。
     *
     * DIAMOND 以降:
     * - バニラ防具値は 0 に固定します。
     * - 被ダメ軽減は {@code CompressedCobblestoneArmorProtectionRules} 側の独自ロジックが担当します。
     */
    public enum CobblestoneArmorMaterial {
        BASE(
            "compressed_cobblestone",
            () -> Ingredient.of(ModItems.COBBLESTONE_GEM.get())
        ),
        COPPER(
            "tier_copper_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.COPPER.getItem().get())
        ),
        IRON(
            "tier_iron_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.IRON.getItem().get())
        ),
        GOLD(
            "tier_gold_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.GOLD.getItem().get())
        ),
        AMETHYST(
            "tier_amethyst_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.AMETHYST.getItem().get())
        ),
        AQUAMARINE(
            "tier_aquamarine_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.AQUAMARINE.getItem().get())
        ),
        TOPAZ(
            "tier_topaz_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.TOPAZ.getItem().get())
        ),
        RUBY(
            "tier_ruby_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.RUBY.getItem().get())
        ),
        SAPPHIRE(
            "tier_sapphire_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.SAPPHIRE.getItem().get())
        ),
        DIAMOND(
            "tier_diamond_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.DIAMOND.getItem().get())
        ),
        EMERALD(
            "tier_emerald_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.EMERALD.getItem().get())
        ),
        NETHERITE(
            "tier_netherite_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.NETHERITE.getItem().get())
        ),
        OBSIDIAN(
            "tier_obsidian_compressed_cobblestone",
            () -> Ingredient.of(ModItems.TierCobblestoneGem.OBSIDIAN.getItem().get())
        );

        /** バニラ防具カーブの下限 tier（BASE）の ordinal です。 */
        private static final int VANILLA_PROGRESS_BASE_ORDINAL = 0;
        /** バニラ防具カーブの上限 tier（SAPPHIRE）の ordinal です。 */
        private static final int VANILLA_PROGRESS_CAP_ORDINAL = 8;
        /** 独自防御 tier の開始 ordinal（DIAMOND）です。 */
        private static final int ADVANCED_TIER_START_ORDINAL = 9;
        /** 独自防御 tier の上限 ordinal（OBSIDIAN）です。 */
        private static final int ADVANCED_TIER_CAP_ORDINAL = 12;

        // === バニラ防具カーブ: ベース値（鉄相当） ===
        private static final int BASE_HELMET_DEFENSE = 2;
        private static final int BASE_CHESTPLATE_DEFENSE = 6;
        private static final int BASE_LEGGINGS_DEFENSE = 5;
        private static final int BASE_BOOTS_DEFENSE = 2;
        private static final int BASE_ENCHANTMENT_VALUE = 9;
        private static final float BASE_TOUGHNESS = 0.0f;
        private static final float BASE_KNOCKBACK_RESISTANCE = 0.0f;

        // === バニラ防具カーブ: 上限値（SAPPHIRE） ===
        private static final int CAP_HELMET_DEFENSE = 3;
        private static final int CAP_CHESTPLATE_DEFENSE = 9;
        private static final int CAP_LEGGINGS_DEFENSE = 7;
        private static final int CAP_BOOTS_DEFENSE = 3;
        private static final int CAP_ENCHANTMENT_VALUE = 16;
        private static final float CAP_TOUGHNESS = 3.5f;
        private static final float CAP_KNOCKBACK_RESISTANCE = 0.12f;

        // === 独自防御 tier 用エンチャ適性 ===
        private static final int ADVANCED_ENCHANTMENT_VALUE_BASE = 16;
        private static final int ADVANCED_ENCHANTMENT_VALUE_CAP = 20;

        private final String registryName;
        private final Supplier<Ingredient> repairIngredient;
        private final int helmetDefense;
        private final int chestplateDefense;
        private final int leggingsDefense;
        private final int bootsDefense;
        private final int enchantmentValue;
        private final float toughness;
        private final float knockbackResistance;
        private DeferredHolder<ArmorMaterial, ArmorMaterial> holder;

        CobblestoneArmorMaterial(String registryName, Supplier<Ingredient> repairIngredient) {
            this.registryName = registryName;
            this.repairIngredient = repairIngredient;

            if (this.usesCustomProtection()) {
                float advancedProgress = computeAdvancedProgressStep(this.ordinal());
                this.helmetDefense = 0;
                this.chestplateDefense = 0;
                this.leggingsDefense = 0;
                this.bootsDefense = 0;
                this.enchantmentValue = lerpInt(ADVANCED_ENCHANTMENT_VALUE_BASE, ADVANCED_ENCHANTMENT_VALUE_CAP, advancedProgress);
                this.toughness = 0.0f;
                this.knockbackResistance = 0.0f;
            } else {
                float vanillaProgress = computeVanillaProgressStep(this.ordinal());
                this.helmetDefense = lerpInt(BASE_HELMET_DEFENSE, CAP_HELMET_DEFENSE, vanillaProgress);
                this.chestplateDefense = lerpInt(BASE_CHESTPLATE_DEFENSE, CAP_CHESTPLATE_DEFENSE, vanillaProgress);
                this.leggingsDefense = lerpInt(BASE_LEGGINGS_DEFENSE, CAP_LEGGINGS_DEFENSE, vanillaProgress);
                this.bootsDefense = lerpInt(BASE_BOOTS_DEFENSE, CAP_BOOTS_DEFENSE, vanillaProgress);
                this.enchantmentValue = lerpInt(BASE_ENCHANTMENT_VALUE, CAP_ENCHANTMENT_VALUE, vanillaProgress);
                this.toughness = lerpFloat(BASE_TOUGHNESS, CAP_TOUGHNESS, vanillaProgress);
                this.knockbackResistance = lerpFloat(BASE_KNOCKBACK_RESISTANCE, CAP_KNOCKBACK_RESISTANCE, vanillaProgress);
            }
        }

        /**
         * DIAMOND 以降はバニラ防具値を使わず、イベント側で軽減します。
         */
        public boolean usesCustomProtection() {
            return this.ordinal() >= ADVANCED_TIER_START_ORDINAL;
        }

        /**
         * DIAMOND=0.0、OBSIDIAN=1.0 の独自防御 tier 進行度です。
         */
        public float getAdvancedProtectionProgress() {
            return computeAdvancedProgressStep(this.ordinal());
        }

        private static float computeVanillaProgressStep(int materialOrdinal) {
            if (materialOrdinal >= VANILLA_PROGRESS_CAP_ORDINAL) {
                return 1.0f;
            }

            if (materialOrdinal <= VANILLA_PROGRESS_BASE_ORDINAL) {
                return 0.0f;
            }

            return (materialOrdinal - VANILLA_PROGRESS_BASE_ORDINAL)
                / (float) (VANILLA_PROGRESS_CAP_ORDINAL - VANILLA_PROGRESS_BASE_ORDINAL);
        }

        private static float computeAdvancedProgressStep(int materialOrdinal) {
            if (materialOrdinal <= ADVANCED_TIER_START_ORDINAL) {
                return 0.0f;
            }

            if (materialOrdinal >= ADVANCED_TIER_CAP_ORDINAL) {
                return 1.0f;
            }

            return (materialOrdinal - ADVANCED_TIER_START_ORDINAL)
                / (float) (ADVANCED_TIER_CAP_ORDINAL - ADVANCED_TIER_START_ORDINAL);
        }

        private static int lerpInt(int baseValue, int capValue, float progress) {
            return Math.round(baseValue + (capValue - baseValue) * progress);
        }

        private static float lerpFloat(float baseValue, float capValue, float progress) {
            return baseValue + (capValue - baseValue) * progress;
        }

        private void register() {
            this.holder = ARMOR_MATERIALS.register(this.registryName, this::createMaterial);
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public Holder<ArmorMaterial> getHolder() {
            return this.holder;
        }

        public int getEnchantmentValue() {
            return this.enchantmentValue;
        }

        public float getToughness() {
            return this.toughness;
        }

        public float getKnockbackResistance() {
            return this.knockbackResistance;
        }

        public int getDefense(ArmorItem.Type armorType) {
            return switch (armorType) {
                case HELMET -> this.helmetDefense;
                case CHESTPLATE, BODY -> this.chestplateDefense;
                case LEGGINGS -> this.leggingsDefense;
                case BOOTS -> this.bootsDefense;
            };
        }

        private ArmorMaterial createMaterial(ResourceLocation id) {
            Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
            for (ArmorItem.Type type : ArmorItem.Type.values()) {
                int value = this.getDefense(type);
                if (value > 0) {
                    defense.put(type, value);
                }
            }

            return new ArmorMaterial(
                defense,
                this.enchantmentValue,
                SoundEvents.ARMOR_EQUIP_IRON,
                this.repairIngredient,
                List.of(new ArmorMaterial.Layer(id)),
                this.toughness,
                this.knockbackResistance
            );
        }
    }

    static {
        for (CobblestoneArmorMaterial material : CobblestoneArmorMaterial.values()) {
            material.register();
        }
    }
}
