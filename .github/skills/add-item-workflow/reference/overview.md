# Item Add Workflow Overview

Use this skill when adding item content in this workspace.

## When to use

Use this skill for:

- normal items
- food items
- BlockItems
- items with crafting recipes
- tiered, colored, or family item sets
- datagen updates for item model, lang, and recipe files

The project already uses both one-off items and grouped tiered items, so always check whether the existing ModItems pattern can be reused first.

## What to check first

Before changing code, confirm:

1. Whether the target is a normal item, food item, or BlockItem
2. Whether it is a single item or part of a tiered or colored family
3. Whether a custom Item class is needed
4. Whether it should appear in a creative tab
5. Whether it should also appear in a vanilla tab
6. Whether it needs a crafting recipe
7. Whether item model, display name, and recipe data should be generated or handwritten

If this is unclear, ModItems, language, model, and recipe locations tend to drift apart.
