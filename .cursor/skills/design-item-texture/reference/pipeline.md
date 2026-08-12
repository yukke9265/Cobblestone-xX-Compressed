# Recolor Pipeline (post-processing)

This project's standard post-process is luminance-preserving tinting.

## Engine

`scripts/generate_recolored_textures.ps1 -ConfigPath <config.psd1>`

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

When adding iron, gold, etc.: add palette columns, extend `VariantOutputNames` and `VariantPaletteIndices`, rerun the wrapper.

## Base repair (before recolor)

Run before `generate_recolored_textures.ps1` when the base may have bad background or missing outline.

1. **Transparency**: pixels with `A < 32` or RGB ≥ 240 (white from AI drafts) → fully transparent
2. **Outline**: every opaque pixel with a transparent 4-neighbor → `#525252` (recolors to family dark tone, e.g. `#5B3422` for copper)

Reference: `Repair-CrushedRawOreBase` in `scripts/generate_crushed_raw_ore_textures.ps1`.

**Caution**: repair overwrites outer-edge pixels to `#525252`. Hand-edited outline colors on the perimeter will be reset on each run.

## Multi-stage wrapper example: crushed_raw_ore

`scripts/generate_crushed_raw_ore_textures.ps1` — use as a template for ore families that need extra steps:

```
1. Repair-CrushedRawOreBase   (transparent bg + #525252 outline)
2. generate_recolored_textures.ps1
3. Variant-specific accents     (e.g. copper oxide green on inner pixels only)
```

Copper oxide accents: hand-picked coordinates, applied only where the pixel is opaque **and not** on the outer outline. Re-check coordinates after resizing the base.

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
