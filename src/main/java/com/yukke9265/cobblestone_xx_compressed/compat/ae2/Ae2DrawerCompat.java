package com.yukke9265.cobblestone_xx_compressed.compat.ae2;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * AE2 連携の入口です。
 * <p>
 * 注意: Storage Bus は {@code ME_STORAGE} を IItemHandler より優先します。
 * ドロワー向けに ME_STORAGE を返すと、実機では中身が 0 件扱いになる事例があったため、
 * 現状は IItemHandler（long 量を Integer.MAX_VALUE 単位で公開）経路だけを使います。
 */
public final class Ae2DrawerCompat {
    private static final String AE2_MOD_ID = "ae2";
    private static final Logger LOGGER = LogUtils.getLogger();

    private Ae2DrawerCompat() {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (!ModList.get().isLoaded(AE2_MOD_ID)) {
            return;
        }

        // ME_STORAGE は登録しない。Storage Bus には ItemHandler 経路で全量を見せる。
        LOGGER.info(
            "AE2 detected: Cobblestone Drawer exposes full contents via IItemHandler "
                + "(ME_STORAGE intentionally not registered)"
        );
    }
}
