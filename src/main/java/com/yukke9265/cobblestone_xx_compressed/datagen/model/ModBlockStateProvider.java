package com.yukke9265.cobblestone_xx_compressed.datagen.model;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.block.OnOffBlock;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluidTypes;
import com.yukke9265.cobblestone_xx_compressed.registry.ModFluids;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
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

        registerCobblestoneTankBlock(ModBlocks.COBBLESTONE_TANK.get(), "cobblestone_tank", "cobblestone_tank");

        for (ModBlocks.TierCobblestoneTank tier : ModBlocks.TierCobblestoneTank.values()) {
            registerCobblestoneTankBlock(tier.getBlock().get(), tier.getRegistryName(), tier.getRegistryName());
        }

        for (ModBlocks.TierCobblestoneGenerator generatorVariant : ModBlocks.TierCobblestoneGenerator.values()) {
            registerCobblestoneGeneratorBlock(generatorVariant);
        }

        // Cobblestone Melter は前面の見た目が ON/OFF で変わるので、
        // 単純な cubeAll ではなく、blockstate と 2 つの model をここでまとめて生成します。
        registerCobblestoneMelterBlock(ModBlocks.COBBLESTONE_MELTER.get(), "cobblestone_melter");
        registerCobblestoneDissolutionChamberBlock(ModBlocks.COBBLESTONE_DISSOLUTION_CHAMBER.get(), "cobblestone_dissolution_chamber");

        // LiquidBlock は level ごとに状態を持ちますが、見た目は fluid renderer が担当します。
        // そのため blockstate 側では、粒子テクスチャだけ持つ最小の model を全状態で共有すれば十分です。
        registerMoltenFluidBlock(ModFluids.MOLTEN_COMPRESSED_COBBLESTONE.getFluidBlock().get(), ModFluids.MOLTEN_COMPRESSED_COBBLESTONE.getFluidBlock().getId().getPath());

        for (ModFluids.TierMoltenCompressedCobblestone tier : ModFluids.TierMoltenCompressedCobblestone.values()) {
            registerMoltenFluidBlock(tier.getFluidEntry().getFluidBlock().get(), tier.getFluidEntry().getFluidBlock().getId().getPath());
        }

        registerMoltenFluidBlock(
            ModFluids.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE.getFluidBlock().get(),
            ModFluids.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE.getFluidBlock().getId().getPath(),
            ModFluidTypes.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE_STILL_TEXTURE
        );

        for (ModFluids.TierMoltenDirtyCompressedCobblestone tier : ModFluids.TierMoltenDirtyCompressedCobblestone.values()) {
            registerMoltenFluidBlock(
                tier.getFluidEntry().getFluidBlock().get(),
                tier.getFluidEntry().getFluidBlock().getId().getPath(),
                ModFluidTypes.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE_STILL_TEXTURE
            );
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

    private void registerCobblestoneTankBlock(
        net.minecraft.world.level.block.Block block,
        String modelName,
        String textureName
    ) {
        registerCubeAllBlock(block, "cobblestone_tank", modelName, textureName);
    }

    private void registerCobblestoneGeneratorBlock(ModBlocks.TierCobblestoneGenerator generatorVariant) {
        registerCubeAllBlock(
            generatorVariant.getBlock().get(),
            generatorVariant.getTextureFolderName(),
            generatorVariant.getRegistryName(),
            generatorVariant.getRegistryName()
        );
    }

    private void registerCobblestoneMelterBlock(Block block, String blockName) {
        ModelFile offModel = this.models()
            .withExistingParent(blockName + "_off", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/cobblestone_melter/cobblestone_melter_top"))
            .texture("down", this.modLoc("block/cobblestone_melter/cobblestone_melter_top"))
            .texture("up", this.modLoc("block/cobblestone_melter/cobblestone_melter_top"))
            .texture("north", this.modLoc("block/cobblestone_melter/cobblestone_melter_front"))
            .texture("south", this.modLoc("block/cobblestone_melter/cobblestone_melter_side"))
            .texture("west", this.modLoc("block/cobblestone_melter/cobblestone_melter_side"))
            .texture("east", this.modLoc("block/cobblestone_melter/cobblestone_melter_side"));

        ModelFile onModel = this.models()
            .withExistingParent(blockName + "_on", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/cobblestone_melter/cobblestone_melter_top"))
            .texture("down", this.modLoc("block/cobblestone_melter/cobblestone_melter_top"))
            .texture("up", this.modLoc("block/cobblestone_melter/cobblestone_melter_top"))
            .texture("north", this.modLoc("block/cobblestone_melter/cobblestone_melter_front_on"))
            .texture("south", this.modLoc("block/cobblestone_melter/cobblestone_melter_side"))
            .texture("west", this.modLoc("block/cobblestone_melter/cobblestone_melter_side"))
            .texture("east", this.modLoc("block/cobblestone_melter/cobblestone_melter_side"));

        this.getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(OnOffBlock.FACING);
            boolean isOn = state.getValue(OnOffBlock.ON);

            // datagen では north 基準の model を作り、
            // blockstate 側で向きごとの回転だけを差し替えるようにします。
            int rotationY = switch (facing) {
                case SOUTH -> 180;
                case EAST -> 90;
                case WEST -> 270;
                default -> 0;
            };

            return ConfiguredModel.builder()
                .modelFile(isOn ? onModel : offModel)
                .rotationY(rotationY)
                .build();
        });

        this.simpleBlockItem(block, offModel);
    }

    private void registerCobblestoneDissolutionChamberBlock(Block block, String blockName) {
        ModelFile offModel = this.models()
            .withExistingParent(blockName + "_off", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_top"))
            .texture("down", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_top"))
            .texture("up", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_top"))
            .texture("north", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_front"))
            .texture("south", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_side"))
            .texture("west", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_side"))
            .texture("east", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_side"));

        ModelFile onModel = this.models()
            .withExistingParent(blockName + "_on", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_top"))
            .texture("down", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_top"))
            .texture("up", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_top"))
            .texture("north", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_front_on"))
            .texture("south", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_side"))
            .texture("west", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_side"))
            .texture("east", this.modLoc("block/cobblestone_dissolution_chamber/cobblestone_dissolution_chamber_side"));

        this.getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(OnOffBlock.FACING);
            boolean isOn = state.getValue(OnOffBlock.ON);

            int rotationY = switch (facing) {
                case SOUTH -> 180;
                case EAST -> 90;
                case WEST -> 270;
                default -> 0;
            };

            return ConfiguredModel.builder()
                .modelFile(isOn ? onModel : offModel)
                .rotationY(rotationY)
                .build();
        });

        this.simpleBlockItem(block, offModel);
    }

    private void registerMoltenFluidBlock(net.minecraft.world.level.block.Block block, String modelName) {
        registerMoltenFluidBlock(block, modelName, ModFluidTypes.MOLTEN_COMPRESSED_COBBLESTONE_STILL_TEXTURE);
    }

    private void registerMoltenFluidBlock(net.minecraft.world.level.block.Block block, String modelName, net.minecraft.resources.ResourceLocation particleTexture) {
        this.simpleBlock(
            block,
            this.models().getBuilder(modelName).texture("particle", particleTexture)
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