package com.yukke9265.cobblestone_xx_compressed.multiblock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * 形成済みメンバ座標からコア座標を引くインデックスです。
 * 前提: form / unform 時だけ更新し、毎 tick では探索しない。
 * 結果: ポート capability は O(1) でコアへ到達できる。
 */
public final class MultiblockFormIndex {
    private static final Map<Level, MultiblockFormIndex> INDEXES = Collections.synchronizedMap(new WeakHashMap<>());

    private final Map<BlockPos, BlockPos> memberToCore = new HashMap<>();

    private MultiblockFormIndex() {
    }

    public static MultiblockFormIndex get(Level level) {
        return INDEXES.computeIfAbsent(level, ignored -> new MultiblockFormIndex());
    }

    public synchronized void registerMembers(BlockPos corePos, Iterable<BlockPos> memberPositions) {
        BlockPos immutableCore = corePos.immutable();
        for (BlockPos memberPos : memberPositions) {
            this.memberToCore.put(memberPos.immutable(), immutableCore);
        }
    }

    public synchronized void unregisterMembers(Iterable<BlockPos> memberPositions) {
        for (BlockPos memberPos : memberPositions) {
            this.memberToCore.remove(memberPos);
        }
    }

    public synchronized BlockPos getCorePos(BlockPos memberPos) {
        return this.memberToCore.get(memberPos);
    }

    public static MultiblockPoweredMachineBlockEntityBase findCore(Level level, BlockPos memberPos) {
        if (level == null || memberPos == null) {
            return null;
        }

        BlockPos corePos = get(level).getCorePos(memberPos);
        if (corePos == null) {
            return null;
        }

        BlockEntity blockEntity = level.getBlockEntity(corePos);
        if (blockEntity instanceof MultiblockPoweredMachineBlockEntityBase multiblockCore) {
            return multiblockCore;
        }

        return null;
    }

    /**
     * メンバ破壊時にコアへ再検証を依頼します。
     * インデックスに無い場合でも、近傍のコアを探して dirty にします（アップグレード枠が空気のとき用）。
     */
    public static void requestRevalidation(Level level, BlockPos changedPos) {
        if (level == null || level.isClientSide || changedPos == null) {
            return;
        }

        MultiblockPoweredMachineBlockEntityBase core = findCore(level, changedPos);
        if (core != null) {
            core.markStructureDirty();
            return;
        }

        BlockEntity self = level.getBlockEntity(changedPos);
        if (self instanceof MultiblockPoweredMachineBlockEntityBase selfCore) {
            selfCore.markStructureDirty();
        }

        // 未登録の空気枠へアップグレードを置いた場合など、近傍コアも再検証する。
        final int searchRadius = 3;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dy = -searchRadius; dy <= searchRadius; dy++) {
            for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                    cursor.set(changedPos.getX() + dx, changedPos.getY() + dy, changedPos.getZ() + dz);
                    BlockEntity neighbor = level.getBlockEntity(cursor);
                    if (neighbor instanceof MultiblockPoweredMachineBlockEntityBase neighborCore) {
                        neighborCore.markStructureDirty();
                    }
                }
            }
        }
    }
}
