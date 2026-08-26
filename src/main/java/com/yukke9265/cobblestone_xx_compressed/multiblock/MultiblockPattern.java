package com.yukke9265.cobblestone_xx_compressed.multiblock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * コア基準の相対パターンです。
 * 前提: コアの向き方向が相対 -Z。文字列／ガイドはプレイヤー視点で「下段＝正面・上段＝背面」。
 * 結果: tryValidate で formed 判定とポート座標一覧が得られる。
 */
public final class MultiblockPattern {
    public record Cell(
        int relativeX,
        int relativeY,
        int relativeZ,
        MultiblockCellType type,
        StructureBlockMatcher matcher
    ) {
    }

    public record ValidationResult(
        List<BlockPos> memberPositions,
        Map<MultiblockCellType, List<BlockPos>> portsByType,
        int accelerationMultiplier,
        int energizedMultiplier,
        int parallelExtraCraftCount
    ) {
    }

    /**
     * パターン全体の相対座標範囲です。
     * 前提: コアが幾何中心である必要はない。
     * 結果: GUI / matchMask の index を一意に決められる。
     */
    public record GridBounds(int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ) {
        public int cellCount() {
            return this.sizeX * this.sizeY * this.sizeZ;
        }

        public int toIndex(int relativeX, int relativeY, int relativeZ) {
            int localX = relativeX - this.minX;
            int localY = relativeY - this.minY;
            int localZ = relativeZ - this.minZ;
            if (localX < 0 || localX >= this.sizeX
                || localY < 0 || localY >= this.sizeY
                || localZ < 0 || localZ >= this.sizeZ) {
                return -1;
            }

            return localY * (this.sizeX * this.sizeZ) + localZ * this.sizeX + localX;
        }

        public int relativeX(int index) {
            return this.minX + (index % this.sizeX);
        }

        public int relativeY(int index) {
            return this.minY + index / (this.sizeX * this.sizeZ);
        }

        public int relativeZ(int index) {
            return this.minZ + ((index / this.sizeX) % this.sizeZ);
        }
    }

    private record KeyDefinition(MultiblockCellType type, StructureBlockMatcher matcher) {
    }

    private final List<Cell> cells;
    private final GridBounds gridBounds;

    public MultiblockPattern(List<Cell> cells) {
        this.cells = List.copyOf(cells);
        this.gridBounds = computeGridBounds(this.cells);
        if (this.gridBounds.cellCount() > 32) {
            throw new IllegalArgumentException(
                "構造セル数が 32 を超えるため matchMask に収まりません: " + this.gridBounds.cellCount()
            );
        }
    }

    public List<Cell> getCells() {
        return this.cells;
    }

    public GridBounds getGridBounds() {
        return this.gridBounds;
    }

