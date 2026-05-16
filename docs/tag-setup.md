# タグ追加手順

この文書では、このプロジェクトに**タグを追加する基本的な方法**をまとめます。

タグは、複数のアイテムやブロックを「ひとまとめのグループ」として扱う仕組みです。
レシピ、loot table、将来的な処理分岐で使うことが多いため、早い段階で考え方を押さえておくと後で楽になります。

## 1. タグは何に使うのか

タグを使うと、特定の 1 アイテム名ではなく、グループ全体を対象にできます。

例:

- レシピで「このタグに入っているどれでも材料に使える」とする
- 将来の判定処理で「圧縮系アイテムかどうか」をまとめて扱う
- ブロックやアイテムを種類別に整理する

## 2. このプロジェクトでの基本方針

今のプロジェクトにはタグ定義ファイルはまだありません。
そのため、まずは `src/main/resources/data/` の下に JSON を直接追加するのが分かりやすいです。

### アイテムタグの配置先

`src/main/resources/data/cobblestonexxcompressed/tags/item/`

### ブロックタグの配置先

`src/main/resources/data/cobblestonexxcompressed/tags/block/`

フォルダ名は複数形の `items` や `blocks` ではなく、このプロジェクトではまず Minecraft / NeoForge の読み込み規則に合わせた現在の構成を意識して、実際に使うパスを固定してから増やすのが安全です。

## 3. アイテムタグの追加例

ここでは、圧縮系アイテムをまとめる `compressed_cobblestones` タグを作る想定で説明します。

### ブロックタグの追加先ファイル

`src/main/resources/data/cobblestonexxcompressed/tags/item/compressed_cobblestones.json`

### ブロックタグの追加例

```json
{
  "replace": false,
  "values": [
    "cobblestonexxcompressed:compressed_cobblestone",
    "cobblestonexxcompressed:double_compressed_cobblestone"
  ]
}
```

### 各項目の意味

- `replace`: 既存の同名タグ内容を置き換えるかどうかです。通常は `false` にします
- `values`: このタグへ含める ID の一覧です

## 4. ブロックタグの追加例

ブロックも同様にまとめられます。

### 追加先ファイル

`src/main/resources/data/cobblestonexxcompressed/tags/block/compressed_cobblestone_blocks.json`

### 追加例

```json
{
  "replace": false,
  "values": [
    "cobblestonexxcompressed:compressed_cobblestone_block"
  ]
}
```

## 5. レシピでタグを使う書き方

タグはレシピ JSON の材料指定でよく使います。

### item 指定の例

```json
{
  "item": "minecraft:cobblestone"
}
```

これは丸石そのものだけを受け付けます。

### tag 指定の例

```json
{
  "tag": "cobblestonexxcompressed:compressed_cobblestones"
}
```

これは、そのタグに入っているどのアイテムでも受け付ける指定です。

## 6. 最初にタグ化すると便利な対象

圧縮系 Mod で最初にタグ化しやすいのは次のようなまとまりです。

1. 圧縮系アイテム全体
2. 圧縮系ブロック全体
3. 段階別の圧縮アイテム
4. 将来的に共通処理したい素材群

たとえば、多段圧縮を進めるなら次のように分けられます。

- `compressed_cobblestones`
- `compressed_cobblestone_blocks`
- `tier_1_compressed_items`

## 7. 命名ルールのおすすめ

タグ名は、あとから見たときに「何の集まりか」が分かる名前にしておくと管理しやすいです。

おすすめの考え方:

- 単一素材の圧縮群なら `compressed_cobblestones`
- ブロック群なら `compressed_cobblestone_blocks`
- 段階を明示するなら `tier_1_compressed_items`

## 8. よくあるミス

### `values` の中の ID が未登録

Java 側でまだ登録していないアイテムやブロックを書くと、意図どおり機能しません。

### `item` タグと `block` タグの場所を間違える

アイテム用とブロック用は別です。使う対象に合わせてフォルダを分けます。

### タグ名と使う側の参照名がずれる

レシピやコード側で参照するタグ名と JSON ファイル名が一致していないと追いにくくなります。

## 9. このプロジェクトでの使い始め方

今の段階では、まず次のように始めるのが現実的です。

1. 圧縮アイテムをいくつか追加する
2. それらをまとめる item tag を 1 つ作る
3. 必要になったタイミングで recipe や将来の処理から参照する

最初から細かくタグを切りすぎるより、使い道が見えた単位で作る方が管理しやすいです。
