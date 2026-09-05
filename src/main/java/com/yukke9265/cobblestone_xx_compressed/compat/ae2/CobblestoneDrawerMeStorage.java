package com.yukke9265.cobblestone_xx_compressed.compat.ae2;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationMode;
import com.yukke9265.cobblestone_xx_compressed.blockentity.AutomationSide;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneDrawerBlockEntity;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Cobblestone Drawer の内部 long ストレージを AE2 の MEStorage として公開します。
 * ストレージバスは ME_STORAGE を IItemHandler より優先するため、ここで全量を long 報告します。
 * パイプ用の面設定が DISABLED でも、バス接続自体を意図した操作とみなして公開します。
 */
public final class CobblestoneDrawerMeStorage implements MEStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean loggedCreation;

    private final CobblestoneDrawerBlockEntity drawer;
    private final boolean canInsert;
    private final boolean canExtract;

    private CobblestoneDrawerMeStorage(
        CobblestoneDrawerBlockEntity drawer,
        boolean canInsert,
        boolean canExtract
    ) {
        this.drawer = drawer;
        this.canInsert = canInsert;
        this.canExtract = canExtract;
    }

    @Nullable
    public static MEStorage create(CobblestoneDrawerBlockEntity drawer, @Nullable Direction side) {
        boolean canInsert = true;
        boolean canExtract = true;

        if (side != null) {
            AutomationMode automationMode = drawer.getAutomationMode(
                AutomationSide.fromWorldSide(side, drawer.getBlockState())
            );
            // DISABLED でもストレージバスからは全量アクセス可能にする。
            // パイプ向け IItemHandler は別途 Empty を返すので、自動化の面設定は維持される。
            if (automationMode == AutomationMode.INPUT) {
                canInsert = true;
                canExtract = false;
            } else if (automationMode == AutomationMode.OUTPUT) {
                canInsert = false;
                canExtract = true;
            } else if (automationMode == AutomationMode.IN_OUT || automationMode == AutomationMode.DISABLED) {
                canInsert = true;
                canExtract = true;
            } else {
                canInsert = true;
                canExtract = true;
            }
        }

        if (!loggedCreation) {
            loggedCreation = true;
            LOGGER.info("Cobblestone Drawer ME_STORAGE is available for AE2 Storage Bus");
        }

        return new CobblestoneDrawerMeStorage(drawer, canInsert, canExtract);
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        if (!(what instanceof AEItemKey itemKey)) {
            return false;
        }

        ItemStack storedItem = this.drawer.getStoredItem();
        return !storedItem.isEmpty()
            && this.drawer.getStoredAmount() > 0L
            && itemKey.matches(storedItem);
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        MEStorage.checkPreconditions(what, amount, mode, source);
        if (!this.canInsert || amount <= 0L || !(what instanceof AEItemKey itemKey)) {
            return 0L;
        }

        return this.drawer.insertStoredItems(itemKey.toStack(), amount, mode.isSimulate());
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        MEStorage.checkPreconditions(what, amount, mode, source);
        if (!this.canExtract || amount <= 0L || !(what instanceof AEItemKey itemKey)) {
            return 0L;
        }

        return this.drawer.extractStoredItems(itemKey.toStack(), amount, mode.isSimulate());
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        ItemStack storedItem = this.drawer.getStoredItem();
        long storedAmount = this.drawer.getStoredAmount();
        if (storedItem.isEmpty() || storedAmount <= 0L) {
            return;
        }

        AEItemKey key = AEItemKey.of(storedItem);
        if (key != null) {
            out.add(key, storedAmount);
        }
    }

    @Override
    public Component getDescription() {
        return this.drawer.getDisplayName();
    }
}
