# Base Texture Generation

Use generation only when there is no close existing base to edit. Prefer copy-edit of a sibling family base when possible.

## Preferred order

1. **Copy + edit** an existing family base (best consistency)
2. **Hand-pixel** a new 16x16 base in the reference tint
3. **GenerateImage** for a large concept, then **post-process down to 16x16**, then repair + recolor

## GenerateImage path (when used)

GenerateImage outputs are usually not Minecraft-ready. Treat them as drafts.

1. Write a short brief: silhouette, reference item color identity, "Minecraft item icon, 16-bit pixel art, flat, transparent background"
2. Call GenerateImage; inspect the result
3. Post-process before shipping:
   - Crop to the subject
   - Resize to **16x16** with nearest-neighbor (no smooth blur)
   - **Remove white background → true alpha** (see below — critical)
   - Quantize / repaint into the **reference palette color** with clear light/dark steps
   - Apply **outline repair** (`#525252` on outer edge) — see [pipeline.md](pipeline.md#base-repair-before-recolor)
4. Save as the family `BaseTextureFileName`
5. Run the family wrapper (repair + recolor + any post-steps)

If the downscale looks soft or noisy, redraw the silhouette by hand on a blank 16x16 using the draft only as a guide.

### White background trap

GenerateImage drafts often save as **opaque white** (`#FFFFFF`), not transparent.

- `generate_recolored_textures.ps1` only skips `A = 0` pixels
- Opaque white **will be tinted** → peach/orange halo around the item
- Always convert white/near-white (RGB ≥ 240) to transparent **before** recolor

## Script-assisted resize (optional)

When automating resize locally, use nearest-neighbor and PNG alpha. Do not introduce a second competing recolor engine—final tinting stays in `generate_recolored_textures.ps1`.

After resize from a draft:

1. Quantize opaque pixels to project gray steps (`#525252`, `#616161`, `#6E6D6D`, `#888788`, `#A6A6A6`, `#B3B1AF`, `#B5B5B5`, `#D7D7D7`)
2. Run base repair (transparent + outline)

## Adjusting content size on 16×16

If the item looks too small in a slot (e.g. 12×12 content with 2px margin):

1. Measure bbox of opaque pixels (target: **14×14 with 1px margin**)
2. Scale content with nearest-neighbor from current bbox to target bbox
3. Re-run base repair (outline will be reset on outer edge)
4. Regenerate variants; **re-check variant-specific accent coordinates**

## One-off vs family

- One unique item: a single final PNG may be enough (still 16x16, style-guide rules)
- Tiered / material family: always go through base + palette + recolor so colors stay aligned
- Ore family: use `OreColors.png`; see [pipeline.md](pipeline.md#palette-orecolorspng)
