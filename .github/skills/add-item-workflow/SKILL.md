---
name: add-item-workflow
description: "Use when adding a new item, food item, BlockItem, item recipe, or tiered item set to this NeoForge 1.21.1 mod."
---

# Item Add Workflow

Use this skill when adding item content in this workspace. Start with [reference/overview.md](reference/overview.md), then use [reference/structure.md](reference/structure.md), [reference/types.md](reference/types.md), and [reference/workflow.md](reference/workflow.md) as needed.

## Quick start

1. Confirm the item shape: normal item, food, BlockItem, recipe item, or tiered set.
2. Pick the closest existing example.
3. Keep the change aligned with ModItems and the datagen providers.
4. Validate with compileJava first, then runData if generated resources change.

## Core files

- Items: src/main/java/com/yukke9265/cobblestone_xx_compressed/registry/ModItems.java
- Creative tabs: src/main/java/com/yukke9265/cobblestone_xx_compressed/CobblestonexXCompressed.java
- Datagen: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/ModDatagen.java
- Language: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/lang/ModEnglishLanguageProvider.java
- Item models: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/model/ModItemModelProvider.java
- Recipes: src/main/java/com/yukke9265/cobblestone_xx_compressed/datagen/recipe/ModRecipeProvider.java

## Validation order

Prefer this order when it applies:

1. compileJava
2. runData
3. processResources
4. runClient

Use the reference file for the full item checklist, food notes, BlockItem notes, tiered set notes, and recipe rules.
