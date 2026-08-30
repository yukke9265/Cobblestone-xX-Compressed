package com.yukke9265.cobblestone_xx_compressed.network;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.shield.ShieldProjectorClientState;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 範囲内プレイヤーの HUD に表示するシールド値を同期します。
 */
public record ShieldProjectorHudPayload(
    BlockPos projectorPos,
    long storedShield,
    long maxShield,
    long shieldGenerationRate
) implements CustomPacketPayload {
    public static final Type<ShieldProjectorHudPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "cobblestone_shield_projector_hud")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldProjectorHudPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ShieldProjectorHudPayload::projectorPos,
        ByteBufCodecs.VAR_LONG,
        ShieldProjectorHudPayload::storedShield,
        ByteBufCodecs.VAR_LONG,
        ShieldProjectorHudPayload::maxShield,
        ByteBufCodecs.VAR_LONG,
        ShieldProjectorHudPayload::shieldGenerationRate,
        ShieldProjectorHudPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShieldProjectorHudPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ShieldProjectorClientState.update(
            payload.projectorPos(),
            payload.storedShield(),
            payload.maxShield(),
            payload.shieldGenerationRate()
        ));
    }
}
