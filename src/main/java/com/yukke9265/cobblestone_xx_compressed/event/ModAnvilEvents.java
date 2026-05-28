package com.yukke9265.cobblestone_xx_compressed.event;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.util.FortuneEnchantedBookHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

// 高レベル Fortune 本は、バニラの最大レベル判定で金床候補から外れてしまいます。
// そのため AnvilUpdateEvent で結果を手動構築し、通常の金床操作と同じ流れで取り出せるようにします。
@SuppressWarnings({ "null", "deprecation" })
@EventBusSubscriber(modid = CobblestonexXCompressed.MODID)
public final class ModAnvilEvents {
    private static final int VANILLA_MAX_FORTUNE_LEVEL = FortuneEnchantedBookHelper.FIRST_FORTUNE_BOOK_LEVEL - 1;

    private ModAnvilEvents() {
    }

    @SubscribeEvent
    static void onAnvilUpdate(@Nonnull AnvilUpdateEvent event) {
        ItemStack leftStack = event.getLeft();
        ItemStack rightStack = event.getRight();

        if (leftStack.isEmpty() || rightStack.isEmpty() || !rightStack.is(Items.ENCHANTED_BOOK)) {
            return;
        }

        HolderLookup.Provider holderLookup = event.getPlayer().registryAccess();
        Holder<Enchantment> fortune = FortuneEnchantedBookHelper.getFortuneEnchantment(holderLookup);
        ItemEnchantments rightEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(rightStack);
        int targetFortuneLevel = rightEnchantments.getLevel(fortune);

        // バニラ範囲内は既存の挙動に任せます。
        if (targetFortuneLevel <= VANILLA_MAX_FORTUNE_LEVEL) {
            return;
        }

        // isBookEnchantable は「この本をそのまま金床へ載せられるか」も見るため、
        // 高レベル本だとここでバニラ上限に引っかかる可能性があります。
        // ここでは「Fortune を本来付けられる道具か」を直接確認します。
        boolean canEnchant = leftStack.is(fortune.value().getSupportedItems());
        boolean isPrimaryItem = leftStack.isPrimaryItemFor(fortune);
        if (!canEnchant || !isPrimaryItem) {
            return;
        }

        int currentFortuneLevel = leftStack.getEnchantmentLevel(fortune);
        if (currentFortuneLevel >= targetFortuneLevel) {
            return;
        }

        // 既存エンチャントとの互換判定が必要になった段階でだけ lookup を取ります。
        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = holderLookup.lookupOrThrow(Registries.ENCHANTMENT);
        if (!canApplyFortuneWithCurrentEnchantments(leftStack, enchantmentLookup, fortune)) {
            return;
        }

        ItemStack outputStack = leftStack.copy();

        // 既存エンチャは維持したまま、Fortune だけを目的の高レベルへ更新します。
        EnchantmentHelper.updateEnchantments(outputStack, enchantments -> enchantments.set(fortune, targetFortuneLevel));

        int renameCost = applyRequestedName(event, outputStack);
        long levelCost = calculateLevelCost(event, currentFortuneLevel, targetFortuneLevel, fortune, renameCost);

        event.setOutput(outputStack);
        event.setMaterialCost(1);
        event.setCost(levelCost);
    }

    private static boolean canApplyFortuneWithCurrentEnchantments(ItemStack stack,
                                                                  HolderLookup.RegistryLookup<Enchantment> enchantmentLookup,
                                                                  Holder<Enchantment> fortune) {
        ItemEnchantments currentEnchantments = stack.getAllEnchantments(enchantmentLookup);

        for (Object2IntMap.Entry<Holder<Enchantment>> enchantmentEntry : currentEnchantments.entrySet()) {
            Holder<Enchantment> existingEnchantment = enchantmentEntry.getKey();

            if (existingEnchantment.equals(fortune)) {
                continue;
            }

            // Silk Touch など、Fortune と共存できないエンチャントが残っている場合は
            // バニラと同じく不許可にして、意図しない組み合わせを作らないようにします。
            if (!Enchantment.areCompatible(existingEnchantment, fortune)) {
                return false;
            }
        }

        return true;
    }

    private static int applyRequestedName(AnvilUpdateEvent event, ItemStack outputStack) {
        String requestedName = event.getName();
        if (requestedName == null) {
            return 0;
        }

        if (requestedName.isBlank()) {
            if (outputStack.has(DataComponents.CUSTOM_NAME)) {
                outputStack.remove(DataComponents.CUSTOM_NAME);
                return 1;
            }

            return 0;
        }

        if (!requestedName.equals(outputStack.getHoverName().getString())) {
            outputStack.set(DataComponents.CUSTOM_NAME, Component.literal(requestedName));
            return 1;
        }

        return 0;
    }

    private static long calculateLevelCost(AnvilUpdateEvent event,
                                           int currentFortuneLevel,
                                           int targetFortuneLevel,
                                           Holder<Enchantment> fortune,
                                           int renameCost) {
        int addedFortuneLevels = targetFortuneLevel - currentFortuneLevel;
        int enchantmentCost = addedFortuneLevels * fortune.value().getAnvilCost();

        // 既存の repair cost を土台にしつつ、高レベル化した分だけ追加コストを載せます。
        // 0 だと取り出せないケースが分かりにくくなるため、最低 1 は確保します。
        return Math.max(1L, event.getCost() + enchantmentCost + renameCost);
    }
}