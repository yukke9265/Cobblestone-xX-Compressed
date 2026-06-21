# Block Add Workflow Overview

Use this skill when adding non-machine block content in this workspace.

## When to use

Use this skill for:

- simple full-cube blocks
- BlockItem-only additions
- stateful blocks with facing or lit properties
- tiered, colored, or family blocks
- datagen updates for blockstate, block model, item model, lang, loot, and tag

If the change needs BlockEntity or Menu behavior, use add-machine-workflow instead.

## What to check first

Before changing code, confirm:

1. Whether the target is a simple block, a stateful block, or a machine-like block
2. Whether a BlockEntity is needed
3. Whether a BlockItem is needed
4. Whether the block should appear in a custom creative tab
5. Whether it should also appear in a vanilla tab
6. Whether tool, loot, or tag setup is needed
7. Whether this is a single block or a tiered or colored family
8. Whether the blockstate, model, lang, loot, and tag data should be generated

If this is unclear, ModBlocks, ModItems, datagen providers, and custom block classes tend to drift apart.
