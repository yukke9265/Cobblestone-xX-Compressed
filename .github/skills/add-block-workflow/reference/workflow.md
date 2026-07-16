# Block Add Workflow Checklist

## Implementation order

1. Define the shape: simple, stateful, tiered, or machine-like.
2. Decide whether the block is a one-off or part of a definition list.
3. Register the Java content in ModBlocks and add helper properties if needed.
4. Register the BlockItem in ModItems.
5. Decide the creative tab placement in CobblestonexXCompressed.
6. Add the display name.
7. Add blockstate and model generation.
8. Add textures.
9. Add loot and tags.
10. Run runData for generated resources.
11. Validate with compileJava, processResources, and runClient as needed.

## Pre- and post-change checklist

### Before adding code

- Identify the closest existing block
- Confirm whether a simple block is enough
- Check whether a custom Block class is required
- Decide whether an enum is better for multiple variants
- Confirm whether this should be handled by datagen

### After adding code

- The block is registered
- The BlockItem is registered
- The creative tab entry is correct
- The display name exists
- The blockstate and model generation exists
- The texture is in the correct place
- The loot entry exists
- The tool tag exists
- runData produced the expected generated JSON
- processResources synced it to bin/main
- No purple-black or transparent block errors appear in-game
- The block drops correctly

## Implementation notes

- Match the existing naming and structure.
- Keep the implementation easy to follow.
- Do not over-abstract a small one-off block.
- Prefer generated language, model, loot, and tag data over handwritten JSON.
- Do not use registerSimpleBlock for blocks that need custom state logic.
- Switch to add-machine-workflow as soon as BlockEntity behavior appears.

## Related documents

- docs/block-addition.md
- docs/block-property-setup.md
- docs/block-state-guide.md
- docs/resource-layout.md
- docs/development-checks.md
- docs/client-server-basics.md
