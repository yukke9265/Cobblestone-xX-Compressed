package com.yukke9265.cobblestone_xx_compressed.registry;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, CobblestonexXCompressed.MODID);
    public static final DeferredRegister.Blocks FLUID_BLOCKS = DeferredRegister.createBlocks(CobblestonexXCompressed.MODID);
    public static final DeferredRegister.Items FLUID_ITEMS = DeferredRegister.createItems(CobblestonexXCompressed.MODID);

    // 水系液体も molten 系と同じように enum で管理しておくと、
    // creative tab、lang、model、client render の順番をそろえやすくなります。
    public enum WaterBasedFluid {
        GLOW_TOPAZ,
        HOT_LAVA,
        HOT_UNSTABLE_EMERALD,
        HOT_UNSTABLE_ENDERITE,
        MOLTEN_ENDER,
        MOLTEN_NETHERITE,
        RED_RUBY,
        SHINY_BLAZE,
        SHINY_SAPPHIRE,
        SHINY_WATER,
        UNSTABLE_EMERALD,
        UNSTABLE_ENDERITE;

        private final ModFluidTypes.WaterBasedFluid fluidTypeTier;
        private FluidEntry fluidEntry;

        WaterBasedFluid() {
            this.fluidTypeTier = ModFluidTypes.WaterBasedFluid.valueOf(this.name());
        }

        public String getRegistryName() {
            return this.fluidTypeTier.getRegistryName();
        }

        public String getEnglishDisplayName() {
            return this.fluidTypeTier.getEnglishDisplayName();
        }

        public String getBucketEnglishDisplayName() {
            return this.fluidTypeTier.getBucketEnglishDisplayName();
        }

        public int getTintColor() {
            return this.fluidTypeTier.getTintColor();
        }

        public FluidEntry getFluidEntry() {
            return this.fluidEntry;
        }

        private void setFluidEntry(FluidEntry fluidEntry) {
            this.fluidEntry = fluidEntry;
        }

        public net.neoforged.neoforge.registries.DeferredHolder<net.neoforged.neoforge.fluids.FluidType, net.neoforged.neoforge.fluids.FluidType> getFluidType() {
            return this.fluidTypeTier.getFluidType();
        }
    }

    // tier molten fluid も block や item と同じように enum で持っておくと、
    // creative tab、lang、model で同じ順序を使い回せます。
    public enum TierMoltenCompressedCobblestone {
        COPPER,
        IRON,
        GOLD,
        AMETHYST,
        AQUAMARINE,
        TOPAZ,
        RUBY,
        SAPPHIRE,
        DIAMOND,
        EMERALD,
        NETHERITE,
        OBSIDIAN;

        private final ModFluidTypes.TierMoltenCompressedCobblestone fluidTypeTier;
        private MoltenFluidEntry fluidEntry;

        TierMoltenCompressedCobblestone() {
            this.fluidTypeTier = ModFluidTypes.TierMoltenCompressedCobblestone.valueOf(this.name());
        }

        public String getRegistryName() {
            return this.fluidTypeTier.getRegistryName();
        }

        public String getEnglishDisplayName() {
            return this.fluidTypeTier.getEnglishDisplayName();
        }

        public String getBucketEnglishDisplayName() {
            return this.fluidTypeTier.getBucketEnglishDisplayName();
        }

        public int getTintColor() {
            return this.fluidTypeTier.getTintColor();
        }

        public MoltenFluidEntry getFluidEntry() {
            return this.fluidEntry;
        }

        private void setFluidEntry(MoltenFluidEntry fluidEntry) {
            this.fluidEntry = fluidEntry;
        }

        public net.neoforged.neoforge.registries.DeferredHolder<net.neoforged.neoforge.fluids.FluidType, net.neoforged.neoforge.fluids.FluidType> getFluidType() {
            return this.fluidTypeTier.getFluidType();
        }
    }

    // dirty molten fluid も clean 版と同じ並びで保持しておくと、
    // creative tab や datagen で別ファミリーを同じ手順で処理できます。
    public enum TierMoltenDirtyCompressedCobblestone {
        COPPER,
        IRON,
        GOLD,
        AMETHYST,
        AQUAMARINE,
        TOPAZ,
        RUBY,
        SAPPHIRE,
        DIAMOND,
        EMERALD,
        NETHERITE,
        OBSIDIAN;

        private final ModFluidTypes.TierMoltenDirtyCompressedCobblestone fluidTypeTier;
        private MoltenFluidEntry fluidEntry;

        TierMoltenDirtyCompressedCobblestone() {
            this.fluidTypeTier = ModFluidTypes.TierMoltenDirtyCompressedCobblestone.valueOf(this.name());
        }

        public String getRegistryName() {
            return this.fluidTypeTier.getRegistryName();
        }

        public String getEnglishDisplayName() {
            return this.fluidTypeTier.getEnglishDisplayName();
        }

        public String getBucketEnglishDisplayName() {
            return this.fluidTypeTier.getBucketEnglishDisplayName();
        }

        public int getTintColor() {
            return this.fluidTypeTier.getTintColor();
        }

        public MoltenFluidEntry getFluidEntry() {
            return this.fluidEntry;
        }

        private void setFluidEntry(MoltenFluidEntry fluidEntry) {
            this.fluidEntry = fluidEntry;
        }

        public net.neoforged.neoforge.registries.DeferredHolder<net.neoforged.neoforge.fluids.FluidType, net.neoforged.neoforge.fluids.FluidType> getFluidType() {
            return this.fluidTypeTier.getFluidType();
        }
    }

    public static final MoltenFluidEntry MOLTEN_COMPRESSED_COBBLESTONE = registerMoltenFluid(
        "molten_compressed_cobblestone",
        ModFluidTypes.MOLTEN_COMPRESSED_COBBLESTONE
    );
    public static final MoltenFluidEntry MOLTEN_DIRTY_COMPRESSED_COBBLESTONE = registerMoltenFluid(
        "molten_dirty_compressed_cobblestone",
        ModFluidTypes.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE
    );

    static {
        for (TierMoltenCompressedCobblestone tier : TierMoltenCompressedCobblestone.values()) {
            tier.setFluidEntry(registerMoltenFluid(tier.getRegistryName(), tier.getFluidType()));
        }

        for (TierMoltenDirtyCompressedCobblestone tier : TierMoltenDirtyCompressedCobblestone.values()) {
            tier.setFluidEntry(registerMoltenFluid(tier.getRegistryName(), tier.getFluidType()));
        }

        for (WaterBasedFluid fluid : WaterBasedFluid.values()) {
            fluid.setFluidEntry(registerWaterBasedFluid(fluid.getRegistryName(), fluid.getFluidType()));
        }
    }

    private ModFluids() {
    }

    private static MoltenFluidEntry registerMoltenFluid(String name, DeferredHolder<net.neoforged.neoforge.fluids.FluidType, net.neoforged.neoforge.fluids.FluidType> fluidType) {
        BaseFlowingFluid.Properties properties = createMoltenFluidProperties(name, fluidType);

        DeferredHolder<Fluid, BaseFlowingFluid.Source> stillFluid = FLUIDS.register(name, () -> new BaseFlowingFluid.Source(properties));
        DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingFluid = FLUIDS.register("flowing_" + name, () -> new BaseFlowingFluid.Flowing(properties));

        DeferredBlock<LiquidBlock> fluidBlock = FLUID_BLOCKS.register(name, () -> new LiquidBlock(stillFluid.get(), createMoltenBlockProperties()));
        DeferredItem<BucketItem> bucketItem = FLUID_ITEMS.register(name + "_bucket", () -> new BucketItem(stillFluid.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));

        return new MoltenFluidEntry(stillFluid, flowingFluid, fluidBlock, bucketItem);
    }

    private static FluidEntry registerWaterBasedFluid(
        String name,
        DeferredHolder<net.neoforged.neoforge.fluids.FluidType, net.neoforged.neoforge.fluids.FluidType> fluidType
    ) {
        BaseFlowingFluid.Properties properties = createWaterBasedFluidProperties(name, fluidType);

        DeferredHolder<Fluid, BaseFlowingFluid.Source> stillFluid = FLUIDS.register(name, () -> new BaseFlowingFluid.Source(properties));
        DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingFluid = FLUIDS.register("flowing_" + name, () -> new BaseFlowingFluid.Flowing(properties));

        DeferredBlock<LiquidBlock> fluidBlock = FLUID_BLOCKS.register(name, () -> new LiquidBlock(stillFluid.get(), createWaterBasedBlockProperties()));
        DeferredItem<BucketItem> bucketItem = FLUID_ITEMS.register(name + "_bucket", () -> new BucketItem(stillFluid.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));

        return new FluidEntry(stillFluid, flowingFluid, fluidBlock, bucketItem);
    }

    private static BaseFlowingFluid.Properties createMoltenFluidProperties(String name, DeferredHolder<net.neoforged.neoforge.fluids.FluidType, net.neoforged.neoforge.fluids.FluidType> fluidType) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, name);

        return new BaseFlowingFluid.Properties(
            fluidType,
            DeferredHolder.create(Registries.FLUID, resourceLocation),
            DeferredHolder.create(Registries.FLUID, resourceLocation.withPrefix("flowing_"))
        )
            .block(DeferredHolder.create(Registries.BLOCK, resourceLocation))
            .bucket(DeferredHolder.create(Registries.ITEM, resourceLocation.withSuffix("_bucket")))
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .tickRate(30)
            .explosionResistance(100.0F);
    }

    private static BaseFlowingFluid.Properties createWaterBasedFluidProperties(
        String name,
        DeferredHolder<net.neoforged.neoforge.fluids.FluidType, net.neoforged.neoforge.fluids.FluidType> fluidType
    ) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, name);

        return new BaseFlowingFluid.Properties(
            fluidType,
            DeferredHolder.create(Registries.FLUID, resourceLocation),
            DeferredHolder.create(Registries.FLUID, resourceLocation.withPrefix("flowing_"))
        )
            .block(DeferredHolder.create(Registries.BLOCK, resourceLocation))
            .bucket(DeferredHolder.create(Registries.ITEM, resourceLocation.withSuffix("_bucket")))
            .slopeFindDistance(4)
            .levelDecreasePerBlock(1)
            .tickRate(5)
            .explosionResistance(100.0F);
    }

    private static BlockBehaviour.Properties createMoltenBlockProperties() {
        return BlockBehaviour.Properties.of()
            .mapColor(ModFluidTypes.getMoltenCompressedCobblestoneMapColor())
            .replaceable()
            .noCollission()
            .strength(100.0F)
            .pushReaction(PushReaction.DESTROY)
            .noLootTable()
            .liquid();
    }

    private static BlockBehaviour.Properties createWaterBasedBlockProperties() {
        return BlockBehaviour.Properties.of()
            .mapColor(ModFluidTypes.getWaterBasedFluidMapColor())
            .replaceable()
            .noCollission()
            .strength(100.0F)
            .pushReaction(PushReaction.DESTROY)
            .noLootTable()
            .liquid();
    }

    public static class FluidEntry {
        private final DeferredHolder<Fluid, BaseFlowingFluid.Source> stillFluid;
        private final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingFluid;
        private final DeferredBlock<LiquidBlock> fluidBlock;
        private final DeferredItem<BucketItem> bucketItem;

        private FluidEntry(
            DeferredHolder<Fluid, BaseFlowingFluid.Source> stillFluid,
            DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingFluid,
            DeferredBlock<LiquidBlock> fluidBlock,
            DeferredItem<BucketItem> bucketItem
        ) {
            this.stillFluid = stillFluid;
            this.flowingFluid = flowingFluid;
            this.fluidBlock = fluidBlock;
            this.bucketItem = bucketItem;
        }

        public DeferredHolder<Fluid, BaseFlowingFluid.Source> getStillFluid() {
            return this.stillFluid;
        }

        public DeferredHolder<Fluid, BaseFlowingFluid.Flowing> getFlowingFluid() {
            return this.flowingFluid;
        }

        public DeferredBlock<LiquidBlock> getFluidBlock() {
            return this.fluidBlock;
        }

        public DeferredItem<BucketItem> getBucketItem() {
            return this.bucketItem;
        }
    }

    public static class MoltenFluidEntry extends FluidEntry {
        private MoltenFluidEntry(
            DeferredHolder<Fluid, BaseFlowingFluid.Source> stillFluid,
            DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingFluid,
            DeferredBlock<LiquidBlock> fluidBlock,
            DeferredItem<BucketItem> bucketItem
        ) {
            super(stillFluid, flowingFluid, fluidBlock, bucketItem);
        }
    }
}