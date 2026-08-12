---
name: design-item-texture
description: "Use when designing, recoloring, or generating Minecraft item PNG textures for this NeoForge 1.21.1 mod—especially transferring a source item's coloring onto a different base design, including post-processing via scripts/generate_recolored_textures.ps1."
---

# Item Texture Design Workflow

Use this skill for **item textures only** (not block/GUI). Start with [reference/overview.md](reference/overview.md). Use [reference/pipeline.md](reference/pipeline.md), [reference/style-guide.md](reference/style-guide.md), and [reference/generation.md](reference/generation.md) as needed.

Item registration, models, lang, and recipes stay in `add-item-workflow`. This skill covers appearance only.

## Main use case

**Color from one item (or TierColors) → different silhouette/design.**

1. Pick the **color source** (usually `scripts/configs/TierColors.png`, or colors sampled from a reference item).
2. Pick or create the **base design** (shape/shading only; tinted by the reference palette color).
3. Run **post-processing** with `scripts/generate_recolored_textures.ps1` via a config + thin wrapper.
4. Confirm output path, file name, and that the item model `layer0` matches.

## Quick start

1. Read an existing close example under `textures/item/<family>/` and its `scripts/configs/*_texture_config.psd1`.
2. Decide: reuse an existing base PNG, edit one, or generate a new base (see generation.md).
3. Keep color identity in `TierColors.png` / palette indices—do not invent a parallel palette unless necessary.
4. Add or update config + `scripts/generate_*_textures.ps1` wrapper.
5. Run the wrapper; inspect outputs with the Read tool (images).
6. Hand-tweak the base if shading or silhouette is wrong, then regenerate variants.

## Core files

- Shared recolor engine: `scripts/generate_recolored_textures.ps1`
- Per-family configs: `scripts/configs/*_texture_config.psd1`
- Shared palette: `scripts/configs/TierColors.png`
- Thin wrappers: `scripts/generate_*_textures.ps1`
- Output PNGs: `src/main/resources/assets/cobblestonexxcompressed/textures/item/...`

## Validation

- Base and outputs are 16x16 PNG with sensible alpha
- File names match registry / model `layer0`
- Variants keep readable shading (not flat fills)
- No purple-black missing texture in-game after `processResources` / `runClient`

## Related

- Item wiring: `add-item-workflow`
- Docs: `docs/variant-content-guide.md`, `docs/resource-layout.md`, `docs/item-addition.md`
