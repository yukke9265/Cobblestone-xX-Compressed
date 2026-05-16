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
