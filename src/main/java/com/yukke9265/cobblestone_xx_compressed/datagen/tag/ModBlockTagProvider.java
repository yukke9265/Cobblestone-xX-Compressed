package com.yukke9265.cobblestone_xx_compressed.datagen.tag;

import java.util.concurrent.CompletableFuture;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CobblestonexXCompressed.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 圧縮丸石と圧縮石はどちらも石系ブロックとして扱うので、
        // まとめて pickaxe タグへ入れます。
        // 適正ツールを後から増やしたいときも、この 1 か所を見れば追えます。
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.COMPRESSED_COBBLESTONE.get())
            .add(ModBlocks.COMPRESSED_STONE.get())
            .add(ModBlocks.COBBLESTONE_MACHINE_CASING.get())
            .add(ModBlocks.COBBLESTONE_TANK.get())
            .add(ModBlocks.COBBLESTONE_FURNACE.get())
            .add(ModBlocks.COBBLESTONE_CRUSHER.get())
            .add(ModBlocks.COBBLESTONE_CENTRIFUGE.get())
            .add(ModBlocks.COBBLESTONE_LASER_DRILL.get())
            .add(ModBlocks.COBBLESTONE_MELTER.get())
            .add(ModBlocks.COBBLESTONE_ASSEMBLY_MACHINE.get())
            .add(ModBlocks.COBBLESTONE_MIXER.get())
            .add(ModBlocks.COBBLESTONE_DISSOLUTION_CHAMBER.get());

        for (ModBlocks.TierCobblestoneGenerator generatorVariant : ModBlocks.TierCobblestoneGenerator.values()) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(generatorVariant.getBlock().get());
        }

        for (ModBlocks.TierCompressedCobblestone tier : ModBlocks.TierCompressedCobblestone.values()) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(tier.getBlock().get());
        }

        for (ModBlocks.TierCompressedStone tier : ModBlocks.TierCompressedStone.values()) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(tier.getBlock().get());
        }

        for (ModBlocks.TierCobblestoneMachineCasing tier : ModBlocks.TierCobblestoneMachineCasing.values()) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(tier.getBlock().get());
        }

        for (ModBlocks.TierCobblestoneTank tier : ModBlocks.TierCobblestoneTank.values()) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(tier.getBlock().get());
        }
    }
}