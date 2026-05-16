# ブロック追加手順

この文書では、このプロジェクトに**新しいブロックを 1 つ追加する流れ**を、現在の実装に合わせて説明します。

ここでは例として `compressed_cobblestone_block` というブロックを追加する想定で進めます。

## 1. このプロジェクトでブロック追加に必要な要素

ブロックは、通常アイテムより少しだけ関連ファイルが多いです。

最低限必要になるものは次の通りです。

1. Java 側のブロック登録
2. Java 側の BlockItem 登録
3. クリエイティブタブへの追加
4. 言語ファイルの追加
5. blockstates JSON
6. block model JSON
7. item model JSON
8. テクスチャ PNG
9. 壊したときに落ちる loot table

## 2. まず既存の例を確認する

このプロジェクトにはすでに `example_block` の例があります。

```java
public static final DeferredBlock<Block> EXAMPLE_BLOCK =
    BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));

public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM =
    ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);
```

これが、このプロジェクトでブロック追加を始めるときの基準になります。

## 3. Java 側でブロックを登録する

### blockstates JSON の追加先

`src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java`

### blockstates JSON の追加例

```java
public static final DeferredBlock<Block> COMPRESSED_COBBLESTONE_BLOCK =
    BLOCKS.registerSimpleBlock(
        "compressed_cobblestone_block",
        BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
    );

public static final DeferredItem<BlockItem> COMPRESSED_COBBLESTONE_BLOCK_ITEM =
    ITEMS.registerSimpleBlockItem("compressed_cobblestone_block", COMPRESSED_COBBLESTONE_BLOCK);
```

### 何をしているか

- `BLOCKS.registerSimpleBlock(...)` でブロック本体を登録します
- `ITEMS.registerSimpleBlockItem(...)` でインベントリに入るブロックアイテムを登録します
- ブロックだけ登録して BlockItem を作らないと、通常は手に持てません

## 4. クリエイティブタブに表示する

このプロジェクトでは、建築ブロックタブへ `example_block` を追加しています。

```java
private void addCreative(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
        event.accept(EXAMPLE_BLOCK_ITEM);
    }
}
```

新しいブロックも見せたい場合は、ここへ次を追加します。

```java
event.accept(COMPRESSED_COBBLESTONE_BLOCK_ITEM);
```

## 5. 言語ファイルを追加する

### block model JSON の追加先

`src/main/resources/assets/cobblestonexxcompressed/lang/en_us.json`

### block model JSON の追加例

```json
"block.cobblestonexxcompressed.compressed_cobblestone_block": "Compressed Cobblestone Block"
```

### 注意

- ブロックの翻訳キーは `block.<modid>.<登録名>` です
- アイテムの `item.` と混同しないようにします

## 6. blockstates JSON を追加する

ブロックがワールド内でどのモデルを使うかを定義します。

### item model JSON の追加先

`src/main/resources/assets/cobblestonexxcompressed/blockstates/compressed_cobblestone_block.json`

### item model JSON の追加例

```json
{
  "variants": {
    "": {
      "model": "cobblestonexxcompressed:block/compressed_cobblestone_block"
    }
  }
}
```

この例は、向きや状態がない単純な立方体ブロック向けです。

## 7. block model JSON を追加する

### テクスチャの追加先

`src/main/resources/assets/cobblestonexxcompressed/models/block/compressed_cobblestone_block.json`

### loot table の追加例

```json
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "cobblestonexxcompressed:block/compressed_cobblestone_block"
  }
}
```

これは 6 面すべて同じテクスチャを使う単純なブロックモデルです。

## 8. item model JSON を追加する

インベントリ内で見えるブロックアイテム用のモデルです。

### loot table の追加先

`src/main/resources/assets/cobblestonexxcompressed/models/item/compressed_cobblestone_block.json`

### 追加例

```json
{
  "parent": "cobblestonexxcompressed:block/compressed_cobblestone_block"
}
```

## 9. テクスチャを追加する

### 追加先

`src/main/resources/assets/cobblestonexxcompressed/textures/block/compressed_cobblestone_block.png`

### テクスチャ追加時の注意

- block model の `textures.all` と名前をそろえます
- ファイルがないと紫黒の欠損表示になります

## 10. loot table を追加する

ブロックは、壊したときのドロップを定義しないと何も落ちないことがあります。

### 言語ファイルの追加先

`src/main/resources/data/cobblestonexxcompressed/loot_table/blocks/compressed_cobblestone_block.json`

### 言語ファイルの追加例

```json
{
  "type": "minecraft:block",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "cobblestonexxcompressed:compressed_cobblestone_block"
        }
      ],
      "conditions": [
        {
          "condition": "minecraft:survives_explosion"
        }
      ]
    }
  ]
}
```

最初は「壊したら自分自身を 1 個落とす」形にしておくと分かりやすいです。

## 11. 最小構成の一覧

単純な立方体ブロックを 1 つ追加する場合、最低限そろうファイルは次の通りです。

- `src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java`
- `src/main/resources/assets/cobblestonexxcompressed/lang/en_us.json`
- `src/main/resources/assets/cobblestonexxcompressed/blockstates/compressed_cobblestone_block.json`
- `src/main/resources/assets/cobblestonexxcompressed/models/block/compressed_cobblestone_block.json`
- `src/main/resources/assets/cobblestonexxcompressed/models/item/compressed_cobblestone_block.json`
- `src/main/resources/assets/cobblestonexxcompressed/textures/block/compressed_cobblestone_block.png`
- `src/main/resources/data/cobblestonexxcompressed/loot_table/blocks/compressed_cobblestone_block.json`

## 12. よくあるミス

### BlockItem を作っていない

これを忘れると、インベントリやクリエイティブタブから使えません。

### item model を置いていない

ワールドでは見えても、手に持ったときやインベントリ表示で見た目が崩れます。

### loot table がない

壊しても戻ってこないので、追加に失敗したように見えることがあります。

### blockstates のモデル参照名が違う

ここがずれると、ワールド配置時の見た目が壊れます。
