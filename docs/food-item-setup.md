# 食料アイテム設定手順

この文書では、このプロジェクトで**食料アイテムを追加する方法**をまとめます。

通常アイテムとの違いは、`Item.Properties()` に `food(...)` を付けて、`FoodProperties.Builder` で食べ物としての設定を与える点です。

このプロジェクトにはすでに食料アイテムの例があります。

```java
public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem(
    "example_item",
    new Item.Properties().food(
        new FoodProperties.Builder()
            .alwaysEdible()
            .nutrition(1)
            .saturationModifier(2f)
            .build()
    )
);
```

まずはこの形を基準にして、必要な設定を増やすのが分かりやすいです。

## 1. 食料アイテムで最低限必要なこと

食料アイテムを 1 つ追加する場合、最低限必要なのは次の 4 つです。

1. Java 側で food 設定付きのアイテムを登録する
2. 言語ファイルへ表示名を追加する
3. item model JSON を追加する
4. テクスチャ PNG を追加する

追加場所の基本は通常アイテムと同じです。

- 登録: `src/main/java/com/yukke9265/cobblestone_xx_compressed/ModItems.java`
- 表示名: `src/main/resources/assets/cobblestonexxcompressed/lang/en_us.json`
- モデル: `src/main/resources/assets/cobblestonexxcompressed/models/item/`
- テクスチャ: `src/main/resources/assets/cobblestonexxcompressed/textures/item/`

## 2. 一番基本の食料アイテム

ここでは例として `cobblestone_soup` という食料アイテムを追加する形で説明します。

### alwaysEdible の追加例

```java
public static final DeferredItem<Item> COBBLESTONE_SOUP = ITEMS.registerSimpleItem(
    "cobblestone_soup",
    new Item.Properties().food(
        new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.6f)
            .build()
    )
);
```

### 何をしているか

- `nutrition(6)` は回復する満腹度です
- `saturationModifier(0.6f)` は隠し満腹度の補正です
- `build()` で food 設定を完成させています

最初は `nutrition` と `saturationModifier` だけで作ると理解しやすいです。

## 3. `nutrition` と `saturationModifier` の考え方

### `nutrition(int)`

見た目に分かりやすい満腹度回復量です。
値が大きいほど、お腹のアイコンが多く回復します。

### `saturationModifier(float)`

すぐには見えない「腹持ちのよさ」に関係する値です。
大きいほど、次に空腹になるまでの余裕が増えます。

### 最初の目安

初心者のうちは、まず次のような感覚で十分です。

- 軽食: `nutrition(2)` 前後
- 普通の食事: `nutrition(5)` から `nutrition(6)` 前後
- 強めの食事: `nutrition(8)` 以上

`saturationModifier` は `0.3f` から `0.8f` くらいで試し、ゲーム内で調整するのが現実的です。

## 4. 空腹でなくても食べられるようにする

通常は、お腹が減っていないと食べられません。
これを無視して食べられるようにするのが `alwaysEdible()` です。

### fast の追加例

```java
public static final DeferredItem<Item> MAGIC_CANDY = ITEMS.registerSimpleItem(
    "magic_candy",
    new Item.Properties().food(
        new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.2f)
            .alwaysEdible()
            .build()
    )
);
```

### fast が向いているアイテム

- 回復用の飴
- バフ目的で食べるアイテム
- 普通の食事よりも特殊効果を重視するアイテム

## 5. 早く食べられるようにする

食べる時間を短くしたい場合は `fast()` を使えます。

### 良い効果の追加例

```java
public static final DeferredItem<Item> QUICK_SNACK = ITEMS.registerSimpleItem(
    "quick_snack",
    new Item.Properties().food(
        new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.1f)
            .fast()
            .build()
    )
);
```

### 向いているアイテム

- お菓子
- 軽食
- 戦闘中や移動中の素早い補助食料

## 6. 効果を付ける基本

食料アイテムに効果を付けるには、`FoodProperties.Builder.effect(...)` を使います。

NeoForge 1.21.x の `FoodProperties.Builder` では、次の形が使えます。

```java
.effect(() -> new MobEffectInstance(効果, 時間, 強さ), 発生確率)
```

### 重要な点

- 効果は `MobEffectInstance` で指定します
- `発生確率` は `0.0f` から `1.0f` の範囲です
- `1.0f` なら必ず発生します
- `0.5f` なら 50% の確率です

## 7. 必ず発動する良い効果を付ける

たとえば、食べると必ず再生効果が付く食料は次のように書けます。

### 確率効果の追加例

```java
public static final DeferredItem<Item> HEALING_BREAD = ITEMS.registerSimpleItem(
    "healing_bread",
    new Item.Properties().food(
        new FoodProperties.Builder()
            .nutrition(5)
            .saturationModifier(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 1.0f)
            .build()
    )
);
```

### 確率効果の例の意味

- `MobEffects.REGENERATION` は再生効果です
- `200` は効果時間です
- `0` はレベル 1 相当です
- `1.0f` なので必ず発動します

