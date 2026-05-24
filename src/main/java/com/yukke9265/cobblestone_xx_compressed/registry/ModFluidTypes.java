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
    public static final ResourceLocation MOLTEN_DIRTY_COMPRESSED_COBBLESTONE_STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        CobblestonexXCompressed.MODID,
        "block/fluid/molten_dirty_compressed_cobblestone_still"
    );
    public static final ResourceLocation MOLTEN_DIRTY_COMPRESSED_COBBLESTONE_FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        CobblestonexXCompressed.MODID,
        "block/fluid/molten_dirty_compressed_cobblestone_flow"
    );
    public static final ResourceLocation WATER_OVERLAY_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        CobblestonexXCompressed.MODID,
        "block/fluid/water_overlay"
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

    // 水ベースの色付き液体は、texture 名、tint、表示名を 1 か所へまとめておくと
    // 登録、lang、datagen の対応関係を追いやすくなります。
    public enum WaterBasedFluid {
        GLOW_TOPAZ("glow_topaz", 0xFFF1A969, "Glow Topaz"),
        HOT_UNSTABLE_EMERALD("hot_unstable_emerald", 0xFF77CB97, "Hot Unstable Emerald"),
        HOT_UNSTABLE_ENDERITE("hot_unstable_enderite", 0xFF9BADA9, "Hot Unstable Enderite"),
        MOLTEN_ENDER("molten_ender", 0xFF1F695D, "Molten Ender"),
        MOLTEN_NETHERITE("molten_netherite", 0xFF696767, "Molten Netherite"),
        RED_RUBY("red_ruby", 0xFFFF4F4F, "Red Ruby"),
        SHINY_BLAZE("shiny_blaze", 0xFFFFB32F, "Shiny Blaze"),
        SHINY_SAPPHIRE("shiny_sapphire", 0xFF7593EB, "Shiny Sapphire"),
        SHINY_WATER("shiny_water", 0xFF345FDA, "Shiny Water"),
        UNSTABLE_EMERALD("unstable_emerald", 0xFF77CB97, "Unstable Emerald"),
        UNSTABLE_ENDERITE("unstable_enderite", 0xFF9BADA9, "Unstable Enderite");

        private final String registryName;
        private final int tintColor;
        private final String englishDisplayName;
        private DeferredHolder<FluidType, FluidType> fluidType;

        WaterBasedFluid(String registryName, int tintColor, String englishDisplayName) {
            this.registryName = registryName;
            this.tintColor = tintColor;
            this.englishDisplayName = englishDisplayName;
        }

        public String getRegistryName() {
            return this.registryName;
        }

        public int getTintColor() {
            return this.tintColor;
        }

        public String getEnglishDisplayName() {
            return this.englishDisplayName;
        }

        public String getBucketEnglishDisplayName() {
            return this.englishDisplayName + " Bucket";
        }

        public DeferredHolder<FluidType, FluidType> getFluidType() {
            return this.fluidType;
        }

        private void setFluidType(DeferredHolder<FluidType, FluidType> fluidType) {
            this.fluidType = fluidType;
        }
    }

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

    // dirty 版も molten compressed cobblestone と同じ tier 色を使うため、
    // registry 名だけを変えた別 enum を用意して同じ流れで登録します。
    public enum TierMoltenDirtyCompressedCobblestone {
        COPPER("molten_dirty_tier_copper_compressed_cobblestone", COPPER_COMPRESSED_COBBLESTONE_TINT),
        IRON("molten_dirty_tier_iron_compressed_cobblestone", IRON_COMPRESSED_COBBLESTONE_TINT),
        GOLD("molten_dirty_tier_gold_compressed_cobblestone", GOLD_COMPRESSED_COBBLESTONE_TINT),
        AMETHYST("molten_dirty_tier_amethyst_compressed_cobblestone", AMETHYST_COMPRESSED_COBBLESTONE_TINT),
        AQUAMARINE("molten_dirty_tier_aquamarine_compressed_cobblestone", AQUAMARINE_COMPRESSED_COBBLESTONE_TINT),
        TOPAZ("molten_dirty_tier_topaz_compressed_cobblestone", TOPAZ_COMPRESSED_COBBLESTONE_TINT),
        RUBY("molten_dirty_tier_ruby_compressed_cobblestone", RUBY_COMPRESSED_COBBLESTONE_TINT),
        SAPPHIRE("molten_dirty_tier_sapphire_compressed_cobblestone", SAPPHIRE_COMPRESSED_COBBLESTONE_TINT),
        DIAMOND("molten_dirty_tier_diamond_compressed_cobblestone", DIAMOND_COMPRESSED_COBBLESTONE_TINT),
        EMERALD("molten_dirty_tier_emerald_compressed_cobblestone", EMERALD_COMPRESSED_COBBLESTONE_TINT),
        NETHERITE("molten_dirty_tier_netherite_compressed_cobblestone", NETHERITE_COMPRESSED_COBBLESTONE_TINT),
        OBSIDIAN("molten_dirty_tier_obsidian_compressed_cobblestone", OBSIDIAN_COMPRESSED_COBBLESTONE_TINT);

        private final String registryName;
        private final int tintColor;
        private DeferredHolder<FluidType, FluidType> fluidType;

        TierMoltenDirtyCompressedCobblestone(String registryName, int tintColor) {
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
            return "Molten Dirty " + ModBlocks.TierCompressedCobblestone.valueOf(this.name()).getEnglishDisplayName();
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
        BASE_COMPRESSED_COBBLESTONE_TINT,
        MOLTEN_COMPRESSED_COBBLESTONE_STILL_TEXTURE,
        MOLTEN_COMPRESSED_COBBLESTONE_FLOWING_TEXTURE
    );
    public static final DeferredHolder<FluidType, FluidType> MOLTEN_DIRTY_COMPRESSED_COBBLESTONE = registerMoltenFluidType(
        "molten_dirty_compressed_cobblestone",
        BASE_COMPRESSED_COBBLESTONE_TINT,
        MOLTEN_DIRTY_COMPRESSED_COBBLESTONE_STILL_TEXTURE,
        MOLTEN_DIRTY_COMPRESSED_COBBLESTONE_FLOWING_TEXTURE
    );

    static {
        for (TierMoltenCompressedCobblestone tier : TierMoltenCompressedCobblestone.values()) {
            tier.setFluidType(registerMoltenFluidType(
                tier.getRegistryName(),
                tier.getTintColor(),
                MOLTEN_COMPRESSED_COBBLESTONE_STILL_TEXTURE,
                MOLTEN_COMPRESSED_COBBLESTONE_FLOWING_TEXTURE
            ));
        }

        for (TierMoltenDirtyCompressedCobblestone tier : TierMoltenDirtyCompressedCobblestone.values()) {
            tier.setFluidType(registerMoltenFluidType(
                tier.getRegistryName(),
                tier.getTintColor(),
                MOLTEN_DIRTY_COMPRESSED_COBBLESTONE_STILL_TEXTURE,
                MOLTEN_DIRTY_COMPRESSED_COBBLESTONE_FLOWING_TEXTURE
            ));
        }

        for (WaterBasedFluid fluid : WaterBasedFluid.values()) {
            fluid.setFluidType(registerWaterBasedFluidType(fluid.getRegistryName(), fluid.getTintColor()));
        }
    }

    private ModFluidTypes() {
    }

    private static DeferredHolder<FluidType, FluidType> registerMoltenFluidType(
        String name,
        int tintColor,
        ResourceLocation stillTexture,
        ResourceLocation flowingTexture
    ) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, name);
        return registerColoredFluidType(
            name,
            tintColor,
            stillTexture,
            flowingTexture,
            null,
            createMoltenProperties(resourceLocation)
        );
    }

    private static DeferredHolder<FluidType, FluidType> registerWaterBasedFluidType(String name, int tintColor) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, name);

        return registerColoredFluidType(
            name,
            tintColor,
            getStillTextureLocation(name),
            getFlowingTextureLocation(name),
            WATER_OVERLAY_TEXTURE,
            createWaterBasedProperties(resourceLocation)
        );
    }

    private static DeferredHolder<FluidType, FluidType> registerColoredFluidType(
        String name,
        int tintColor,
        ResourceLocation stillTexture,
        ResourceLocation flowingTexture,
        ResourceLocation overlayTexture,
        FluidType.Properties properties
    ) {
        return FLUID_TYPES.register(name, resourceLocation -> new ColoredFluidType(properties, tintColor, stillTexture, flowingTexture, overlayTexture));
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

    private static FluidType.Properties createWaterBasedProperties(ResourceLocation resourceLocation) {
        return FluidType.Properties.create()
            .descriptionId(Util.makeDescriptionId("fluid_type", resourceLocation))
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .density(1_000)
            .viscosity(1_000)
            .temperature(300)
            .canExtinguish(true)
            .fallDistanceModifier(0.0F)
            .canHydrate(true)
            .motionScale(0.014D)
            .supportsBoating(true)
            .rarity(net.minecraft.world.item.Rarity.COMMON)
            .adjacentPathType(null)
            .pathType(null);
    }

    public static MapColor getMoltenCompressedCobblestoneMapColor() {
        return MapColor.COLOR_LIGHT_GRAY;
    }

    public static MapColor getWaterBasedFluidMapColor() {
        return MapColor.COLOR_LIGHT_BLUE;
    }

    public static ResourceLocation getStillTextureLocation(String fluidName) {
        return ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "block/fluid/" + fluidName + "_still");
    }

    public static ResourceLocation getFlowingTextureLocation(String fluidName) {
        return ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "block/fluid/" + fluidName + "_flow");
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        registerClientExtension(event, MOLTEN_COMPRESSED_COBBLESTONE.get(), "molten_compressed_cobblestone");
        registerClientExtension(event, MOLTEN_DIRTY_COMPRESSED_COBBLESTONE.get(), "molten_dirty_compressed_cobblestone");

        for (TierMoltenCompressedCobblestone tier : TierMoltenCompressedCobblestone.values()) {
            registerClientExtension(event, tier.getFluidType().get(), tier.getRegistryName());
        }

        for (TierMoltenDirtyCompressedCobblestone tier : TierMoltenDirtyCompressedCobblestone.values()) {
            registerClientExtension(event, tier.getFluidType().get(), tier.getRegistryName());
        }

        for (WaterBasedFluid fluid : WaterBasedFluid.values()) {
            registerClientExtension(event, fluid.getFluidType().get(), fluid.getRegistryName());
        }
    }

    private static void registerClientExtension(RegisterClientExtensionsEvent event, FluidType fluidType, String registryName) {
        if (!(fluidType instanceof ColoredFluidType coloredFluidType)) {
            return;
        }

        CobblestonexXCompressed.LOGGER.info(
            "Register fluid client tint: {} -> world={}",
            registryName,
            String.format("0x%08X", coloredFluidType.tintColor)
        );

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return coloredFluidType.stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return coloredFluidType.flowingTexture;
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return coloredFluidType.overlayTexture;
            }

            @Override
            public int getTintColor() {
                return coloredFluidType.tintColor;
            }

            @Override
            public int getTintColor(@Nonnull FluidState fluidState, @Nonnull BlockAndTintGetter getter, @Nonnull BlockPos position) {
                return coloredFluidType.tintColor;
            }

            @Override
            public int getTintColor(@Nonnull FluidStack fluidStack) {
                return coloredFluidType.tintColor;
            }
        }, fluidType);
    }

    private static class ColoredFluidType extends FluidType {
        private final ResourceLocation stillTexture;
        private final ResourceLocation flowingTexture;
        private final ResourceLocation overlayTexture;
        private final int tintColor;

        private ColoredFluidType(
            Properties properties,
            int tintColor,
            ResourceLocation stillTexture,
            ResourceLocation flowingTexture,
            ResourceLocation overlayTexture
        ) {
            super(properties);
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
            this.overlayTexture = overlayTexture;
            this.tintColor = tintColor;
        }
    }
}