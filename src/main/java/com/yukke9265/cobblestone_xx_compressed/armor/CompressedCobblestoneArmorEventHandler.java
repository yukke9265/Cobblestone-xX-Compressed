package com.yukke9265.cobblestone_xx_compressed.armor;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModAttachmentTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 圧縮丸石 advanced 防具の装備キャッシュ更新と、被ダメ軽減適用を担当します。
 */
@EventBusSubscriber(modid = CobblestonexXCompressed.MODID)
public final class CompressedCobblestoneArmorEventHandler {
    private static final ResourceLocation KNOCKBACK_IMMUNITY_MODIFIER_ID =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "advanced_cobblestone_armor_knockback_immunity");
    private static final AttributeModifier KNOCKBACK_IMMUNITY_MODIFIER =
        new AttributeModifier(KNOCKBACK_IMMUNITY_MODIFIER_ID, 1.0, Operation.ADD_VALUE);

    private CompressedCobblestoneArmorEventHandler() {
    }

    @SubscribeEvent
    public static void onEquipmentChanged(LivingEquipmentChangeEvent event) {
        if (!event.getSlot().isArmor()) {
            return;
        }

        refreshArmorState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        refreshArmorState(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        refreshArmorState(event.getEntity());
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }

        CompressedCobblestoneArmorState armorState = entity.getData(ModAttachmentTypes.COMPRESSED_COBBLESTONE_ARMOR_STATE.get());
        if (!armorState.hasAdvancedArmor()) {
            return;
        }

        DamageContainer damageContainer = event.getContainer();
        float incomingDamage = damageContainer.getNewDamage();
        if (incomingDamage <= 0.0f) {
            return;
        }

        float reduction = armorState.getDamageReduction();
        float remainingDamage = incomingDamage * (1.0f - reduction);
        if (remainingDamage <= 0.0f) {
            event.setCanceled(true);
            return;
        }

        damageContainer.setNewDamage(remainingDamage);
    }

    private static void refreshArmorState(LivingEntity entity) {
        CompressedCobblestoneArmorState state = CompressedCobblestoneArmorEquipmentScanner.scan(entity);
        entity.setData(ModAttachmentTypes.COMPRESSED_COBBLESTONE_ARMOR_STATE.get(), state);
        updateKnockbackImmunity(entity, CompressedCobblestoneArmorProtectionRules.grantsKnockbackImmunity(state.getAdvancedPieceCount()));
    }

    /**
     * DIAMOND 以降の防具装備時は、ノックバック耐性を 100% 付与します。
     */
    private static void updateKnockbackImmunity(LivingEntity entity, boolean enabled) {
        AttributeInstance knockbackResistance = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockbackResistance == null) {
            return;
        }

        boolean hasModifier = knockbackResistance.hasModifier(KNOCKBACK_IMMUNITY_MODIFIER_ID);
        if (enabled && !hasModifier) {
            knockbackResistance.addTransientModifier(KNOCKBACK_IMMUNITY_MODIFIER);
            return;
        }

        if (!enabled && hasModifier) {
            knockbackResistance.removeModifier(KNOCKBACK_IMMUNITY_MODIFIER_ID);
        }
    }
}
