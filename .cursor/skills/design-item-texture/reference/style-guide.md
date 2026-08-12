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
- Family items should share silhouette; tiers differ mainly by color

## Shading for recolor

Because post-process multiplies by luminance ratio:

- Paint the base in the **reference** tint (often TierColors index 0)
- Use a range of lights and darks, not a single flat fill
- Keep specular / rim pixels either brighter or as low-alpha highlights
- Do not bake strong unrelated hues into the base if those areas should recolor

## What to avoid

- Photographic or smooth AI look without pixel quantization
- Soft anti-aliased blobs that turn to muddy mush after tint
- Different silhouettes per tier when the design is meant to be one family
- Writing variants by hand when the recolor script can keep them in sync

## Checking results

1. Read the PNG with the image Read tool
2. Compare against a known good sibling (e.g. existing gem/dust)
3. In-game: no purple-black; color matches the intended tier/source item
