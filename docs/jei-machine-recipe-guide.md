# JEI に機械レシピを表示する連携手順

この文書では、このプロジェクトで**独自機械のレシピを JEI に表示する流れ**をまとめます。

ここでいう JEI 連携とは、たとえば次のようなことです。

- 圧縮機や独自かまどのレシピ一覧を JEI に出す
- 機械ブロックを JEI のレシピ触媒として表示する
- 機械 GUI の矢印部分をクリックして JEI のレシピ画面を開く
- 必要なら Menu に対してレシピ転送を追加する

このプロジェクトには、すでに次の機械まわりの土台があります。

- `CobblestoneFurnaceBlockEntity`
- `CobblestoneFurnaceMenu`
- `CobblestoneFurnaceScreen`

そのため JEI 連携では、**既存の機械処理を作り直すのではなく、既存の機械情報を JEI から見える形で登録する**、という考え方になります。

## 1. 先に知っておくこと

JEI は、機械そのものを動かすための仕組みではありません。

JEI が担当するのは主に次の 3 つです。

1. レシピカテゴリを作る
2. レシピ一覧を JEI に渡す
3. GUI と JEI をつなぐ

つまり、**JEI を入れただけで独自レシピが自動表示されるわけではありません**。

先に Mod 側で次ができている必要があります。

- 独自 `Recipe` がある
- `RecipeType` がある
- `RecipeSerializer` がある
- レシピ JSON を読み込める
- 機械側がそのレシピを使える

この土台がまだない場合は、先に [docs/machine-recipe-guide.md](./machine-recipe-guide.md) を進めた方が整理しやすいです。

## 2. このプロジェクトでの考え方

このプロジェクトの `build.gradle` には、すでに JEI 依存を追加するためのコメント例があります。

つまり、最初の一歩は次の 2 段階です。

1. Gradle に JEI 依存を入れる
2. JEI 用の plugin クラスと category クラスを追加する

ここで大事なのは、**JEI のクラスを Mod 本体の初期化処理へ直接混ぜない**ことです。

JEI 連携コードは、基本的に JEI 用クラスへ分けて閉じ込めた方が安全です。
そうしておくと、サーバー側処理や通常の機械処理と混ざりにくくなります。

## 3. 追加する主なクラス

独自機械レシピを JEI 表示する最小構成は、だいたい次の形です。

1. JEI plugin クラス
2. JEI recipe category クラス
3. 必要なら表示用の補助データクラス

たとえば Cobblestone Furnace を JEI 表示するなら、次のような名前にすると追いやすいです。

- `jei/ModJeiPlugin`
- `jei/category/CobblestoneFurnaceRecipeCategory`

機械の種類が増える場合も、JEI 側はこの分け方にしておくと整理しやすいです。

## 4. Gradle に JEI 依存を入れる

まずは `build.gradle` の `repositories` と `dependencies` を調整します。

このプロジェクトの `dependencies` には、すでに JEI 用のコメント例があります。
そのため、まずはそこを有効化する形が分かりやすいです。

例:

```gradle
repositories {
    maven {
        url = 'https://maven.blamejared.com'
    }
}

dependencies {
    compileOnly "mezz.jei:jei-${mc_version}-common-api:${jei_version}"
    compileOnly "mezz.jei:jei-${mc_version}-neoforge-api:${jei_version}"
    localRuntime "mezz.jei:jei-${mc_version}-neoforge:${jei_version}"
}
```

役割は次のとおりです。

- `compileOnly`: JEI API をコンパイル時だけ使う
- `localRuntime`: 開発実行時だけ JEI 本体を読み込む

このプロジェクトでは `localRuntime` が用意されているので、**開発用には必要だが、この Mod の公開依存にはしたくない**という JEI の使い方に合っています。

## 5. gradle.properties に JEI バージョンを持たせる

管理しやすさを優先するなら、`gradle.properties` に JEI バージョン変数を追加すると見返しやすいです。

例:

```properties
jei_version=適合するJEIの版
```

Minecraft 1.21.1 と NeoForge 1.21.1 に合う版は時期によって変わるため、ここは JEI の公開ページや配布先で確認してください。

## 6. JEI plugin クラスを作る

JEI との接続の入口になるのが `IModPlugin` 実装です。

このクラスには `@JeiPlugin` を付けます。

役割は主に次のとおりです。

- カテゴリ登録
- レシピ登録
- 触媒登録
- GUI クリック領域登録
- 必要ならレシピ転送登録

最小形は次のイメージです。

