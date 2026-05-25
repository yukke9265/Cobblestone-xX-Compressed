# Mod アイコンとメタ情報の設定

この文書では、このプロジェクトで **Mod 一覧に出る表示名、作者、説明文、ロゴ画像、バージョン** をどこで設定するかをまとめます。

NeoForge の Mod 情報は 1 か所だけで完結しているように見えて、実際には次の 3 つに分かれています。

- `gradle.properties`
- `src/main/templates/META-INF/neoforge.mods.toml`
- `src/main/resources/`

どこに何を書くかを先に分けて覚えておくと迷いにくいです。

## 1. まず結論

### Mod 名、バージョン、ライセンス

`gradle.properties`

### 作者名、説明文、ロゴファイル名、URL、クレジット

`src/main/templates/META-INF/neoforge.mods.toml`

### ロゴ画像ファイル本体

`src/main/resources/`

## 2. `gradle.properties` で管理するもの

このプロジェクトでは、`build.gradle` からテンプレート展開をしているため、基本情報の一部は `gradle.properties` で持っています。

主に見る項目は次の 4 つです。

- `mod_id`
- `mod_name`
- `mod_license`
- `mod_version`

例:

```properties
mod_id=cobblestonexxcompressed
mod_name=Cobblestone xXCompressed
mod_license=All Rights Reserved
mod_version=1.0.0
```

### それぞれの意味

#### `mod_id`

Mod の内部 ID です。

- すべて小文字
- 通常は一度決めたら途中で変えない
- `@Mod(...)` の値や assets/data の名前空間と一致している必要があります

#### `mod_name`

Mod 一覧に表示される名前です。

#### `mod_license`

配布時のライセンス表記です。

#### `mod_version`

Mod 一覧や jar 名に使われるバージョンです。

## 3. `neoforge.mods.toml` で管理するもの

実際に Mod 情報としてゲームへ渡されるのは `src/main/templates/META-INF/neoforge.mods.toml` です。

このファイルはテンプレートなので、`${mod_name}` や `${mod_version}` のような値が `gradle.properties` から差し込まれます。

このプロジェクトでよく触る項目は次の通りです。

- `displayName`
- `authors`
- `description`
- `logoFile`
- `credits`
- `displayURL`
- `updateJSONURL`

例:

```toml
[[mods]]
modId="${mod_id}"
version="${mod_version}"
displayName="${mod_name}"
logoFile="mod_icon.png"
authors="yukke9265"
credits="Thanks to everyone who tested this mod."
displayURL="https://example.com"
description='''
Cobblestone を中心にした圧縮系・機械系 Mod です。
'''
```

### `displayName`

表示名です。

このプロジェクトでは `${mod_name}` を使っているので、通常は `gradle.properties` 側の `mod_name` を直せば十分です。

### `authors`

作者名です。

複数人なら、分かりやすい文字列でそのまま並べて問題ありません。

例:

```toml
authors="yukke9265, contributorA"
```

### `description`

説明文です。

- 複数行で書ける
- Mod 一覧で表示される
- 何をする Mod かを短くまとめるのが分かりやすい

### `logoFile`

Mod 一覧に表示する画像ファイル名です。

ここに書くのは **ファイル名** です。
画像そのものは `src/main/resources/` に置きます。

## 4. ロゴ画像の置き場所

ロゴ画像は `src/main/resources/` に置きます。

例:

- `src/main/resources/mod_icon.png`

そして `neoforge.mods.toml` でこう指定します。

```toml
logoFile="mod_icon.png"
```

この設定で、jar のルートに `mod_icon.png` が入り、Mod 一覧から読まれます。

### 注意点

- `assets/<modid>/textures/` ではありません
- `logoFile` には通常フルパスではなくファイル名を書きます
- jar に入っていないと表示されません

## 5. おすすめの画像サイズ

厳密な固定値ではありませんが、まずは次のどれかにすると扱いやすいです。

- `64x64`
- `128x128`
- `256x256`

基本は正方形 PNG が無難です。

迷ったら `128x128` の PNG で始めるのが分かりやすいです。

## 6. このプロジェクトで実際に反映される流れ

このプロジェクトの `build.gradle` では、`src/main/templates` 以下の `neoforge.mods.toml` を展開して、生成済みリソースとしてビルドへ含めています。

つまり流れは次の通りです。

1. `gradle.properties` の値を読む
2. `src/main/templates/META-INF/neoforge.mods.toml` へ差し込む
3. ビルド時に実際の `META-INF/neoforge.mods.toml` が生成される
4. jar の中に入る

そのため、**最終成果物へ反映される設定元はテンプレート側** だと考えると整理しやすいです。

## 7. よくあるミス

### `logoFile` だけ書いて画像を置いていない

ファイル名指定だけでは表示されません。

### 画像を `assets/<modid>/textures` に置く

Mod ロゴは通常のゲームテクスチャとは扱いが違います。
このプロジェクトでは `src/main/resources/` 直下へ置くのが分かりやすいです。

### `mod_name` を直したのに表示が変わらない

ビルド済み jar を見ている場合は、再ビルドが必要です。

### `mod_id` を途中で変更する

assets や data のパス、`@Mod` の値、既存セーブとの互換に影響しやすいので、基本は避けた方が安全です。

## 8. 変更後の確認

変更したら、次のどちらかで確認できます。

- `gradlew.bat runClient`
- `gradlew.bat build`

ゲーム内の Mod 一覧で次を確認します。

- 表示名
- 作者名
- 説明文
- ロゴ画像
- バージョン

## 9. このプロジェクトでまず触る場所まとめ

### 表示名を変えたい

`gradle.properties` の `mod_name`

### バージョンを変えたい

`gradle.properties` の `mod_version`

### 作者名や説明を変えたい

`src/main/templates/META-INF/neoforge.mods.toml`

### アイコンを付けたい

1. `src/main/resources/mod_icon.png` を置く
2. `src/main/templates/META-INF/neoforge.mods.toml` に `logoFile="mod_icon.png"` を書く

この 2 手順で足せます。
