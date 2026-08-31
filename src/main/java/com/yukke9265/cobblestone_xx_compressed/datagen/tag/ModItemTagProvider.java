package com.yukke9265.cobblestone_xx_compressed.datagen.tag;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.registry.ModItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, CobblestonexXCompressed.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider provider) {
        // 外部 mod が参照する粉タグは、まだ forge 名前空間を使っているものと
        // common の c 名前空間を使っているものが混在しています。
        // そのため、この mod の粉は両方へ入れておくと相互利用しやすくなります。
        this.addDustTags("amethyst", ModItems.AMETHYST_DUST);
        this.addDustTags("ancient_debris", ModItems.ANCIENT_DEBRIS_DUST);
        this.addDustTags("aquamarine", ModItems.AQUAMARINE_DUST);
        this.addDustTags("coal", ModItems.COAL_DUST);
        this.addDustTags("copper", ModItems.COPPER_DUST);
        this.addDustTags("diamond", ModItems.DIAMOND_DUST);
        this.addDustTags("emerald", ModItems.EMERALD_DUST);
        this.addDustTags("ender", ModItems.ENDER_DUST);
        this.addDustTags("gold", ModItems.GOLD_DUST);
        this.addDustTags("iron", ModItems.IRON_DUST);
        this.addDustTags("lapis", ModItems.LAPIS_DUST);
        this.addDustTags("obsidian", ModItems.OBSIDIAN_DUST);
        this.addDustTags("ruby", ModItems.RUBY_DUST);
        this.addDustTags("sapphire", ModItems.SAPPHIRE_DUST);
        this.addDustTags("topaz", ModItems.TOPAZ_DUST);

        // ピッケルはバニラのツール用タグへ入れておくと、
        // エンチャント台やクラスター破壊など既存の仕組みがそのまま動きます。
        this.addPickaxeTags(ModItems.COMPRESSED_COBBLESTONE_PICKAXE);
        for (ModItems.TierCompressedCobblestonePickaxe tier : ModItems.TierCompressedCobblestonePickaxe.values()) {
            this.addPickaxeTags(tier.getItem());
        }

        for (ModItems.CompressedCobblestoneArmorPiece piece : ModItems.CompressedCobblestoneArmorPiece.values()) {
            this.addArmorTags(ModItems.getBaseCompressedCobblestoneArmor(piece), piece.getArmorType());
        }
        for (ModItems.TierCompressedCobblestoneArmor tier : ModItems.TierCompressedCobblestoneArmor.values()) {
            for (ModItems.CompressedCobblestoneArmorPiece piece : ModItems.CompressedCobblestoneArmorPiece.values()) {
                this.addArmorTags(tier.getItem(piece), piece.getArmorType());
            }
        }

        // 装飾品スロット mod は「そのスロット用タグに入っているアイテム」だけ装備できます。
        // チャーム枠が一般的なので、Curios と Accessories の両方へ入れておきます。
        Item flyingStone = Objects.requireNonNull(ModItems.FLYING_STONE.get());
        this.tag(this.createItemTag("curios", "charm")).add(flyingStone);
        this.tag(this.createItemTag("curios", "curio")).add(flyingStone);
        this.tag(this.createItemTag("accessories", "charm")).add(flyingStone);

        // AE2 未導入でも datagen できるよう、AE2 アイテムは optional タグ経由で参照します。
        this.addOptionalAe2ItemTag("ae2_sky_stone", "sky_stone_block");
        this.addOptionalAe2ItemTag("ae2_sky_dust", "sky_dust");
        this.addOptionalAe2ItemTag("ae2_printed_logic_processor", "printed_logic_processor");
        this.addOptionalAe2ItemTag("ae2_printed_calculation_processor", "printed_calculation_processor");
        this.addOptionalAe2ItemTag("ae2_printed_engineering_processor", "printed_engineering_processor");
        this.addOptionalAe2ItemTag("ae2_printed_silicon", "printed_silicon");

        // Advanced AE 未導入でも datagen できるよう、optional タグ経由で参照します。
        this.addOptionalAdvancedAeItemTag("advanced_ae_quantum_infused_dust", "quantum_infused_dust");
    }

    private void addOptionalAe2ItemTag(String tagPath, String ae2ItemPath) {
        this.tag(this.createItemTag(CobblestonexXCompressed.MODID, tagPath))
            .addOptional(ResourceLocation.fromNamespaceAndPath("ae2", ae2ItemPath));
    }

    private void addOptionalAdvancedAeItemTag(String tagPath, String advancedAeItemPath) {
        this.tag(this.createItemTag(CobblestonexXCompressed.MODID, tagPath))
            .addOptional(ResourceLocation.fromNamespaceAndPath("advanced_ae", advancedAeItemPath));
    }

    @SuppressWarnings("null")
    private void addArmorTags(DeferredItem<Item> armorItem, ArmorItem.Type armorType) {
        Item item = Objects.requireNonNull(armorItem.get());

        switch (armorType) {
            case HELMET -> {
                this.tag(ItemTags.HEAD_ARMOR).add(item);
                this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(item);
            }
            case CHESTPLATE -> {
                this.tag(ItemTags.CHEST_ARMOR).add(item);
                this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(item);
            }
            case LEGGINGS -> {
                this.tag(ItemTags.LEG_ARMOR).add(item);
                this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(item);
            }
            case BOOTS -> {
                this.tag(ItemTags.FOOT_ARMOR).add(item);
                this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(item);
            }
            default -> {
            }
        }

        this.tag(ItemTags.ARMOR_ENCHANTABLE).add(item);
    }

    @SuppressWarnings("null")
    private void addPickaxeTags(DeferredItem<Item> pickaxeItem) {
        Item item = Objects.requireNonNull(pickaxeItem.get());

        this.tag(ItemTags.PICKAXES).add(item);
        this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES).add(item);
        this.tag(ItemTags.MINING_ENCHANTABLE).add(item);
        this.tag(ItemTags.MINING_LOOT_ENCHANTABLE).add(item);
        this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(item);
    }

    @SuppressWarnings("null")
    private void addDustTags(String materialName, DeferredItem<Item> dustItem) {
        TagKey<Item> commonDusts = this.createItemTag("c", "dusts");
        TagKey<Item> forgeDusts = this.createItemTag("forge", "dusts");
        TagKey<Item> commonMaterialDust = this.createItemTag("c", "dusts/" + materialName);
        TagKey<Item> forgeMaterialDust = this.createItemTag("forge", "dusts/" + materialName);
        Item item = Objects.requireNonNull(dustItem.get());

        // 総称タグと個別タグの両方へ入れておくことで、
        // "どんな dust でも可" と "iron dust が必要" の両方に反応できます。
        this.tag(commonMaterialDust).add(item);
        this.tag(forgeMaterialDust).add(item);
        this.tag(commonDusts).addTag(commonMaterialDust);
        this.tag(forgeDusts).addTag(forgeMaterialDust);
    }

    @SuppressWarnings("null")
    private TagKey<Item> createItemTag(String namespace, String path) {
        return Objects.requireNonNull(ItemTags.create(Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(namespace, path))));
    }
}