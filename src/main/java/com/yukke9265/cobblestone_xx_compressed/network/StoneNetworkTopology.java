package com.yukke9265.cobblestone_xx_compressed.network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.yukke9265.cobblestone_xx_compressed.blockentity.StoneNetworkPointBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * ポイントとリレーのつながりを塗りつぶし、塊ごとにネットワーク ID を付けます。
 *
 * 前提:
 * - リレーは BE を持たない。tick も capability もバッファも無い。
 * - 問い合わせのたびに地形を歩かない。置く・壊すときだけ塗りつぶす。
 * - 未ロードチャンクは hasChunkAt で止める。向こう側は、ロード時にポイント側がつなぎ直す。
 *
 * 結果:
 * - 同じ tick の塗りつぶしは 1 回にまとめる。
 * - つながった導体は同じ ID、分裂したら最小ポイント座標で新しい ID になる。
 */
public final class StoneNetworkTopology {
    private static final Map<Level, Set<BlockPos>> PENDING_REBUILDS = new WeakHashMap<>();

    private StoneNetworkTopology() {
    }

    public static void scheduleRebuild(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }

        PENDING_REBUILDS.computeIfAbsent(level, ignored -> new HashSet<>()).add(pos.immutable());
    }

    public static void scheduleRebuildFromNeighbors(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }

        for (Direction direction : Direction.values()) {
            scheduleRebuild(level, pos.relative(direction));
        }
    }

    public static void flushPendingWork(Level level) {
        if (level.isClientSide) {
            return;
        }

        Set<BlockPos> pendingStarts = PENDING_REBUILDS.remove(level);
        if (pendingStarts != null) {
            Set<BlockPos> visitedThisFlush = new HashSet<>();
            for (BlockPos startPos : pendingStarts) {
                rebuildFrom(level, startPos, visitedThisFlush);
            }
        }

        StoneNetworkRegistry.flushPendingCacheInvalidations(level);
    }

    private static void rebuildFrom(Level level, BlockPos startPos, Set<BlockPos> visitedThisFlush) {
        if (!level.hasChunkAt(startPos)) {
            return;
        }

        if (!StoneNetworkBlocks.isConductor(level.getBlockState(startPos))) {
            return;
        }

        if (visitedThisFlush.contains(startPos)) {
            return;
        }

        ArrayDeque<BlockPos> stack = new ArrayDeque<>();
        stack.push(startPos.immutable());
        List<BlockPos> points = new ArrayList<>();

        while (!stack.isEmpty()) {
            BlockPos currentPos = stack.pop();
            if (!visitedThisFlush.add(currentPos)) {
                continue;
            }

            if (!level.hasChunkAt(currentPos)) {
                visitedThisFlush.remove(currentPos);
                continue;
            }

            BlockState currentState = level.getBlockState(currentPos);
            if (!StoneNetworkBlocks.isConductor(currentState)) {
                continue;
            }

            if (StoneNetworkBlocks.isPoint(currentState)) {
                points.add(currentPos.immutable());
            }

            for (Direction direction : Direction.values()) {
                BlockPos nextPos = currentPos.relative(direction);
                if (visitedThisFlush.contains(nextPos)) {
                    continue;
                }

                if (!level.hasChunkAt(nextPos)) {
                    continue;
                }

                if (StoneNetworkBlocks.isConductor(level.getBlockState(nextPos))) {
                    stack.push(nextPos.immutable());
                }
            }
        }

        if (points.isEmpty()) {
            return;
        }

        String networkId = StoneNetworkIds.fromMinimumPoint(points);
        for (BlockPos pointPos : points) {
            BlockEntity blockEntity = level.getBlockEntity(pointPos);
            if (blockEntity instanceof StoneNetworkPointBlockEntity pointBlockEntity) {
                pointBlockEntity.applyRebuiltNetworkId(networkId);
            }
        }
    }
}
