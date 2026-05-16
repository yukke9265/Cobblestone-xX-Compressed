# 状態付きブロック手順

この文書では、このプロジェクトで状態付きブロックを追加するときの考え方をまとめます。

ここでいう「状態付きブロック」は、たとえば次のようなものです。

- 向きを持つブロック
- 点灯 / 消灯を持つブロック
- 開閉状態を持つブロック
- 水入りかどうかの状態を持つブロック

今のプロジェクトの `ModBlocks.java` にある `registerSimpleBlock(...)` は、状態を持たない単純なブロックには向いています。
一方で、状態を持つブロックは、**独自の `Block` クラスを作る構成**に切り替える方が分かりやすいです。

## 1. 状態付きブロックで増える要素

単純ブロックとの主な違いは次の通りです。

1. `Block` を継承した独自クラスを作る
2. `BlockStateProperties` で使うプロパティを決める
3. `createBlockStateDefinition(...)` で状態を登録する
4. `registerDefaultState(...)` で初期状態を決める
5. `blockstates` JSON に状態ごとのモデル割り当てを書く

つまり、状態付きブロックは「Java 側の状態定義」と「JSON 側の見た目切り替え」の両方が必要です。

## 2. 一番分かりやすい例は向き付きブロック

最初に試すなら、水平向きだけを持つブロックが分かりやすいです。

よく使うプロパティは次です。

```java
BlockStateProperties.HORIZONTAL_FACING
```

これで北・南・東・西の 4 方向を持たせられます。

## 3. 独自 Block クラスの基本形

たとえば `RotatingCompressedBlock` のようなクラスを作る場合、最小構成は次のようになります。

```java
public class RotatingCompressedBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public RotatingCompressedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
```

## 4. このコードで何をしているか

- `FACING` で向きプロパティを定義する
- `registerDefaultState(...)` で最初の向きを決める
- `createBlockStateDefinition(...)` で、このブロックがその状態を持つと宣言する

この 3 つがそろって初めて、ブロック状態として使えるようになります。

## 5. 配置した向きに合わせるには

向き付きブロックでは、プレイヤーが置いた向きに応じて状態を決めることが多いです。

その場合は、`getStateForPlacement(...)` をオーバーライドします。

```java
@Override
public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
}
```

これで、プレイヤーの向きに応じて正面が決まります。

## 6. 点灯 / 消灯状態を持たせる場合

向きではなく、オンオフ状態を持ちたい場合は `BooleanProperty` を使うことが多いです。

例:

```java
public static final BooleanProperty LIT = BlockStateProperties.LIT;
```

初期状態は次のように書けます。

```java
this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
```

そして `createBlockStateDefinition(...)` で `builder.add(LIT);` を追加します。

## 7. 複数の状態を同時に持てる

向きと点灯状態を両方持つこともできます。

例:

```java
builder.add(FACING, LIT);
```

初期状態も両方指定します。

```java
this.registerDefaultState(
    this.stateDefinition.any()
        .setValue(FACING, Direction.NORTH)
        .setValue(LIT, false)
);
```

状態が増えるほど、`blockstates` JSON 側の組み合わせも増える点に注意が必要です。

## 8. blockstates JSON の役割

状態付きブロックでは、`blockstates/<name>.json` が特に重要になります。

たとえば向き付きブロックなら、状態ごとにモデル回転を切り替えます。

```json
{
  "variants": {
    "facing=north": { "model": "cobblestonexxcompressed:block/rotating_block" },
    "facing=south": { "model": "cobblestonexxcompressed:block/rotating_block", "y": 180 },
    "facing=west": { "model": "cobblestonexxcompressed:block/rotating_block", "y": 270 },
    "facing=east": { "model": "cobblestonexxcompressed:block/rotating_block", "y": 90 }
  }
}
```

これで、同じモデルを回転して使い回せます。

## 9. 点灯状態の blockstates 例

点灯 / 消灯でモデルやテクスチャを変えたい場合は、`lit=true` と `lit=false` を分けて書きます。

```json
{
  "variants": {
    "lit=false": { "model": "cobblestonexxcompressed:block/glow_block_off" },
    "lit=true": { "model": "cobblestonexxcompressed:block/glow_block_on" }
  }
}
```

## 10. 登録は simpleBlock ではなく独自コンストラクタ呼び出しにする

状態付きブロックでは、`registerSimpleBlock(...)` ではなく、自分で `new 独自Blockクラス(...)` を作る形が分かりやすいです。

例:

```java
public static final DeferredBlock<Block> ROTATING_BLOCK = BLOCKS.register(
    "rotating_block",
    () -> new RotatingCompressedBlock(
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(2.0F, 6.0F)
            .sound(SoundType.STONE)
            .requiresCorrectToolForDrops()
    )
);
```

## 11. このプロジェクトでのおすすめの進め方

今のプロジェクトはまだ単純ブロック中心なので、最初は次の順が安全です。

1. 向きだけを持つブロックを 1 個作る
2. `blockstates` JSON の回転切り替えを確認する
3. 次に `lit` のような真偽値状態を追加する

いきなり複数状態を持つ複雑な機械ブロックへ進むより、1 つずつ増やす方が追いやすいです。

## 12. よくあるミス

### `createBlockStateDefinition(...)` に追加していない

プロパティを定義しても、ここで `builder.add(...)` しないと状態として使えません。

### `registerDefaultState(...)` を忘れる

初期状態が決まっていないと、思わぬエラーや不整合の原因になります。

### `blockstates` JSON のキー名が Java 側と合っていない

たとえば `facing=north` や `lit=true` の文字列は、Java 側の状態名と一致している必要があります。

### `registerSimpleBlock(...)` のままで済ませようとする

状態を持つブロックは、独自クラスへ切り替えた方が結果的に整理しやすいです。

## 13. 確認順

状態付きブロックを追加したら、次の順で確認すると切り分けしやすいです。

1. `gradlew.bat build`
2. `gradlew.bat runClient`

ゲーム内では次を確認します。

1. 置いた向きが意図どおりか
2. 見た目が状態ごとに切り替わるか
3. 点灯 / 消灯や開閉の状態変化が意図どおりか
