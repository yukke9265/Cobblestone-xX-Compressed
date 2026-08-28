package com.yukke9265.cobblestone_xx_compressed.item;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.compat.accessory.AccessoryInventoryCompat;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CobblestonexXCompressed.MODID)
public final class FlyingStoneFlightHandler {
    private static final ResourceLocation FLIGHT_MODIFIER_ID =
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "flying_stone_flight");
    private static final AttributeModifier FLIGHT_MODIFIER =
        new AttributeModifier(FLIGHT_MODIFIER_ID, 1.0, Operation.ADD_VALUE);

    private FlyingStoneFlightHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player instanceof FakePlayer) {
            return;
        }

        // 他プレイヤーのインベントリはクライアントへ全部は来ないので、
        // 見た目用の属性を足し引きすると飛行状態がずれます。
        if (player.level().isClientSide() && !player.isLocalPlayer()) {
            return;
        }

        updateFlight(player, hasFlyingStone(player));
    }

    // バニラのインベントリ（メイン・防具・オフハンド）に加えて、
    // Curios / Accessories の装飾品スロットも見ます。
    private static boolean hasFlyingStone(Player player) {
        Item flyingStone = ModItems.FLYING_STONE.get();
        if (player.getInventory().hasAnyMatching(stack -> stack.is(flyingStone))) {
            return true;
        }

        return AccessoryInventoryCompat.hasItem(player, flyingStone);
    }

    // NeoForge の CREATIVE_FLIGHT を足すと、サバイバルでもクリエ飛行が使えます。
    // 外したときはこの修飾子だけ外すので、クリエ本体の飛行は残ります。
    private static void updateFlight(Player player, boolean shouldFly) {
        AttributeInstance flight = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (flight == null) {
            return;
        }

        boolean hasModifier = flight.hasModifier(FLIGHT_MODIFIER_ID);
        if (shouldFly && !hasModifier) {
            flight.addTransientModifier(FLIGHT_MODIFIER);
        } else if (!shouldFly && hasModifier) {
            flight.removeModifier(FLIGHT_MODIFIER_ID);
        }
    }
}