```java
package com.yukke9265.cobblestone_xx_compressed.jei;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.category.CobblestoneFurnaceRecipeCategory;
import com.yukke9265.cobblestone_xx_compressed.menu.CobblestoneFurnaceMenu;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import com.yukke9265.cobblestone_xx_compressed.registry.ModMenuType;
import com.yukke9265.cobblestone_xx_compressed.registry.ModRecipeTypes;
import com.yukke9265.cobblestone_xx_compressed.screen.CobblestoneFurnaceScreen;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    public static final RecipeType<CobblestoneFurnaceRecipe> COBBLESTONE_FURNACE_JEI_RECIPE_TYPE =
        RecipeType.createFromVanilla(
            BuiltInRegistries.RECIPE_TYPE.getKey(ModRecipeTypes.COBBLESTONE_FURNACE.get()),
            CobblestoneFurnaceRecipe.class
        );

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CobblestoneFurnaceRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        List<CobblestoneFurnaceRecipe> recipes = minecraft.level
            .getRecipeManager()
            .getAllRecipesFor(ModRecipeTypes.COBBLESTONE_FURNACE.get())
            .stream()
            .map(holder -> holder.value())
            .toList();

        registration.addRecipes(COBBLESTONE_FURNACE_JEI_RECIPE_TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COBBLESTONE_FURNACE.get()), COBBLESTONE_FURNACE_JEI_RECIPE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CobblestoneFurnaceScreen.class, 79, 34, 24, 17, COBBLESTONE_FURNACE_JEI_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
            CobblestoneFurnaceMenu.class,
            ModMenuType.COBBLESTONE_FURNACE_MENU.get(),
            COBBLESTONE_FURNACE_JEI_RECIPE_TYPE,
            0,
            1,
            2,
            36
        );
    }
}
```

## 7. plugin クラスでやっていること

### `getPluginUid()`

この plugin を JEI が識別するための ID を返します。

### `registerCategories(...)`

JEI の表示カテゴリを登録します。

たとえば次のような 1 カテゴリが 1 機械に対応する、と考えると分かりやすいです。

- Cobblestone Furnace 用カテゴリ
- Compressor 用カテゴリ
- Crusher 用カテゴリ

### `registerRecipes(...)`

Minecraft の `RecipeManager` から、対象 `RecipeType` のレシピ一覧を取り出して JEI に渡します。

ここで重要なのは、**JEI に渡す内容は JEI 専用データではなく、すでに読み込まれている Mod の独自レシピそのもの**でよい、という点です。

### `registerRecipeCatalysts(...)`

触媒を登録します。

触媒とは、JEI の画面で「このブロックからこのレシピ一覧を開ける」という入口になるアイテムです。

機械ブロック本体を登録するのが一番分かりやすいです。

### `registerGuiHandlers(...)`

機械 GUI の特定座標をクリックしたときに、JEI のレシピ表示を開けるようにします。

多くの場合は、GUI 上の矢印や進行バーの位置を指定します。

### `registerRecipeTransferHandlers(...)`

これは任意です。

対応する `Menu` があり、JEI から入力スロットへ自動配置したいときに使います。

引数の `0, 1, 2, 36` は次の意味です。

- 機械入力スロット開始 index
- 機械入力スロット数
- プレイヤー所持品開始 index
- プレイヤー所持品スロット数

今の `CobblestoneFurnaceMenu` は、入力が 0、出力が 1、プレイヤー所持品が 2 以降なので、この形が合いやすいです。

## 8. recipe category クラスを作る

実際の見た目を決めるのが `IRecipeCategory<T>` 実装です。

ここでは主に次を決めます。

- カテゴリ名
- 背景画像
- アイコン
- 入力スロット位置
- 出力スロット位置

最小形は次のようなイメージです。

```java
package com.yukke9265.cobblestone_xx_compressed.jei.category;

import com.yukke9265.cobblestone_xx_compressed.CobblestonexXCompressed;
import com.yukke9265.cobblestone_xx_compressed.jei.ModJeiPlugin;
import com.yukke9265.cobblestone_xx_compressed.recipe.CobblestoneFurnaceRecipe;
import com.yukke9265.cobblestone_xx_compressed.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CobblestoneFurnaceRecipeCategory implements IRecipeCategory<CobblestoneFurnaceRecipe> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CobblestonexXCompressed.MODID, "textures/gui/cobblestone_furnace.png");

    private final IDrawable background;
    private final IDrawable icon;

    public CobblestoneFurnaceRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 26, 16, 124, 54);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.COBBLESTONE_FURNACE.get()));
    }

    @Override
    public RecipeType<CobblestoneFurnaceRecipe> getRecipeType() {
        return ModJeiPlugin.COBBLESTONE_FURNACE_JEI_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.cobblestonexxcompressed.cobblestone_furnace");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CobblestoneFurnaceRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 18)
            .addIngredients(recipe.getIngredient());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 18)
            .addItemStack(recipe.getResultItem(null));
    }
}
```

