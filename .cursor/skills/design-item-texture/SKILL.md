---
name: design-item-texture
description: "Use when designing, recoloring, or generating Minecraft item PNG textures for this NeoForge 1.21.1 mod—especially transferring a source item's coloring onto a different base design, including post-processing via scripts/generate_recolored_textures.ps1."
---

# Item Texture Design Workflow

Use this skill for **item textures only** (not block/GUI). Start with [reference/overview.md](reference/overview.md). Use [reference/pipeline.md](reference/pipeline.md), [reference/style-guide.md](reference/style-guide.md), and [reference/generation.md](reference/generation.md) as needed.

Item registration, models, lang, and recipes stay in `add-item-workflow`. This skill covers appearance only.

## Main use case

**Color from one item (or palette) → different silhouette/design.**

1. Pick the **color source** (`TierColors.png`, `OreColors.png`, or a sampled reference item).
2. Pick or create the **base design** (shape/shading only; painted in the reference palette color).
3. **Repair the base** if needed (transparent background + dark outline — see [style-guide.md](reference/style-guide.md)).
4. Run **post-processing** with `scripts/generate_recolored_textures.ps1` via a config + wrapper.
5. Apply **family-specific post-steps** in the wrapper when needed (e.g. copper oxide spots).
6. Confirm output path, file name, and that the item model `layer0` matches.

## Tier vs ore (material axis)

| Axis | Palette | Example family | Variant naming |
|------|---------|----------------|----------------|
| Tier | `TierColors.png` | `cobblestone_dust` | `tier_copper_cobblestone_dust` |
| Ore / vanilla material | `OreColors.png` | `crushed_raw_ore` | `crushed_raw_copper` |

Use `OreColors.png` when variants follow **ore type**, not cobblestone tier. See [pipeline.md](reference/pipeline.md#ore-palette-orecolorspng).

## Quick start

1. Read an existing close example under `textures/item/<family>/` and its `scripts/configs/*_texture_config.psd1`.
2. Decide: reuse an existing base PNG, edit one, or generate a new base (see [generation.md](reference/generation.md)).
3. Keep color identity in the chosen palette — do not invent a parallel palette unless necessary.
4. Add or update config + `scripts/generate_*_textures.ps1` wrapper.
5. Run the wrapper; **verify with pixel dump** (margins, alpha) and Read tool (images).
6. Hand-tweak the base if shading, silhouette, or size is wrong, then regenerate variants.

## Core files

- Shared recolor engine: `scripts/generate_recolored_textures.ps1`
- Per-family configs: `scripts/configs/*_texture_config.psd1`
- Tier palette: `scripts/configs/TierColors.png`
- Ore palette: `scripts/configs/OreColors.png`
- Thin wrappers: `scripts/generate_*_textures.ps1`
- Output PNGs: `src/main/resources/assets/cobblestonexxcompressed/textures/item/...`

Reference implementation (ore family + multi-stage wrapper): `crushed_raw_ore` — see [pipeline.md](reference/pipeline.md#multi-stage-wrapper-example-crushed_raw_ore).

## Validation

- Base and outputs are 16x16 PNG with **true transparent** background (not white/black fill)
- Outer edge uses dark outline `#525252` on base (recolors to family dark tone)
- Content bbox roughly **14×14 with 1px margin** on each side (adjust if item looks too small)
- File names match registry id / model `layer0`
- Variants keep readable shading (not flat fills)
- No purple-black missing texture in-game after `processResources` / `runClient`

Full checklist: [style-guide.md](reference/style-guide.md#base-validation-checklist).

## Related

- Item wiring: `add-item-workflow`
- Docs: `docs/variant-content-guide.md`, `docs/resource-layout.md`, `docs/item-addition.md`