## 8. 一定確率で効果を付ける

怪しいキノコ料理のように、確率で効果を出したい場合もあります。

### 複数効果の追加例

```java
public static final DeferredItem<Item> STRANGE_STEW = ITEMS.registerSimpleItem(
    "strange_stew",
    new Item.Properties().food(
        new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.4f)
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0), 0.5f)
            .build()
    )
);
```

### この例の意味

- 50% の確率で暗視が付きます
- 毎回必ず同じ結果にしない食料を作りたいときに向いています

## 9. 良い効果と悪い効果を混ぜる

食料 1 つに複数効果を付けることもできます。

### usingConvertsTo の追加例

```java
public static final DeferredItem<Item> RISKY_MEAL = ITEMS.registerSimpleItem(
    "risky_meal",
    new Item.Properties().food(
        new FoodProperties.Builder()
            .nutrition(8)
            .saturationModifier(0.8f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.3f)
            .build()
    )
);
```

### この書き方のポイント

- `effect(...)` は複数回つなげられます
- 良い効果と悪い効果を混ぜる設計もできます
- 強い効果を付けるなら、悪い効果や低確率化でバランスを取りやすいです

## 10. 食べたあとに別アイテムへ変える

スープやジュースのように、食べたあとで容器を返したい場合は `usingConvertsTo(...)` を使います。

### 追加例

```java
public static final DeferredItem<Item> COBBLESTONE_SOUP = ITEMS.registerSimpleItem(
    "cobblestone_soup",
    new Item.Properties().food(
        new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.6f)
            .usingConvertsTo(Items.BOWL)
            .build()
    )
);
```

### usingConvertsTo が向いているアイテム

- スープ
- シチュー
- 瓶入り飲み物
- バケツ入り食品のような特殊アイテム

## 11. よく使う効果の例

食料アイテムで付けやすい効果の例です。

- `MobEffects.REGENERATION`: 回復系
- `MobEffects.MOVEMENT_SPEED`: 移動補助
- `MobEffects.DAMAGE_BOOST`: 攻撃補助
- `MobEffects.NIGHT_VISION`: 探索補助
- `MobEffects.FIRE_RESISTANCE`: 耐性系
- `MobEffects.POISON`: デメリット
- `MobEffects.HUNGER`: デメリット
- `MobEffects.CONFUSION`: ネタ寄りやリスク付き食料向け

## 12. 必要な import の例

効果付き食料を作るときは、少なくとも次の import が必要になりやすいです。

```java
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
```

`Items.BOWL` などを使わない場合は `Items` の import は不要です。

## 13. どこに書くのがよいか

このプロジェクトでは、通常アイテムを `ModItems.java` に寄せる方針が分かりやすいです。
そのため、新しい食料アイテムもまずは `ModItems.java` に追加するのがおすすめです。

今の `CobblestonexXCompressed.java` にも食料アイテムの例がありますが、今後数が増えるなら `ModItems.java` 側へ寄せていく方が整理しやすくなります。

## 14. 食料アイテム追加後に必要なリソース

食料アイテムでも、通常アイテムと同じく次のリソースが必要です。

### 言語ファイルの例

```json
"item.cobblestonexxcompressed.cobblestone_soup": "Cobblestone Soup"
```

### item model JSON の例

`src/main/resources/assets/cobblestonexxcompressed/models/item/cobblestone_soup.json`

```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "cobblestonexxcompressed:item/cobblestone_soup"
  }
}
```

### テクスチャの配置先

`src/main/resources/assets/cobblestonexxcompressed/textures/item/cobblestone_soup.png`

## 15. よくあるミス

### `stacksTo(64)` だけ付けて food を付けていない

これだと普通のアイテムであって、食べ物にはなりません。

### `effect(...)` の確率を間違える

`1.0f` は 100%、`0.1f` は 10% です。整数感覚で書くと意図がずれます。

### 効果付き食料なのに `alwaysEdible()` を付け忘れる

空腹時以外でも使いたい設計なら忘れやすい点です。

### 容器を返す想定なのに `usingConvertsTo(...)` がない

スープ系では特に起こりやすいです。

## 16. 最初に試すならこの 3 パターン

初心者のうちは、まず次の 3 種類を試すと理解しやすいです。

1. 普通の食料
2. 必ず良い効果が付く食料
3. 容器を返す食料

たとえば次のような順です。

1. `cobblestone_cookie`
2. `healing_bread`
3. `cobblestone_soup`

## 17. このプロジェクトでのおすすめ確認順

食料アイテムを追加したら、次の順で確認すると切り分けしやすいです。

1. `gradlew.bat build`
2. `gradlew.bat runClient`

ゲーム内では次を確認します。

1. クリエイティブタブに表示されるか
2. ちゃんと食べられるか
3. 満腹度と腹持ちが意図どおりか
4. 効果が想定どおり付くか
5. 容器を返す場合は食後に戻るか
