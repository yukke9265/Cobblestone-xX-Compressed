# Recolor Pipeline (post-processing)

This project's standard post-process is luminance-preserving tinting.

## Engine

`scripts/generate_recolored_textures.ps1 -ConfigPath <config.psd1>`

Behavior (important for design):

- Skips fully transparent pixels
- Keeps very faint alpha (`A < 16`) as-is (highlights)
- For other pixels: `newRGB = targetRGB * (sourceLuminance / referenceLuminance)`
- So the **base must be painted in the reference color**; brightness carries shading

## Config shape

Example pattern (see `scripts/configs/cobblestone_gem_texture_config.psd1`):

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

Keep wrappers thin, matching existing scripts:

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

## Adding a new item family

1. Create `textures/item/<family>/` and the base PNG (16x16)
2. Add `scripts/configs/<family>_texture_config.psd1`
3. Add `scripts/generate_<family>_textures.ps1`
4. Run the wrapper and confirm each `generated: ...` line
5. Open a few output PNGs with the Read tool and check silhouette + color identity

## Transferring color from a specific item

Preferred order:

1. If the item is already a tier variant → use the matching `TierColors` index
2. If not → sample the item's main opaque mid-tone into a temporary 1-row palette PNG, set `ReferencePaletteIndex` / `VariantPaletteIndices` accordingly
3. Prefer extending shared `TierColors.png` only when the color will be reused across families

## Fix loop

If variants look muddy, flat, or wrong hue:

1. Fix **base** shading (more contrast in luminance)
2. Confirm base mid-tone matches the **reference** palette color
3. Regenerate all variants
4. Avoid hand-editing every tier PNG unless one-off art is required
