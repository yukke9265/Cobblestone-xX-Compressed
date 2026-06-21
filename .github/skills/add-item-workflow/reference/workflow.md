# Item Add Workflow Checklist

## Implementation order

1. Define the shape: normal, food, BlockItem, tiered, or recipe-bearing.
2. Decide whether the item is a one-off or part of a definition list.
3. Register the Java content in ModItems and add shared properties if needed.
4. Decide the creative tab placement.
5. Add the display name.
6. Add the item model.
7. Add textures.
8. Add the recipe if needed.
9. Run runData for generated resources.
10. Validate with compileJava, processResources, and runClient as needed.

## Pre- and post-change checklist

### Before adding code

- Identify the closest existing item
- Confirm whether Item.Properties alone is enough
- Check whether an enum is better for multiple variants
- Confirm that the texture path fits the existing layout
- Confirm that language and model data can be generated

### After adding code

- The item is registered
- The creative tab entry is correct
- The display name exists
- The item model exists
- The texture is in the correct place
- The recipe exists when needed
- runData produced the expected generated JSON
- processResources synced it to bin/main
- No purple-black texture or missing translation key appears in-game

## Implementation notes

- Match the existing naming and structure.
- Keep the implementation easy to follow.
- Do not over-abstract a small one-off item.
- Prefer generated language, model, and recipe data over handwritten JSON.
- Keep BlockItem additions aligned with the block-side workflow.

## Related documents

- docs/item-addition.md
- docs/food-item-setup.md
- docs/recipe-setup.md
- docs/variant-content-guide.md
- docs/resource-layout.md
- docs/development-checks.md
