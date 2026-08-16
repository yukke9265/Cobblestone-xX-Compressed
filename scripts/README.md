# Texture Generation Scripts

このフォルダには、色違い PNG をまとめて生成する PowerShell スクリプトを置いています。

## Files

- `generate_recolored_textures.ps1`
  - 共通の色替えスクリプトです。
  - `.psd1` の設定ファイルを読んで、基準 PNG とパレット PNG から複数の出力 PNG を作ります。

- `configs/cobblestone_bread_texture_config.psd1`
  - 丸石パン用の設定ファイルです。
  - tier 一覧はここにまとめています。

- `configs/compressed_cobblestone_texture_config.psd1`
  - 圧縮丸石ブロック用の設定ファイルです。
  - tier ごとの出力 PNG 名はここで管理します。

- `configs/cobblestone_gem_texture_config.psd1`
  - 丸石ジェム用の設定ファイルです。
  - item 用 PNG の tier 出力名をここで管理します。

- `configs/cobblestone_dust_texture_config.psd1`
  - 丸石ダスト用の設定ファイルです。
  - item 用 PNG の tier 出力名をここで管理します。

- `configs/compressed_stone_texture_config.psd1`
  - 圧縮石ブロック用の設定ファイルです。
  - block 用 PNG の tier 出力名をここで管理します。

- `configs/cobblestone_machine_casing_texture_config.psd1`
  - Cobblestone Machine Casing 用の設定ファイルです。
  - block 用 PNG の tier 出力名をここで管理します。

- `configs/TierColors.png`
  - 共通の tier 色パレットです。
  - 上端 1 行を左から読み、先頭を基準色、右側を variant 色として使います。

- `configs/OreColors.png`
  - 原石タイプ別の色パレットです（tier ではなく copper / iron / gold など）。
  - index 0 は基準灰、index 1 以降が各原石の代表色です。

- `texture_utils.ps1`
  - テクスチャ生成の共通ヘルパーです。
  - ベース修復、pixel map 生成、アクセント配置、ピクセルダンプなどを提供します。

- `dump_texture.ps1`
  - PNG の 16×16 ピクセルダンプと bbox / internal_holes を表示します。
  - 生成後の確認用に使います。

- `sample_vanilla_item_color.ps1`
  - バニラアイテム PNG から多い色をサンプルします。
  - `OreColors.png` に色を追加するときに使います。

- `generate_crushed_raw_ore_textures.ps1`
  - 粉砕原石（crushed_raw_ore）ファミリー用の多段ラッパーです。
  - 修復 → 再着色 → 銅の緑化アクセントまで一括実行します。

- `configs/crushed_raw_ore_texture_config.psd1`
  - 粉砕原石の再着色設定です。

- `configs/crushed_raw_ore_copper_accents.psd1`
  - 銅バリアントの緑化アクセント座標です。緑の量を変えるときはここを編集します。

- `generate_cobblestone_bread_textures.ps1`
  - 丸石パン用の薄いラッパーです。
  - 共通スクリプトへ上の設定ファイルを渡すだけなので、普段はこちらを実行すれば十分です。

- `generate_compressed_cobblestone_textures.ps1`
  - 圧縮丸石ブロック用の薄いラッパーです。
  - 共通スクリプトへ圧縮丸石用設定を渡します。

- `generate_cobblestone_gem_textures.ps1`
  - 丸石ジェム用の薄いラッパーです。
  - 共通スクリプトへ丸石ジェム用設定を渡します。

- `generate_cobblestone_acceleration_chip_textures.ps1`
  - Cobblestone Acceleration Chip 用の薄いラッパーです。
  - 共通スクリプトへ acceleration chip 用設定を渡します。

- `generate_cobblestone_energized_cube_textures.ps1`
  - Cobblestone Energized Cube 用の薄いラッパーです。
  - 共通スクリプトへ energized cube 用設定を渡します。

- `generate_cobblestone_parallel_chip_textures.ps1`
  - Cobblestone Parallel Chip 用の薄いラッパーです。
  - 共通スクリプトへ parallel module 用設定を渡します。

- `generate_cobblestone_dust_textures.ps1`
  - 丸石ダスト用の薄いラッパーです。
  - 共通スクリプトへ丸石ダスト用設定を渡します。

- `generate_compressed_stone_textures.ps1`
  - 圧縮石ブロック用の薄いラッパーです。
  - 共通スクリプトへ圧縮石用設定を渡します。

- `generate_cobblestone_machine_casing_textures.ps1`
  - Cobblestone Machine Casing 用の薄いラッパーです。
  - 共通スクリプトへ machine casing 用設定を渡します。

## How It Works

1. `BaseTextureFileName` で指定した PNG を元画像として読みます。
2. `PaletteTexturePath` または `PaletteTextureFileName` で指定したパレット画像の最上段を左から読みます。
3. `ReferencePaletteIndex` の色を通常版の基準色として使います。
4. その右側の色を順番に `VariantOutputNames` へ対応させて出力します。
5. 元画像の明るさ比を保ったまま色替えするので、陰影を残した recolor になります。

## Cobblestone Bread Usage

ワークスペースのルートで次を実行します。

```powershell
.\scripts\generate_cobblestone_bread_textures.ps1
```

これで `src/main/resources/assets/cobblestonexxcompressed/textures/item/cobblestone_bread` にある
既存の tier PNG を上書きします。

圧縮丸石ブロックを生成するときは次を実行します。

```powershell
.\scripts\generate_compressed_cobblestone_textures.ps1
```

これで `src/main/resources/assets/cobblestonexxcompressed/textures/block/compressed_cobblestone` に
tier 用 PNG を出力します。