## 9. category クラスで意識すること

### 入力は `Ingredient` をそのまま使う

独自 `Recipe` が `Ingredient` を持っているなら、JEI 側でもそれを直接使うのが簡単です。

### 出力は表示用 `ItemStack` を返す

JEI は表示が目的なので、実際の機械処理とは別に、表示用として安全に取り出せる `ItemStack` があると扱いやすいです。

### 背景は既存 GUI を切り出すと分かりやすい

すでに `CobblestoneFurnaceScreen` 用のテクスチャがあるなら、その一部を切り出して再利用すると見た目が揃います。

## 10. 独自 Recipe に用意しておくと楽なもの

JEI 連携を楽にするため、独自 `Recipe` には次のような取得メソッドがあると扱いやすいです。

- `Ingredient getIngredient()`
- `ItemStack getResultItem(HolderLookup.Provider provider)` または表示用結果を返すメソッド
- `int getProcessingTime()`
- `float getExperience()`

この情報が取り出しやすいほど、JEI 側では表示組み立てに集中できます。

## 11. Menu と Screen をどう結びつけるか

このプロジェクトの `CobblestoneFurnaceMenu` は、次の構成です。

- 入力スロット: 0
- 出力スロット: 1
- プレイヤー所持品開始: 2

そのため、JEI 側の転送設定を入れるなら、入力 1 スロット機械として扱いやすいです。

また `CobblestoneFurnaceScreen` に対しては、GUI 上の矢印位置をクリック領域にすると直感的です。

ここで座標がずれる場合は、次を確認します。

1. Screen テクスチャの左上基準座標
2. 矢印画像の x, y
3. 幅と高さ

## 12. クライアント専用として意識すること

JEI 連携コードは**基本的にクライアント表示用**です。

そのため、次の点を混ぜないようにします。

- サーバーでしか動かない機械処理
- Mod 本体初期化での JEI クラス直接参照
- BlockEntity の tick 処理の中に JEI 依存を書くこと

JEI 用クラスは `jei` パッケージのような場所にまとめ、通常処理から切り離す方が安全です。

## 13. よくある実装順

初心者向けには、次の順が追いやすいです。

1. 独自レシピ基盤を完成させる
2. JEI 依存を追加する
3. `ModJeiPlugin` を作る
4. `RecipeCategory` を 1 つ作る
5. 触媒を登録する
6. GUI クリック領域を登録する
7. 必要ならレシピ転送を追加する

最初から転送まで全部入れなくても、**まずは一覧表示だけ通す**方が切り分けしやすいです。

## 14. よくあるミス

### レシピ本体が未完成なのに JEI から始める

JEI は表示層なので、土台の `RecipeType` や `RecipeSerializer` がないと進みません。

### plugin クラスを通常初期化コードから呼び出そうとする

JEI plugin は JEI 側が検出するものです。
Mod 本体から明示的に new したり登録したりしない方が安全です。

### GUI 座標を適当に入れてクリック領域がずれる

JEI のクリック領域は、Screen 内の実際の描画位置に合わせる必要があります。

### レシピ転送の index を間違える

`Menu` 側のスロット番号と JEI 側の指定がずれると、転送がうまく動きません。

## 15. 確認手順

このプロジェクトでの確認は、次の順が分かりやすいです。

1. `gradlew.bat build` でコンパイルが通るか確認する
2. `gradlew.bat runClient` で JEI 入りクライアントを起動する
3. JEI で機械ブロックからレシピ一覧を開けるか確認する
4. 機械 GUI の矢印クリックで同じカテゴリが開くか確認する
5. 転送を入れたなら、入力スロットへ自動配置できるか確認する

最初の確認では、次だけ通れば十分です。

- JEI にカテゴリが見える
- レシピが 1 件以上見える
- クラッシュしない

## 16. このプロジェクトで最初に狙うとよい完成形

最初の到達点としては、次くらいがちょうどよいです。

1. `CobblestoneFurnace` の独自レシピを 1 種類作る
2. JEI にその一覧を表示する
3. `Cobblestone Furnace` ブロックを触媒登録する
4. `CobblestoneFurnaceScreen` の矢印から JEI を開けるようにする

ここまでできれば、あとから Compressor や Crusher を増やすときも、同じ形を横展開しやすくなります。
