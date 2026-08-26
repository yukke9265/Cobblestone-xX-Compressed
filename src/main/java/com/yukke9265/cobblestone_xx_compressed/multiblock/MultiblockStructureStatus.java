package com.yukke9265.cobblestone_xx_compressed.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 3x3x3 固定パターン向けの構造状況ユーティリティです。
 * 前提: 相対座標は x/z = -1..1、y = 0..2。
 * 結果: GUI 用の一致ビットと期待セル種別を共通の index で扱える。
 */
public final class MultiblockStructureStatus {
    public static final int SIZE = 3;
    public static final int LAYER_COUNT = 3;
    public static final int CELL_COUNT = SIZE * SIZE * LAYER_COUNT;

    private MultiblockStructureStatus() {
    }

    /**
     * 相対座標を 0..26 の index へ変換します。
     * 正面（相対 z = -1）が各層グリッドの上段になります。
     */
    public static int toIndex(int relativeX, int relativeY, int relativeZ) {
        return relativeY * (SIZE * SIZE) + (relativeZ + 1) * SIZE + (relativeX + 1);
    }

    public static int relativeX(int index) {
        return (index % SIZE) - 1;
    }

    public static int relativeY(int index) {
        return index / (SIZE * SIZE);
    }

    public static int relativeZ(int index) {
        return ((index / SIZE) % SIZE) - 1;
    }

    public static boolean isMatched(int matchMask, int index) {
        if (index < 0 || index >= CELL_COUNT) {
            return false;
        }

        return (matchMask & (1 << index)) != 0;
    }

    public static int withMatched(int matchMask, int index, boolean matched) {
        if (index < 0 || index >= CELL_COUNT) {
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
        int matchMask = 0;
        int matchedCount = 0;
        int requiredCount = 0;
        MultiblockCellType[] expectedTypes = new MultiblockCellType[CELL_COUNT];

        for (MultiblockPattern.Cell cell : pattern.getCells()) {
            int index = toIndex(cell.relativeX(), cell.relativeY(), cell.relativeZ());
            if (index < 0 || index >= CELL_COUNT) {
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
            boolean matched = MultiblockPattern.matchesCell(cell.type(), state);
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

    public static String typeLabel(MultiblockCellType type) {
        if (type == null) {
            return "-";
        }

        return switch (type) {
            case CORE -> "コア";
            case AIR -> "空気";
            case CASING -> "筐体";
            case ITEM_IN -> "アイテム入力";
            case ITEM_OUT -> "アイテム出力";
            case FLUID_IN -> "流体入力";
            case FLUID_OUT -> "流体出力";
            case COBBLE_IN -> "丸石入力";
            case UPGRADE_ACCEL -> "加速UG(空可)";
            case UPGRADE_ENERGY -> "蓄電UG(空可)";
            case UPGRADE_PARALLEL -> "並列UG(空可)";
        };
    }
}
