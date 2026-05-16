package com.yukke9265.cobblestone_xx_compressed.util;

public final class LongDataHelper {
    private LongDataHelper() {
    }

    public static int lowerInt(long value) {
        return (int) (value & 0xFFFFFFFFL);
    }

    public static int upperInt(long value) {
        return (int) (value >>> 32);
    }

    public static long toLong(int lower, int upper) {
        return (lower & 0xFFFFFFFFL) | ((long) upper << 32);
    }
}