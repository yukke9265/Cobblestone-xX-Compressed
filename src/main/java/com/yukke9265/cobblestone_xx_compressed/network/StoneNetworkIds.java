package com.yukke9265.cobblestone_xx_compressed.network;

import java.util.List;

import net.minecraft.core.BlockPos;

/**
 * ネットワークポイントが属するネットワークの ID です。
 *
 * 塊の ID は、その塊に含まれるポイント座標の最小値です。
 * 既存ポイントの ID をコピーすると、分裂後も古い塊のまま残るため使いません。
 * リレーだけの塊はポイントが無いので ID を付けません。
 */
public final class StoneNetworkIds {
    public static final String SHARED = "shared";

    private StoneNetworkIds() {
    }

    public static String fromPointPos(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static String fromMinimumPoint(List<BlockPos> points) {
        if (points.isEmpty()) {
            return "";
        }

        BlockPos minimumPos = points.get(0);
        for (BlockPos pointPos : points) {
            if (pointPos.compareTo(minimumPos) < 0) {
                minimumPos = pointPos;
            }
        }

        return fromPointPos(minimumPos);
    }
}
