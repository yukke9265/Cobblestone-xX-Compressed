# Recolor Pipeline (post-processing)

This project's standard post-process is luminance-preserving tinting.

## Engine

`scripts/generate_recolored_textures.ps1 -ConfigPath <config.psd1>`

Shared helpers live in `scripts/texture_utils.ps1` (dot-source from family wrappers):

| Function | Use |
|----------|-----|
| `Repair-ItemTextureBase` | Transparent bg + `#525252` outline before recolor |
| `New-PixelMapBitmap` | Build 16×16 base from string map + symbol table |
| `Test-PixelMapSymbols` | Fail fast if map uses undefined symbols |
| `Apply-TextureAccentPixels` | Post-recolor accents on inner pixels only |
| `Write-TextureDump` | Pixel dump + bbox + internal hole count |
| `New-OreColorsPalette` | Create/refresh `OreColors.png` |
| `Get-VanillaItemDominantColors` | Sample colors from a reference item PNG |

CLI wrappers:

```powershell
.\scripts\dump_texture.ps1 -Path src/main/resources/.../crushed_raw_copper.png
.\scripts\sample_vanilla_item_color.ps1 -Path D:\1.21.1\assets\minecraft\textures\item\raw_copper.png
```

Behavior (important for design):

- Skips fully transparent pixels
- Keeps very faint alpha (`A < 16`) as-is (highlights)
- For other pixels: `newRGB = targetRGB * (sourceLuminance / referenceLuminance)`
- So the **base must be painted in the reference color**; brightness carries shading
- **Opaque white/near-white pixels are NOT skipped** — they will be tinted. Always repair the base first.

## Config shape

Example pattern (tier family — see `scripts/configs/cobblestone_gem_texture_config.psd1`):

```powershell
@{
    TextureDirectory = '../../src/main/resources/assets/cobblestonexxcompressed/textures/item/cobblestone_gem'
    BaseTextureFileName = 'cobblestone_gem.png'
    PaletteTexturePath = 'TierColors.png'
    ReferencePaletteIndex = 0
    VariantOutputNames = @(
        'tier_copper_cobblestone_gem'
        # ...
    )
}
```

Optional:

- `VariantPaletteIndices` — when output order is not `reference+1, reference+2, ...` (see `molten_bucket_overlay_texture_config.psd1`)
- `PaletteTextureFileName` — palette next to the textures instead of `PaletteTexturePath`

Paths in the config are resolved relative to the **config file directory** unless absolute.

## Wrapper script

**Thin wrapper** (tier families — recolor only):

```powershell
$configPath = Join-Path $PSScriptRoot 'configs\cobblestone_gem_texture_config.psd1'
$sharedScriptPath = Join-Path $PSScriptRoot 'generate_recolored_textures.ps1'

& $sharedScriptPath -ConfigPath $configPath
```

Run from repo root or `scripts/`:

```powershell
powershell -File scripts/generate_cobblestone_gem_textures.ps1
```

## Palette: TierColors.png

- Location: `scripts/configs/TierColors.png`
- Top row pixels are colors; column index is the palette index
- Common layout (verify before changing): index 0 cobblestone gray, then copper, iron, gold, amethyst, ...

Do not reshuffle existing indices without regenerating every dependent family.

## Palette: OreColors.png

Use when variants are **ore type** (copper, iron, gold, …), not cobblestone tier.

- Location: `scripts/configs/OreColors.png`
- Index 0: reference gray (same role as TierColors index 0 — base is painted in this)
- Index 1+: one color per ore, sampled from vanilla item textures (e.g. `raw_copper` → `#C4704A`)
- Extend the top row when adding ores; do not reshuffle existing indices

Example config (`scripts/configs/crushed_raw_ore_texture_config.psd1`):

```powershell
@{
    TextureDirectory = '../../src/main/resources/assets/cobblestonexxcompressed/textures/item/crushed_raw_ore'
    BaseTextureFileName = 'crushed_raw_ore.png'
    PaletteTexturePath = 'OreColors.png'
    ReferencePaletteIndex = 0
    VariantOutputNames = @(
        'crushed_raw_copper'
    )
    VariantPaletteIndices = @(
        1
    )
}
```

When adding iron, gold, etc.:

