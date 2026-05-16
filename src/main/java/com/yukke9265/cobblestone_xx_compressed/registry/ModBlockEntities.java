package com.yukke9265.cobblestone_xx_compressed.registry;

import java.util.function.Supplier;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneGeneratorBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneCrusherBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneCentrifugeBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneFEGeneratorBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneFurnaceBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestoneMixerBlockEntity;
import com.yukke9265.cobblestone_xx_compressed.blockentity.CobblestonePoweredFurnaceBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CobblestonexXCompressed.MODID);

    public static final Supplier<BlockEntityType<CobblestoneFurnaceBlockEntity>> COBBLESTONE_FURNACE_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register(
            "cobblestone_furnace",
            () -> BlockEntityType.Builder.of(
                CobblestoneFurnaceBlockEntity::new,
                ModBlocks.COBBLESTONE_FURNACE.get()
            ).build(null)
        );

    public static final Supplier<BlockEntityType<CobblestonePoweredFurnaceBlockEntity>> COBBLESTONE_POWERED_FURNACE_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register(
            "cobblestone_powered_furnace",
            () -> BlockEntityType.Builder.of(
                CobblestonePoweredFurnaceBlockEntity::new,
                ModBlocks.COBBLESTONE_POWERED_FURNACE.get()
            ).build(null)
        );

    public static final Supplier<BlockEntityType<CobblestoneCrusherBlockEntity>> COBBLESTONE_CRUSHER_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register(
            "cobblestone_crusher",
            () -> BlockEntityType.Builder.of(
                CobblestoneCrusherBlockEntity::new,
                ModBlocks.COBBLESTONE_CRUSHER.get()
            ).build(null)
        );

    public static final Supplier<BlockEntityType<CobblestoneFEGeneratorBlockEntity>> COBBLESTONE_FE_GENERATOR_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register(
            "cobblestone_fe_generator",
            () -> BlockEntityType.Builder.of(
                CobblestoneFEGeneratorBlockEntity::new,
                ModBlocks.COBBLESTONE_FE_GENERATOR.get()
            ).build(null)
        );

    public static final Supplier<BlockEntityType<CobblestoneCentrifugeBlockEntity>> COBBLESTONE_CENTRIFUGE_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register(
            "cobblestone_centrifuge",
            () -> BlockEntityType.Builder.of(
                CobblestoneCentrifugeBlockEntity::new,
                ModBlocks.COBBLESTONE_CENTRIFUGE.get()
            ).build(null)
        );

    public static final Supplier<BlockEntityType<CobblestoneMixerBlockEntity>> COBBLESTONE_MIXER_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register(
            "cobblestone_mixer",
            () -> BlockEntityType.Builder.of(
                CobblestoneMixerBlockEntity::new,
                ModBlocks.COBBLESTONE_MIXER.get()
            ).build(null)
        );

    public static final Supplier<BlockEntityType<CobblestoneGeneratorBlockEntity>> COBBLESTONE_GENERATOR_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register(
            "cobblestone_generator",
            () -> BlockEntityType.Builder.of(
                (pos, state) -> {
                    if (state.getBlock() instanceof com.yukke9265.cobblestone_xx_compressed.block.CobblestoneGeneratorBlock generatorBlock) {
                        return new CobblestoneGeneratorBlockEntity(pos, state, generatorBlock.getGeneratorVariant());
                    }

                    throw new IllegalStateException("Cobblestone Generator 以外の block state で生成しようとしました: " + state);
                },
                getCobblestoneGeneratorBlocks()
            ).build(null)
        );

    private static Block[] getCobblestoneGeneratorBlocks() {
        Block[] blocks = new Block[ModBlocks.TierCobblestoneGenerator.values().length];
        int index = 0;

        for (ModBlocks.TierCobblestoneGenerator generatorVariant : ModBlocks.TierCobblestoneGenerator.values()) {
            blocks[index] = generatorVariant.getBlock().get();
            index++;
        }

        return blocks;
    }
}