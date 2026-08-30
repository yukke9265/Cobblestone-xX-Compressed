package com.yukke9265.cobblestone_xx_compressed.shield;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * 防具の Incoming 軽減が止めない状態（炎上・凍結・酸素・有害エフェクト）を範囲内で解消します。
 * 発動条件はシールド残量ゲートです。消費はしません。
 */
public final class ShieldProjectorEnvironmentGuard {
    private ShieldProjectorEnvironmentGuard() {
    }

    /**
     * カバー中プレイヤーの環境デバフを毎 tick クリアします。
     */
    public static void tryClear(ServerPlayer player) {
        player.clearFire();
        player.setTicksFrozen(0);
        player.setAirSupply(player.getMaxAirSupply());
        removeHarmfulEffects(player);
    }

    /**
     * 未知の HARMFUL 登録デバフも含め、カテゴリ一律で除去します。
     */
    private static void removeHarmfulEffects(ServerPlayer player) {
        List<Holder<MobEffect>> harmfulEffects = new ArrayList<>();
        for (MobEffectInstance instance : player.getActiveEffects()) {
            Holder<MobEffect> effect = instance.getEffect();
            if (effect.value().getCategory() == MobEffectCategory.HARMFUL) {
                harmfulEffects.add(effect);
            }
        }

        for (Holder<MobEffect> effect : harmfulEffects) {
            player.removeEffect(effect);
        }
    }
}
