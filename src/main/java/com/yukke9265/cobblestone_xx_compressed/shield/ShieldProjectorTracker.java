package com.yukke9265.cobblestone_xx_compressed.shield;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.yukke9265.cobblestone_xx_compressed.blockentity.ShieldProjectorBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * 稼働中シールドプロジェクターの位置を次元ごとに保持し、被ダメ時の検索を軽くします。
 */
public final class ShieldProjectorTracker {
    private static final Map<ResourceKey<Level>, Set<BlockPos>> ACTIVE_PROJECTORS = new ConcurrentHashMap<>();

    private ShieldProjectorTracker() {
    }

    public static void add(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }

        ACTIVE_PROJECTORS
            .computeIfAbsent(level.dimension(), key -> ConcurrentHashMap.newKeySet())
            .add(pos.immutable());
    }

    public static void remove(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }

        Set<BlockPos> positions = ACTIVE_PROJECTORS.get(level.dimension());
        if (positions == null) {
            return;
        }

        positions.remove(pos);
        if (positions.isEmpty()) {
            ACTIVE_PROJECTORS.remove(level.dimension(), positions);
        }
    }

    /**
     * プレイヤーを覆う稼働プロジェクターのうち、残りシールドが最大のものを返します。
     */
    public static ShieldProjectorBlockEntity findBestCoveringProjector(Player player) {
        Level level = player.level();
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        Set<BlockPos> positions = ACTIVE_PROJECTORS.get(serverLevel.dimension());
        if (positions == null || positions.isEmpty()) {
            return null;
        }

        ShieldProjectorBlockEntity bestProjector = null;
        long bestShield = -1L;

        for (BlockPos pos : positions) {
            BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
            if (!(blockEntity instanceof ShieldProjectorBlockEntity projector)) {
                continue;
            }

            if (!projector.canProtectPlayers()) {
                continue;
            }

            if (!projector.isPlayerInRange(player)) {
                continue;
            }

            long shield = projector.getStoredShield();
            if (shield > bestShield) {
                bestShield = shield;
                bestProjector = projector;
            }
        }

        return bestProjector;
    }
}
