# Base Texture Generation

Use generation only when there is no close existing base to edit. Prefer copy-edit of a sibling family base when possible.

## Preferred order

1. **Copy + edit** an existing family base (best consistency)
2. **Hand-pixel** a new 16x16 base in the reference tint
3. **GenerateImage** for a large concept, then **post-process down to 16x16**, then recolor

## GenerateImage path (when used)

GenerateImage outputs are usually not Minecraft-ready. Treat them as drafts.

1. Write a short brief: silhouette, reference item color identity, "Minecraft item icon, 16-bit pixel art, flat, transparent background"
2. Call GenerateImage; inspect the result
3. Post-process before shipping:
   - Crop to the subject
   - Resize to **16x16** with nearest-neighbor (no smooth blur)
   - Drop or clean background to true alpha
   - Quantize / repaint into the **reference palette color** with clear light/dark steps
4. Save as the family `BaseTextureFileName`
5. Run `generate_recolored_textures.ps1` for variants

If the downscale looks soft or noisy, redraw the silhouette by hand on a blank 16x16 using the draft only as a guide.

## Script-assisted resize (optional)

When automating resize locally, use nearest-neighbor and PNG alpha. Do not introduce a second competing recolor engine—final tinting stays in `generate_recolored_textures.ps1`.

## One-off vs family

- One unique item: a single final PNG may be enough (still 16x16, style-guide rules)
- Tiered / material family: always go through base + palette + recolor so colors stay aligned with `TierColors.png`
