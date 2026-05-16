# アイテム追加手順

この文書では、このプロジェクトに**新しい通常アイテムを 1 つ追加する流れ**を、できるだけ順番に追える形で説明します。

対象の前提は次の通りです。

- Minecraft 1.21.1
- NeoForge 1.21.1
- Java 21
- このプロジェクトの modid は `cobblestonexxcompressed`

## 1. まず今の構成を把握する

現在のコードでは、アイテム登録に関係するファイルが次のように分かれています。

### `ModItems.java`

`DeferredRegister.Items` を使って、アイテムを登録する専用クラスです。
今は `example_item1` がここで登録されています。

```java
public class ModItems {
    public static final DeferredRegister.Items ITEMS = 
        DeferredRegister.createItems(CobblestonexXCompressed.MODID);

    public static final DeferredItem<Item> EXAMPLE_ITEM = 
        ITEMS.registerSimpleItem("example_item1", new Item.Properties().stacksTo(64));
}
```

### `CobblestonexXCompressed.java`

Mod 全体の初期化を行うクラスです。
この中で `ModItems.ITEMS.register(modEventBus);` を呼んでいるため、`ModItems.java` に追加したアイテムが実際にゲームへ登録されます。

```java
// ModItemsクラスのアイテムも登録します
ModItems.ITEMS.register(modEventBus);
```

つまり、**通常アイテムを増やすだけなら、基本的には `ModItems.java` に定義を追加すればよい**です。

## 2. 一番小さい追加単位を理解する

通常アイテムの追加で最低限必要になるのは、次の 3 つです。

1. Java 側でアイテムを登録する
2. 言語ファイルに表示名を追加する
3. 見た目用のモデル JSON とテクスチャを置く

レシピまで付けたい場合は、これに加えてレシピ JSON が必要です。

## 3. Java 側でアイテムを登録する

ここでは例として、`compressed_cobblestone` という名前のアイテムを追加する流れを書きます。

### 言語ファイルの追加先

`src/main/java/com/yukke9265/cobblestone_xx_compressed/ModItems.java`

### 言語ファイルの追加例

```java
public static final DeferredItem<Item> COMPRESSED_COBBLESTONE =
    ITEMS.registerSimpleItem("compressed_cobblestone", new Item.Properties().stacksTo(64));
```

### 何をしているか

- `compressed_cobblestone` がゲーム内 ID のパス部分になります
- 完全な ID は `cobblestonexxcompressed:compressed_cobblestone` になります
- `new Item.Properties().stacksTo(64)` は、通常アイテムとして最大 64 個まで重ねられる設定です

### 命名の注意

- Java の定数名は大文字の `COMPRESSED_COBBLESTONE`
- 登録名は小文字スネークケースの `compressed_cobblestone`

Minecraft のリソースやデータファイルは、基本的にこの登録名を基準にそろえると追いやすくなります。

## 4. クリエイティブタブに表示させる

このプロジェクトには独自タブ `EXAMPLE_TAB` があり、そこに表示するアイテムを `displayItems` の中で追加しています。

現在は次のような形です。

```java
.displayItems((parameters, output) -> {
    output.accept(EXAMPLE_ITEM.get());
    output.accept(ModItems.EXAMPLE_ITEM.get());
}).build());
```

新しいアイテムを独自タブに出したい場合は、ここへ次のように追加します。

```java
output.accept(ModItems.COMPRESSED_COBBLESTONE.get());
```

### ここで大事なこと

- 登録そのものは `ModItems.java` が担当します
- クリエイティブタブへ見せるかどうかは `CobblestonexXCompressed.java` 側で決まります

つまり、**登録しただけではタブに見えないことがある**ので、この追加を忘れないようにします。

## 5. 言語ファイルに名前を追加する

ゲーム内で人が読める表示名を出すには、言語ファイルへ翻訳キーを追加します。

### モデル JSON の追加先

`src/main/resources/assets/cobblestonexxcompressed/lang/en_us.json`

### 追加例

