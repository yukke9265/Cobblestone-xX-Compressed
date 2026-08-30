package com.yukke9265.cobblestone_xx_compressed.shield;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.ShieldProjectorBlockEntity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 範囲内プレイヤーの被ダメ肩代わりと、環境オーラを担当します。
 */
@EventBusSubscriber(modid = CobblestonexXCompressed.MODID)
public final class ShieldProjectorProtectionHandler {
    private ShieldProjectorProtectionHandler() {
    }

    /**
     * シールド残量がある範囲内プレイヤーへ、環境デバフ解消オーラをかけます。
     */
    @SubscribeEvent
    public static void onPlayerTickPre(PlayerTickEvent.Pre event) {
        tryApplyEnvironmentAura(event.getEntity());
    }

    /**
     * 範囲内・稼働・シールド残量ありのときだけ環境オーラをかけます。
     * ゲートのみで、デバフ解除ではシールドを消費しません。
     */
    private static void tryApplyEnvironmentAura(LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        if (player.isRemoved()) {
            return;
        }

        ShieldProjectorBlockEntity projector = ShieldProjectorTracker.findBestCoveringProjector(player);
        if (projector == null || !projector.canProtectPlayers()) {
            return;
        }

        if (projector.getStoredShield() <= 0L) {
            return;
        }

        ShieldProjectorEnvironmentGuard.tryClear(player);
    }

    /**
     * 防具軽減（HIGH）のあとに走り、残りダメだけをシールドで肩代わりします。
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
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
        // 防具側が先に setNewDamage / cancel したあとの値だけを見ます。
        float incomingDamage = damageContainer.getNewDamage();
        if (incomingDamage <= 0.0f) {
            return;
        }

        float absorbed = projector.absorbDamage(incomingDamage);
        if (absorbed <= 0.0f) {
            return;
        }

        projector.playAbsorbFeedbackSound(player);

        float remainingDamage = incomingDamage - absorbed;
        if (remainingDamage <= 0.0f) {
            event.setCanceled(true);
            return;
        }

        damageContainer.setNewDamage(remainingDamage);
    }
}
