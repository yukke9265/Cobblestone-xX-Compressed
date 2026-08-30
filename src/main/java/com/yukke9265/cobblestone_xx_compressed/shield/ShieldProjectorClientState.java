package com.yukke9265.cobblestone_xx_compressed.shield;

import net.minecraft.core.BlockPos;

/**
 * クライアント側で HUD 表示用のシールド値を保持します。
 */
public final class ShieldProjectorClientState {
    private static BlockPos projectorPos;
    private static long storedShield;
    private static long maxShield;
    private static long shieldGenerationRate;
    private static long lastUpdateGameTime;

    private ShieldProjectorClientState() {
    }

    public static void update(BlockPos pos, long stored, long max, long generationRate) {
        projectorPos = pos.immutable();
        storedShield = stored;
        maxShield = max;
        shieldGenerationRate = generationRate;
        lastUpdateGameTime = System.currentTimeMillis();
    }

    public static boolean hasActiveDisplay(long maxAgeMillis) {
        if (projectorPos == null) {
            return false;
        }

        return System.currentTimeMillis() - lastUpdateGameTime <= maxAgeMillis;
    }

    public static BlockPos getProjectorPos() {
        return projectorPos;
    }

    public static long getStoredShield() {
        return storedShield;
    }

    public static long getMaxShield() {
        return maxShield;
    }

    public static long getShieldGenerationRate() {
        return shieldGenerationRate;
    }

    public static void clear() {
        projectorPos = null;
        storedShield = 0L;
        maxShield = 0L;
        shieldGenerationRate = 0L;
        lastUpdateGameTime = 0L;
    }
}
