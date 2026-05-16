# レシピ設定手順

この文書では、このプロジェクトで**クラフトレシピを追加する基本的な方法**を説明します。

現在のプロジェクトには、レシピ用の Java データ生成コードはまだ入っていません。
そのため、まずは **JSON ファイルを直接追加する方法** が一番分かりやすく、管理もしやすいです。

## 1. レシピファイルを置く場所

Minecraft 1.21.1 のレシピ JSON は、次の場所へ置きます。

`src/main/resources/data/cobblestonexxcompressed/recipe/`

ここで重要なのは次の 2 点です。

- `data` 配下はゲームデータ用
- `recipe` は単数形

ファイル名はレシピ ID の末尾になります。

たとえば `compressed_cobblestone.json` を置くと、レシピ ID は次のようになります。

`cobblestonexxcompressed:compressed_cobblestone`

## 2. まずは shapeless recipe から覚える

一番簡単なのは、材料の並び順を問わない `minecraft:crafting_shapeless` です。

### shaped recipe の例

丸石 9 個から `compressed_cobblestone` を 1 個作る例です。

```json
{
  "type": "minecraft:crafting_shapeless",
  "ingredients": [
    {
      "item": "minecraft:cobblestone"
    },
    {
      "item": "minecraft:cobblestone"
    },
    {
      "item": "minecraft:cobblestone"
    },
    {
      "item": "minecraft:cobblestone"
    },
    {
      "item": "minecraft:cobblestone"
    },
    {
      "item": "minecraft:cobblestone"
    },
    {
      "item": "minecraft:cobblestone"
    },
    {
      "item": "minecraft:cobblestone"
    },
    {
      "item": "minecraft:cobblestone"
    }
  ],
  "result": {
    "id": "cobblestonexxcompressed:compressed_cobblestone",
    "count": 1
  }
}
```

### 向いているケース

- 並び順を気にしない合成
- とにかく最初に動くレシピを作りたいとき

### 注意

今回のように 9 個材料を使う圧縮レシピは、実際には見た目どおり 3x3 で並べたいことが多いです。
その場合は、次の shaped recipe の方が自然です。

## 3. よく使うのは shaped recipe

材料の配置を固定したい場合は `minecraft:crafting_shaped` を使います。

### 逆レシピの例

3x3 で丸石を並べて `compressed_cobblestone` を作る例です。

```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "CCC",
    "CCC",
    "CCC"
  ],
  "key": {
    "C": {
      "item": "minecraft:cobblestone"
    }
  },
  "result": {
    "id": "cobblestonexxcompressed:compressed_cobblestone",
    "count": 1
  }
}
```

### 各項目の意味

- `pattern`: 作業台の見た目どおりの配置
- `key`: パターン内の文字が何の材料を意味するか
- `result`: 出来上がるアイテム

### 読み方

- 1 行目 `CCC`
- 2 行目 `CCC`
- 3 行目 `CCC`

つまり、3x3 全部に丸石を置くレシピです。

## 4. 圧縮解除レシピも一緒に作ると分かりやすい

圧縮系アイテムでは、逆レシピも用意することが多いです。

### 例

`compressed_cobblestone` 1 個から丸石 9 個へ戻す shapeless recipe です。

```json
{
  "type": "minecraft:crafting_shapeless",
  "ingredients": [
    {
      "item": "cobblestonexxcompressed:compressed_cobblestone"
    }
  ],
  "result": {
    "id": "minecraft:cobblestone",
    "count": 9
  }
}
```

この場合は、たとえばファイル名を `compressed_cobblestone_unpacking.json` にすると意味が分かりやすいです。

## 5. ファイル名の付け方

レシピ JSON は、後で見返したときに役割が分かる名前にしておくと管理しやすいです。

おすすめは次のような付け方です。

- `compressed_cobblestone.json`
- `compressed_cobblestone_unpacking.json`
- `double_compressed_cobblestone.json`

圧縮段階が増える Mod では、名前ルールを早めに決めておくと混乱しにくくなります。

## 6. item と tag の使い分け

材料指定には `item` だけでなく `tag` も使えます。

### item を使う例

```json
{
  "item": "minecraft:cobblestone"
}
```

これは「丸石そのものだけを受け付ける」指定です。

### tag を使う例

```json
{
  "tag": "minecraft:planks"
}
```

これは「planks タグに入っているどの木材板でもよい」という指定です。

### このプロジェクトで最初におすすめなのはどちらか

最初は `item` を使う方が分かりやすいです。
受け付ける材料が 1 種類に固定されるため、意図を追いやすくなります。

## 7. レシピが動かないときの確認ポイント

### JSON の置き場所が違う

特に次の間違いが起こりやすいです。

- `assets` の下に置いてしまう
- `recipes` と複数形でフォルダを作ってしまう
- `modid` 部分を間違える

正しい場所は次です。

`src/main/resources/data/cobblestonexxcompressed/recipe/`

### result の ID を間違える

追加したアイテムの登録名と一致していないと、レシピの出力先が見つかりません。

### まだ存在しないアイテムを出力にしている

レシピ JSON は書けていても、Java 側でそのアイテムが未登録だと成立しません。
先に [item-addition.md](./item-addition.md) の手順でアイテムを登録してください。

## 8. このプロジェクトでのおすすめ作業順

圧縮アイテムを 1 つ追加するなら、次の順が分かりやすいです。

1. Java 側でアイテム登録を追加する
2. 言語ファイルを追加する
3. モデル JSON とテクスチャを追加する
4. 圧縮レシピを追加する
5. 圧縮解除レシピを追加する
6. ゲームを起動して作業台で確認する

## 9. 将来的にデータ生成へ移行する場合

このプロジェクトの instructions には `src/generated/resources` を使う前提も書かれています。
将来的にレシピ数が増えたら、Java でレシピを自動生成する data generator を導入する価値があります。

ただし、今の状態ではまず JSON 直書きの方が次の利点があります。

- 何が読み込まれるかをそのまま見やすい
- 初心者でも 1 ファイルずつ理解しやすい
- レシピの失敗原因を切り分けしやすい

最初の数個は JSON で管理し、量が増えてから data generator に切り替えるのが現実的です。

## 10. 最小構成の例

`compressed_cobblestone` 追加時に必要になりやすいファイルは次の通りです。

- `src/main/java/com/yukke9265/cobblestone_xx_compressed/ModItems.java`
- `src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java`
- `src/main/resources/assets/cobblestonexxcompressed/lang/en_us.json`
- `src/main/resources/assets/cobblestonexxcompressed/models/item/compressed_cobblestone.json`
- `src/main/resources/assets/cobblestonexxcompressed/textures/item/compressed_cobblestone.png`
- `src/main/resources/data/cobblestonexxcompressed/recipe/compressed_cobblestone.json`
- `src/main/resources/data/cobblestonexxcompressed/recipe/compressed_cobblestone_unpacking.json`
