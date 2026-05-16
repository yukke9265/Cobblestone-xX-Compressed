package com.yukke9265.cobblestone_xx_compressed.datagen.model;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CobblestonexXCompressed.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // 圧縮丸石は全方向同じ見た目の単純な立方体ブロックなので、
        // cubeAll + simpleBlockWithItem で blockstate / block model / item model をまとめて生成できます。
        registerCompressedCobblestoneBlock(ModBlocks.COMPRESSED_COBBLESTONE.get(), "compressed_cobblestone");

        for (ModBlocks.TierCompressedCobblestone tier : ModBlocks.TierCompressedCobblestone.values()) {
            registerCompressedCobblestoneBlock(tier.getBlock().get(), tier.getRegistryName());
        }

        registerCompressedStoneBlock(ModBlocks.COMPRESSED_STONE.get(), "compressed_stone");

        for (ModBlocks.TierCompressedStone tier : ModBlocks.TierCompressedStone.values()) {
            registerCompressedStoneBlock(tier.getBlock().get(), tier.getRegistryName());
        }

        registerCobblestoneMachineCasingBlock(ModBlocks.COBBLESTONE_MACHINE_CASING.get(), "cobblestone_machine_casing", "cobblestone_machine_casing");

        for (ModBlocks.TierCobblestoneMachineCasing tier : ModBlocks.TierCobblestoneMachineCasing.values()) {
            registerCobblestoneMachineCasingBlock(tier.getBlock().get(), tier.getRegistryName(), tier.getRegistryName());
        }

        for (ModBlocks.TierCobblestoneGenerator generatorVariant : ModBlocks.TierCobblestoneGenerator.values()) {
            registerCobblestoneGeneratorBlock(generatorVariant);
        }
    }

    private void registerCompressedCobblestoneBlock(net.minecraft.world.level.block.Block block, String textureName) {
        registerCubeAllBlock(block, "compressed_cobblestone", textureName);
    }

    private void registerCompressedStoneBlock(net.minecraft.world.level.block.Block block, String textureName) {
        registerCubeAllBlock(block, "compressed_stone", textureName);
    }

    private void registerCobblestoneMachineCasingBlock(
        net.minecraft.world.level.block.Block block,
        String modelName,
        String textureName
    ) {
        registerCubeAllBlock(block, "cobblestone_machine_casing", modelName, textureName);
    }

    private void registerCobblestoneGeneratorBlock(ModBlocks.TierCobblestoneGenerator generatorVariant) {
        registerCubeAllBlock(
            generatorVariant.getBlock().get(),
            generatorVariant.getTextureFolderName(),
            generatorVariant.getRegistryName(),
            generatorVariant.getRegistryName()
        );
    }

    private void registerCubeAllBlock(net.minecraft.world.level.block.Block block, String textureFolder, String textureName) {
        registerCubeAllBlock(block, textureFolder, textureName, textureName);
    }

    private void registerCubeAllBlock(
        net.minecraft.world.level.block.Block block,
        String textureFolder,
        String modelName,
        String textureName
    ) {
        this.simpleBlockWithItem(
            block,
            this.models().cubeAll(modelName, this.modLoc("block/" + textureFolder + "/" + textureName))
        );
    }
}