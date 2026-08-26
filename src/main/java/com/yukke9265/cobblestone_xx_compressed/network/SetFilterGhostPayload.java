package com.yukke9265.cobblestone_xx_compressed.network;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.menu.BaseMenu;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/*
 * 方針:
 * JEI からフィルタ ghost へドロップした内容をサーバへ伝えます。
 * アイテムはそのまま、バケツ無し流体は fluidId 文字列で送ります。
 */
public record SetFilterGhostPayload(
    int containerId,
    int ghostIndex,
    ItemStack itemStack,
    String fluidId
) implements CustomPacketPayload {
    public static final Type<SetFilterGhostPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "set_filter_ghost")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SetFilterGhostPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        SetFilterGhostPayload::containerId,
        ByteBufCodecs.VAR_INT,
        SetFilterGhostPayload::ghostIndex,
        ItemStack.OPTIONAL_STREAM_CODEC,
        SetFilterGhostPayload::itemStack,
        ByteBufCodecs.STRING_UTF8,
        SetFilterGhostPayload::fluidId,
        SetFilterGhostPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetFilterGhostPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player.containerMenu instanceof BaseMenu menu)) {
                return;
            }
            if (menu.containerId != payload.containerId()) {
                return;
            }

            if (!payload.fluidId().isEmpty()) {
                Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(payload.fluidId()));
                if (fluid == null || fluid == Fluids.EMPTY) {
                    return;
                }
                menu.applySlotFilterGhostFluid(payload.ghostIndex(), new FluidStack(fluid, 1));
                return;
            }

            menu.applySlotFilterGhostItem(payload.ghostIndex(), payload.itemStack());
        });
    }
}
