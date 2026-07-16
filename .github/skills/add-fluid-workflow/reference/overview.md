# Fluid Add Workflow Overview

Use this skill when adding fluid content in this workspace.

## When to use

Use this skill for fluid content, including:

- FluidType registration
- source and flowing fluid pairs
- liquid blocks
- bucket items
- fluid textures and tint setup
- fluid-related datagen
- machine or tank use of custom fluids

The project already has a baseline fluid implementation. Start from the closest existing fluid and keep the same responsibility split.

## What to check first

Before changing code, confirm:

1. Whether the fluid needs to be placeable in the world
2. Whether it needs a bucket
3. Whether it needs translucent rendering
4. Whether it needs distinct still and flowing textures
5. Whether a tint change alone is enough
6. Whether it is only for tanks or machine recipes
7. Whether more fluids are likely later
8. Whether fluid tags or recipe integration are needed now

If this is unclear, FluidType, fluid, block, bucket, and client rendering responsibilities tend to mix together.
