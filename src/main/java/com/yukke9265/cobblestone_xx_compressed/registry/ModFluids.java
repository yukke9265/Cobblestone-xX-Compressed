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

    public static final MoltenFluidEntry MOLTEN_COMPRESSED_COBBLESTONE = registerMoltenFluid(
        "molten_compressed_cobblestone",
        ModFluidTypes.MOLTEN_COMPRESSED_COBBLESTONE
    );

    static {
        for (TierMoltenCompressedCobblestone tier : TierMoltenCompressedCobblestone.values()) {
            tier.setFluidEntry(registerMoltenFluid(tier.getRegistryName(), tier.getFluidType()));
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

    public static class MoltenFluidEntry {
        private final DeferredHolder<Fluid, BaseFlowingFluid.Source> stillFluid;
        private final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingFluid;
        private final DeferredBlock<LiquidBlock> fluidBlock;
        private final DeferredItem<BucketItem> bucketItem;

        private MoltenFluidEntry(
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
}