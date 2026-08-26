package com.yukke9265.cobblestone_xx_compressed.multiblock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * コア基準の固定相対パターンです。
 * 前提: 相対座標は「コアが北向きのとき -Z が正面」。
 * 結果: tryValidate で formed 判定とポート座標一覧が得られる。
 */
public final class MultiblockPattern {
    public record Cell(int relativeX, int relativeY, int relativeZ, MultiblockCellType type) {
    }

    public record ValidationResult(
        List<BlockPos> memberPositions,
        Map<MultiblockCellType, List<BlockPos>> portsByType,
        int accelerationMultiplier,
        int energizedMultiplier,
        int parallelExtraCraftCount
    ) {
    }

    private final List<Cell> cells;

    public MultiblockPattern(List<Cell> cells) {
        this.cells = List.copyOf(cells);
    }

    public List<Cell> getCells() {
        return this.cells;
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

            if (!matchesCell(cell.type(), state)) {
                return Optional.empty();
            }

            if (cell.type() != MultiblockCellType.AIR) {
                memberPositions.add(worldPos);
            }

            if (isPortType(cell.type())) {
                portsByType.computeIfAbsent(cell.type(), ignored -> new ArrayList<>()).add(worldPos);
            }

            if (cell.type() == MultiblockCellType.UPGRADE_ACCEL) {
                int value = MachineUpgradeBlockHelper.getAccelerationMultiplier(state);
                if (value > 0) {
                    accelerationMultiplier = value;
                }
            } else if (cell.type() == MultiblockCellType.UPGRADE_ENERGY) {
                int value = MachineUpgradeBlockHelper.getEnergizedMultiplier(state);
                if (value > 0) {
                    energizedMultiplier = value;
                }
            } else if (cell.type() == MultiblockCellType.UPGRADE_PARALLEL) {
                int value = MachineUpgradeBlockHelper.getParallelExtraCraftCount(state);
                if (value > 0) {
                    parallelExtraCraftCount = value;
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

    public static boolean matchesCell(MultiblockCellType type, BlockState state) {
        return switch (type) {
            case AIR -> state.isAir();
            case CASING -> isCasing(state);
            case ITEM_IN -> state.is(ModBlocks.MULTIBLOCK_ITEM_INPUT_PORT.get());
            case ITEM_OUT -> state.is(ModBlocks.MULTIBLOCK_ITEM_OUTPUT_PORT.get());
            case FLUID_IN -> state.is(ModBlocks.MULTIBLOCK_FLUID_INPUT_PORT.get());
            case FLUID_OUT -> state.is(ModBlocks.MULTIBLOCK_FLUID_OUTPUT_PORT.get());
            case COBBLE_IN -> state.is(ModBlocks.MULTIBLOCK_COBBLE_INPUT_PORT.get());
            case UPGRADE_ACCEL -> state.isAir() || MachineUpgradeBlockHelper.isAccelerationUpgradeBlock(state);
            case UPGRADE_ENERGY -> state.isAir() || MachineUpgradeBlockHelper.isEnergizedUpgradeBlock(state);
            case UPGRADE_PARALLEL -> state.isAir() || MachineUpgradeBlockHelper.isParallelUpgradeBlock(state);
            case CORE -> true;
        };
    }

    public static boolean isCasing(BlockState state) {
        if (state.is(ModBlocks.COBBLESTONE_MACHINE_CASING.get())) {
            return true;
        }

        for (ModBlocks.TierCobblestoneMachineCasing tier : ModBlocks.TierCobblestoneMachineCasing.values()) {
            if (state.is(tier.getBlock().get())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 第一機用 3x3x3 パターンです。
     * 底面中央がコア、中段に item/cobble ポート、上面にアップグレード枠 3 つ。
     */
    public static MultiblockPattern createMultiblockCrusherPattern() {
        List<Cell> cells = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) {
                    cells.add(new Cell(0, 0, 0, MultiblockCellType.CORE));
                } else {
                    cells.add(new Cell(x, 0, z, MultiblockCellType.CASING));
                }
            }
        }

        cells.add(new Cell(-1, 1, -1, MultiblockCellType.CASING));
        cells.add(new Cell(1, 1, -1, MultiblockCellType.CASING));
        cells.add(new Cell(-1, 1, 1, MultiblockCellType.CASING));
        cells.add(new Cell(1, 1, 1, MultiblockCellType.CASING));
        cells.add(new Cell(0, 1, -1, MultiblockCellType.ITEM_IN));
        cells.add(new Cell(0, 1, 1, MultiblockCellType.ITEM_OUT));
        cells.add(new Cell(-1, 1, 0, MultiblockCellType.COBBLE_IN));
        cells.add(new Cell(1, 1, 0, MultiblockCellType.CASING));
        cells.add(new Cell(0, 1, 0, MultiblockCellType.AIR));

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == -1) {
                    cells.add(new Cell(x, 2, z, MultiblockCellType.UPGRADE_ACCEL));
                } else if (x == 0 && z == 0) {
                    cells.add(new Cell(x, 2, z, MultiblockCellType.UPGRADE_ENERGY));
                } else if (x == 0 && z == 1) {
                    cells.add(new Cell(x, 2, z, MultiblockCellType.UPGRADE_PARALLEL));
                } else {
                    cells.add(new Cell(x, 2, z, MultiblockCellType.CASING));
                }
            }
        }

        return new MultiblockPattern(cells);
    }
}
