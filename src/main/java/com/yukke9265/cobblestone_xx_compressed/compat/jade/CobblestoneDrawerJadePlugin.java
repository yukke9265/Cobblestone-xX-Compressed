package com.yukke9265.cobblestone_xx_compressed.compat.jade;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneDrawerBlockEntity;

import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Cobblestone Drawer の格納量を Jade のアイテム表示へ正しく載せるプラグインです。
 * IItemHandler の仮想スロットではなく、内部 long 量を使います。
 */
@WailaPlugin
public class CobblestoneDrawerJadePlugin implements IWailaPlugin {
    public static final ResourceLocation DRAWER_STORAGE =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_drawer_storage");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerItemStorage(
            CobblestoneDrawerJadeStorageProvider.INSTANCE,
            CobblestoneDrawerBlockEntity.class
        );
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerItemStorageClient(CobblestoneDrawerJadeStorageProvider.INSTANCE);
    }
}
