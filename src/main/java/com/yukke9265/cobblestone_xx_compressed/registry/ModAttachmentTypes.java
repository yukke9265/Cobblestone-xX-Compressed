package com.yukke9265.cobblestone_xx_compressed.registry;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.armor.CompressedCobblestoneArmorState;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * プレイヤー等へ付ける Attachment 登録置き場です。
 */
public final class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CobblestonexXCompressed.MODID);

    public static final Supplier<AttachmentType<CompressedCobblestoneArmorState>> COMPRESSED_COBBLESTONE_ARMOR_STATE =
        ATTACHMENT_TYPES.register(
            "compressed_cobblestone_armor_state",
            () -> AttachmentType.builder(CompressedCobblestoneArmorState::empty).build()
        );

    private ModAttachmentTypes() {
    }
}
