# ブロック設定手順

この文書では、このプロジェクトでブロックの性質をどう設定するかをまとめます。

ブロック追加そのものの流れは [block-addition.md](./block-addition.md) に書いてあります。
ここでは、その中でも特に `BlockBehaviour.Properties` で何を決めるかに絞って整理します。

今のプロジェクトでは、[src/main/java/com/yukke9265/cobblestone_xx_compressed/ModBlocks.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/ModBlocks.java) に次の例があります。

```java
public static final DeferredBlock<Block> COBBLESTONE_FURNACE =
    BLOCKS.registerSimpleBlock(
        "cobblestone_furnace",
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(1.0F)
            .requiresCorrectToolForDrops()
    );
```

この `BlockBehaviour.Properties.of()...` に設定を足していくことで、ブロックの性質が決まります。

## 1. まず覚えるべき基本形

最初は次の形を基準にすると分かりやすいです。

```java
BlockBehaviour.Properties.of()
    .mapColor(MapColor.STONE)
    .strength(1.0F)
    .requiresCorrectToolForDrops()
```

これは次の意味です。

- マップ上では石系の色として扱う
- ある程度の硬さを持つ
- 適切なツールで壊さないとドロップしない

石系、金属系、圧縮ブロック系では、この形から少しずつ足していくことが多いです。

## 2. `mapColor(...)`

`mapColor(...)` は、地図上で表示される色や一部の見た目判定に関わる設定です。

### `mapColor(...)` の例

```java
.mapColor(MapColor.STONE)
```

### `mapColor(...)` の考え方

- 石系なら `MapColor.STONE`
- 土系なら土に近い色
- 木系なら木材系に近い色

最初は「素材に近い色を選ぶ」で十分です。

## 3. `strength(...)`

`strength(...)` は、ブロックの壊れやすさと爆発耐性に関係する基本設定です。

### 1 引数版の例

```java
.strength(1.0F)
```

これは簡単な指定方法です。

### 2 引数版の例

```java
.strength(3.0F, 6.0F)
```

こちらは、壊す硬さと爆発耐性をより細かく分けたいときに使えます。

### `strength(...)` の考え方

- 値が小さいほど壊しやすい
- 値が大きいほど壊しにくい
- 爆発耐性も高くしたいなら 2 引数版を使うと分かりやすい

## 4. `destroyTime(...)` と `explosionResistance(...)`

`strength(...)` でまとめて決める代わりに、個別に指定する方法もあります。

### 個別指定の例

```java
BlockBehaviour.Properties.of()
    .destroyTime(2.0F)
    .explosionResistance(10.0F)
```

### 個別指定が向いている場面

- 壊すのは普通だけど爆発には強い、のように差を付けたいとき
- `strength(...)` より意味をはっきり書きたいとき

最初は `strength(...)` の方が分かりやすいですが、調整したくなったらこちらを使う形で十分です。

## 5. `requiresCorrectToolForDrops()`

この設定を付けると、適切なツールで壊さないとドロップしなくなります。

### `requiresCorrectToolForDrops()` の例

```java
.requiresCorrectToolForDrops()
```

### 向いているブロック

- 石系ブロック
- 金属系ブロック
- 圧縮鉱石や圧縮素材ブロック

### 付けない方がよいことが多いブロック

- 柔らかい装飾ブロック
- 手で回収してよい軽量ブロック

## 6. `sound(...)`

`sound(...)` は、設置・歩行・破壊時の音の種類を決めます。

### `sound(...)` の例

```java
.sound(SoundType.STONE)
```

### よくある使い分け

- 石系なら `SoundType.STONE`
- 木系なら `SoundType.WOOD`
- 金属系なら `SoundType.METAL`
- ガラス系なら `SoundType.GLASS`

見た目だけでなく、触った感触を決める設定なので地味ですが大事です。

## 7. `lightLevel(...)`

光るブロックにしたい場合は `lightLevel(...)` を使います。

### `lightLevel(...)` の例

```java
.lightLevel(state -> 15)
```

### 何を意味しているか

- 0 は発光なし
- 15 はかなり強い発光

### `lightLevel(...)` が向いているブロック

- ランプ
- 発光鉱石
- 特殊な魔法ブロック

今のプロジェクト方針なら、最初は固定値で十分です。

## 8. `noOcclusion()`

`noOcclusion()` は、完全な立方体としてふるまわせたくないブロックで使います。

### `noOcclusion()` の例

```java
.noOcclusion()
```

### `noOcclusion()` が向いているブロック

- 透ける見た目のブロック
- 細い形状のブロック
- 機械系でフルキューブではないモデル

単純な立方体ブロックでは、通常は不要です。

## 9. `noCollission()`

`noCollission()` を付けると、当たり判定のないブロックになります。

### `noCollission()` の例

```java
.noCollission()
```

### `noCollission()` が向いているブロック

- 装飾だけの薄い表示用ブロック
- 草や特殊エフェクト的なブロック

普通の固形ブロックに付けると、見た目があるのにすり抜けるので注意が必要です。

## 10. `friction(...)`

`friction(...)` は、ブロック表面の滑りやすさに関係します。

### `friction(...)` の例

```java
.friction(0.98F)
```

### `friction(...)` が向いているブロック

- 氷のように滑りやすい床
- 特殊な素材床

普通のブロックでは触らなくてもよい設定です。

## 11. `speedFactor(...)` と `jumpFactor(...)`