```json
"item.cobblestonexxcompressed.compressed_cobblestone": "Compressed Cobblestone"
```

### ルール

- アイテムの翻訳キーは `item.<modid>.<登録名>`
- 今回なら `item.cobblestonexxcompressed.compressed_cobblestone`

日本語表示も欲しい場合は、後で `ja_jp.json` を追加して同じキーに日本語名を入れます。

## 6. アイテムモデル JSON を追加する

アイテムの見た目を表示するには、モデル JSON が必要です。

### テクスチャの追加先

`src/main/resources/assets/cobblestonexxcompressed/models/item/compressed_cobblestone.json`

### Properties 設定の例

```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "cobblestonexxcompressed:item/compressed_cobblestone"
  }
}
```

### 何を意味しているか

- `minecraft:item/generated` は、通常の平面アイテム用の親モデルです
- `layer0` に指定したテクスチャが実際の見た目になります

## 7. テクスチャを追加する

モデル JSON が参照する PNG ファイルを置きます。

### 追加先

`src/main/resources/assets/cobblestonexxcompressed/textures/item/compressed_cobblestone.png`

### 注意

- ファイル名は登録名とそろえると分かりやすいです
- モデル JSON の `layer0` と名前が一致していないと紫黒の欠損テクスチャになります

## 8. 必要なら特別な設定を付ける

`Item.Properties()` にはいろいろな設定を足せます。

### 例

```java
new Item.Properties().stacksTo(16)
```

これは最大スタック数を 16 にする例です。

このプロジェクト内にも、`CobblestonexXCompressed.java` で食料アイテムの例があります。

```java
public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem(
    "example_item",
    new Item.Properties().food(new FoodProperties.Builder()
        .alwaysEdible().nutrition(1).saturationModifier(2f).build())
);
```

食べ物、耐久値付きアイテム、特殊処理付きアイテムにしたい場合は、このように `Properties` を増やしたり、`Item` を継承した独自クラスを作ったりします。

## 9. 追加後に確認するポイント

アイテムを追加したのにゲーム内で見えない場合は、次の順で確認すると切り分けしやすいです。

1. Java 側で登録名が正しいか
2. `ModItems.ITEMS.register(modEventBus);` が有効なままか
3. クリエイティブタブへ `output.accept(...)` を追加したか
4. 言語キーが `item.<modid>.<登録名>` になっているか
5. モデル JSON のファイル名と中身のテクスチャ名が一致しているか
6. テクスチャ PNG が正しい場所にあるか

## 10. よくあるミス

### 登録名とファイル名がずれる

たとえば Java 側が `compressed_cobblestone` なのに、モデルやテクスチャが `compressedCobblestone` だと読み込まれません。

### 言語キーの先頭が違う

- アイテムは `item.`
- ブロックは `block.`

ここを間違えると、ゲーム内では翻訳キーそのものが表示されます。

### クリエイティブタブの追加漏れ

登録されていても、タブに出していなければ「追加されていない」ように見えることがあります。

## 11. 最小構成の完成イメージ

新しい `compressed_cobblestone` を追加する場合、最低限そろうファイルは次の通りです。

- `src/main/java/com/yukke9265/cobblestone_xx_compressed/ModItems.java`
- `src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java`
- `src/main/resources/assets/cobblestonexxcompressed/lang/en_us.json`
- `src/main/resources/assets/cobblestonexxcompressed/models/item/compressed_cobblestone.json`
- `src/main/resources/assets/cobblestonexxcompressed/textures/item/compressed_cobblestone.png`

## 12. 実際の作業順のおすすめ

初心者のうちは、次の順で追加すると失敗箇所を特定しやすいです。

1. `ModItems.java` に登録を書く
2. クリエイティブタブに追加する
3. `en_us.json` に表示名を書く
4. モデル JSON を置く
5. テクスチャ PNG を置く
6. ゲームを起動して表示確認する
7. 必要ならレシピを追加する

レシピの追加方法は [recipe-setup.md](./recipe-setup.md) を参照してください。
