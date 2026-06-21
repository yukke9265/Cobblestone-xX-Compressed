# Block Add Workflow Types

## Requirements by block type

### A. One simple full-cube block

Minimum requirements:

1. Register the block in ModBlocks.
2. Register the BlockItem in ModItems.
3. Add the block to the custom creative tab if needed.
4. Add it to a vanilla tab if needed.
5. Add the display name in ModEnglishLanguageProvider.
6. Add blockstate, model, and item model generation in ModBlockStateProvider.
7. Add the matching texture PNG.
8. Add loot in ModBlockLootTableProvider.
9. Add tool tags in ModBlockTagProvider.

### B. Stateful blocks

Everything from A, plus:

1. A dedicated Block class
2. A BlockStateProperties or custom property design
3. An initial registerDefaultState(...)
4. A createBlockStateDefinition(...) implementation
5. A getStateForPlacement(...) implementation if needed
6. A custom Block instance in ModBlocks instead of registerSimpleBlock
7. A blockstate or model setup that handles the state changes

### C. BlockItem-only additions

This is a subset of A.

1. The base Block must already be registered.
2. Register the BlockItem with ITEMS.registerSimpleBlockItem(...) or an existing helper.
3. Decide whether it belongs in BUILDING_BLOCKS or a custom tab.
4. Keep the block name and item model consistent.

### D. Tiered, colored, or family blocks

Everything from A or B, plus:

1. Check whether the set can be grouped in an enum or definition list.
2. Keep the registry name, English name, and target Block in one place.
3. Move shared BlockBehaviour.Properties into a helper.
4. Prefer loops over one-off manual registration when practical.
5. Drive language, model, loot, and tags from the same definition list.
6. Generate color-variant textures from a base image when possible.
7. Avoid over-abstracting small sets.

### E. Machine-like blocks

This skill only covers the entry point for those.

1. Use add-machine-workflow if a BlockEntity is needed.
2. Use add-machine-workflow if Menu, Screen, capability, or RecipeType is needed.
3. If it is only a facing or ON/OFF state, this skill is still appropriate.
