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