    private static GridBounds computeGridBounds(List<Cell> cells) {
        if (cells.isEmpty()) {
            throw new IllegalArgumentException("パターンにセルがありません");
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (Cell cell : cells) {
            minX = Math.min(minX, cell.relativeX());
            minY = Math.min(minY, cell.relativeY());
            minZ = Math.min(minZ, cell.relativeZ());
            maxX = Math.max(maxX, cell.relativeX());
            maxY = Math.max(maxY, cell.relativeY());
            maxZ = Math.max(maxZ, cell.relativeZ());
        }

        return new GridBounds(
            minX,
            minY,
            minZ,
            maxX - minX + 1,
            maxY - minY + 1,
            maxZ - minZ + 1
        );
    }

    public Optional<ValidationResult> tryValidate(Level level, BlockPos corePos, Direction facing) {
        List<BlockPos> memberPositions = new ArrayList<>();
        Map<MultiblockCellType, List<BlockPos>> portsByType = new EnumMap<>(MultiblockCellType.class);
        int accelerationMultiplier = 1;
        int energizedMultiplier = 1;
        int parallelExtraCraftCount = 0;

        for (Cell cell : this.cells) {
            if (cell.type() == MultiblockCellType.CORE) {
                continue;
            }

            BlockPos worldPos = toWorldPos(corePos, facing, cell.relativeX(), cell.relativeY(), cell.relativeZ());
            BlockState state = level.getBlockState(worldPos);

            if (!matchesCell(cell, state)) {
                return Optional.empty();
            }

            if (cell.type() != MultiblockCellType.AIR) {
                memberPositions.add(worldPos);
            }

            // INOUT 枠はポート種をブロックから判定。筐体ならポート登録しない。
            if (cell.type() == MultiblockCellType.INOUT) {
                Optional<MultiblockCellType> portType = resolvePortType(state);
                if (portType.isPresent()) {
                    portsByType.computeIfAbsent(portType.get(), ignored -> new ArrayList<>()).add(worldPos);
                }
            } else if (isPortType(cell.type())) {
                portsByType.computeIfAbsent(cell.type(), ignored -> new ArrayList<>()).add(worldPos);
            }

            // アップグレード枠は種類自由。置かれたブロック種から倍率を拾う。
            if (cell.type() == MultiblockCellType.UPGRADE) {
                int accel = MachineUpgradeBlockHelper.getAccelerationMultiplier(state);
                if (accel > accelerationMultiplier) {
                    accelerationMultiplier = accel;
                }

                int energy = MachineUpgradeBlockHelper.getEnergizedMultiplier(state);
                if (energy > energizedMultiplier) {
                    energizedMultiplier = energy;
                }

                int parallel = MachineUpgradeBlockHelper.getParallelExtraCraftCount(state);
                if (parallel > 0) {
                    parallelExtraCraftCount += parallel;
                }
            }
        }

        for (Map.Entry<MultiblockCellType, List<BlockPos>> entry : portsByType.entrySet()) {
            entry.setValue(Collections.unmodifiableList(entry.getValue()));
        }

        return Optional.of(new ValidationResult(
            Collections.unmodifiableList(memberPositions),
            Collections.unmodifiableMap(portsByType),
            accelerationMultiplier,
            energizedMultiplier,
            parallelExtraCraftCount
        ));
    }

    public static BlockPos toWorldPos(BlockPos corePos, Direction facing, int relativeX, int relativeY, int relativeZ) {
        BlockPos rotated = rotateOffset(relativeX, relativeY, relativeZ, facing);
        return corePos.offset(rotated);
    }

    private static BlockPos rotateOffset(int relativeX, int relativeY, int relativeZ, Direction facing) {
        return switch (facing) {
            case SOUTH -> new BlockPos(-relativeX, relativeY, -relativeZ);
            case WEST -> new BlockPos(relativeZ, relativeY, -relativeX);
            case EAST -> new BlockPos(-relativeZ, relativeY, relativeX);
            default -> new BlockPos(relativeX, relativeY, relativeZ);
        };
    }

    private static boolean isPortType(MultiblockCellType type) {
        return type == MultiblockCellType.ITEM_IN
            || type == MultiblockCellType.ITEM_OUT
            || type == MultiblockCellType.FLUID_IN
            || type == MultiblockCellType.FLUID_OUT
            || type == MultiblockCellType.COBBLE_IN;
    }

    /**
     * 置かれているブロックからポート Role を求めます。
     * 前提: ポート以外（筐体など）では empty。
     */
    private static Optional<MultiblockCellType> resolvePortType(BlockState state) {
        if (state.is(ModBlocks.MULTIBLOCK_ITEM_INPUT_PORT.get())) {
            return Optional.of(MultiblockCellType.ITEM_IN);
        }
        if (state.is(ModBlocks.MULTIBLOCK_ITEM_OUTPUT_PORT.get())) {
            return Optional.of(MultiblockCellType.ITEM_OUT);
        }
        if (state.is(ModBlocks.MULTIBLOCK_FLUID_INPUT_PORT.get())) {
            return Optional.of(MultiblockCellType.FLUID_IN);
        }
        if (state.is(ModBlocks.MULTIBLOCK_FLUID_OUTPUT_PORT.get())) {
            return Optional.of(MultiblockCellType.FLUID_OUT);
        }
        if (state.is(ModBlocks.MULTIBLOCK_COBBLE_INPUT_PORT.get())) {
            return Optional.of(MultiblockCellType.COBBLE_IN);
        }

        return Optional.empty();
    }

    public static boolean matchesCell(Cell cell, BlockState state) {
        return cell.matcher().test(state);
    }

    public static boolean isCasing(BlockState state) {
        return StructureCategories.CASING.matches(state);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 文字列レイヤーからパターンを組み立てます。
     * 前提: 各層の上段が正面(相対 z が小さい側)。CORE 記号は全体でちょうど1つ。
     * 結果: CORE を (0,0,0) とした相対 Cell 一覧になる。
     */
    public static final class Builder {
        private final Map<Integer, String[]> layersByY = new TreeMap<>();
        private final Map<Character, KeyDefinition> keys = new HashMap<>();

        private Builder() {
        }

        public Builder layer(int relativeY, String... rows) {
            if (rows == null || rows.length == 0) {
                throw new IllegalArgumentException("layer の行が空です: y=" + relativeY);
            }

            int width = rows[0].length();
            if (width == 0) {
                throw new IllegalArgumentException("layer の幅が 0 です: y=" + relativeY);
            }

            for (int i = 0; i < rows.length; i++) {
                if (rows[i] == null || rows[i].length() != width) {
                    throw new IllegalArgumentException(
                        "layer の行幅が揃っていません: y=" + relativeY + ", row=" + i
                    );
                }
            }

            if (this.layersByY.containsKey(relativeY)) {
                throw new IllegalArgumentException("同じ y の layer が重複しています: y=" + relativeY);
            }

            this.layersByY.put(relativeY, rows.clone());
            return this;
        }

        public Builder key(char symbol, MultiblockCellType type, StructureBlockMatcher matcher) {
            if (this.keys.containsKey(symbol)) {
                throw new IllegalArgumentException("記号が重複しています: '" + symbol + "'");
            }

            this.keys.put(symbol, new KeyDefinition(type, matcher));
            return this;
        }

        public MultiblockPattern build() {
            if (this.layersByY.isEmpty()) {
                throw new IllegalStateException("layer が1つもありません");
            }

            int coreLayerY = 0;
            int coreRow = -1;
            int coreCol = -1;
            int coreCount = 0;

            for (Map.Entry<Integer, String[]> entry : this.layersByY.entrySet()) {
                int y = entry.getKey();
                String[] rows = entry.getValue();
                for (int row = 0; row < rows.length; row++) {
                    String line = rows[row];
                    for (int col = 0; col < line.length(); col++) {
                        char symbol = line.charAt(col);
                        KeyDefinition definition = this.keys.get(symbol);
                        if (definition == null) {
                            throw new IllegalStateException(
                                "未定義の記号です: '" + symbol + "' (y=" + y + ", row=" + row + ", col=" + col + ")"
                            );
                        }

                        if (definition.type() == MultiblockCellType.CORE) {
                            coreCount++;
                            coreLayerY = y;
                            coreRow = row;
                            coreCol = col;
                        }
                    }
                }
            }

            if (coreCount != 1) {
                throw new IllegalStateException("CORE 記号はちょうど1つ必要です（現在 " + coreCount + " 個）");
            }

            List<Cell> cells = new ArrayList<>();
            for (Map.Entry<Integer, String[]> entry : this.layersByY.entrySet()) {
                int y = entry.getKey();
                String[] rows = entry.getValue();
                for (int row = 0; row < rows.length; row++) {
                    String line = rows[row];
                    for (int col = 0; col < line.length(); col++) {
                        char symbol = line.charAt(col);
                        KeyDefinition definition = this.keys.get(symbol);
                        // ガイドと同じ向き: 下段=正面(相対z小)、左=相対x大。
                        // コア向きの正面はプレイヤー視点では奥側に見えるため、文字列は視点合わせで書く。
                        int relativeX = coreCol - col;
                        int relativeY = y - coreLayerY;
                        int relativeZ = coreRow - row;
                        cells.add(new Cell(
                            relativeX,
                            relativeY,
                            relativeZ,
                            definition.type(),
                            definition.matcher()
                        ));
                    }
                }
            }

            // CORE が相対 (0,0,0) になっていることを保証する。
            boolean hasOriginCore = false;
            for (Cell cell : cells) {
                if (cell.type() == MultiblockCellType.CORE
                    && cell.relativeX() == 0
                    && cell.relativeY() == 0
                    && cell.relativeZ() == 0) {
                    hasOriginCore = true;
                    break;
                }
            }

            if (!hasOriginCore) {
                throw new IllegalStateException("CORE の相対座標が (0,0,0) になりませんでした");
            }

            return new MultiblockPattern(cells);
        }
    }

    /**
     * 第一機用 3x3x3 パターンです。
     * 底面にコア、中段に IO 枠、上面中央列がアップグレード枠 3 つ。
     * 各層の下段が正面（ガイドの「下=正面」と同じ）。
     */
    public static MultiblockPattern createMultiblockCrusherPattern() {
        return builder()
            .layer(0,
                "CCC",
                "CCC",
                "CKC")
            .layer(1,
                "CIC",
                "I~I",
                "CIC")
            .layer(2,
                "CUC",
                "CUC",
                "CUC")
            .key('K', MultiblockCellType.CORE, StructureBlockMatcher.any())
            .key('C', MultiblockCellType.CASING, StructureBlockMatcher.category(StructureCategories.CASING))
            .key('~', MultiblockCellType.AIR, StructureBlockMatcher.air())
            .key('I', MultiblockCellType.INOUT, StructureBlockMatcher.category(StructureCategories.INOUT))
            .key('U', MultiblockCellType.UPGRADE, StructureBlockMatcher.category(StructureCategories.UPGRADE))
            .build();
    }
}
