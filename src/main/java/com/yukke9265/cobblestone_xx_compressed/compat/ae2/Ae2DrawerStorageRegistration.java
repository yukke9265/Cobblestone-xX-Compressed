package com.yukke9265.cobblestone_xx_compressed.compat.ae2;

import com.yukke9265.cobblestone_xx_compressed.registry.ModBlockEntities;

import appeng.api.AECapabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * AE2 導入時だけ読み込む登録処理です。
 * ストレージバスは ME_STORAGE を優先するため、ここで全量を long として公開します。
 */
public final class Ae2DrawerStorageRegistration {
    private Ae2DrawerStorageRegistration() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            AECapabilities.ME_STORAGE,
            ModBlockEntities.COBBLESTONE_DRAWER_BLOCK_ENTITY.get(),
            CobblestoneDrawerMeStorage::create
        );
    }
}
