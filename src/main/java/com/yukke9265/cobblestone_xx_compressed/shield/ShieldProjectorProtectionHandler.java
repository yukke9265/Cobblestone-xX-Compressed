package com.yukke9265.cobblestone_xx_compressed.shield;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.ShieldProjectorBlockEntity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * 範囲内プレイヤーの被ダメを共有シールドで肩代わりします。
 */
@EventBusSubscriber(modid = CobblestonexXCompressed.MODID)
public final class ShieldProjectorProtectionHandler {
    private ShieldProjectorProtectionHandler() {
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }

        if (!(entity instanceof Player player)) {
            return;
        }

        ShieldProjectorBlockEntity projector = ShieldProjectorTracker.findBestCoveringProjector(player);
        if (projector == null) {
            return;
        }

        DamageContainer damageContainer = event.getContainer();
        float incomingDamage = damageContainer.getNewDamage();
        if (incomingDamage <= 0.0f) {
            return;
        }

        float absorbed = projector.absorbDamage(incomingDamage);
        if (absorbed <= 0.0f) {
            return;
        }

        float remainingDamage = incomingDamage - absorbed;
        if (remainingDamage <= 0.0f) {
            event.setCanceled(true);
            return;
        }

        damageContainer.setNewDamage(remainingDamage);
    }
}
