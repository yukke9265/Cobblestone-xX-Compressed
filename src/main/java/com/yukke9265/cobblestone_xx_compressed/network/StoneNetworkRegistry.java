package com.yukke9265.cobblestone_xx_compressed.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.yukke9265.cobblestone_xx_compressed.blockentity.StoneNetworkPointBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * ロード中のネットワークポイントを、ネットワーク ID ごとに集めます。
 *
 * リレーは BE を持たないので、メンバー管理はポイントだけが行います。
 * 機械の付け外しでは塗りつぶしをせず、終点キャッシュだけを同じ tick にまとめて捨てます。
 */
public final class StoneNetworkRegistry {
    private static final Map<Level, Map<String, Set<BlockPos>>> MEMBERS_BY_LEVEL = new WeakHashMap<>();
    private static final Map<Level, Set<String>> PENDING_CACHE_INVALIDATIONS = new WeakHashMap<>();

    private StoneNetworkRegistry() {
    }

    public static void register(Level level, BlockPos pos, String networkId) {
        if (level.isClientSide) {
            return;
        }

        Map<String, Set<BlockPos>> membersById = MEMBERS_BY_LEVEL.computeIfAbsent(level, ignored -> new HashMap<>());
        Set<BlockPos> members = membersById.computeIfAbsent(networkId, ignored -> new HashSet<>());
        members.add(pos.immutable());
    }

    public static void unregister(Level level, BlockPos pos, String networkId) {
        if (level.isClientSide) {
            return;
        }

        Map<String, Set<BlockPos>> membersById = MEMBERS_BY_LEVEL.get(level);
        if (membersById == null) {
            return;
        }

        Set<BlockPos> members = membersById.get(networkId);
        if (members == null) {
            return;
        }

        members.remove(pos);
        if (!members.isEmpty()) {
            return;
        }

        membersById.remove(networkId);
        if (membersById.isEmpty()) {
            MEMBERS_BY_LEVEL.remove(level);
        }
    }

    public static List<BlockPos> getMembers(Level level, String networkId) {
        Map<String, Set<BlockPos>> membersById = MEMBERS_BY_LEVEL.get(level);
        if (membersById == null) {
            return List.of();
        }

        Set<BlockPos> members = membersById.get(networkId);
        if (members == null || members.isEmpty()) {
            return List.of();
        }

        return new ArrayList<>(members);
    }

    public static void scheduleCacheInvalidation(Level level, String networkId) {
        if (level.isClientSide || networkId.isEmpty()) {
            return;
        }

        PENDING_CACHE_INVALIDATIONS.computeIfAbsent(level, ignored -> new HashSet<>()).add(networkId);
    }

    public static void flushPendingCacheInvalidations(Level level) {
        if (level.isClientSide) {
            return;
        }

        Set<String> pendingIds = PENDING_CACHE_INVALIDATIONS.remove(level);
        if (pendingIds == null) {
            return;
        }

        for (String networkId : pendingIds) {
            invalidateEndpointCaches(level, networkId);
        }
    }

    public static void invalidateEndpointCaches(Level level, String networkId) {
        if (level.isClientSide) {
            return;
        }

        for (BlockPos memberPos : getMembers(level, networkId)) {
            BlockEntity blockEntity = level.getBlockEntity(memberPos);
            if (blockEntity instanceof StoneNetworkPointBlockEntity pointBlockEntity) {
                pointBlockEntity.clearLocalAccessCache();
            }
        }
    }
}
