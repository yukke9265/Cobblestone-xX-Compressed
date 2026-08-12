# Item Texture Style Guide

## Hard constraints

- Size: **16x16** PNG
- Alpha: transparent background; keep soft edge pixels intentional
- Path: `src/main/resources/assets/cobblestonexxcompressed/textures/item/...`
- Name: match registry id / model `layer0` (snake_case)

## Inventory readability

At 16x16 the player must recognize the item in a slot.

- One clear silhouette (blob / rod / gem / chip / dust pile, etc.)
- Strong outer contrast against typical GUI backgrounds
- Avoid tiny interior detail that disappears when scaled
- Family items should share silhouette; variants differ mainly by color
- **Target content size**: opaque bbox about **14×14**, **1px margin** on each side (compare with `cobblestone_dust` if unsure)

## Outer outline

Existing families (e.g. `cobblestone_dust`) use a dark perimeter on the base:

- Base outline color: **`#525252`**
- After recolor, becomes family dark tone (e.g. copper → `#5B3422`, tier copper dust → `#6B3928`)

Apply outline on the **outermost opaque pixels** (pixels adjacent to transparent). Do not rely on recolor alone for the frame.

## Shading for recolor

Because post-process multiplies by luminance ratio:

- Paint the base in the **reference** tint (TierColors or OreColors index 0)
- Use a range of lights and darks, not a single flat fill
- Keep specular / rim pixels either brighter or as low-alpha highlights
- Do not bake strong unrelated hues into the base if those areas should recolor
- Hues recolor cannot produce (e.g. copper green oxide) → add in wrapper **after** recolor, on **inner** pixels only
- Recolor keeps **base** luminance shape — it does not copy the source item's brightness distribution

### Base highlight targets (ore families)

Before recolor, check base brightness with `Get-TextureBrightnessStats` or `dump_texture.ps1`:

| Check | Target | Why |
|-------|--------|-----|
| Light pixels (lum ≥ 170) | **≥ 10%** | crushed outputs inherit base highlight ratio |
| Luminance range (max/min) | **≥ 2.0×** | readable shading after tint |
| Outline share | **≤ 30%** | outer `#525252` stays dark after recolor |

If crushed variants look darker than the source raw item, brighten palette (`-Mode Bright`) first; then add `A`/`B` highlight symbols to the base.

## What to avoid

- Photographic or smooth AI look without pixel quantization
- Soft anti-aliased blobs that turn to muddy mush after tint
- **Opaque white or black background** (will tint or look wrong in-game)
- Different silhouettes per variant when the design is meant to be one family
- Writing variants by hand when the recolor script can keep them in sync
- Placing accent pixels on the outline (they will fight the dark frame)

## Base validation checklist

Before recolor, confirm the base PNG:

1. **Alpha**: corners and surrounding area are transparent (`A = 0`), not `#FFFFFF` or `#000000`
2. **Outline**: outer edge pixels are `#525252`
3. **BBox**: opaque region roughly 14×14, margins ~1px (dump pixels or script — image Read alone may misread margins)
4. **Shading**: mid-tone matches reference palette index 0

After recolor, confirm each variant:

1. No tinted halo outside the silhouette
2. Outline recolored to dark family tone
3. Compare visually with a known-good sibling item (e.g. `tier_copper_cobblestone_dust`)
4. Run `compare_texture_brightness.ps1` when matching a source item's brightness
5. In-game: no purple-black; color matches intended source

## Checking results

1. **`scripts/dump_texture.ps1 -Path <png>`** — bbox, opaque count, `internal_holes` (must be 0)
2. **`scripts/compare_texture_brightness.ps1`** — avg luminance, dark/mid/light ratio vs source item
3. Read the PNG with the image Read tool
4. Compare against a known good sibling
5. In-game: no purple-black; color matches the intended tier/source item
