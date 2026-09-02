package com.yukke9265.cobblestone_xx_compressed.registry;

import com.yukke9265.cobblestone_xx_compressed.registry.ModItems.TierCobblestoneBread;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

/**
 * 丸石パン系の食べ物設定をまとめたクラスです。
 * tier が上がるほど効果の種類とレベルが強くなる想定で数値を持ちます。
 */
public final class CobblestoneBreadFoodProperties {
    private static final int STACK_SIZE = 64;
    private static final float EFFECT_CHANCE = 1.0f;
    private static final int TICKS_PER_SECOND = 20;

    private CobblestoneBreadFoodProperties() {
    }

    public static Item.Properties createBase() {
        FoodProperties.Builder builder = breadBuilder(4, 0.3f);
        // 通常版は酔いの代償つきで、中盤 tier までの耐性 II を基準にします。
        addEffect(builder, MobEffects.CONFUSION, 10, 1);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 60, 1);
        return itemProperties(builder);
    }

    public static Item.Properties createTier(TierCobblestoneBread tier) {
        return switch (tier) {
            case COPPER -> createCopper();
            case IRON -> createIron();
            case GOLD -> createGold();
            case AMETHYST -> createAmethyst();
            case AQUAMARINE -> createAquamarine();
            case TOPAZ -> createTopaz();
            case RUBY -> createRuby();
            case SAPPHIRE -> createSapphire();
            case DIAMOND -> createDiamond();
            case EMERALD -> createEmerald();
            case NETHERITE -> createNetherite();
            case OBSIDIAN -> createObsidian();
        };
    }

    private static Item.Properties createCopper() {
        FoodProperties.Builder builder = breadBuilder(5, 0.4f);
        addEffect(builder, MobEffects.CONFUSION, 8, 0);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 60, 1);
        addEffect(builder, MobEffects.REGENERATION, 30, 1);
        return itemProperties(builder);
    }

    private static Item.Properties createIron() {
        FoodProperties.Builder builder = breadBuilder(6, 0.5f);
        addEffect(builder, MobEffects.CONFUSION, 3, 0);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 60, 1);
        addEffect(builder, MobEffects.DIG_SPEED, 30, 1);
        addEffect(builder, MobEffects.REGENERATION, 60, 0);
        return itemProperties(builder);
    }

    private static Item.Properties createGold() {
        FoodProperties.Builder builder = breadBuilder(6, 0.6f);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 90, 1);
        addEffect(builder, MobEffects.DIG_SPEED, 60, 2);
        addEffect(builder, MobEffects.LUCK, 120, 4);
        addEffect(builder, MobEffects.REGENERATION, 60, 1);
        addEffect(builder, MobEffects.HEAL, 1, 1);
        return itemProperties(builder);
    }

    private static Item.Properties createAmethyst() {
        FoodProperties.Builder builder = breadBuilder(7, 0.6f);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 90, 1);
        addEffect(builder, MobEffects.DIG_SPEED, 90, 1);
        addEffect(builder, MobEffects.NIGHT_VISION, 180, 0);
        addEffect(builder, MobEffects.REGENERATION, 90, 1);
        addEffect(builder, MobEffects.HEAL, 1, 2);
        return itemProperties(builder);
    }

    private static Item.Properties createAquamarine() {
        FoodProperties.Builder builder = breadBuilder(7, 0.7f);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 120, 2);
        addEffect(builder, MobEffects.DIG_SPEED, 90, 1);
        addEffect(builder, MobEffects.WATER_BREATHING, 180, 0);
        addEffect(builder, MobEffects.REGENERATION, 90, 1);
        addEffect(builder, MobEffects.HEAL, 1, 5);
        return itemProperties(builder);
    }

    private static Item.Properties createTopaz() {
        FoodProperties.Builder builder = breadBuilder(8, 0.7f);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 120, 2);
        addEffect(builder, MobEffects.DIG_SPEED, 120, 2);
        addEffect(builder, MobEffects.JUMP, 120, 0);
        addEffect(builder, MobEffects.REGENERATION, 90, 1);
        addEffect(builder, MobEffects.HEAL, 1, 1);
        addEffect(builder, MobEffects.HEAL, 1, 10);
        return itemProperties(builder);
    }

    private static Item.Properties createRuby() {
        FoodProperties.Builder builder = breadBuilder(8, 0.8f);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 120, 3);
        addEffect(builder, MobEffects.DIG_SPEED, 120, 2);
        addEffect(builder, MobEffects.DAMAGE_BOOST, 120, 3);
        addEffect(builder, MobEffects.REGENERATION, 120, 2);
        addEffect(builder, MobEffects.HEAL, 1, 20);
        return itemProperties(builder);
    }

    private static Item.Properties createSapphire() {
        FoodProperties.Builder builder = breadBuilder(9, 0.8f);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 120, 3);
        addEffect(builder, MobEffects.DIG_SPEED, 120, 2);
        addEffect(builder, MobEffects.MOVEMENT_SPEED, 120, 3);
        addEffect(builder, MobEffects.REGENERATION, 120, 2);
        addEffect(builder, MobEffects.HEAL, 1, 30);
        return itemProperties(builder);
    }

    private static Item.Properties createDiamond() {
        FoodProperties.Builder builder = breadBuilder(10, 1.0f);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 180, 3);
        addEffect(builder, MobEffects.DIG_SPEED, 180, 3);
        addEffect(builder, MobEffects.ABSORPTION, 180, 9);
        addEffect(builder, MobEffects.REGENERATION, 180, 3);
        addEffect(builder, MobEffects.HEAL, 1, 40);
        return itemProperties(builder);
    }

    private static Item.Properties createEmerald() {
        FoodProperties.Builder builder = breadBuilder(10, 1.0f);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 180, 3);
        addEffect(builder, MobEffects.DIG_SPEED, 180, 3);
        addEffect(builder, MobEffects.LUCK, 180, 9);
        addEffect(builder, MobEffects.REGENERATION, 180, 4);
        addEffect(builder, MobEffects.HEAL, 1, 50);
        return itemProperties(builder);
    }

    private static Item.Properties createNetherite() {
        FoodProperties.Builder builder = breadBuilder(12, 1.2f);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 180, 4);
        addEffect(builder, MobEffects.DIG_SPEED, 180, 4);
        addEffect(builder, MobEffects.FIRE_RESISTANCE, 180, 4);
        addEffect(builder, MobEffects.DAMAGE_BOOST, 180, 5);
        addEffect(builder, MobEffects.REGENERATION, 180, 4);
        addEffect(builder, MobEffects.HEAL, 1, 60);
        return itemProperties(builder);
    }

    private static Item.Properties createObsidian() {
        FoodProperties.Builder builder = breadBuilder(14, 1.5f);
        addEffect(builder, MobEffects.DAMAGE_RESISTANCE, 180, 4);
        addEffect(builder, MobEffects.DIG_SPEED, 180, 10);
        addEffect(builder, MobEffects.ABSORPTION, 180, 20);
        addEffect(builder, MobEffects.REGENERATION, 180, 9);
        addEffect(builder, MobEffects.HEAL, 1, 100);
        return itemProperties(builder);
    }

    private static FoodProperties.Builder breadBuilder(int nutrition, float saturation) {
        return new FoodProperties.Builder()
            .nutrition(nutrition)
            .saturationModifier(saturation)
            .alwaysEdible();
    }

    private static Item.Properties itemProperties(FoodProperties.Builder builder) {
        return new Item.Properties()
            .stacksTo(STACK_SIZE)
            .food(builder.build());
    }

    private static void addEffect(
        FoodProperties.Builder builder,
        Holder<MobEffect> effect,
        int durationSeconds,
        int amplifier
    ) {
        int durationTicks = durationSeconds * TICKS_PER_SECOND;
        builder.effect(
            () -> new MobEffectInstance(effect, durationTicks, amplifier),
            EFFECT_CHANCE
        );
    }
}
