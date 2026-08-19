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
        registerStandardOnOffMachineBlock(ModBlocks.COBBLESTONE_FURNACE.get(), "cobblestone_furnace");
        registerStandardOnOffMachineBlock(ModBlocks.COBBLESTONE_POWERED_FURNACE.get(), "cobblestone_powered_furnace");
        registerStandardOnOffMachineBlock(ModBlocks.COBBLESTONE_CRUSHER.get(), "cobblestone_crusher");
        registerStandardOnOffMachineBlock(ModBlocks.COBBLESTONE_FE_GENERATOR.get(), "cobblestone_fe_generator");
        registerStandardOnOffMachineBlock(ModBlocks.COBBLESTONE_CENTRIFUGE.get(), "cobblestone_centrifuge");
        registerStandardOnOffMachineBlock(ModBlocks.COBBLESTONE_LASER_DRILL.get(), "cobblestone_laser_drill");
        registerStandardOnOffMachineBlock(ModBlocks.COBBLESTONE_MIXER.get(), "cobblestone_mixer");
        registerStandardOnOffMachineBlock(ModBlocks.STONE_BREAK_SIMULATOR.get(), "stone_break_simulator");
        registerCobblestoneExtremeCompressorBlock(ModBlocks.COBBLESTONE_EXTREME_COMPRESSOR.get(), "cobblestone_extreme_compressor");
        registerCobblestoneMelterBlock(ModBlocks.COBBLESTONE_MELTER.get(), "cobblestone_melter");
        registerCobblestoneAssemblyMachineBlock(ModBlocks.COBBLESTONE_ASSEMBLY_MACHINE.get(), "cobblestone_assembly_machine");
        registerCobblestoneEnchanterBlock(ModBlocks.COBBLESTONE_ENCHANTER.get(), "cobblestone_enchanter");
        registerCobblestoneChemicalReactorBlock(ModBlocks.COBBLESTONE_CHEMICAL_REACTOR.get(), "cobblestone_chemical_reactor");
        registerStandardOnOffMachineBlock(ModBlocks.COBBLESTONE_REACTION_CHAMBER.get(), "cobblestone_reaction_chamber");
        registerStandardOnOffMachineBlock(ModBlocks.COBBLESTONE_CRYSTALLIZATION_CHAMBER.get(), "cobblestone_crystallization_chamber");
        registerCobblestoneDissolutionChamberBlock(ModBlocks.COBBLESTONE_DISSOLUTION_CHAMBER.get(), "cobblestone_dissolution_chamber");
        registerStandardOnOffMachineBlock(ModBlocks.COBBLESTONE_FLUID_MIXER.get(), "cobblestone_fluid_mixer");

        // LiquidBlock は level ごとに状態を持ちますが、見た目は fluid renderer が担当します。
        // そのため blockstate 側では、粒子テクスチャだけ持つ最小の model を全状態で共有すれば十分です。
        registerFluidBlock(ModFluids.MOLTEN_COMPRESSED_COBBLESTONE.getFluidBlock().get(), ModFluids.MOLTEN_COMPRESSED_COBBLESTONE.getFluidBlock().getId().getPath());

        for (ModFluids.TierMoltenCompressedCobblestone tier : ModFluids.TierMoltenCompressedCobblestone.values()) {
            registerFluidBlock(tier.getFluidEntry().getFluidBlock().get(), tier.getFluidEntry().getFluidBlock().getId().getPath());
        }

        registerFluidBlock(
            ModFluids.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE.getFluidBlock().get(),
            ModFluids.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE.getFluidBlock().getId().getPath(),
            ModFluidTypes.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE_STILL_TEXTURE
        );

        for (ModFluids.TierMoltenDirtyCompressedCobblestone tier : ModFluids.TierMoltenDirtyCompressedCobblestone.values()) {
            registerFluidBlock(
                tier.getFluidEntry().getFluidBlock().get(),
                tier.getFluidEntry().getFluidBlock().getId().getPath(),
                ModFluidTypes.MOLTEN_DIRTY_COMPRESSED_COBBLESTONE_STILL_TEXTURE
            );
        }

        for (ModFluids.WaterBasedFluid fluid : ModFluids.WaterBasedFluid.values()) {
            registerFluidBlock(
                fluid.getFluidEntry().getFluidBlock().get(),
                fluid.getFluidEntry().getFluidBlock().getId().getPath(),
                ModFluidTypes.getStillTextureLocation(fluid.getRegistryName())
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

    @SuppressWarnings("null")
    private void registerStandardOnOffMachineBlock(Block block, String blockName) {
        ModelFile offModel = this.models()
            .withExistingParent(blockName + "_off", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/" + blockName + "/" + blockName + "_top"))
            .texture("down", this.modLoc("block/" + blockName + "/" + blockName + "_top"))
            .texture("up", this.modLoc("block/" + blockName + "/" + blockName + "_top"))
            .texture("north", this.modLoc("block/" + blockName + "/" + blockName + "_front"))
            .texture("south", this.modLoc("block/" + blockName + "/" + blockName + "_side"))
            .texture("west", this.modLoc("block/" + blockName + "/" + blockName + "_side"))
            .texture("east", this.modLoc("block/" + blockName + "/" + blockName + "_side"));

        ModelFile onModel = this.models()
            .withExistingParent(blockName + "_on", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/" + blockName + "/" + blockName + "_top"))
            .texture("down", this.modLoc("block/" + blockName + "/" + blockName + "_top"))
            .texture("up", this.modLoc("block/" + blockName + "/" + blockName + "_top"))
            .texture("north", this.modLoc("block/" + blockName + "/" + blockName + "_front_on"))
            .texture("south", this.modLoc("block/" + blockName + "/" + blockName + "_side"))
            .texture("west", this.modLoc("block/" + blockName + "/" + blockName + "_side"))
            .texture("east", this.modLoc("block/" + blockName + "/" + blockName + "_side"));

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

    private void registerCobblestoneEnchanterBlock(Block block, String blockName) {
        // 既存テクスチャの実フォルダ名が cobblestone_enchnater になっているため、
        // datagen 側でもその実体に合わせて参照します。
        String textureFolderName = "cobblestone_enchnater";

        ModelFile offModel = this.models()
            .withExistingParent(blockName + "_off", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_top"))
            .texture("down", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_top"))
            .texture("up", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_top"))
            .texture("north", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_front"))
            .texture("south", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_side"))
            .texture("west", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_side"))
            .texture("east", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_side"));

        ModelFile onModel = this.models()
            .withExistingParent(blockName + "_on", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_top"))
            .texture("down", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_top"))
            .texture("up", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_top"))
            .texture("north", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_front_on"))
            .texture("south", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_side"))
            .texture("west", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_side"))
            .texture("east", this.modLoc("block/" + textureFolderName + "/" + textureFolderName + "_side"));

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

    private void registerCobblestoneExtremeCompressorBlock(Block block, String blockName) {
        ModelFile offModel = this.models()
            .withExistingParent(blockName + "_off", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_top"))
            .texture("down", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_top"))
            .texture("up", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_top"))
            .texture("north", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_front"))
            .texture("south", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_side"))
            .texture("west", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_side"))
            .texture("east", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_side"));

        ModelFile onModel = this.models()
            .withExistingParent(blockName + "_on", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_top"))
            .texture("down", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_top"))
            .texture("up", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_top"))
            .texture("north", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_front_on"))
            .texture("south", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_side"))
            .texture("west", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_side"))
            .texture("east", this.modLoc("block/cobblestone_extreme_compressor/cobblestone_extreme_compressor_side"));

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

    private void registerCobblestoneChemicalReactorBlock(Block block, String blockName) {
        ModelFile offModel = this.models()
            .withExistingParent(blockName + "_off", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_top"))
            .texture("down", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_top"))
            .texture("up", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_top"))
            .texture("north", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_front"))
            .texture("south", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_side"))
            .texture("west", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_side"))
            .texture("east", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_side"));

        ModelFile onModel = this.models()
            .withExistingParent(blockName + "_on", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_top"))
            .texture("down", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_top"))
            .texture("up", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_top"))
            .texture("north", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_front_on"))
            .texture("south", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_side"))
            .texture("west", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_side"))
            .texture("east", this.modLoc("block/cobblestone_chemical_reactor/cobblestone_chemical_reactor_side"));

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

    private void registerCobblestoneAssemblyMachineBlock(Block block, String blockName) {
        ModelFile offModel = this.models()
            .withExistingParent(blockName + "_off", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_top"))
            .texture("down", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_top"))
            .texture("up", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_top"))
            .texture("north", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_front"))
            .texture("south", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_side"))
            .texture("west", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_side"))
            .texture("east", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_side"));

        ModelFile onModel = this.models()
            .withExistingParent(blockName + "_on", this.mcLoc("block/cube"))
            .texture("particle", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_top"))
            .texture("down", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_top"))
            .texture("up", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_top"))
            .texture("north", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_front_on"))
            .texture("south", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_side"))
            .texture("west", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_side"))
            .texture("east", this.modLoc("block/cobblestone_assembly_machine/cobblestone_assembly_machine_side"));

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

    private void registerFluidBlock(net.minecraft.world.level.block.Block block, String modelName) {
        registerFluidBlock(block, modelName, ModFluidTypes.MOLTEN_COMPRESSED_COBBLESTONE_STILL_TEXTURE);
    }

    private void registerFluidBlock(net.minecraft.world.level.block.Block block, String modelName, net.minecraft.resources.ResourceLocation particleTexture) {
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