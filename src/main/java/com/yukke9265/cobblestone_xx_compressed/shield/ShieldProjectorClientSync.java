package com.yukke9265.cobblestone_xx_compressed.shield;

import com.yukke9265.cobblestone_xx_compressed.network.ShieldProjectorHudPayload;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * サーバから範囲内プレイヤーへシールド HUD 用データを送ります。
 */
public final class ShieldProjectorClientSync {
    private ShieldProjectorClientSync() {
    }

    public static void sendToPlayer(ServerPlayer player, BlockPos projectorPos, long storedShield, long maxShield) {
        PacketDistributor.sendToPlayer(
            player,
            new ShieldProjectorHudPayload(projectorPos, storedShield, maxShield)
        );
    }
}
