package com.yukke9265.cobblestone_xx_compressed.registry;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nonnull;

public class ModFluidTypes {
    public static final ResourceLocation MOLTEN_COMPRESSED_COBBLESTONE_STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        CobblestonexXCompressed.MODID,
        "block/fluid/molten_compressed_cobblestone_still"
    );
    public static final ResourceLocation MOLTEN_COMPRESSED_COBBLESTONE_FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        CobblestonexXCompressed.MODID,
        "block/fluid/molten_compressed_cobblestone_flow"
    );

    public static final int BASE_COMPRESSED_COBBLESTONE_TINT = 0xFFB3B1AF;
    public static final int COPPER_COMPRESSED_COBBLESTONE_TINT = 0xFFC78357;
    public static final int IRON_COMPRESSED_COBBLESTONE_TINT = 0xFFC7CDD6;
    public static final int GOLD_COMPRESSED_COBBLESTONE_TINT = 0xFFF2C94C;
    public static final int AMETHYST_COMPRESSED_COBBLESTONE_TINT = 0xFFB07CFF;
    public static final int AQUAMARINE_COMPRESSED_COBBLESTONE_TINT = 0xFF66D6C3;
    public static final int TOPAZ_COMPRESSED_COBBLESTONE_TINT = 0xFFFFC857;
    public static final int RUBY_COMPRESSED_COBBLESTONE_TINT = 0xFFE25555;
    public static final int SAPPHIRE_COMPRESSED_COBBLESTONE_TINT = 0xFF5B7CFF;
    public static final int DIAMOND_COMPRESSED_COBBLESTONE_TINT = 0xFF9EE7FF;
    public static final int EMERALD_COMPRESSED_COBBLESTONE_TINT = 0xFF48C774;
    public static final int NETHERITE_COMPRESSED_COBBLESTONE_TINT = 0xFF6C5A64;
    public static final int OBSIDIAN_COMPRESSED_COBBLESTONE_TINT = 0xFF4B3B63;

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, CobblestonexXCompressed.MODID);

    // tier 版 molten fluid は block/item/datagen 側でも同じ順番で回したいため、
    // registry 名と tint 色をここへまとめます。
    public enum TierMoltenCompressedCobblestone {
        COPPER("molten_tier_copper_compressed_cobblestone", COPPER_COMPRESSED_COBBLESTONE_TINT),
        IRON("molten_tier_iron_compressed_cobblestone", IRON_COMPRESSED_COBBLESTONE_TINT),
        GOLD("molten_tier_gold_compressed_cobblestone", GOLD_COMPRESSED_COBBLESTONE_TINT),
        AMETHYST("molten_tier_amethyst_compressed_cobblestone", AMETHYST_COMPRESSED_COBBLESTONE_TINT),
        AQUAMARINE("molten_tier_aquamarine_compressed_cobblestone", AQUAMARINE_COMPRESSED_COBBLESTONE_TINT),
        TOPAZ("molten_tier_topaz_compressed_cobblestone", TOPAZ_COMPRESSED_COBBLESTONE_TINT),
        RUBY("molten_tier_ruby_compressed_cobblestone", RUBY_COMPRESSED_COBBLESTONE_TINT),
        SAPPHIRE("molten_tier_sapphire_compressed_cobblestone", SAPPHIRE_COMPRESSED_COBBLESTONE_TINT),
        DIAMOND("molten_tier_diamond_compressed_cobblestone", DIAMOND_COMPRESSED_COBBLESTONE_TINT),
        EMERALD("molten_tier_emerald_compressed_cobblestone", EMERALD_COMPRESSED_COBBLESTONE_TINT),
        NETHERITE("molten_tier_netherite_compressed_cobblestone", NETHERITE_COMPRESSED_COBBLESTONE_TINT),
        OBSIDIAN("molten_tier_obsidian_compressed_cobblestone", OBSIDIAN_COMPRESSED_COBBLESTONE_TINT);

        private final String registryName;
        private final int tintColor;
        private DeferredHolder<FluidType, FluidType> fluidType;

        TierMoltenCompressedCobblestone(String registryName, int tintColor) {
            this.registryName = registryName;
            this.tintColor = tintColor;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public int getTintColor() {
            return this.tintColor;
        }

        public String getEnglishDisplayName() {
            return "Molten " + ModBlocks.TierCompressedCobblestone.valueOf(this.name()).getEnglishDisplayName();
        }

        public String getBucketEnglishDisplayName() {
            return this.getEnglishDisplayName() + " Bucket";
        }

        public DeferredHolder<FluidType, FluidType> getFluidType() {
            return this.fluidType;
        }

        private void setFluidType(DeferredHolder<FluidType, FluidType> fluidType) {
            this.fluidType = fluidType;
        }
    }

    // molten compressed cobblestone は今後 tier 版を増やす予定があるため、
    // 通常版も tier 版も共通 helper で増やせる形にしておきます。
    public static final DeferredHolder<FluidType, FluidType> MOLTEN_COMPRESSED_COBBLESTONE = registerMoltenFluidType(
        "molten_compressed_cobblestone",
        BASE_COMPRESSED_COBBLESTONE_TINT
    );

    static {
        for (TierMoltenCompressedCobblestone tier : TierMoltenCompressedCobblestone.values()) {
            tier.setFluidType(registerMoltenFluidType(tier.getRegistryName(), tier.getTintColor()));
        }
    }

    private ModFluidTypes() {
    }

    private static DeferredHolder<FluidType, FluidType> registerMoltenFluidType(String name, int tintColor) {
        return FLUID_TYPES.register(name, resourceLocation -> new MoltenFluidType(createMoltenProperties(resourceLocation), tintColor));
    }

    private static FluidType.Properties createMoltenProperties(ResourceLocation resourceLocation) {
        return FluidType.Properties.create()
            .descriptionId(Util.makeDescriptionId("fluid_type", resourceLocation))
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
            .density(2_000)
            .viscosity(6_000)
            .temperature(1_300)
            .lightLevel(10)
            .canExtinguish(false)
            .fallDistanceModifier(0.0F)
            .canHydrate(false)
            .motionScale(0.0023333333333333335D)
            .supportsBoating(false)
            .rarity(net.minecraft.world.item.Rarity.UNCOMMON)
            .adjacentPathType(null)
            .pathType(null);
    }

    public static MapColor getMoltenCompressedCobblestoneMapColor() {
        return MapColor.COLOR_LIGHT_GRAY;
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        registerClientExtension(event, MOLTEN_COMPRESSED_COBBLESTONE.get(), "molten_compressed_cobblestone");

        for (TierMoltenCompressedCobblestone tier : TierMoltenCompressedCobblestone.values()) {
            registerClientExtension(event, tier.getFluidType().get(), tier.getRegistryName());
        }
    }

    private static void registerClientExtension(RegisterClientExtensionsEvent event, FluidType fluidType, String registryName) {
        if (!(fluidType instanceof MoltenFluidType moltenFluidType)) {
            return;
        }

        CobblestonexXCompressed.LOGGER.info(
            "Register molten fluid client tint: {} -> world={}",
            registryName,
            String.format("0x%08X", moltenFluidType.tintColor)
        );

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return moltenFluidType.stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return moltenFluidType.flowingTexture;
            }

            @Override
            public int getTintColor() {
                return moltenFluidType.tintColor;
            }

            @Override
            public int getTintColor(@Nonnull FluidState fluidState, @Nonnull BlockAndTintGetter getter, @Nonnull BlockPos position) {
                return moltenFluidType.tintColor;
            }

            @Override
            public int getTintColor(@Nonnull FluidStack fluidStack) {
                return moltenFluidType.tintColor;
            }
        }, fluidType);
    }

    private static class MoltenFluidType extends FluidType {
        private final ResourceLocation stillTexture;
        private final ResourceLocation flowingTexture;
        private final int tintColor;

        private MoltenFluidType(Properties properties, int tintColor) {
            super(properties);
            this.stillTexture = MOLTEN_COMPRESSED_COBBLESTONE_STILL_TEXTURE;
            this.flowingTexture = MOLTEN_COMPRESSED_COBBLESTONE_FLOWING_TEXTURE;
            this.tintColor = tintColor;
        }
    }
}