1. Sample mid-tone with `sample_vanilla_item_color.ps1` from `raw_<ore>.png`
2. Add a column to `OreColors.png` (do not reshuffle existing indices)
3. Extend `VariantOutputNames` and `VariantPaletteIndices` in the family config
4. Rerun the family wrapper

## Adding a new ore to crushed_raw_ore

1. `.\scripts\sample_vanilla_item_color.ps1 -Path <vanilla raw_ore png>`
2. Add the mid-tone hex to `OreColors.png` via `New-OreColorsPalette` in the wrapper (append only)
3. Update `crushed_raw_ore_texture_config.psd1`
4. If the ore needs accents recolor cannot do (e.g. copper green), add `configs/crushed_raw_ore_<ore>_accents.psd1` and call `Apply-TextureAccentPixels` in the wrapper
5. `.\scripts\generate_crushed_raw_ore_textures.ps1`
6. `.\scripts\dump_texture.ps1 -Path ...\crushed_raw_<ore>.png` — confirm `internal_holes=0`

## Base repair (before recolor)

Run before `generate_recolored_textures.ps1` when the base may have bad background or missing outline.

1. **Transparency**: pixels with `A < 32` or RGB ≥ 240 (white from AI drafts) → fully transparent
2. **Outline**: every opaque pixel with a transparent 4-neighbor → `#525252` (recolors to family dark tone, e.g. `#5B3422` for copper)

Reference: `Repair-ItemTextureBase` in `scripts/texture_utils.ps1`.

**Caution**: repair overwrites outer-edge pixels to `#525252`. Hand-edited outline colors on the perimeter will be reset on each run.

## Pixel map bases (script-authored art)

For new 16×16 bases, prefer `New-PixelMapBitmap` in `texture_utils.ps1`:

```powershell
$pixelMap = @('................', '......EE88......', ...)
$colorBySymbol = @{ '.' = $null; '5' = '#525252'; '6' = '#616161'; ... }
$bitmap = New-PixelMapBitmap -PixelMap $pixelMap -ColorBySymbol $colorBySymbol
```

Rules:

- **Every symbol in the map must exist in `$colorBySymbol`** — undefined symbols become transparent and cause internal holes (a common bug).
- `Test-PixelMapSymbols` runs automatically inside `New-PixelMapBitmap`.
- After changing the map, rerun repair + recolor; **re-check accent coordinates** if the family uses post-recolor accents.

## Multi-stage wrapper example: crushed_raw_ore

`scripts/generate_crushed_raw_ore_textures.ps1` — use as a template for ore families that need extra steps:

```
1. New-OreColorsPalette (if needed)
2. New-PixelMapBitmap base (or hand-edited PNG)
3. Repair-ItemTextureBase
4. generate_recolored_textures.ps1
5. Apply-TextureAccentPixels from configs/*_accents.psd1
6. dump_texture.ps1 — internal_holes must be 0
```

Copper oxide accents: `scripts/configs/crushed_raw_ore_copper_accents.psd1` — hand-picked coordinates, applied only where the pixel is opaque **and not** on the outer outline. Re-check coordinates after resizing the base.

## Adding a new item family

1. Create `textures/item/<family>/` and the base PNG (16x16)
2. Repair base (transparency + outline) — see [style-guide.md](style-guide.md)
3. Choose palette: `TierColors.png` or `OreColors.png`
4. Add `scripts/configs/<family>_texture_config.psd1`
5. Add `scripts/generate_<family>_textures.ps1` (repair + recolor + optional post-steps)
6. Run the wrapper and confirm each `generated: ...` line
7. **Pixel-dump** margins/alpha; Read tool for visual check

## Transferring color from a specific item

Preferred order:

1. If the item is already a tier variant → use the matching `TierColors` index
2. If the item is a vanilla ore → use or add a slot in `OreColors.png`
3. If not in either palette → sample the item's main opaque mid-tone into a new palette row
4. Prefer extending shared palettes only when the color will be reused across families

## Fix loop

If variants look muddy, flat, wrong hue, or have colored background:

1. **Check base alpha** — white fill tinted by recolor is the most common bug
2. Fix **base** shading (more contrast in luminance)
3. Confirm base mid-tone matches the **reference** palette color
4. Confirm outline (`#525252`) on outer edge
5. Adjust content size (target 14×14 bbox, 1px margin) if item looks too small
6. Regenerate all variants
7. Re-apply variant-specific accents if coordinates shifted

Avoid hand-editing every variant PNG unless one-off art is required.
