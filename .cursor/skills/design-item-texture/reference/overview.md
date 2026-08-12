# Overview

## What this skill owns

- Item PNG design decisions
- Color transfer from a palette or reference item onto another design
- Post-processing with the existing recolor scripts
- Placement under `textures/item/`

## What this skill does not own

- Item registration, creative tabs, lang, models, recipes → `add-item-workflow`
- Block or GUI textures

## Mental model

Separate **shape** from **color**.

| Role | Meaning | Typical file |
|------|---------|--------------|
| Base design | Silhouette + shading in the reference tint | `.../textures/item/<family>/<base>.png` |
| Color source | Target hues (and the reference hue the base was painted in) | `scripts/configs/TierColors.png` |
| Post-process | Remap base luminance onto each target color | `generate_recolored_textures.ps1` |

Typical project pattern: paint the base in the **reference palette color** (often index 0 = cobblestone gray), then generate copper/iron/gold/... variants from the same shape.

## When the user says "A の色で B の形に"

1. **Color** = A (or A's tier slot in `TierColors.png`)
2. **Design** = B's silhouette (new or existing base PNG)
3. **Post-process** = config that points base → palette → output names
4. Regenerate; if wrong, fix the **base** (shape/shading), not each variant by hand

## Decision tree

1. Same shape, only recolor? → config + run recolor (no new art)
2. New shape, existing tier colors? → new/edited base + same `TierColors.png` + new config/wrapper
3. Color from a one-off item not in TierColors? → sample that item's main opaque color into a palette row, or extend `TierColors.png` carefully
4. Brand-new look with no close base? → create base first ([generation.md](generation.md)), then recolor
