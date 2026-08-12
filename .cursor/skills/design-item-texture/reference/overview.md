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
| Color source | Target hues (and the reference hue the base was painted in) | `TierColors.png` or `OreColors.png` |
| Base repair | Transparent bg + dark outline before recolor | wrapper script or manual edit |
| Post-process | Remap base luminance onto each target color | `generate_recolored_textures.ps1` |
| Variant post-step | Per-material accents recolor cannot do | wrapper script (e.g. copper oxide) |

Typical project pattern: paint the base in the **reference palette color** (often index 0), then generate variants from the same shape.

## When the user says "A の色で B の形に"

1. **Color** = A (tier slot in `TierColors.png`, ore slot in `OreColors.png`, or sampled reference)
2. **Design** = B's silhouette (new or existing base PNG)
3. **Base repair** = transparent background + `#525252` outline (if not already correct)
4. **Post-process** = config that points base → palette → output names
5. Regenerate; if wrong, fix the **base** (shape/shading/size), not each variant by hand

## Decision tree

1. Same shape, only recolor? → config + run recolor (no new art)
2. New shape, **tier** colors? → new/edited base + `TierColors.png` + new config/wrapper
3. New shape, **ore / vanilla material** colors (not tier)? → new/edited base + `OreColors.png` + new config/wrapper
4. Color from a one-off item not in any palette? → sample into a new palette row; prefer `OreColors.png` for ore-only families
5. Brand-new look with no close base? → create base first ([generation.md](generation.md)), repair, then recolor
6. Single-color recolor cannot express the look (e.g. copper + green oxide)? → recolor first, then **variant-specific pixel accents** in the wrapper
