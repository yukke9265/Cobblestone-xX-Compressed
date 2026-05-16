# リソース配置ルール

この文書では、このプロジェクトで**どの種類のファイルをどこへ置くか**を整理します。

Minecraft Mod 開発では、ファイルの中身が正しくても、置き場所が違うだけで読み込まれません。
そのため、機能を増やす前にフォルダの役割を把握しておくと作業がかなり楽になります。

## 1. 大きく分けると `java` と `resources`

### `src/main/java`

Java コードを置く場所です。

例:

- アイテム登録
- ブロック登録
- イベント処理
- 設定クラス

### `src/main/resources`

ゲームが読み込む JSON、lang、テクスチャなどを置く場所です。

例:

- item model
- block model
- blockstates
- loot table
- recipe
- lang
- `META-INF/neoforge.mods.toml`

## 2. assets と data の違い

resources の中は、主に `assets` と `data` に分かれます。

### `assets/<modid>`

見た目や表示に関係するものを置きます。

例:

- `lang/`
- `textures/`
- `models/`
- `blockstates/`

### `data/<modid>`

ゲームルールや内容に関係するものを置きます。

例:

- `recipe/`
- `loot_table/`
- `tags/`
- 将来的な worldgen 関連データ

迷ったときは、次の感覚で切り分けると分かりやすいです。

- 見た目なら `assets`
- 動作ルールなら `data`

## 3. このプロジェクトでよく使う配置先

### アイテム名・ブロック名

`src/main/resources/assets/cobblestonexxcompressed/lang/en_us.json`

### アイテムモデル

`src/main/resources/assets/cobblestonexxcompressed/models/item/`

### ブロックモデル

`src/main/resources/assets/cobblestonexxcompressed/models/block/`

### ブロックステート

`src/main/resources/assets/cobblestonexxcompressed/blockstates/`

### アイテムテクスチャ

`src/main/resources/assets/cobblestonexxcompressed/textures/item/`

### ブロックテクスチャ

`src/main/resources/assets/cobblestonexxcompressed/textures/block/`

### レシピ

`src/main/resources/data/cobblestonexxcompressed/recipe/`

### ブロックドロップ用 loot table

`src/main/resources/data/cobblestonexxcompressed/loot_table/blocks/`

## 4. `src/generated/resources` の役割

このプロジェクトの `build.gradle` では、次の設定があります。

- `src/generated/resources` を main resources に含める
- `runs.data` で data generator の出力先を `src/generated/resources` にしている

つまり、将来的に data generator を使い始めた場合は、生成ファイルがここへ出力されます。

### 今の段階での考え方

- 手書きするファイルはまず `src/main/resources`
- 自動生成するファイルは `src/generated/resources`

この分け方にしておくと、あとから見たときに「手で書いたのか、自動生成なのか」を判断しやすいです。

## 5. 命名ルールの基本

登録名やファイル名は、基本的に小文字スネークケースでそろえます。

例:

- `compressed_cobblestone`
- `compressed_cobblestone_block`
- `double_compressed_cobblestone`

Java 定数名だけは大文字になります。

例:

- `COMPRESSED_COBBLESTONE`
- `COMPRESSED_COBBLESTONE_BLOCK`

## 6. ファイル名と登録名をそろえる理由

Minecraft のリソース読み込みは、登録名とファイル名の対応が重要です。

たとえばアイテム登録名が `compressed_cobblestone` なら、関連ファイルも次のようにそろえると追いやすくなります。

- `compressed_cobblestone.json`
- `compressed_cobblestone.png`
- `item.cobblestonexxcompressed.compressed_cobblestone`

ここがずれると、表示名未翻訳、モデル欠損、テクスチャ欠損が起こりやすくなります。

## 7. よくある配置ミス

### `recipe` を `assets` に置く

レシピは `data` 側です。

### `loot_table` を置いていない

ブロックを壊したときの挙動が期待どおりになりません。

### `models/item` と `models/block` を混同する

ブロックはワールド用モデルとアイテム用モデルの 2 つが必要になることがあります。

### `src/generated/resources` に手書きし続ける

これは datagen の出力で上書きされる可能性があるため、通常はおすすめしません。
