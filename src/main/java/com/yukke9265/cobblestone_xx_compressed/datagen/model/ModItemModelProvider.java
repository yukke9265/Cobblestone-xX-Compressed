package com.yukke9265.cobblestone_xx_compressed.datagen.model;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CobblestonexXCompressed.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // 今回は通常版も tier 版も、丸石パンの item model をすべて datagen で出します。
        // これで main/resources 側と generated/resources 側に同じ役割の JSON が混在せず、
        // 「どこを直せば model が変わるのか」が追いやすくなります。

        // それぞれの item model は minecraft:item/generated を親にして、
        // layer0 へ対応する PNG を 1 枚だけ貼る、最も基本的な形です。
        // テクスチャ参照先は「textures/item/cobblestone_bread/<登録名>.png」で固定し、
        // item の登録名から自動で組み立てます。
        // こうしておくと、model 側と texture 側の名前ズレが起きにくくなります。
        registerCobblestoneBreadItemModel(ModItems.COBBLESTONE_BREAD);

        for (ModItems.TierCobblestoneBread tier : ModItems.TierCobblestoneBread.values()) {
            registerCobblestoneBreadItemModel(tier.getItem());
        }

        // 丸石ジェムも同じ generated item model を使います。
        // フォルダ名だけ別なので、共通メソッドへ渡す引数だけ変えます。
        registerCobblestoneGemItemModel(ModItems.COBBLESTONE_GEM);

        for (ModItems.TierCobblestoneGem tier : ModItems.TierCobblestoneGem.values()) {
            registerCobblestoneGemItemModel(tier.getItem());
        }

        registerCobblestoneDustItemModel(ModItems.COBBLESTONE_DUST);

        for (ModItems.TierCobblestoneDust tier : ModItems.TierCobblestoneDust.values()) {
            registerCobblestoneDustItemModel(tier.getItem());
        }

        registerCobblestoneDirtyDustItemModel(ModItems.COBBLESTONE_DIRTY_DUST);

        for (ModItems.TierCobblestoneDirtyDust tier : ModItems.TierCobblestoneDirtyDust.values()) {
            registerCobblestoneDirtyDustItemModel(tier.getItem());
        }

        registerCobblestoneMixedDustItemModel(ModItems.COBBLESTONE_MIXED_DUST);

        for (ModItems.TierCobblestoneMixedDust tier : ModItems.TierCobblestoneMixedDust.values()) {
            registerCobblestoneMixedDustItemModel(tier.getItem());
        }

        registerCompressedCobblestoneSingularityBitItemModel(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_BIT);

        for (ModItems.TierCompressedCobblestoneSingularityBit tier : ModItems.TierCompressedCobblestoneSingularityBit.values()) {
            registerCompressedCobblestoneSingularityBitItemModel(tier.getItem());
        }

        registerCompressedCobblestoneSingularityFragmentItemModel(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY_FRAGMENT);

        for (ModItems.TierCompressedCobblestoneSingularityFragment tier : ModItems.TierCompressedCobblestoneSingularityFragment.values()) {
            registerCompressedCobblestoneSingularityFragmentItemModel(tier.getItem());
        }

        registerCompressedCobblestoneSingularityItemModel(ModItems.COMPRESSED_COBBLESTONE_SINGULARITY);

        for (ModItems.TierCompressedCobblestoneSingularity tier : ModItems.TierCompressedCobblestoneSingularity.values()) {
            registerCompressedCobblestoneSingularityItemModel(tier.getItem());
        }

        registerDirtyMixtureItemModel(ModItems.DIRTY_MIXTURE);

        registerCoalDustItemModel(ModItems.COAL_DUST);

        registerCobblestoneWireItemModel(ModItems.COBBLESTONE_WIRE);

        for (ModItems.TierCobblestoneWire tier : ModItems.TierCobblestoneWire.values()) {
            registerCobblestoneWireItemModel(tier.getItem());
        }

        registerCobblestoneRodItemModel(ModItems.COBBLESTONE_ROD);

        for (ModItems.TierCobblestoneRod tier : ModItems.TierCobblestoneRod.values()) {
            registerCobblestoneRodItemModel(tier.getItem());
        }

        registerCobblestoneMotorItemModel(ModItems.COBBLESTONE_MOTOR);

        for (ModItems.TierCobblestoneMotor tier : ModItems.TierCobblestoneMotor.values()) {
            registerCobblestoneMotorItemModel(tier.getItem());
        }

        registerCobblestoneCircuitItemModel(ModItems.COBBLESTONE_CIRCUIT);

        for (ModItems.TierCobblestoneCircuit tier : ModItems.TierCobblestoneCircuit.values()) {
            registerCobblestoneCircuitItemModel(tier.getItem());
        }

        registerCobblestoneProcessorItemModel(ModItems.COBBLESTONE_PROCESSOR);

        for (ModItems.TierCobblestoneProcessor tier : ModItems.TierCobblestoneProcessor.values()) {
            registerCobblestoneProcessorItemModel(tier.getItem());
        }

        registerCobblestoneCircuitPackageItemModel(ModItems.COBBLESTONE_CIRCUIT_PACKAGE);

        for (ModItems.TierCobblestoneCircuitPackage tier : ModItems.TierCobblestoneCircuitPackage.values()) {
            registerCobblestoneCircuitPackageItemModel(tier.getItem());
        }

        registerCobblestoneAccelerationChipItemModel(ModItems.COBBLESTONE_ACCELERATION_CHIP);

        for (ModItems.TierCobblestoneAccelerationChip tier : ModItems.TierCobblestoneAccelerationChip.values()) {
            registerCobblestoneAccelerationChipItemModel(tier.getItem());
        }

        registerCobblestoneEnergizedCubeItemModel(ModItems.COBBLESTONE_ENERGIZED_CUBE);

        for (ModItems.TierCobblestoneEnergizedCube tier : ModItems.TierCobblestoneEnergizedCube.values()) {
            registerCobblestoneEnergizedCubeItemModel(tier.getItem());
        }
    }

    // 丸石パン系は、登録名と PNG ファイル名を同じにしておくと管理がかなり楽です。
    // その前提をこのメソッドに閉じ込めておけば、追加時は item 登録とこの呼び出しだけ見れば足ります。
    private void registerCobblestoneBreadItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_bread");
    }

    private void registerCobblestoneGemItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_gem");
    }

    private void registerCobblestoneDustItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_dust");
    }

    private void registerCobblestoneDirtyDustItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_dirty_dust");
    }

    private void registerCobblestoneMixedDustItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_mixed_dust");
    }

    private void registerCompressedCobblestoneSingularityBitItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "compressed_cobblestone_singularity_bit");
    }

    private void registerCompressedCobblestoneSingularityFragmentItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "compressed_cobblestone_singularity_fragment");
    }

    private void registerCompressedCobblestoneSingularityItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "compressed_cobblestone_singularity");
    }

    private void registerDirtyMixtureItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "dust");
    }

    private void registerCoalDustItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "dust");
    }

    private void registerCobblestoneWireItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_wire");
    }

    private void registerCobblestoneRodItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_rod");
    }

    private void registerCobblestoneMotorItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_motor");
    }

    private void registerCobblestoneCircuitItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_circuit");
    }

    private void registerCobblestoneProcessorItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_processor");
    }

    private void registerCobblestoneCircuitPackageItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_circuit_package");
    }

    private void registerCobblestoneAccelerationChipItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_acceleration_chip");
    }

    private void registerCobblestoneEnergizedCubeItemModel(DeferredItem<Item> item) {
        registerGeneratedItemModel(item, "cobblestone_energized_cube");
    }

    private void registerGeneratedItemModel(DeferredItem<Item> item, String textureFolder) {
        String itemName = item.getId().getPath();

        withExistingParent(itemName, mcLoc("item/generated"))
            .texture(
                "layer0",
                ResourceLocation.fromNamespaceAndPath(
                    CobblestonexXCompressed.MODID,
                    "item/" + textureFolder + "/" + itemName
                )
            );
    }
}