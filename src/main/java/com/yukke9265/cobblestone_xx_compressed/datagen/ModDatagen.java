package com.yukke9265.cobblestone_xx_compressed.datagen;

import com.yukke9265.cobblestone_xx_compressed.datagen.lang.ModEnglishLanguageProvider;
import com.yukke9265.cobblestone_xx_compressed.datagen.lang.ModJapaneseLanguageProvider;
import com.yukke9265.cobblestone_xx_compressed.datagen.loot.ModBlockLootTableProvider;
import com.yukke9265.cobblestone_xx_compressed.datagen.model.ModBlockStateProvider;
import com.yukke9265.cobblestone_xx_compressed.datagen.model.ModItemModelProvider;
import com.yukke9265.cobblestone_xx_compressed.datagen.recipe.ModRecipeProvider;
import com.yukke9265.cobblestone_xx_compressed.datagen.tag.ModBlockTagProvider;
import com.yukke9265.cobblestone_xx_compressed.datagen.tag.ModItemTagProvider;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class ModDatagen {
    private ModDatagen() {
    }

    // datagen 実行時に、ここから各 provider を登録します。
    // provider を増やしたいときは、このクラスへ createProvider(...) を足していくと追いやすいです。
    public static void gatherData(GatherDataEvent event) {
        if (event.includeClient()) {
            // ブロックの blockstate / block model 生成の入口です。
            event.createProvider(output -> new ModBlockStateProvider(output, event.getExistingFileHelper()));

            // 通常アイテムや BlockItem の item model 生成の入口です。
            event.createProvider(output -> new ModItemModelProvider(output, event.getExistingFileHelper()));

            // 言語ファイルも datagen で管理します。
            // main/resources と generated/resources の両方に同じキーを置くと追跡しづらいため、
            // 今回は en_us をこの provider 側へまとめます。
            event.createProvider(output -> new ModEnglishLanguageProvider(output));
            event.createProvider(output -> new ModJapaneseLanguageProvider(output));
        }

        if (event.includeServer()) {
            // レシピ JSON を生成する入口です。
            // このプロジェクトでは、作業台で使う通常クラフトレシピと、
            // 機械が読む独自レシピの両方を ModRecipeProvider へまとめています。
            // 新しい機械レシピを datagen 対応するときは、まずその機械用の
            // buildXxxRecipes(...) と saveXxxRecipe(...) を ModRecipeProvider 側へ足します。
            event.createProvider(ModRecipeProvider::new);

            // ブロックタグを datagen 側に寄せます。
            // これで適正ツールの指定も Java 側からまとめて更新できます。
            var blockTagProvider = event.createProvider(
                (output, lookupProvider) -> new ModBlockTagProvider(output, lookupProvider, event.getExistingFileHelper()));

            // item 側の互換タグも datagen でまとめて管理します。
            // forge / c の dust タグへ粉アイテムを流して、外部 mod の材料指定に合わせます。
            event.createProvider(
                (output, lookupProvider) -> new ModItemTagProvider(output, lookupProvider, blockTagProvider.contentsGetter(), event.getExistingFileHelper()));

            // ルートテーブルも provider から出すようにして、
            // generated JSON を直接編集しなくても済むようにします。
            event.createProvider(ModBlockLootTableProvider::new);
        }
    }
}