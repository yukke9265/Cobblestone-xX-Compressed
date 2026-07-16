# Item Add Workflow Types

## Requirements by item type

### A. One normal item

Minimum requirements:

1. Register the item in ModItems.
2. Add it to the custom creative tab if needed.
3. Add it to a vanilla tab if needed.
4. Add the display name in ModEnglishLanguageProvider.
5. Add a model in ModItemModelProvider or an existing helper.
6. Add the matching PNG texture.
7. Add a recipe if needed.

### B. Food items

Everything from A, plus:

1. Design Item.Properties().food(...)
2. Decide nutrition
3. Decide saturationModifier
4. Decide whether alwaysEdible, fast, effect, or usingConvertsTo is needed
5. Move shared settings into a helper when practical

### C. BlockItems

This starts from the block side.

1. The source Block must already be registered.
2. Register the BlockItem with ITEMS.registerSimpleBlockItem(...) or an existing helper.
3. Decide whether it belongs in BUILDING_BLOCKS or a custom tab.
4. Keep the item model aligned with the block model.
5. Keep the block-side display name in sync.

### D. Tiered, colored, or family items

Everything from A or B, plus:

1. Check whether the set can live in an enum or definition list.
2. Keep the registry name, English name, and target Item in one place.
3. Move shared Properties into a helper.
4. Prefer loops over one-off manual registration when practical.
5. Drive language, model, and recipe data from the same definition list.
6. Generate color-variant textures from a base image when possible.
7. Do not over-abstract small sets.

### E. Recipe-bearing items

Everything from A or B, plus:

1. The output item must already be registered.
2. Add a builder or definition entry in ModRecipeProvider.
3. Decide whether the recipe is shaped or shapeless.
4. Define unlock conditions.
5. Add reverse recipes when compression-style workflows need them.
6. Run runData to refresh generated recipes.