丸石ジェムを生成するときは次を実行します。

```powershell
.\scripts\generate_cobblestone_gem_textures.ps1
```

これで `src/main/resources/assets/cobblestonexxcompressed/textures/item/cobblestone_gem` に
tier 用 PNG を出力します。

Cobblestone Acceleration Chip を生成するときは次を実行します。

```powershell
.\scripts\generate_cobblestone_acceleration_chip_textures.ps1
```

これで `src/main/resources/assets/cobblestonexxcompressed/textures/item/cobblestone_acceleration_chip` に
tier 用 PNG を出力します。

Cobblestone Energized Cube を生成するときは次を実行します。

```powershell
.\scripts\generate_cobblestone_energized_cube_textures.ps1
```

これで `src/main/resources/assets/cobblestonexxcompressed/textures/item/cobblestone_energized_cube` に
tier 用 PNG を出力します。

Cobblestone Parallel Chip を生成するときは次を実行します。

```powershell
.\scripts\generate_cobblestone_parallel_chip_textures.ps1
```

これで `src/main/resources/assets/cobblestonexxcompressed/textures/item/cobblestone_parallel_module` に
tier 用 PNG を出力します。

丸石ダストを生成するときは次を実行します。

```powershell
.\scripts\generate_cobblestone_dust_textures.ps1
```

これで `src/main/resources/assets/cobblestonexxcompressed/textures/item/cobblestone_dust` に
tier 用 PNG を出力します。

圧縮石ブロックを生成するときは次を実行します。

```powershell
.\scripts\generate_compressed_stone_textures.ps1
```

これで `src/main/resources/assets/cobblestonexxcompressed/textures/block/compressed_stone` に
tier 用 PNG を出力します。

Cobblestone Machine Casing を生成するときは次を実行します。

```powershell
.\scripts\generate_cobblestone_machine_casing_textures.ps1
```

これで `src/main/resources/assets/cobblestonexxcompressed/textures/block/cobblestone_machine_casing` に
tier 用 PNG を出力します。

## Generic Usage

別のテクスチャでも同じ仕組みを使いたい場合は、まず `.psd1` 設定を 1 つ追加します。

例:

```powershell
.\scripts\generate_recolored_textures.ps1 -ConfigPath .\scripts\configs\your_texture_config.psd1
```

設定ファイルには次を入れます。

```powershell
@{
    TextureDirectory = 'src/main/resources/assets/yourmod/textures/item/example'
    BaseTextureFileName = 'example.png'
    PaletteTexturePath = 'TierColors.png'
    ReferencePaletteIndex = 0
    VariantOutputNames = @(
        'tier_one_example'
        'tier_two_example'
    )
}
```

## When You Add A New Tier

丸石パンで tier を増やすときは、PNG 自動生成の観点では次の 2 か所だけです。

1. `TierColors.png` の最上段に新しい色を右側へ追加する
2. `configs/cobblestone_bread_texture_config.psd1` の `VariantOutputNames` に対応する出力名を追加する

その後で `generate_cobblestone_bread_textures.ps1` を実行すれば、新しい PNG が生成されます.

## Ore family (crushed_raw_ore)

原石タイプ別（tier 別ではない）のテクスチャは `OreColors.png` を使います。

```powershell
.\scripts\generate_crushed_raw_ore_textures.ps1
```

生成後の確認:

```powershell
.\scripts\dump_texture.ps1 -Path src/main/resources/assets/cobblestonexxcompressed/textures/item/crushed_raw_ore/crushed_raw_copper.png
```

`internal_holes=0` であること、bbox がおおよそ 14×14 前後であることを確認します。

バニラ原石から色を取るとき:

```powershell
# 一覧（輝度付き）
.\scripts\sample_vanilla_item_color.ps1 -Path D:\1.21.1\assets\minecraft\textures\item\raw_copper.png

# パレット用（ore ファミリーは Bright 推奨）
.\scripts\sample_vanilla_item_color.ps1 -Path D:\1.21.1\assets\minecraft\textures\item\raw_copper.png -Mode Bright
```

生成後の明るさ比較:

```powershell
.\scripts\compare_texture_brightness.ps1 `
  -ReferencePath D:\1.21.1\assets\minecraft\textures\item\raw_gold.png `
  -OutputPath src/main/resources/assets/cobblestonexxcompressed/textures/item/crushed_raw_ore/crushed_raw_gold.png
```

合格目安: `avg_ratio >= 0.7`, 明部 (lum>=170) >= 5%. WARN が出たらパレットを Bright で再サンプル、またはベースのハイライト (`A`/`B` 記号) を増やす。

新しい原石を追加するとき:

1. `generate_crushed_raw_ore_textures.ps1` の `$oreSourcePaths` にパスを追加
2. `configs/crushed_raw_ore_texture_config.psd1` に出力名と palette index を追加
3. 必要なら `configs/crushed_raw_ore_<ore>_accents.psd1` を追加
4. `generate_crushed_raw_ore_textures.ps1` を再実行（パレット自動取得 + 明るさレポート付き）

## Validation helpers

```powershell
# ピクセルダンプ + bbox + 内部の透明穴チェック
.\scripts\dump_texture.ps1 -Path <png>

# サマリーだけ
.\scripts\dump_texture.ps1 -Path <png> -SummaryOnly

# パレット色サンプル（-Mode Bright / Mid / Highlight / List）
.\scripts\sample_vanilla_item_color.ps1 -Path <raw_ore.png> -Mode Bright

# 元テクスチャとの明るさ比較
.\scripts\compare_texture_brightness.ps1 -ReferencePath <raw.png> -OutputPath <crushed.png>
```
