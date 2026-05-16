# 発光圧縮ブロック実装例

この文書では、このプロジェクトに発光する圧縮ブロックを 1 つ追加する場合の実装イメージをまとめます。

今回は、`glowing_compressed_cobblestone` という単純な発光ブロックを例にします。
これは状態を持たない単純ブロックなので、今の `ModBlocks` ベースでも比較的追加しやすいです。

## 1. この例が向いている理由

今のプロジェクトは、まだ単純ブロック中心です。
そのため、次の条件を満たす題材が練習に向いています。

- BlockEntity が不要
- GUI が不要
- でも普通のブロックより設定差が分かる

発光圧縮ブロックはこの条件に合っています。

## 2. Java 側の登録例

まず [src/main/java/com/yukke9265/cobblestone_xx_compressed/ModBlocks.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/ModBlocks.java) に、次のようなブロック登録を追加します。

```java
public static final DeferredBlock<Block> GLOWING_COMPRESSED_COBBLESTONE =
    BLOCKS.registerSimpleBlock(
        "glowing_compressed_cobblestone",
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(2.0F, 6.0F)
            .sound(SoundType.STONE)
            .lightLevel(state -> 15)
            .requiresCorrectToolForDrops()
    );
```

## 3. BlockItem の登録例

次に [src/main/java/com/yukke9265/cobblestone_xx_compressed/ModItems.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/ModItems.java) で BlockItem を登録します。

```java
public static final DeferredItem<BlockItem> GLOWING_COMPRESSED_COBBLESTONE_ITEM =
    ModItems.ITEMS.registerSimpleBlockItem(
        "glowing_compressed_cobblestone",
        ModBlocks.GLOWING_COMPRESSED_COBBLESTONE
    );
```

ここまでで、ブロック本体とインベントリ用アイテムがそろいます。

## 4. 何を設定しているか

この例のプロパティは次の意味です。

- `mapColor(MapColor.STONE)`: 石系の色として扱う
- `strength(2.0F, 6.0F)`: ある程度硬く、爆発にもそこそこ強い
- `sound(SoundType.STONE)`: 石の音にする
- `lightLevel(state -> 15)`: 強く発光させる
- `requiresCorrectToolForDrops()`: 適切なツールが必要

つまり、「石系の発光圧縮ブロック」を意図した設定です。

## 5. クリエイティブタブへの追加

[src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java) のタブ追加処理にも表示登録が必要です。

独自タブなら、`displayItems` に次を追加します。

```java
output.accept(ModItems.GLOWING_COMPRESSED_COBBLESTONE_ITEM.get());
```

バニラの建築ブロックタブへ出すなら、`addCreative(...)` にも追加します。

```java
event.accept(ModItems.GLOWING_COMPRESSED_COBBLESTONE_ITEM.get());
```

## 6. 言語ファイル

[src/main/resources/assets/cobblestonexxcompressed/lang/en_us.json](src/main/resources/assets/cobblestonexxcompressed/lang/en_us.json) に表示名を追加します。

```json
"block.cobblestonexxcompressed.glowing_compressed_cobblestone": "Glowing Compressed Cobblestone"
```

## 7. blockstates JSON

状態を持たない単純ブロックなので、blockstates は単純な `variants` 1 個で十分です。

配置先:

`src/main/resources/assets/cobblestonexxcompressed/blockstates/glowing_compressed_cobblestone.json`

内容例:

```json
{
  "variants": {
    "": {
      "model": "cobblestonexxcompressed:block/glowing_compressed_cobblestone"
    }
  }
}
```

## 8. block model JSON

6 面同じテクスチャなら、`cube_all` が一番簡単です。

配置先:

`src/main/resources/assets/cobblestonexxcompressed/models/block/glowing_compressed_cobblestone.json`

内容例:

```json
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "cobblestonexxcompressed:block/glowing_compressed_cobblestone"
  }
}
```

## 9. item model JSON

配置先:

`src/main/resources/assets/cobblestonexxcompressed/models/item/glowing_compressed_cobblestone.json`

内容例:

```json
{
  "parent": "cobblestonexxcompressed:block/glowing_compressed_cobblestone"
}
```

## 10. テクスチャ

配置先:

`src/main/resources/assets/cobblestonexxcompressed/textures/block/glowing_compressed_cobblestone.png`

発光ブロックでも、まずは普通の 1 枚テクスチャで十分です。
見た目をもっと光らせたくなったら、後でテクスチャやモデルを調整していけばよいです。

## 11. loot table

壊したときに自分自身を落とすなら、通常の単純ブロックと同じ形で作れます。

配置先:

`src/main/resources/data/cobblestonexxcompressed/loot_table/blocks/glowing_compressed_cobblestone.json`

内容例:

```json
{
  "type": "minecraft:block",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "cobblestonexxcompressed:glowing_compressed_cobblestone"
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

## 12. レシピを付けるなら

レシピを付ける場合は、たとえば「圧縮丸石 + 光源ブロック」で作る形が分かりやすいです。

例:

- `compressed_cobblestone` 8 個
- `glowstone` 1 個

このあたりは [recipe-setup.md](./recipe-setup.md) の形で JSON を追加すれば進められます。

## 13. この例がよい練習になる理由

このブロックは、単純ブロックの範囲のまま次のことを練習できます。

1. `lightLevel(...)` の使い方
2. BlockItem 登録
3. blockstates / models / loot table の一式
4. 建築ブロックとしての基本構成

つまり、状態付きブロックや機械ブロックへ進む前の中間課題としてちょうどよいです。

## 14. よくあるミス

### `lightLevel(...)` を入れたのに見た目が普通すぎる

発光量と見た目の派手さは別です。まずは「実際に周囲が明るくなるか」で確認します。

### BlockItem を追加していない

ワールドに置く前に、インベントリから取り出せなくなります。

### 建築ブロックタブへ追加していない

登録は成功していても、見つけにくくなります。

### loot table を忘れる

壊したときに回収できず、追加失敗に見えることがあります。

## 15. 確認順

追加後は、次の順で確認すると分かりやすいです。

1. `gradlew.bat build`
2. `gradlew.bat runClient`

ゲーム内では次を確認します。

1. タブに表示されるか
2. 置けるか
3. 周囲を実際に照らすか
4. 壊したときに回収できるか
