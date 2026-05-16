package com.yukke9265.cobblestone_xx_compressed.blockentity;

import com.yukke9265.cobblestone_xx_compressed.block.RotatingBlock;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public enum AutomationSide {
    UP(0, "up"),
    DOWN(1, "down"),
    FRONT(2, "front"),
    BACK(3, "back"),
    LEFT(4, "left"),
    RIGHT(5, "right");

    private final int index;
    private final String serializedName;

    AutomationSide(int index, String serializedName) {
        this.index = index;
        this.serializedName = serializedName;
    }

    public int getIndex() {
        return this.index;
    }

    public String getSerializedName() {
        return this.serializedName;
    }

    public static AutomationSide fromIndex(int index) {
        for (AutomationSide automationSide : values()) {
            if (automationSide.index == index) {
                return automationSide;
            }
        }

        throw new IllegalArgumentException("不明な自動搬出入面 index です: " + index);
    }

    public static AutomationSide fromWorldSide(Direction worldSide, BlockState blockState) {
        if (worldSide == Direction.UP) {
            return UP;
        }

        if (worldSide == Direction.DOWN) {
            return DOWN;
        }

        Direction facing = blockState.getValue(RotatingBlock.FACING);
        if (worldSide == facing) {
            return FRONT;
        }

        if (worldSide == facing.getOpposite()) {
            return BACK;
        }

        if (worldSide == facing.getClockWise()) {
            return RIGHT;
        }

        if (worldSide == facing.getCounterClockWise()) {
            return LEFT;
        }

        throw new IllegalArgumentException("水平面の相対変換に失敗しました: " + worldSide);
    }
}