この 2 つは、ブロック上での移動やジャンプに補正をかけたいときに使います。

### `speedFactor(...)` と `jumpFactor(...)` の例

```java
.speedFactor(0.8F)
.jumpFactor(0.9F)
```

### 何が起こるか

- `speedFactor(0.8F)` で移動が少し遅くなる
- `jumpFactor(0.9F)` でジャンプが少し弱くなる

### `speedFactor(...)` と `jumpFactor(...)` が向いているブロック

- 泥のような重い床
- 加速床や減速床
- 特殊ギミックブロック

## 12. `instabreak()`

一瞬で壊れるブロックにしたいなら `instabreak()` を使います。

### `instabreak()` の例

```java
.instabreak()
```

### `instabreak()` が向いているブロック

- 軽い装飾物
- テスト用ブロック
- すぐ置き直す前提の補助ブロック

## 13. `randomTicks()`

ランダムティックを使うブロックで設定します。

### `randomTicks()` の例

```java
.randomTicks()
```

### 注意

これを付けるだけで植物成長などが勝手に実装されるわけではありません。
実際にランダムティックで何をするかは、ブロッククラス側の処理も必要です。

つまり、これは「その仕組みを使う前提を有効にする設定」です。

## 14. `ignitedByLava()`

溶岩で燃える性質を持たせたい場合に使います。

### `ignitedByLava()` の例

```java
.ignitedByLava()
```

### `ignitedByLava()` が向いているブロック

- 木や布に近い素材
- 可燃性の装飾物

石系や金属系では普通は付けません。

## 15. `replaceable()`

簡単に置き換えやすいブロックにしたい場合に使います。

### `replaceable()` の例

```java
.replaceable()
```

### `replaceable()` が向いているブロック

- 草や薄い装飾物
- 一時的な表示用ブロック

通常の建材ブロックに付けることは少ないです。

## 16. `noLootTable()` と `lootFrom(...)`

ドロップ設定まわりにもプロパティ側の指定があります。

### `noLootTable()` の例

```java
.noLootTable()
```

壊しても loot table を持たせない前提のときに使います。

### `lootFrom(...)` の例

```java
.lootFrom(() -> Blocks.STONE)
```

別ブロックの loot 設定を流用したいときに使えます。

ただし、このプロジェクトではまず JSON で loot table を明示する方が追いやすいです。

## 17. `ofFullCopy(...)` で既存ブロックを基準にする

既存ブロックに近い性質を持たせたい場合は、`of()` から全部書くより `ofFullCopy(...)` が分かりやすいことがあります。

### `ofFullCopy(...)` の例

```java
BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)
```

### `ofFullCopy(...)` が向いている場面

- 丸石にかなり近い圧縮ブロック
- バニラブロックを少しだけ調整したいとき

完全に別物を作るより、「ほぼ既存ブロックと同じ」にしたいときに便利です。

## 18. このプロジェクトでのおすすめ書き方

今の [src/main/java/com/yukke9265/cobblestone_xx_compressed/ModBlocks.java](src/main/java/com/yukke9265/cobblestone_xx_compressed/ModBlocks.java) の流れに合わせるなら、石系ブロックはまず次の形が分かりやすいです。

```java
public static final DeferredBlock<Block> EXAMPLE_STONE_BLOCK =
    BLOCKS.registerSimpleBlock(
        "example_stone_block",
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(2.0F, 6.0F)
            .sound(SoundType.STONE)
            .requiresCorrectToolForDrops()
    );
```

この形なら、色、硬さ、音、ツール要件が一目で分かります。

## 19. 最初に試すと理解しやすい 3 パターン

初心者なら、次の 3 種類を作るとプロパティの違いを理解しやすいです。

1. 石系ブロック
2. 発光ブロック
3. すり抜ける装飾ブロック

### 石系ブロックの例

```java
BlockBehaviour.Properties.of()
    .mapColor(MapColor.STONE)
    .strength(2.0F, 6.0F)
    .sound(SoundType.STONE)
    .requiresCorrectToolForDrops()
```

### 発光ブロックの例

```java
BlockBehaviour.Properties.of()
    .mapColor(MapColor.COLOR_YELLOW)
    .strength(1.5F)
    .sound(SoundType.GLASS)
    .lightLevel(state -> 15)
```

### 装飾ブロックの例

```java
BlockBehaviour.Properties.of()
    .mapColor(MapColor.NONE)
    .instabreak()
    .noCollission()
    .noOcclusion()
```

## 20. よくあるミス

### `noCollission()` を普通のブロックに付けてしまう

見た目はあるのに通り抜けられてしまいます。

### `requiresCorrectToolForDrops()` を付けたのに loot table 側を考えていない

適切なツールが必要なつもりでも、確認不足だと「壊しても戻らない」に見えることがあります。

### `lightLevel(...)` を付けたのにモデルや見た目が発光っぽくない

実際には光っていても、見た目が地味だと分かりづらいことがあります。

### `randomTicks()` だけで挙動が増えると思ってしまう

ランダムティックの処理本体は別です。プロパティだけでは動きません。

## 21. 追加後の確認順

ブロック設定を変えたあとは、次の順で確認すると切り分けしやすいです。

1. `gradlew.bat build`
2. `gradlew.bat runClient`

ゲーム内では次を確認します。

1. 壊す速度が想定どおりか
2. 正しいツールでしか回収できないか
3. 音が見た目に合っているか
4. 光るなら明るさが想定どおりか
5. 当たり判定や見た目がずれていないか
