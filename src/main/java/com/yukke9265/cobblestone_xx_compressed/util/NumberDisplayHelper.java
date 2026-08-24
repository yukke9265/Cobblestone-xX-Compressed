package com.yukke9265.cobblestone_xx_compressed.util;

import java.util.Locale;

/**
 * GUI / JEI 向けの大きな数値を、1000 進の K/M/G/T 表記にまとめるヘルパーです。
 * 前提: 内部値は long のまま扱い、表示だけを短くします。
 * 結果: 999 以下はそのまま、1000 以上は 1.5K や 13.42G のようになります。
 */
public final class NumberDisplayHelper {
    private static final String[] SUFFIXES = { "", "K", "M", "G", "T", "P", "E" };
    private static final double UNIT = 1000.0d;

    private NumberDisplayHelper() {
    }

    public static String format(long value) {
        if (value < 0L) {
            if (value == Long.MIN_VALUE) {
                return "-" + formatPositive(Long.MAX_VALUE);
            }
            return "-" + formatPositive(-value);
        }
        return formatPositive(value);
    }

    public static String formatRange(long current, long max) {
        return format(current) + " / " + format(max);
    }

    public static String formatCp(long value) {
        return format(value) + " CP";
    }

    public static String formatCpPerTick(long value) {
        return format(value) + " CP/t";
    }

    public static String formatTotalCp(long value) {
        return format(value) + " total CP";
    }

    public static String formatCpRange(long current, long max) {
        return formatRange(current, max);
    }

    public static String formatFe(long value) {
        return format(value) + " FE";
    }

    public static String formatFePerTick(long value) {
        return format(value) + " FE/t";
    }

    public static String formatFeRange(long current, long max) {
        return formatRange(current, max);
    }

    public static String formatMillibuckets(long value) {
        return format(value) + " mB";
    }

    public static String formatMillibucketsPerTick(long value) {
        return format(value) + " mB/t";
    }

    public static String formatMillibucketsRange(long current, long max) {
        return formatRange(current, max) + " mB";
    }

    private static String formatPositive(long value) {
        if (value < 1000L) {
            return Long.toString(value);
        }

        double scaled = value;
        int suffixIndex = 0;
        while (scaled >= UNIT && suffixIndex < SUFFIXES.length - 1) {
            scaled /= UNIT;
            suffixIndex++;
        }

        String mantissa = formatMantissa(scaled);
        if ("1000".equals(mantissa) && suffixIndex < SUFFIXES.length - 1) {
            scaled /= UNIT;
            suffixIndex++;
            mantissa = formatMantissa(scaled);
        }

        return mantissa + SUFFIXES[suffixIndex];
    }

    // 目的: 小数は最大 2 桁まで残し、末尾の 0 は落とす。
    private static String formatMantissa(double value) {
        String text = String.format(Locale.ROOT, "%.2f", value);
        int dotIndex = text.indexOf('.');
        if (dotIndex < 0) {
            return text;
        }

        int endIndex = text.length() - 1;
        while (endIndex > dotIndex && text.charAt(endIndex) == '0') {
            endIndex--;
        }
        if (endIndex == dotIndex) {
            return text.substring(0, dotIndex);
        }
        return text.substring(0, endIndex + 1);
    }
}
