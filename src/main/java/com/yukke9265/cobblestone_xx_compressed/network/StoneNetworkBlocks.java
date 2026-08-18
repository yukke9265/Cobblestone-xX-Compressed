package com.yukke9265.cobblestone_xx_compressed.network;

import com.yukke9265.cobblestone_xx_compressed.block.StoneNetworkPointBlock;
import com.yukke9265.cobblestone_xx_compressed.block.StoneNetworkRelayBlock;

import net.minecraft.world.level.block.state.BlockState;

/**
 * 石ネットワークの導体判定です。
 *
 * ポイントもリレーも同じ導線として扱います。capability の終点にはしません。
 */
public final class StoneNetworkBlocks {
    private StoneNetworkBlocks() {
    }

    public static boolean isConductor(BlockState state) {
        return isPoint(state) || isRelay(state);
    }

    public static boolean isPoint(BlockState state) {
        return state.getBlock() instanceof StoneNetworkPointBlock;
    }

    public static boolean isRelay(BlockState state) {
        return state.getBlock() instanceof StoneNetworkRelayBlock;
    }
}
