package com.yukke9265.cobblestone_xx_compressed.multiblock;

import java.util.Locale;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * マルチブロック構造の一致状況ユーティリティです。
 * 前提: index はパターンの GridBounds 基準（コア中心固定ではない）。
 * 結果: GUI 用の一致ビットと期待セル種別を共通の index で扱える。
 */
public final class MultiblockStructureStatus {
    private MultiblockStructureStatus() {
    }

    public static boolean isMatched(int matchMask, int index) {
        if (index < 0 || index >= 32) {
            return false;
        }

        return (matchMask & (1 << index)) != 0;
    }

    public static int withMatched(int matchMask, int index, boolean matched) {
        if (index < 0 || index >= 32) {
            return matchMask;
        }

        int bit = 1 << index;
        if (matched) {
            return matchMask | bit;
        }

        return matchMask & ~bit;
    }

    /**
     * パターンを走査し、一致ビットと不足数を計算します。
     */
    public static InspectionResult inspect(MultiblockPattern pattern, Level level, BlockPos corePos, Direction facing) {
        MultiblockPattern.GridBounds bounds = pattern.getGridBounds();
        int cellCount = bounds.cellCount();
        int matchMask = 0;
        int matchedCount = 0;
        int requiredCount = 0;
        MultiblockCellType[] expectedTypes = new MultiblockCellType[cellCount];

        for (MultiblockPattern.Cell cell : pattern.getCells()) {
            int index = bounds.toIndex(cell.relativeX(), cell.relativeY(), cell.relativeZ());
            if (index < 0 || index >= cellCount) {
                continue;
            }

            expectedTypes[index] = cell.type();
            requiredCount++;

            BlockPos worldPos = MultiblockPattern.toWorldPos(
                corePos,
                facing,
                cell.relativeX(),
                cell.relativeY(),
                cell.relativeZ()
            );
            BlockState state = level.getBlockState(worldPos);
            boolean matched = MultiblockPattern.matchesCell(cell, state);
            if (matched) {
                matchMask = withMatched(matchMask, index, true);
                matchedCount++;
            }
        }

        return new InspectionResult(matchMask, matchedCount, requiredCount, expectedTypes);
    }

    public record InspectionResult(
        int matchMask,
        int matchedCount,
        int requiredCount,
        MultiblockCellType[] expectedTypes
    ) {
        public boolean isComplete() {
            return this.matchedCount >= this.requiredCount && this.requiredCount > 0;
        }
    }

    public static Component typeLabel(MultiblockCellType type) {
        if (type == null) {
            return Component.literal("-");
        }

        return Component.translatable("multiblock_cell.cobblestonexxcompressed." + type.name().toLowerCase(Locale.ROOT));
    }
}
